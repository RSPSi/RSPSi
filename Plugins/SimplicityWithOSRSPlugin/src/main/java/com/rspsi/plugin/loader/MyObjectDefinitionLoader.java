package com.rspsi.plugin.loader;

import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.io.Buffer;
import com.rspsi.core.misc.FixedHashMap;
import com.displee.cache.index.archive.Archive;

import java.io.IOException;

public class MyObjectDefinitionLoader extends ObjectDefinitionLoader {

	
	private int count;
	private FixedHashMap<Integer, ObjectDefinition> cache = new FixedHashMap<Integer, ObjectDefinition>(20);
	

	private int[] indices;
	private Buffer data;
	
	public ObjectDefinition decode(int id, Buffer buffer) {
		ObjectDefinition definition = new ObjectDefinition();
		definition.reset();
		definition.setName("[REG]");
		int interactive = -1;
		int lastOpcode = -1;
		do {
			int opcode;
			opcode = buffer.readUByte();
			if (opcode == 0) {
				break;
			}

			if (opcode == 1) {
				int count = buffer.readUByte();
				if (count > 0) {
					if (definition.getModelIds() == null) {
						int[] modelTypes = new int[count];
						int[] modelIds = new int[count];

						for (int i = 0; i < count; i++) {
							modelIds[i] = buffer.readUShort();
							modelTypes[i] = buffer.readUByte();
						}
						definition.setModelIds(modelIds);
						definition.setModelTypes(modelTypes);
					} else {
						buffer.setPosition(buffer.getPosition() + count * 3);
					}
				}
			} else if (opcode == 2) {
				definition.setName(definition.getName() + buffer.readString());
			} else if (opcode == 3) {
				definition.setDescription(buffer.readStringBytes());
			} else if (opcode == 5) {
				int count = buffer.readUByte();
				if (count > 0) {
					if (definition.getModelIds() == null) {
						definition.setModelTypes(null);
						int[] modelIds = new int[count];

						for (int i = 0; i < count; i++) {
							modelIds[i] = buffer.readUShort();
						}
						definition.setModelIds(modelIds);
					} else {
						buffer.setPosition(buffer.getPosition() + count * 2);
					}
				}
			} else if (opcode == 14) {
				definition.setWidth(buffer.readUByte());
			} else if (opcode == 15) {
				definition.setLength(buffer.readUByte());
			} else if (opcode == 17) {
				definition.setSolid(false);
			} else if (opcode == 18) {
				definition.setImpenetrable(false);
			} else if (opcode == 19) {
				interactive = buffer.readUByte();
				if (interactive == 1) {
					definition.setInteractive(true);
				}
			} else if (opcode == 21) {
				definition.setContouredGround(true);
			} else if (opcode == 22) {
				definition.setDelayShading(true);
			} else if (opcode == 23) {
				definition.setOccludes(true);
			} else if (opcode == 24) {
				int animation = buffer.readUShort();
				if (animation == 65535) {
					animation = -1;
				}
				definition.setAnimation(animation);
			} else if (opcode == 28) {
				definition.setDecorDisplacement(buffer.readUByte());
			} else if (opcode == 29) {
				definition.setAmbientLighting(buffer.readByte());
			} else if (opcode == 39) {
				definition.setLightDiffusion(buffer.readByte());
			} else if (opcode >= 30 && opcode < 39) {
				String[] interactions = new String[10];
				
				interactions[opcode - 30] = buffer.readString();
				if (interactions[opcode - 30].equalsIgnoreCase("hidden")) {
					interactions[opcode - 30] = null;
				}
				definition.setInteractions(interactions);
			} else if (opcode == 40) {
				int count = buffer.readUByte();
				int[] originalColours = new int[count];
				int[] replacementColours = new int[count];
				for (int i = 0; i < count; i++) {
					originalColours[i] = buffer.readUShort();
					replacementColours[i] = buffer.readUShort();
				}
				definition.setOriginalColours(originalColours);
				definition.setReplacementColours(replacementColours);
			} else if (opcode == 60) {
				definition.setMinimapFunction(buffer.readUShort());
			} else if (opcode == 62) {
				definition.setInverted(true);
			} else if (opcode == 64) {
				definition.setCastsShadow(false);
			} else if (opcode == 65) {
				definition.setScaleX(buffer.readUShort());
			} else if (opcode == 66) {
				definition.setScaleY(buffer.readUShort());
			} else if (opcode == 67) {
				definition.setScaleZ(buffer.readUShort());
			} else if (opcode == 68) {
				definition.setMapscene(buffer.readUShort());
			} else if (opcode == 69) {
				definition.setSurroundings(buffer.readUByte());
			} else if (opcode == 70) {
				definition.setTranslateX(buffer.readShort());
			} else if (opcode == 71) {
				definition.setTranslateY(buffer.readShort());
			} else if (opcode == 72) {
				definition.setTranslateZ(buffer.readShort());
			} else if (opcode == 73) {
				definition.setObstructsGround(true);
			} else if (opcode == 74) {
				definition.setHollow(true);
			} else if (opcode == 75) {
				definition.setSupportItems(buffer.readUByte());
			} else if (opcode == 77) {
				int varbit = buffer.readUShort();
				if (varbit == 65535) {
					varbit = -1;
				}

				int varp = buffer.readUShort();
				if (varp == 65535) {
					varp = -1;
				}

				int count = buffer.readUByte();
				int[] morphisms = new int[count + 1];
				for (int i = 0; i <= count; i++) {
					morphisms[i] = buffer.readUShort();
					if (morphisms[i] == 65535) {
						morphisms[i] = -1;
					}
				}
				definition.setMorphisms(morphisms);
				definition.setVarbit(varbit);
				definition.setVarp(varp);
			} else if (opcode == 249) {
				int count = buffer.readUByte();
				for(int i = 0;i<count;i++) {
					byte str = (byte) buffer.readUByte();
					if(str == 1)
						buffer.readString();
					else
						buffer.readInt();
				}
			} else {
				System.out.println("Unrecognised object opcode NORM " + opcode + " last;" + lastOpcode);
				break;
			}
			lastOpcode = opcode;
		} while (true);

		if (interactive == -1) {
			definition.setInteractive(definition.getModelIds() != null && (definition.getModelTypes() == null || definition.getModelTypes()[0] == 10) || definition.getInteractions() != null);
		}

		if (definition.isHollow()) {
			definition.setSolid(false);
			definition.setImpenetrable(false);
		}
		definition.setDelayShading(false);

		if (definition.getSupportItems() == -1) {
			definition.setSupportItems(definition.isSolid() ? 1 : 0);
		}
		return definition;
	
	}
	

