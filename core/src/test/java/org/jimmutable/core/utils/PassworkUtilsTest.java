package org.jimmutable.core.utils;

import org.junit.Test;

import junit.framework.TestCase;

public class PassworkUtilsTest extends TestCase
{

	@Test
	public void testBasicFunctionality() throws Exception
	{
		String salted_hash = PasswordUtils.getSaltedHash("The Super Secret Password");
		assertTrue(salted_hash.contains("$"));
		assertTrue(PasswordUtils.checkPassword("The Super Secret Password", salted_hash));
		assertFalse(PasswordUtils.checkPassword("Not The Super Secret Password", salted_hash));
	}
	@Test
	public void testWierdPassword() throws Exception
	{
		//test $ specifically
		String salted_hash = PasswordUtils.getSaltedHash("The Super $ecret Password with a dollar sign");
		assertTrue(PasswordUtils.checkPassword("The Super $ecret Password with a dollar sign", salted_hash));
	
		//test all other weird characters
		salted_hash = PasswordUtils.getSaltedHash("~`!1@2#3$4%5^6&7*8(9)0_-+={[}]|;:<,>\\.\"\'?/");
		assertTrue(PasswordUtils.checkPassword("~`!1@2#3$4%5^6&7*8(9)0_-+={[}]|;:<,>\\.\"\'?/", salted_hash));
		
		salted_hash = PasswordUtils.getSaltedHash("~`!1@2#3$4%5^6&7*8(9)0_-+={[}]|;:<,>\\.\"\'?/");
		assertTrue(PasswordUtils.checkPassword("~`!1@2#3$4%5^6&7*8(9)0_-+={[}]|;:<,>\\.\"\'?/", salted_hash));
	}
}
