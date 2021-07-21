package com.rspsi.editor.workspace;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rspsi.editor.ObjectPreviewWindow;
import com.rspsi.editor.datasets.ObjectDataset;
import com.rspsi.misc.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
