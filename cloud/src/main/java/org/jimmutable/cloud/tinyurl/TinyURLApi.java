package org.jimmutable.cloud.tinyurl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResultWithTyping;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.Format;

/**
 * First, check to see if we have shrunk this URL before (if so, just return the
 * appropriate TinyURLResult) If not, then load the URL:
 * http://tinyurl.com/api-create.php?url=[URL The result contains the "tiny" url
 * Then create a TinyURLResult object, store, index and return it
 * 
 * @author andrew.towe
 *
 */

public class TinyURLApi
{
	private static final Logger logger = LoggerFactory.getLogger(TinyURLApi.class);
	
	private static final String tiny_url_master_string = "http://tinyurl.com/api-create.php?url=";

	public static TinyUrlResult tinyURLComplex( String url, TinyUrlResult default_value )
	{
		List<OneSearchResultWithTyping> result_response = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(TinyUrlResult.INDEX_DEFINITION, new StandardSearchRequest(String.format("%s:\"%s\"", TinyUrlResult.FIELD_URL.getSimpleFieldName().getSimpleName(), url), 10000, 0), null);

		if ( !result_response.isEmpty())
		{
			OneSearchResultWithTyping map = result_response.get(0);

			ObjectId id = new ObjectId(map.readAsAtom(TinyUrlResult.SEARCH_FIELD_ID.getSimpleFieldName(), null));
			try
			{
				String json = new String(CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(new ObjectIdStorageKey(TinyUrlResult.KIND, id, StorageKeyExtension.JSON), null), "UTF-8");

				TinyUrlResult entry = (TinyUrlResult) StandardObject.deserialize(json);

				return entry;

			}
			catch ( Exception e )
			{
				logger.error("Error getting tinyURL", e);
				return default_value;
			}
		}
		else
		{
			String tiny_url_lookup = tiny_url_master_string + url;
			BufferedReader reader;
			try
			{
				reader = new BufferedReader(new InputStreamReader(new URL(tiny_url_lookup).openStream()));
				String tiny_url = reader.readLine();
				TinyUrlResult tiny_url_result = new TinyUrlResult(ObjectId.createRandomId(), url, tiny_url);
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(tiny_url_result, Format.JSON_PRETTY_PRINT);
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocumentAsync(tiny_url_result);
				return tiny_url_result;
			}
			catch ( Exception e )
			{
				logger.error("Error with TinyURL", e);
				return default_value;
			}
		}
	}
}
