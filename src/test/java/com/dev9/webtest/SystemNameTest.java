package com.dev9.webtest;

import org.junit.Test;

import com.dev9.webtest.util.SystemName;

import static org.junit.Assert.*;

public class SystemNameTest {

	@Test
	public void testGetSystemName() {
		String result = SystemName.getSystemName();
		assertNotNull(result);
		assertTrue(result.length() > 8);
	}
}