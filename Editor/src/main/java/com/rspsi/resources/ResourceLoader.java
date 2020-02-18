package com.rspsi.resources;

import com.google.common.collect.Lists;
import javafx.scene.image.Image;

import java.util.Collection;
import java.util.List;

public class ResourceLoader {
	
	private static ResourceLoader singleton;
	public static ResourceLoader getSingleton() {
		if(singleton == null)
			singleton = new ResourceLoader();
		return singleton;
	}
	
	private Image logo16, logo32, logo64, logo128;
	
	public ResourceLoader() {
		logo16 = new Image(getClass().getResource("/images/logo128.png").toString(), 16, 16, true, true, true);
		logo32 = new Image(getClass().getResource("/images/logo128.png").toString(), 32, 32, true, true, true);
		logo64 = new Image(getClass().getResource("/images/logo128.png").toString(), 64, 64, true, true, false);
		logo128 = new Image(getClass().getResource("/images/logo128.png").toString(), 128, 128, true, true, true);
	}

	public Image getLogo16() {
		return logo16;
	}

	public Image getLogo32() {
		return logo32;
	}

	public Image getLogo64() {
		return logo64;
	}

	public Image getLogo128() {
		return logo128;
	}


	public List<Image> getIcons() {
		return Lists.newArrayList(logo16, logo32, logo64, logo128);
	}
}
