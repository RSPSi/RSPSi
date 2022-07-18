package com.rspsi.plugin.loader;

import com.displee.cache.index.archive.Archive;

import java.util.List;

import com.google.common.collect.Lists;
import com.jagex.Client;
import com.jagex.cache.config.VariableBits;
import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.loader.config.VariableBitLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.io.Buffer;
import com.rspsi.core.misc.FixedHashMap;

public class ObjectDefinitionLoaderOSRS extends ObjectDefinitionLoader {

	private int count, count728;
	private FixedHashMap<Integer, ObjectDefinition> cache = new FixedHashMap<Integer, ObjectDefinition>(1000);
	
	private Buffer data, data728;
	private int[] indices, indices728;
	
	@Override
	public void init(Archive archive) {
		data = new Buffer(archive.file("loc.dat"));
		Buffer buffer = new Buffer(archive.file("loc.idx"));
		count = buffer.readUShort();
		System.out.println("Expected " + count + " ids");
		indices = new int[count];
		int offset = 2;
		for (int index = 0; index < count; index++) {
			indices[index] = offset;
			offset += buffer.readUShort();
		}

		data728 = new Buffer(archive.file("loc.dat"));
		Buffer buffer728 = new Buffer(archive.file("loc.idx"));
		count728 = buffer728.readUShort();
		System.out.println("Expected " + count + " ids");
		indices728 = new int[count728];
		offset = 2;
		for (int index = 0; index < count728; index++) {
			indices728[index] = offset;
			offset += buffer728.readUShort();
		}

	}
	
	@Override
	public void init(Buffer data, Buffer indexBuffer) {
		count = indexBuffer.readUShort();
		indices = new int[count];
		int offset = 2;
		for (int index = 0; index < count; index++) {
			indices[index] = offset;
			offset += indexBuffer.readUShort();
		}

		
	}

