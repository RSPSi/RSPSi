package com.jagex.cache.anim;

import com.jagex.cache.loader.anim.FrameLoader;

import java.util.HashMap;
import java.util.Map;

public class Animation {

	/**
	 * The animation precedence (will this animation 'override' other animations or
	 * will this one yield).
	 */
	private int animatingPrecedence = -1;

	/**
	 * The duration of each frame in this Animation.
	 */
	private int[] durations;

	/**
	 * The amount of frames in this Animation.
	 */
	private int frameCount;

	private int[] interleaveOrder;

	/**
	 * The amount of frames subtracted to restart the loop.
	 */
	private int loopOffset = -1;

	/**
	 * The maximum times this animation will loop.
	 */
	private int maximumLoops = 99;

	/**
	 * Indicates whether or not this player's shield will be displayed whilst this
	 * animation is played.
	 */
	private int playerOffhand = -1;

	/**
	 * Indicates whether or not this player's weapon will be displayed whilst this
	 * animation is played.
	 */
	private int playerMainhand = -1;

	/**
	 * The primary frame ids of this Animation.
	 */
	private int[] primaryFrames;

	private int priority = 5;

	private int replayMode = 2;

	/**
	 * The secondary frame ids of this Animation.
	 */
	private int[] secondaryFrames;

	private boolean stretches = false;

	/**
	 * The walking precedence (will the player be prevented from moving or can they
	 * continue).
	 */
	private int walkingPrecedence = -1;

	private int mayaId = -1;

	private Map<Integer, Integer> mayaFrameSounds;

	private int mayaStart, mayaEnd;

	private boolean[] mayaMasks;


	public int duration(int frameId) {
		int duration = durations[frameId];
		if (duration == 0) {
			Frame frame = FrameLoader.lookup(primaryFrames[frameId]);

			if (frame != null) {
				duration = durations[frameId] = frame.getDuration();
			}
		}

		return duration == 0 ? 1 : duration;
	}

	/**
	 * Gets the animation precedence (will this animation 'override' other
	 * animations or will this one yield).
	 */
	public int getAnimatingPrecedence() {
		return animatingPrecedence;
	}

	public int[] getDurations() {
		return durations;
	}

	/**
	 * Gets the amount of frames in this Animation.
	 * 
	 * @return The amount of frames.
	 */
	public int getFrameCount() {
		return frameCount;
	}

	public int[] getInterleaveOrder() {
		return interleaveOrder;
	}

	/**
	 * Gets the amount of frames subtracted to restart the loop.
	 * 
	 * @return The loop offset.
	 */
	public int getLoopOffset() {
		return loopOffset;
	}

	/**
	 * Gets the maximum times this animation will loop.
	 * 
	 * @return The maximum loop count.
	 */
	public int getMaximumLoops() {
		return maximumLoops;
	}

	/**
	 * Returns whether or not this player's shield will be displayed whilst this
	 * animation is played.
	 */
	public int getPlayerShieldDelta() {
		return playerOffhand;
	}

	/**
	 * Returns whether or not this player's weapon will be displayed whilst this
	 * animation is played.
	 */
	public int getPlayerWeaponDelta() {
		return playerMainhand;
	}

	/**
	 * Gets the primary frame ids of this Animation.
	 * 
	 * @return The primary frame ids.
	 */
	public int getPrimaryFrame(int index) {
		return primaryFrames[index];
	}

	/**
	 * Gets the priority of this Animation.
	 * 
	 * @return The priority.
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Gets the replay mode of this Animation.
	 * 
	 * @return The replay mode.
	 */
	public int getReplayMode() {
		return replayMode;
	}

	/**
	 * Gets the secondary frame ids of this Animation.
	 * 
	 * @return The secondary frame ids.
	 */
	public int getSecondaryFrame(int index) {
		return secondaryFrames[index];
	}

	/**
	 * Gets the walking precedence (will the player be prevented from moving or can
	 * they continue).
	 * 
	 * @return The walking precedence.
	 */
	public int getWalkingPrecedence() {
		return walkingPrecedence;
	}

	public Map<Integer, Integer> getMayaFrameSounds() {
		return mayaFrameSounds;
	}

	public boolean[] getMayaMasks() {
		return mayaMasks;
	}

	public boolean stretches() {
		return stretches;
	}

	public void setAnimatingPrecedence(int animatingPrecedence) {
		this.animatingPrecedence = animatingPrecedence;
	}

	public void setDurations(int[] durations) {
		this.durations = durations;
	}

	public void setFrameCount(int frameCount) {
		this.frameCount = frameCount;
	}

	public void setInterleaveOrder(int[] interleaveOrder) {
		this.interleaveOrder = interleaveOrder;
	}

	public void setLoopOffset(int loopOffset) {
		this.loopOffset = loopOffset;
	}

	public void setMaximumLoops(int maximumLoops) {
		this.maximumLoops = maximumLoops;
	}

	public void setPlayerOffhand(int playerOffhand) {
		this.playerOffhand = playerOffhand;
	}

	public void setPlayerMainhand(int playerMainhand) {
		this.playerMainhand = playerMainhand;
	}

	public void setPrimaryFrames(int[] primaryFrames) {
		this.primaryFrames = primaryFrames;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setReplayMode(int replayMode) {
		this.replayMode = replayMode;
	}

	public void setSecondaryFrames(int[] secondaryFrames) {
		this.secondaryFrames = secondaryFrames;
	}

	public void setStretches(boolean stretches) {
		this.stretches = stretches;
	}

	public void setWalkingPrecedence(int walkingPrecedence) {
		this.walkingPrecedence = walkingPrecedence;
	}

	public void setMayaId(int mayaId) {
		this.mayaId = mayaId;
	}

	public void setMayaStart(int mayaStart) {
		this.mayaStart = mayaStart;
	}

	public void setMayaEnd(int mayaEnd) {
		this.mayaEnd = mayaEnd;
	}

	public void setMayaFrameSounds(Map<Integer, Integer> mayaFrameSounds) {
		this.mayaFrameSounds = mayaFrameSounds;
	}

	public void setMayaMasks(boolean[] mayaMasks) {
		this.mayaMasks = mayaMasks;
	}

}