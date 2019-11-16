package com.jagex.cache;

/**
 * A Java implementation of the DState struct (structure holding all the
 * decompression-side stuff).
 * 
 * @see {@code http://svn.apache.org/repos/asf/labs/axmake/trunk/src/libuc++/srclib/bzip2/bzlib_private.h}
 */
class BZip2DecompressionState {

	// Class32

	/* for undoing the Burrows-Wheeler transform (FAST) */
	public static int[] tt;
	int[][] base;
	/* the buffer for bit stream reading */
	int bsBuff;
	int bsLive;
	static final int bzGSize = 50;

	/* Constants */
	static final int bzMaxAlphaSize = 258;

	static final int bzMaxCodeLen = 23;
	static final int bzMaxSelectors = 18002; // (2 + (900000 / BZ_G_SIZE))

	static final int bzNGroups = 6;
	static final int bzNIters = 4;
	static final int bzRunB = 1;
	int[] cftab;
	byte[] compressed;

	int count;
	int currentBlock;

	byte[] decompressed; // out
	int decompressedLength;
	boolean[] inUse;

	boolean[] inUse16;
	int k0;

	byte[][] len;
	int length;

	int[][] limit;
	int[] minLens;
	/* for decoding the MTF values */
	byte[] mtfa;
	/*-- Constants for the fast MTF decoder. --*/
	final int mtfaSize = 4096;
	int[] mtfbase;
	final int mtflSize = 16;

	int nextIn;
	int nextOut;
	/* map of bytes used in block */
	int nInUse;
	/* for undoing the Burrows-Wheeler transform */
	int origPtr;
	int[][] perm;

	boolean randomised;
	byte[] selector;
	byte[] selectorMtf;
	byte[] seqToUnseq;

	byte stateOutCh;
	/* for doing the final run-length decoding */
	int stateOutLen;

	int totalInHi32;
	int totalInLo32;
	int totalOutHigh32;
	int totalOutLo32;
	int tPos;
	int[] unzftab;
	int usedBlocks;

	BZip2DecompressionState() {
		unzftab = new int[256];
		cftab = new int[257];
		inUse = new boolean[256];
		inUse16 = new boolean[16];
		seqToUnseq = new byte[256];
		mtfa = new byte[4096];
		mtfbase = new int[16];
		selector = new byte[18002];
		selectorMtf = new byte[18002];
		len = new byte[6][258];
		limit = new int[6][258];
		base = new int[6][258];
		perm = new int[6][258];
		minLens = new int[6];
	}

}