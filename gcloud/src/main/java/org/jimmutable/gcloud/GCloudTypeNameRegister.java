package org.jimmutable.gcloud;

import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.gcloud.examples.SearchExampleLibraryPatron;
import org.jimmutable.gcloud.pubsub.messages.StandardMessageOnUpsert;

public class GCloudTypeNameRegister {
	static public void registerAllTypes() {
		JimmutableTypeNameRegister.registerAllTypes();

		ObjectParseTree.registerTypeName(StandardMessageOnUpsert.class);
		ObjectParseTree.registerTypeName(SearchExampleLibraryPatron.class);
	}
}
