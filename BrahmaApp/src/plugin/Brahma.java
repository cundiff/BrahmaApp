package plugin;

public class Brahma {
	public static void main(String args[]) {
		PluginCore core = new PluginCore();
		BrahmaFrame frame = new BrahmaFrame(core);
		frame.show();
	}
}