	public ObjectDefinition decode667(int id, Buffer buffer) {

		ObjectDefinition definition = new ObjectDefinition();
		definition.reset();
		definition.setName("[667]");
		int interactive = -1;
		int lastOpcode = -1;
		do {
			int opcode;
			opcode = buffer.readUByte();
			if (opcode == 0) {
				break;
			}

			if (opcode == 1) {
				int count = buffer.readUByte();
				if (count > 0) {
					if (definition.getModelIds() == null) {
						int[] modelTypes = new int[count];
						int[] modelIds = new int[count];

						for (int i = 0; i < count; i++) {
							modelIds[i] = buffer.readUShort();
							modelTypes[i] = buffer.readUByte();
						}
						definition.setModelIds(modelIds);
						definition.setModelTypes(modelTypes);
					} else {
						buffer.setPosition(buffer.getPosition() + count * 3);
					}
				}
			} else if (opcode == 2) {
				definition.setName(definition.getName() + buffer.readString());
			} else if (opcode == 3) {
				definition.setDescription(buffer.readStringBytes());
			} else if (opcode == 5) {
				int count = buffer.readUByte();
				if (count > 0) {
					if (definition.getModelIds() == null) {
						definition.setModelTypes(null);
						int[] modelIds = new int[count];

						for (int i = 0; i < count; i++) {
							modelIds[i] = buffer.readUShort();
						}
						definition.setModelIds(modelIds);
					} else {
						buffer.setPosition(buffer.getPosition() + count * 2);
					}
				}
			} else if (opcode == 14) {
				definition.setWidth(buffer.readUByte());
			} else if (opcode == 15) {
				definition.setLength(buffer.readUByte());
			} else if (opcode == 17) {
				definition.setSolid(false);
			} else if (opcode == 18) {
				definition.setImpenetrable(false);
			} else if (opcode == 19) {
				interactive = buffer.readUByte();
				if (interactive == 1) {
					definition.setInteractive(true);
				}
			} else if (opcode == 21) {
				definition.setContouredGround(true);
			} else if (opcode == 22) {
				definition.setDelayShading(true);
			} else if (opcode == 23) {
				definition.setOccludes(true);
			} else if (opcode == 24) {
				int animation = buffer.readUShort();
				if (animation == 65535) {
					animation = -1;
				}
				definition.setAnimation(animation);
			} else if (opcode == 28) {
				definition.setDecorDisplacement(buffer.readUByte());
			} else if (opcode == 29) {
				definition.setAmbientLighting(buffer.readByte());
			} else if (opcode == 39) {
				definition.setLightDiffusion(buffer.readByte());
			} else if (opcode >= 30 && opcode < 39) {
				String[] interactions = new String[10];
				
				interactions[opcode - 30] = buffer.readString();
				if (interactions[opcode - 30].equalsIgnoreCase("hidden")) {
					interactions[opcode - 30] = null;
				}
				definition.setInteractions(interactions);
			} else if (opcode == 40) {
				int count = buffer.readUByte();
				int[] originalColours = new int[count];
				int[] replacementColours = new int[count];
				for (int i = 0; i < count; i++) {
					originalColours[i] = buffer.readUShort();
					replacementColours[i] = buffer.readUShort();
				}
				definition.setOriginalColours(originalColours);
				definition.setReplacementColours(replacementColours);
			} else if (opcode == 41) {
				int i = buffer.readUByte();
				for (int x = 0; x < i; x++) {
					buffer.readUShort();
					buffer.readUShort();
				}//TODO OSRS Texturing
			} else if(opcode == 42) {
				int count = buffer.readUByte();
				buffer.skip(count);
			} else if (opcode == 62) {
				definition.setInverted(true);
			} else if (opcode == 64) {
				definition.setCastsShadow(false);
			} else if (opcode == 65) {
				definition.setScaleX(buffer.readUShort());
			} else if (opcode == 66) {
				definition.setScaleY(buffer.readUShort());
			} else if (opcode == 67) {
				definition.setScaleZ(buffer.readUShort());
			} else if (opcode == 68) {
				definition.setMapscene(buffer.readUShort());
			} else if (opcode == 69) {
				definition.setSurroundings(buffer.readUByte());
			} else if (opcode == 70) {
				definition.setTranslateX(buffer.readShort());
			} else if (opcode == 71) {
				definition.setTranslateY(buffer.readShort());
			} else if (opcode == 72) {
				definition.setTranslateZ(buffer.readShort());
			} else if (opcode == 73) {
				definition.setObstructsGround(true);
			} else if (opcode == 74) {
				definition.setHollow(true);
			} else if (opcode == 75) {
				definition.setSupportItems(buffer.readUByte());
			} else if (opcode == 77 || opcode == 92) {
				int varbit = buffer.readUShort();
				if (varbit == 65535) {
					varbit = -1;
				}

				int varp = buffer.readUShort();
				if (varp == 65535) {
					varp = -1;
				}
				
				if(opcode == 92) {
					buffer.skip(2);
				}

				int count = buffer.readUByte();
				int[] morphisms = new int[count + 1];
				for (int i = 0; i <= count; i++) {
					morphisms[i] = buffer.readUShort();
					if (morphisms[i] == 65535) {
						morphisms[i] = -1;
					}
				}
				definition.setMorphisms(morphisms);
				definition.setVarbit(varbit);
				definition.setVarp(varp);
			} else if(opcode == 78 || opcode == 99 || opcode == 100) {//TODO Figure out what these do in OSRS
				buffer.skip(3);
			} else if(opcode == 79) {
				buffer.skip(5);
				int count = buffer.readByte();
				buffer.skip(2 * count);
			} else if(opcode == 81 || opcode == 101 || opcode == 104 || opcode == 178) {
				buffer.skip(1);
			} else if (opcode == 93 || opcode == 95 || opcode == 102 || opcode == 107 || (opcode >= 164 && opcode <= 167)) {
				buffer.skip(2);
			} else if (opcode == 106) {
				int count = buffer.readUByte();
				buffer.skip(3 * count);
			} else if (opcode >= 150 && opcode < 155) {
				buffer.readString();
			} else if (opcode == 160) {
				int count = buffer.readUByte();
				buffer.skip(2 * count);
			} else if (opcode == 162 || opcode == 163 || opcode == 173) {
				buffer.skip(4);
			} else if (opcode == 170 || opcode == 171) {
				buffer.readUSmart();
			} else if (opcode == 249) {
				int count = buffer.readUByte();
				for(int i = 0;i<count;i++) {
					byte str = (byte) buffer.readUByte();
					if(str == 1)
						buffer.readString();
					else
						buffer.readInt();
				}
			} else {
				System.out.println("Unrecognised object opcode 667 " + opcode + " last;" + lastOpcode);
				break;
			}
			lastOpcode = opcode;
		} while (true);

		if (interactive == -1) {
			definition.setInteractive(definition.getModelIds() != null && (definition.getModelTypes() == null || definition.getModelTypes()[0] == 10) || definition.getInteractions() != null);
		}

		if (definition.isHollow()) {
			definition.setSolid(false);
			definition.setImpenetrable(false);
		}
		definition.setDelayShading(false);

		if (definition.getSupportItems() == -1) {
			definition.setSupportItems(definition.isSolid() ? 1 : 0);
		}
		return definition;
	
	}
	
