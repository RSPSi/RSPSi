package com.rspsi.plugin.loader;

import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.io.Buffer;
import com.rspsi.core.misc.FixedHashMap;
import com.displee.cache.index.archive.Archive;


public class MyObjectDefinitionLoader extends ObjectDefinitionLoader {

	
	private int count;
	private FixedHashMap<Integer, ObjectDefinition> cache = new FixedHashMap<Integer, ObjectDefinition>(20);

	private Buffer data;
	
	private int[] indices;
	
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
	
	@Override
	public void init(Archive config) {
		data = new Buffer(config.file("loc.dat"));
		Buffer buffer = new Buffer(config.file("loc.idx"));
		count = buffer.readUShort();
		System.out.println("Expected " + count + " ids");
		indices = new int[count];
		int offset = 2;
		for (int index = 0; index < count; index++) {
			indices[index] = offset;
			offset += buffer.readUShort();
		}
	
	}

	@Override
	public void init(Buffer buffer, Buffer index) {
		

	}
	
	@Override
	public ObjectDefinition forId(int id) {
		if(cache.contains(id))
			return cache.get(id);

		ObjectDefinition definition = null;

	
				data.setPosition(indices[id]);
				definition = decode(id, data);
			
		

		definition.setId(id);
		
		
		cache.put(id, definition);
		return definition;
	}
	

	 
	
	@Override
	public int count() {
		return count;
	}
}
