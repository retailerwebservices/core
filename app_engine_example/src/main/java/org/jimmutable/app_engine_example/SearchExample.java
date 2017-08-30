package org.jimmutable.app_engine_example;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.gcloud.examples.SearchExampleLibraryPatron;
import org.jimmutable.gcloud.search.DocumentId;
import org.jimmutable.gcloud.search.IndexId;
import org.jimmutable.gcloud.search.IndexManager;
import org.jimmutable.gcloud.search.IndexQuery;
import org.jimmutable.gcloud.search.IndexQueryManager;
import org.jimmutable.gcloud.search.IndexQueryWriter;

public class SearchExample extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6893627755083611557L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		Builder builder = new Builder(SearchExampleLibraryPatron.TYPE_NAME);

		/* required */
		builder.set(SearchExampleLibraryPatron.FIELD_FIRST_NAME, "First");
		builder.set(SearchExampleLibraryPatron.FIELD_LAST_NAME, "Last");
		builder.set(SearchExampleLibraryPatron.FIELD_NUM_BOOKS, 0);
		builder.set(SearchExampleLibraryPatron.FIELD_DOCUMENT_ID, new DocumentId("TestLibraryPatron"));
		builder.set(SearchExampleLibraryPatron.FIELD_INDEX_ID, new IndexId("TestIndexId"));
		builder.set(SearchExampleLibraryPatron.FIELD_OBJECT_ID, new ObjectId(000000001));

		/* optional */
		builder.set(SearchExampleLibraryPatron.FIELD_EMAIL_ADDRESS, "email@address.com");
		builder.set(SearchExampleLibraryPatron.FIELD_SSN, "123121234");

		builder.set(SearchExampleLibraryPatron.FIELD_BIRTH_DATE, new Day("01/24/1990"));

		SearchExampleLibraryPatron patron1 = (SearchExampleLibraryPatron) builder.create(null);

		for (int i = 0; i < 10; i++) {

			try {
				IndexManager.upsert(patron1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		PrintWriter out = response.getWriter();

		out.println(IndexQueryManager.search(new GetAll()));

	}

	private class GetAll implements IndexQuery {

		private final IndexId index_id = new IndexId("TestIndexId");

		@Override
		public IndexId getSimpleIndexId() {
			return index_id;
		}

		@Override
		public void writeQuery(IndexQueryWriter writer) {
			writer.setQueryString(SearchExampleLibraryPatron.FIELD_FIRST_NAME.getSimpleFieldName().getSimpleName());
		}

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

	}

}
