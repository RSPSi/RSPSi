package com.rspsi.core.misc;

import javafx.scene.shape.Rectangle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
public class TileArea {

    @Getter
    private final int startX, startY, endX, endY;

    @Getter
    private Rectangle rectangle;
    
	public TileArea(int startX, int startY, int endX, int endY) {
	
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
        int baseX = Math.min(startX, endX);
        int baseY = Math.min(startY, endY);
        int width = Math.max(startX, endX) - Math.min(startX, endX) + 1;
        int height = Math.max(startY, endY) - Math.min(startY, endY) + 1;
		rectangle = new Rectangle(baseX, baseY, width, height);
	}
    
   
    

    public boolean contains(int x, int y){
        return rectangle.contains(x, y);
    }
    


}
