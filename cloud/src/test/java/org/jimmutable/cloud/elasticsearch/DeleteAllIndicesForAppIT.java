package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.IntegrationTest;
import org.jimmutable.core.objects.JimmutableBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for {@link ISearch#deleteAllIndicesForApp(ApplicationId)}.
 *
 * This drops every index belonging to the "integration" application, so
 * do not run it in the same JVM as ITs that expect their indices to survive.
 *
 * @author preston.mccumber
 */
public class DeleteAllIndicesForAppIT extends IntegrationTest
{
	private static final IndexDefinition FIRST_INDEX = new IndexDefinition("integration_deleteallfirst_v1");
	private static final IndexDefinition SECOND_INDEX = new IndexDefinition("integration_deleteallsecond_v1");
	private static final IndexDefinition OTHER_APP_INDEX = new IndexDefinition("someotherapp_deleteall_v1");

	private ElasticSearchRESTClient elastic_search;

	@BeforeClass
	public static void setup()
	{
		setupEnvironment();
	}

	@Before
	public void seedTestData()
	{
		elastic_search = new ElasticSearchRESTClient();

		assertTrue(elastic_search.upsertIndex(createIndexDefinition(FIRST_INDEX)));
		assertTrue(elastic_search.upsertIndex(createIndexDefinition(SECOND_INDEX)));
		assertTrue(elastic_search.upsertDocument(new MyIndexable(FIRST_INDEX, new SearchDocumentId("doc0"))));

		assertTrue(elastic_search.indexExists(FIRST_INDEX));
		assertTrue(elastic_search.indexExists(SECOND_INDEX));
	}

	@Test
	public void deletesEveryIndexBelongingToTheApplication()
	{
		assertTrue(elastic_search.deleteAllIndicesForApp(ISearch.INTEGRATION_TEST_APPLICATION_ID));

		assertFalse(elastic_search.indexExists(FIRST_INDEX));
		assertFalse(elastic_search.indexExists(SECOND_INDEX));
	}

	@Test
	public void succeedsWhenTheApplicationHasNoIndicesLeft()
	{
		assertTrue(elastic_search.deleteAllIndicesForApp(ISearch.INTEGRATION_TEST_APPLICATION_ID));

		// A wildcard that matches nothing is a clean environment, not a failure.
		assertTrue(elastic_search.deleteAllIndicesForApp(ISearch.INTEGRATION_TEST_APPLICATION_ID));
	}

	@Test
	public void leavesOtherApplicationsAlone()
	{
		// Deliberately left behind -- the guard will not let us delete an index that
		// does not belong to the integration app.
		assertTrue(elastic_search.upsertIndex(createIndexDefinition(OTHER_APP_INDEX)));

		assertTrue(elastic_search.deleteAllIndicesForApp(ISearch.INTEGRATION_TEST_APPLICATION_ID));

		assertFalse(elastic_search.indexExists(FIRST_INDEX));
		assertTrue(elastic_search.indexExists(OTHER_APP_INDEX));
	}

	@Test
	public void refusesToWipeAnApplicationOtherThanTheIntegrationTestApplication()
	{
		assertFalse(elastic_search.deleteAllIndicesForApp(new ApplicationId("someotherapp")));

		assertTrue(elastic_search.indexExists(FIRST_INDEX));
		assertTrue(elastic_search.indexExists(SECOND_INDEX));
	}

	/* Build after setupEnvironment() registers cloud type names. */
	private static SearchIndexDefinition createIndexDefinition( IndexDefinition index )
	{
		JimmutableBuilder b = new JimmutableBuilder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS, MyIndexable.theBoolean);
		b.add(SearchIndexDefinition.FIELD_FIELDS, MyIndexable.theText);
		b.add(SearchIndexDefinition.FIELD_FIELDS, MyIndexable.theAtom);
		b.add(SearchIndexDefinition.FIELD_FIELDS, MyIndexable.theDay);
		b.add(SearchIndexDefinition.FIELD_FIELDS, MyIndexable.theFloat);
		b.add(SearchIndexDefinition.FIELD_FIELDS, MyIndexable.theLong);
		b.add(SearchIndexDefinition.FIELD_FIELDS, MyIndexable.theTimestamp);
		b.add(SearchIndexDefinition.FIELD_FIELDS, MyIndexable.theTextArray);
		b.add(SearchIndexDefinition.FIELD_FIELDS, MyIndexable.theAtomArray);
		b.add(SearchIndexDefinition.FIELD_FIELDS, MyIndexable.theLongArray);
		b.add(SearchIndexDefinition.FIELD_FIELDS, MyIndexable.theFloatArray);
		b.add(SearchIndexDefinition.FIELD_FIELDS, MyIndexable.theBooleanArray);
		b.add(SearchIndexDefinition.FIELD_FIELDS, MyIndexable.theTimestampArray);

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, index);

		return (SearchIndexDefinition) b.create();
	}
}
