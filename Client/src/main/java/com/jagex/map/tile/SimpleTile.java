package com.jagex.map.tile;



import com.rspsi.options.Options;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public final class SimpleTile {

	int centreColour;
	int eastColour;
	int northEastColour;
	int northColour;
	int tileColour;
	boolean flat;
	int texture;
	
	public boolean textured;
	int colour;

	public SimpleTile(int centreColour, int eastColour, int northEastColour, int northColour, int texture,
			int tileColour, boolean flat, int colour, boolean tex) {
		this.centreColour = centreColour;
		this.eastColour = eastColour;
		this.northEastColour = northEastColour;
		this.northColour = northColour;
		this.texture = texture;
		this.tileColour = tileColour;
		this.flat = flat;
		this.colour = colour;
		
		this.textured = tex;
		int cheapHax = texture;
		if(Options.hdTextures.get())
			cheapHax = -1;
		textured = cheapHax != -1;
	}

}