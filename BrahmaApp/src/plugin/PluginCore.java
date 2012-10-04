package plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PluginCore {
	private HashMap<String, Plugin> idToPlugin;
	private Plugin currentPlugin;
	private DependencyManager dm;
	
	// Plugin manager
	PluginManager pluginManager;
	
	public PluginCore() {
		idToPlugin = new HashMap<String, Plugin>();
		dm = DependencyManager.INSTANCE;
		try 
		{
			this.pluginManager = new PluginManager(this);
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		Thread thread = new Thread(this.pluginManager);
		thread.start();
	}
	
	
	public void addPlugin(Plugin plugin) 
	{
		for (Plugin p: this.idToPlugin.values())
		{
			this.dm.checkForPluginDependency(plugin, p);
		}
		this.idToPlugin.put(plugin.getId(), plugin);
	}
	
	public void removePlugin(String id) 
	{
		Plugin plugin = this.idToPlugin.remove(id);
		try {
			ArrayList<String> dependencies = this.dm.getSafeLoadOrder(id);
			for (int i = dependencies.size() - 1; i >=0; i--)
			{
				this.idToPlugin.get(dependencies.get(i)).stop(); //stop the plugin and all plugins it depends on
			}
			this.dm.removePlugin(id);
		} catch (Exception e) {
			//won't get here, as no cycles will exist if the plugin was successfully started
		}
	
		
		
	}
	
	public Plugin getCurrentPlugin()
	{
		return currentPlugin;
	}
	
	public Plugin getPluginById(String id)
	{
		return this.idToPlugin.get(id);
	}


	public Set<String> getAllPluginIds() {
		return this.idToPlugin.keySet();
	}
}
