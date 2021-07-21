package com.rspsi.opengl;

import com.google.common.collect.Queues;
import com.jogamp.common.nio.Buffers;
import com.jogamp.graph.curve.Region;
import com.jogamp.graph.curve.opengl.RegionRenderer;
import com.jogamp.graph.curve.opengl.RenderState;
import com.jogamp.graph.curve.opengl.TextRegionUtil;
import com.jogamp.graph.font.Font;
import com.jogamp.graph.font.FontFactory;
import com.jogamp.graph.geom.SVertex;
import com.jogamp.nativewindow.NativeSurface;
import com.jogamp.nativewindow.SurfaceUpdatedListener;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.javafx.NewtCanvasJFX;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.GLBuffers;
import com.rspsi.editor.game.listeners.GameKeyListener;
import com.rspsi.editor.game.listeners.GameMouseListener;
import com.rspsi.editor.game.save.UndoRedoSystem;
import com.rspsi.jagex.Client;
import com.rspsi.jagex.cache.loader.textures.TextureLoader;
import com.rspsi.jagex.map.object.DefaultWorldObject;
import com.rspsi.jagex.map.object.GroundDecoration;
import com.rspsi.jagex.map.tile.SceneTile;
import com.rspsi.options.Options;
import com.rspsi.renderer.Camera;
import com.rspsi.util.ChangeListenerUtil;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.layout.Pane;
import jogamp.newt.driver.windows.DisplayDriver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.runelite.gpu.*;
import net.runelite.gpu.exceptions.ShaderException;
import net.runelite.gpu.util.*;
import org.apache.commons.compress.utils.Lists;
import org.joml.Matrix4f;
import org.joml.Vector3i;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.jogamp.opengl.GL.GL_BACK;
import static com.jogamp.opengl.GL.GL_DONT_CARE;
import static net.runelite.gpu.Shader.LINUX_VERSION_HEADER;
import static net.runelite.gpu.Shader.WINDOWS_VERSION_HEADER;
import static net.runelite.gpu.util.GLUtil.*;

@Slf4j
public class GLEditorWindow implements GLEventListener, SurfaceUpdatedListener {

    private static final int MAX_TEMP_VERTICES = 65535;
    public static boolean shouldUploadScene;
    private static int viewDistance = 1000;
    private final int RENDER_MODES = Region.VARWEIGHT_RENDERING_BIT;
    private final IntBuffer uniformBuffer = GpuIntBuffer.allocateDirect(5 + 3 + 2048 * 4);
    private final float[] textureOffsets = new float[128];
    private final int[] sampleCount = new int[]{4};
    public Consumer<GLEditorWindow> uploadScene;
    public Animator animator;
    public GLWindow window;
    public Camera camera = new Camera();
    int weight;
    long lastMessage = 0;
    private GameKeyListener keyboardInput;
    private GameMouseListener mouseInput;
    private Client client;
    private NewtCanvasJFX newtCanvasJFX;
    private GL3 gl;
    private SceneUploader sceneUploader;
    private int lastViewportWidth, lastViewportHeight;
    private int canvasWidth, canvasHeight;
    private TextureManager textureManager = new TextureManager();
    private int glBasicProgram;
    private int glProgram;
    private int glComputeProgram;
    private int glSmallComputeProgram;
    private int glUnorderedComputeProgram;
    private int glUIProgram;
    private int fboMainRenderer;
    private int rboDepthMain;
    private int texColorMain;
    private int[] pboIds = new int[2];
    private int pboIndex;
    private int vaoHandle;
    private int fboSceneHandle;
    private int colorTexSceneHandle;
    private int rboSceneHandle;
    private int depthTexSceneHandle;
    // scene vertex buffer id
    private int bufferId;
    // scene uv buffer id
    private int uvBufferId;
    private int tmpBufferId; // temporary scene vertex buffer
    private int tmpUvBufferId; // temporary scene uv buffer
    private int tmpModelBufferId; // scene model buffer, large
    private int tmpModelBufferSmallId; // scene model buffer, small
    private int tmpModelBufferUnorderedId;
    private int tmpOutBufferId; // target vertex buffer for compute shaders
    private int tmpOutUvBufferId; // target uv buffer for compute shaders
    private int textureArrayId;
    private int uniformBufferId;
    private ModelBuffers modelBuffers;
    private int lastCanvasWidth;
    private int lastCanvasHeight;
    private int lastStretchedCanvasWidth;
    private int lastStretchedCanvasHeight;
    private AntiAliasingMode lastAntiAliasingMode;
    // Uniforms
    private int uniUseFog;
    private int uniFogColor;
    private int uniFogDepth;
    private int uniDrawDistance;
    private int uniProjectionMatrix;
    private int uniBrightness;
    private int uniTextures;
    private int uniTextureOffsets;
    private int uniBlockSmall;
    private int uniBlockLarge;
    private int uniBlockMain;
    private int uniSmoothBanding;
    private RegionRenderer regionRenderer;
    private RenderState renderState;
    private Font font;
    private TextRegionUtil textRegionUtil;


