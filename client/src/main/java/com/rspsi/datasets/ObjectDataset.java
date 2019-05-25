package com.rspsi.datasets;

import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;

public class ObjectDataset {

	private int id;
	private int type;
	private int zoom = -1;
	private String name;
	private transient boolean root;
	
	public ObjectDataset() {
		
	}

	public ObjectDataset(int type) {
		id = -1;
		this.type = type;
		root = true;
	}

	public ObjectDataset(int id, int type, String name) {
		this.id = id;
		this.type = type;
		this.name = name + "[" + type + "]";
	}

	public ObjectDataset(ObjectDataset copy, int zoom) {
		this.id = copy.id;
		this.type = copy.type;
		this.name = copy.name;
		this.zoom = zoom;
	}

	public ObjectDefinition getDefinition() {
		return ObjectDefinitionLoader.lookup(id);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public boolean isRoot() {
		return root;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return id + ": " + name;
	}

	public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}
}
