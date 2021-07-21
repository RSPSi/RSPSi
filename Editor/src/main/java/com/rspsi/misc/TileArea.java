package com.rspsi.misc;

import javafx.scene.shape.Rectangle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.joml.Vector2i;

import java.util.function.Consumer;

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


    public void forEach(Consumer<Vector2i> positionConsumer) {
        for(int x = startX;x<=endX;x++)
            for(int y = startY ;y<=endY;y++){

            }
	}
}