    public void drawText(String text, int x, int y) {
        textRegionUtil.drawString3D(gl, regionRenderer, font, font.getPixelSize(12, 200), text, new float[] {1f, 1f, 1f, 1f}, new int[] {});
    }

    @SneakyThrows
    public NewtCanvasJFX init(Client client, Pane pane) {
        this.client = client;
        this.font = FontFactory.get(getClass().getResourceAsStream("/font/JetBrainsMono-Regular.ttf"), true);
        bufferId = uvBufferId = uniformBufferId = tmpBufferId = tmpUvBufferId = tmpModelBufferId = tmpModelBufferSmallId = tmpModelBufferUnorderedId = tmpOutBufferId = tmpOutUvBufferId = -1;

        modelBuffers = new ModelBuffers();
        sceneUploader = new SceneUploader(client);
        GLProfile.initSingleton();
        com.jogamp.newt.Display jfxNewtDisplay = NewtFactory.createDisplay(null, true);
        DisplayDriver.dumpDisplayList("");
        Screen screen = NewtFactory.createScreen(jfxNewtDisplay, 0);
        log.info("SCREENS: {}", Screen.getAllScreens().stream().findFirst().get());
        GLProfile glProfile = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCaps = new GLCapabilities(glProfile);


        log.info("Display: {} \n Screen: {} \n GLProfile: {} \n GLCaps {} \n", jfxNewtDisplay, screen, glProfile, glCaps);

        window = GLWindow.create(screen, glCaps);
        window.setSize(pane.widthProperty().intValue(), pane.heightProperty().intValue());
        window.setSurfaceSize(pane.widthProperty().intValue(), pane.heightProperty().intValue());

        window.addSurfaceUpdatedListener(this);
        window.addGLEventListener(this);

        log.info("Generated window: {}", window);

        //
        newtCanvasJFX = new NewtCanvasJFX(window);
        newtCanvasJFX.setWidth(pane.widthProperty().intValue());
        newtCanvasJFX.setHeight(pane.heightProperty().intValue());


        camera.setup(newtCanvasJFX);
        //camera.setup(window);

        //window.setVisible(true);


        lastViewportWidth = lastViewportHeight = lastCanvasWidth = lastCanvasHeight = -1;
        lastStretchedCanvasWidth = lastStretchedCanvasHeight = -1;
        lastAntiAliasingMode = null;

        textureArrayId = -1;

        animator = new Animator();
        animator.setUpdateFPSFrames(3, null);
        animator.setModeBits(false, AnimatorBase.MODE_EXPECT_AWT_RENDERING_THREAD);
        animator.add(window);
        animator.start();

        return newtCanvasJFX;
    }

