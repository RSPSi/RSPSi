package com.rspsi.game.map;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.rspsi.ui.MainWindow;
import com.rspsi.cache.CacheFileType;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.google.common.io.Files;
import com.jagex.cache.loader.map.MapIndexLoader;
import com.jagex.net.ResourceResponse;
import com.rspsi.util.ChangeListenerUtil;
import com.rspsi.util.FXDialogs;
import com.rspsi.util.FilterMode;
import com.rspsi.util.RetentionFileChooser;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Renders a global map view.
 * Work in progress
 * Currently JavaFX doesn't do this as well as Swing.
 * @author James
 *
 */
public class MapView extends JFrame {
	

	public static boolean renderHash = true;
	public static boolean renderXY = true;
	public static BooleanProperty showImages = new SimpleBooleanProperty(true);
	public static IntegerProperty heightLevel = new SimpleIntegerProperty(0);
	
	
	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onResourceResponse(ResourceResponse response) {
		if(response.getRequest().getType() == CacheFileType.MAP) {
			for(Component component : jPanel.getComponents()) {
				if(component instanceof RegionView) {
					RegionView view = (RegionView) component;
						view.deliverResource(response);
					
				}
			}
		}
	}
	
	public void invalidateChildren() {
		for(Component component : jPanel.getComponents()) {
			if(component instanceof RegionView) {
				RegionView view = (RegionView) component;
					view.invalidate();
				
			}
		}
	}


	private JPanel jPanel;
	private JScrollPane jScrollPane;
	
	public MapView() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		jScrollPane = new JScrollPane();
		jPanel = new JPanel();
		jPanel.setLayout(null);
		jScrollPane.setViewportView(jPanel);
		JMenuBar menu = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem saveOption = new JMenuItem("Save map_index");
		this.setJMenuBar(menu);
		saveOption.addActionListener(al -> {
			Platform.runLater(() -> {
				File f = RetentionFileChooser.showSaveDialog("Please select a location", null, "map_index", FilterMode.NONE);
				if(f != null) {
					try {
						Files.write(MapIndexLoader.instance.encode(), f);
					} catch (IOException e) {
						FXDialogs.showError(MainWindow.getSingleton().getStage().getOwner(), "Error while saving map_index", "There was a failure while attempting to save\nthe map_index to the selected file.");
						e.printStackTrace();
					}
				}
			});
		});
		file.add(saveOption);
		menu.add(file);
		
		JPanel bottomBar = new JPanel();
		//bottomBar.setLayout(new Box);
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		
		setContentPane(contentPane);
		
		contentPane.add(jScrollPane, BorderLayout.CENTER);
		ChangeListenerUtil.addListener(() -> invalidateChildren(), showImages);
		ChangeListenerUtil.addListener(() -> invalidateChildren(), heightLevel);
	}
	

	public void initTiles() {

		EventBus.getDefault().register(this);
		RegionViewMouseListener listener = new RegionViewMouseListener();
		System.out.println("INIT TILES 1");

		jPanel.setPreferredSize(new Dimension(150 * 64 + 1, 150 * 64 + 1));
		jPanel.revalidate();
		Thread t = new Thread(() -> {
			for(int y = 150;y>=0;y--) {
				for(int x = 0;x<150;x++) {
					RegionView r = new RegionView(x, y);
					r.addMouseListener(listener);
					r.setSize(new Dimension(64, 64));
					r.setLocation(new Point(x * 64, (64 * 149) - y * 64));//Swapped for horizontal view
					r.setVisible(true);
					jPanel.add(r);
					
				}
			}
	
			System.out.println("INIT TILES 2");
			jPanel.revalidate();
			jPanel.repaint();
			System.out.println("INIT TILES DONE");
		});
		
		t.start();
		
	}

}
