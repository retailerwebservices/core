package org.jimmutable.gcloud.pubsub.examples;

import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;

public class ExampleMessage extends StandardImmutableObject
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.examples.ExamplePubSubMessage"); public TypeName getTypeName() { return TYPE_NAME; }
	
	static public final FieldDefinition.Long FIELD_UPDATED_OBJECT_ID = new FieldDefinition.String("title",null);
}
