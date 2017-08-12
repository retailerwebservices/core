/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jimmutable.app_engine_example;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.SortExpression;
import com.google.appengine.api.search.SortOptions;

public class HelloAppEngine extends HttpServlet 
{
	private static final long serialVersionUID = -4907844515014487767L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)  throws IOException 
	{
		response.setContentType("text/plain");
		response.getWriter().println("Hello App Engine! Search page2!");

		String index_name = "dev-index";
	
		IndexSpec indexSpec = IndexSpec.newBuilder().setName(index_name).build();
		Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
		
		putDocument(index, "PA6-5000", "jim.kane@gmail.com", "shadofax@lotr.com", "digitalpanda.com", "the rain in spain stays mainly on the plains");
		putDocument(index, "PA6-5001", "aaron.thomas@gmail.com", "golem@lotr.com", "gmail.com", "now is a time when all good men come to the aid of their country");
		putDocument(index, "PA6-5002", "john.kennedy@gmail.com", "frodo@lotr.com", "gmail.com", "Ask not what your country can do for you, but what you can do for your country");
		
		 SortOptions sortOptions = SortOptions.newBuilder()
			      .addSortExpression(SortExpression.newBuilder()
			    		  .setExpression("domain")
			          .setDirection(SortExpression.SortDirection.ASCENDING)
			          .setDefaultValue(""))
			      .addSortExpression(SortExpression.newBuilder()
			    		  	  .setExpression("email")
				          .setDirection(SortExpression.SortDirection.DESCENDING)
				          .setDefaultValue(""))
			      .setLimit(1000)
			      .build();
		 
		 QueryOptions options = QueryOptions.newBuilder()
			      .setSortOptions(sortOptions)
			      .build();
		
		 Query query = Query.newBuilder().setOptions(options).build("shadofax");


		 Results<ScoredDocument> results = index.search(query);

		 for ( ScoredDocument d : results )
		 {
			 System.out.println(d.getId()+" -- "+d.getOnlyField("domain").getAtom()+" -- "+d.getOnlyField("email").getText());
		 }
	}
	
	static public void putDocument(Index index, String doc_id, String email, String email2, String domain, String content)
	{
		Document doc = Document.newBuilder()
				// Setting the document identifer is optional.
				// If omitted, the search service will create an identifier.
				.setId(doc_id.toLowerCase())
				.addField(Field.newBuilder().setName("content").setText(content))
				.addField(Field.newBuilder().setName("email").setText(email+" "+email2))
				.addField(Field.newBuilder().setName("domain").setAtom(domain))
				.build();
	
		index.put(doc);
	}
}
