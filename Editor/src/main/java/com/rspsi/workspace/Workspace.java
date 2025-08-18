package com.rspsi.workspace;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rspsi.ui.ObjectPreviewWindow;
import com.rspsi.datasets.ObjectDataset;
import com.rspsi.core.misc.JsonUtil;

public class Workspace {

	private static final ObjectMapper JSON_MAPPER = JsonUtil.getDefaultMapper();
	private File workspacePath;
	
	public void load() {
		
		File objectSwatches = new File(workspacePath, "object.swatch");
		if(objectSwatches.exists()) {
			try {
				List<ObjectDataset> dataset = JSON_MAPPER.readValue(objectSwatches, new TypeReference<List<ObjectDataset>>() {});
				ObjectPreviewWindow.instance.loadToSwatches(dataset);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void save() {
		File objectSwatches = new File(workspacePath, "object.swatch");
		
	}
	
	

}
