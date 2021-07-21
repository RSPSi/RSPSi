package com.rspsi.jagex.cache.def;

import com.rspsi.editor.cache.CacheFileType;
import com.rspsi.jagex.Client;
import com.rspsi.jagex.cache.graphics.Sprite;
import com.rspsi.jagex.cache.loader.anim.FrameLoader;
import com.rspsi.jagex.entity.Renderable;
import com.rspsi.jagex.entity.model.Mesh;
import com.rspsi.jagex.entity.model.MeshLoader;
import com.rspsi.jagex.entity.model.ModelInstance;
import com.rspsi.jagex.net.ResourceProvider;
import com.rspsi.misc.FixedIntegerKeyMap;
import com.rspsi.misc.FixedLongKeyMap;
import lombok.Getter;
import lombok.Setter;
import com.displee.cache.index.Index;

@Getter
@Setter
public final class ObjectDefinition {


	public void setContouredGround(boolean b) {
		clipType = b ? 0 : -1;
	}
	// Thanks Super_

	public static FixedIntegerKeyMap<Mesh> baseModels = new FixedIntegerKeyMap<Mesh>(500);
	public static Client client;
	public static boolean lowMemory;
	public static FixedLongKeyMap<Mesh> models = new FixedLongKeyMap<Mesh>(500);
	private static Mesh[] parts = new Mesh[4];


	public static void dispose() {
		baseModels.clear();
		models.clear();
	}
	
	private Sprite mapFunctionSprite, mapSceneSprite;
	
	public void generateSprites(Index spriteIndex) {
		
	}

	private byte ambientLighting;
	private int animation;
	private boolean castsShadow;
	private int clipType = -1;
	private int decorDisplacement;
	private boolean delayShading;
	private byte[] description;
	private boolean hollow;
	private int id = -1;
	private boolean impenetrable;
	private String[] interactions;
	private boolean interactive;
	private boolean inverted;
	private int length;
	private byte lightDiffusion;
	private int mapscene;
	private int minimapFunction;
	private int[] modelIds;
	private int[] modelTypes;
	private int[] morphisms;
	private int varbit;
	private int varp;
	private String name;
	private boolean obstructsGround;
	private boolean occludes;
	private int[] originalColours;
	private int[] replacementColours;
	private int[] retextureToFind;
	private int[] textureToReplace;
	private int scaleX;
	private int scaleY;
	private int scaleZ;
	private boolean solid;
	private int supportItems;
	private int surroundings;
	private int translateX;
	private int translateY;
	private int translateZ;
	private int width;
	
	private int areaId = -1;

	public int[] getModelIds() {
		if(modelIds != null && modelTypes != null && modelTypes[0] == 22 && modelIds[0] == 1105 && (areaId != -1 || minimapFunction != -1))
			return new int[] {111};
		return modelIds;
	}

	public String getName() {
		return (name == null || name.equals("") ? minimapFunction != -1 ? "minimap-function:" + minimapFunction : "null" : name + ( minimapFunction != -1 ? " minimap-function:" + minimapFunction : "")) ;
	}


	public final void loadModels(ResourceProvider provider) {
		if (getModelIds() != null) {
			for (int id : getModelIds()) {
				provider.requestFile(CacheFileType.MODEL, id);
			}
		}
	}

	private final Mesh model(int type, int frame, int orientation) {
		Mesh base = null;
		long key;
		if (modelTypes == null) {
			if (type != 10)
				return null;

			key = ((frame + 1L) << 32) | ((inverted ? 1L : 0L) << 16) | (((long) id) << 6) | orientation;
			Mesh model = models.get(key);
			if (model != null)
				return model;

			if (getModelIds() == null)
				return null;

			boolean invert = inverted ^ orientation > 3;
			int count = getModelIds().length;
			for (int index = 0; index < count; index++) {
				int id = getModelIds()[index];
				if (invert) {
					id |= 0x10000;
				}

				base = baseModels.get(id);
				if (base == null) {
					base = MeshLoader.getSingleton().lookup(id & 0xffff);
					if (base == null) {
						return null;
					}
					base = base.copy();

					if (invert) {
						base.invert();
						//System.out.println("INVERTING");
					}

					baseModels.put(id, base);
				}

				if (count > 1) {
					parts[index] = base;
				}
			}

			if (count > 1) {
				base = new Mesh(count, parts);
			}
		} else {
			int index = -1;
			for (int i = 0; i < modelTypes.length; i++) {
				if (modelTypes[i] != type) {
					continue;
				}

				index = i;
				break;
			}

			if (index == -1)
				return null;

			key = ((frame + 1L) << 32) | ((inverted ? 1L : 0L) << 16) | (((long) id) << 6)  | (((long) index) << 3) | orientation;

			Mesh model = models.get(key);
			if (model != null)
				return model;

			int id = getModelIds()[index];
			boolean invert = inverted ^ orientation > 3;
			if (invert) {
				id |= 0x10000;
			}

			base = baseModels.get(id);
			if (base == null) {
				base = MeshLoader.getSingleton().lookup(id & 0xffff);

				if (base == null) {
					return null;
				}

				base = base.copy();

				if (invert) {
					base.invert();
					//System.out.println("INVERTING");
				}

				baseModels.put(id, base);
			}
		}

		boolean scale = scaleX != 128 || scaleY != 128 || scaleZ != 128;
		boolean translate = translateX != 0 || translateY != 0 || translateZ != 0;

		Mesh model = new Mesh(base, originalColours == null, FrameLoader.isInvalid(frame),
				orientation == 0 && frame == -1 && !scale && !translate, retextureToFind == null);
		if (frame != -1) {
			model.prepareSkeleton();
			model.apply(frame);
			model.faceGroups = null;
			model.vertexGroups = null;
		}
		
		if(type == 4 && orientation > 3) {//OSRS
			model.pitch(256);
			model.offsetVertices(45, 0, -45);
		}
		
		orientation &= 3;

		while (orientation-- > 0) {
			model.rotateClockwise();
		}

		if (originalColours != null) {
			for (int colour = 0; colour < originalColours.length; colour++) {
				model.recolour(originalColours[colour], replacementColours[colour]);
			}

		}

		if(retextureToFind != null){
			for(int index = 0;index < retextureToFind.length;index++)
				model.retexture(retextureToFind[index], textureToReplace[index]);
		}
		if (scale) {
			model.scale(scaleX, scaleZ, scaleY);
		}

		if (translate) {
			model.translate(translateX, translateY, translateZ);
		}

		if (supportItems == 1) {
			model.anInt1654 = model.getHeight();
		}
		models.put(key, model);
		return model;
	}

