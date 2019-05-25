package com.jagex.link;

public class Linkable {

	// Class30

	private long key;
	private Linkable next;
	private Linkable previous;

	public long getKey() {
		return key;
	}

	public Linkable getNext() {
		return next;
	}

	public Linkable getPrevious() {
		return previous;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public void setNext(Linkable next) {
		this.next = next;
	}

	public void setPrevious(Linkable previous) {
		this.previous = previous;
	}

	public void unlink() {
		if (previous == null || next == null)
			return;

		previous.next = next;
		next.previous = previous;
		next = null;
		previous = null;
	}

}