//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rspsi.plugin.loader;

import com.jagex.Client;
import com.jagex.cache.config.VariableBits;
import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.loader.config.VariableBitLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.io.Buffer;
import com.rspsi.core.misc.FixedHashMap;
import com.displee.cache.index.archive.Archive;

public class ObjectDefinitionLoaderOSRS extends ObjectDefinitionLoader {
    private int count;
    private FixedHashMap<Integer, ObjectDefinition> cache = new FixedHashMap(1000);
    private Buffer data;
    private int[] indices;

    public ObjectDefinitionLoaderOSRS() {
    }

    public void init(Archive archive) {
        this.data = new Buffer(archive.file("loc.dat"));
        Buffer buffer = new Buffer(archive.file("loc.idx"));
        this.count = buffer.readUShort();
        System.out.println("Expected " + this.count + " ids");
        this.indices = new int[this.count];
        int offset = 2;

        for(int index = 0; index < this.count; ++index) {
            this.indices[index] = offset;
            offset += buffer.readUShort();
        }

    }

    public void init(Buffer data, Buffer indexBuffer) {
        this.data = data;
        this.count = indexBuffer.readUShort();
        this.indices = new int[this.count];
        int offset = 2;

        for(int index = 0; index < this.count; ++index) {
            this.indices[index] = offset;
            offset += indexBuffer.readUShort();
        }
    }

    public ObjectDefinition decode(int id, Buffer buffer) {
        ObjectDefinition definition = new ObjectDefinition();
        definition.reset();
        int interactive = -1;
        int lastOpcode = -1;

        while(true) {
            while(true) {
                int opcode = buffer.readUByte();
                if (opcode == 0) {
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

                int var1;
                int[] originalTex;
                int[] replacementTex;
                int var5;
                if (opcode == 1) {
                    var1 = buffer.readUByte();
                    if (var1 > 0) {
                        if (definition.getModelIds() == null) {
                            originalTex = new int[var1];
                            replacementTex = new int[var1];

                            for(var5 = 0; var5 < var1; ++var5) {
                                replacementTex[var5] = buffer.readUShort();
                                originalTex[var5] = buffer.readUByte();
                            }

                            definition.setModelIds(replacementTex);
                            definition.setModelTypes(originalTex);
                        } else {
                            buffer.setPosition(buffer.getPosition() + var1 * 3);
                        }
                    }
                } else if (opcode == 2) {
                    definition.setName(buffer.readString());
                } else if (opcode == 3) {
                    definition.setDescription(buffer.readStringBytes());
                } else {
                    int var3;
                    if (opcode == 5) {
                        var1 = buffer.readUByte();
                        if (var1 > 0) {
                            if (definition.getModelIds() == null) {
                                definition.setModelTypes((int[])null);
                                originalTex = new int[var1];

                                for(var3 = 0; var3 < var1; ++var3) {
                                    originalTex[var3] = buffer.readUShort();
                                }

                                definition.setModelIds(originalTex);
                            } else {
                                buffer.setPosition(buffer.getPosition() + var1 * 2);
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
                        var1 = buffer.readUShort();
                        if (var1 == 65535) {
                            var1 = -1;
                        }

                        definition.setAnimation(var1);
                    } else if (opcode != 27) {
                        if (opcode == 28) {
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
                            var1 = buffer.readUByte();
                            originalTex = new int[var1];
                            replacementTex = new int[var1];

                            for(var5 = 0; var5 < var1; ++var5) {
                                originalTex[var5] = buffer.readUShort();
                                replacementTex[var5] = buffer.readUShort();
                            }

                            definition.setOriginalColours(originalTex);
                            definition.setReplacementColours(replacementTex);
                        } else if (opcode == 41) {
                            var1 = buffer.readUByte();
                            originalTex = new int[var1];
                            replacementTex = new int[var1];

                            for(var5 = 0; var5 < var1; ++var5) {
                                originalTex[var5] = buffer.readUShort();
                                replacementTex[var5] = buffer.readUShort();
                            }

                            definition.setRetextureToFind(originalTex);
                            definition.setTextureToReplace(replacementTex);
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
                        } else {
                            int var2;
                            if (opcode != 77 && opcode != 92) {
                                if (opcode == 78) {
                                    buffer.skip(3);
                                } else if (opcode == 79) {
                                    buffer.skip(5);
                                    int count = buffer.readByte();
                                    buffer.skip(2 * count);
                                } else if (opcode == 81) {
                                    buffer.skip(1);
                                } else if (opcode == 82) {
                                    buffer.readUShort();
                                } else {
                                    if (opcode != 249) {
                                        System.out.println("Unrecognised object opcode " + opcode + " last;" + lastOpcode + "ID: " + id);
                                        continue;
                                    }

                                    var1 = buffer.readUByte();

                                    for(var2 = 0; var2 < var1; ++var2) {
                                        boolean b = buffer.readUByte() == 1;
                                        var5 = buffer.readUTriByte();
                                        if (b) {
                                            buffer.readString();
                                        } else {
                                            buffer.readInt();
                                        }
                                    }
                                }
                            } else {
                                var1 = buffer.readUShort();
                                if (var1 == 65535) {
                                    var1 = -1;
                                }

                                var2 = buffer.readUShort();
                                if (var2 == 65535) {
                                    var2 = -1;
                                }

                                var3 = -1;
                                if (opcode == 92) {
                                    var3 = buffer.readUShort();
                                    if (var3 == 65535) {
                                        var3 = -1;
                                    }
                                }

                                var5 = buffer.readUByte();
                                int[] morphisms = new int[var5 + 2];

                                for(int i = 0; i <= var5; ++i) {
                                    morphisms[i] = buffer.readUShort();
                                    if (morphisms[i] == 65535) {
                                        morphisms[i] = -1;
                                    }
                                }

                                morphisms[var5 + 1] = var3;
                                definition.setMorphisms(morphisms);
                                definition.setVarbit(var1);
                                definition.setVarp(var2);
                            }
                        }
                    }
                }

                lastOpcode = opcode;
            }
        }
    }

    public ObjectDefinition forId(int id) {
        if (this.cache.contains(id)) {
            return this.cache.get(id);
        } else if (id >= this.indices.length) {
            return lookup(1);
        } else {
            Buffer data = new Buffer(this.data.getPayload());
            data.setPosition(this.indices[id]);
            ObjectDefinition definition = this.decode(id, data);
            definition.setId(id);
            this.cache.put(id, definition);
            return definition;
        }
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
}