	public final Renderable modelAt(int[][] tileHeights, int type, int orientation, int aY, int bY, int cY, int dY, int frameId, int mean) {
		Mesh model = model(type, frameId, orientation);
		if (model == null) {
			return null;
		}

		Renderable r;
		if (!delayShading) {
			r = model.toModelInstance(64 + ambientLighting, 768 + lightDiffusion, -50, -10, -50);
		} else {
			model.ambient = 64 + ambientLighting;
			model.contrast = lightDiffusion + 768;
			model.calculateNormals();
			r = model;
		}

		if(delayShading) {
			r = model.method3326();
		}


		if (clipType >= 0) {
			if(r instanceof Mesh) {
				r = ((Mesh)r).contourGround(tileHeights, aY, bY, cY, mean, true, clipType);
			} else if(r instanceof ModelInstance) {
				r = ((ModelInstance)r).contourGround(tileHeights, aY, bY, cY, mean, true, clipType);
			}
		}

		return r;
	}

	public boolean obstructsGround() {
		return obstructsGround;
	}

	public boolean occludes() {
		return occludes;
	}

	public final boolean ready() {
		if (getModelIds() == null)
			return true;
		boolean ready = true;
		for (int id : getModelIds()) {
			ready &= MeshLoader.getSingleton().loaded(id);
		}

		return ready;
	}

	protected int modelTries = 0;
	
	public final boolean readyOrThrow(int type) throws Exception {
		if (modelTypes == null) {
			if (getModelIds() == null || type != 10)
				return true;

			boolean ready = true;
			for (int id : getModelIds()) {
				ready &= MeshLoader.getSingleton().loaded(id);
			}
			if(ready)
				modelTries = 0;
			else
				modelTries++;
			if(modelTries > 500)
				throw new Exception("Model missing");
			return ready;
		}

		for (int index = 0; index < modelTypes.length; index++) {
			if (modelTypes[index] == type) {
				boolean ready = MeshLoader.getSingleton().loaded(getModelIds()[index]);

				if(ready)
					modelTries = 0;
				else
					modelTries++;

				if(modelTries > 500)
					throw new Exception("Model missing");
				return ready;
			}
		}
		modelTries = 0;
		return true;
	}
	
	public final boolean ready(int type) {
		if (modelTypes == null) {
			if (getModelIds() == null || type != 10)
				return true;

			boolean ready = true;
			for (int id : getModelIds()) {
				ready &= MeshLoader.getSingleton().loaded(id);
			}
			if(ready)
				modelTries = 0;
			else
				modelTries++;
			return ready || modelTries > 500;
		}

		for (int index = 0; index < modelTypes.length; index++) {
			if (modelTypes[index] == type) {
				boolean ready = MeshLoader.getSingleton().loaded(getModelIds()[index]);

				if(ready)
					modelTries = 0;
				else
					modelTries++;
				return ready || modelTries > 500;
			}
		}
		modelTries = 0;
		return true;
	}

	public final void reset() {
		modelIds = null;
		modelTypes = null;
		name = null;
		description = null;
		originalColours = null;
		replacementColours = null;
		retextureToFind = null;
		textureToReplace = null;
		width = 1;
		length = 1;
		solid = true;
		impenetrable = true;
		interactive = false;
		clipType = -1;
		delayShading = false;
		occludes = false;
		animation = -1;
		decorDisplacement = 16;
		ambientLighting = 0;
		lightDiffusion = 0;
		interactions = null;
		minimapFunction = -1;
		mapscene = -1;
		inverted = false;
		castsShadow = true;
		scaleX = 128;
		scaleY = 128;
		scaleZ = 128;
		surroundings = 0;
		translateX = 0;
		translateY = 0;
		translateZ = 0;
		obstructsGround = false;
		hollow = false;
		supportItems = -1;
		varbit = -1;
		varp = -1;
		morphisms = null;
	}



}