    @Override
    public void display(GLAutoDrawable drawable) {

        try {
            gl = drawable.getGL().getGL3();
            gl.setSwapInterval(0);
            int sky = 0;
            gl.glClearColor((sky >> 16 & 0xFF) / 255f, (sky >> 8 & 0xFF) / 255f, (sky & 0xFF) / 255f, 1f);
            gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);


            //log.info("Draw {}", gl);

            if (client.loadState.get() != Client.LoadState.ACTIVE)
                return;


            if (client.canvasWidth > 0 && client.canvasHeight > 0 && (client.canvasWidth != lastViewportWidth || client.canvasHeight != lastViewportHeight)) {
                canvasWidth = client.canvasWidth;
                canvasHeight = client.canvasHeight;
                createProjectionMatrix(0, client.canvasWidth, client.canvasHeight, 0, 1, viewDistance * Perspective.LOCAL_TILE_SIZE);
                lastViewportWidth = client.canvasWidth;
                lastViewportHeight = client.canvasHeight;
                log.info("Canvas size changed");
            }
            camera.temporaryBind(client);


            if (shouldUploadScene) {
                uploadScene.accept(this);
                shouldUploadScene = false;
            }
            // base FBO to enable picking
            gl.glBindFramebuffer(gl.GL_DRAW_FRAMEBUFFER, fboMainRenderer);

            // Setup anti-aliasing
            final AntiAliasingMode antiAliasingMode = AntiAliasingMode.DISABLED;
            final boolean aaEnabled = antiAliasingMode != AntiAliasingMode.DISABLED;
            if (aaEnabled) {
                gl.glEnable(gl.GL_MULTISAMPLE);

                final int stretchedCanvasWidth = client.canvasWidth;
                final int stretchedCanvasHeight = client.canvasHeight;

                // Re-create fbo
                if (lastStretchedCanvasWidth != stretchedCanvasWidth
                        || lastStretchedCanvasHeight != stretchedCanvasHeight
                        || lastAntiAliasingMode != antiAliasingMode) {
                    final int maxSamples = glGetInteger(gl, gl.GL_MAX_SAMPLES);
                    final int samples = Math.min(antiAliasingMode.getSamples(), maxSamples);

                    initAAFbo(stretchedCanvasWidth, stretchedCanvasHeight, samples);

                    lastStretchedCanvasWidth = stretchedCanvasWidth;
                    lastStretchedCanvasHeight = stretchedCanvasHeight;
                }

                gl.glBindFramebuffer(gl.GL_DRAW_FRAMEBUFFER, fboSceneHandle);
            }
            lastAntiAliasingMode = antiAliasingMode;


            //log.info("display: {} {} {} {} {}", canvasWidth, canvasHeight, lastAntiAliasingMode, lastStretchedCanvasHeight, lastStretchedCanvasWidth);

            // Clear scene

            modelBuffers.flip();
            modelBuffers.flipVertUv();

            IntBuffer vertexBuffer = modelBuffers.getVertexBuffer().getBuffer();
            FloatBuffer uvBuffer = modelBuffers.getUvBuffer().getBuffer();
            IntBuffer modelBuffer = modelBuffers.getModelBuffer().getBuffer();
            IntBuffer modelBufferSmall = modelBuffers.getModelBufferSmall().getBuffer();
            IntBuffer modelBufferUnordered = modelBuffers.getModelBufferUnordered().getBuffer();

            gl.glBindBuffer(gl.GL_ARRAY_BUFFER, tmpBufferId);
            gl.glBufferData(gl.GL_ARRAY_BUFFER, vertexBuffer.limit() * Integer.BYTES, vertexBuffer, gl.GL_DYNAMIC_DRAW);

            gl.glBindBuffer(gl.GL_ARRAY_BUFFER, tmpUvBufferId);
            gl.glBufferData(gl.GL_ARRAY_BUFFER, uvBuffer.limit() * Float.BYTES, uvBuffer, gl.GL_DYNAMIC_DRAW);

            gl.glBindBuffer(gl.GL_ARRAY_BUFFER, tmpModelBufferId);
            gl.glBufferData(gl.GL_ARRAY_BUFFER, modelBuffer.limit() * Integer.BYTES, modelBuffer, gl.GL_DYNAMIC_DRAW);

            gl.glBindBuffer(gl.GL_ARRAY_BUFFER, tmpModelBufferSmallId);
            gl.glBufferData(gl.GL_ARRAY_BUFFER, modelBufferSmall.limit() * Integer.BYTES, modelBufferSmall, gl.GL_DYNAMIC_DRAW);

            gl.glBindBuffer(gl.GL_ARRAY_BUFFER, tmpModelBufferUnorderedId);
            gl.glBufferData(gl.GL_ARRAY_BUFFER, modelBufferUnordered.limit() * Integer.BYTES, modelBufferUnordered, gl.GL_DYNAMIC_DRAW);

            // UBO
            gl.glBindBuffer(gl.GL_UNIFORM_BUFFER, uniformBufferId);
            uniformBuffer.clear();

            client.method144(600 + client.cameraRoll * 3, client.cameraRoll, client.cameraYaw & 0x7ff);
            uniformBuffer
                    .put(client.xCameraCurve)
                    .put(client.yCameraCurve)
                    .put(camera.getCenterX())
                    .put(camera.getCenterY())
                    .put(512)
                    .put(camera.getPosition().x) //x
                    .put(camera.getPosition().z) // z
                    .put(camera.getPosition().y); // y
            uniformBuffer.flip();

            gl.glBufferSubData(gl.GL_UNIFORM_BUFFER, 0, uniformBuffer.limit() * Integer.BYTES, uniformBuffer);
            gl.glBindBuffer(gl.GL_UNIFORM_BUFFER, 0);

            // Draw 3d scene
            if (TextureLoader.instance != null && this.bufferId != -1) {
                gl.glUniformBlockBinding(glSmallComputeProgram, uniBlockSmall, 0);
                gl.glUniformBlockBinding(glComputeProgram, uniBlockLarge, 0);

                gl.glBindBufferBase(gl.GL_UNIFORM_BUFFER, 0, uniformBufferId);

                /*
                 * Compute is split into two separate programs 'small' and 'large' to
                 * save on GPU resources. Small will sort <= 512 faces, large will do <= 4096.
                 */

                // unordered

                gl.glUseProgram(glUnorderedComputeProgram);

                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 0, tmpModelBufferUnorderedId);
                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 1, this.bufferId);
                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 2, tmpBufferId);
                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 3, tmpOutBufferId);
                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 4, tmpOutUvBufferId);
                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 5, this.uvBufferId);
                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 6, tmpUvBufferId);

                gl.glDispatchCompute(modelBuffers.getUnorderedModels(), 1, 1);

                // small
                gl.glUseProgram(glSmallComputeProgram);

                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 0, tmpModelBufferSmallId);
                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 1, this.bufferId);
                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 2, tmpBufferId);
                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 3, tmpOutBufferId); // vout[]
                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 4, tmpOutUvBufferId); //uvout[]
                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 5, this.uvBufferId);
                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 6, tmpUvBufferId);

                gl.glDispatchCompute(modelBuffers.getSmallModels(), 1, 1);

                // large
                gl.glUseProgram(glComputeProgram);

                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 0, tmpModelBufferId);
                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 1, this.bufferId);
                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 2, tmpBufferId);
                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 3, tmpOutBufferId);
                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 4, tmpOutUvBufferId);
                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 5, this.uvBufferId);
                gl.glBindBufferBase(gl.GL_SHADER_STORAGE_BUFFER, 6, tmpUvBufferId);

                gl.glDispatchCompute(modelBuffers.getLargeModels(), 1, 1);

                gl.glMemoryBarrier(gl.GL_SHADER_STORAGE_BARRIER_BIT);

                if (textureArrayId == -1) {
                    textureArrayId = textureManager.initTextureArray(gl);
                }
                gl.glViewport(camera.getViewport().getX(), camera.getViewport().getY(), camera.getViewport().getWidth(), camera.getViewport().getHeight());
                gl.glUseProgram(glProgram);

                final int fogDepth = 0;
                gl.glUniform1i(uniUseFog, fogDepth > 0 ? 1 : 0);
                gl.glUniform4f(uniFogColor, (sky >> 16 & 0xFF) / 255f, (sky >> 8 & 0xFF) / 255f, (sky & 0xFF) / 255f, 1f);
                gl.glUniform1i(uniFogDepth, fogDepth);
                gl.glUniform1i(uniDrawDistance, viewDistance * Perspective.LOCAL_TILE_SIZE);

                // Brightness happens to also be stored in the texture provider, so we use that
                gl.glUniform1f(uniBrightness, 0.7f);//(float) textureProvider.getBrightness());
                gl.glUniform1f(uniSmoothBanding, 1f);


                // Bind uniforms
                gl.glUniformBlockBinding(glProgram, uniBlockMain, 0);
                gl.glUniform1i(uniTextures, 1); // texture sampler array is bound to texture1
                gl.glUniform2fv(uniTextureOffsets, 128, textureOffsets, 0);

                // We just allow the GL to do face culling. Note this requires the priority renderer
                // to have logic to disregard culled faces in the priority depth testing.
                gl.glEnable(gl.GL_CULL_FACE);
                gl.glCullFace(GL_BACK);

                // Enable blending for alpha
                gl.glEnable(gl.GL_BLEND);
                gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);

                // Draw output of compute shaders
                gl.glBindVertexArray(vaoHandle);

                gl.glEnableVertexAttribArray(0);
                gl.glBindBuffer(gl.GL_ARRAY_BUFFER, tmpOutBufferId);
                gl.glVertexAttribIPointer(0, 4, gl.GL_INT, 0, 0);

                gl.glEnableVertexAttribArray(1);
                gl.glBindBuffer(gl.GL_ARRAY_BUFFER, tmpOutUvBufferId);
                gl.glVertexAttribPointer(1, 4, gl.GL_FLOAT, false, 0, 0);

                gl.glDrawArrays(gl.GL_TRIANGLES, 0, modelBuffers.getTargetBufferOffset() + modelBuffers.getTempOffset());

                gl.glDisable(gl.GL_BLEND);
                gl.glDisable(gl.GL_CULL_FACE);

                gl.glUseProgram(0);
            }

            if (aaEnabled) {
                gl.glBindFramebuffer(gl.GL_READ_FRAMEBUFFER, fboSceneHandle);
                gl.glBindFramebuffer(gl.GL_DRAW_FRAMEBUFFER, fboMainRenderer);
                gl.glBlitFramebuffer(0, 0, lastStretchedCanvasWidth, lastStretchedCanvasHeight,
                        0, 0, lastStretchedCanvasWidth, lastStretchedCanvasHeight,
                        gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT, gl.GL_NEAREST);

                gl.glReadBuffer(gl.GL_COLOR_ATTACHMENT1);
                gl.glDrawBuffer(gl.GL_COLOR_ATTACHMENT1);
                gl.glBlitFramebuffer(0, 0, lastStretchedCanvasWidth, lastStretchedCanvasHeight,
                        0, 0, lastStretchedCanvasWidth, lastStretchedCanvasHeight,
                        gl.GL_COLOR_BUFFER_BIT, gl.GL_NEAREST);

                // Reset
                gl.glReadBuffer(gl.GL_COLOR_ATTACHMENT0);
                gl.glDrawBuffer(gl.GL_COLOR_ATTACHMENT0);
            }

            gl.glBindFramebuffer(gl.GL_READ_FRAMEBUFFER, fboMainRenderer);
            gl.glBindFramebuffer(gl.GL_DRAW_FRAMEBUFFER, 0);
            gl.glBlitFramebuffer(0, 0, canvasWidth, canvasHeight,
                    0, 0, canvasWidth, canvasHeight,
                    gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT, gl.GL_NEAREST);

            gl.glBindFramebuffer(gl.GL_READ_FRAMEBUFFER, 0);

            if (System.currentTimeMillis() - lastMessage >= TimeUnit.SECONDS.toMillis(30)) {
                log.info(" FPS: {} | {} | {} ", animator, camera, camera.getPosition());

                lastMessage = System.currentTimeMillis();
            }

            modelBuffers.clearVertUv();
            modelBuffers.clear();

			postRender.forEach(evt -> evt.accept(gl));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

