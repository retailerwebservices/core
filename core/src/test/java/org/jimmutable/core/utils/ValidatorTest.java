package org.jimmutable.core.utils;

import org.jimmutable.core.utils.Validator.ValidCharacters;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ValidatorTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ValidatorTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ValidatorTest.class );
    }

    public void testContainsOnlyValidCharacters()
    {
    	testContainsOnlyValidChracters(true, "hello world", Validator.LOWERCASE_LETTERS, Validator.SPACE);
    	testContainsOnlyValidChracters(false, "Hello world", Validator.LOWERCASE_LETTERS, Validator.SPACE);
    
    	testContainsOnlyValidChracters(true, "Hello 199 world", Validator.LETTERS, Validator.NUMBERS, Validator.SPACE);
    	testContainsOnlyValidChracters(false, "Hello 199 world", Validator.NUMBERS, Validator.SPACE);
    	testContainsOnlyValidChracters(false, "Hello 199 world", Validator.LETTERS, Validator.NUMBERS);
    }
    
    public void testCharacterValidators()
    {
    	testCharacterValidator(Validator.LETTERS,new char[]{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'});
    	testCharacterValidator(Validator.UPPERCASE_LETTERS,new char[]{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'});
    	testCharacterValidator(Validator.LOWERCASE_LETTERS,new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'});
    	testCharacterValidator(Validator.NUMBERS,new char[]{'0','1','2','3','4','5','6','7','8','9'});
    	testCharacterValidator(Validator.COMMON_WHITESPACE,new char[]{' ','\n','\r','\t'});
    	testCharacterValidator(Validator.SPACE,new char[]{' '});
    	testCharacterValidator(Validator.DOT,new char[]{'.'});
    	testCharacterValidator(Validator.DASH,new char[]{'-'});
    	testCharacterValidator(Validator.UNDERSCORE,new char[]{'_'});
    	testCharacterValidator(Validator.FORWARD_SLASH,new char[]{'/'});
    	testCharacterValidator(Validator.BACKWARD_SLASH,new char[]{'\\'});
    	testCharacterValidator(Validator.COLON,new char[]{':'});
    }
    
    private void testContainsOnlyValidChracters(boolean should_be_valid, String str, ValidCharacters... allowed_characters)
    {
    	try
    	{
    		Validator.containsOnlyValidCharacters(str, allowed_characters);
    		
    		if ( !should_be_valid ) fail();
    	}
    	catch(Exception e)
    	{
    		if ( should_be_valid ) fail();
    	}
    }
    
    private void testCharacterValidator(Validator.ValidCharacters validator, char[] valid_chars)
    {
    	for ( int i = 0; i < 10_000; i++ )
    	{
    		char ch = (char)i;
    		
    		boolean should_cur_character_be_valid = false;
    		
    		for ( int j = 0; j < valid_chars.length; j++ )
    		{
    			if ( valid_chars[j] == ch ) 
    			{
    				should_cur_character_be_valid = true;
    				break;
    			}
    		}
    		
    		assertEquals(should_cur_character_be_valid, validator.isValid(ch));
    	}
    }
}
