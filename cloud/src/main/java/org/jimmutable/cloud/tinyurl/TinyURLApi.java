package org.jimmutable.cloud.tinyurl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.StorageKeyExtension;
import org.jimmutable.core.fields.FieldMap;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.FieldName;
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
	private static final Logger logger = LogManager.getLogger(TinyURLApi.class);
	
	private static final String tiny_url_master_string = "http://tinyurl.com/api-create.php?url=";

	public static TinyUrlResult tinyURLComplex( String url, TinyUrlResult default_value )
	{
		JSONServletResponse result_response = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(TinyUrlResult.INDEX_DEFINITION, new StandardSearchRequest(String.format("%s:\"%s\"", TinyUrlResult.FIELD_URL.getSimpleFieldName().getSimpleName(), url), 10000, 0));

		if ( result_response instanceof SearchResponseOK &&((SearchResponseOK) result_response).getSimpleResults().size()>0)
		{
			FieldMap<FieldName, String> map = ((SearchResponseOK) result_response).getSimpleResults().get(0).getSimpleContents();

			ObjectId id = new ObjectId(map.get(TinyUrlResult.SEARCH_FIELD_ID.getSimpleFieldName()));
			try
			{
				String json = new String(CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(new ObjectIdStorageKey(TinyUrlResult.KIND, id, StorageKeyExtension.JSON), null), "UTF-8");

				TinyUrlResult entry = (TinyUrlResult) StandardObject.deserialize(json);

				return entry;

			}
			catch ( Exception e )
			{
				logger.error(e);
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
				logger.error(e);
				return default_value;
			}
		}
	}
}
