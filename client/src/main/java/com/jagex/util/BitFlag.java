package com.jagex.util;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.major.map.RenderFlags;

/**
 * Represents a character entity's update flags.
 * 
 * @author relex lawl
 */

public class BitFlag {
	
	/**
	 * A set containing the entity's update flags.
	 */
	private BitSet flags = new BitSet();
	
	/**
	 * Checks if {@code flag} is contained in the entity's flag set.
	 * @param flag	The flag to check.
	 * @return		The flags set contains said flag.
	 */
	public boolean flagged(RenderFlags flag) {
		return flags.get(flag.ordinal());
	}
	
	/**
	 * Checks if an update is required by checking if flags set is empty.
	 * @return	Flags set is not empty.
	 */
	public boolean isUpdateRequired() {
		return !flags.isEmpty();
	}
	
	/**
	 * Puts a flag value into the flags set.
	 * @param flag	Flag to put into the flags set.
	 * @return		The UpdateFlag instance.
	 */
	public BitFlag flag(RenderFlags flag) {
		flags.set(flag.ordinal(), true);
		return this;
	}
	
	public BitFlag() {
		
	}
	
	public BitFlag(BitFlag toCopy) {
		for(RenderFlags flag : RenderFlags.values()) {
			if(toCopy.flagged(flag))
				flag(flag);
		}
	}
	
	public BitFlag(BitFlag toCopy, List<RenderFlags> exclude) {
		for(RenderFlags flag : RenderFlags.values()) {
			if(toCopy.flagged(flag) && !exclude.contains(flag))
				flag(flag);
		}
	}
	
	public BitFlag(byte val) {
		for(RenderFlags flag : RenderFlags.values()) {
			if((val & flag.getBit()) == flag.getBit())
				this.flag(flag);
		}
	}

	/**
	 * Removes every flag in the flags set.
	 * @return	The UpdateFlag instance.
	 */
	public BitFlag reset() {
		flags.clear();
		return this;
	}

	public BitFlag flagAll(RenderFlags... flags) {
		for(RenderFlags flag : flags) {
			flag(flag);
		}
		return this;
	}
	
	public byte encode() {
		byte flag = 0;
		if(this.flagged(RenderFlags.BLOCKED_TILE))
			flag |= RenderFlags.BLOCKED_TILE.getBit();
		if(this.flagged(RenderFlags.BRIDGE_TILE))
			flag |= RenderFlags.BRIDGE_TILE.getBit();
		if(this.flagged(RenderFlags.FORCE_LOWEST_PLANE))
			flag |= RenderFlags.FORCE_LOWEST_PLANE.getBit();
		if(this.flagged(RenderFlags.RENDER_ON_LOWER_Z))
			flag |= RenderFlags.RENDER_ON_LOWER_Z.getBit();
		if(this.flagged(RenderFlags.DISABLE_RENDERING))
			flag |= RenderFlags.DISABLE_RENDERING.getBit();
		return flag;
	}
}
