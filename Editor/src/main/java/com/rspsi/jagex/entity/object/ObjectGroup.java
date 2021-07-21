package com.rspsi.jagex.entity.object;

import com.rspsi.jagex.map.object.DefaultWorldObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ObjectGroup {

	private final int objectId;
	private List<DefaultWorldObject> objects;

	public ObjectGroup(int objectId) {
		objects = new ArrayList<>();
		this.objectId = objectId;
	}

	public void addObject(DefaultWorldObject object) {
		if (objects.contains(object))
			return;
		objects.add(object);

	}

	public int getObjectId() {
		return objectId;
	}

	public List<DefaultWorldObject> getObjects() {
		return objects;
	}

	public void sort() {
		Collections.sort(objects, Comparator.comparingInt(DefaultWorldObject::getLocHash));

	}

}
