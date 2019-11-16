package com.jagex.cache;

/*
 * http://svn.apache.org/repos/asf/labs/axmake/trunk/src/libuc++/srclib/bzip2/
 */
public final class BZip2Decompressor {

	// Class13

	private static BZip2DecompressionState state = new BZip2DecompressionState();

	/*
	 * http://svn.apache.org/repos/asf/labs/axmake/trunk/src/libuc++/srclib/
	 * bzip2/huffman.c
	 */
	private static void createDecodeTables(int[] limit, int[] base, int[] perm, byte[] length, int minLength,
			int maxLength, int alphabetSize) {
		int pp = 0;
		for (int i = minLength; i <= maxLength; i++) {
			for (int j = 0; j < alphabetSize; j++) {
				if (length[j] == i) {
					perm[pp] = j;
					pp++;
				}
			}
		}

		for (int i = 0; i < 23; i++) {
			base[i] = 0;
		}

		for (int i = 0; i < alphabetSize; i++) {
			base[length[i] + 1]++;
		}

		for (int i = 1; i < 23; i++) {
			base[i] += base[i - 1];
		}

		for (int i = 0; i < 23; i++) {
			limit[i] = 0;
		}

		int vec = 0;
		for (int i = minLength; i <= maxLength; i++) {
			vec += base[i + 1] - base[i];
			limit[i] = vec - 1;
			vec <<= 1;
		}

		for (int i = minLength + 1; i <= maxLength; i++) {
			base[i] = (limit[i - 1] + 1 << 1) - base[i];
		}
	}

	public static int decompress(byte[] output, int length, byte[] compressed, int decompressedLength, int minLen) {
		synchronized (state) {
			state.compressed = compressed;
			state.nextIn = minLen;
			state.decompressed = output;
			state.nextOut = 0;
			state.decompressedLength = decompressedLength;
			state.length = length;
			state.bsLive = 0;
			state.bsBuff = 0;
			state.totalInLo32 = 0;
			state.totalInHi32 = 0;
			state.totalOutLo32 = 0;
			state.totalOutHigh32 = 0;
			state.currentBlock = 0;
			decompress(state);
			length -= state.length;
			return length;
		}
	}

