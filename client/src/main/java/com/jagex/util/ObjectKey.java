package com.jagex.util;

public class ObjectKey {
	
	private int x, y;
	private int id;
	private int type;
	private int orientation;
	private boolean solid;
	private boolean interactive;
	
	public ObjectKey(int x, int y, int id, int type, int orientation, boolean solid, boolean interactive) {
		this.x = x;
		this.y = y;
		this.id = id;
		this.type = type;
		this.orientation = orientation;
		this.solid = solid;
		this.interactive = interactive;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public int getOrientation() {
		return orientation;
	}

	public boolean isSolid() {
		return solid;
	}

	public boolean isInteractive() {
		return interactive;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + (interactive ? 1231 : 1237);
		result = prime * result + orientation;
		result = prime * result + (solid ? 1231 : 1237);
		result = prime * result + type;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectKey other = (ObjectKey) obj;
		if (id != other.id)
			return false;
		if (interactive != other.interactive)
			return false;
		if (orientation != other.orientation)
			return false;
		if (solid != other.solid)
			return false;
		if (type != other.type)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	
	
	

}
