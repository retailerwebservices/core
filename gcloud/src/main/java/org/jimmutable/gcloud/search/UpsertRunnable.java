package org.jimmutable.gcloud.search;

import java.util.logging.Logger;

import com.google.appengine.api.search.Index;

public class UpsertRunnable implements Runnable {

	private static Logger logger = Logger.getLogger(UpsertRunnable.class.getName());

	Indexable indexable;
	Index index;

	UpsertRunnable(Index index, Indexable indexable) {
		this.index = index;
		this.indexable = indexable;
	}

	public void run() {
		logger.info("Sleeping!!!!");

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Awake!!!!");
		// index.put(indexable.getDocument());
	}

}
