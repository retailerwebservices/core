package org.jimmutable.cloud.servlets;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.JimmutableCloudTypeNameRegister;
import org.jimmutable.cloud.elasticsearch.IndexDefinition;
import org.jimmutable.cloud.objects.StandardChangeLogEntry;
import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.servlets.common.DoSearch;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.objects.common.FacebookId;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.objects.common.ObjectReference;
import org.jimmutable.core.objects.common.TimezoneID;
import org.jimmutable.core.objects.common.USDMonetaryAmount;
import org.jimmutable.core.serialization.Format;
import org.junit.Test;

public class DoSearchIT 
{
	DoSearch test = new DoSearch()
	{
		
		@Override
		protected IndexDefinition getSearchIndexDefinition()
		{
			return StandardChangeLogEntry.INDEX_DEFINITION;
		}
	};
	
	@Test
	public void testCheckForTimeNothingToAdd() {
		String s = test.checkForTimes("name:bob AND last:smith");
		assertEquals("name:bob AND last:smith", s);
	}
	
	@Test
	public void testCheckForSimpleTime() {
		String s = test.checkForTimes("scheduled_start:>2018-01-01 12:30");
		assertEquals("scheduled_start:>1514827800000", s);
	}
	
	@Test
	public void testCheckForSimpleTimeInclusive() {
		String s = test.checkForTimes("scheduled_start:>=2018-01-01 12:30");
		assertEquals("(scheduled_start:>1514827800000 OR scheduled_start:1514827800000)", s);
	}
	
	@Test
	public void testCheckForSimpleTimeRange() {
		String s = test.checkForTimes("scheduled_start:>2017-06-03 10:03 AND scheduled_stop:<2017-07-16 12:30");
		assertEquals("scheduled_start:>1496502180000 AND scheduled_stop:<1500226200000", s);
	}
	
	@Test
	public void testCheckForComplexTime() {
		String s = test.checkForTimes("scheduled_start:>2016-12-12 12:30 AND name:bob AND last:smith");
		assertEquals("scheduled_start:>1481563800000 AND name:bob AND last:smith", s);
	}
	
	@Test
	public void testSearchIntegrationTest() throws ParseException
	{
		
		try
		{
			CloudExecutionEnvironment.startupIntegrationTest(new ApplicationId("integration"));
		} catch (RuntimeException e)
		{

		}finally {			
			JimmutableCloudTypeNameRegister.registerAllTypes();
		}
		if (!CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexProperlyConfigured(StandardChangeLogEntry.INDEX_MAPPING))
		{
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertIndex(StandardChangeLogEntry.INDEX_MAPPING);
			try
			{
				Thread.sleep(2000);
			} catch (InterruptedException e)
			{
			}
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		

		String inputString = "2017-07-04 12:00";
		Date d2 =  formatter.parse(inputString);
		
		ObjectId id = new ObjectId("0000-0000-0000-0000");
		StandardChangeLogEntry standard_change_log_entry = new StandardChangeLogEntry(id, new ObjectReference(new Kind("thing"), id), 0, id, "Short",null, new FieldArrayList<ObjectId>(), null,null);

		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(standard_change_log_entry, Format.JSON);
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(standard_change_log_entry);

		ObjectId id2 = new ObjectId("0000-0000-0000-0001");
		StandardChangeLogEntry standard_change_log_entry2 = new StandardChangeLogEntry(id2, new ObjectReference(new Kind("thing"), id2), 0, id2, "Short",null, new FieldArrayList<ObjectId>(), null,null);

		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(standard_change_log_entry2, Format.JSON);
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(standard_change_log_entry2);

		
		String search_string = "short_description:Short";
		
		search_string = test.checkForTimes(search_string);//this is what we are really trying to check. 
	
		JSONServletResponse search = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(StandardChangeLogEntry.INDEX_DEFINITION, new StandardSearchRequest(search_string));
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().delete(standard_change_log_entry);
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().deleteDocument(StandardChangeLogEntry.INDEX_DEFINITION, standard_change_log_entry.getSimpleSearchDocumentId());
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().delete(standard_change_log_entry2);
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().deleteDocument(StandardChangeLogEntry.INDEX_DEFINITION, standard_change_log_entry2.getSimpleSearchDocumentId());
		
		assertEquals(2, ((SearchResponseOK)search).getSimpleResults().size());
		assertEquals("short_description:Short", search_string);
	}
}
