package plugin.loader;

import com.google.common.collect.Maps;
import com.jagex.Client;
import com.jagex.cache.config.VariableBits;
import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.loader.config.VariableBitLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.io.Buffer;
import com.jagex.util.ByteBufferUtils;
import lombok.extern.slf4j.Slf4j;
import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;
import lombok.val;

import java.nio.ByteBuffer;
import java.util.Arrays;
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

		val highestId = Arrays.stream(index.archiveIds()).max().getAsInt();
		val highestArchive = index.archive(highestId);
		val highestFile = Arrays.stream(highestArchive.fileIds()).max().getAsInt();
		size = highestId * 256 + highestFile;
		for (int id = 0; id < size; id++) {
			int archiveId = id >> 8;
			Archive archive = index.archive(archiveId);
			if (Objects.nonNull(archive)) {
				int fileId = (id) & (1 << 8) - 1;
				File file = archive.file(fileId);
				if (Objects.nonNull(file) && Objects.nonNull(file.getData())) {
					try {
						ObjectDefinition def = decode(id, ByteBuffer.wrap(file.getData()));
						definitions.put(id, def);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

	public ObjectDefinition decode(int id, ByteBuffer buffer) {
		ObjectDefinition definition = new ObjectDefinition();
		definition.reset();
		definition.setId(id);
		int interactive = -1;
		int lastOpcode = -1;
		try {
			for (;;) {
				int opcode = buffer.get() & 0xff;
				if (opcode == 0)
					break;
				if (opcode == 1) {
					int count = buffer.get() & 0xFF;
					if (count > 0) {
						if (definition.getModelIds() == null) {
							int[] modelTypes = new int[count];
							int[] modelIds = new int[count];

							for (int i = 0; i < count; i++) {
								modelIds[i] = buffer.getShort() & 0xFFFF;
								modelTypes[i] = buffer.get() & 0xFF;
							}
							definition.setModelIds(modelIds);
							definition.setModelTypes(modelTypes);
						} else {buffer.get(new byte[count * 3]);
						}
					}
				} else if (opcode == 5) {
					int count = buffer.get() & 0xff;
					if (count > 0) {
						if (definition.getModelIds() == null) {
							definition.setModelTypes(null);
							int[] modelIds = new int[count];

							for (int i = 0; i < count; i++) {
								modelIds[i] = buffer.getShort() & 0xFFFF;
							}
							definition.setModelIds(modelIds);
						} else {
							buffer.get(new byte[count * 2]);
						}
					}

				} else if (opcode == 2) {
					definition.setName(ByteBufferUtils.getOSRSString(buffer));
				} else if (opcode == 14) {
					definition.setWidth(buffer.get() & 0xff);
				} else if (opcode == 15) {
					definition.setLength(buffer.get() & 0xff);
				} else if (opcode == 17) {
					definition.setSolid(false);
				} else if (opcode == 18) {
					definition.setImpenetrable(false);
				} else if (opcode == 19) {
					interactive = buffer.get() & 0xff;
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
					int animation = buffer.getShort() & 0xFFFF;
					if (animation == 65535) {
						animation = -1;
					}
					definition.setAnimation(-1);
				} else if (opcode == 27) {
					//setInteractType(1);
				} else if (opcode == 28) {
					definition.setDecorDisplacement((buffer.get() & 0xff) << 2);
				} else if (opcode == 29) {
					definition.setAmbientLighting((byte) (buffer.get()));
				} else if (opcode == 39) {
					definition.setLightDiffusion((byte) (buffer.get()));
				} else if (opcode >= 30 && opcode < 39) {
					String[] interactions = new String[10];
					interactions[opcode - 30] = ByteBufferUtils.getOSRSString(buffer);
					if (interactions[opcode - 30].equalsIgnoreCase("hidden")) {
						interactions[opcode - 30] = null;
					}
					definition.setInteractions(interactions);
				} else if (opcode == 40) {
					int count = buffer.get() & 0xff;
					int[] originalColours = new int[count];
					int[] replacementColours = new int[count];
					for (int i = 0; i < count; i++) {
						originalColours[i] = buffer.getShort() & 0xffff;
						replacementColours[i] = buffer.getShort() & 0xffff;
					}
					definition.setOriginalColours(originalColours);
					definition.setReplacementColours(replacementColours);
				} else if (opcode == 41) {
					int i = buffer.get() & 0xff;
					for (int x = 0; x < i; x++) {
						int i1 = buffer.getShort() & 0xffff;
						int i2 = buffer.getShort() & 0xffff;
					}
				} else if (opcode == 42) {
					int i = buffer.get() & 0xff;
					for (int index = 0; index < i; index++)
						buffer.get();
				} else if (opcode == 44) {
					int i = buffer.getShort() & 0xffff;
				} else if (opcode == 45) {
					int i = buffer.getShort() & 0xffff;
				} else if (opcode == 60) {
					definition.setMinimapFunction(buffer.getShort() & 0xffff);
				} else if (opcode == 62) {
					definition.setInverted(true);
				} else if (opcode == 64) {
					definition.setCastsShadow(false);
				} else if (opcode == 65) {
					definition.setScaleX(buffer.getShort() & 0xffff);
				} else if (opcode == 66) {
					definition.setScaleY(buffer.getShort() & 0xffff);
				} else if (opcode == 67) {
					definition.setScaleZ(buffer.getShort() & 0xffff);
				} else if (opcode == 68) {
					definition.setMapscene(buffer.getShort() & 0xffff);
				} else if (opcode == 69) {
					definition.setSurroundings(buffer.get() & 0xff);
				} else if (opcode == 70) {
					definition.setTranslateX(buffer.getShort() & 0xffff);
				} else if (opcode == 71) {
					definition.setTranslateY(buffer.getShort() & 0xffff);
				} else if (opcode == 72) {
					definition.setTranslateZ(buffer.getShort() & 0xffff);
				} else if (opcode == 73) {
					definition.setObstructsGround(true);
				} else if (opcode == 74) {
					definition.setHollow(true);
				} else if (opcode == 75) {
					definition.setSupportItems(buffer.get() & 0xff);
				} else if (opcode == 77 || opcode == 92) {
					int varbit = buffer.getShort() & 0xffff;
					if (varbit == 65535) {
						varbit = -1;
					}
					int varp = buffer.getShort() & 0xffff;
					if (varp == 65535) {
						varp = -1;
					}
					int var3 = -1;
					if (opcode == 92) {
						var3 = ByteBufferUtils.getUShort(buffer);
						if (var3 == 65535)
							var3 = -1;
					}
					int count = buffer.get() & 0xff;
					int[] morphisms = new int[count + 2];
					for (int i = 0; i <= count; i++) {
						morphisms[i] = ByteBufferUtils.getUShort(buffer);
						if (morphisms[i] == 65535) {
							morphisms[i] = -1;
						}
					}
					morphisms[count + 1] = var3;
					definition.setMorphisms(morphisms);
					definition.setVarbit(varbit);
					definition.setVarp(varp);
				} else if (opcode == 78) {
					buffer.getShort();
					buffer.get();
				} else if (opcode == 79) {
					buffer.getShort();
					buffer.getShort();
					buffer.get();
					int count = buffer.get();
					for (int index = 0; index < count; index++)
						buffer.getShort();
				} else if (opcode == 81) {
					buffer.get();
				} else if (opcode == 93) {
					buffer.getShort();
				} else if (opcode == 95) {

				} else if (opcode == 99) {
					buffer.get();
					buffer.getShort();
				} else if (opcode == 100) {
					buffer.get();
					buffer.getShort();
				} else if (opcode == 101) {
					buffer.get();
				} else if (opcode == 102) {
					buffer.getShort();

				} else if (opcode == 249) {
					int var1 = buffer.get() & 0xff;
					for (int var2 = 0; var2 < var1; var2++) {
						boolean b = (buffer.get() & 0xff) == 1;
						int var5 = ByteBufferUtils.readU24Int(buffer);
						if (b) {
							ByteBufferUtils.getOSRSString(buffer);
						} else {
							buffer.getInt();
						}
					}
				} else {
					continue;
				}
				lastOpcode = opcode;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("Last succesful opcode: " + lastOpcode);
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
			if(bits == null){
				//log.info("  varbit {} was null!", def.getVarbit());
				return null;
			}
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
