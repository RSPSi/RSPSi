package com.jagex.entity.model;

public class MeshUtils {

	public static MeshRevision getRevision(byte[] data) {
		if (data[data.length - 1] == -3 && data[data.length - 2] == -1) {
			return MeshRevision.TYPE_3;
		} else if (data[data.length - 1] == -2 && data[data.length - 2] == -1) {
			return MeshRevision.TYPE_2;
		} else if (data[data.length - 1] == -1 && data[data.length - 2] == -1) {
			return MeshRevision.TYPE_1;
		} else {
			return MeshRevision.OLD_FORMAT;
		}
	}

}
