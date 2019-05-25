package com.rspsi.plugins;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

public class ClientPluginLoader {
	
	private static int count = 0;
	
	private static ServiceLoader<ClientPlugin> serviceLoader;
	
	public static ServiceLoader<ClientPlugin> getServiceLoader(){
		if(serviceLoader == null)
			loadPlugins();
		return serviceLoader;
	}
	
	
	
	public static void loadPlugins() {
		File pluginPath = new File("plugins" + File.separator + "active");
		System.out.println("Plugin folder contains " + pluginPath.listFiles().length + " files.");
		File[] plugins = pluginPath.listFiles((File dir, String name) -> name.endsWith(".jar"));
		
		List<URL> urls = Lists.newArrayList();
		
		for(File pluginFile : plugins) {
			
			try {
				URL url = pluginFile.toURI().toURL();
				urls.add(url);
				System.out.println("Added url " + url.toString());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
        URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[]{}), Thread.currentThread().getContextClassLoader());

        serviceLoader = ServiceLoader.load(ClientPlugin.class, urlClassLoader);
        forEach(plugin -> {
        	plugin.initializePlugin();
        	count++;
        });
        System.out.println("Loaded " + count + " client plugins!");
        try {
			urlClassLoader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}
	
	public static void forEach(Consumer<ClientPlugin> consumer) {
		ServiceLoader<ClientPlugin> serviceLoader = getServiceLoader();
		
		for(ClientPlugin plugin : serviceLoader) {
			consumer.accept(plugin);
		}
		
	}
}
