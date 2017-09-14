package org.jimmutable.cloud;

import org.jimmutable.cloud.elasticsearch.SearchIndexDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseError;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseOK;
import org.jimmutable.cloud.servlet_utils.get.GetResponseError;
import org.jimmutable.cloud.servlet_utils.get.GetResponseOK;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResult;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseError;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.servlet_utils.upsert.UpsertResponseOK;
import org.jimmutable.cloud.servlet_utils.upsert.UpsertResponseValidationError;
import org.jimmutable.core.serialization.reader.ObjectParseTree;

public class JimmutableCloudTypeNameRegister
{
	static public void registerAllTypes()
	{
		ObjectParseTree.registerTypeName(GeneralResponseError.class);
		ObjectParseTree.registerTypeName(GeneralResponseOK.class);
		ObjectParseTree.registerTypeName(GetResponseError.class);
		ObjectParseTree.registerTypeName(GetResponseOK.class);
		ObjectParseTree.registerTypeName(OneSearchResult.class);
		ObjectParseTree.registerTypeName(SearchIndexFieldDefinition.class);
		ObjectParseTree.registerTypeName(SearchIndexDefinition.class);
		ObjectParseTree.registerTypeName(SearchResponseError.class);
		ObjectParseTree.registerTypeName(SearchResponseOK.class);
		ObjectParseTree.registerTypeName(StandardSearchRequest.class);
		ObjectParseTree.registerTypeName(UpsertResponseOK.class);
		ObjectParseTree.registerTypeName(UpsertResponseValidationError.class);
	}
}
