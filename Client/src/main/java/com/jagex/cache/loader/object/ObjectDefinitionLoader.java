package com.jagex.cache.loader.object;

import com.jagex.Client;
import com.jagex.cache.config.VariableBits;
import com.jagex.cache.def.ObjectDefinition;
import com.jagex.cache.loader.IndexedLoaderBase;
import com.jagex.cache.loader.config.VariableBitLoader;

public abstract class ObjectDefinitionLoader implements IndexedLoaderBase<ObjectDefinition>{
	
	public ObjectDefinition morphism(int id) {
		ObjectDefinition def = lookup(id);
		int morphismIndex = -1;
		if (def.getVarbit() != -1) {
			VariableBits bits = VariableBitLoader.lookup(def.getVarbit());
			int variable = bits.getSetting();
			int low = bits.getLow();
			int high = bits.getHigh();
			int mask = Client.BIT_MASKS[high - low];
			morphismIndex = Client.getSingleton().settings[variable] >> low & mask;
		} else if (def.getVarp() != -1)
			morphismIndex = Client.getSingleton().settings[def.getVarp()];
		if (morphismIndex < 0 || morphismIndex >= def.getMorphisms().length || def.getMorphisms()[morphismIndex] == -1)
			return null;
		else
			return lookup(def.getMorphisms()[morphismIndex]);
	}
	
	public static ObjectDefinitionLoader instance;

	public static ObjectDefinition lookup(int id) {
		return instance.forId(id);
	}
	
	public static int getCount() {
		return instance.count();
	}
	
	public static ObjectDefinition getMorphism(int id) {
		return instance.morphism(id);
	}
	
	

}
