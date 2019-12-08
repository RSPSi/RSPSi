package com.jagex.map.tile;



import com.rspsi.options.Options;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
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
	

	public int getCentreColour() {
		return centreColour;
	}

	public int getEastColour() {
		return eastColour;
	}

	public int getNorthColour() {
		return northColour;
	}

	public int getNorthEastColour() {
		return northEastColour;
	}

	public int getTexture() {
		return texture;
	}

	public int getTileColour() {
		return tileColour;
	}

	public boolean isFlat() {
		return flat;
	}

	public void setCentreColour(int centreColour) {
		this.centreColour = centreColour;
	}

	public void setEastColour(int eastColour) {
		this.eastColour = eastColour;
	}

	public void setFlat(boolean flat) {
		this.flat = flat;
	}

	public void setNorthColour(int northColour) {
		this.northColour = northColour;
	}

	public void setNorthEastColour(int northEastColour) {
		this.northEastColour = northEastColour;
	}

	public void setTexture(int texture) {
		this.texture = texture;
	}

	public void setTileColour(int anInt722) {
		tileColour = anInt722;
	}


	public boolean isTextured() {
		return textured;
	}


	public int getColour() {
		return colour;
	}


	@Getter
	@Setter
	private int bufferOffset, uvBufferOffset, bufferLen;
	
	

}