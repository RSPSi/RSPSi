package com.rspsi.plugin.loader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import com.jagex.Client;
import com.jagex.cache.config.VariableBits;
import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.loader.config.VariableBitLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.io.Buffer;
import com.rspsi.misc.FixedHashMap;
import lombok.extern.slf4j.Slf4j;
import org.displee.cache.index.Index;
import org.displee.cache.index.archive.Archive;
import org.displee.cache.index.archive.file.File;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

@Slf4j
public class ObjectDefLoader extends ObjectDefinitionLoader {

	private Map<Integer, ObjectDefinition> definitions = Maps.newHashMap();
	
	@Override
	public void init(Archive archive) {

	}
	
	@Override
	public void init(Buffer data, Buffer indexBuffer) {

	}

	private int size;

	public void decodeObjects(Index index) {
		size = index.getLastArchive().getId() * 256 + index.getLastArchive().getLastFile().getId();
		for (int id = 0; id < size; id++) {
			File file = index.getArchive(id >>> 8).getFile(id & 0xff);
			if (Objects.nonNull(file) && Objects.nonNull(file.getData())) {
				ObjectDefinition def = null;
				def = decode(id, new Buffer(file.getData()));
				definitions.put(id, def);
				System.out.println("id: " + id);
			}
		}
	}

	public ObjectDefinition decode(int id, Buffer buffer) {
		ObjectDefinition definition = new ObjectDefinition();
		definition.reset();
		int interactive = -1;
		int lastOpcode = -1;
		int opcode = -1;
		do {
			try {
				opcode = buffer.readUByte();
				if (opcode == 0)
					break;
				if (opcode == 1 || opcode == 5) {
					if (opcode == 5) {
						int length = buffer.readUByte();
						if (length > 0) {
							for (int index = 0; index < length; index++) {
								buffer.readUShort();
							}
						}
					}
					if (opcode == 1) {
						int i_73_ = buffer.readUByte();
						int[][] modelIds = new int[i_73_][];
						int[] shapes = new int[i_73_];
						for (int type = 0; type < i_73_; type++) {
							shapes[type] = (byte) buffer.readByte();
							int i_75_ = buffer.readUByte();
							modelIds[type] = new int[i_75_];
							System.out.println("Type " + type);
							for (int model = 0; i_75_ > model; model++) {
								System.out.println("model index " + model + ", max " + i_75_);
								modelIds[type][model] = buffer.readBigSmart();
							}
						}
						int[] models = Ints.concat(modelIds);
						definition.setModelIds(models);
						definition.setModelTypes(shapes);
					}
//				if (opcode == 5) {
//					int length = buffer.readUByte();
//					for (int index = 0; index < length; index++) {
//						buffer.skip(1);
//						int length2 = buffer.readUByte();
//						for (int i = 0; i < length2; i++)
//							buffer.readBigSmart();
//					}
//				}
				} else if (opcode == 2) {
					definition.setName(buffer.readStringAlternative());
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
					definition.setAnimation(-1);
				} else if (opcode == 27) {
					//setInteractType(1);
				} else if (opcode == 28) {
					definition.setDecorDisplacement(buffer.readUByte());
				} else if (opcode == 29) {
					definition.setAmbientLighting(buffer.readByte());
				} else if (opcode == 39) {
					definition.setLightDiffusion(buffer.readByte());
				} else if (opcode >= 30 && opcode < 39) {
					String[] interactions = new String[10];

					interactions[opcode - 30] = buffer.readStringAlternative();
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
				} else if (opcode == 42) {
					int i = buffer.readUByte();
					buffer.skip(i);
				} else if (opcode == 44) {
					int i = buffer.readUShort();
				} else if (opcode == 45) {
					int i = buffer.readUShort();
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
					if (opcode == 92) {
						var3 = buffer.readBigSmart();
						if (var3 == 65535)
							var3 = -1;
					}

					int count = buffer.readUByte();
					int[] morphisms = new int[count + 2];
					for (int i = 0; i <= count; i++) {
						morphisms[i] = buffer.readBigSmart();
						if (morphisms[i] == 65535) {
							morphisms[i] = -1;
						}
					}
					morphisms[count + 1] = var3;

					definition.setMorphisms(morphisms);
					definition.setVarbit(varbit);
					definition.setVarp(varp);
				} else if (opcode == 78) {//TODO Figure out what these do in OSRS
					//First short = ambient sound
					buffer.skip(3);
				} else if (opcode == 79) {
					buffer.skip(5);
					int count = buffer.readByte();
					buffer.skip(2 * count);
				} else if (opcode == 81) {
					buffer.skip(1);//Clip type?
				} else if (opcode == 93) {
					buffer.skip(2);
				} else if (opcode == 95) {
					buffer.skip(2);
				} else if (opcode == 99) {
					buffer.skip(3);
				} else if (opcode == 100) {
					buffer.skip(3);
				} else if (opcode == 101) {
					buffer.skip(1);
				} else if (opcode == 102) {
					buffer.skip(2);
				} else if (opcode == 104) {
					buffer.skip(1);
				} else if (opcode == 106) {
					int size = buffer.readUByte();
					for (int index = 0; index < size; index++) {
						buffer.readBigSmart();
						buffer.readUByte();
					}
				} else if (opcode == 7) {
					buffer.skip(2);
				} else if (opcode >= 150 && opcode < 155) {
					buffer.readStringAlternative();
				} else if (opcode == 160) {
					int size = buffer.readUByte();
					buffer.skip(size * 2);
				} else if (opcode == 162) {
					buffer.skip(4);
				} else if (opcode == 163) {
					buffer.skip(4);
				} else if (opcode == 164) {
					buffer.skip(4);
				} else if (opcode == 165) {
					buffer.skip(2);
				} else if (opcode == 166) {
					buffer.skip(2);
				} else if (opcode == 167) {
					buffer.skip(2);
				} else if (opcode == 170) {
					buffer.readUSmart();
				} else if (opcode == 171) {
					buffer.readUSmart();
				} else if (opcode == 173) {
					buffer.skip(4);
				} else if (opcode == 178) {
					buffer.skip(1);
				} else if (opcode == 249) {
					int var1 = buffer.readUByte();
					for (int var2 = 0; var2 < var1; var2++) {
						boolean b = buffer.readUByte() == 1;
						int var5 = buffer.readUTriByte();
						if (b) {
							buffer.readStringAlternative();
						} else {
							buffer.readInt();
						}
					}
				}
				lastOpcode = opcode;
			} catch (Exception ex) {
				ObjectDefinition def =  new ObjectDefinition();
				def.setName("Nigga cock");
				return def;
			}
		} while (true);

//		if (interactive == -1) {
//			definition.setInteractive(definition.getModelIds() != null && (definition.getModelTypes() == null || definition.getModelTypes()[0] == 10) || definition.getInteractions() != null);
//		}

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
	public ObjectDefinition forId(int id) {
		if (definitions.containsKey(id))
			return definitions.get(id);
		return forId(1);
	}

	@Override
	public int count() {
		return definitions.size();
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
