package org.jimmutable.cloud.simple_object_store.scan;

import org.jimmutable.cloud.s3.S3Path;
import org.jimmutable.cloud.simple_object_store.SimpleObjectStore;
import org.jimmutable.core.objects.TransientImmutableObject;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

public class ScanRequest 
{
	private S3Path root_path; // required
	private ScanListener listener; // required
	private int processing_threads; // required, >= 1
	private SimpleObjectStore store;
	
	public ScanRequest(SimpleObjectStore store, S3Path root_path, ScanListener listener, int processing_threads) 
	{ 
		Validator.notNull(store, root_path, listener);
		if ( processing_threads < 1 ) processing_threads = 1;
		
		this.store = store;
		this.root_path = root_path;
		this.listener = listener;
		this.processing_threads = processing_threads;
	}
	
	public SimpleObjectStore getSimpleObjectStore() { return store; }
	public S3Path getSimpleRootPath() { return root_path; }
	public ScanListener getSimpleScanListener() { return listener; }
	public int getSimpleProcessingThreadCount() { return processing_threads; }
}
