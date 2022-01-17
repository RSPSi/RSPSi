package com.rspsi.core.misc;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;

public class FixedHashMap<K, V> {
	
	private HashMap<K, V> map = Maps.newHashMap();
	private int maxSize;
	
	public FixedHashMap(int size) {
		this.maxSize = size;
	}
	
	public void put(K key, V value) {
		if(map.size() >= maxSize) {
			Set<Entry<K, V>> copy = map.entrySet();
			map.clear();
			boolean b = true;
			for(Entry<K, V> entry : copy) {
				if(b) {
					b = false;
					continue;
				}
				map.put(entry.getKey(), entry.getValue());
			}
		}
		map.put(key, value);
	}
	
	public V get(K key) {
		return map.get(key);
	}
	
	public boolean contains(K key) {
		return get(key) != null;
	}
	
	public void remove(K key) {
		map.remove(key);
	}
	
	public void remove(K key, V value) {
		map.remove(key, value);
	}
	
	public void clear() {
		map.clear();
	}
	

}
