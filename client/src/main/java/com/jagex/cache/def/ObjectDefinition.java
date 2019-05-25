package com.jagex.cache.def;

import com.jagex.Client;
import com.jagex.cache.loader.anim.FrameLoader;
import com.jagex.entity.model.Mesh;
import com.jagex.entity.model.MeshLoader;
import com.jagex.net.ResourceProvider;
import com.rspsi.misc.FixedIntegerKeyMap;
import com.rspsi.misc.FixedLongKeyMap;

public final class ObjectDefinition {

	// Thanks Super_

	public static FixedIntegerKeyMap<Mesh> baseModels = new FixedIntegerKeyMap<Mesh>(500);
	public static Client client;
	public static boolean lowMemory;
	public static FixedLongKeyMap<Mesh> models = new FixedLongKeyMap<Mesh>(30);
	private static Mesh[] parts = new Mesh[4];
	

	public static void dispose() {
		baseModels = null;
		models = null;
	}

	private byte ambientLighting;
	private int animation;
	private boolean castsShadow;
	private boolean contouredGround;
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

	public boolean castsShadow() {
		return castsShadow;
	}
	

	public boolean contoursGround() {
		return contouredGround;
	}

	public int getAnimation() {
		return animation;
	}

	public int getDecorDisplacement() {
		return decorDisplacement;
	}

	public byte[] getDescription() {
		return description;
	}

	public int getId() {
		return id;
	}

	public String getInteraction(int index) {
		return interactions[index];
	}

	public String[] getInteractions() {
		return interactions;
	}

	public int getLength() {
		return length;
	}

	public int getMapscene() {
		return mapscene;
	}

	public int getMinimapFunction() {
		return minimapFunction;
	}

	public int[] getModelIds() {
		if(modelIds != null && modelTypes != null && modelTypes[0] == 22 && modelIds[0] == 1105 && getMinimapFunction() != -1)
			return new int[] {111};
		return modelIds;
	}

	public int[] getModelTypes() {
		return modelTypes;
	}

	public int[] getMorphisms() {
		return morphisms;
	}

	public int getMorphVarbitIndex() {
		return varbit;
	}

	public int getMorphVariableIndex() {
		return varp;
	}

	public String getName() {
		return (name == null || name.equals("") ? getMinimapFunction() != -1 ? "minimap-function:" + minimapFunction : "null" : name + (getMinimapFunction() != -1 ? " minimap-function:" + minimapFunction : "")) ;
	}

	public int getSurroundings() {
		return surroundings;
	}

	public int getWidth() {
		return width;
	}

	public boolean isImpenetrable() {
		return impenetrable;
	}

	public boolean isInteractive() {
		return interactive;
	}

	public boolean isSolid() {
		return solid;
	}

	public final void loadModels(ResourceProvider provider) {
		if (getModelIds() != null) {
			for (int id : getModelIds()) {
				provider.requestFile(0, id);
			}
		}
	}

