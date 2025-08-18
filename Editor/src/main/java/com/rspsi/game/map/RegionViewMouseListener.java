package com.rspsi.game.map;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.jagex.cache.loader.map.MapIndexLoader;
import com.rspsi.ui.EditRegionsWindow;

import javafx.application.Platform;
import javafx.stage.Stage;

public class RegionViewMouseListener implements MouseListener {
	
	private EditRegionsWindow editRegions;
	public RegionViewMouseListener() {
		editRegions = new EditRegionsWindow();
		try {
			editRegions.start(new Stage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		if(arg0.getComponent() instanceof RegionView){
			RegionView view = (RegionView) arg0.getComponent();
			view.setHovered(true);
		}
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		if(arg0.getComponent() instanceof RegionView){
			RegionView view = (RegionView) arg0.getComponent();
			view.setHovered(false);
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if(arg0.getComponent() instanceof RegionView){
			RegionView view = (RegionView) arg0.getComponent();
			if(arg0.getButton() == MouseEvent.BUTTON3) {
					for(Component comp : view.getParent().getComponents()){
						if(comp instanceof RegionView){
							RegionView rv = (RegionView) comp;
							rv.setSelected(false);
						}
					}
				
				view.setSelected(true);
				JPopupMenu popup = new JPopupMenu();
				JMenuItem editRegion = new JMenuItem("Edit region");
				editRegion.addActionListener(al -> {
					Platform.runLater(() -> {

						editRegions.show(view);
						if(editRegions.valid()) {
							System.out.println("LS: " + editRegions.getLandscapeId() + " OBJ: " + editRegions.getObjectId());
							MapIndexLoader.setRegionData(view.getRegionX(), view.getRegionY(), editRegions.getLandscapeId(), editRegions.getObjectId());
							view.images = null;
							view.loadMap();
							view.invalidate();
						}
					});
				});
				popup.add(editRegion);
				popup.show(arg0.getComponent(), arg0.getX(), arg0.getY());
			} else {
				if(!arg0.isShiftDown()){
					for(Component comp : view.getParent().getComponents()){
						if(comp instanceof RegionView){
							RegionView rv = (RegionView) comp;
							rv.setSelected(false);
						}
					}
				}
				view.setSelected(true);
			}
		
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

		System.out.println("mouse released!");
	}


}
