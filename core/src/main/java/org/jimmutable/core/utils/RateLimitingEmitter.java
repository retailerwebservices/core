package org.jimmutable.core.utils;

import java.util.Random;

/**
 * A rate limiting emitter is an emitter that passes objects from a source to a
 * sink at a maximum rate. The rate of emission may be *less* than the specified
 * rate (for example, if the source runs dry momentarily) but will never be
 * (substantially) faster than the rate specified.
 * 
 * @author jim.kane
 * 
 * @param <T> The type of object being emitted
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Token_bucket">Token Bucket Algorithm</a>
 */
public class RateLimitingEmitter<T> extends Thread
{
    /**
     * Number of "tokens" accumulated so far. Each emission spends 1.0 token.
     */
    private float tokens_in_bucket = 0.0f;
    
    /**
     * Target (and approximate maximum) number of emissions per second
     */
	private float rate_per_second = 100.0f;
	
    public float getRate() { return rate_per_second; }
    
	private Source<T> source = null;
	
	private Sink<T> sink = null;
	
	private boolean stopped = false;
	
	
	private RateLimitingEmitter(Source<T> source, Sink<T> sink, float rate_per_second)
	{
	    if (null == source) throw new NullPointerException();
        if (null == sink) throw new NullPointerException();
        
        if (rate_per_second < 0) throw new IllegalArgumentException("rate_per_second must be positive");
	    
        this.source = source;
		this.sink = new ThreadPoolSinkExecutor<T>(sink);
		this.rate_per_second = rate_per_second;
        
		// Designed to run near real time... make it the max priority possible
		setPriority(Thread.MAX_PRIORITY);
		setName(String.format("RateLimitingEmitter (%.2f/sec)", rate_per_second));
	}
	
    public void setRate(float new_rate_per_second)
    {
        rate_per_second = new_rate_per_second;
        setName(String.format("RateLimitingEmitter (%.2f/sec)", rate_per_second));
    }
    
    private void addTokensToBucket(long start, long end)
    {
        float elapsed_seconds = (end-start)/1000.0f;
        tokens_in_bucket += elapsed_seconds*rate_per_second;
        
        // No matter what the rate is we can hold at least one and change tokens in the bucket
        if ( tokens_in_bucket < 1.9f )
            return;
        
        // Don't allow more than rate_per_second tokens to accumulate (i.e. max burst of 1 sec)
        if ( tokens_in_bucket > rate_per_second )
        {
            tokens_in_bucket = rate_per_second;
        }
    }
    
    public void kill()
    {
        stopped = true;
    }
	
	public void run()
	{
		long last_time_point = System.currentTimeMillis();
		
		while (! stopped)
		{
		    try
		    {
		        /* It's not actually important that we sleep for only 10ms.
		         * This in effect tells the JVM scheduler that we are done and want
		         * to be woken back up "soon". This number could be larger (even much
		         * larger), but the effect would be to create a longer latency in
		         * the handling of new events from the source.
		         * This has been empirically tested by Jim and Jeff to have a negligible
		         * usage of system resources when not actually processing any requests.
		         */
	            try { sleep(10); } catch(InterruptedException e ) {}
	            
	            long new_time_point = System.currentTimeMillis();
	            
	            addTokensToBucket(last_time_point,new_time_point);
	            last_time_point = new_time_point;
	            
	            while ( tokens_in_bucket > 1.0f )
	            {
	                try
	                {
	                    // TODO How to protect against long-running getNext implementations? - Create several canonical Sources that are tested and wrap it a "Do Not Fuck With" comment
	                    T emitted = source.getNext(null);
	                    if ( emitted == null ) break;

	                    tokens_in_bucket -= 1.0f;
	                    sink.onEmit(emitted);
	                }
	                catch (Exception e)
	                {
	                    e.printStackTrace();
	                }
	            }
		    }
		    catch (Exception e)
		    {
		        e.printStackTrace();
		    }
		}
	}
	
	static public <T> RateLimitingEmitter<T> startEmitter(Source<T> source, Sink<T> sink, float rate)
	{
	    RateLimitingEmitter<T> emitter = new RateLimitingEmitter<T>(source, sink, rate);
	    emitter.start();
	    return emitter;
	}
	
	
	/**
	 * Test suite
	 */
	static private class Test
	{
	    static public class RateRecordingSink<T> implements Sink
	    {
	        private long last_print_time = System.currentTimeMillis();
	        
	        private int object_count = 0;
	        private float rate_per_sec = 0.0f;
	        private Sink<T> wrapped_sink;
	        
	        private String label_for_print = null;
	        
	        private RateRecordingSink()
	        {
	            this(null, null);
	        }
	        
	        
	        private RateRecordingSink(Sink<T> wrapped_sink, String label_for_print)
	        {
	            this.wrapped_sink = wrapped_sink;
	            this.label_for_print = label_for_print;
	        }
	        
	        public void onEmit(Object obj)
	        {
	            try
	            {
	                object_count++;
	                long cur_time = System.currentTimeMillis();
	                
	                if ( cur_time - last_print_time > 1000 )
	                {
	                    float elapsed_seconds = (float)(cur_time - last_print_time)/1000.0f;
	                    rate_per_sec = (float)object_count/elapsed_seconds;
	                    
	                    if ( label_for_print != null )
	                        System.out.println(label_for_print+": "+rate_per_sec+" / sec");
	                    
	                    object_count = 0;
	                    last_print_time = cur_time;
	                }
	            }
	            finally
	            {
	                if ( wrapped_sink != null )
	                    wrapped_sink.onEmit((T)obj);
	            }
	        }
	        
	        public float getCurrentRatePerSec() { return rate_per_sec; }
	    }
	    
	    static public class InfiniteTestSource implements Source<String>
	    {
	    		private Random rnd = new Random();
	    		
	        public String getNext(String value_if_currently_no_work) 
	        {
	            return ""+rnd.nextInt();
	        }
	    }
	    
	    static public void main(String args[]) throws Exception
	    {
	        RateLimitingEmitter<String> foo = new RateLimitingEmitter(new InfiniteTestSource(), new RateRecordingSink(null, "Recorded Rate"), 0.5f);
	        
	        while(true)
	        {
	            Thread.currentThread().sleep(10000);
	            foo.setRate(foo.getRate()*2);
	            System.out.println("Changed rate to "+foo.getRate()+"/sec");
	        }
	    }
	}
}
