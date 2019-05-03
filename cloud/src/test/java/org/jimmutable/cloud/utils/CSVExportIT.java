package org.jimmutable.cloud.utils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.IntegrationTest;
import org.jimmutable.cloud.objects.StandardChangeLogEntry;
import org.jimmutable.cloud.servlet_utils.search.RequestExportCSV;
import org.jimmutable.cloud.servlet_utils.search.SearchFieldId;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.objects.common.ObjectReference;
import org.jimmutable.core.serialization.Format;
import org.junit.BeforeClass;
import org.junit.Test;

public class CSVExportIT extends IntegrationTest
{
	@BeforeClass
	public static void setup()
	{
		setupEnvironment();

		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertIndex(StandardChangeLogEntry.INDEX_MAPPING);

		ObjectId id = new ObjectId("0000-0000-0000-0000");
		StandardChangeLogEntry standard_change_log_entry = new StandardChangeLogEntry(id, new ObjectReference(new Kind("thing"), id), 0, new ObjectId("0000-0000-0000-0001"), "Short",null, new FieldArrayList<ObjectId>(), null,null);


		for (int i = 0; i < 100; i++)
		{
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocumentAsync(standard_change_log_entry);
		}
		try
		{
			Thread.sleep(3000);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

	}

	@Test
	public void exportUsersToCSV()
	{

		Set<SearchFieldId> set = new HashSet<SearchFieldId>();

		set.add(new SearchFieldId(StandardChangeLogEntry.SEARCH_FIELD_ID.getSimpleFieldName().getSimpleName()));
		set.add(new SearchFieldId(StandardChangeLogEntry.SEARCH_FIELD_SUBJECT.getSimpleFieldName().getSimpleName()));

		RequestExportCSV request = new RequestExportCSV(StandardChangeLogEntry.INDEX_DEFINITION, true, "subject:thing", set);

		System.out.println(request.toJavaCode(Format.JSON_PRETTY_PRINT, "blaa"));

		Set<SearchFieldId> fields = new HashSet<>();

		File file = new File(System.getProperty("user.home") + "/jimmutable_dev/csv-export/exportUsersToCSV.csv");

		CSVExport.exportCSV(request, file);
	}

}
