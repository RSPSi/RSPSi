package com.rspsi.plugin.loader;

import com.displee.cache.index.archive.Archive;

import java.nio.ByteBuffer;

import com.jagex.cache.def.Floor;
import com.jagex.cache.loader.floor.FloorDefinitionLoader;
import com.jagex.cache.loader.floor.FloorType;

public class FloorDefinitionLoader317 extends FloorDefinitionLoader {

	private Floor[] overlays;
	private Floor[] underlays;

	@Override
	public void init(Archive archive) {
		ByteBuffer buffer = ByteBuffer.wrap(archive.file("flo.dat").getData());
		int underlayAmount = buffer.getShort();
		System.out.println("Underlay Floors Loaded: " + underlayAmount);
		underlays = new Floor[underlayAmount];
		for (int i = 0; i < underlayAmount; i++) {
			underlays[i] = decodeOverlay(buffer);
			underlays[i].generateHsl();
		}
		System.out.println("Overlay Floors Loaded: " + underlayAmount);
		overlays = new Floor[underlayAmount];
		for(int i = 0;i<underlays.length;i++)
			overlays[i] = underlays[i];
	}

	@Override
	public void init(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int underlayAmount = buffer.getShort();
		System.out.println("Underlay Floors Loaded: " + underlayAmount);
		underlays = new Floor[underlayAmount];
		for (int i = 0; i < underlayAmount; i++) {
			underlays[i] = decodeUnderlay(buffer);
			underlays[i].generateHsl();
		}
		int overlayAmount = buffer.getShort();
		System.out.println("Overlay Floors Loaded: " + overlayAmount);
		overlays = new Floor[overlayAmount];
		for (int i = 0; i < overlayAmount; i++) {
			
			overlays[i] = decodeOverlay(buffer);
			overlays[i].generateHsl();
		}
	}

	public Floor decodeUnderlay(ByteBuffer buffer) {
		Floor floor = new Floor();
		while (true){
			int opcode = buffer.get();
			if (opcode == 0) {
				break;
			} else if (opcode == 1) {
				int rgb = ((buffer.get() & 0xff) << 16) + ((buffer.get() & 0xff) << 8) + (buffer.get() & 0xff);
				floor.setRgb(rgb);
			} else {
				System.out.println("Error unrecognised underlay code: " + opcode);
			}
		}
		return floor;
	}

	public Floor decodeOverlay(ByteBuffer buffer) {
		//All these classes were made for 474 data so a lot of 
		//Them match already
		Floor floor = new Floor();
		while (true) {
			int opcode = buffer.get();
			if (opcode == 0) {
				break;
			} else if (opcode == 1) {
				//This bit right here is readUTriByte
				int rgb = ((buffer.get() & 0xff) << 16) + ((buffer.get() & 0xff) << 8) + (buffer.get() & 0xff);
				floor.setRgb(rgb);
			} else if (opcode == 2) {
				int texture = buffer.get() & 0xff;
				floor.setTexture(texture);
			} else if(opcode == 3) {
				
			} else if (opcode == 5) {
				floor.setShadowed(false);
			} else if(opcode == 6) {
				while (buffer.get() != 10) {
					//This is just to read a string, I don't use it in the editor for some reason
				}
			} else if (opcode == 7) {
				int anotherRgb = ((buffer.get() & 0xff) << 16) + ((buffer.get() & 0xff) << 8) + (buffer.get() & 0xff);
				floor.setAnotherRgb(anotherRgb);
			} else {
				System.out.println("Error unrecognised overlay code: " + opcode);
			}
		}
		return floor;
	}
	
	@Override
	public Floor getFloor(int id, FloorType type) {
		if(type == FloorType.OVERLAY)
			return overlays[id];
		else
			return underlays[id];
	}


	@Override
	public int getSize(FloorType type) {
		if(type == FloorType.OVERLAY)
			return overlays.length;
		else
			return underlays.length;
	}

	@Override
	public int count() {
		return 0;
	}

	@Override
	public Floor forId(int arg0) {
		return null;
	}


}
