package com.rspsi.plugins.ui;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Consumer;

import com.google.common.collect.Lists;
import com.rspsi.ui.MainWindow;

public class ApplicationPluginLoader {
	
	private static int count = 0;
	
	private static ServiceLoader<ApplicationPlugin> serviceLoader;
	
	public static ServiceLoader<ApplicationPlugin> getServiceLoader(){
		if(serviceLoader == null)
			loadPlugins(MainWindow.getSingleton());
		return serviceLoader;
	}
	
	public static void loadPlugins(MainWindow window) {
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

        serviceLoader = ServiceLoader.load(ApplicationPlugin.class, urlClassLoader);
        forEach(plugin -> {
        	System.out.println("i");
        	plugin.initialize(window);
        	count++;
        });
        System.out.println("Loaded " + count + " application plugins!");
        try {
			urlClassLoader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}
	
	public static void forEach(Consumer<ApplicationPlugin> consumer) {
		ServiceLoader<ApplicationPlugin> serviceLoader = getServiceLoader();
		
		for(ApplicationPlugin plugin : serviceLoader) {
			consumer.accept(plugin);
		}
		
	}
}
