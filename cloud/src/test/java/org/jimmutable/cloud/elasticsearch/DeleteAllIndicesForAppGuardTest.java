package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertFalse;

import org.jimmutable.cloud.ApplicationId;
import org.junit.Test;

/**
 * The safety guard on {@link ISearch#deleteAllIndicesForApp(ApplicationId)} is the
 * only thing standing between a mis-configured test run and a wiped cluster, so it
 * gets a plain unit test of its own.
 *
 * The application id check is the half that can be asserted deterministically --
 * it is evaluated before anything about the environment, so these cases hold no
 * matter what else has (or has not) started up in this JVM. The environment type
 * half is covered by DeleteAllIndicesForAppIT.
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
