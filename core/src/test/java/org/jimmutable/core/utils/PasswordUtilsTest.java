package org.jimmutable.core.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PasswordUtilsTest
{

	@Test
	public void testBasicFunctionality() throws Exception
	{
		String salted_hash = PasswordUtils.getSaltedHash("The Super Secret Password", null);
		assertTrue(salted_hash.contains("$"));
		assertTrue(PasswordUtils.authenticated("The Super Secret Password", salted_hash));
		assertFalse(PasswordUtils.authenticated("Not The Super Secret Password", salted_hash));
	}

	@Test
	public void testWierdPassword() throws Exception
	{
		// test $ specifically
		String salted_hash = PasswordUtils.getSaltedHash("The Super $ecret Password with a dollar sign", null);

		assertTrue(PasswordUtils.authenticated("The Super $ecret Password with a dollar sign", salted_hash));

		// test all other weird characters
		salted_hash = PasswordUtils.getSaltedHash("~`!1@2#3$4%5^6&7*8(9)0_-+={[}]|;:<,>\\.\"\'?/", null);
		assertTrue(PasswordUtils.authenticated("~`!1@2#3$4%5^6&7*8(9)0_-+={[}]|;:<,>\\.\"\'?/", salted_hash));

		salted_hash = PasswordUtils.getSaltedHash("~`!1@2#3$4%5^6&7*8(9)0_-+={[}]|;:<,>\\.\"\'?/", null);
		assertTrue(PasswordUtils.authenticated("~`!1@2#3$4%5^6&7*8(9)0_-+={[}]|;:<,>\\.\"\'?/", salted_hash));

		// start of heading character
		char SOH = (char) 1;

		String more = "~`!1@2#3$4%5^6&7*8(9)0_-+={[}]|;:<,>\\.\"\'?/" + SOH;

		// non-printable char in hash
		salted_hash = PasswordUtils.getSaltedHash(more, null);
		assertTrue(PasswordUtils.authenticated(more, salted_hash));

	}

	@Test
	public void nullParameterPasswordMatchesHash()
	{
		assertFalse(PasswordUtils.authenticated("password", null));
	}

	@Test
	public void nullParameterGetSaltedHash() throws Exception
	{
		assertNull(PasswordUtils.getSaltedHash(null, null));
	}

	@Test
	public void emptyParameterPasswordMatchesHash()
	{
		assertFalse(PasswordUtils.authenticated("", "my_hash"));
	}

	@Test
	public void badHash()
	{
		assertFalse(PasswordUtils.authenticated("abc", "my_hash"));
	}

	@Test
	public void emptyParameterGetSaltedHash()
	{
		assertNull(PasswordUtils.getSaltedHash("", null));
	}

}
