package com.jagex.link;

/**
 * A HashTable that resolves collisions using separate chaining for the buckets.
 */
public final class HashTable {

	// Class1

	/**
	 * The amount of buckets in this HashTable.
	 */
	private int bucketCount;

	/**
	 * The buckets used by this HashTable.
	 */
	private Linkable[] buckets;

	/**
	 * Creates the HashTable with the specified size.
	 *
	 * @param size
	 *            The size.
	 */
	public HashTable(int size) {
		bucketCount = size;
		buckets = new Linkable[size];

		for (int index = 0; index < size; index++) {
			Linkable linkable = buckets[index] = new Linkable();
			linkable.setNext(linkable);
			linkable.setPrevious(linkable);
		}
	}

	/**
	 * Gets the {@link Linkable} with the specified {@code key} from this HashTable.
	 * 
	 * @param key
	 *            The key.
	 * @return The Linkable, or {@code null} if this HashTable does not contain an
	 *         associated for the specified key.
	 */
	public Linkable get(long key) {
		Linkable linkable = buckets[(int) (key & bucketCount - 1)];
		for (Linkable next = linkable.getNext(); next != linkable; next = next.getNext()) {
			if (next.getKey() == key)
				return next;
		}

		return null;
	}

	/**
	 * Associates the specified {@link Linkable} with the specified {@code key}.
	 * 
	 * @param key
	 *            The key.
	 * @param linkable
	 *            The Linkable.
	 */
	public void put(long key, Linkable linkable) {
		if (linkable.getPrevious() != null) {
			linkable.unlink();
		}

		Linkable current = buckets[(int) (key & bucketCount - 1)];
		linkable.setPrevious(current.getPrevious());
		linkable.setNext(current);
		linkable.getPrevious().setNext(linkable);
		linkable.getNext().setPrevious(linkable);
		linkable.setKey(key);
	}

}