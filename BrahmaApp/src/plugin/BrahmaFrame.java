package plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class BrahmaFrame {
	private PluginCore core;
	
	private JFrame frame;
	private JPanel contentPane;
	private JList sideList;
	private JPanel centerEnvelope;
	private JLabel bottomLabel;
	private DefaultListModel<String> listModel;
	
	
	public BrahmaFrame(final PluginCore core)
	{
		this.core = core;
		
		// Lets create the elements that we will need
		frame = new JFrame("Pluggable Board Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		contentPane = (JPanel)frame.getContentPane();
		contentPane.setPreferredSize(new Dimension(700, 500));
		bottomLabel = new JLabel("No plugins registered yet!");
		
		listModel = new DefaultListModel<String>();
		sideList = new JList(listModel);
		sideList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sideList.setLayoutOrientation(JList.VERTICAL);
		JScrollPane scrollPane = new JScrollPane(sideList);
		scrollPane.setPreferredSize(new Dimension(100, 50));
		
		// Create center display area
		centerEnvelope = new JPanel(new BorderLayout());
		centerEnvelope.setBorder(BorderFactory.createLineBorder(Color.black, 5));
		
		// Lets lay them out, contentPane by default has BorderLayout as its layout manager
		contentPane.add(centerEnvelope, BorderLayout.CENTER);
		contentPane.add(scrollPane, BorderLayout.EAST);
		contentPane.add(bottomLabel, BorderLayout.SOUTH);
		
		for (String id : this.core.getAllPluginIds())
		{
			this.listModel.addElement(id);
		}
		
		// Add action listeners
		sideList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// If the list is still updating, return
				if(e.getValueIsAdjusting())
					return;
				
				// List has finalized selection, let's process further
				int index = sideList.getSelectedIndex();
				String id = listModel.elementAt(index);
				Plugin plugin = core.getPluginById(id);
				Plugin currentPlugin = core.getCurrentPlugin();
				
				if(plugin == null || plugin.equals(currentPlugin))
					return;
				
				 //Stop previously running plugin
				if(currentPlugin != null)
					currentPlugin.stop();
				
				// The newly selected plugin is our current plugin
				currentPlugin = plugin;
				
				// Clear previous working area
				centerEnvelope.removeAll();
				
				// Create new working area
				JPanel centerPanel = new JPanel();
				centerEnvelope.add(centerPanel, BorderLayout.CENTER); 
				
				// Ask plugin to layout the working area
				currentPlugin.layout(centerPanel);
				contentPane.revalidate();
				contentPane.repaint();
				
				// Start the plugin (after starting all the plugins it depends on)
				try {
					ArrayList<String> loadOrder = DependencyManager.INSTANCE.getSafeLoadOrder(currentPlugin.getId());
					for (String pid: loadOrder)
					{
						core.getPluginById(pid).start();	
					}
					bottomLabel.setText("The " + currentPlugin.getId() + " is running!");
				} catch (Exception e1) {
					//there was a cyclic dependency, so do nothing
					bottomLabel.setText("Cyclic plugin dependency detected. Unable to start selected plugin.");
				}
				
			}
		});	
	}
	
	public void show()
	{
		frame.pack();
		frame.setVisible(true);
	}
	
	public void hide()
	{
		frame.setVisible(false);
	}
	
	public void addPluginFromList(String id)
	{
		this.listModel.addElement(id);
		this.bottomLabel.setText("The " + id + " plugin has been recently added!");
	}
	
	public void removePluginFromList(String id)
	{
		this.listModel.removeElement(id);
		this.bottomLabel.setText("The " + id + " plugin has been recently removed!");
	}
	
}