//        minimapController.drawCanvas(scene);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        try {
            gl = drawable.getGL().getGL3();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        log.info("Reshape {} {} {} {}", x, y, width, height);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        try {
            gl = drawable.getGL().getGL3();

            gl.getContext().enableGLDebugMessage(true);
            gl.getContext().addGLDebugListener(new GLDebugListener() {
                @Override
                public void messageSent(GLDebugMessage event) {
                    log.info("{}", event);
                }
            });
            gl.glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, 0, null, 0, true);
            gl.glEnable(gl.GL_DEPTH_TEST);
            gl.glDepthFunc(gl.GL_LEQUAL);
            gl.glDepthRangef(0.000001f, 1);
            IntBuffer intBuf1 = GpuIntBuffer.allocateDirect(1);
            gl.glGetIntegerv(gl.GL_DEPTH_BITS, intBuf1);
            System.out.printf("depth bits %s \n", intBuf1.get(0));

            gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            renderState = RenderState.createRenderState(SVertex.factory());
            renderState.setColorStatic(0.3f, 0.4f, 0.3f, 1f);
            renderState.setHintMask(RenderState.BITHINT_GLOBAL_DEPTH_TEST_ENABLED);

            regionRenderer = RegionRenderer.create(renderState, RegionRenderer.defaultBlendEnable, RegionRenderer.defaultBlendDisable);
            textRegionUtil = new TextRegionUtil(RENDER_MODES);
            regionRenderer.init(gl, RENDER_MODES);
            regionRenderer.enable(gl, false);
            //this.programID = this.newProgram(gl);
            initVao();
            initProgram();
            initUniformBuffer();
            initBuffers();

            //setupBuffers();

            uploadScene = (glEditorWindow) -> {
                modelBuffers.clearVertUv();

                log.info("Scene uploader!");
                sceneUploader.upload(client.sceneGraph, modelBuffers.getVertexBuffer(), modelBuffers.getUvBuffer());

                modelBuffers.flipVertUv();

                IntBuffer vertexBuffer = modelBuffers.getVertexBuffer().getBuffer();
                FloatBuffer uvBuffer = modelBuffers.getUvBuffer().getBuffer();

                gl.glBindBuffer(gl.GL_ARRAY_BUFFER, bufferId);
                gl.glBufferData(gl.GL_ARRAY_BUFFER, vertexBuffer.limit() * GLBuffers.SIZEOF_INT, vertexBuffer, gl.GL_STATIC_COPY);

                gl.glBindBuffer(gl.GL_ARRAY_BUFFER, uvBufferId);
                gl.glBufferData(gl.GL_ARRAY_BUFFER, uvBuffer.limit() * GLBuffers.SIZEOF_FLOAT, uvBuffer, gl.GL_STATIC_COPY);

                gl.glBindBuffer(gl.GL_ARRAY_BUFFER, 0);

                modelBuffers.clearVertUv();


                drawTiles();


            };


            // disable vsync
