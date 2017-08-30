package org.jimmutable.gcloud.pubsub.messages;

import java.util.Objects;

import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

/**
 * A standard message used to indicate that an object has been "upserted" (newly
 * inserted or updated). By far the most frequently used standard message
 * 
 * @author kanej
 *
 */
public class StandardMessageOnUpsert extends StandardImmutableObject<StandardMessageOnUpsert> {
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.gcloud.StandardMessageOnUpsert");

	public TypeName getTypeName() {
		return TYPE_NAME;
	}

	static public final FieldDefinition.Stringable<Kind> FIELD_KIND = new FieldDefinition.Stringable("kind", null,
			Kind.CONVERTER);
	static public final FieldDefinition.Stringable<ObjectId> FIELD_OBJECT_ID = new FieldDefinition.Stringable(
			"object_id", null, ObjectId.CONVERTER);

	private Kind kind; // required
	private ObjectId object_id; // required

	public StandardMessageOnUpsert(ObjectParseTree t) {
		kind = t.getStringable(FIELD_KIND);
		object_id = t.getStringable(FIELD_OBJECT_ID);
	}

	public StandardMessageOnUpsert(Kind kind, ObjectId object_id) {
		this.kind = kind;
		this.object_id = object_id;

		complete();
	}

	public Kind getSimpleKind() {
		return kind;
	}

	public ObjectId getSimpleObjectId() {
		return object_id;
	}

	public void write(ObjectWriter writer) {
		writer.writeStringable(FIELD_KIND, getSimpleKind());
		writer.writeStringable(FIELD_OBJECT_ID, getSimpleObjectId());
	}

	public int compareTo(StandardMessageOnUpsert o) {
		int ret = Comparison.startCompare();

		ret = Comparison.continueCompare(ret, getSimpleKind(), o.getSimpleKind());
		ret = Comparison.continueCompare(ret, getSimpleObjectId(), o.getSimpleObjectId());

		return ret;
	}

	public void freeze() {
	}

	public void normalize() {
	}

	public void validate() {
		Validator.notNull(kind, object_id);
	}

	public int hashCode() {
		return Objects.hash(kind, object_id);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof StandardMessageOnUpsert))
			return false;

		StandardMessageOnUpsert other = (StandardMessageOnUpsert) obj;

		return compareTo(other) == 0;
	}
}
