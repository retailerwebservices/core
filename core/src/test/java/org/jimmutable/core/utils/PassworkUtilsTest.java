package org.jimmutable.core.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jimmutable.core.exceptions.ValidationException;
import org.junit.Test;

public class PassworkUtilsTest
{

	@Test
	public void testBasicFunctionality() throws Exception
	{
		String salted_hash = PasswordUtils.getSaltedHash("The Super Secret Password");
		assertTrue(salted_hash.contains("$"));
		assertTrue(PasswordUtils.passwordMatchesHash("The Super Secret Password", salted_hash));
		assertFalse(PasswordUtils.passwordMatchesHash("Not The Super Secret Password", salted_hash));
	}

	@Test
	public void testWierdPassword() throws Exception
	{
		// test $ specifically
		String salted_hash = PasswordUtils.getSaltedHash("The Super $ecret Password with a dollar sign");
		assertTrue(PasswordUtils.passwordMatchesHash("The Super $ecret Password with a dollar sign", salted_hash));

		// test all other weird characters
		salted_hash = PasswordUtils.getSaltedHash("~`!1@2#3$4%5^6&7*8(9)0_-+={[}]|;:<,>\\.\"\'?/");
		assertTrue(PasswordUtils.passwordMatchesHash("~`!1@2#3$4%5^6&7*8(9)0_-+={[}]|;:<,>\\.\"\'?/", salted_hash));

		salted_hash = PasswordUtils.getSaltedHash("~`!1@2#3$4%5^6&7*8(9)0_-+={[}]|;:<,>\\.\"\'?/");
		assertTrue(PasswordUtils.passwordMatchesHash("~`!1@2#3$4%5^6&7*8(9)0_-+={[}]|;:<,>\\.\"\'?/", salted_hash));

		// start of heading character
		char SOH = (char) 1;

		String more = "~`!1@2#3$4%5^6&7*8(9)0_-+={[}]|;:<,>\\.\"\'?/" + SOH;

		// non-printable char in hash
		salted_hash = PasswordUtils.getSaltedHash(more);
		assertTrue(PasswordUtils.passwordMatchesHash(more, salted_hash));

	}

	@Test(expected = ValidationException.class)
	public void nullParameterPasswordMatchesHash() throws Exception
	{
		PasswordUtils.passwordMatchesHash("password", null);
	}

	@Test(expected = ValidationException.class)
	public void nullParameterGetSaltedHash() throws Exception
	{
		PasswordUtils.getSaltedHash(null);
	}

	@Test(expected = ValidationException.class)
	public void emptyParameterPasswordMatchesHash() throws Exception
	{
		PasswordUtils.passwordMatchesHash("", "my_hash");
	}

	@Test(expected = ValidationException.class)
	public void emptyParameterGetSaltedHash() throws Exception
	{
		PasswordUtils.getSaltedHash("");
	}

}
