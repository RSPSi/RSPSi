package com.rspsi.game.map;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

import javax.swing.JComponent;

import com.jagex.net.ResourceResponse;
import com.jagex.util.TextRenderUtils;

public class RegionView extends JComponent {

	private MapTile region;
	private boolean isHovered;
	private boolean isSelected;
	private int landscapeId = -1;
	private int objectsId = -1;
	private int hash;
	private int regionX;
	private int regionY;
	public BufferedImage[] images;

	@Override
	public void invalidate(){
		super.invalidate();
		if(this.getParent() != null)
			this.getParent().repaint();
	}

	public void setHovered(boolean isHovered) {
		this.isHovered = isHovered;
		this.invalidate();
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		this.invalidate();
	}

	public RegionView(int x, int y) {
		super();
		this.regionX = x;
		this.regionY = y;
		this.hash = (x << 8) + y;
	}
	
	


	public void loadMap(){
		Optional<MapTile> mapTile = MapTile.create(this, regionX, regionY);
		if(!mapTile.isPresent())
			return;
		this.region = mapTile.get();
		region.init();
		this.landscapeId = region.landscapeId;
		this.objectsId = region.objectsId;
	}

	@Override
	public void paintComponent(Graphics g){
		//System.out.println("painting component");
		//if(region.isVisible(minX, maxX, minY, maxY)){
			Graphics2D g2d = (Graphics2D) g;
			g.setFont(new Font("Helvetica", 0, 9));
			boolean exists = MapTile.exists(regionX, regionY);
			if(exists && region == null && images == null)
				loadMap();
			if(!exists){
				g.setColor(java.awt.Color.black);
				g.fillRect(0, 0, 64, 64);
				TextRenderUtils.renderCenter(g2d, "NULL", 32, 18, Color.red.getRGB());
				if(MapView.renderXY) {


					TextRenderUtils.renderCenter(g2d, "X: " + (regionX * 64), 31, 33, Color.red.getRGB());
					TextRenderUtils.renderCenter(g2d, "Y: " + (regionY * 64), 31, 43, Color.red.getRGB());
				}
			} else {
				if(!MapView.showImages.get()) {
					if(MapView.renderHash) {

						TextRenderUtils.renderCenter(g2d, "HASH: " + hash, 32, 18, Color.black.getRGB());
						TextRenderUtils.renderCenter(g2d, "HASH: " + hash, 31, 19, Color.white.getRGB());
					} 
					if(MapView.renderXY) {

						TextRenderUtils.renderCenter(g2d, "X: " + (regionX * 64), 32, 32, Color.black.getRGB());
						TextRenderUtils.renderCenter(g2d, "Y: " + (regionY * 64), 32, 42, Color.black.getRGB());

						TextRenderUtils.renderCenter(g2d, "X: " + (regionX * 64), 31, 33, Color.white.getRGB());
						TextRenderUtils.renderCenter(g2d, "Y: " + (regionY * 64), 31, 43, Color.white.getRGB());
					}
				} else if(images == null) {
					region.loadTile();
					g.setColor(java.awt.Color.black);
					g.clearRect(0, 0, 64, 64);
					g.setColor(java.awt.Color.black);
					g.fillRect(0, 0, 64, 64);
					g.setColor(java.awt.Color.white);
					if(MapView.renderHash) 
						TextRenderUtils.renderCenter(g2d, "HASH: " + hash, 32, 18, Color.white.getRGB());

					TextRenderUtils.renderCenter(g2d, "Loading...", 32, 42, Color.white.getRGB());


				} else {
					region = null;
					g.drawImage(images[MapView.heightLevel.get()], 0, 0, this);
					if(MapView.renderHash) {

						TextRenderUtils.renderCenter(g2d, "HASH: " + hash, 32, 18, Color.black.getRGB());
						TextRenderUtils.renderCenter(g2d, "HASH: " + hash, 31, 19, Color.white.getRGB());
					} 
					if(MapView.renderXY) {

						TextRenderUtils.renderCenter(g2d, "X: " + (regionX * 64), 32, 32, Color.black.getRGB());
						TextRenderUtils.renderCenter(g2d, "Y: " + (regionY * 64), 32, 42, Color.black.getRGB());

						TextRenderUtils.renderCenter(g2d, "X: " + (regionX * 64), 31, 33, Color.white.getRGB());
						TextRenderUtils.renderCenter(g2d, "Y: " + (regionY * 64), 31, 43, Color.white.getRGB());
					}
				}
			}
			//}	

			if(this.isSelected){
				g.setColor(new java.awt.Color(104, 66, 244, 50));
				g.fillRect(0, 0, 64, 64);
			} else if(this.isHovered){
				g.setColor(new java.awt.Color(66, 134, 244, 50));
				g.fillRect(0, 0, 64, 64);
			}

			g.setColor(java.awt.Color.red);
			g.drawRect(0, 0, 64, 64);

	}

	public void deliverResource(ResourceResponse response) {
		if(region != null && region.landscapeId == response.getRequest().getFile()) {
			region.onResourceResponse(response);
		}
	}

	public int getRegionX() {
		return regionX;
	}

	public void setRegionX(int regionX) {
		this.regionX = regionX;
	}

	public int getLandscapeId() {
		return landscapeId;
	}

	public int getObjectsId() {
		return objectsId;
	}

	public int getRegionY() {
		return regionY;
	}



}
