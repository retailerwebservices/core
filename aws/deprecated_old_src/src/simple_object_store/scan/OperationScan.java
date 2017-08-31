package org.jimmutable.aws.simple_object_store.scan;

import org.apache.logging.log4j.core.Filter.Result;
import org.jimmutable.core.threading.OperationMonitor;
import org.jimmutable.core.threading.OperationPool;
import org.jimmutable.core.threading.OperationRunnable;

public class OperationScan extends OperationRunnable
{
	static public final int THREAD_COUNT = 100;
	
	// Request
	private ScanRequest request;
	
	// State data
	private OperationPool child_operations;
	
	// Statistics
	volatile int stats_objects_listed = 0;
	volatile int stats_objects_processed_by_listener = 0;
	
	public OperationScan(ScanRequest request)
	{
		this.request = request;
	}
	
	public ScanRequest getSimpleRequest() { return request; }
	public OperationPool getSimpleChildOperations() { return child_operations; }
	
	public int getSimpleStatsObjectsListed() { return stats_objects_listed; }
	public int getSimpleStatsObjectsProcessedByListener() { return stats_objects_processed_by_listener; }
	
	
	protected Result performOperation() throws Exception
	{
		if ( shouldStop() ) return Result.STOPPED;

		// Setup the child operations pool
		OperationList list_operation = new OperationList(this);
		
		child_operations = new OperationPool(list_operation, getSimpleRequest().getSimpleProcessingThreadCount()+1); // the +1 is for there to be thread for listing...
		
		Result result = OperationRunnable.executeWithMonitor(child_operations, 500, new ScanMonitor(), Result.ERROR);
		
		getSimpleRequest().getSimpleScanListener().onScanComplete(this);
		
		return result;
	}
	
	private class ScanMonitor implements OperationMonitor
	{
		public void onOperationMonitorHeartbeat(OperationRunnable runnable)
		{
			if ( !(runnable instanceof OperationScan) ) return;
			
			OperationScan scan_op = (OperationScan)runnable;
			scan_op.getSimpleRequest().getSimpleScanListener().onScanHearbeat(scan_op);
		}
		
	}
}