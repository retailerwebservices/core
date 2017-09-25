package org.jimmutable.cloud.simple_object_store.scan;

import org.jimmutable.cloud.s3.S3Path;
import org.jimmutable.core.objects.StandardObject;

import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * The scan operation sends it's resutls to an implementation of the ScanListener class
 * 
 * Implementations should take care to be thread safe (since multiple threads are used to scan objects)
 * 
 * @author jim.kane
 *
 */

public interface ScanListener 
{	
	/**
	 * Invoked whenever an object is listed.  Return true if the object should be loaded, false if the object should be skipped
	 * 
	 * @param scan The scan operation
	 * @param path The path of the object
	 * @param object_summary The full object summary of an S3 object that has been listed
	 * 
	 * @return True if the object should be loaded, false if the object should be skipped
	 */
	public boolean shouldLoadObject(OperationScan scan, S3Path path, S3ObjectSummary object_summary);
	
	/**
	 * Called after an object has been loaded from S3
	 * 
	 * @param scan The scan operation
	 * @param path The path the object was loaded from
	 * @param object_summary The full object summary of the S3 object this object was load from
	 * @param obj The object that was loaded
	 * 
	 */
	public void onLoadObject(OperationScan scan, S3Path path, S3ObjectSummary object_summary, StandardObject obj);

	/**
	 * Called when the scan, and all operations in the scan thread pool are
	 * complete
	 * 
	 * @param scan
	 *            The scan operation
	 */
	public void onScanComplete(OperationScan scan);
	
	/**
	 * This method is called every so often (approx 1/sec) and is typcially used
	 * to print out log statements that monitor the scanning process.
	 * 
	 * @param scan
	 *            The scan that is underway
	 */
	public void onScanHearbeat(OperationScan scan);
}
