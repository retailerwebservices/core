package org.jimmutable.cloud.storage;

import java.util.concurrent.TimeUnit;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.objects.common.ObjectReference;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.junit.Test;

public class StandardImmutableObjectCacheTest
{
	
	@Test
	public void testConvenienceMethod() {
		TestStorable storable = new TestStorable(new ObjectId("0000-0000-0000-0000"));
		StandardImmutableObjectCache simple_cache = CloudExecutionEnvironment.getSimpleCurrent().getSimpleCache();
		simple_cache.put(new ObjectReference(storable.getSimpleKind(), storable.getSimpleObjectId()), storable);
		assert(simple_cache.has(storable)==true);
	}
	
	@Test
	public void testSize() {
		TestStorable storable = new TestStorable(new ObjectId("0000-0000-0000-0000"));
		StandardImmutableObjectCache simple_cache = CloudExecutionEnvironment.getSimpleCurrent().getSimpleCache();
		simple_cache.put(new ObjectReference(storable.getSimpleKind(), storable.getSimpleObjectId()), storable);
		for(int i = 0;i<100000;i++) {
			TestStorable not_what_we_are_looking_for = new TestStorable(ObjectId.createRandomId());
			simple_cache.put(new ObjectReference(new Kind("not-what-we-are-looking-for"),not_what_we_are_looking_for.getSimpleObjectId()),not_what_we_are_looking_for);
		}
		assert(simple_cache.has(storable)==false);
	}

	private class TestStorable extends StandardImmutableObject implements Storable{
		ObjectId id;
		public TestStorable(ObjectId id) {
			this.id = id;
		}

		@Override
		public Kind getSimpleKind()
		{
			return new Kind("test-kind");
		}

		@Override
		public ObjectId getSimpleObjectId()
		{
			return id;
		}

		@Override
		public String serialize( Format format )
		{
			return ObjectWriter.serialize(format, this);
		}

		@Override
		public int compareTo( Object o )
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public TypeName getTypeName()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void write( ObjectWriter writer )
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void freeze()
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void normalize()
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void validate()
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public int hashCode()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean equals( Object obj )
		{
			// TODO Auto-generated method stub
			return false;
		}
		
	}

}
