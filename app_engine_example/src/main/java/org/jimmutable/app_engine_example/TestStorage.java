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
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageOptions;

public class TestStorage extends HttpServlet 
{
/**
	 * 
	 */
	private static final long serialVersionUID = 1824578504701033445L;
	
	Bucket bucket;
	Storage storage = StorageOptions.getDefaultInstance().getService();
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)  throws IOException 
	{
		response.setContentType("text/plain");
		response.getWriter().println("Hello App Engine! Test Storage!");

		
		
		// Instantiates a client
		bucket = storage.create(BucketInfo.newBuilder(bucket_name)
				// See here for possible values: http://g.co/cloud/storage/docs/storage-classes
				.setStorageClass(StorageClass.REGIONAL)
				// Possible values: http://g.co/cloud/storage/docs/bucket-locations#location-mr
				.setLocation("us-east1").setVersioningEnabled(true).build());

		System.out.printf("Bucket %s created.%n", bucket.getName());
		
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
