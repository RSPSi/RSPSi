package com.rspsi.plugins;

import com.rspsi.jagex.Client;
import com.rspsi.jagex.net.ResourceResponse;

public interface ClientPlugin {
	
	void initializePlugin();
	void onGameLoaded(Client client) throws Exception;
	default void onResourceDelivered(ResourceResponse resource) {
		
	}

}
