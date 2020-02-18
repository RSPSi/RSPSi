package plugin.loader.texture;


import com.jagex.io.Buffer;

public class TextureOperation {


	boolean aBoolean2375;
	TextureOperation[] subOperations;
	int anInt2381;
	static int anInt2383 = 0;
	static int anInt2384 = 0;
	static int[] anIntArray2386 = new int[]{1, -1, -1, 1};

	final int[] method152(int var1, int var2) {
		return this.subOperations[var1].aBoolean2375?this.subOperations[var1].method154(var2, (byte)-118):this.subOperations[var1].method166(-1, var2)[0];
	}

	int[] method154(int var1, byte var2) {
		throw new IllegalStateException("This operation does not have dragComponent monochrome output");
	}

	int getSpriteFrame() {
		return -1;
	}


	void decode(int var1, Buffer var2) {
	}

	void postDecode() {
	}

	int getSpriteGroup() {
		return -1;
	}

	final int[][] method162(int var1, int var2) {
		if(this.subOperations[var2].aBoolean2375) {
			int[] var4 = this.subOperations[var2].method154(var1, (byte)-105);
			return new int[][]{var4, var4, var4};
		} else {
			return this.subOperations[var2].method166(-1, var1);
		}
	}

	public TextureOperation(int var1, boolean var2) {
		this.subOperations = new TextureOperation[var1];
		this.aBoolean2375 = var2;
	}

	int[][] method166(int var1, int var2) {
		if(var1 == -1) {
			throw new IllegalStateException("This operation does not have dragComponent colour output");
		} else {
			return null;
		}
	}
}