	private static void decompress(BZip2DecompressionState state) {
		int gMinLen = 0;
		int[] gLimit = null;
		int[] gBase = null;
		int[] gPerm = null;

		if (BZip2DecompressionState.tt == null) {// Because this isn't small
													// decompress
			BZip2DecompressionState.tt = new int[0x186a0]; // 100000
		}

		boolean flag19 = true;
		while (flag19) {
			byte uc = getUnsignedChar(state);
			if (uc == 23)
				return;
			uc = getUnsignedChar(state);
			uc = getUnsignedChar(state);
			uc = getUnsignedChar(state);
			uc = getUnsignedChar(state);
			uc = getUnsignedChar(state);
			state.currentBlock++;
			uc = getUnsignedChar(state);
			uc = getUnsignedChar(state);
			uc = getUnsignedChar(state);
			uc = getUnsignedChar(state);
			uc = getBit(state);

            state.randomised = uc != 0;

			if (state.randomised) {
				System.out.println("PANIC! RANDOMISED BLOCK!");
			}

			state.origPtr = 0;
			uc = getUnsignedChar(state);
			state.origPtr = state.origPtr << 8 | uc & 0xff;
			uc = getUnsignedChar(state);
			state.origPtr = state.origPtr << 8 | uc & 0xff;
			uc = getUnsignedChar(state);
			state.origPtr = state.origPtr << 8 | uc & 0xff;

			/*--- Receive the mapping table ---*/

			for (int i = 0; i < 16; i++) {
				byte bit = getBit(state);
                state.inUse16[i] = bit == 1;
			}

			for (int i = 0; i < 256; i++) {
				state.inUse[i] = false;
			}

			for (int i = 0; i < 16; i++) {
				if (state.inUse16[i]) {
					for (int j = 0; j < 16; j++) {
						byte bit = getBit(state);
						if (bit == 1) {
							state.inUse[i * 16 + j] = true;
						}
					}
				}
			}

			makeMaps(state);
			int alphabetSize = state.nInUse + 2;

			/*
			 * number of different Huffman tables in use
			 */
			int huffmanTableCount = getBits(3, state);

			/*
			 * number of times that the Huffman tables are swapped (each 50 bytes)
			 */
			int swapCount = getBits(15, state);

			/*--- Now the selectors ---*/
			for (int i = 0; i < swapCount; i++) {
				int count = 0;
				do {
					byte terminator = getBit(state);
					if (terminator == 0) {
						break;
					}
					count++;
				} while (true);

				/*
				 * zero-terminated (see above) bit runs (0..62) of MTF'ed Huffman table
				 * (*selectors_used)
				 */
				state.selectorMtf[i] = (byte) count;
			}

			/*--- Undo the MTF values for the selectors. ---*/

			byte[] pos = new byte[6];
			for (byte v = 0; v < huffmanTableCount; v++) {
				pos[v] = v;
			}

			for (int i = 0; i < swapCount; i++) {
				byte v = state.selectorMtf[i];
				byte tmp = pos[v];
				for (; v > 0; v--) {
					pos[v] = pos[v - 1];
				}

				pos[0] = tmp;
				state.selector[i] = tmp;
			}

			/*--- Now the coding tables ---*/

			for (int t = 0; t < huffmanTableCount; t++) {
				int read = getBits(5, state);
				for (int i = 0; i < alphabetSize; i++) {
					do {
						byte bit = getBit(state);
						if (bit == 0) {
							break;
						}
						bit = getBit(state);
						if (bit == 0) {
							read++;
						} else {
							read--;
						}
					} while (true);
					state.len[t][i] = (byte) read;
				}
			}

			/*--- Create the Huffman decoding tables ---*/

			for (int t = 0; t < huffmanTableCount; t++) {
				byte minLen = 32;
				int maxLen = 0;
				for (int i = 0; i < alphabetSize; i++) {
					if (state.len[t][i] > maxLen) {
						maxLen = state.len[t][i];
					}
					if (state.len[t][i] < minLen) {
						minLen = state.len[t][i];
					}
				}

				createDecodeTables(state.limit[t], state.base[t], state.perm[t], state.len[t], minLen, maxLen,
						alphabetSize);
				state.minLens[t] = minLen;
			}

			/*--- Now the MTF values ---*/

			int eob = state.nInUse + 1; // End of block?
			int groupNo = -1;
			int groupPos = 0;
			for (int i = 0; i <= 255; i++) {
				state.unzftab[i] = 0;
			}
			/*-- MTF init --*/

			int kk = 4095;
			for (int ii = 15; ii >= 0; ii--) {
				for (int jj = 15; jj >= 0; jj--) {
					state.mtfa[kk] = (byte) (ii * 16 + jj);
					kk--;
				}

				state.mtfbase[ii] = kk + 1;
			}

			/*-- end MTF init --*/

			int nblock = 0;

			// start get MTF val
			if (groupPos == 0) {
				groupNo++;
				groupPos = 50;
				byte gSel = state.selector[groupNo];
				gMinLen = state.minLens[gSel];
				gLimit = state.limit[gSel];
				gPerm = state.perm[gSel];
				gBase = state.base[gSel];
			}
			groupPos--;
			int zn = gMinLen;
			int zvec;
			byte zj;
			for (zvec = getBits(zn, state); zvec > gLimit[zn]; zvec = zvec << 1 | zj) {
				zn++;
				zj = getBit(state);
			}
			for (int nextSym = gPerm[zvec - gBase[zn]]; nextSym != eob;) {

				// end get mtf val

				if (nextSym == 0 || nextSym == 1) {
					int es = -1;
					int n = 1;
					do {
						if (nextSym == 0) {
							es += n;
						} else if (nextSym == 1) {
							es += 2 * n;
						}
						n *= 2;
						if (groupPos == 0) {
							groupNo++;
							groupPos = 50;
							byte gSel = state.selector[groupNo];
							gMinLen = state.minLens[gSel];
							gLimit = state.limit[gSel];
							gPerm = state.perm[gSel];
							gBase = state.base[gSel];
						}
						groupPos--;
						int zn_ = gMinLen;
						int zvec_;
						byte byte10;
						for (zvec_ = getBits(zn_, state); zvec_ > gLimit[zn_]; zvec_ = zvec_ << 1 | byte10) {
							zn_++;
							byte10 = getBit(state);
						}

						nextSym = gPerm[zvec_ - gBase[zn_]];
					} while (nextSym == 0 || nextSym == 1);
					es++;
					byte uc_ = state.seqToUnseq[state.mtfa[state.mtfbase[0]] & 0xff];
					state.unzftab[uc_ & 0xff] += es;
					for (; es > 0; es--) {
						BZip2DecompressionState.tt[nblock] = uc_ & 0xff;
						nblock++;
					}

				} else {
					int nn = nextSym - 1;
					byte uc_;
					/* avoid general-case expense */
					if (nn < 16) {
						int pp = state.mtfbase[0];
						uc_ = state.mtfa[pp + nn];
						for (; nn > 3; nn -= 4) {
							int z = pp + nn;
							state.mtfa[z] = state.mtfa[z - 1];
							state.mtfa[z - 1] = state.mtfa[z - 2];
							state.mtfa[z - 2] = state.mtfa[z - 3];
							state.mtfa[z - 3] = state.mtfa[z - 4];
						}

						for (; nn > 0; nn--) {
							state.mtfa[pp + nn] = state.mtfa[pp + nn - 1];
						}

						state.mtfa[pp] = uc_;
					} else {
						/* general case */
						int lno = nn / 16; // 16 is the MTFL size
						int off = nn % 16;
						int pp = state.mtfbase[lno] + off;
						uc_ = state.mtfa[pp];
						for (; pp > state.mtfbase[lno]; pp--) {
							state.mtfa[pp] = state.mtfa[pp - 1];
						}

						state.mtfbase[lno]++;
						for (; lno > 0; lno--) {
							state.mtfbase[lno]--;
							state.mtfa[state.mtfbase[lno]] = state.mtfa[state.mtfbase[lno - 1] + 16 - 1];
						}

						state.mtfbase[0]--;
						state.mtfa[state.mtfbase[0]] = uc_;
						if (state.mtfbase[0] == 0) {
							int kk_ = 4095;
							for (int ii = 15; ii >= 0; ii--) {
								for (int jj = 15; jj >= 0; jj--) {
									state.mtfa[kk_] = state.mtfa[state.mtfbase[ii] + jj];
									kk_--;
								}

								state.mtfbase[ii] = kk_ + 1;
							}

						}
					}

					state.unzftab[state.seqToUnseq[uc_ & 0xff] & 0xff]++;
					BZip2DecompressionState.tt[nblock] = state.seqToUnseq[uc_ & 0xff] & 0xff;
					nblock++;
					if (groupPos == 0) {
						groupNo++;
						groupPos = 50;
						byte byte14 = state.selector[groupNo];
						gMinLen = state.minLens[byte14];
						gLimit = state.limit[byte14];
						gPerm = state.perm[byte14];
						gBase = state.base[byte14];
					}
					groupPos--;
					int zn_ = gMinLen;
					int zvec_;
					byte byte11;
					for (zvec_ = getBits(zn_, state); zvec_ > gLimit[zn_]; zvec_ = zvec_ << 1 | byte11) {
						zn_++;
						byte11 = getBit(state);
					}

					nextSym = gPerm[zvec_ - gBase[zn_]];
				}
			}

			/*
			 * Now we know what nblock is, we can do a better sanity check on s->origPtr.
			 */
			state.stateOutLen = 0;
			state.stateOutCh = 0;
			/*-- Set up cftab to facilitate generation of T^(-1) --*/
			state.cftab[0] = 0;
			for (int i = 1; i <= 256; i++) {
				state.cftab[i] = state.unzftab[i - 1];
			}

			for (int i = 1; i <= 256; i++) {
				state.cftab[i] += state.cftab[i - 1];
			}

			/*-- compute the T^(-1) vector --*/
			for (int i = 0; i < nblock; i++) {
				byte uc_ = (byte) (BZip2DecompressionState.tt[i] & 0xff);
				BZip2DecompressionState.tt[state.cftab[uc_ & 0xff]] |= i << 8;
				state.cftab[uc_ & 0xff]++;
			}

			state.tPos = BZip2DecompressionState.tt[state.origPtr] >> 8;
			state.usedBlocks = 0;
			state.tPos = BZip2DecompressionState.tt[state.tPos];
			state.k0 = (byte) (state.tPos & 0xff);
			state.tPos >>= 8;
			state.usedBlocks++;
			state.count = nblock;
			finish(state);

            flag19 = state.usedBlocks == state.count + 1 && state.stateOutLen == 0;
		}
	}

