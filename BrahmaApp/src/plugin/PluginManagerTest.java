package plugin;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class PluginManagerTest {

	PluginCore pc;
	PluginManager pm;
	
	@Before
	public void setUp() throws Exception {
		pc = new PluginCore();
		pm = new PluginManager(pc);
	}

	@Test (expected = InvalidPathException.class)
	public void testLoadBundleInvalidPath() throws Exception {
		pm.loadBundle(FileSystems.getDefault().getPath(":"));
		
	}

	@Test
	public void testLoadBundle() throws Exception {
		Path pluginDir = FileSystems.getDefault().getPath("plugins");
		File pluginFolder = pluginDir.toFile();
		File[] files = pluginFolder.listFiles();
		if(files != null) {
			for(File f : files) {
				pm.loadBundle(f.toPath());
			}
		}
		Assert.assertEquals(files.length, pc.getAllPluginIds().size());
	}
	
	@Test
	public void testUnloadBundle() throws Exception {
		Path pluginDir = FileSystems.getDefault().getPath("plugins");
		File pluginFolder = pluginDir.toFile();
		File[] files = pluginFolder.listFiles();
		if(files != null) {
			for(File f : files) {
				pm.loadBundle(f.toPath());
			}
		}
		
		pm.unloadBundle(files[0].toPath());
		Assert.assertEquals(1, pc.getAllPluginIds().size());

		Assert.assertEquals("Personal Record", pc.getPluginById("Personal Record").getId());
	}
}
