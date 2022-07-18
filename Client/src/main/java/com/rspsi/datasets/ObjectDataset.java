package com.rspsi.datasets;

import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.loader.object.ObjectDefinitionLoader;
import com.rspsi.core.misc.Vector3;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObjectDataset {

	private int id;
	private Vector3 rotation;
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
		this.name = name;
	}

	public ObjectDataset(ObjectDataset copy, int zoom, Vector3 rotation) {
		this.id = copy.id;
		this.type = copy.type;
		this.name = copy.name;
		this.zoom = zoom;
		this.rotation = rotation;
	}


	public ObjectDefinition getDefinition() {
		return ObjectDefinitionLoader.lookup(id);
	}
	
	@Override
	public String toString() {
		return id + ": " + name;
	}
}
