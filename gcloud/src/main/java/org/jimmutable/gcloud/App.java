package org.jimmutable.gcloud;

import java.util.Date;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
    		String index_name = "dev-index";
    		
    		
    		String myDocId = "PA6-5000";
    		
    		Document doc = Document.newBuilder()
    		    // Setting the document identifer is optional.
    		    // If omitted, the search service will create an identifier.
    		    .setId(myDocId)
    		    .addField(Field.newBuilder().setName("content").setText("the rain in spain"))
    		    .addField(Field.newBuilder().setName("email").setText("jim.kane@gmail.com"))
    		    .addField(Field.newBuilder().setName("domain").setAtom("digitalpanda.com"))
    		    .addField(Field.newBuilder().setName("published").setDate(new Date()))
    		    .build();
    		
       
    		IndexSpec indexSpec = IndexSpec.newBuilder().setName(index_name).build();
        
    		Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
    		index.put(doc);
    		
    		System.out.println(index.getName());
    }
}