	private static void finish(BZip2DecompressionState state) { // TODO lame
																// name
		// unRLE_obuf_to_output_FAST
		byte stateOutCh = state.stateOutCh;
		int stateOutLen = state.stateOutLen;
		int nBlockUsed = state.usedBlocks;
		int k0 = state.k0;
		int[] tt = BZip2DecompressionState.tt;
		int tPos = state.tPos;
		byte[] decompressed = state.decompressed;
		int csNextOut = state.nextOut;
		int length = state.length;

		int availOutInit = length;
		int savedNBlockPP = state.count + 1;

		outer: do {

			/* try to finish existing run */
			if (stateOutLen > 0) {
				do {
					if (length == 0) {
						break outer;
					}
					if (stateOutLen == 1) {
						break;
					}
					decompressed[csNextOut] = stateOutCh;

					/*
					 * In the actual implementation it updates the BZ CRC here, but Jagex's doesn't
					 * do this.
					 */
					stateOutLen--;
					csNextOut++;
					length--;
				} while (true);
				if (length == 0) {
					stateOutLen = 1;
					break;
				}

				decompressed[csNextOut] = stateOutCh;
				csNextOut++;
				length--;
			}

			boolean flag = true;
			while (flag) {
				flag = false;
				if (nBlockUsed == savedNBlockPP) {
					stateOutLen = 0;
					break outer;
				}
				stateOutCh = (byte) k0;

				// BZ_GET_FAST_C

				tPos = tt[tPos];
				byte k1 = (byte) (tPos & 0xff);
				tPos >>= 8;

				nBlockUsed++;
				if (k1 != k0) {
					k0 = k1;
					if (length == 0) {
						stateOutLen = 1;
					} else {
						decompressed[csNextOut] = stateOutCh;
						csNextOut++;
						length--;
						flag = true;
						continue;
					}
					break outer;
				}

				if (nBlockUsed != savedNBlockPP) {
					continue;
				}
				if (length == 0) {
					stateOutLen = 1;
					break outer;
				}
				decompressed[csNextOut] = stateOutCh;
				csNextOut++;
				length--;
				flag = true;
			}
			stateOutLen = 2;

			// BZ_GET_FAST_C

			tPos = tt[tPos];
			byte k1 = (byte) (tPos & 0xff);
			tPos >>= 8;

			// end BZ_GET_FAST_C

			if (++nBlockUsed != savedNBlockPP) {
				if (k1 != k0) {
					k0 = k1;
				} else {
					stateOutLen = 3;

					tPos = tt[tPos];
					byte k1_ = (byte) (tPos & 0xff);
					tPos >>= 8;

					if (++nBlockUsed != savedNBlockPP) {
						if (k1_ != k0) {
							k0 = k1_;
						} else {

							tPos = tt[tPos];
							byte k1__ = (byte) (tPos & 0xff);
							tPos >>= 8;

							nBlockUsed++;
							stateOutLen = (k1__ & 0xff) + 4;

							tPos = tt[tPos];
							k0 = (byte) (tPos & 0xff);
							tPos >>= 8;

							nBlockUsed++;
						}
					}
				}
			}
		} while (true);

		int oldTotalOutLo32 = state.totalOutLo32;
		state.totalOutLo32 += availOutInit - length;
		if (state.totalOutLo32 < oldTotalOutLo32) {
			state.totalOutHigh32++;
		}
		state.stateOutCh = stateOutCh;
		state.stateOutLen = stateOutLen;
		state.usedBlocks = nBlockUsed;
		state.k0 = k0;
		BZip2DecompressionState.tt = tt;
		state.tPos = tPos;
		state.decompressed = decompressed;
		state.nextOut = csNextOut;
		state.length = length;
	}

	private static byte getBit(BZip2DecompressionState state) {
		return (byte) getBits(1, state);
	}

	private static int getBits(int amount, BZip2DecompressionState state) {
		int bits;
		do {
			if (state.bsLive >= amount) {
				int v = state.bsBuff >> state.bsLive - amount & (1 << amount) - 1;
				state.bsLive -= amount;
				bits = v;
				break;
			}
			state.bsBuff = state.bsBuff << 8 | state.compressed[state.nextIn] & 0xff;
			state.bsLive += 8;
			state.nextIn++;
			state.decompressedLength--;
			state.totalInLo32++;
			if (state.totalInLo32 == 0) {
				state.totalInHi32++;
			}
		} while (true);
		return bits;
	}

	private static byte getUnsignedChar(BZip2DecompressionState state) {
		return (byte) getBits(8, state);
	}

	private static void makeMaps(BZip2DecompressionState state) {
		state.nInUse = 0;
		for (int i = 0; i < 256; i++) {
			if (state.inUse[i]) {
				state.seqToUnseq[state.nInUse] = (byte) i;
				state.nInUse++;
			}
		}
	}

}