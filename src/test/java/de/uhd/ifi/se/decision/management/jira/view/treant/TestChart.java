package de.uhd.ifi.se.decision.management.jira.view.treant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

public class TestChart {

	private Chart chart;

	@Before
	public void setUp(){
		chart = new Chart();
	}

	@Test
	public void testExist() {
		assertNotNull(chart);
	}

	@Test
	public void testGetContainer(){
		assertEquals("#treant-container", chart.getContainer());
	}

	@Test
	public void testGetConnectors(){
		assertEquals(ImmutableMap.of("type", "straight"), chart.getConnectors());
	}

	@Test
	public void testGetRootOrientation(){
		assertEquals("NORTH", chart.getRootOrientation());
	}

	@Test
	public void testGetLevelSeparation(){
		assertEquals(30, chart.getLevelSeparation(), 0.0);
	}

	@Test
	public void testGetSiblingSeparation(){
		assertEquals(30, chart.getSiblingSeparation(), 0.0);
	}

	@Test
	public void testGetSubTreeSeparation(){
		assertEquals(30, chart.getSubTreeSeparation(), 0.0);
	}

	@Test
	public void testGetNode(){
		assertEquals(ImmutableMap.of("collapsable", true), chart.getNode());
	}
}