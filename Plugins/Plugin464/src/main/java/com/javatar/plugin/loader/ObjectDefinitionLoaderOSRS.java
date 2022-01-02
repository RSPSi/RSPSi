//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.javatar.plugin.loader;

import com.google.common.collect.Maps;
import com.jagex.Client;
import com.jagex.cache.config.VariableBits;
import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.def.RSArea;
import com.jagex.cache.loader.config.VariableBitLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.io.Buffer;
import com.displee.cache.index.archive.Archive;
import com.displee.cache.index.archive.file.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ObjectDefinitionLoaderOSRS extends ObjectDefinitionLoader {
    private static final Logger log = LoggerFactory.getLogger(ObjectDefinitionLoaderOSRS.class);
    private int count;
    private Map<Integer, ObjectDefinition> cache = Maps.newConcurrentMap();

    public ObjectDefinitionLoaderOSRS() {
    }

    public void init(Archive archive) {
        this.count = highestId + 1;
        File[] var2 = archive.getFiles();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            File file = var2[var4];
            if (file != null && file.getData() != null) {
                try {
                    Buffer buffer = new Buffer(file.getData());
                    ObjectDefinition def = this.decode(file.getId(), buffer);
                    this.cache.put(file.getId(), def);
                } catch (Exception var8) {
                    System.err.println("ID: " + file.getId());
                    var8.printStackTrace();
                }
            }
        }

    }

    public void init(Buffer data, Buffer indexBuffer) {
    }



    public ObjectDefinition decode(int id, Buffer buffer) {
        ObjectDefinition definition = new ObjectDefinition();
        definition.reset();
        definition.setId(id);
        int lastOpcode = -1;

        while (true) {
            int opcode = buffer.readUByte();
            if (opcode == 0) {
                definition.setInteractive(definition.getModelIds() != null && (definition.getModelTypes() == null || definition.getModelTypes()[0] == 10) || definition.getInteractions() != null);

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
            if (opcode == 1) {
                int i = buffer.readUByte();
                if (i > 0) {
                    int[] objectModels = new int[i];
                    int[] objectTypes = new int[i];
                    for (int j = 0; j < i; j++) {
                        objectModels[j] = buffer.readUShort();
                        objectTypes[j] = buffer.readUByte();
                    }
                    definition.setModelIds(objectModels);
                    definition.setModelTypes(objectTypes);
                }
            } else if (opcode == 2) {
                definition.setName(buffer.readStringAlternative());
            } else if (opcode == 5) {
                int size = buffer.readUByte();
                if (size > 0) {
                    if (definition.getModelIds() == null) {
                        int[] objectModels = new int[size];
                        for (int i = 0; i < size; i++) {
                            objectModels[i] = buffer.readUShort();
                        }
                        definition.setModelIds(objectModels);
                    } else {
                        for (int i = 0; i < size; i++) {
                            buffer.readUShort();
                        }
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
                definition.setInteractive(buffer.readUByte() == 1);
            } else if (opcode == 21) {
                definition.setContouredGround(true);
            } else if (opcode == 22) {
                definition.setDelayShading(true);
            } else if (opcode == 23) {
                definition.setOccludes(true);
            } else if (opcode == 24) {
                int animationID = buffer.readUShort();
                if (animationID == 65535)
                    animationID = -1;
                definition.setAnimation(animationID);
            } else if (opcode == 27) {
            } else if (opcode == 28) {
                definition.setDecorDisplacement(buffer.readUByte());
            } else if (opcode == 29) {
                definition.setAmbientLighting(buffer.readByte());
            } else if (opcode == 39) {
                definition.setLightDiffusion((byte) (buffer.readByte() * 5));
            } else if (opcode >= 30 && opcode < 35) {
                String[] interactions = new String[5];
                interactions[opcode - 30] = buffer.readStringAlternative();
                if (interactions[opcode - 30].equalsIgnoreCase("hidden")) {
                    interactions[opcode - 30] = null;
                }
                definition.setInteractions(interactions);
            } else if (opcode == 40) {
                int size = buffer.readUByte();
                int[] originalColours = new int[size];
                int[] replacementColours = new int[size];
                for (int i = 0; i < size; i++) {
                    originalColours[i] = buffer.readUShort();
                    replacementColours[i] = buffer.readUShort();
                }
                definition.setOriginalColours(originalColours);
                definition.setReplacementColours(replacementColours);
            } else if (opcode == 60) {
                buffer.readUShort();
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
                int varpID = buffer.readUShort();
                if (varpID == 0xFFFF) {
                    varpID = -1;
                }
                definition.setVarbit(varpID);

                int configId = buffer.readUShort();
                if (configId == 0xFFFF) {
                    configId = -1;
                }
                definition.setVarp(configId);

                int length = buffer.readUByte();
                int[] configChangeDest = new int[length + 1];

                for (int i = 0; i <= length; i++) {
                    configChangeDest[i] = buffer.readUShort();
                    if (configChangeDest[i] == 65535) {
                        configChangeDest[i] = -1;
                    }
                }
                definition.setMorphisms(configChangeDest);
            } else if (opcode == 78) {
                buffer.skip(3);
            } else if (opcode == 79) {
                buffer.skip(5);
                int size = buffer.readUByte();
                for (int i = 0; i < size; i++) {
                    buffer.readUShort();
                }
            } else {
                System.out.println("Unknown Object Definition opcode " + opcode + " last = " + lastOpcode);
            }
            lastOpcode = opcode;
        }
    }

    public ObjectDefinition forId(int id) {
        return this.cache.getOrDefault(id, new ObjectDefinition());
    }

    public int count() {
        return this.count;
    }

    public ObjectDefinition morphism(int id) {
        ObjectDefinition def = this.forId(id);
        int morphismIndex = -1;
        if (def.getVarbit() != -1) {
            VariableBits bits = VariableBitLoader.lookup(def.getVarbit());
            int variable = bits.getSetting();
            int low = bits.getLow();
            int high = bits.getHigh();
            int mask = Client.BIT_MASKS[high - low];
            morphismIndex = Client.getSingleton().settings[variable] >> low & mask;
        } else if (def.getVarp() != -1) {
            morphismIndex = Client.getSingleton().settings[def.getVarp()];
        }

        int var2;
        if (morphismIndex >= 0 && morphismIndex < def.getMorphisms().length) {
            var2 = def.getMorphisms()[morphismIndex];
        } else {
            var2 = def.getMorphisms()[def.getMorphisms().length - 1];
        }

        return var2 == -1 ? null : ObjectDefinitionLoader.lookup(var2);
    }

    public void renameMapFunctions(RSAreaLoaderOSRS areaLoader) {
        this.cache.values().stream().filter((objectDefinition) -> {
            return objectDefinition.getAreaId() != -1;
        }).forEach((objectDefinition) -> {
            RSArea area = areaLoader.forId(objectDefinition.getAreaId());
            if (objectDefinition.getName() == null || objectDefinition.getName().equals("null") || objectDefinition.getName().isEmpty()) {
                objectDefinition.setName("minimap-function:" + area.getSpriteId());
            }

        });
    }
}
