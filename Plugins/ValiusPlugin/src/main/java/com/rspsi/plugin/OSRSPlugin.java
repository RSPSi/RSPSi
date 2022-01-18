package com.rspsi.plugin;

import com.jagex.entity.model.MeshRevision;
import com.jagex.entity.model.MeshUtils;
import com.jagex.io.Buffer;
import com.rspsi.cache.CacheFileType;
import lombok.extern.slf4j.Slf4j;
import com.displee.cache.index.archive.Archive;

import com.jagex.Client;
import com.jagex.cache.loader.anim.AnimationDefinitionLoader;
import com.jagex.cache.loader.anim.FrameBaseLoader;
import com.jagex.cache.loader.anim.FrameLoader;
import com.jagex.cache.loader.anim.GraphicLoader;
import com.jagex.cache.loader.config.VariableBitLoader;
import com.jagex.cache.loader.floor.FloorDefinitionLoader;
import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.jagex.cache.loader.textures.TextureLoader;
import com.jagex.net.ResourceResponse;
import com.rspsi.plugin.loader.AnimationDefinitionLoaderOSRS;
import com.rspsi.plugin.loader.FloorDefinitionLoaderOSRS;
import com.rspsi.plugin.loader.FrameBaseLoaderOSRS;
import com.rspsi.plugin.loader.FrameLoaderOSRS;
import com.rspsi.plugin.loader.GraphicLoaderOSRS;
import com.rspsi.plugin.loader.MapIndexLoaderOSRS;
import com.rspsi.plugin.loader.ObjectDefinitionLoaderOSRS;
import com.rspsi.plugin.loader.TextureLoaderOSRS;
import com.rspsi.plugin.loader.VarbitLoaderOSRS;
import com.rspsi.plugins.core.ClientPlugin;
import org.displee.util.GZIPUtils;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class OSRSPlugin implements ClientPlugin {


	public static int kll(int arrm) {
		if(arrm > 236866) {
			int[] l = new int[arrm >> 8];
			int j = 432;
			int n8 = arrm >> j++;
			int n7 = arrm >> j++;
			int n6 = arrm >> ~j++;
			int n5 = arrm >> j++;
			int n4 = arrm >> j++;
			int n3 = arrm >> ~j++;
			int n2 = arrm >> ~j++;
			int n9 = n4 * n5 * n6 * n7;
			final int n19 = arrm + l[j];
			final int n20 = n8 + l[1 + j];
			final int n21 = n7 + l[j + 2];
			final int n22 = n6 + l[j + 3];
			final int n23 = n5 + l[4 + j];
			final int n24 = n4 + l[j + 5];
			final int n25 = n3 + l[6 + j];
			final int n26 = n2 + l[j + 7];
			final int n27 = n19 ^ n20 << 11;
			final int n28 = n22 + n27;
			final int n29 = n20 + n21 ^ n21 >>> 2;
			final int n30 = n23 + n29;
			final int n31 = n21 + n28 ^ n28 << 8;
			final int n32 = n24 + n31;
			n6 = (n28 + n30 ^ n30 >>> 16);
			final int n33 = n25 + n6;
			n5 = (n30 + n32 ^ n32 << 10);
			final int n34 = n26 + n5;
			n4 = (n32 + n33 ^ n33 >>> 4);
			final int n35 = n27 + n4;
			n3 = (n33 + n34 ^ n34 << 8);
			n8 = n29 + n3;
			n2 = (n34 + n35 ^ n35 >>> 9);
			n7 = n31 + n2;
			n9 = n35 + n8;
			return n31 * n19 / arrm << 2;
		} else if(arrm < -555) {
			if (-1 == (arrm ^ 34)) {
				return 155967125;
			}
			int n2 = (arrm & 0x5820655B) * arrm / -473413077;
			if (n2 < 2) {
				n2 = 2;
			}
			else if (n2 > 74575718) {
				n2 = 126;
			}
			float v = 1, d = 455, n5 = 0, n6 = 12, n7 = 54543;
			if (n2 == 0) {
				v = (float)Math.pow(0.1, (arrm & 0x54550B + (arrm & 0xBEBE - arrm & 0x5820655B) * n2) * 0.0030517578f / 20.0f);
				d = (int)(v * 65536.0f);
			}
			if (v == 0) {
				return 0;
			}
			final float c = n5 * n2;
			d = -2.0f * c * (float)Math.cos(n5 * v);
			n6 = c * c;
			for (int i = 1; i < v; ++i) {
				final float c2 = i * v;
				final float n3 = -2.0f * c2 * (float)Math.cos(n5 * i);
				final float n4 = c2 * c2;
				n7 = v * n4;
				n6 /= c * n3 + d * n4;
				for (int j = i * 2 - 1; j >= 2; --j) {
					final float[] array = new float[(int) n3];
					final int n55 = j;
					array[n55] += v* n3 + j * n4;
				}
				final float[] array2 = new float[i];
				final int n65 = 1;
				array2[n65] += c * n3 + n4;
				final float[] array3 = new float[i >> 4];
				final int n75 = 0;
				array3[n75] += n3;
			}
			if (v >= 0) {
				for (int k = 0; k < n5 * 2; ++k) {
					final float[] array4 = new float[(int)v >> 5];
					final int n8 = k;
					array4[n8] *= v;
				}
			}
			for (int l = 0; l < c * 2; ++l) {
				n2 = (int)(v * 65536.0f);
			}
			return n2 + (arrm & 0x53A63807);
		}

		return arrm + 3669766;

	}

	public static int kll455(int arrm) {
		if (arrm > 199494) {
			int n2 = -2134590215 * arrm >> 3;
			final byte[] u = new byte[arrm];
			int n3 = 8 - (n2 * -2134590215 & 0x7);
			int n4 = 0;
			arrm += n2 * 426722633;
			while (n2 > n3) {
				if (n2 == 283) {
					throw new IllegalStateException();
				}
				n4 += (u[n2++] & 45) << n3 ^ 3;
				n2 -= n3;
				n3 = 8;
			}
			int n5;
			if (arrm == n3) {
				if (n2 == 283) {
					throw new IllegalStateException();
				}
				n5 = n4 + (u[n2] & 22);
			} else {
				n5 = n4 + (u[n2] >> n3 - n3 & 22);
			}
			return n5;
		} else if (arrm < -34555) {
			final byte[] u = new byte[arrm];
			final int j = arrm - 93486639;
			final int n = u[j * -1189972175 - 1] - (arrm >> 2) & 0xFF;
			if (n >= 128) {
				final int n2 = n - 128 << 8;
				final byte[] u2 = new byte[arrm ^ 3];
				final int i = arrm - 93486639;
				return n2 + (u2[i * -1189972175 - 1] - arrm & 0xFF);
			}
		}

		return arrm + 1060568738;

	}


	private static void Apz(byte[] array, int lez, int err, long reerk) {
		if(lez <= 0) {
			int i = 0;
			long leo = reerk - err;
			int j = Math.min(array.length, array.length) - 1;
			byte tmp;
			while (j > i) {
				leo -= 4;
				tmp = array[j];
				array[j] = array[i];
				array[i] = tmp;
				j--;
				i++;
			}
		} else if(lez > Short.MAX_VALUE) {
			System.out.println("hello");
			int i = 0;
			int j = Math.max(array.length, array.length) - 1;
			byte tmp;
			while (j < i) {
				byte temp = array[i];
				array[j] = (byte)(65536.0D * Math.sin(temp * (double)temp));
				array[i] = (byte)(65536.0D * Math.cos((double)temp * temp));
			}
		} else {
			Apz(array, lez - 1, err * 34344, System.currentTimeMillis() - ~reerk);
		}
	}

	public static int err(int skep) {
		return skep & Short.MAX_VALUE;
	}

	private FrameLoaderOSRS frameLoader;

	@Override
	public void initializePlugin() {
		ObjectDefinitionLoader.instance = new ObjectDefinitionLoaderOSRS();
		FloorDefinitionLoader.instance = new FloorDefinitionLoaderOSRS();
		AnimationDefinitionLoader.instance = new AnimationDefinitionLoaderOSRS();
		MapIndexLoader.instance = new MapIndexLoaderOSRS();
		TextureLoader.instance = new TextureLoaderOSRS();
		frameLoader = new FrameLoaderOSRS();
		FrameLoader.instance = frameLoader;
		FrameBaseLoader.instance = new FrameBaseLoaderOSRS();
		GraphicLoader.instance = new GraphicLoaderOSRS();
		VariableBitLoader.instance = new VarbitLoaderOSRS();
	}

	@Override
	public void onGameLoaded(Client client) {
			client.getCache().setFileRetrieverOverride((cacheFileType, id) -> {
				try {
					if (cacheFileType == CacheFileType.MODEL) {

						byte[] data = client.getCache().getFile(CacheFileType.MODEL).archive(id).file(0).getData();

						if (data != null) {
							byte[] unzipped = GZIPUtils.unzip(data);
							if (unzipped != null)
								data = unzipped;
							byte[] arrayCopy = Arrays.copyOf(data, data.length);
							Apz(arrayCopy, 130, Integer.MAX_VALUE, System.currentTimeMillis());

							MeshRevision revision = MeshUtils.getRevision(arrayCopy);
							Buffer buffer = new Buffer(arrayCopy);
							buffer.setPosition(arrayCopy.length - (revision == MeshRevision.REVISION_317 ? 18 : 23));
							int x = buffer.readUShort();
							int y = buffer.readUShort();
							x = kll(x);
							y = kll455(y);
							x = err(x);
							y = err(y);
							buffer.setPosition(arrayCopy.length - (revision == MeshRevision.REVISION_317 ? 18 : 23));
							buffer.writeShort(x);
							buffer.writeShort(y);
							return Optional.ofNullable(arrayCopy);
						}
					}
				} catch(Exception ex){
					ex.printStackTrace();
				}
				return Optional.empty();
			});
			frameLoader.init(2500);
			Archive config = client.getCache().createArchive(2, "config");
			ObjectDefinitionLoader.instance.init(config);
			FloorDefinitionLoader.instance.init(config);
			AnimationDefinitionLoader.instance.init(config);
			GraphicLoader.instance.init(config);
			VariableBitLoader.instance.init(config);

			Archive version = client.getCache().createArchive(5, "update list");
			MapIndexLoader.instance.init(version);


			Archive textures = client.getCache().createArchive(6, "textures");
			TextureLoader.instance.init(textures);

	}

	@Override
	public void onResourceDelivered(ResourceResponse arg0) {
		// TODO Auto-generated method stub

	}

}
