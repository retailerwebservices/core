package org.jimmutable.cloud;

import org.jimmutable.cloud.attachments.AttachmentMetaData;
import org.jimmutable.cloud.cache.CacheEvent;
import org.jimmutable.cloud.elasticsearch.SearchIndexDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.cloud.email.Email;
import org.jimmutable.cloud.messaging.StandardMessageOnUpsert;
import org.jimmutable.cloud.objects.StandardChangeLogEntry;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseError;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseOK;
import org.jimmutable.cloud.servlet_utils.get.GetResponseError;
import org.jimmutable.cloud.servlet_utils.get.GetResponseOK;
import org.jimmutable.cloud.servlet_utils.search.AdvancedSearchComboBoxChoice;
import org.jimmutable.cloud.servlet_utils.search.AdvancedSearchField;
import org.jimmutable.cloud.servlet_utils.search.IncludeFieldInView;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResult;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResultWithTyping;
import org.jimmutable.cloud.servlet_utils.search.RequestExportCSV;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseError;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.SearchUIData;
import org.jimmutable.cloud.servlet_utils.search.Sort;
import org.jimmutable.cloud.servlet_utils.search.SortBy;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.servlet_utils.upsert.UpsertResponseOK;
import org.jimmutable.cloud.servlet_utils.upsert.UpsertResponseValidationError;
import org.jimmutable.cloud.storage.StorageMetadata;
import org.jimmutable.cloud.tinyurl.TinyUrlResult;
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
		ObjectParseTree.registerTypeName(StandardMessageOnUpsert.class);

		ObjectParseTree.registerTypeName(GeneralResponseError.class);
		ObjectParseTree.registerTypeName(GetResponseError.class);
		ObjectParseTree.registerTypeName(SearchResponseError.class);
		ObjectParseTree.registerTypeName(UpsertResponseValidationError.class);

		ObjectParseTree.registerTypeName(StandardSearchRequest.class);
		ObjectParseTree.registerTypeName(OneSearchResult.class);
		ObjectParseTree.registerTypeName(OneSearchResultWithTyping.class);
		ObjectParseTree.registerTypeName(Sort.class);
		ObjectParseTree.registerTypeName(SortBy.class);

		ObjectParseTree.registerTypeName(SearchIndexFieldDefinition.class);
		ObjectParseTree.registerTypeName(SearchIndexDefinition.class);

		ObjectParseTree.registerTypeName(SearchUIData.class);
		ObjectParseTree.registerTypeName(AdvancedSearchField.class);
		ObjectParseTree.registerTypeName(AdvancedSearchComboBoxChoice.class);

		ObjectParseTree.registerTypeName(IncludeFieldInView.class);
		ObjectParseTree.registerTypeName(RequestExportCSV.class);

		ObjectParseTree.registerTypeName(StorageMetadata.class);

		ObjectParseTree.registerTypeName(StandardChangeLogEntry.class);
		ObjectParseTree.registerTypeName(AttachmentMetaData.class);
		ObjectParseTree.registerTypeName(TinyUrlResult.class);

		ObjectParseTree.registerTypeName(StandardMessageOnUpsert.class);

		ObjectParseTree.registerTypeName(Email.class);
		ObjectParseTree.registerTypeName(CacheEvent.class);
	}
}
