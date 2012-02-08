package com.dynacrongroup.webtest;

import org.junit.Test;

import com.dynacrongroup.webtest.SystemName;

import static org.junit.Assert.*;

public class SystemNameTest {

	@Test
	public void testGetSystemName() {
		String result = SystemName.getSystemName();
		assertNotNull(result);
		assertTrue(result.length() > 8);
	}
}