package com.jagex.map;

import com.jagex.Client;
import com.jagex.cache.def.Floor;
import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.def.RSArea;
import com.jagex.cache.loader.config.RSAreaLoader;
import com.jagex.cache.loader.floor.FloorDefinitionLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.chunk.Chunk;
import com.jagex.draw.raster.GameRasterizer;
import com.jagex.entity.Renderable;
import com.jagex.entity.model.Mesh;
import com.jagex.entity.object.RenderableObject;
import com.jagex.io.Buffer;
import com.jagex.map.object.GroundDecoration;
import com.jagex.map.tile.TileUtils;
import com.jagex.net.ResourceProvider;
import com.jagex.util.ColourUtils;
import com.jagex.util.Constants;
import com.jagex.util.ObjectKey;
import com.rspsi.options.Options;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;

@Slf4j
public final class MapRegion {

	private static final int[] anIntArray140 = { 16, 32, 64, 128 };
	private static final int[] anIntArray152 = { 1, 2, 4, 8 }; // orientation ->
	// ??
	private static final int[] COSINE_VERTICES = { 1, 0, -1, 0 };

	public static boolean lowMemory = false;
	public static int maximumPlane = 99;
	private static final int[] SINE_VERTICIES = { 0, -1, 0, 1 };

	public static int calculateHeight(int x, int y) {
		int height = interpolatedNoise(x + 45365, y + 0x16713, 4) - 128
				+ (interpolatedNoise(x + 10294, y + 37821, 2) - 128 >> 1) + (interpolatedNoise(x, y, 1) - 128 >> 2);
		height = (int) (height * 0.3D) + 35;

		if (height < 10) {
			height = 10;
		} else if (height > 60) {
			height = 60;
		}

		return height;
	}

	private static int interpolate(int a, int b, int angle, int frequencyReciprocal) {
		int cosine = 0x10000 - Constants.COSINE[angle * 1024 / frequencyReciprocal] >> 1;
		return (a * (0x10000 - cosine) >> 16) + (b * cosine >> 16);
	}

	private static int interpolatedNoise(int x, int y, int frequencyReciprocal) {
		int adj_x = x / frequencyReciprocal;
		int i1 = x & frequencyReciprocal - 1;
		int adj_y = y / frequencyReciprocal;
		int k1 = y & frequencyReciprocal - 1;
		int l1 = smoothNoise(adj_x, adj_y);
		int i2 = smoothNoise(adj_x + 1, adj_y);
		int j2 = smoothNoise(adj_x, adj_y + 1);
		int k2 = smoothNoise(adj_x + 1, adj_y + 1);
		int l2 = interpolate(l1, i2, i1, frequencyReciprocal);
		int i3 = interpolate(j2, k2, i1, frequencyReciprocal);
		return interpolate(l2, i3, k1, frequencyReciprocal);
	}

	public static int light(int colour, int light) {
		if (colour == -1)
			return 0xbc614e;

		light = light * (colour & 0x7f) / 128;
		if (light < 2) {
			light = 2;
		} else if (light > 126) {
			light = 126;
		}

		return (colour & 0xff80) + light;
	}

	private static int getOverlayShadow(int i, int j) {
		if (i == -2)
			return 0xbc614e;
		if (i == -1) {
			if (j < 0) {
				j = 0;
			} else if (j > 127) {
				j = 127;
			}
			j = 127 - j;
			return j;
		}
		j = (j * (i & 0x7f)) / 128;
		if (j < 2) {
			j = 2;
		} else if (j > 126) {
			j = 126;
		}
		return (i & 0xff80) + j;
	}

	public static void loadObjects(Buffer buffer, ResourceProvider provider) {
		int id = -1;
		do {
			int offset = buffer.readUSmartInt();
			if (offset == 0) {
				break;
			}

			id += offset;
			ObjectDefinition definition = ObjectDefinitionLoader.lookup(id);
			definition.loadModels(provider);

			do {
				int in = buffer.readUSmart();
				if (in == 0) {
					break;
				}
				buffer.readUByte();
			} while (true);
		} while (true);
	}

	public static boolean objectReady(int objectId, int type) {
		ObjectDefinition definition = ObjectDefinitionLoader.lookup(objectId);
		if (type == 11) {
			type = 10;
		} else if (type >= 5 && type <= 8) {
			type = 4;
		}

		return definition.ready(type);
	}

	public static boolean objectsReady(byte[] data, int x, int y, Map<Integer, Integer> remapping) {
		boolean ready = true;
		Buffer buffer = new Buffer(data);
		int id = -1;

		while (true) {
			int offset = buffer.readUSmartInt();
			if (offset == 0)
				return ready;

			id += offset;
			int position = 0;
			boolean skip = false;

			while (true) {
				int terminate;
				if (skip) {
					terminate = buffer.readUSmart();
					if (terminate == 0) {
						break;
					}

					buffer.readUByte();
				} else {
					terminate = buffer.readUSmart();
					if (terminate == 0) {
						break;
					}

					position += terminate - 1;
					int localY = position & 63;
					int localX = position >> 6 & 63;
					int type = buffer.readUByte() >> 2;
					int viewportX = localX + x;
					int viewportY = localY + y;

					// if (viewportX > 0 && viewportY > 0 && viewportX < 63 && viewportY < 63) {
					ObjectDefinition definition = ObjectDefinitionLoader.lookup(remapping.getOrDefault(id, id));
					if(definition == null)
						continue;
					if (type != 22 || !lowMemory || definition.isInteractive() || definition.obstructsGround()) {
						ready &= definition.ready();
						// if(ready)
						// skip = true;
					}
					// }
				}
			}
		}
	}

	public static boolean objectsReady(
			final byte[] data,
			final int x, final int y
	) {
		boolean ready = true;

		final Buffer buffer = new Buffer(data);

		int id = -1;
		int idOffset;

		while ((idOffset = buffer.readUnsignedIntSmartShortCompat()) != 0) {
			id += idOffset;

			final ObjectDefinition definition = ObjectDefinitionLoader.lookup(id);

			while (buffer.readUnsignedShortSmart() != 0) {
				final int attributes = buffer.readUnsignedByte();
				if (definition == null) {
					continue;
				}
				final int type = attributes >> 2;
				if (type != 22
						|| !lowMemory
						|| definition.isInteractive()
						|| definition.obstructsGround()) {
					ready &= definition.ready();
				}
			}
		}

		return ready;
	}

	private static int perlinNoise(int x, int y) {
		int n = x + y * 57;
		n = n << 13 ^ n;
		n = n * (n * n * 15731 + 0xc0ae5) + 0x5208dd0d & 0x7fffffff;
		return n >> 19 & 0xff;
	}

	private static int smoothNoise(int x, int y) {
		int corners = perlinNoise(x - 1, y - 1) + perlinNoise(x + 1, y - 1) + perlinNoise(x - 1, y + 1)
				+ perlinNoise(x + 1, y + 1);
		int sides = perlinNoise(x - 1, y) + perlinNoise(x + 1, y) + perlinNoise(x, y - 1) + perlinNoise(x, y + 1);
		int center = perlinNoise(x, y);
		return corners / 16 + sides / 8 + center / 4;
	}

	private final int hueOffset = -8;
	private final int luminanceOffset = -16;

	private final int[] anIntArray128;
	private final int[][] tileLighting;
	private final int[][][] anIntArrayArrayArray135;
	private final int[] chromas;
	private final int[] hues;
	private final int length;
	private final int width;
	private final int[] luminances;
	public final byte[][][] overlayOrientations;
	public final short[][][] overlays;
	public final short[][][] underlays;
	public final byte[][][] manualTileHeight;
	public final byte[][][] overlayShapes;
	private final int[] saturations;
	public final byte[][][] shading;
	public final byte[][][] tileFlags;
	public final int[][][] tileHeights;

	private final SceneGraph scene;

	public MapRegion(SceneGraph scene, int width, int length) {
		this.scene = scene;
		maximumPlane = 99;
		this.width = width;
		this.length = length;
		tileHeights = new int[4][width + 1][length + 1];
		tileFlags = new byte[4][width][length];
		underlays = new short[4][width][length];
		overlays = new short[4][width][length];
		manualTileHeight = new byte[4][width][length];
		overlayShapes = new byte[4][width][length];
		overlayOrientations = new byte[4][width][length];
		anIntArrayArrayArray135 = new int[4][width + 1][length + 1];
		shading = new byte[4][width + 1][length + 1];
		tileLighting = new int[width + 1][length + 1];
		hues = new int[length];
		saturations = new int[length];
		luminances = new int[length];
		chromas = new int[length];
		anIntArray128 = new int[length];

	}

	public void setHeights() {
		// TODO Find a better way to fix the sloping issue

		for (int z = 0; z < 4; z++) {
			if (length + 1 >= 0) {
				System.arraycopy(tileHeights[z][width - 1], 0, tileHeights[z][width], 0, length + 1);
			}

			for (int x = 0; x <= width; x++) {
				tileHeights[z][x][length] = tileHeights[z][x][length - 1];
			}
		}
	}

	public void decodeConstructedLandscapes(byte[] data, SceneGraph scene, int plane, int topLeftRegionX,
                                            int topLeftRegionY, int collisionPlane, int regionX, int regionY, int orientation) {

		decoding: {
			Buffer buffer = new Buffer(data);
			int id = -1;
			do {
				int idOffset = buffer.readUSmartInt();
				if (idOffset == 0) {
					break decoding;
				}

				id += idOffset;
				int config = 0;

				do {
					int offset = buffer.readUSmartInt();
					if (offset == 0) {
						break;
					}

					config += offset - 1;
					int x = config & 0x3f;
					int y = config >> 6 & 0x3f;
					int objectPlane = config >> 12;
					int packed = buffer.readUByte();
					int type = packed >> 2;
					int rotation = packed & 3;

					if (objectPlane == plane && y >= regionY && y < regionY + 8 && x >= regionX && x < regionX + 8) {
						ObjectDefinition definition = ObjectDefinitionLoader.lookup(id);
						int localX = topLeftRegionX + TileUtils.getObjectXOffset(x & 7, y & 7, definition.getWidth(),
								definition.getLength(), orientation);
						int localY = topLeftRegionY + TileUtils.getObjectYOffset(x & 7, y & 7, definition.getWidth(),
								definition.getLength(), orientation);

						if (localX > 0 && localY > 0 && localX < length - 1 && localY < width - 1) {
							/*
							 * int mapPlane = objectPlane; if ((tileFlags[1][localX][localY] & BRIDGE_TILE)
							 * == BRIDGE_TILE) { mapPlane--; }
							 */

							spawnObjectToWorld(scene, id, localX, localY, collisionPlane, type,
									rotation + orientation & 3, false);
						}
					}
				} while (true);
			} while (true);
		}
	}

