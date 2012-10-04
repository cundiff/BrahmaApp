package plugin;

import static org.junit.Assert.*;

import javax.swing.JPanel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import plugin.DependencyManagerTests.TestPlugin1;

public class PluginCoreTest {

	PluginCore pc;
	Plugin a;
	Plugin b;
	
	public class TestPlugin1 extends Plugin{

		public TestPlugin1(String id) {
			super(id);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void layout(JPanel panel) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void start() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void stop() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public class TestPlugin2 extends TestPlugin1
	{

		public TestPlugin2(String id) {
			super(id);
			// TODO Auto-generated constructor stub
		}
		
	}

	@Before
	public void setUp() throws Exception {
		this.pc = new PluginCore();
		this.a = new TestPlugin1("a");
		this.b = new TestPlugin1("b");
	}

	@Test
	public void testAddPlugin() {
	
		Assert.assertTrue(pc.getAllPluginIds().isEmpty());
		
		pc.addPlugin(a);
		Assert.assertSame(a, pc.getPluginById("a"));
		pc.addPlugin(b);
		Assert.assertSame(b, pc.getPluginById("b"));
		
		Assert.assertEquals(2, pc.getAllPluginIds().size());
	}
	
	@Test
	public void testRemovePlugin() {
		pc.addPlugin(a);
		pc.addPlugin(b);
		
		pc.removePlugin("a");
		Assert.assertEquals(1, pc.getAllPluginIds().size());
		
		pc.removePlugin("b");		
		Assert.assertTrue(pc.getAllPluginIds().isEmpty());
			
		// Remove nonexistant
		pc.removePlugin("c");
		
		// Dependency cases checked in DependencyManagerTests
	}

}
