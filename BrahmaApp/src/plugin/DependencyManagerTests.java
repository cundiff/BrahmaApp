package plugin;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DependencyManagerTests {
	
	private DependencyManager dm;

	@Before
	public void setUp() throws Exception {
		this.dm = DependencyManager.INSTANCE;
		
		
	}
	
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

	@After
	public void tearDown() throws Exception {
	}
	
	@Rule
	public ExpectedException e = ExpectedException.none();
	
	@Test
	public void TestThatDependenciesCanBeFound()
	{
		TestPlugin1 p1 = new TestPlugin1("super");
		TestPlugin2 p2 = new TestPlugin2("child");
		
		Assert.assertTrue(dm.checkForPluginDependency(p1,p2));
	}
	
	@Test
	public void testThatNoDependenciesReturnsBlankLoadOrder()
	{
		
	}
	
	@Test
	public void testThatDirectlyCyclicDependenciesThrowException() throws Exception
	{
		HashMap<String, ArrayList<String>> dependencies = new HashMap<String,ArrayList<String>>();

		ArrayList<String> a_deps = new ArrayList<String>();
		ArrayList<String> b_deps = new ArrayList<String>();
		
		a_deps.add("B");
		b_deps.add("A");
		
		dependencies.put("A", a_deps);
		dependencies.put("B", b_deps);
		
		dm.setDependencies(dependencies);
		e.expect(Exception.class);
		e.expectMessage("Cyclic dependency detected");
		dm.getSafeLoadOrder("A");
		
	}
	
	@Test
	public void testThatIndirectlyCyclicDependenciesThrowException() throws Exception
	{
		HashMap<String, ArrayList<String>> dependencies = new HashMap<String,ArrayList<String>>();

		ArrayList<String> a_deps = new ArrayList<String>();
		ArrayList<String> b_deps = new ArrayList<String>();
		ArrayList<String> c_deps = new ArrayList<String>();
		
		a_deps.add("B");
		b_deps.add("C");
		c_deps.add("B");
		
		dependencies.put("A", a_deps);
		dependencies.put("B", b_deps);
		dependencies.put("C", c_deps);
		
		dm.setDependencies(dependencies);
		e.expect(Exception.class);
		e.expectMessage("Cyclic dependency detected");
		dm.getSafeLoadOrder("A");
	}
	
	@Test
	public void testThatNonCyclicDependenciesReturnCorrectLoadOrder() throws Exception
	{
		
		
		HashMap<String, ArrayList<String>> dependencies = new HashMap<String,ArrayList<String>>();

		ArrayList<String> a_deps = new ArrayList<String>();
		ArrayList<String> b_deps = new ArrayList<String>();
		ArrayList<String> c_deps = new ArrayList<String>();
		
		a_deps.add("B");
		a_deps.add("C");
		a_deps.add("E");
		
		b_deps.add("E");
		b_deps.add("G");
		
		c_deps.add("B");
		c_deps.add("G");
		
		dependencies.put("A", a_deps);
		dependencies.put("B", b_deps);
		dependencies.put("C", c_deps);
		
		ArrayList<String> expectedResult = new ArrayList<String>(); //should return { E, G, B, C, A }
		expectedResult.add("E");
		expectedResult.add("G");
		expectedResult.add("B");
		expectedResult.add("C");
		expectedResult.add("A");
		
		dm.setDependencies(dependencies);

		Assert.assertEquals(expectedResult, dm.getSafeLoadOrder("A"));
	}
	
	@Test
	public void testRemovePlugin() 
	{
		HashMap<String, ArrayList<String>> dependencies = new HashMap<String,ArrayList<String>>();

		ArrayList<String> a_deps = new ArrayList<String>();
		ArrayList<String> b_deps = new ArrayList<String>();
	

		a_deps.add("C");
		a_deps.add("E");
		
		b_deps.add("E");
		b_deps.add("G");
		
		
		
		dependencies.put("A", a_deps);
		dependencies.put("B", b_deps);
		
		HashMap<String, ArrayList<String>> expectedResult = new HashMap<String,ArrayList<String>>();
		
		expectedResult.put("A", a_deps);
		
		dm.setDependencies(dependencies);
		dm.removePlugin("G");

		Assert.assertEquals(expectedResult, dependencies);
	}
	

}