	private final Mesh model(int type, int frame, int orientation) {
		Mesh base = null;
		long key;
		if (modelTypes == null) {
			if (type != 10)
				return null;

			key = frame + 1L << 32 | id << 6 | orientation;
			Mesh model = (Mesh) models.get(key);
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

				base = (Mesh) baseModels.get(id);
				if (base == null) {
					base = MeshLoader.getSingleton().lookup(id & 0xffff);
					if (base == null) {
						System.out.println("failed lookup id " + id);
						return null;
					}

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

			key = frame + 1L << 32 | id << 6 | index << 3 | orientation;
			Mesh model = (Mesh) models.get(key);
			if (model != null)
				return model;

			int id = getModelIds()[index];
			boolean invert = inverted ^ orientation > 3;
			if (invert) {
				id |= 0x10000;
			}

			base = (Mesh) baseModels.get(id);
			if (base == null) {
				base = MeshLoader.getSingleton().lookup(id & 0xffff);

				if (base == null) {
					System.out.println("BASE NULL FOR ID " + id);
					return null;
				}

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
				orientation == 0 && frame == -1 && !scale && !translate);
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
		if (scale) {
			model.scale(scaleX, scaleZ, scaleY);
		}

		if (translate) {
			model.translate(translateX, translateY, translateZ);
		}

		model.light(64 + ambientLighting, 768 + lightDiffusion * 5, -50, -10, -50, !delayShading);
		if (supportItems == 1) {
			model.anInt1654 = model.getModelHeight();
		}
		models.put(key, model);
		return model;
	}

	public final Mesh modelAt(int type, int orientation, int aY, int bY, int cY, int dY, int frameId) {
		Mesh model = model(type, frameId, orientation);
		if (model == null) {
			
			//System.out.println("fail1 " + type + ":" + frameId + ":" + orientation);
			return null;
		}

		if (contouredGround || delayShading) {
			model = new Mesh(contouredGround, delayShading, model);
		}

		if (contouredGround) {
			int y = (aY + bY + cY + dY) / 4;
			for (int vertex = 0; vertex < model.vertices; vertex++) {
				int x = model.vertexX[vertex];
				int z = model.vertexZ[vertex];
				int l2 = aY + (bY - aY) * (x + 64) / 128;
				int i3 = dY + (cY - dY) * (x + 64) / 128;
				int j3 = l2 + (i3 - l2) * (z + 64) / 128;
				model.vertexY[vertex] += j3 - y;
			}

			model.computeSphericalBounds();
		}
		return model;
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
		width = 1;
		length = 1;
		solid = true;
		impenetrable = true;
		interactive = false;
		contouredGround = false;
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


	public byte getAmbientLighting() {
		return ambientLighting;
	}


	public void setAmbientLighting(byte ambientLighting) {
		this.ambientLighting = ambientLighting;
	}


	public void setAnimation(int animation) {
		this.animation = animation;
	}


	public void setCastsShadow(boolean castsShadow) {
		this.castsShadow = castsShadow;
	}


	public void setContouredGround(boolean contouredGround) {
		this.contouredGround = contouredGround;
	}


	public void setDecorDisplacement(int decorDisplacement) {
		this.decorDisplacement = decorDisplacement;
	}


	public void setDelayShading(boolean delayShading) {
		this.delayShading = delayShading;
	}


	public void setDescription(byte[] description) {
		this.description = description;
	}


	public void setHollow(boolean hollow) {
		this.hollow = hollow;
	}


	public void setId(int id) {
		this.id = id;
	}


	public void setImpenetrable(boolean impenetrable) {
		this.impenetrable = impenetrable;
	}


	public void setInteractions(String[] interactions) {
		this.interactions = interactions;
	}


	public void setInteractive(boolean interactive) {
		this.interactive = interactive;
	}


	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}


	public void setLength(int length) {
		this.length = length;
	}


	public void setLightDiffusion(byte lightDiffusion) {
		this.lightDiffusion = lightDiffusion;
	}


	public void setMapscene(int mapscene) {
		this.mapscene = mapscene;
	}


	public void setMinimapFunction(int minimapFunction) {
		this.minimapFunction = minimapFunction;
	}


	public void setModelIds(int[] modelIds) {
		this.modelIds = modelIds;
	}


	public void setModelTypes(int[] modelTypes) {
		this.modelTypes = modelTypes;
	}


	public void setMorphisms(int[] morphisms) {
		this.morphisms = morphisms;
	}


	public void setVarbit(int varbit) {
		this.varbit = varbit;
	}


	public void setVarp(int varp) {
		this.varp = varp;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setObstructsGround(boolean obstructsGround) {
		this.obstructsGround = obstructsGround;
	}


	public void setOccludes(boolean occludes) {
		this.occludes = occludes;
	}


	public void setOriginalColours(int[] originalColours) {
		this.originalColours = originalColours;
	}


	public void setReplacementColours(int[] replacementColours) {
		this.replacementColours = replacementColours;
	}


	public void setScaleX(int scaleX) {
		this.scaleX = scaleX;
	}


	public void setScaleY(int scaleY) {
		this.scaleY = scaleY;
	}


	public void setScaleZ(int scaleZ) {
		this.scaleZ = scaleZ;
	}


	public void setSolid(boolean solid) {
		this.solid = solid;
	}


	public void setSupportItems(int supportItems) {
		this.supportItems = supportItems;
	}


	public void setSurroundings(int surroundings) {
		this.surroundings = surroundings;
	}


	public void setTranslateX(int translateX) {
		this.translateX = translateX;
	}


	public void setTranslateY(int translateY) {
		this.translateY = translateY;
	}


	public void setTranslateZ(int translateZ) {
		this.translateZ = translateZ;
	}


	public void setWidth(int width) {
		this.width = width;
	}

	public boolean isCastsShadow() {
		return castsShadow;
	}


	public boolean isContouredGround() {
		return contouredGround;
	}


	public boolean isDelayShading() {
		return delayShading;
	}


	public boolean isHollow() {
		return hollow;
	}


	public boolean isInverted() {
		return inverted;
	}


	public byte getLightDiffusion() {
		return lightDiffusion;
	}


	public int getVarbit() {
		return varbit;
	}


	public int getVarp() {
		return varp;
	}


	public boolean isObstructsGround() {
		return obstructsGround;
	}


	public boolean isOccludes() {
		return occludes;
	}


	public int[] getOriginalColours() {
		return originalColours;
	}


	public int[] getReplacementColours() {
		return replacementColours;
	}


	public int getScaleX() {
		return scaleX;
	}


	public int getScaleY() {
		return scaleY;
	}


	public int getScaleZ() {
		return scaleZ;
	}


	public int getSupportItems() {
		return supportItems;
	}


	public int getTranslateX() {
		return translateX;
	}


	public int getTranslateY() {
		return translateY;
	}


	public int getTranslateZ() {
		return translateZ;
	}

	

}