	public static boolean validObjectFile(byte[] data) {

		try {

			Buffer buffer = new Buffer(data);
			for (int z = 0; z < 4; z++) {
				for (int localX = 0; localX < 64; localX++) {
					for (int localY = 0; localY < 64; localY++) {
						do {
							int in = buffer.readUByte();
							if (in == 0) {
								break;
							} else if (in == 1) {
								buffer.readUByte();
								break;
							} else if (in <= 49) {
								buffer.readUByte();
							}
						} while (true);
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return true;
		}
		return false;

	}

	public void unpackObjects(SceneGraph scene, byte[] data, int offsetX, int offsetY) {
		//System.out.println("Width: " + width + " Length: " + length);

		final Buffer buf = new Buffer(data);

		int id = -1;
		int idOffset;

		while ((idOffset = buf.readUnsignedIntSmartShortCompat()) != 0) {
			id += idOffset;

			int position = 0;
			int positionOffset;

			while ((positionOffset = buf.readUnsignedShortSmart()) != 0) {
				position += positionOffset - 1;

				final int localY = position & 0x3F;
				final int localX = position >> 6 & 0x3F;
				int height = position >> 12;
				if (height >= 4) {
					height = 3;
				}

				final int attributes = buf.readUnsignedByte();
				final int type = attributes >> 2;
				final int orientation = attributes & 0x3;

				final int x = offsetX + localX;
				final int y = offsetY + localY;

				spawnObjectToWorld(scene, id, x, y, height, type, orientation, false);
			}
		}
	}

	public void decodeOldMapData(Buffer buffer, int x, int y, int z, int regionX, int regionY) {// XXX
		if (x >= 0 && x < width && y >= 0 && y < length) {
			tileFlags[z][x][y] = 0;
			manualTileHeight[z][x][y] = 0;
			tileHeights[z][x][y] = 0;
			overlays[z][x][y] = 0;
			overlayShapes[z][x][y] = 0;
			overlayOrientations[z][x][y] = 0;
			underlays[z][x][y] = 0;
			do {
				int type = buffer.readUByte();

				if (type == 0) {
					manualTileHeight[z][x][y] = 0;
					if (z == 0) {
						tileHeights[0][x][y] = -calculateHeight(0xe3b7b + x + regionX, 0x87cce + y + regionY) * 8;
					} else {
						tileHeights[z][x][y] = tileHeights[z - 1][x][y] - 240;
					}

					return;
				} else if (type == 1) {
					manualTileHeight[z][x][y] = 1;
					int height = buffer.readUByte();
					if (height == 1) {
						height = 0;
					}
					if (z == 0) {
						tileHeights[0][x][y] = -height * 8;
					} else {
						tileHeights[z][x][y] = tileHeights[z - 1][x][y] - height * 8;
					}

					return;
				} else if (type <= 49) {
					overlays[z][x][y] = buffer.readByte();
					overlayShapes[z][x][y] = (byte) ((type - 2) / 4);
					overlayOrientations[z][x][y] = (byte) (type - 2 & 3);
				} else if (type <= 81) {
					tileFlags[z][x][y] = (byte) (type - 49);
				} else {
					underlays[z][x][y] = (byte) (type - 81);
				}
			} while (true);
		}

		do {
			int in = buffer.readUByte();
			if (in == 0) {
				break;
			} else if (in == 1) {
				buffer.readUByte();
				return;
			} else if (in <= 49) {
				buffer.readUByte();
			}
		} while (true);
	}

	public void loadTerrain(
			final Buffer in,
			final int regionX, final int regionY,
			final int z,
			final int x, final int y
	) {
		if (x >= 0 && x < width && y >= 0 && y < length) {
			tileFlags[z][x][y] = 0;

			while (true) {
				final int attribute = in.readUnsignedShort();

				if (attribute == 0) {
					manualTileHeight[z][x][y] = 0;

					if (z == 0) {
						tileHeights[0][x][y] = -calculateHeight(0xe3b7b + x + regionX, 0x87cce + y + regionY) * 8;
					} else {
						tileHeights[z][x][y] = tileHeights[z - 1][x][y] - 240;
					}

					return;
				} else if (attribute == 1) {
					manualTileHeight[z][x][y] = 1;

					int height = in.readUnsignedByte();
					if (height == 1) {
						height = 0;
					}

					if (z == 0) {
						tileHeights[0][x][y] = -height * 8;
					} else {
						tileHeights[z][x][y] = tileHeights[z - 1][x][y] - height * 8;
					}

					return;
				} else if (attribute <= 49) {
					overlays[z][x][y] = in.readShortRegular();
					overlayShapes[z][x][y] = (byte) ((attribute - 2) / 4);
					overlayOrientations[z][x][y] = (byte) (attribute - 2 & 3);
				} else if (attribute <= 81) {
					tileFlags[z][x][y] = (byte) (attribute - 49);
				} else {
					underlays[z][x][y] = (short) (attribute - 81);
				}
			}
		}

		while (true) {
			int i = in.readUShort();
			if (i == 0) {
				break;
			} else if (i == 1) {
				in.readUByte();
				return;
			} else if (i <= 49) {
				in.readUShort();
			}
		}
	}

	public void loadTerrain(
			final Buffer in,
			final int regionX, final int regionY,
			final int baseX, final int baseY
	) {
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < 64; x++) {
				final int ourX = baseX + x;
				for (int y = 0; y < 64; y++) {
					loadTerrain(in, regionX, regionY, z, ourX, baseY + y);
				}
			}
		}
	}

	public void unpackTiles(byte[] data, int dX, int dY, int regionX, int regionY) {
		final Buffer buffer = new Buffer(data);
		final int position = buffer.position;

		try {
			loadTerrain(buffer, regionX, regionY, dX, dY);
		} catch (final Exception e) {
			e.printStackTrace();

			buffer.position = position;

			for (int z = 0; z < 4; z++) {
				for (int localX = 0; localX < 64; localX++) {
					for (int localY = 0; localY < 64; localY++) {
						decodeOldMapData(buffer, localX + dX, localY + dY, z, regionX, regionY);
					}
				}
			}
		}

		this.setHeights();// XXX Fix for ending of region sloping down
	}

	/**
	 * Returns the plane that actually contains the collision flag, to adjust for
	 * objects such as bridges. TODO better name
	 *
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 * @return The correct z coordinate.
	 */
	public int getCollisionPlane(int x, int y, int z) {
		/*
		 * if ((tileFlags[z][x][y] & FORCE_LOWEST_PLANE) != 0) return 0; else if (z > 0
		 * && (tileFlags[1][x][y] & BRIDGE_TILE) != 0) return z - 1;
		 */

		return z;
	}

	private int underlay_floor_map_color;
	private int underlay_floor_texture;

	public void method171(SceneGraph scene) {

		for (int z = 0; z < 4; z++) {
			byte[][] shading = this.shading[z];
			byte byte0 = 96;
			char diffusion = '\u0300';
			byte lightX = -50;
			byte lightY = -10;
			byte lightZ = -50;

			int light = diffusion * (int) Math.sqrt(lightX * lightX + lightY * lightY + lightZ * lightZ) >> 8;
			for (int y = 1; y < length; y++) {
				for (int x = 1; x < width; x++) {
					int dhWidth = tileHeights[z][x + 1][y] - tileHeights[z][x - 1][y];
					int dhLength = tileHeights[z][x][y + 1] - tileHeights[z][x][y - 1];

					int distance = (int) Math.sqrt(dhWidth * dhWidth + 0x10000 + dhLength * dhLength);
					int dx = (dhWidth << 8) / distance;
					int dy = 0x10000 / distance;
					int dz = (dhLength << 8) / distance;
					int lightness = byte0 + (lightX * dx + lightY * dy + lightZ * dz) / light;
					int offset = (shading[x - 1][y] >> 2) + (shading[x + 1][y] >> 3) + (shading[x][y - 1] >> 2)
							+ (shading[x][y + 1] >> 3) + (shading[x][y] >> 1);
					tileLighting[x][y] = lightness - offset;
				}
			}

			for (int index = 0; index < length; index++) {
				hues[index] = 0;
				saturations[index] = 0;
				luminances[index] = 0;
				chromas[index] = 0;
				anIntArray128[index] = 0;
			}

			for (int centreX = -5; centreX < width + 5; centreX++) {
				for (int y = 0; y < length; y++) {
					int maxX = centreX + 5;
					if (maxX >= 0 && maxX < width) {
						int id = underlays[z][maxX][y] & 0x7FFF;

						if (id > 0) {
							Floor floor = FloorDefinitionLoader.getUnderlay(id - 1);
							if (floor == null)
								floor = FloorDefinitionLoader.getUnderlay(0);
							hues[y] += floor.getWeightedHue();
							saturations[y] += floor.getSaturation();
							luminances[y] += floor.getLuminance();
							chromas[y] += floor.getChroma();
							anIntArray128[y]++;
						}
					}

					int minX = centreX - 5;
					if (minX >= 0 && minX < width) {
						int id = underlays[z][minX][y] & 0x7FFF;

						if (id > 0) {
							Floor floor = FloorDefinitionLoader.getUnderlay(id - 1);
							if (floor == null)
								floor = FloorDefinitionLoader.getUnderlay(0);
							hues[y] -= floor.getWeightedHue();
							saturations[y] -= floor.getSaturation();
							luminances[y] -= floor.getLuminance();
							chromas[y] -= floor.getChroma();
							anIntArray128[y]--;
						}
					}
				}

				if (centreX >= 0 && centreX < width) {
					int blended_anIntArray124 = 0;
					int blended_anIntArray125 = 0;
					int blended_anIntArray126 = 0;
					int blended_anIntArray124_divisor = 0;
					int blend_direction_tracker = 0;

					for (int centreY = -5; centreY < length + 5; centreY++) {
						int j18 = centreY + 5;
						if (j18 >= 0 && j18 < length) {
							blended_anIntArray124 += hues[j18];
							blended_anIntArray125 += saturations[j18];
							blended_anIntArray126 += luminances[j18];
							blended_anIntArray124_divisor += chromas[j18];
							blend_direction_tracker += anIntArray128[j18];
						}

						int k18 = centreY - 5;
						if (k18 >= 0 && k18 < length) {
							blended_anIntArray124 -= hues[k18];
							blended_anIntArray125 -= saturations[k18];
							blended_anIntArray126 -= luminances[k18];
							blended_anIntArray124_divisor -= chromas[k18];
							blend_direction_tracker -= anIntArray128[k18];
						}

						if (centreY >= 0
								&& centreY < length/*
													 * && (!lowMemory || (tileFlags[0][centreX][centreY] & BRIDGE_TILE)
													 * != 0 || (tileFlags[z][centreX][centreY] & DISABLE_RENDERING) == 0
													 * && getCollisionPlane(centreX, centreY, z) == currentPlane)
													 */) {
							if (z < maximumPlane) {
								maximumPlane = z;
							}

							int underlay = underlays[z][centreX][centreY] & 0x7FFF;
							int overlayFloorId = overlays[z][centreX][centreY] & 0x7FFF;

							if (underlay > 0 || overlayFloorId > 0) {
								int centreHeight = tileHeights[z][centreX][centreY];
								int eastHeight = tileHeights[z][centreX + 1][centreY];
								int northEastHeight = tileHeights[z][centreX + 1][centreY + 1];
								int northHeight = tileHeights[z][centreX][centreY + 1];
								int centreLight = tileLighting[centreX][centreY];
								int eastLight = tileLighting[centreX + 1][centreY];
								int northEastLight = tileLighting[centreX + 1][centreY + 1];
								int northLight = tileLighting[centreX][centreY + 1];
								int hsl_bitset_unmodified = -1;
								int hsl_bitset_randomized = -1;

								/*
								 * if (underlay > 0) { int hue = blended_anIntArray124 * 256 /
								 * blended_anIntArray124_divisor; int saturation = blended_anIntArray125 /
								 * blend_direction_tracker; int luminance = blended_anIntArray126 /
								 * blend_direction_tracker; hsl_bitset_unmodified = ColourUtils.toHsl(hue,
								 * saturation, luminance); hue = hue + hueOffset & 0xff; luminance +=
								 * luminanceOffset;
								 * 
								 * if (luminance < 0) { luminance = 0; } else if (luminance > 255) { luminance =
								 * 255; }
								 * 
								 * hsl_bitset_randomized = ColourUtils.toHsl(hue, saturation, luminance); }
								 */

								if (underlay > 0 || overlayFloorId != 0) {
									int anIntArray124 = -1;
									int sat = 0;
									int lum = 0;
									if (underlay == 0) {
										anIntArray124 = -1;
										sat = 0;
										lum = 0;
									} else if (underlay > 0) {
										if (blended_anIntArray124_divisor < 1) {
											blended_anIntArray124_divisor = 1;
										}
										if (blend_direction_tracker == 0) {
											blend_direction_tracker = 1;
										}

										anIntArray124 = (blended_anIntArray124 << 8) / blended_anIntArray124_divisor;
										sat = blended_anIntArray125 / blend_direction_tracker;
										lum = blended_anIntArray126 / blend_direction_tracker;
										hsl_bitset_unmodified = ColourUtils.toHsl(anIntArray124, sat, lum);
										// anIntArray124 = anIntArray124 + anIntArray124Offset & 0xff;
										// lum += offsetLightning;
										if (lum < 0) {
											lum = 0;
										} else if (lum > 255) {
											lum = 255;
										}

									} else {
										anIntArray124 = underlay;
										sat = 0;
										lum = 0;
									}
									if (anIntArray124 != -1 && hsl_bitset_randomized == -1) {
										hsl_bitset_randomized = ColourUtils.toHsl(anIntArray124, sat, lum);
									}

									if (hsl_bitset_unmodified == -1) {
										hsl_bitset_unmodified = hsl_bitset_randomized;
									}

								}

								if (z > 0) {
									boolean flag = underlay != 0 || overlayShapes[z][centreX][centreY] == 0;

                                    if (overlayFloorId > 0
											&& !FloorDefinitionLoader.getOverlay(overlayFloorId - 1).isShadowed()) {
										flag = false;
									}

									if (flag && centreHeight == eastHeight && centreHeight == northEastHeight
											&& centreHeight == northHeight) {
										anIntArrayArrayArray135[z][centreX][centreY] |= 0x924;
									}
								}

								int rgb_bitset_randomized = 0;
								if (hsl_bitset_unmodified != -1) {
									try {
										rgb_bitset_randomized = GameRasterizer.getInstance().colourPalette[light(
												hsl_bitset_randomized, 96)];
									} catch (Exception ex) {

										System.out.println("ERROR WITH " + overlayFloorId + " : " + underlay + " at "
												+ centreX + ":" + centreY + ":" + z);
									}
								}

								if (overlayFloorId == 0) {
									if (Options.hdTextures.get()) {
										if (underlay - 1 >= FloorDefinitionLoader.getUnderlayCount()) {
											underlay = FloorDefinitionLoader.getUnderlayCount();
										}
										Floor floor = FloorDefinitionLoader.getUnderlay(underlay - 1);
										if(floor == null)
											continue;
										int underlay_texture_id = floor.getTexture();
										if (underlay_texture_id != -1) {
											underlay_texture_id = 154; // 632, 154
										}
										underlay_floor_texture = underlay_texture_id;
										underlay_floor_map_color = ColourUtils.checkedLight(hsl_bitset_unmodified, 96);
										int tile_opcode = overlayShapes[z][centreX][centreY] + 1;
										if (tile_opcode == 1) {
											tile_opcode = 434;
										}
										byte tile_orientation = overlayShapes[z][centreX][centreY];
										/**
										 * Adds underlay tile
										 */
										int overlay_hsl = ColourUtils.toHsl(floor.getHue(), floor.getSaturation(),
												floor.getLuminance());

										byte flag = tileFlags[z][centreX][centreY];
										scene.addTile(z, centreX, centreY, tile_opcode, tile_orientation,
												underlay_texture_id, centreHeight, eastHeight, northEastHeight,
												northHeight, light(hsl_bitset_unmodified, centreLight),
												light(hsl_bitset_unmodified, eastLight),
												light(hsl_bitset_unmodified, northEastLight),
												light(hsl_bitset_unmodified, northLight),
												getOverlayShadow(overlay_hsl, centreLight),
												getOverlayShadow(overlay_hsl, eastLight),
												getOverlayShadow(overlay_hsl, northEastLight),
												getOverlayShadow(overlay_hsl, northLight), rgb_bitset_randomized,
												rgb_bitset_randomized, underlay_floor_map_color, underlay_floor_texture,
												underlay_floor_map_color, false, flag, underlay - 1, overlayFloorId - 1);
									} else {
										byte flag = tileFlags[z][centreX][centreY];
										scene.addTile(z, centreX, centreY, 0, 0, -1, centreHeight, eastHeight,
												northEastHeight, northHeight, light(hsl_bitset_unmodified, centreLight),
												light(hsl_bitset_unmodified, eastLight),
												light(hsl_bitset_unmodified, northEastLight),
												light(hsl_bitset_unmodified, northLight), 0, 0, 0, 0,
												rgb_bitset_randomized, rgb_bitset_randomized, -1, 0, 0, true, flag, underlay - 1, overlayFloorId - 1);
									}

								} else {
									int tileType = overlayShapes[z][centreX][centreY] + 1;
									byte orientation = overlayOrientations[z][centreX][centreY];
									if (overlayFloorId - 1 >= FloorDefinitionLoader.getOverlayCount()) {
										overlayFloorId = FloorDefinitionLoader.getOverlayCount();
									}
									Floor overlayFloor = FloorDefinitionLoader.getOverlay(overlayFloorId - 1);
									if(overlayFloor == null)
										continue;

									int overlayTextureId = overlayFloor.getTexture();

									int overlayTextureColour = -1;
									int overlayMapColour = 0;

									int overlayHslColour;
									int overlayRgbColour;

									if (overlayTextureId > TextureLoader.instance.count()) {
										overlayTextureId = -1;
									}
									if (overlayTextureId >= 0 && TextureLoader.getTexture(overlayTextureId) == null) {
										overlayTextureId = -1;
									}

									if (overlayTextureId >= 0) {
										overlayRgbColour = TextureLoader.getTexture(overlayTextureId)
												.averageTextureColour();
										overlayHslColour = -1;
									} else if (overlayFloor.getRgb() == 0xff00ff) { // transparent
										overlayRgbColour = 0;
										overlayHslColour = -2;
										overlayTextureId = -1;
									} else if (overlayFloor.getRgb() == 0x333333) { // transparent
										overlayRgbColour = GameRasterizer.getInstance().colourPalette[ColourUtils
												.checkedLight(overlayFloor.getColour(), 96)];
										overlayHslColour = -2;
										overlayTextureId = -1;
									} else {
										overlayHslColour = ColourUtils.toHsl(overlayFloor.getHue(),
												overlayFloor.getSaturation(), overlayFloor.getLuminance());
										overlayRgbColour = GameRasterizer.getInstance().colourPalette[ColourUtils
												.checkedLight(overlayFloor.getColour(), 96)];
									}

									if (overlayRgbColour == 0x000000 && overlayFloor.getAnotherRgb() != -1) {

										int newOverlayColour = ColourUtils.toHsl(overlayFloor.getAnotherHue(),
												overlayFloor.getAnotherSaturation(),
												overlayFloor.getAnotherLuminance());
										overlayRgbColour = GameRasterizer.getInstance().colourPalette[ColourUtils
												.checkedLight(newOverlayColour, 96)];

									}

									/*
									 * if (overlayRgbColour == 0x000000 && floor.getAnotherRgb() != -1) {
									 * 
									 * int newOverlayColour = ColourUtils.toHsl(floor.getAnotherHue(),
									 * floor.getAnotherSaturation(), floor.getAnotherLuminance()); overlayRgbColour
									 * = GameRasterizer.getInstance().colourPalette[ColourUtils.checkedLight(
									 * newOverlayColour, 96)];
									 * 
									 * }
									 */

									/*if ((overlayFloorId - 1) == 54) {
										overlayRgbColour = 0x8B8B83;
										overlayHslColour = -2;
									}
									if ((overlayFloorId - 1) == 111) {
										overlayRgbColour = TextureLoader.getTexture(1).averageTextureColour();
										overlayHslColour = -1;// method177(150,100,100);
										overlayTextureId = 1;
									} else if (overlayHslColour == 6363) { // river bank (brown shit) 508
										overlayRgbColour = 0x483B21;
										overlayHslColour = ColourUtils.toHsl(25, 146, 24);
									}
										 * else if((overlayFloorId-1) == 54){ overlayRgbColour =
										 * overlayFloor.getColour(); overlayHslColour = -2; overlayTextureId = -1; }
										 */

									if (Options.hdTextures.get()) {

										if (overlayFloor.getAnotherRgb() != -1) {
											overlayMapColour = (GameRasterizer.getInstance().colourPalette[overlayFloor
													.getAnotherRgb()] != 1)
															? GameRasterizer.getInstance().colourPalette[overlayFloor
																	.getAnotherRgb()]
															: 0;
										}
										if ((overlayTextureId >= 0)) {
											overlayHslColour = -1;
											if (overlayFloor.getRgb() != 0xff00ff) {
												overlayHslColour = overlayFloor.getRgb();
												if (overlayTextureId > TextureLoader.instance.count()) {
													overlayRgbColour = (overlayHslColour != -1
															? GameRasterizer
																	.getInstance().colourPalette[overlayHslColour]
															: 0);
												}
												overlayTextureColour = getOverlayShadow(overlayFloor.getRgb(), 96);
											} else {
												if (overlayTextureId > TextureLoader.instance.count()) {
													overlayRgbColour = overlayFloor.getAnotherRgb();
												}
												overlayHslColour = -2;
												underlay_floor_map_color = -1;
												overlayTextureColour = -1;
											}
										} else if (overlayFloor.getRgb() == -1) {
											if (overlayTextureId > TextureLoader.instance.count()) {
												overlayRgbColour = overlayMapColour;
											}
											overlayHslColour = -2;
											// ?
											if (z > 0) {
												underlay_floor_texture = -1;
											}

											overlayTextureId = -1;
										} else {
											overlayTextureColour = getOverlayShadow(overlayFloor.getRgb(), 96);
											overlayHslColour = overlayFloor.getRgb();
											if (overlayTextureId > TextureLoader.instance.count()) {
												overlayRgbColour = GameRasterizer
														.getInstance().colourPalette[overlayTextureColour];
											}
										}
									}

									if (Options.hdTextures.get()) {
										byte flag = tileFlags[z][centreX][centreY];
										scene.addTile(z, centreX, centreY, tileType, orientation, overlayTextureId,
												centreHeight, eastHeight, northEastHeight, northHeight,
												light(hsl_bitset_unmodified, centreLight),
												light(hsl_bitset_unmodified, eastLight),
												light(hsl_bitset_unmodified, northEastLight),
												light(hsl_bitset_unmodified, northLight),
												getOverlayShadow(overlayHslColour, centreLight),
												getOverlayShadow(overlayHslColour, eastLight),
												getOverlayShadow(overlayHslColour, northEastLight),
												getOverlayShadow(overlayHslColour, northLight), rgb_bitset_randomized,
												overlayRgbColour, overlayTextureColour, underlay_floor_texture,
												underlay_floor_map_color, false, flag, underlay - 1, overlayFloorId - 1);
									} else {
										byte flag = tileFlags[z][centreX][centreY];
										scene.addTile(z, centreX, centreY, tileType, orientation, overlayTextureId,
												centreHeight, eastHeight, northEastHeight, northHeight,
												light(hsl_bitset_unmodified, centreLight),
												light(hsl_bitset_unmodified, eastLight),
												light(hsl_bitset_unmodified, northEastLight),
												light(hsl_bitset_unmodified, northLight),
												ColourUtils.checkedLight(overlayHslColour, centreLight),
												ColourUtils.checkedLight(overlayHslColour, eastLight),
												ColourUtils.checkedLight(overlayHslColour, northEastLight),
												ColourUtils.checkedLight(overlayHslColour, northLight),
												rgb_bitset_randomized, overlayRgbColour, -1, 0, 0, true, flag, underlay - 1, overlayFloorId - 1);
									}

								}
							}
						}
					}
				}
			}

			for (int y = 0; y < length; y++) {
				for (int x = 0; x < width; x++) {
					scene.setCollisionPlane(x, y, z, getCollisionPlane(x, y, z));
				}
			}
		}

		scene.shadeObjects(64, -50, -10, -50, 768);
		/*
		 * for (int x = 0; x < width; x++) { for (int y = 0; y < length; y++) { if
		 * ((tileFlags[1][x][y] & BRIDGE_TILE) != 0) { scene.method276(x, y); } } }
		 */
		/*
		 * XXX Something to do with occluding objects int flag = 1; int j2 = 2; int k2 =
		 * 4; for (int plane = 0; plane < 4; plane++) { if (plane > 0) { flag <<= 3; j2
		 * <<= 3; k2 <<= 3; }
		 * 
		 * for (int z = 0; z <= plane; z++) { for (int y = 0; y <= length; y++) { for
		 * (int x = 0; x <= width; x++) { if ((anIntArrayArrayArray135[z][x][y] & flag)
		 * != 0) { int currentY = y; int l5 = y; int i7 = z; int k8 = z;
		 * 
		 * for (; currentY > 0 && (anIntArrayArrayArray135[z][x][currentY - 1] & flag)
		 * != 0; currentY--) {
		 * 
		 * }
		 * 
		 * for (; l5 < length && (anIntArrayArrayArray135[z][x][l5 + 1] & flag) != 0;
		 * l5++) {
		 * 
		 * }
		 * 
		 * label0: for (; i7 > 0; i7--) { for (int j10 = currentY; j10 <= l5; j10++) {
		 * if ((anIntArrayArrayArray135[i7 - 1][x][j10] & flag) == 0) { break label0; }
		 * } }
		 * 
		 * label1: for (; k8 < plane; k8++) { for (int k10 = currentY; k10 <= l5; k10++)
		 * { if ((anIntArrayArrayArray135[k8 + 1][x][k10] & flag) == 0) { break label1;
		 * } } }
		 * 
		 * int l10 = (k8 + 1 - i7) * (l5 - currentY + 1); if (l10 >= 8) { char c1 =
		 * '\360'; int k14 = tileHeights[k8][x][currentY] - c1; int l15 =
		 * tileHeights[i7][x][currentY]; chunk.sceneGraph.method277(plane, x * 128, l15,
		 * x * 128, l5 * 128 + 128, k14, currentY * 128, 1); for (int l16 = i7; l16 <=
		 * k8; l16++) { for (int l17 = currentY; l17 <= l5; l17++) {
		 * anIntArrayArrayArray135[l16][x][l17] &= ~flag; } } } }
		 * 
		 * if ((anIntArrayArrayArray135[z][x][y] & j2) != 0) { int l4 = x; int i6 = x;
		 * int j7 = z; int l8 = z; for (; l4 > 0 && (anIntArrayArrayArray135[z][l4 -
		 * 1][y] & j2) != 0; l4--) {
		 * 
		 * } for (; i6 < width && (anIntArrayArrayArray135[z][i6 + 1][y] & j2) != 0;
		 * i6++) {
		 * 
		 * } label2: for (; j7 > 0; j7--) { for (int i11 = l4; i11 <= i6; i11++) { if
		 * ((anIntArrayArrayArray135[j7 - 1][i11][y] & j2) == 0) { break label2; } } }
		 * 
		 * label3: for (; l8 < plane; l8++) { for (int j11 = l4; j11 <= i6; j11++) { if
		 * ((anIntArrayArrayArray135[l8 + 1][j11][y] & j2) == 0) { break label3; } } }
		 * 
		 * int k11 = (l8 + 1 - j7) * (i6 - l4 + 1); if (k11 >= 8) { char c2 = '\360';
		 * int l14 = tileHeights[l8][l4][y] - c2; int i16 = tileHeights[j7][l4][y];
		 * chunk.sceneGraph.method277(plane, l4 * 128, i16, i6 * 128 + 128, y * 128,
		 * l14, y * 128, 2); for (int i17 = j7; i17 <= l8; i17++) { for (int i18 = l4;
		 * i18 <= i6; i18++) { anIntArrayArrayArray135[i17][i18][y] &= ~j2; } } } }
		 * 
		 * if ((anIntArrayArrayArray135[z][x][y] & k2) != 0) { int i5 = x; int j6 = x;
		 * int k7 = y; int i9 = y; for (; k7 > 0 && (anIntArrayArrayArray135[z][x][k7 -
		 * 1] & k2) != 0; k7--) {
		 * 
		 * } for (; i9 < length && (anIntArrayArrayArray135[z][x][i9 + 1] & k2) != 0;
		 * i9++) {
		 * 
		 * } label4: for (; i5 > 0; i5--) { for (int l11 = k7; l11 <= i9; l11++) { if
		 * ((anIntArrayArrayArray135[z][i5 - 1][l11] & k2) == 0) { break label4; } } }
		 * 
		 * label5: for (; j6 < width; j6++) { for (int i12 = k7; i12 <= i9; i12++) { if
		 * ((anIntArrayArrayArray135[z][j6 + 1][i12] & k2) == 0) { break label5; } } }
		 * 
		 * if ((j6 - i5 + 1) * (i9 - k7 + 1) >= 4) { int j12 = tileHeights[z][i5][k7];
		 * chunk.sceneGraph.method277(plane, i5 * 128, j12, j6 * 128 + 128, i9 * 128 +
		 * 128, j12, k7 * 128, 4); for (int k13 = i5; k13 <= j6; k13++) { for (int i15 =
		 * k7; i15 <= i9; i15++) { anIntArrayArrayArray135[z][k13][i15] &= ~k2; } } } }
		 * } } } }
		 */
		SceneGraph.minimapUpdate = true;
	}

	public void method174(int startX, int startY, int xLen, int yLen) {
		for (int y = startY; y <= startY + yLen; y++) {
			for (int x = startX; x <= startX + xLen; x++) {
				if (x > 0 && x < width && y > 0 && y < length) {
					shading[0][x][y] = 127;

					if (x == startX && x > 0) {
						tileHeights[0][x][y] = tileHeights[0][x - 1][y];
					}

					if (x == startX + xLen && x < width - 1) {
						tileHeights[0][x][y] = tileHeights[0][x + 1][y];
					}

					if (y == startY && y > 0) {
						tileHeights[0][x][y] = tileHeights[0][x][y - 1];
					}

					if (y == startY + yLen && y < length - 1) {
						tileHeights[0][x][y] = tileHeights[0][x][y + 1];
					}
				}
			}
		}
	}

	public byte[] save_terrain_block(Chunk chunk) {
		Buffer buffer = new Buffer(new byte[131072]);
		for (int level = 0; level < 4; level++) {
			for (int x = chunk.offsetX; x < chunk.offsetX + 64; x++) {
				for (int y = chunk.offsetY; y < chunk.offsetY + 64; y++) {
					save_terrain_tile(level, x, y, buffer);
				}

			}

		}

		byte[] data = Arrays.copyOf(buffer.getPayload(), buffer.getPosition());
		return data;
	}

	private void save_terrain_tile(int level, int x, int y, Buffer buffer) {
		if (overlays[level][x][y] != 0) {
			buffer.writeShort(overlayShapes[level][x][y] * 4 + (overlayOrientations[level][x][y] & 3) + 2);
			buffer.writeShort(overlays[level][x][y] & 0x7FFF);
		}
		if (tileFlags[level][x][y] != 0) {
			buffer.writeShort(tileFlags[level][x][y] + 49);
		}
		if (underlays[level][x][y] != 0) {
			buffer.writeShort((underlays[level][x][y] & 0x7FFF) + 81);
		}
		if (manualTileHeight[level][x][y] == 1 || level == 0) {
			buffer.writeShort(1);
			if (level == 0) {
				buffer.writeByte(-tileHeights[level][x][y] / 8);
			} else {
				buffer.writeByte(-(tileHeights[level][x][y] - tileHeights[level - 1][x][y]) / 8);
			}
		} else {
			buffer.writeShort(0);
		}
	}

	public ObjectKey spawnObjectToWorld(SceneGraph scene, int id, int x, int y, int z, int type, int orientation,
                                        boolean temporary) {

		maximumPlane = Math.min(z, maximumPlane);

		// XXX System.out.println("Attempting to spawn ID " + id + " at " + new
		// Location(x, y, z).toString());
		
		int centre = tileHeights[z][x][y];
		int east = tileHeights[z][x + 1][y];
		int northEast = tileHeights[z][x + 1][y + 1];
		int north = tileHeights[z][x][y + 1];
		int mean = centre + east + northEast + north >> 2;
		ObjectDefinition definition = ObjectDefinitionLoader.lookup(id);

		/*
		 * long idTag = 1073741824 | tile_z | tile_x << 7 | object_type << 14 |
		 * object_orientation << 20; if(!object_def.hasActions) idTag |= Long.MIN_VALUE;
		 * if(!object_def.isSolidObject) idTag |= 4194304L; idTag |= (long) object_id <<
		 * 32;
		 */
		// long key = (long) (orientation << 20 | type << 14 | (y << 7 | x) + +
		// 0x40000000);

		ObjectKey objectKey = new ObjectKey(x, y, id, type, orientation, definition.isSolid(),  definition.isInteractive());
	

		if (type == 22) {

			Renderable object;
			if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
				object = definition.modelAt(22, orientation, centre, east, northEast, north, -1);
			} else {
				object = new RenderableObject(id, orientation, 22, centre, east, northEast, north,
						definition.getAnimation(), true);
			}

			GroundDecoration deco = scene.addFloorDecoration(x, y, z, object, objectKey, mean, temporary);

				if (deco != null && definition.getMinimapFunction() >= 0 && definition.getMinimapFunction() < Client.mapFunctions.length && definition.getModelIds() != null && definition.getModelIds()[0] == 111) {
					deco.setMinimapFunction(Client.mapFunctions[definition.getMinimapFunction()]);
				} else if (deco != null && definition.getAreaId() >= 0 && definition.getModelIds() != null && definition.getModelIds()[0] == 111) {
					RSArea area = RSAreaLoader.get(definition.getAreaId());
					int func = area.getSpriteId();
					deco.setMinimapFunction(Client.getSingleton().getCache().getSprite(func));
				}

		} else if (type == 10 || type == 11) {
			Renderable object;
			if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
				object = definition.modelAt(10, orientation, centre, east, northEast, north, -1);
			} else {
				object = new RenderableObject(id, orientation, 10, centre, east, northEast, north,
						definition.getAnimation(), true);
			}

			if (object != null) {
				int yaw = 0;
				if (type == 11) {
					yaw += 256;
				}

				int width;
				int length;

				if (orientation == 1 || orientation == 3) {
					width = definition.getLength();
					length = definition.getWidth();
				} else {
					width = definition.getWidth();
					length = definition.getLength();
				}

				if (scene.addObject(x, y, z, width, length, object, objectKey, yaw, mean, temporary)
						&& definition.isCastsShadow() && !temporary) {
					Mesh model;
					if (object instanceof Mesh) {
						model = (Mesh) object;
					} else {
						model = definition.modelAt(10, orientation, centre, east, northEast, north, -1);
					}

					if (model != null) {
						/*
						 * //XXX Might have to disable? if(SceneGraph.currentState.isPresent() &&
						 * SceneGraph.currentState.get().getType() == ChangeType.OBJECT_SPAWN) { for
						 * (int dx = 0; dx <= width; dx++) { for (int dy = 0; dy <= length; dy++) {
						 * ObjectState state = new ObjectState(x + dx, y + dy, z);
						 * state.backupState(chunk);
						 * ((SpawnObject)SceneGraph.currentState.get()).backupTile(state);
						 * 
						 * } } } for (int dx = 0; dx <= width; dx++) { for (int dy = 0; dy <= length;
						 * dy++) {
						 * 
						 * 
						 * int l5 = Math.max(30, model.boundingPlaneRadius / 4);
						 * 
						 * if (l5 > shading[z][x + dx][y + dy]) { shading[z][x + dx][y + dy] = (byte)
						 * l5; } } }
						 */}
				}
			} else {
				System.out.println("TYPE 10 MODEL NULL");
			}

		} else if (type >= 12) {
			Renderable object;
			if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
				object = definition.modelAt(type, orientation, centre, east, northEast, north, -1);
			} else {
				object = new RenderableObject(id, orientation, type, centre, east, northEast, north,
						definition.getAnimation(), true);
			}

