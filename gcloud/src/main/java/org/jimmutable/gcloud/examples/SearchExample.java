package org.jimmutable.gcloud.examples;

import java.util.HashSet;
import java.util.Set;

import org.jimmutable.gcloud.search.DocumentWriter;

public class SearchExample 
{
	static public void main(String args[])
	{
		System.out.println("Hello World");
		
		System.out.println(DocumentWriter.allSubstrings("abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMN", 1, 10, null).length());
		System.out.println(DocumentWriter.allSubstrings("abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMN", 1, 10, null));
	}
	
	
	
	
}
