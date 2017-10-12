package org.jimmutable.cloud;

import org.jimmutable.cloud.elasticsearch.SearchIndexDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.cloud.messaging.StandardMessageOnUpsert;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseError;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseOK;
import org.jimmutable.cloud.servlet_utils.get.GetResponseError;
import org.jimmutable.cloud.servlet_utils.get.GetResponseOK;
import org.jimmutable.cloud.servlet_utils.search.AdvancedSearchField;
import org.jimmutable.cloud.servlet_utils.search.IncludeFieldInView;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResult;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseError;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.SearchUIData;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.servlet_utils.upsert.UpsertResponseOK;
import org.jimmutable.cloud.servlet_utils.upsert.UpsertResponseValidationError;
import org.jimmutable.core.serialization.reader.ObjectParseTree;

/**
 * Before a TypeName may be read, its corresponding class must be registered via
 * a call to ObjectParseTree.registerTypeName. To achieve this, main methods
 * will typically have their first statement be something like
 * MyPackageRegister.registerAllTypes();
 *
 * If your package depends on other packages (almost always the case) your
 * implementation of registerAllTypes() should include classes to the
 * TypeNameRegister of all packages on which you depend. (Don't worry, it is
 * safe to register one TypeName multiple times -- the last one simply wins)
 *
 * Every TypeNameRegister of cloud specific classes should call
 * JimmutableCloudTypeNameRegister.registerAllTypes().
 * 
 * @author Preston McCumber
 */
public class JimmutableCloudTypeNameRegister
{
	static public void registerAllTypes()
	{
		ObjectParseTree.registerTypeName(GeneralResponseOK.class);
		ObjectParseTree.registerTypeName(GetResponseOK.class);
		ObjectParseTree.registerTypeName(SearchResponseOK.class);
		ObjectParseTree.registerTypeName(UpsertResponseOK.class);
		
		ObjectParseTree.registerTypeName(GeneralResponseError.class);
		ObjectParseTree.registerTypeName(GetResponseError.class);
		ObjectParseTree.registerTypeName(SearchResponseError.class);
		ObjectParseTree.registerTypeName(UpsertResponseValidationError.class);
		
		ObjectParseTree.registerTypeName(StandardSearchRequest.class);
		ObjectParseTree.registerTypeName(OneSearchResult.class);
		
		ObjectParseTree.registerTypeName(SearchIndexFieldDefinition.class);
		ObjectParseTree.registerTypeName(SearchIndexDefinition.class);
		
		ObjectParseTree.registerTypeName(StandardMessageOnUpsert.class);
		ObjectParseTree.registerTypeName(SearchUIData.class);
		ObjectParseTree.registerTypeName(AdvancedSearchField.class);
		ObjectParseTree.registerTypeName(IncludeFieldInView.class);
	}
}