	public ObjectDefinition decode(int id, Buffer buffer) {
		ObjectDefinition definition = new ObjectDefinition();
		definition.reset();
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
				definition.setName(buffer.readString());
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
			} else if(opcode == 27) {
				//setInteractType(1);
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
				definition.setSurroundings(buffer.readUByte());//Not used in OSRS?
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
				
				int var3 = -1;
				if(opcode == 92) {
					var3 = buffer.readUShort();
					if(var3 == 65535)
						var3 = -1;
				}

				int count = buffer.readUByte();
				int[] morphisms = new int[count + 2];
				for (int i = 0; i <= count; i++) {
					morphisms[i] = buffer.readUShort();
					if (morphisms[i] == 65535) {
						morphisms[i] = -1;
					}
				}
				morphisms[count + 1] = var3;
				
				definition.setMorphisms(morphisms);
				definition.setVarbit(varbit);
				definition.setVarp(varp);
			} else if(opcode == 78) {//TODO Figure out what these do in OSRS
				//First short = ambient sound
				buffer.skip(3);
			} else if(opcode == 79) {
				buffer.skip(5);
				int count = buffer.readByte();
				buffer.skip(2 * count);
			} else if(opcode == 81) {
				buffer.skip(1);//Clip type?
			} else if (opcode == 82) {
				buffer.readUShort();//AreaType
				
			} else if(opcode == 249) {
				int var1 = buffer.readUByte();
				for(int var2 = 0;var2<var1;var2++) {
					boolean b = buffer.readUByte() == 1;
					int var5 = buffer.readUTriByte();
					if(b) {
						buffer.readString();
					} else {
						buffer.readInt();
					}
				}
			} else {
				System.out.println("Unrecognised object opcode " + opcode + " last;" + lastOpcode + "ID: " + id);
				continue;
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
	
	public ObjectDefinition decode728(int id, Buffer buffer) {
		ObjectDefinition definition = new ObjectDefinition();
		definition.reset();
		definition.setName("[728]");
		int interactive = -1;
		int lastOpcode = -1;
		do {
			int opcode;
			opcode = buffer.readUByte();
			if (opcode == 0) {
				break;
			}

			if (opcode == 1 || opcode == 5) {
				int count = buffer.readUByte();
				if (count > 0) {
					if (definition.getModelIds() == null) {
						List<Integer> eocModels = Lists.newArrayList();
						List<Integer> eocModelTypes = Lists.newArrayList();
						
						for (int i = 0; i < count; i++) {
							int length = buffer.readUByte();
							int type = buffer.readUByte();
							for(int index2 = 0;index2<length;index2++) {
								eocModels.add(buffer.readBigSmart());
								eocModelTypes.add(type);
							}
							
						}
						for(int i = 0;i<eocModelTypes.size();i++) {
							if(eocModelTypes.get(i) > 22)
								eocModelTypes.set(i, 22);
						}
						definition.setModelIds(eocModels.stream().mapToInt(Integer::intValue).toArray());
						definition.setModelTypes(eocModelTypes.stream().mapToInt(Integer::intValue).toArray());
					} else {
						buffer.setPosition(buffer.getPosition() + count * 3);
					}
				}
			} else if (opcode == 2) {
				definition.setName(definition.getName() + buffer.readStringAlternative());
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
				int animation = buffer.readBigSmart();
				if (animation == 65535) {
					animation = -1;
				}
				definition.setAnimation(animation);
			} else if (opcode == 28) {
				definition.setDecorDisplacement(buffer.readUByte());
			} else if (opcode == 29) {
				definition.setAmbientLighting(buffer.readByte());
			} else if (opcode == 39) {
				definition.setLightDiffusion((byte) (buffer.readByte() * 5));
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
				int i_69_ = (buffer.readUByte());
				buffer.skip(i_69_);
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
				
				int var = -1;
				if(opcode == 92) {
					var = buffer.readBigSmart();
				}

				int count = buffer.readUByte();
				int[] morphisms = new int[count + 2];
				for (int i = 0; i <= count; i++) {
					morphisms[i] = buffer.readBigSmart();
					if (morphisms[i] == 65535) {
						morphisms[i] = -1;
					}
				}
				morphisms[count + 1] = var;
				definition.setMorphisms(morphisms);
				definition.setVarbit(varbit);
				definition.setVarp(varp);
			} else if(opcode == 78 || opcode == 99 || opcode == 100) {//TODO Figure out what these do in OSRS
				buffer.skip(3);
			} else if(opcode == 79) {
				buffer.skip(5);
				int count = buffer.readByte();
				buffer.skip(2 * count);
			} else if(opcode == 81 || opcode == 101|| opcode == 104 || opcode == 178) {
				buffer.skip(1);
			} else if(opcode == 93 || opcode == 95 || opcode == 102 || opcode == 107 || (opcode >= 164 && opcode <= 167)) {
				buffer.skip(2);
			} else if(opcode == 106) {
				int var = buffer.readUByte();
				for(int i = 0;i<var;i++) {
					buffer.readBigSmart();
					buffer.skip(1);
				}
			} else if(opcode >= 150 && opcode < 155) {
				buffer.readStringAlternative();
			} else if(opcode == 160) {
				int count = buffer.readByte();
				buffer.skip(2 * count);
			} else if(opcode == 162 || opcode == 172) {
				buffer.skip(4);
			} else if(opcode == 170 || opcode == 171) {
				buffer.readUSmart();
			} else if (opcode == 249) {
				int count = buffer.readUByte();
				for(int i = 0;i<count;i++) {
					byte str = (byte) buffer.readUByte();
					//buffer.skip(4);
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
	
	private static final int[] OBJECTS_728 = { 226, 2639, 16455, 32157, 32159, 53084 };

	private static boolean is728Object(int objectId) {
		for (int aOBJECTS_728 : OBJECTS_728) {
			if (objectId == aOBJECTS_728) {
				return true;
			}
		}
		return objectId > 57265 && objectId < 65000;
	}

	@Override
	public ObjectDefinition forId(int id) {
		if(cache.contains(id))
			return cache.get(id);
		try {
		ObjectDefinition definition = null;

		if(is728Object(id)) {
			data728.setPosition(indices728[id]);
			definition = decode728(id, data728);
		} else {
			if(id >= indices.length) {
				if(id >= indices728.length) {
					System.out.println("OBJ ID " + id + " TOO HIGH");
					return forId(0);
				} else {
					data728.setPosition(indices728[id]);
					definition = decode728(id, data728);
				}
			} else {
				data.setPosition(indices[id]);
				definition = decode(id, data);
			}
		}

		definition.setId(id);
		

		return definition;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return forId(1);
	}

	@Override
	public int count() {
		return count;
	}

	@Override
	public ObjectDefinition morphism(int id) {
		ObjectDefinition def = forId(id);
		int morphismIndex = -1;
		if (def.getVarbit() != -1) {
			VariableBits bits = VariableBitLoader.lookup(def.getVarbit());
			int variable = bits.getSetting();
			int low = bits.getLow();
			int high = bits.getHigh();
			int mask = Client.BIT_MASKS[high - low];
			morphismIndex = Client.getSingleton().settings[variable] >> low & mask;
		} else if (def.getVarp() != -1)
			morphismIndex = Client.getSingleton().settings[def.getVarp()];
		
		int var2;
		if(morphismIndex >= 0 && morphismIndex < def.getMorphisms().length) {
			var2 = def.getMorphisms()[morphismIndex];
		} else {
			var2 = def.getMorphisms()[def.getMorphisms().length - 1];
		}
		return var2 == -1 ? null : ObjectDefinitionLoader.lookup(var2);
	}


}
