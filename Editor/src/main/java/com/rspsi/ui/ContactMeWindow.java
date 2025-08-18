package com.rspsi.ui;

import java.awt.Desktop;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.rspsi.util.FXUtils;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ContactMeWindow extends Application {

	private Stage primaryStage;
	

    @FXML
    private Hyperlink pmLink;

    @FXML
    private Hyperlink visitSiteLink;
    
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/contactme.fxml"));
		loader.setController(this);
	    Parent root = loader.load();
		primaryStage.setTitle("Contact details");
		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.setAlwaysOnTop(true);
		primaryStage.setWidth(250);
		primaryStage.setScene(new Scene(root));
		FXUtils.centerStage(primaryStage);
		primaryStage.centerOnScreen();
		
		pmLink.setOnAction(evt -> {
			try {
				openWebpage(new URL("https://www.rune-server.ee/private.php?do=newpm&u=158291").toURI());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		visitSiteLink.setOnAction(evt -> {
			try {
				openWebpage(new URL("https://rspsi.com/").toURI());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		primaryStage.sizeToScene();
	}
	
	public static boolean openWebpage(URI uri) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(uri);
	            return true;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    return false;
	}

	public void showAndWait() {
		if(primaryStage.isShowing()) {
			primaryStage.toFront();
			return;
		}
		primaryStage.showAndWait();
	}

}