//            gl.setSwapInterval(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        shutdownBuffers();
        shutdownProgram();
        shutdownVao();
        shutdownAAFbo();
    }

    private void shutdownProgram() {
        gl.glDeleteProgram(glProgram);
        glProgram = -1;
        gl.glDeleteProgram(glComputeProgram);
        glComputeProgram = -1;
        gl.glDeleteProgram(glSmallComputeProgram);
        glSmallComputeProgram = -1;
        gl.glDeleteProgram(glUnorderedComputeProgram);
        glUnorderedComputeProgram = -1;

        gl.glDeleteProgram(glUIProgram);
        glUIProgram = -1;

    }

    private void shutdownVao() {
        glDeleteVertexArrays(gl, vaoHandle);
        vaoHandle = -1;
    }

    private void shutdownAAFbo() {
        if (colorTexSceneHandle != -1) {
            glDeleteTexture(gl, colorTexSceneHandle);
            colorTexSceneHandle = -1;
        }

        if (depthTexSceneHandle != -1) {
            glDeleteTexture(gl, depthTexSceneHandle);
            depthTexSceneHandle = -1;
        }

        if (fboSceneHandle != -1) {
            glDeleteFrameBuffer(gl, fboSceneHandle);
            fboSceneHandle = -1;
        }

        if (rboSceneHandle != -1) {
            glDeleteRenderbuffers(gl, rboSceneHandle);
            rboSceneHandle = -1;
        }
    }

    private void initVao() {
        // Create VAO
        vaoHandle = glGenVertexArrays(gl);
        // unbind VBO
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, 0);
    }

    private void initProgram() throws ShaderException {
        String versionHeader = OSType.getOSType() == OSType.Linux ? LINUX_VERSION_HEADER : WINDOWS_VERSION_HEADER;
        Template template = new Template();
        template.addInclude(GLEditorWindow.class);


        //glBasicProgram = Shader.BASIC_PROGRAM.compile(gl, template);
        glProgram = Shader.PROGRAM.compile(gl, template);
        glComputeProgram = Shader.COMPUTE_PROGRAM.compile(gl, template);
        glSmallComputeProgram = Shader.SMALL_COMPUTE_PROGRAM.compile(gl, template);
        glUnorderedComputeProgram = Shader.UNORDERED_COMPUTE_PROGRAM.compile(gl, template);
        glUIProgram = Shader.UI_PROGRAM.compile(gl, template);

        //glBasicProgram.link(gl, System.out);
        //glProgram.link(gl, System.out);
        //glComputeProgram.link(gl, System.out);
        //glSmallComputeProgram.link(gl, System.out);
        //glUnorderedComputeProgram.link(gl, System.out);

        initUniforms();
    }

    private void initUniforms() {

        uniProjectionMatrix = gl.glGetUniformLocation(glProgram, "projectionMatrix");
        uniBrightness = gl.glGetUniformLocation(glProgram, "brightness");
        uniSmoothBanding = gl.glGetUniformLocation(glProgram, "smoothBanding");
        uniUseFog = gl.glGetUniformLocation(glProgram, "useFog");
        uniFogColor = gl.glGetUniformLocation(glProgram, "fogColor");
        uniFogDepth = gl.glGetUniformLocation(glProgram, "fogDepth");
        uniDrawDistance = gl.glGetUniformLocation(glProgram, "drawDistance");

        uniTextures = gl.glGetUniformLocation(glProgram, "textures");
        uniTextureOffsets = gl.glGetUniformLocation(glProgram, "textureOffsets");

        uniBlockSmall = gl.glGetUniformBlockIndex(glSmallComputeProgram, "uniforms");
        uniBlockLarge = gl.glGetUniformBlockIndex(glComputeProgram, "uniforms");
        uniBlockMain = gl.glGetUniformBlockIndex(glProgram, "uniforms");
    }

    private void initUniformBuffer() {
        uniformBufferId = glGenBuffers(gl);
        gl.glBindBuffer(gl.GL_UNIFORM_BUFFER, uniformBufferId);
        uniformBuffer.clear();
        uniformBuffer.put(new int[8]);
        final int[] pad = new int[2];
        for (int i = 0; i < 2048; i++) {
            uniformBuffer.put(Perspective.SINE[i]);
            uniformBuffer.put(Perspective.COSINE[i]);
            uniformBuffer.put(pad);
        }
        uniformBuffer.flip();

        gl.glBufferData(gl.GL_UNIFORM_BUFFER, uniformBuffer.limit() * Integer.BYTES, uniformBuffer, gl.GL_DYNAMIC_DRAW);
        gl.glBindBuffer(gl.GL_UNIFORM_BUFFER, 0);
    }

    private void initBuffers() {
        bufferId = GLUtil.glGenBuffers(gl);
        uvBufferId = GLUtil.glGenBuffers(gl);
        tmpBufferId = GLUtil.glGenBuffers(gl);
        tmpUvBufferId = GLUtil.glGenBuffers(gl);
        tmpModelBufferId = GLUtil.glGenBuffers(gl);
        tmpModelBufferSmallId = GLUtil.glGenBuffers(gl);
        tmpModelBufferUnorderedId = GLUtil.glGenBuffers(gl);
        tmpOutBufferId = GLUtil.glGenBuffers(gl);
        tmpOutUvBufferId = GLUtil.glGenBuffers(gl);
    }

    private void createProjectionMatrix(float left, float right, float bottom, float top, float near, float far) {
        gl.glUseProgram(glProgram);

        FloatBuffer fb = Buffers.newDirectFloatBuffer(16);
        new Matrix4f()
                .setOrtho(left, right, bottom, top, near, far)
                .get(fb);
        gl.glUniformMatrix4fv(uniProjectionMatrix, 1, false, fb);

        gl.glUseProgram(0);
    }

    private void initAAFbo(int width, int height, int aaSamples) {
        // Create and bind the FBO
        fboSceneHandle = GLUtil.glGenFrameBuffer(gl);
        gl.glBindFramebuffer(gl.GL_FRAMEBUFFER, fboSceneHandle);

        // Create color render buffer
        rboSceneHandle = GLUtil.glGenRenderbuffer(gl);
        gl.glBindRenderbuffer(gl.GL_RENDERBUFFER, rboSceneHandle);
        gl.glRenderbufferStorageMultisample(gl.GL_RENDERBUFFER, aaSamples, gl.GL_RGBA, width, height);
        gl.glFramebufferRenderbuffer(gl.GL_FRAMEBUFFER, gl.GL_COLOR_ATTACHMENT0, gl.GL_RENDERBUFFER, rboSceneHandle);

        // Create color texture
        colorTexSceneHandle = GLUtil.glGenTexture(gl);
        gl.glBindTexture(gl.GL_TEXTURE_2D_MULTISAMPLE, colorTexSceneHandle);
        gl.glTexImage2DMultisample(gl.GL_TEXTURE_2D_MULTISAMPLE, aaSamples, gl.GL_RGBA, width, height, true);
        // Bind color tex
        gl.glFramebufferTexture2D(gl.GL_FRAMEBUFFER, gl.GL_COLOR_ATTACHMENT0, gl.GL_TEXTURE_2D_MULTISAMPLE, colorTexSceneHandle, 0);

        // Create depth texture
        depthTexSceneHandle = GLUtil.glGenTexture(gl);
        gl.glBindTexture(gl.GL_TEXTURE_2D_MULTISAMPLE, depthTexSceneHandle);
        gl.glTexImage2DMultisample(gl.GL_TEXTURE_2D_MULTISAMPLE, aaSamples, gl.GL_DEPTH_COMPONENT, width, height, true);
        // bind depth tex
        gl.glFramebufferTexture2D(gl.GL_FRAMEBUFFER, gl.GL_DEPTH_ATTACHMENT, gl.GL_TEXTURE_2D_MULTISAMPLE, depthTexSceneHandle, 0);

        // Reset
        gl.glBindTexture(gl.GL_TEXTURE_2D_MULTISAMPLE, 0);
        gl.glBindFramebuffer(gl.GL_FRAMEBUFFER, 0);
        gl.glBindRenderbuffer(gl.GL_RENDERBUFFER, 0);
    }

    private void shutdownBuffers() {
        if (bufferId != -1) {
            glDeleteBuffer(gl, bufferId);
            bufferId = -1;
        }

        if (uvBufferId != -1) {
            glDeleteBuffer(gl, uvBufferId);
            uvBufferId = -1;
        }

        if (tmpBufferId != -1) {
            glDeleteBuffer(gl, tmpBufferId);
            tmpBufferId = -1;
        }

        if (tmpUvBufferId != -1) {
            glDeleteBuffer(gl, tmpUvBufferId);
            tmpUvBufferId = -1;
        }

        if (tmpModelBufferId != -1) {
            glDeleteBuffer(gl, tmpModelBufferId);
            tmpModelBufferId = -1;
        }

        if (tmpModelBufferSmallId != -1) {
            glDeleteBuffer(gl, tmpModelBufferSmallId);
            tmpModelBufferSmallId = -1;
        }

        if (tmpModelBufferUnorderedId != -1) {
            glDeleteBuffer(gl, tmpModelBufferUnorderedId);
            tmpModelBufferUnorderedId = -1;
        }

        if (tmpOutBufferId != -1) {
            glDeleteBuffer(gl, tmpOutBufferId);
            tmpOutBufferId = -1;
        }

        if (tmpOutUvBufferId != -1) {
            glDeleteBuffer(gl, tmpOutUvBufferId);
            tmpOutUvBufferId = -1;
        }
    }

    public final <T extends Event> void addEventHandler(final EventType<T> eventType,
                                                        final EventHandler<? super T> eventHandler) {
        ChangeListenerUtil.addListener(() -> System.out.println("focused"), newtCanvasJFX.focusedProperty());
        newtCanvasJFX.addEventHandler(eventType, eventHandler);
    }

    void drawTiles() {
        modelBuffers.clear();

        modelBuffers.setTargetBufferOffset(0);
        for (int z = 0; z < (Options.allHeightsVisible.get() ? 4 : Options.currentHeight.get() + 1); z++) {
            for (int x = 0; x < client.sceneGraph.width; x++) {
                for (int y = 0; y < client.sceneGraph.length; y++) {
                    SceneTile tile = client.sceneGraph.tiles.get(new Vector3i(x, y, z));
                    if (tile != null) {
                       tile.drawTiles(modelBuffers, camera);
                    }
                }
            }
        }

        if(!Options.showObjects.get())
            return;

        List<DefaultWorldObject> renderables = Lists.newArrayList();
        for (int z = 0; z < (Options.allHeightsVisible.get() ? 4 : Options.currentHeight.get() + 1); z++) {
            for (int x = 0; x < client.sceneGraph.width; x++) {
                for (int y = 0; y < client.sceneGraph.length; y++) {
                    SceneTile tile = client.sceneGraph.tiles.get(new Vector3i(x, y, z));
                    if (tile != null) {
                        //log.info("Tile found {}", renderables.size());
                        renderables.addAll(tile.getExistingObjects());
                        //log.info("Added objects {}", renderables.size());

                    }
                }
            }
        }


        //	log.info("Attempting to sort {} renderables", renderables.size());

        //TODO Sort these using camera and alpha
        for (DefaultWorldObject worldObject : renderables) {
            if (worldObject instanceof GroundDecoration) {
                GroundDecoration decoration = (GroundDecoration) worldObject;
                if (decoration.getMinimapFunction() != null && !Options.showMinimapFunctionModels.get())
                    continue;
            }
            worldObject.draw(modelBuffers, worldObject.getX(), worldObject.getRenderHeight(), worldObject.getY());
        }
        modelBuffers.flipVertUv();

        // allocate enough size in the outputBuffer for the static verts + the dynamic verts -- each vertex is an ivec4, 4 ints
        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, tmpOutBufferId);
        gl.glBufferData(gl.GL_ARRAY_BUFFER, (modelBuffers.getTargetBufferOffset() + MAX_TEMP_VERTICES) * GLBuffers.SIZEOF_INT * 4, null, gl.GL_STREAM_DRAW);

        gl.glBindBuffer(gl.GL_ARRAY_BUFFER, tmpOutUvBufferId);
        gl.glBufferData(gl.GL_ARRAY_BUFFER, (modelBuffers.getTargetBufferOffset() + MAX_TEMP_VERTICES) * GLBuffers.SIZEOF_FLOAT * 4, null, gl.GL_STREAM_DRAW);

    }

    public void reset() {
        if (gl == null) {
            log.info("Failed to reset, gl is null!");
            return;
        }
        shutdownBuffers();
        shutdownProgram();
        shutdownVao();
        shutdownAAFbo();
    }

    @Override
    public void surfaceUpdated(Object updater, NativeSurface ns, long when) {

    }

    public void uploadTextures() {
        textureManager.updateTextures(gl, textureArrayId);
    }

    public ConcurrentLinkedQueue<Consumer<GL3>> postRender = Queues.newConcurrentLinkedQueue();
}
