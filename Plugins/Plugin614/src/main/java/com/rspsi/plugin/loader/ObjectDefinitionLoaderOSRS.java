package com.rspsi.plugin.loader;

import org.displee.cache.index.archive.Archive;
import org.displee.cache.index.archive.file.File;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import com.jagex.Client;
import com.jagex.cache.config.VariableBits;
import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.loader.config.VariableBitLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.io.Buffer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectDefinitionLoaderOSRS extends ObjectDefinitionLoader {

	private int count;
	private Map<Integer, ObjectDefinition> cache = Maps.newConcurrentMap();


	@Override
	public void init(Archive archive) {
		count = archive.getHighestId() + 1;
		for (File file : archive.getFiles()) {
			if (file != null && file.getData() != null) {
				try {
					Buffer buffer = new Buffer(file.getData());
					ObjectDefinition def = decode(file.getId(), buffer);
					cache.put(file.getId(), def);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

	}

	@Override
	public void init(Buffer data, Buffer indexBuffer) {


	}

	public void decodeNew(int opcode) {
	/*	if (opcode == 1 || opcode == 5) {
			boolean aBoolean1162 = false;
			if (opcode == 5 && aBoolean1162)
				skipReadModelIds(stream);
			int i_73_ = stream.readUnsignedByte();
			modelIds = new int[i_73_][];
			possibleTypes = new byte[i_73_];
			for (int i_74_ = 0; i_74_ < i_73_; i_74_++) {
				possibleTypes[i_74_] = (byte) stream.readByte();
				int i_75_ = stream.readUnsignedByte();
				modelIds[i_74_] = new int[i_75_];
				for (int i_76_ = 0; i_75_ > i_76_; i_76_++)
					modelIds[i_74_][i_76_] = stream.readBigSmart();
			}
			if (opcode == 5 && !aBoolean1162)
				skipReadModelIds(stream);
		} else if (opcode == 2) {
			name = stream.readString();
		} else if (opcode == 14) {
			sizeX = stream.readUnsignedByte();
		} else if (opcode == 15) {
			sizeY = stream.readUnsignedByte();
		} else if (opcode == 17) { // nocliped
			projectileCliped = false;
			clipType = 0;
		} else if (opcode == 18) {
			projectileCliped = false;
		} else if (opcode == 19) {
			secondInt = stream.readUnsignedByte();
		} else if (opcode == 21) {
			aByte3912 = (byte) 1;
		} else if (opcode == 22) {
			aBoolean3867 = true;
		} else if (opcode == 23) {
			thirdInt = 1;
		} else if (opcode == 24) {
			objectAnimation = stream.readBigSmart();
		} else if (opcode == 27) // cliped, no idea
			// diff between 2
			// and 1
			clipType = 1;
		else if (opcode == 28)
			anInt3892 = (stream
					.readUnsignedByte() << 2);
		else if (opcode == 29) {// 29
			anInt3878 = stream.readByte();
		} else if (opcode == 39) {
			anInt3840 = (stream.readByte() * 5);
		}
		// 39
		else if (opcode >= 30 && opcode < 35) {
			options[-30 + opcode] = (stream
					.readString());
		} else if (opcode == 40) {
			int i_53_ = (stream
					.readUnsignedByte());
			originalColors = new short[i_53_];
			modifiedColors = new short[i_53_];
			for (int i_54_ = 0; i_53_ > i_54_; i_54_++) {
				originalColors[i_54_] = (short) (stream
						.readUnsignedShort());
				modifiedColors[i_54_] = (short) (stream
						.readUnsignedShort());
			}
		} else if (44 == opcode) {
			int i_86_ = (short) stream
					.readUnsignedShort();
			int i_87_ = 0;
			for (int i_88_ = i_86_; i_88_ > 0; i_88_ >>= 1)
				i_87_++;
			unknownArray3 = new byte[i_87_];
			byte i_89_ = 0;
			for (int i_90_ = 0; i_90_ < i_87_; i_90_++) {
				if ((i_86_ & 1 << i_90_) > 0) {
					unknownArray3[i_90_] = i_89_;
					i_89_++;
				} else
					unknownArray3[i_90_] = (byte) -1;
			}
		} else if (opcode == 45) {
			int i_91_ = (short) stream
					.readUnsignedShort();
			int i_92_ = 0;
			for (int i_93_ = i_91_; i_93_ > 0; i_93_ >>= 1)
				i_92_++;
			unknownArray4 = new byte[i_92_];
			byte i_94_ = 0;
			for (int i_95_ = 0; i_95_ < i_92_; i_95_++) {
				if ((i_91_ & 1 << i_95_) > 0) {
					unknownArray4[i_95_] = i_94_;
					i_94_++;
				} else
					unknownArray4[i_95_] = (byte) -1;
			}
		} else if (opcode == 41) { // object anim?
			int i_71_ = (stream
					.readUnsignedByte());
			aShortArray3920 = new short[i_71_];
			aShortArray3919 = new short[i_71_];
			for (int i_72_ = 0; i_71_ > i_72_; i_72_++) {
				aShortArray3920[i_72_] = (short) (stream
						.readUnsignedShort());
				aShortArray3919[i_72_] = (short) (stream
						.readUnsignedShort());
			}
		} else if (opcode == 42) {
			int i_69_ = (stream
					.readUnsignedByte());
			aByteArray3858 = (new byte[i_69_]);
			for (int i_70_ = 0; i_70_ < i_69_; i_70_++)
				aByteArray3858[i_70_] = (byte) (stream
						.readByte());
		} else if (opcode == 62) {
			aBoolean3839 = true;
		} else if (opcode == 64) {
			aBoolean3872 = false;
		}
		// 64
		else if (opcode == 65) {
			anInt3902 = stream
					.readUnsignedShort();
		} else if (opcode == 66) {
			anInt3841 = stream
					.readUnsignedShort();
		} else if (opcode == 67) {
			anInt3917 = stream
					.readUnsignedShort();
		} else if (opcode == 69)
			cflag = stream
					.readUnsignedByte();
		else if (opcode == 70) {
			anInt3883 = stream
					.readShort() << 2;
		} else if (opcode == 71)
			anInt3889 = stream
					.readShort() << 2;
		else if (opcode == 72) {
			anInt3915 = stream
					.readShort() << 2;
		} else if (opcode == 73)
			secondBool = true;
		else if (opcode == 74)
			ignoreClipOnAlternativeRoute = true;
		else if (opcode == 75) {
			anInt3855 = stream
					.readUnsignedByte();
		} else if (opcode == 77 || opcode == 92) {
			configFileId = stream
					.readUnsignedShort();
			if (configFileId == 65535)
				configFileId = -1;
			configId = stream
					.readUnsignedShort();
			if (configId == 65535)
				configId = -1;
			int i_66_ = -1;
			if (opcode == 92) {
				i_66_ = stream
						.readBigSmart();
			}
			int i_67_ = stream
					.readUnsignedByte();
			toObjectIds = new int[i_67_
					- -2];
			for (int i_68_ = 0; i_67_ >= i_68_; i_68_++) {
				toObjectIds[i_68_] = stream
						.readBigSmart();
			}
			toObjectIds[i_67_ + 1] = i_66_;
		} else if (opcode == 78) {
			anInt3860 = stream
					.readUnsignedShort();
			anInt3904 = stream
					.readUnsignedByte();
		} else if (opcode == 79) {
			anInt3900 = stream
					.readUnsignedShort();
			anInt3905 = stream
					.readUnsignedShort();
			anInt3904 = stream
					.readUnsignedByte();
			int i_64_ = stream
					.readUnsignedByte();
			anIntArray3859 = new int[i_64_];
			for (int i_65_ = 0; i_65_ < i_64_; i_65_++)
				anIntArray3859[i_65_] = stream
						.readUnsignedShort();
		} else if (opcode == 81) {
			aByte3912 = (byte) 2;
			anInt3882 = 256 * stream
					.readUnsignedByte();
		} else if (opcode == 82) {
			aBoolean3891 = true;
		} else if (opcode == 88)
			aBoolean3853 = false;
		else if (opcode == 89) {
			aBoolean3895 = false;
		} else if (opcode == 90)
			aBoolean3870 = true;
		else if (opcode == 91) {
			aBoolean3873 = true;
		} else if (opcode == 93) {
			aByte3912 = (byte) 3;
			anInt3882 = stream
					.readUnsignedShort();
		} else if (opcode == 94)
			aByte3912 = (byte) 4;
		else if (opcode == 95) {
			aByte3912 = (byte) 5;
			anInt3882 = stream
					.readShort();
		} else if (opcode == 96) {
			aBoolean3924 = true;
		} else if (opcode == 97)
			aBoolean3866 = true;
		else if (opcode == 98)
			aBoolean3923 = true;
		else if (opcode == 99) {
			anInt3857 = stream
					.readUnsignedByte();
			anInt3835 = stream
					.readUnsignedShort();
		} else if (opcode == 100) {
			anInt3844 = stream
					.readUnsignedByte();
			anInt3913 = stream
					.readUnsignedShort();
		} else if (opcode == 101) {
			anInt3850 = stream
					.readUnsignedByte();
		} else if (opcode == 102)
			anInt3838 = stream
					.readUnsignedShort();
		else if (opcode == 103)
			thirdInt = 0;
		else if (opcode == 104) {
			anInt3865 = stream
					.readUnsignedByte();
		} else if (opcode == 105)
			aBoolean3906 = true;
		else if (opcode == 106) {
			int i_55_ = stream
					.readUnsignedByte();
			anIntArray3869 = new int[i_55_];
			animations = new int[i_55_];
			for (int i_56_ = 0; i_56_ < i_55_; i_56_++) {
				animations[i_56_] = stream
						.readBigSmart();
				int i_57_ = stream
						.readUnsignedByte();
				anIntArray3869[i_56_] = i_57_;
				anInt3881 += i_57_;
			}
		} else if (opcode == 107)
			anInt3851 = stream
					.readUnsignedShort();
		else if (opcode >= 150
				&& opcode < 155) {
			options[opcode
					+ -150] = stream
					.readString();
		} else if (opcode == 160) {
			int i_62_ = stream
					.readUnsignedByte();
			anIntArray3908 = new int[i_62_];
			for (int i_63_ = 0; i_62_ > i_63_; i_63_++)
				anIntArray3908[i_63_] = stream
						.readUnsignedShort();
		} else if (opcode == 162) {
			aByte3912 = (byte) 3;
			anInt3882 = stream
					.readInt();
		} else if (opcode == 163) {
			aByte3847 = (byte) stream
					.readByte();
			aByte3849 = (byte) stream
					.readByte();
			aByte3837 = (byte) stream
					.readByte();
			aByte3914 = (byte) stream
					.readByte();
		} else if (opcode == 164) {
			anInt3834 = stream
					.readShort();
		} else if (opcode == 165) {
			anInt3875 = stream
					.readShort();
		} else if (opcode == 166) {
			anInt3877 = stream
					.readShort();
		} else if (opcode == 167)
			anInt3921 = stream
					.readUnsignedShort();
		else if (opcode == 168) {
			aBoolean3894 = true;
		} else if (opcode == 169) {
			aBoolean3845 = true;
			// added
			// opcode
		} else if (opcode == 170) {
			int anInt3383 = stream
					.readUnsignedSmart();
			// added
			// opcode
		} else if (opcode == 171) {
			int anInt3362 = stream
					.readUnsignedSmart();
			// added
			// opcode
		} else if (opcode == 173) {
			int anInt3302 = stream
					.readUnsignedShort();
			int anInt3336 = stream
					.readUnsignedShort();
			// added
			// opcode
		} else if (opcode == 177) {
			boolean ub = true;
			// added
			// opcode
		} else if (opcode == 178) {
			int db = stream
					.readUnsignedByte();
		} else if (opcode == 189) {
			boolean bloom = true;
		} else if (opcode >= 190
				&& opcode < 196) {
			if (anIntArray4534 == null) {
				anIntArray4534 = new int[6];
				Arrays.fill(
						anIntArray4534,
						-1);
			}
			anIntArray4534[opcode - 190] = stream
					.readUnsignedShort();
		} else if (opcode == 249) {
			int length = stream
					.readUnsignedByte();
			if (parameters == null)
				parameters = new HashMap<Integer, Object>(
						length);
			for (int i_60_ = 0; i_60_ < length; i_60_++) {
				boolean bool = stream
						.readUnsignedByte() == 1;
				int i_61_ = stream
						.read24BitInt();
				if (!bool)
					parameters
							.put(i_61_,
									stream.readInt());
				else
					parameters
							.put(i_61_,
									stream.readString());

			}
		}
	}*/

}

	public ObjectDefinition decode(int id, Buffer buffer) {
		ObjectDefinition definition = new ObjectDefinition();
		definition.reset();
		definition.setId(id);
		int interactive = -1;
		int lastOpcode = -1;
		do {
			int opcode = buffer.readUByte();
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
				definition.setName(buffer.readOSRSString());
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

				interactions[opcode - 30] = buffer.readOSRSString();
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
				int count = buffer.readUByte();
				int[] originalTex = new int[count];
				int[] replacementTex = new int[count];
				for (int i = 0; i < count; i++) {
					originalTex[i] = buffer.readUShort();
					replacementTex[i] = buffer.readUShort();
				}
				definition.setRetextureToFind(originalTex);
				definition.setTextureToReplace(replacementTex);
			} else if (opcode == 60) {
				//definition.setMinimapFunction(buffer.readUShort());
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
					var3 = buffer.readUShort();
					if (var3 == 65535)
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
			} else if (opcode == 78) {//TODO Figure out what these do in OSRS
				//First short = ambient sound
				buffer.skip(3);
			} else if (opcode == 79) {
				buffer.skip(5);
				int count = buffer.readUByte();
				buffer.skip(2 * count);
			} else if (opcode == 81) {
				buffer.skip(1);//Clip type?
			} else if (opcode == 82) {
				int function = buffer.readUShort();
				definition.setMinimapFunction(function);

			} else if (opcode == 249) {
				int var1 = buffer.readUByte();
				for (int var2 = 0; var2 < var1; var2++) {
					boolean b = buffer.readUByte() == 1;
					int var5 = buffer.readUTriByte();
					if (b) {
						buffer.readOSRSString();
					} else {
						buffer.readInt();
					}
				}
			} else {
				System.out.println("ObjId: " + id + ", Unrecognised object opcode " + opcode + " last;" + lastOpcode + "ID: " + id);
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

	@Override
	public ObjectDefinition forId(int id) {
		return cache.get(id);
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
		if (morphismIndex >= 0 && morphismIndex < def.getMorphisms().length) {
			var2 = def.getMorphisms()[morphismIndex];
		} else {
			var2 = def.getMorphisms()[def.getMorphisms().length - 1];
		}
		return var2 == -1 ? null : ObjectDefinitionLoader.lookup(var2);
	}


}
