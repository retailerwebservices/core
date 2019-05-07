package org.jimmutable.cloud.storage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.IntegrationTest;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.junit.BeforeClass;
import org.junit.Test;

public class StandardImmutableObjectCacheIT extends IntegrationTest
{
	@BeforeClass
	public static void setup()
	{
		setupEnvironment();
		ObjectParseTree.registerTypeName(TestStorable.class);

	}

	@Test
	public void testConvenienceMethod()
	{
		TestStorable storable = new TestStorable(new ObjectId("0000-0000-0000-0000"));
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleCache().put(storable.getSimpleKind(), storable.getSimpleObjectId(), storable);
		assert (CloudExecutionEnvironment.getSimpleCurrent().getSimpleCache().has(storable) == true);
	}

	@Test
	public void testTime()
	{
		TestStorable storable = new TestStorable(new ObjectId("0000-0000-0000-0000"));
		StandardImmutableObjectCache simple_cache = new StandardImmutableObjectCache(CloudExecutionEnvironment.getSimpleCurrent().getSimpleCacheService(), CloudExecutionEnvironment.getSimpleCurrent().getSimpleEnvironmentType().getSimpleCode().toLowerCase(), TimeUnit.SECONDS.toMillis(20));
		simple_cache.put(storable.getSimpleKind(), storable.getSimpleObjectId(), storable);
		try
		{
			TimeUnit.SECONDS.sleep(21);
		}
		catch ( InterruptedException e )
		{
			fail();
		}
		assert (simple_cache.has(storable) == false);
	}

	@Test
	public void testIsExcluded()
	{
		TestStorable storable = new TestStorable(new ObjectId("0000-0000-0000-0000"));
		StandardImmutableObjectCache simple_cache = new StandardImmutableObjectCache(CloudExecutionEnvironment.getSimpleCurrent().getSimpleCacheService(), CloudExecutionEnvironment.getSimpleCurrent().getSimpleEnvironmentType().getSimpleCode().toLowerCase(), TimeUnit.SECONDS.toMillis(20));
		// CR - As mentioned in StandardImmutableObjectCache, I don't think isExcluded should be private. Therefore, you can 
		// test using the has or get methods. -PM
		assertFalse(simple_cache.isExcluded(simple_cache.createCacheKey(storable.getSimpleKind(),storable.getSimpleObjectId())));
		simple_cache.addExclusion(storable.getSimpleKind());
		assertTrue(simple_cache.isExcluded(simple_cache.createCacheKey(storable.getSimpleKind(),storable.getSimpleObjectId())));
		simple_cache.removeExclusion(storable.getSimpleKind());
		assertFalse(simple_cache.isExcluded(simple_cache.createCacheKey(storable.getSimpleKind(),storable.getSimpleObjectId())));
		
	}

	private class TestStorable extends StandardImmutableObject implements Storable
	{
		ObjectId id;

		public TestStorable( ObjectId id )
		{
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
			return new TypeName("test_storable");
		}

		@Override
		public void write( ObjectWriter writer )
		{
			writer.writeString(new FieldName("id"), id.getSimpleValue());

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
