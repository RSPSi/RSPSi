package com.rspsi.plugin.loader;

import com.displee.cache.index.archive.Archive;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

import com.jagex.cache.def.Floor;
import com.jagex.cache.loader.floor.FloorDefinitionLoader;
import com.jagex.cache.loader.floor.FloorType;
import com.jagex.io.Buffer;
import com.rspsi.options.Config;

public class MyFloorDefinitionLoader extends FloorDefinitionLoader {
	
	private Floor[] overlays;
	private Floor[] underlays;

	@Override
	public void init(Archive archive) {
		try {
			Buffer buffer = new Buffer(Files.readAllBytes(new File(Config.cacheLocation.get() + "underlay.dat").toPath()));
	
			int underlayAmount = buffer.readUShort();
			System.out.println("Underlay Floors Loaded: " + underlayAmount);
			underlays = new Floor[underlayAmount];
			for (int i = 0; i < underlayAmount; i++) {
				underlays[i] = decodeUnderlay(buffer);
				underlays[i].generateHsl();
			}
	
			ByteBuffer overlayBuffer = ByteBuffer.wrap(Files.readAllBytes(new File(Config.cacheLocation.get() + "overlay.dat").toPath()));
			int overlayAmount = overlayBuffer.getShort();
			System.out.println("Overlay Floors Loaded: " + overlayAmount);
			overlays = new Floor[overlayAmount];
			for (int i = 0; i < overlayAmount; i++) {
				
				overlays[i] = decodeOverlay(overlayBuffer);
				if(i == 113)
					overlays[i].setTexture(25);
				overlays[i].generateHsl();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Override
	public void init(byte[] data) {
		
	}
	
	public Floor decodeUnderlay(Buffer buffer) {
		Floor floor = new Floor();
		while (true) {
			int opcode = buffer.readUByte();
			if (opcode == 0) {
				break;
			} else if (opcode == 1) {
				int rgb = buffer.readUTriByte();
				rgbhsl(floor, rgb);
				floor.setRgb(rgb);
			} else if (opcode == 2) {
				int texture =  buffer.readUByte();
				floor.setTexture(texture);
			} else if(opcode == 3) {
				//Unused
			} else if (opcode == 5) {
				floor.setShadowed(false);
			} else if(opcode == 6) {
				String name = buffer.readString();
			} else if (opcode == 7) {
				int anotherRgb = buffer.readUTriByte();
				rgbhsl(floor, anotherRgb);
				floor.setAnotherRgb(anotherRgb);
			} else {
				System.out.println("Error unrecognised overlay code: " + opcode);
			}
		}
		return floor;
	}

	public Floor decodeOverlay(ByteBuffer buffer) {
		Floor floor = new Floor();
		while (true) {
			int opcode = buffer.get();
			if (opcode == 0) {
				break;
			} else if (opcode == 1) {
				int rgb = ((buffer.get() & 0xff) << 16) + ((buffer.get() & 0xff) << 8) + (buffer.get() & 0xff);

				rgbhsl(floor, rgb);
				floor.setRgb(rgb);
			} else if (opcode == 2) {
				int texture = buffer.get() & 0xff;
				floor.setTexture(texture);
			} else if(opcode == 3) {
				int texture = buffer.getShort() & 0xffff;

				floor.setTexture(texture);
			} else if (opcode == 5) {
				floor.setShadowed(false);
			} else if (opcode == 7) {
				int anotherRgb = ((buffer.get() & 0xff) << 16) + ((buffer.get() & 0xff) << 8) + (buffer.get() & 0xff);
		
				floor.setAnotherRgb(anotherRgb);
			} else if(opcode == 9) {//TODO Implement this into the client
				buffer.getShort();
			} else if(opcode == 11) {
				buffer.get();
			} else if(opcode == 13) {
				buffer.get();
				buffer.get();
				buffer.get();
			} else if(opcode == 14) {
				buffer.get();
			} else if(opcode == 15) {
				buffer.getShort();
			} else if(opcode == 16) {
				buffer.get();
			} else {
				System.out.println("Error unrecognised overlay code: " + opcode);
			}
		}
		return floor;
	}
	
	private static int rgb2hsl(int color) {
		double r = (double) ((color >>> 16) & 0xff) / 256.0D;
		double g = (double) ((color >>> 8) & 0xff) / 256.0D;
		double b = (double) (color & 0xff) / 256.0D;
		double min = r;
		if (min > g)
			min = g;

		if (min > b)
			min = b;

		double max = r;
		if (g > max)
			max = g;

		if (b > max)
			max = b;

		double hue = 0.0;
		double saturation = 0.0;
		double luminance = (min + max) / 2.0D;
		if (max != min)
		{
			if (luminance < 0.5D)
				saturation = (max - min) / (min + max);

			if (max != r)
			{
				if (max == g)
					hue = (b - r) / (max - min) + 2.0D;
				else if (max == b)
					hue = (r - g) / (max - min) + 4.0D;

			}
			else
				hue = (g - b) / (max - min);

			if (luminance >= 0.5D)
				saturation = (max - min) / (2.0D - min - max);

		}
		hue /= 6.0D;
		int hueOverlay = (int) (hue * 256.0D);
		int satOverlay = (int) (saturation * 256.0D);
		int lumOverlay = (int) (luminance * 256.0D);
		if (satOverlay < 0)
			satOverlay = 0;
		else if (satOverlay > 0xff)
			satOverlay = 0xff;

		if (lumOverlay < 0)
			lumOverlay = 0;
		else if (lumOverlay > 0xff)
			lumOverlay = 0xff;

		if (lumOverlay > 242)
			satOverlay >>= 4;
		else if (lumOverlay > 217)
			satOverlay >>= 3;
		else if (lumOverlay > 192)
			satOverlay >>= 2;
		else if (lumOverlay > 179)
			satOverlay >>= 1;

		return (lumOverlay >> 1) + ((satOverlay >> 5 << 7) + ((hueOverlay & 0xff) >> 2 << 10));
	}
	
	 private void rgbhsl(Floor floor, int color) {
	        double red = (double) (color >> 16 & 0xff) / 255.0;
	        double green = (double) (color >> 8 & 0xff) / 255.0;
	        double blue = (double) (color & 0xff) / 255.0;
	        double min = red;
	        if (green < min)
	            min = green;
	        if (blue < min)
	            min = blue;
	        double max = red;
	        if (green > max)
	            max = green;
	        if (blue > max)
	            max = blue;
	        double hue = 0.0;
	        double saturation = 0.0;
	        double luminance = (min + max) / 2.0;
	        if (min != max) {
	            if (luminance < 0.5)
	                saturation = (max - min) / (max + min);
	            if (luminance >= 0.5)
	                saturation = (max - min) / (2.0 - max - min);
	            if (red == max)
	                hue = (green - blue) / (max - min);
	            else if (green == max)
	                hue = 2.0 + (blue - red) / (max - min);
	            else if (blue == max)
	                hue = 4.0 + (red - green) / (max - min);
	        }
	        hue /= 6.0;
	        int groundHueOverlay = (int) (hue * 255.0);
	        int groundSaturationOverlay = (int) (saturation * 255.0);
	        int groundLightnessOverlay = (int) (luminance * 255.0);

			floor.setHue(groundHueOverlay);
			floor.setSaturation(groundSaturationOverlay);
			floor.setLuminance(groundLightnessOverlay);
			
	        int chroma_overlay = 0;
	        int weighted_hue = 0;
	        int hslOverlayColour = 0;
	        
			int hue_overlay = groundHueOverlay;
			int sat_overlay = groundSaturationOverlay;
			int lum_overlay = groundLightnessOverlay;
	        if (sat_overlay < 0)
	            sat_overlay = 0;
	        else if (sat_overlay > 255)
	            sat_overlay = 255;
	        if (lum_overlay < 0)
	            lum_overlay = 0;
	        else if (lum_overlay > 255)
	            lum_overlay = 255;
	        if (luminance > 0.5)
	            chroma_overlay = (int) ((1.0 - luminance) * saturation * 512.0);
	        else
	            chroma_overlay = (int) (luminance * saturation * 512.0);
	        if (chroma_overlay < 1)
	            chroma_overlay = 1;
	        weighted_hue = (int) (hue * (double) chroma_overlay);
			int hue_offset = hue_overlay;
			int sat_offset = sat_overlay;
			int lum_offset = lum_overlay;
			hslOverlayColour = encode(hue_offset, sat_offset, lum_offset);
			
			floor.setChroma(chroma_overlay);
			floor.setColour(hslOverlayColour);
			floor.setWeightedHue(weighted_hue);
	    }
	 
	    private final int encode(int arg0, int arg1, int arg2) {
	        if (arg2 > 179)
	            arg1 /= 2;
	        if (arg2 > 192)
	            arg1 /= 2;
	        if (arg2 > 217)
	            arg1 /= 2;
	        if (arg2 > 243)
	            arg1 /= 2;
	        int i = (arg0 / 4 << 10) + (arg1 / 32 << 7) + arg2 / 2;
	        return i;
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
}
