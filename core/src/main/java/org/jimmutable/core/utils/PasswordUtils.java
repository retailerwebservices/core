package org.jimmutable.core.utils;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;

/**
 * This is our class that we use to ensure that passwords are secure.
 * 
 * <br>
 * <b>Information about handling passwords</b><br>
 * 1) this will work with any java string. So ~`!@#$%^&*()-_=+{[}]:;|<,>.?/ ,
 * numbers, Lower and Upper Case Letters are all valid. 2) we need to make sure
 * that all characters that needed to be escaped (i.e. \,",') are escaped.
 * 
 * @author andrew.towe
 *
 */

public class PasswordUtils
{
	private static final int iterations = 20 * 1000;
	private static final int saltLen = 32;
	private static final int desiredKeyLen = 256;

	/**
	 * 
	 * Computes a salted PBKDF2 hash of given plain text password suitable for
	 * storing in a database. Empty passwords are not supported. If hashing fails
	 * the default_value is returned.
	 * 
	 * @param password
	 *            plain test password String
	 * @return the hash value of the plain test password or default_value if hashing
	 *         fails
	 * @throws Exception
	 */
	public static String getSaltedHash(String password, String default_value)
	{
		try
		{
			Validator.notNull(password);
			Validator.min(password.length(), 1);

			byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
			// store the salt with the password
			return Base64.encodeBase64String(salt) + "$" + hash(password, salt);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return default_value;
	}

	/**
	 * 
	 * Checks whether given plain text password corresponds to a stored salted hash
	 * of the password.
	 * 
	 * @param password
	 *            plain test password String
	 * @param password_hash
	 *            stored hash value String
	 * @return true if password matches the hash value, else false
	 * 
	 * @throws Exception
	 */
	public static boolean authenticated(String password, String password_hash)
	{
		try
		{
			Validator.notNull(password, password_hash);
			Validator.min(password.length(), 1);
			Validator.min(password_hash.length(), 1);

			String[] saltAndPass = password_hash.split("\\$");
			if (saltAndPass.length != 2)
			{
				throw new IllegalStateException("The stored password have the form 'salt$hash'");
			}
			String hashOfInput = hash(password, Base64.decodeBase64(saltAndPass[0]));
			return hashOfInput.equals(saltAndPass[1]);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	// using PBKDF2 from Sun, an alternative is https://github.com/wg/scrypt
	// cf. http://www.unlimitednovelty.com/2012/03/dont-use-bcrypt.html
	private static String hash(String password, byte[] salt) throws Exception
	{

		Validator.notNull(password, salt);
		Validator.min(password.length(), 1);

		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		SecretKey key = f.generateSecret(new PBEKeySpec(password.toCharArray(), salt, iterations, desiredKeyLen));
		return Base64.encodeBase64String(key.getEncoded());
	}
}