	public ObjectDefinition decodeOSRS(int id, Buffer buffer) {
		ObjectDefinition definition = new ObjectDefinition();
		definition.reset();
		definition.setName("[OSRS]");
		int interactive = -1;
		int lastOpcode = -1;
		do {
			int opcode;
			opcode = buffer.readUByte();
			if (opcode == 0) {
				break;
			}

			if (opcode == 1) {
				int count = buffer.readUByte();
				if (count > 0) {
					if (definition.getModelIds() == null) {
						int[] modelTypes = new int[count];
						int[] modelIds = new int[count];

						for (int i = 0; i < count; i++) {
							modelIds[i] = buffer.readUShort();
							modelTypes[i] = buffer.readUByte();
						}
						definition.setModelIds(modelIds);
						definition.setModelTypes(modelTypes);
					} else {
						buffer.setPosition(buffer.getPosition() + count * 3);
					}
				}
			} else if (opcode == 2) {
				definition.setName(definition.getName() + buffer.readString());
			} else if (opcode == 3) {
				definition.setDescription(buffer.readStringBytes());
			} else if (opcode == 5) {
				int count = buffer.readUByte();
				if (count > 0) {
					if (definition.getModelIds() == null) {
						definition.setModelTypes(null);
						int[] modelIds = new int[count];

						for (int i = 0; i < count; i++) {
							modelIds[i] = buffer.readUShort();
						}
						definition.setModelIds(modelIds);
					} else {
						buffer.setPosition(buffer.getPosition() + count * 2);
					}
				}
			} else if (opcode == 14) {
				definition.setWidth(buffer.readUByte());
			} else if (opcode == 15) {
				definition.setLength(buffer.readUByte());
			} else if (opcode == 17) {
				definition.setSolid(false);
			} else if (opcode == 18) {
				definition.setImpenetrable(false);
			} else if (opcode == 19) {
				interactive = buffer.readUByte();
				if (interactive == 1) {
					definition.setInteractive(true);
				}
			} else if (opcode == 21) {
				definition.setContouredGround(true);
			} else if (opcode == 22) {
				definition.setDelayShading(true);
			} else if (opcode == 23) {
				definition.setOccludes(true);
			} else if (opcode == 24) {
				int animation = buffer.readUShort();
				if (animation == 65535) {
					animation = -1;
				}
				definition.setAnimation(animation);
			} else if (opcode == 28) {
				definition.setDecorDisplacement(buffer.readUByte());
			} else if (opcode == 29) {
				definition.setAmbientLighting(buffer.readByte());
			} else if (opcode == 39) {
				definition.setLightDiffusion(buffer.readByte());
			} else if (opcode >= 30 && opcode < 39) {
				String[] interactions = new String[10];
				
				interactions[opcode - 30] = buffer.readString();
				if (interactions[opcode - 30].equalsIgnoreCase("hidden")) {
					interactions[opcode - 30] = null;
				}
				definition.setInteractions(interactions);
			} else if (opcode == 40) {
				int count = buffer.readUByte();
				int[] originalColours = new int[count];
				int[] replacementColours = new int[count];
				for (int i = 0; i < count; i++) {
					originalColours[i] = buffer.readUShort();
					replacementColours[i] = buffer.readUShort();
				}
				definition.setOriginalColours(originalColours);
				definition.setReplacementColours(replacementColours);
			} else if (opcode == 41) {
				int i = buffer.readUByte();
				for (int x = 0; x < i; x++) {
					buffer.readUShort();
					buffer.readUShort();
				}//TODO OSRS Texturing
			} else if (opcode == 60) {
				definition.setMinimapFunction(buffer.readUShort());
			} else if (opcode == 62) {
				definition.setInverted(true);
			} else if (opcode == 64) {
				definition.setCastsShadow(false);
			} else if (opcode == 65) {
				definition.setScaleX(buffer.readUShort());
			} else if (opcode == 66) {
				definition.setScaleY(buffer.readUShort());
			} else if (opcode == 67) {
				definition.setScaleZ(buffer.readUShort());
			} else if (opcode == 68) {
				definition.setMapscene(buffer.readUShort());
			} else if (opcode == 69) {
				definition.setSurroundings(buffer.readUByte());
			} else if (opcode == 70) {
				definition.setTranslateX(buffer.readShort());
			} else if (opcode == 71) {
				definition.setTranslateY(buffer.readShort());
			} else if (opcode == 72) {
				definition.setTranslateZ(buffer.readShort());
			} else if (opcode == 73) {
				definition.setObstructsGround(true);
			} else if (opcode == 74) {
				definition.setHollow(true);
			} else if (opcode == 75) {
				definition.setSupportItems(buffer.readUByte());
			} else if (opcode == 77 || opcode == 92) {
				int varbit = buffer.readUShort();
				if (varbit == 65535) {
					varbit = -1;
				}

				int varp = buffer.readUShort();
				if (varp == 65535) {
					varp = -1;
				}
				
				if(opcode == 92) {
					buffer.skip(2);
				}

				int count = buffer.readUByte();
				int[] morphisms = new int[count + 1];
				for (int i = 0; i <= count; i++) {
					morphisms[i] = buffer.readUShort();
					if (morphisms[i] == 65535) {
						morphisms[i] = -1;
					}
				}
				definition.setMorphisms(morphisms);
				definition.setVarbit(varbit);
				definition.setVarp(varp);
			} else if(opcode == 78) {//TODO Figure out what these do in OSRS
				buffer.skip(3);
			} else if(opcode == 79) {
				buffer.skip(5);
				int count = buffer.readByte();
				buffer.skip(2 * count);
			} else if(opcode == 81) {
				buffer.skip(1);
			} else if (opcode == 82) {
				buffer.readUShort();
			} else if (opcode == 249) {
				int count = buffer.readUByte();
				for(int i = 0;i<count;i++) {
					byte str = (byte) buffer.readUByte();
					if(str == 1)
						buffer.readString();
					else
						buffer.readInt();
				}
			} else {
				System.out.println("Unrecognised object opcode OSRS " + opcode + " last;" + lastOpcode);
				break;
			}
			lastOpcode = opcode;
		} while (true);

		if (interactive == -1) {
			definition.setInteractive(definition.getModelIds() != null && (definition.getModelTypes() == null || definition.getModelTypes()[0] == 10) || definition.getInteractions() != null);
		}

		if (definition.isHollow()) {
			definition.setSolid(false);
			definition.setImpenetrable(false);
		}
		definition.setDelayShading(false);

		if (definition.getSupportItems() == -1) {
			definition.setSupportItems(definition.isSolid() ? 1 : 0);
		}
		return definition;
	}
	
