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

import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jimmutable.gcloud.logging.LogSupplier;
import org.jimmutable.gcloud.search.DocumentId;
import org.jimmutable.gcloud.search.IndexId;
import org.jimmutable.gcloud.search.IndexManager;

import com.google.appengine.api.search.Document;

public class IndexManagerExample extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3926568940872850785L;

	private static final Logger logger = Logger.getLogger(IndexManagerExample.class.getName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		int count = 0;
		while (count < 2) {
			count++;
			try {
				IndexManager.upsert(new Sentence("doc-no" + count, "hip hop"));
			} catch (InterruptedException e) {
				logger.severe(new LogSupplier(e));
			}
		}

		Document d = IndexManager.getComplex(new IndexId("MyIndexable"), new DocumentId("doc-no1"), null);

		if (d == null) {
			logger.info(new LogSupplier("getComplex returned null"));
		} else {
			logger.info(new LogSupplier(d.toString()));
		}

		count = 0;
		while (count < 2) {
			count++;
			try {
				IndexManager.delete(new Sentence("doc-no0" + count, "hip hop"));
			} catch (InterruptedException e) {
				logger.severe(new LogSupplier(e));
			}
		}

	}

}
