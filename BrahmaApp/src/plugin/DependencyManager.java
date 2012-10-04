package plugin;

import java.util.ArrayList;
import java.util.HashMap;

public enum DependencyManager {
	INSTANCE;
	private HashMap<String, ArrayList<String>> dependencies = new HashMap<String, ArrayList<String>>();

	public boolean checkForPluginDependency(Plugin plugin_a, Plugin plugin_b) {
		Class<?> a = plugin_a.getClass();
		Class<?> b = plugin_b.getClass();
		if (a.isAssignableFrom(b)) {

		
				if (!this.dependencies.containsKey(plugin_b.getId())) {
					this.dependencies.put(plugin_b.getId(),
							new ArrayList<String>());

				}
				ArrayList<String> old_dependencies = this.dependencies
						.get(plugin_b.getId());
				old_dependencies.add(plugin_a.getId());
				this.dependencies.put(plugin_b.getId(), old_dependencies);

				return true;

			
			
		}

		return false;
	}

	public ArrayList<String> getSafeLoadOrder(String id) throws Exception {
		ArrayList<String> loadOrder = new ArrayList<String>();
		ArrayList<String> visited = new ArrayList<String>();
		ArrayList<String> temp = new ArrayList<String>();

		visited.add(id);

		if (this.dependencies.get(id) != null) {
			for (String s : this.dependencies.get(id)) {
				temp.addAll(visited);
				safeLoadOrderHelper(s, temp, loadOrder);
				if (!loadOrder.contains(s)) {
					loadOrder.add(s);
				}
				temp.clear();
			}
		}

		if (!loadOrder.contains(id)) {
			loadOrder.add(id);
		}

		return loadOrder;

	}

	private void safeLoadOrderHelper(String id, ArrayList<String> visited,
			ArrayList<String> loadOrder) throws Exception {

		ArrayList<String> temp = new ArrayList<String>();

		visited.add(id);

		if (this.dependencies.get(id) != null) {
			for (String s : this.dependencies.get(id)) {
				if (visited.contains(s)) {
					throw new Exception("Cyclic dependency detected");
				}
				temp.addAll(visited);
				safeLoadOrderHelper(s, temp, loadOrder);
				temp.clear();
			}
		}

		if (!loadOrder.contains(id)) {
			loadOrder.add(id);
		}
	}

	protected void setDependencies(HashMap<String, ArrayList<String>> d) {
		this.dependencies = d;
	}

	public void removePlugin(String id) {
		this.dependencies.remove(id);
		for (String pid : this.dependencies.keySet())
		{
			if(this.dependencies.get(pid).contains(id))
			{
				this.removePlugin(pid); //if a plugin is dependent on the one being removed, we also want to remove it and all the ones that it depends on
			}
		}
		
	}

}
