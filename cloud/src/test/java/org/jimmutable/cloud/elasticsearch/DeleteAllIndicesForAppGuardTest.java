package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertFalse;

import org.jimmutable.cloud.ApplicationId;
import org.junit.Test;

/**
 * Unit tests for the application-id portion of the bulk-delete safety guard.
 *
 * @author preston.mccumber
 */
public class DeleteAllIndicesForAppGuardTest
{
	@Test
	public void refusesNullApplicationId()
	{
		assertFalse(ElasticSearchRESTClient.isSafeToDeleteAllIndicesForApp(null));
	}

	@Test
	public void refusesApplicationsOtherThanTheIntegrationTestApplication()
	{
		assertFalse(ElasticSearchRESTClient.isSafeToDeleteAllIndicesForApp(new ApplicationId("adrocket")));
		assertFalse(ElasticSearchRESTClient.isSafeToDeleteAllIndicesForApp(new ApplicationId("production")));
		assertFalse(ElasticSearchRESTClient.isSafeToDeleteAllIndicesForApp(new ApplicationId("integrationtest")));
	}
}
