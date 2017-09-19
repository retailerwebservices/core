package org.jimmutable.platform_test;

import static org.junit.Assert.*;

import org.junit.Test;

public class AppTest extends App
{

//	@Test
	public void testMain()
	{
		try{
			App.main(null);
			assertTrue( true );
		} catch (Exception e) {
			e.printStackTrace();
			assert(false);
		}
	}

}
