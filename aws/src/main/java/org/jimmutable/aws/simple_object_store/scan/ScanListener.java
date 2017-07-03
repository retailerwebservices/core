package org.jimmutable.aws.simple_object_store.scan;

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
	 * Invoked whenever an object is listed.  Implementations must take care to be thread safe[
	 * 
	 * @param store The store being scanned
	 * @param path The path that has been listed
	 */
	public void processObject(OperationScan scan, S3ObjectSummary object_summary);

	/**
	 * Called when the scan, and all operations in the scan thread pool are
	 * complete
	 * 
	 * @param store
	 *            The store that the scan was run in
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