			scene.addObject(x, y, z, 1, 1, object, objectKey, 0, mean, temporary);
			if (!temporary && type >= 12 && type <= 17 && type != 13 && z > 0) {
				anIntArrayArrayArray135[z][x][y] |= 0x924;
			}

		} else if (type == 0) {
			Renderable object;
			if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
				object = definition.modelAt(0, orientation, centre, east, northEast, north, -1);
			} else {
				object = new RenderableObject(id, orientation, 0, centre, east, northEast, north,
						definition.getAnimation(), true);
			}
			scene.addWall(objectKey, x, y, z, anIntArray152[orientation], object, null, mean, 0, temporary);
			if (!temporary)
				if (orientation == 0) {
					if (definition.isCastsShadow()) {
						shading[z][x][y] = 50;
						shading[z][x][y + 1] = 50;
					}

					if (definition.occludes()) {
						anIntArrayArrayArray135[z][x][y] |= 0x249;
					}
				} else if (orientation == 1) {
					if (definition.isCastsShadow()) {
						shading[z][x][y + 1] = 50;
						shading[z][x + 1][y + 1] = 50;
					}

					if (definition.occludes()) {
						anIntArrayArrayArray135[z][x][y + 1] |= 0x492;
					}
				} else if (orientation == 2) {
					if (definition.isCastsShadow()) {
						shading[z][x + 1][y] = 50;
						shading[z][x + 1][y + 1] = 50;
					}

					if (definition.occludes()) {
						anIntArrayArrayArray135[z][x + 1][y] |= 0x249;
					}
				} else if (orientation == 3) {
					if (definition.isCastsShadow()) {
						shading[z][x][y] = 50;
						shading[z][x + 1][y] = 50;
					}

					if (definition.occludes()) {
						anIntArrayArrayArray135[z][x][y] |= 0x492;
					}
				}

			if (!temporary && definition.getDecorDisplacement() != 16) {
				scene.displaceWallDecor(x, y, z, definition.getDecorDisplacement());
			}
		} else if (type == 1) {
			Renderable object;
			if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
				object = definition.modelAt(1, orientation, centre, east, northEast, north, -1);
			} else {
				object = new RenderableObject(id, orientation, 1, centre, east, northEast, north,
						definition.getAnimation(), true);
			}

			scene.addWall(objectKey, x, y, z, anIntArray140[orientation], object, null, mean, 0, temporary);
			if (definition.isCastsShadow() && !temporary) {
				if (orientation == 0) {
					shading[z][x][y + 1] = 50;
				} else if (orientation == 1) {
					shading[z][x + 1][y + 1] = 50;
				} else if (orientation == 2) {
					shading[z][x + 1][y] = 50;
				} else if (orientation == 3) {
					shading[z][x][y] = 50;
				}
			}

		} else if (type == 2) {
			int oppositeOrientation = orientation + 1 & 3;
			Renderable obj11;
			Renderable obj12;
			if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
				obj11 = definition.modelAt(2, 4 + orientation, centre, east, northEast, north, -1);
				obj12 = definition.modelAt(2, oppositeOrientation, centre, east, northEast, north, -1);
			} else {
				obj11 = new RenderableObject(id, 4 + orientation, 2, centre, east, northEast, north,
						definition.getAnimation(), true);
				obj12 = new RenderableObject(id, oppositeOrientation, 2, centre, east, northEast, north,
						definition.getAnimation(), true);
			}
			scene.addWall(objectKey, x, y, z, anIntArray152[orientation], obj11, obj12, mean,
					anIntArray152[oppositeOrientation], temporary);
			if (!temporary && definition.occludes()) {
				if (orientation == 0) {
					anIntArrayArrayArray135[z][x][y] |= 0x249;
					anIntArrayArrayArray135[z][x][y + 1] |= 0x492;
				} else if (orientation == 1) {
					anIntArrayArrayArray135[z][x][y + 1] |= 0x492;
					anIntArrayArrayArray135[z][x + 1][y] |= 0x249;
				} else if (orientation == 2) {
					anIntArrayArrayArray135[z][x + 1][y] |= 0x249;
					anIntArrayArrayArray135[z][x][y] |= 0x492;
				} else if (orientation == 3) {
					anIntArrayArrayArray135[z][x][y] |= 0x492;
					anIntArrayArrayArray135[z][x][y] |= 0x249;
				}
			}

			if (!temporary && definition.getDecorDisplacement() != 16) {// TODO
				scene.displaceWallDecor(x, y, z, definition.getDecorDisplacement());
			}
		} else if (type == 3) {
			Renderable object;
			if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
				object = definition.modelAt(3, orientation, centre, east, northEast, north, -1);
			} else {
				object = new RenderableObject(id, orientation, 3, centre, east, northEast, north,
						definition.getAnimation(), true);
			}

			scene.addWall(objectKey, x, y, z, anIntArray140[orientation], object, null, mean, 0, temporary);
			if (!temporary && definition.isCastsShadow()) {
				if (orientation == 0) {
					shading[z][x][y + 1] = 50;
				} else if (orientation == 1) {
					shading[z][x + 1][y + 1] = 50;
				} else if (orientation == 2) {
					shading[z][x + 1][y] = 50;
				} else if (orientation == 3) {
					shading[z][x][y] = 50;
				}
			}

		} else if (type == 9) {
			Renderable object;
			if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
				object = definition.modelAt(type, orientation, centre, east, northEast, north, -1);
			} else {
				object = new RenderableObject(id, orientation, type, centre, east, northEast, north,
						definition.getAnimation(), true);
			}

			scene.addObject(x, y, z, 1, 1, object, objectKey, 0, mean, temporary);

		} else {
			if (definition.isContouredGround()) {
				if (orientation == 1) {
					int tmp = north;
					north = northEast;
					northEast = east;
					east = centre;
					centre = tmp;
				} else if (orientation == 2) {
					int tmp = north;
					north = east;
					east = tmp;
					tmp = northEast;
					northEast = centre;
					centre = tmp;
				} else if (orientation == 3) {
					int tmp = north;
					north = centre;
					centre = east;
					east = northEast;
					northEast = tmp;
				}
			}

			if (type == 4) {
				Renderable object;
				if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
					object = definition.modelAt(4, 0, centre, east, northEast, north, -1);
				} else {
					object = new RenderableObject(id, 0, 4, centre, east, northEast, north, definition.getAnimation(),
							true);
				}
				scene.addWallDecoration(objectKey, y, orientation * 512, z, 0, mean, object, x, 0, anIntArray152[orientation],
						temporary);
			} else if (type == 5) {
				int displacement = 16;
				ObjectKey existing = scene.getWallKey(x, y, z);
				if (existing != null) {
					int existingId = objectKey.getId();
					displacement = ObjectDefinitionLoader.lookup(existingId).getDecorDisplacement();
				}

				Renderable object;
				if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
					object = definition.modelAt(4, 0, centre, east, northEast, north, -1);
				} else {
					object = new RenderableObject(id, 0, 4, centre, east, northEast, north, definition.getAnimation(),
							true);
				}

				scene.addWallDecoration(objectKey, y, orientation * 512, z, COSINE_VERTICES[orientation] * displacement, mean,
						object, x, SINE_VERTICIES[orientation] * displacement, anIntArray152[orientation], temporary);
			} else if (type == 6) {
				Renderable object;
				if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
					object = definition.modelAt(4, 0, centre, east, northEast, north, -1);
				} else {
					object = new RenderableObject(id, 0, 4, centre, east, northEast, north, definition.getAnimation(),
							true);
				}

				scene.addWallDecoration(objectKey, y, orientation, z, 0, mean, object, x, 0, 256, temporary);
			} else if (type == 7) {
				Renderable object;
				if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
					object = definition.modelAt(4, 0, centre, east, northEast, north, -1);
				} else {
					object = new RenderableObject(id, 0, 4, centre, east, northEast, north, definition.getAnimation(),
							true);
				}
				scene.addWallDecoration(objectKey, y, orientation, z, 0, mean, object, x, 0, 512, temporary);
			} else if (type == 8) {
				Renderable object;
				if (definition.getAnimation() == -1 && definition.getMorphisms() == null) {
					object = definition.modelAt(4, 0, centre, east, northEast, north, -1);
				} else {
					object = new RenderableObject(id, 0, 4, centre, east, northEast, north, definition.getAnimation(),
							true);
				}
				scene.addWallDecoration(objectKey, y, orientation, z, 0, mean, object, x, 0, 768, temporary);
			}
		}
		return objectKey;
	}

	long lastUpdate = 0;
	public void updateTiles() {
		//synchronized (this) {
			if(System.currentTimeMillis() - lastUpdate < 200)
				return;
			
			lastUpdate = System.currentTimeMillis();

			boolean showBlending = !Options.disableBlending.get();
			boolean hideOverlays = !Options.showOverlay.get();

			for (int z = 0; z < 4; z++) {
				byte[][] shading = this.shading[z];
				byte byte0 = 96;
				char diffusion = '\u0300';
				byte lightX = -50;
				byte lightY = -10;
				byte lightZ = -50;

				int light = diffusion * (int) Math.sqrt(lightX * lightX + lightY * lightY + lightZ * lightZ) >> 8;
				for (int y = 1; y < length; y++) {
					for (int x = 1; x < width; x++) {
						int dhWidth = tileHeights[z][x + 1][y] - tileHeights[z][x - 1][y];
						int dhLength = tileHeights[z][x][y + 1] - tileHeights[z][x][y - 1];

						int distance = (int) Math.sqrt(dhWidth * dhWidth + 0x10000 + dhLength * dhLength);
						if (distance == 0) {
							distance = 1;
						}
						int dx = (dhWidth << 8) / distance;
						int dy = 0x10000 / distance;
						int dz = (dhLength << 8) / distance;
						int lightness = byte0 + (lightX * dx + lightY * dy + lightZ * dz) / light;
						int offset = (shading[x - 1][y] >> 2) + (shading[x + 1][y] >> 3) + (shading[x][y - 1] >> 2)
								+ (shading[x][y + 1] >> 3) + (shading[x][y] >> 1);
						tileLighting[x][y] = lightness - offset;
					}
				}

				for (int index = 0; index < length; index++) {
					hues[index] = 0;
					saturations[index] = 0;
					luminances[index] = 0;
					chromas[index] = 0;
					anIntArray128[index] = 0;
				}

				for (int centreX = -5; centreX < width + 5; centreX++) {
					for (int y = 0; y < length; y++) {
						int maxX = centreX + 5;
						if (maxX >= 0 && maxX < width) {
							int id = underlays[z][maxX][y] & 0x7FFF;

							if (id > 0) {
								Floor floor = FloorDefinitionLoader.getUnderlay(id - 1);
								if(floor == null)
									floor = FloorDefinitionLoader.getUnderlay(0);
								hues[y] += floor.getWeightedHue();
								saturations[y] += floor.getSaturation();
								luminances[y] += floor.getLuminance();
								chromas[y] += floor.getChroma();
								anIntArray128[y]++;
							}
						}

						int minX = centreX - 5;
						if (minX >= 0 && minX < width) {
							int id = underlays[z][minX][y] & 0x7FFF;

							if (id > 0) {
								Floor floor = FloorDefinitionLoader.getUnderlay(id - 1);
								if(floor == null)
									floor = FloorDefinitionLoader.getUnderlay(0);
								hues[y] -= floor.getWeightedHue();
								saturations[y] -= floor.getSaturation();
								luminances[y] -= floor.getLuminance();
								chromas[y] -= floor.getChroma();
								anIntArray128[y]--;
							}
						}
					}

					if (centreX >= 0 && centreX < width) {
						int blended_anIntArray124 = 0;
						int blended_anIntArray125 = 0;
						int blended_anIntArray126 = 0;
						int blended_anIntArray124_divisor = 0;
						int blend_direction_tracker = 0;

						for (int centreY = -5; centreY < length + 5; centreY++) {
							int j18 = centreY + 5;
							if (j18 >= 0 && j18 < length) {
								blended_anIntArray124 += hues[j18];
								blended_anIntArray125 += saturations[j18];
								blended_anIntArray126 += luminances[j18];
								blended_anIntArray124_divisor += chromas[j18];
								blend_direction_tracker += anIntArray128[j18];
							}

							int k18 = centreY - 5;
							if (k18 >= 0 && k18 < length) {
								blended_anIntArray124 -= hues[k18];
								blended_anIntArray125 -= saturations[k18];
								blended_anIntArray126 -= luminances[k18];
								blended_anIntArray124_divisor -= chromas[k18];
								blend_direction_tracker -= anIntArray128[k18];
							}

							if (centreY >= 0
									&& centreY < length/*
														 * && (!lowMemory || (tileFlags[0][centreX][centreY] &
														 * BRIDGE_TILE) != 0 || (tileFlags[z][centreX][centreY] &
														 * DISABLE_RENDERING) == 0 && getCollisionPlane(centreX,
														 * centreY, z) == currentPlane)
														 */) {
								if (z < maximumPlane) {
									maximumPlane = z;
								}

								int underlay = underlays[z][centreX][centreY] & 0x7FFF;
								int overlayFloorId = overlays[z][centreX][centreY] & 0x7FFF;

								/*
								 * boolean hiddenHL = showHiddenTiles && z == Options.currentHeight.get(); if
								 * (underlay == 0 && overlayFloorId == 0 && (showHiddenTiles && !hiddenHL ||
								 * !showHiddenTiles)) { if (scene.tiles[z][centreX][centreY] != null) {
								 * 
								 * int finalZ = z; int finalX = centreX; int finalY = centreY;
								 * SceneGraph.onCycleEnd.add(sceneGraph -> {
								 * sceneGraph.tiles[finalZ][finalX][finalY].simple = null;
								 * 
								 * }); } }
								 */

								if (underlay > 0 || overlayFloorId > 0 /*|| hiddenHL*/) {
									int centreHeight = tileHeights[z][centreX][centreY];
									int eastHeight = tileHeights[z][centreX + 1][centreY];
									int northEastHeight = tileHeights[z][centreX + 1][centreY + 1];
									int northHeight = tileHeights[z][centreX][centreY + 1];
									int centreLight = tileLighting[centreX][centreY];
									int eastLight = tileLighting[centreX + 1][centreY];
									int northEastLight = tileLighting[centreX + 1][centreY + 1];
									int northLight = tileLighting[centreX][centreY + 1];
									int hsl_bitset_unmodified = -1;
									int hsl_bitset_randomized = -1;

									/*
									 * if (underlay > 0) { int hue = l9 * 256 / k15; int saturation = j13 / k16; int
									 * luminance = j14 / k16; underlayColour = ColourUtils.toHsl(hue, saturation,
									 * luminance); hue = hue + hueOffset & 0xff; luminance += luminanceOffset;
									 * 
									 * if (luminance < 0) { luminance = 0; } else if (luminance > 255) { luminance =
									 * 255; }
									 * 
									 * adjustedColour = ColourUtils.toHsl(hue, saturation, luminance); }
									 */

									if (underlay > 0 || overlayFloorId != 0) {
										int anIntArray124 = -1;
										int sat = 0;
										int lum = 0;
										if (underlay == 0) {
											anIntArray124 = -1;
											sat = 0;
											lum = 0;
										} else if (underlay > 0) {
											if (showBlending) {
												if (blended_anIntArray124_divisor < 1) {
													blended_anIntArray124_divisor = 1;
												}

												anIntArray124 = (blended_anIntArray124 << 8)
														/ blended_anIntArray124_divisor;
												sat = blended_anIntArray125 / blend_direction_tracker;
												lum = blended_anIntArray126 / blend_direction_tracker;
												hsl_bitset_unmodified = ColourUtils.toHsl(anIntArray124, sat, lum);
												// anIntArray124 = anIntArray124 + anIntArray124Offset & 0xff;
												// lum += offsetLightning;
												if (lum < 0) {
													lum = 0;
												} else if (lum > 255) {
													lum = 255;
												}
											} else {
												Floor floor = FloorDefinitionLoader.getUnderlay(underlay - 1);
												if(floor == null)
													floor = FloorDefinitionLoader.getUnderlay(0);
												int hue = floor.getHue();
												int saturation = floor.getSaturation();
												int luminance = floor.getLuminance();
												hsl_bitset_unmodified = ColourUtils.toHsl(hue, saturation, luminance);
												hue = hue + hueOffset & 0xff;
												luminance += luminanceOffset;

												if (luminance < 0) {
													luminance = 0;
												} else if (luminance > 255) {
													luminance = 255;
												}

												hsl_bitset_randomized = ColourUtils.toHsl(hue, saturation, luminance);
											}
											/*
											 * } else if(underlay == 0 && overlayFloorId == 0 && hiddenHL){
											 * 
											 * int hue = 120; int saturation = 128; int luminance = 128;
											 * hsl_bitset_unmodified = ColourUtils.toHsl(hue, saturation, luminance);
											 * hue = hue + hueOffset & 0xff; luminance += luminanceOffset;
											 * 
											 * if (luminance < 0) { luminance = 0; } else if (luminance > 255) {
											 * luminance = 255; }
											 * 
											 * hsl_bitset_randomized = ColourUtils.toHsl(hue, saturation, luminance);
											 */
										} else {
											anIntArray124 = underlay;
											sat = 0;
											lum = 0;
										}
										if (anIntArray124 != -1 && hsl_bitset_randomized == -1) {
											hsl_bitset_randomized = ColourUtils.toHsl(anIntArray124, sat, lum);
										}

										if (hsl_bitset_unmodified == -1) {
											hsl_bitset_unmodified = hsl_bitset_randomized;
										}

									}

									if (z > 0) {
										boolean flag = underlay != 0 || overlayShapes[z][centreX][centreY] == 0;

                                        if (overlayFloorId > 0
												&& !FloorDefinitionLoader.getOverlay(overlayFloorId - 1).isShadowed()) {
											flag = false;
										}

										if (/* hiddenHL || */flag && centreHeight == eastHeight
												&& centreHeight == northEastHeight && centreHeight == northHeight) {
											anIntArrayArrayArray135[z][centreX][centreY] |= 0x924;
										}
									}

									int rgb_bitset_randomized = 0;
									if (hsl_bitset_unmodified != -1) {
										try {// XXX Fix this
											rgb_bitset_randomized = GameRasterizer.getInstance().colourPalette[light(
													hsl_bitset_randomized, 96)];
										} catch (Exception ex) {

											System.out.println("ERROR WITH " + overlayFloorId + " : " + underlay
													+ " at " + centreX + ":" + centreY + ":" + z);
										}
									}

									if (overlayFloorId == 0 || hideOverlays) {
										byte flag = tileFlags[z][centreX][centreY];
										/*
										 * if(underlay == 0 && overlayFloorId == 0 && hiddenHL) { flag |= 64; }
										 */
										if (Options.hdTextures.get()) {
											if (underlay - 1 >= FloorDefinitionLoader.getUnderlayCount()) {
												underlay = FloorDefinitionLoader.getUnderlayCount();
											}
											Floor floor = FloorDefinitionLoader.getUnderlay(underlay - 1);
											int underlay_texture_id = floor.getTexture();
											if (underlay_texture_id != -1) {
												underlay_texture_id = 154; // 632, 154
											}
											underlay_floor_texture = underlay_texture_id;
											underlay_floor_map_color = ColourUtils.checkedLight(hsl_bitset_unmodified,
													96);
											int tile_opcode = overlayShapes[z][centreX][centreY] + 1;

											byte tile_orientation = overlayShapes[z][centreX][centreY];
											/**
											 * Adds underlay tile
											 */
											int overlay_hsl = ColourUtils.toHsl(floor.getHue(), floor.getSaturation(),
													floor.getLuminance());

											scene.addTile(z, centreX, centreY, tile_opcode, tile_orientation,
													underlay_texture_id, centreHeight, eastHeight, northEastHeight,
													northHeight, light(hsl_bitset_unmodified, centreLight),
													light(hsl_bitset_unmodified, eastLight),
													light(hsl_bitset_unmodified, northEastLight),
													light(hsl_bitset_unmodified, northLight),
													getOverlayShadow(overlay_hsl, centreLight),
													getOverlayShadow(overlay_hsl, eastLight),
													getOverlayShadow(overlay_hsl, northEastLight),
													getOverlayShadow(overlay_hsl, northLight), rgb_bitset_randomized,
													rgb_bitset_randomized, underlay_floor_map_color,
													underlay_floor_texture, underlay_floor_map_color, false, flag, underlay - 1, overlayFloorId - 1);
										} else {
											scene.addTile(z, centreX, centreY, 0, 0, -1, centreHeight, eastHeight,
													northEastHeight, northHeight,
													light(hsl_bitset_unmodified, centreLight),
													light(hsl_bitset_unmodified, eastLight),
													light(hsl_bitset_unmodified, northEastLight),
													light(hsl_bitset_unmodified, northLight), 0, 0, 0, 0,
													rgb_bitset_randomized, rgb_bitset_randomized, -1, 0, 0, true, flag, underlay - 1, overlayFloorId - 1);
										}

									} else {
										int tileType = overlayShapes[z][centreX][centreY] + 1;
										byte orientation = overlayOrientations[z][centreX][centreY];

										Floor overlayFloor = FloorDefinitionLoader.getOverlay(overlayFloorId - 1);
										int overlayTextureId = overlayFloor.getTexture();

										int overlayTextureColour = -1;
										int overlayMapColour = 0;

										int overlayHslColour;
										int overlayRgbColour;

										if (overlayTextureId > TextureLoader.instance.count()) {
											overlayTextureId = -1;
										}
										if (overlayTextureId >= 0
												&& TextureLoader.getTexture(overlayTextureId) == null) {
											overlayTextureId = -1;
										}
										if (overlayTextureId >= 0) {
											overlayRgbColour = TextureLoader.getTexture(overlayTextureId)
													.averageTextureColour();
											overlayHslColour = -1;
										} else if (overlayFloor.getRgb() == 0xff00ff) { // transparent
											overlayRgbColour = 0;
											overlayHslColour = -2;
											overlayTextureId = -1;
										} else if (overlayFloor.getRgb() == 0x333333) { // transparent
											overlayRgbColour = GameRasterizer.getInstance().colourPalette[ColourUtils
													.checkedLight(overlayFloor.getColour(), 96)];
											overlayHslColour = -2;
											overlayTextureId = -1;
										} else {
											overlayHslColour = ColourUtils.toHsl(overlayFloor.getHue(),
													overlayFloor.getSaturation(), overlayFloor.getLuminance());
											overlayRgbColour = GameRasterizer.getInstance().colourPalette[ColourUtils
													.checkedLight(overlayFloor.getColour(), 96)];
										}

										/*
										 * if (overlayRgbColour == 0x000000 && floor.getAnotherRgb() != -1) {
										 * 
										 * int newOverlayColour = ColourUtils.toHsl(floor.getAnotherHue(),
										 * floor.getAnotherSaturation(), floor.getAnotherLuminance()); overlayRgbColour
										 * = GameRasterizer.getInstance().colourPalette[ColourUtils.checkedLight(
										 * newOverlayColour, 96)];
										 * 
										 * }
										 */


										if (Options.hdTextures.get()) {

											if (overlayFloor.getAnotherRgb() != -1) {
												overlayMapColour = (GameRasterizer
														.getInstance().colourPalette[overlayFloor.getAnotherRgb()] != 1)
																? GameRasterizer
																		.getInstance().colourPalette[overlayFloor
																				.getAnotherRgb()]
																: 0;
											}
											if ((overlayTextureId >= 0)) {
												overlayHslColour = -1;
												if (overlayFloor.getRgb() != 0xff00ff) {
													overlayHslColour = overlayFloor.getRgb();
													if (overlayTextureId > TextureLoader.instance.count()) {
														overlayRgbColour = (overlayHslColour != -1
																? GameRasterizer
																		.getInstance().colourPalette[overlayHslColour]
																: 0);
													}
													overlayTextureColour = getOverlayShadow(overlayFloor.getRgb(), 96);
												} else {
													if (overlayTextureId > TextureLoader.instance.count()) {
														overlayRgbColour = overlayFloor.getAnotherRgb();
													}
													overlayHslColour = -2;
													underlay_floor_map_color = -1;
													overlayTextureColour = -1;
												}
											} else if (overlayFloor.getRgb() == -1) {
												if (overlayTextureId > TextureLoader.instance.count()) {
													overlayRgbColour = overlayMapColour;
												}
												overlayHslColour = -2;
												// ?
												if (z > 0) {
													underlay_floor_texture = -1;
												}

												overlayTextureId = -1;
											} else {
												overlayTextureColour = getOverlayShadow(overlayFloor.getRgb(), 96);
												overlayHslColour = overlayFloor.getRgb();
												if (overlayTextureId > TextureLoader.instance.count()) {
													overlayRgbColour = GameRasterizer
															.getInstance().colourPalette[overlayTextureColour];
												}
											}
										}

										if (Options.hdTextures.get()) {
											byte flag = tileFlags[z][centreX][centreY];
											scene.addTile(z, centreX, centreY, tileType, orientation, overlayTextureId,
													centreHeight, eastHeight, northEastHeight, northHeight,
													light(hsl_bitset_unmodified, centreLight),
													light(hsl_bitset_unmodified, eastLight),
													light(hsl_bitset_unmodified, northEastLight),
													light(hsl_bitset_unmodified, northLight),
													getOverlayShadow(overlayHslColour, centreLight),
													getOverlayShadow(overlayHslColour, eastLight),
													getOverlayShadow(overlayHslColour, northEastLight),
													getOverlayShadow(overlayHslColour, northLight),
													rgb_bitset_randomized, overlayRgbColour, overlayTextureColour,
													underlay_floor_texture, underlay_floor_map_color, false, flag, underlay - 1, overlayFloorId - 1);
										} else {
											byte flag = tileFlags[z][centreX][centreY];
											scene.addTile(z, centreX, centreY, tileType, orientation, overlayTextureId,
													centreHeight, eastHeight, northEastHeight, northHeight,
													light(hsl_bitset_unmodified, centreLight),
													light(hsl_bitset_unmodified, eastLight),
													light(hsl_bitset_unmodified, northEastLight),
													light(hsl_bitset_unmodified, northLight),
													getOverlayShadow(overlayHslColour, centreLight),
													getOverlayShadow(overlayHslColour, eastLight),
													getOverlayShadow(overlayHslColour, northEastLight),
													getOverlayShadow(overlayHslColour, northLight),
													rgb_bitset_randomized, overlayRgbColour, -1, 0, 0, true, flag, underlay - 1, overlayFloorId - 1);
										}

									}
								} else {
									scene.getTile(z, centreX, centreY).simple = null;
									scene.getTile(z, centreX, centreY).shape = null;
								}
							}
						}
					}
				}

			}

		//}

		SceneGraph.minimapUpdate = true;
	}
	

	public void updateLocalizedTiles(Chunk chunk) {
		//synchronized (this) {

			int width = chunk.offsetX + 64;
			int length = chunk.offsetY + 64;
			boolean showBlending = !Options.disableBlending.get();
			boolean hideOverlays = !Options.showOverlay.get();

			for (int z = 0; z < 4; z++) {
				byte[][] shading = this.shading[z];
				byte byte0 = 96;
				char diffusion = '\u0300';
				byte lightX = -50;
				byte lightY = -10;
				byte lightZ = -50;

				int light = diffusion * (int) Math.sqrt(lightX * lightX + lightY * lightY + lightZ * lightZ) >> 8;
				for (int y = chunk.offsetY + 1; y < length; y++) {
					for (int x = chunk.offsetX + 1; x < width; x++) {
						int dhWidth = tileHeights[z][x + 1][y] - tileHeights[z][x - 1][y];
						int dhLength = tileHeights[z][x][y + 1] - tileHeights[z][x][y - 1];

						int distance = (int) Math.sqrt(dhWidth * dhWidth + 0x10000 + dhLength * dhLength);
						if (distance == 0) {
							distance = 1;
						}
						int dx = (dhWidth << 8) / distance;
						int dy = 0x10000 / distance;
						int dz = (dhLength << 8) / distance;
						int lightness = byte0 + (lightX * dx + lightY * dy + lightZ * dz) / light;
						int offset = (shading[x - 1][y] >> 2) + (shading[x + 1][y] >> 3) + (shading[x][y - 1] >> 2)
								+ (shading[x][y + 1] >> 3) + (shading[x][y] >> 1);
						tileLighting[x][y] = lightness - offset;
					}
				}

				for (int index = chunk.offsetY; index < length; index++) {
					hues[index] = 0;
					saturations[index] = 0;
					luminances[index] = 0;
					chromas[index] = 0;
					anIntArray128[index] = 0;
				}

				for (int centreX = chunk.offsetX -5; centreX < width + 5; centreX++) {
					for (int y = 0; y < length; y++) {
						int maxX = centreX + 5;
						if (maxX >= 0 && maxX < width) {
							int id = underlays[z][maxX][y] & 0x7FFF;

							if (id > 0) {
								Floor floor = FloorDefinitionLoader.getUnderlay(id - 1);
								hues[y] += floor.getWeightedHue();
								saturations[y] += floor.getSaturation();
								luminances[y] += floor.getLuminance();
								chromas[y] += floor.getChroma();
								anIntArray128[y]++;
							}
						}

						int minX = centreX - 5;
						if (minX >= 0 && minX < width) {
							int id = underlays[z][minX][y] & 0x7FFF;

							if (id > 0) {
								Floor floor = FloorDefinitionLoader.getUnderlay(id - 1);
								hues[y] -= floor.getWeightedHue();
								saturations[y] -= floor.getSaturation();
								luminances[y] -= floor.getLuminance();
								chromas[y] -= floor.getChroma();
								anIntArray128[y]--;
							}
						}
					}

					if (centreX >= 0 && centreX < width) {
						int blended_anIntArray124 = 0;
						int blended_anIntArray125 = 0;
						int blended_anIntArray126 = 0;
						int blended_anIntArray124_divisor = 0;
						int blend_direction_tracker = 0;

						for (int centreY = chunk.offsetY -5; centreY < length + 5; centreY++) {
							int j18 = centreY + 5;
							if (j18 >= 0 && j18 < length) {
								blended_anIntArray124 += hues[j18];
								blended_anIntArray125 += saturations[j18];
								blended_anIntArray126 += luminances[j18];
								blended_anIntArray124_divisor += chromas[j18];
								blend_direction_tracker += anIntArray128[j18];
							}

							int k18 = centreY - 5;
							if (k18 >= 0 && k18 < length) {
								blended_anIntArray124 -= hues[k18];
								blended_anIntArray125 -= saturations[k18];
								blended_anIntArray126 -= luminances[k18];
								blended_anIntArray124_divisor -= chromas[k18];
								blend_direction_tracker -= anIntArray128[k18];
							}

							if (centreY >= 0 && centreY < length) {
								if (z < maximumPlane) {
									maximumPlane = z;
								}

								int underlay = underlays[z][centreX][centreY] & 0x7FFF;
								int overlayFloorId = overlays[z][centreX][centreY] & 0x7FFF;

								if (underlay > 0 || overlayFloorId > 0) {
									int centreHeight = tileHeights[z][centreX][centreY];
									int eastHeight = tileHeights[z][centreX + 1][centreY];
									int northEastHeight = tileHeights[z][centreX + 1][centreY + 1];
									int northHeight = tileHeights[z][centreX][centreY + 1];
									int centreLight = tileLighting[centreX][centreY];
									int eastLight = tileLighting[centreX + 1][centreY];
									int northEastLight = tileLighting[centreX + 1][centreY + 1];
									int northLight = tileLighting[centreX][centreY + 1];
									int hsl_bitset_unmodified = -1;
									int hsl_bitset_randomized = -1;

									if (underlay > 0 || overlayFloorId != 0) {
										int anIntArray124 = -1;
										int sat = 0;
										int lum = 0;
										if (underlay == 0) {
											anIntArray124 = -1;
											sat = 0;
											lum = 0;
										} else if (underlay > 0) {
											if (showBlending) {
												if (blended_anIntArray124_divisor < 1) {
													blended_anIntArray124_divisor = 1;
												}

												anIntArray124 = (blended_anIntArray124 << 8)
														/ blended_anIntArray124_divisor;
												sat = blended_anIntArray125 / blend_direction_tracker;
												lum = blended_anIntArray126 / blend_direction_tracker;
												hsl_bitset_unmodified = ColourUtils.toHsl(anIntArray124, sat, lum);
												// anIntArray124 = anIntArray124 + anIntArray124Offset & 0xff;
												// lum += offsetLightning;
												if (lum < 0) {
													lum = 0;
												} else if (lum > 255) {
													lum = 255;
												}
											} else {
												Floor floor = FloorDefinitionLoader.getUnderlay(underlay - 1);
												int hue = floor.getHue();
												int saturation = floor.getSaturation();
												int luminance = floor.getLuminance();
												hsl_bitset_unmodified = ColourUtils.toHsl(hue, saturation, luminance);
												hue = hue + hueOffset & 0xff;
												luminance += luminanceOffset;

												if (luminance < 0) {
													luminance = 0;
												} else if (luminance > 255) {
													luminance = 255;
												}

												hsl_bitset_randomized = ColourUtils.toHsl(hue, saturation, luminance);
											}
											
										} else {
											anIntArray124 = underlay;
											sat = 0;
											lum = 0;
										}
										if (anIntArray124 != -1 && hsl_bitset_randomized == -1) {
											hsl_bitset_randomized = ColourUtils.toHsl(anIntArray124, sat, lum);
										}

										if (hsl_bitset_unmodified == -1) {
											hsl_bitset_unmodified = hsl_bitset_randomized;
										}

									}

									if (z > 0) {
										boolean flag = underlay != 0 || overlayShapes[z][centreX][centreY] == 0;

                                        if (overlayFloorId > 0
												&& !FloorDefinitionLoader.getOverlay(overlayFloorId - 1).isShadowed()) {
											flag = false;
										}

										if (/* hiddenHL || */flag && centreHeight == eastHeight
												&& centreHeight == northEastHeight && centreHeight == northHeight) {
											anIntArrayArrayArray135[z][centreX][centreY] |= 0x924;
										}
									}

									int rgb_bitset_randomized = 0;
									if (hsl_bitset_unmodified != -1) {
										try {// XXX Fix this
											rgb_bitset_randomized = GameRasterizer.getInstance().colourPalette[light(
													hsl_bitset_randomized, 96)];
										} catch (Exception ex) {

											System.out.println("ERROR WITH " + overlayFloorId + " : " + underlay
													+ " at " + centreX + ":" + centreY + ":" + z);
										}
									}

									if (overlayFloorId == 0 || hideOverlays) {
										byte flag = tileFlags[z][centreX][centreY];
										/*
										 * if(underlay == 0 && overlayFloorId == 0 && hiddenHL) { flag |= 64; }
										 */
										if (Options.hdTextures.get()) {
											if (underlay - 1 >= FloorDefinitionLoader.getUnderlayCount()) {
												underlay = FloorDefinitionLoader.getUnderlayCount();
											}
											Floor floor = FloorDefinitionLoader.getUnderlay(underlay - 1);
											int underlay_texture_id = floor.getTexture();
											if (underlay_texture_id != -1) {
												underlay_texture_id = 154; // 632, 154
											}
											underlay_floor_texture = underlay_texture_id;
											underlay_floor_map_color = ColourUtils.checkedLight(hsl_bitset_unmodified,
													96);
											int tile_opcode = overlayShapes[z][centreX][centreY] + 1;

											byte tile_orientation = overlayShapes[z][centreX][centreY];
											/**
											 * Adds underlay tile
											 */
											int overlay_hsl = ColourUtils.toHsl(floor.getHue(), floor.getSaturation(),
													floor.getLuminance());

											scene.addTile(z, centreX, centreY, tile_opcode, tile_orientation,
													underlay_texture_id, centreHeight, eastHeight, northEastHeight,
													northHeight, light(hsl_bitset_unmodified, centreLight),
													light(hsl_bitset_unmodified, eastLight),
													light(hsl_bitset_unmodified, northEastLight),
													light(hsl_bitset_unmodified, northLight),
													getOverlayShadow(overlay_hsl, centreLight),
													getOverlayShadow(overlay_hsl, eastLight),
													getOverlayShadow(overlay_hsl, northEastLight),
													getOverlayShadow(overlay_hsl, northLight), rgb_bitset_randomized,
													rgb_bitset_randomized, underlay_floor_map_color,
													underlay_floor_texture, underlay_floor_map_color, false, flag, underlay - 1, overlayFloorId - 1);
										} else {
											scene.addTile(z, centreX, centreY, 0, 0, -1, centreHeight, eastHeight,
													northEastHeight, northHeight,
													light(hsl_bitset_unmodified, centreLight),
													light(hsl_bitset_unmodified, eastLight),
													light(hsl_bitset_unmodified, northEastLight),
													light(hsl_bitset_unmodified, northLight), 0, 0, 0, 0,
													rgb_bitset_randomized, rgb_bitset_randomized, -1, 0, 0, true, flag, underlay - 1, overlayFloorId - 1);
										}

									} else {
										int tileType = overlayShapes[z][centreX][centreY] + 1;
										byte orientation = overlayOrientations[z][centreX][centreY];

										Floor overlayFloor = FloorDefinitionLoader.getOverlay(overlayFloorId - 1);
										int overlayTextureId = overlayFloor.getTexture();

										int overlayTextureColour = -1;
										int overlayMapColour = 0;

										int overlayHslColour;
										int overlayRgbColour;

										if (overlayTextureId > TextureLoader.instance.count()) {
											overlayTextureId = -1;
										}
										if (overlayTextureId >= 0
												&& TextureLoader.getTexture(overlayTextureId) == null) {
											overlayTextureId = -1;
										}
										if (overlayTextureId >= 0) {
											overlayRgbColour = TextureLoader.getTexture(overlayTextureId)
													.averageTextureColour();
											overlayHslColour = -1;
										} else if (overlayFloor.getRgb() == 0xff00ff) { // transparent
											overlayRgbColour = 0;
											overlayHslColour = -2;
											overlayTextureId = -1;
										} else if (overlayFloor.getRgb() == 0x333333) { // transparent
											overlayRgbColour = GameRasterizer.getInstance().colourPalette[ColourUtils
													.checkedLight(overlayFloor.getColour(), 96)];
											overlayHslColour = -2;
											overlayTextureId = -1;
										} else {
											overlayHslColour = ColourUtils.toHsl(overlayFloor.getHue(),
													overlayFloor.getSaturation(), overlayFloor.getLuminance());
											overlayRgbColour = GameRasterizer.getInstance().colourPalette[ColourUtils
													.checkedLight(overlayFloor.getColour(), 96)];
										}


										if (Options.hdTextures.get()) {

											if (overlayFloor.getAnotherRgb() != -1) {
												overlayMapColour = (GameRasterizer
														.getInstance().colourPalette[overlayFloor.getAnotherRgb()] != 1)
																? GameRasterizer
																		.getInstance().colourPalette[overlayFloor
																				.getAnotherRgb()]
																: 0;
											}
											if ((overlayTextureId >= 0)) {
												overlayHslColour = -1;
												if (overlayFloor.getRgb() != 0xff00ff) {
													overlayHslColour = overlayFloor.getRgb();
													if (overlayTextureId > TextureLoader.instance.count()) {
														overlayRgbColour = (overlayHslColour != -1
																? GameRasterizer
																		.getInstance().colourPalette[overlayHslColour]
																: 0);
													}
													overlayTextureColour = getOverlayShadow(overlayFloor.getRgb(), 96);
												} else {
													if (overlayTextureId > TextureLoader.instance.count()) {
														overlayRgbColour = overlayFloor.getAnotherRgb();
													}
													overlayHslColour = -2;
													underlay_floor_map_color = -1;
													overlayTextureColour = -1;
												}
											} else if (overlayFloor.getRgb() == -1) {
												if (overlayTextureId > TextureLoader.instance.count()) {
													overlayRgbColour = overlayMapColour;
												}
												overlayHslColour = -2;
												// ?
												if (z > 0) {
													underlay_floor_texture = -1;
												}

												overlayTextureId = -1;
											} else {
												overlayTextureColour = getOverlayShadow(overlayFloor.getRgb(), 96);
												overlayHslColour = overlayFloor.getRgb();
												if (overlayTextureId > TextureLoader.instance.count()) {
													overlayRgbColour = GameRasterizer
															.getInstance().colourPalette[overlayTextureColour];
												}
											}
										}

										if (Options.hdTextures.get()) {
											byte flag = tileFlags[z][centreX][centreY];
											scene.addTile(z, centreX, centreY, tileType, orientation, overlayTextureId,
													centreHeight, eastHeight, northEastHeight, northHeight,
													light(hsl_bitset_unmodified, centreLight),
													light(hsl_bitset_unmodified, eastLight),
													light(hsl_bitset_unmodified, northEastLight),
													light(hsl_bitset_unmodified, northLight),
													getOverlayShadow(overlayHslColour, centreLight),
													getOverlayShadow(overlayHslColour, eastLight),
													getOverlayShadow(overlayHslColour, northEastLight),
													getOverlayShadow(overlayHslColour, northLight),
													rgb_bitset_randomized, overlayRgbColour, overlayTextureColour,
													underlay_floor_texture, underlay_floor_map_color, false, flag, underlay - 1, overlayFloorId - 1);
										} else {
											byte flag = tileFlags[z][centreX][centreY];
											scene.addTile(z, centreX, centreY, tileType, orientation, overlayTextureId,
													centreHeight, eastHeight, northEastHeight, northHeight,
													light(hsl_bitset_unmodified, centreLight),
													light(hsl_bitset_unmodified, eastLight),
													light(hsl_bitset_unmodified, northEastLight),
													light(hsl_bitset_unmodified, northLight),
													getOverlayShadow(overlayHslColour, centreLight),
													getOverlayShadow(overlayHslColour, eastLight),
													getOverlayShadow(overlayHslColour, northEastLight),
													getOverlayShadow(overlayHslColour, northLight),
													rgb_bitset_randomized, overlayRgbColour, -1, 0, 0, true, flag, underlay - 1, overlayFloorId - 1);
										}

									}
								} else {
									scene.getTile(z, centreX, centreY).simple = null;
									scene.getTile(z, centreX, centreY).shape = null;
								}
							}
						}
					}
				}

			}

		//}

		SceneGraph.minimapUpdate = true;
	}

	/*
	 * public int[][][] getAnIntArrayArrayArray135() { return
	 * anIntArrayArrayArray135; }
	 * 
	 * public byte getOverlayOrientation(int z, int x, int y) { return
	 * overlayOrientations[z][x % 64][y % 64]; }
	 * 
	 * 
	 * public byte getOverlay(int z, int x, int y) { return overlays[z][x % 64][y %
	 * 64]; }
	 * 
	 * 
	 * public byte getManualTileHeight(int z, int x, int y) { return
	 * manualTileHeight[z][x % 64][y % 64]; }
	 * 
	 * 
	 * public byte getOverlayShape(int z, int x, int y) { return overlayShapes[z][x
	 * % 64][y % 64]; }
	 * 
	 * 
	 * public byte getTileFlag(int z, int x, int y) { return tileFlags[z][x % 64][y
	 * % 64]; }
	 * 
	 * 
	 * public int getTileHeight(int z, int x, int y) { return tileHeights[z][x %
	 * 64][y % 64]; }
	 * 
	 * 
	 * public byte getUnderlay(int z, int x, int y) { return underlays[z][x % 64][y
	 * % 64]; }
	 * 
	 * 
	 * 
	 * public void setOverlayOrientation(int z, int x, int y, byte orientation) {
	 * overlayOrientations[z][x % 64][y % 64] = orientation; }
	 * 
	 * 
	 * public void setOverlay(int z, int x, int y, byte overlay) { overlays[z][x %
	 * 64][y % 64] = overlay; }
	 * 
	 * 
	 * public void setManualTileHeight(int z, int x, int y, byte val) {
	 * manualTileHeight[z][x % 64][y % 64] = val; }
	 * 
	 * 
	 * public void setOverlayShape(int z, int x, int y, byte shape) {
	 * overlayShapes[z][x % 64][y % 64] = shape; }
	 * 
	 * 
	 * public void setTileFlag(int z, int x, int y, byte flag) { tileFlags[z][x %
	 * 64][y % 64] = flag; }
	 * 
	 * 
	 * public void setTileHeight(int z, int x, int y, int height) { tileHeights[z][x
	 * % 64][y % 64] = height; }
	 * 
	 * 
	 * public void setUnderlay(int z, int x, int y, byte underlay) { underlays[z][x
	 * % 64][y % 64] = underlay; }
	 * 
	 * public int[][] getTileHeights(int z) { return tileHeights[z]; }
	 */

}