	public void init(Archive config, Archive sound) throws IOException {

		Buffer buffer = new Buffer(config.file("loc.dat"));
		Buffer index = new Buffer(config.file("loc.idx"));
		
		buffer667 = new Buffer(config.file("loc2.dat"));
		Buffer streamIdx667 = new Buffer(config.file("loc2.idx"));


		osrsBuffer = new Buffer(config.file("loc3.dat"));
		Buffer osrsIdx = new Buffer(config.file("loc3.idx"));
		try {


			int totalObjects = index.readUShort();
			int totalObjects667 = streamIdx667.readUShort();
			int totalObjectsOSRS = osrsIdx.readUShort();
		


			indices = new int[totalObjects];
			indices667 = new int[totalObjects667];
			indicesOsrs = new int[totalObjectsOSRS];

			int i = 2;
			for (int j = 0; j < totalObjects; j++) {
				indices[j] = i;
				i += index.readUShort();
			}
			i = 2;
			for (int j = 0; j < totalObjects667; j++) {
				indices667[j] = i;
				i += streamIdx667.readUShort();
			}
			i = 2;
			for (int j = 0; j < totalObjectsOSRS; j++) {
				indicesOsrs[j] = i;
				i += osrsIdx.readUShort();
			}
			
			this.data = buffer;
			count = indices667.length;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void init(Archive config) {

	
	}

	@Override
	public void init(Buffer buffer, Buffer index) {
		

	}
	
	@Override
	public ObjectDefinition forId(int id) {
		if(cache.contains(id))
			return cache.get(id);

		ObjectDefinition definition = null;
		boolean loadNew = (
				/*id == 8550 || id == 8551 || id == 7847 || id == 8150 || */id == 32159 || id == 32157 || id == 36672 || id == 36675 || id == 36692 || id == 34138 || id >= 39260 && id <= 39271 || id == 39229 || id == 39230 || id == 39231 || id == 36676 || id == 36692 || id > 11915 && id <= 11929 || id >= 11426 && id <= 11444 || id >= 14835 && id <= 14845 || id >= 11391 && id <= 11397 || id >= 12713 && id <= 12715
				);
		if(isOSRSObject(id)) {
			osrsBuffer.setPosition(indicesOsrs[id]);
			definition = decodeOSRS(id, osrsBuffer);
		} else {
			if(loadNew || id >= indices.length) {
				if(id >= indices667.length) {
					System.out.println("OBJ ID " + id + " TOO HIGH");
					return forId(0);
				}
				buffer667.setPosition(indices667[id]);
				definition = decode667(id, buffer667);
			} else {
				data.setPosition(indices[id]);
				definition = decode(id, data);
			}
		}

		definition.setId(id);
		
		
		cache.put(id, definition);
		return definition;
	}
	
	
	  private static boolean isOSRSObject(int objectId)
	  {
	    for (int i = 0; i < OBJECTS_OSRS.length; i++) {
	      if (objectId == OBJECTS_OSRS[i]) {
	        return true;
	      }
	    }
	    return false;
	  }
	  
	  private static final int[] OBJECTS_OSRS = {
	  
	    732, 4451, 6926, 7823, 7824, 7825, 7826, 7827, 7828, 7829, 7830, 7834, 
	    11853, 14645, 14674, 14675, 17118, 20196, 21696, 21697, 21698, 21699, 21700, 21701, 21702, 21703, 21704, 
	    21705, 21706, 21707, 21708, 21709, 21710, 21711, 21712, 21713, 21714, 21715, 21716, 21717, 21718, 21748, 
	    21749, 21750, 21751, 21752, 21753, 21754, 21755, 21756, 21757, 21758, 21759, 21760, 21761, 21762, 21763, 
	    21765, 21766, 21767, 21768, 21769, 21770, 21772, 21773, 21775, 21776, 21777, 21779, 21780, 21946, 21947, 
	    22494, 22495, 23100, 23101, 26571, 26572, 1502, 12930, 12931, 12932, 20737, 23102, 23104, 23106, 23107, 
	    23108, 23109, 23112, 23610, 26294, 6775, 27059, 27059, 26765,
	    
	    11698, 11699, 11700, 
	    
	    26765 };
	 private Buffer buffer667, osrsBuffer;
	 private int[] indices667, indicesOsrs;
	 
	@Override
	public int count() {
		return count;
	}
}
