/**
 * 
 */
package cerberus.data.collection.thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;

/**
 * Read and write locks for collections.
 *  
 * @author kalkusch
 *
 */
public class CollectionReadWriteLock implements CollectionLock {

	protected Lock readLock = null;
	
	protected Lock writeLock = null;	
	
	//protected Condition conReaderCounter;
	
	protected Condition conReaderPending;
	
	//protected Condition conWriteInProgress;
	
	protected Condition conWritePending;
	
	
	protected AtomicBoolean abReaderIsPermitted;
	
	protected AtomicBoolean abWriterIsPermitted;
	
	protected AtomicInteger aiReaderCounter;
	
	
	private long timeoutWaitForWriter = 100;
	
	private long timeoutWaitForReader = 100;
	
	private TimeUnit timeSlice = TimeUnit.MICROSECONDS;
	/**
	 * 
	 */
	public CollectionReadWriteLock() {
		
		createLocksAndConditions();
	}
	
	/**
	 * initialize
	 *
	 */
	private void createLocksAndConditions() {
		readLock = new ReentrantLock();
		writeLock = new ReentrantLock();
					
		//conReaderCounter = this.readLock.newCondition();
		conReaderPending = this.readLock.newCondition();	
		
		//conWriteInProgress = this.writeLock.newCondition();
		conWritePending = this.writeLock.newCondition();	
		
		abWriterIsPermitted = new AtomicBoolean(true);
		
		abReaderIsPermitted = new AtomicBoolean(true);
		aiReaderCounter = new AtomicInteger(0);
	}
	
	/* (non-Javadoc)
	 * @see prometheus.data.collection.thread.CollectionLock#getReadToken()
	 */
	public boolean getReadToken() {
		
		try {
			readLock.lock();			
						
			if ( abReaderIsPermitted.get() ) {
				if ( aiReaderCounter.getAndIncrement() == 0 ) {
					
				}
			}
			else {
				return false;
			}
		}
		finally {
			readLock.unlock();
		}		
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see prometheus.data.collection.thread.CollectionLock#getReadTokenWait()
	 */
	public boolean getReadTokenWait() {
		
		boolean result = true;
		try {
			readLock.lock();			
						
			if ( abReaderIsPermitted.get() ) {
				
				conReaderPending.await(timeoutWaitForWriter,timeSlice);
				
				if ( aiReaderCounter.getAndIncrement() == 0 ) {
					
				}
			}
			else {
				return false;
			}
		}
		catch (InterruptedException ie) {
			assert false: " error: " + ie.toString();
	
			result = false;
		}
		finally {
			readLock.unlock();
		}		
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see prometheus.data.collection.thread.CollectionLock#returnReadToken()
	 */
	public void returnReadToken() {
				
		try {
			readLock.lock();			
			if ( aiReaderCounter.decrementAndGet() < 1 ) {
				
				/**
				 * free write pending...
				 */
				
				try {
					writeLock.lock();
					conWritePending.signal();
					
					//conWritePending.signalAll();
				}
				finally{
					writeLock.unlock();
				}
				
			} // end: if ( aiReaderCounter.decrementAndGet() < 1 ) {
		}		
		finally {
			readLock.unlock();
		}		
	}
	
	/* (non-Javadoc)
	 * @see prometheus.data.collection.thread.CollectionLock#returnWriteToken()
	 */
	public void returnWriteToken() {
				
		try {
			writeLock.lock();			
										
			/**
			 * free write pending...
			 */
			abWriterIsPermitted.set( true );	
			
			/**
			 * notify pending writers...
			 */
			conWritePending.signal();	
		}		
		finally {
			writeLock.unlock();
		}		
		
		/**
		 * signal readers...
		 */
		setStateNewReaders( true );
		
		try {
			readLock.lock();		
			conReaderPending.signalAll();
		}
		finally {
			readLock.unlock();	
		}			
	}

	
	/**
	 * Blocks all new Readers by setting flag.
	 *
	 */
	private boolean setStateNewReaders( final boolean bSetState ) {
		final boolean bPriorState;
		
		try {
			readLock.lock();
			
			/**
			 * Block new readers...
			 */
			bPriorState = abReaderIsPermitted.getAndSet( bSetState );
		}
		finally {
			readLock.unlock();
		}	
		return bPriorState;
	}
	
	/* (non-Javadoc)
	 * @see prometheus.data.collection.thread.CollectionLock#getWriteToken()
	 */
	public boolean getWriteToken() {
						
		// TODO: Test behaviour of next line... 		 
		final boolean bPriorState = setStateNewReaders(false);
		
		boolean result = true;
		
		try {
			writeLock.lock();
			if ( ! abWriterIsPermitted.getAndSet(false) ) {
		
				/**
				 * abWriterIsPermitted was already false!
				 * Another writer is accessign the data.
				 */
				
				if ( conWritePending.await( timeoutWaitForReader, timeSlice) ) {
					/**
					 * only one writer...
					 */
					abWriterIsPermitted.set( false );
				}
				else {
					/**
					 * Could not grab token,
					 * undo...
					 */				
					result = false;
				} // end: if ( ! conWritePending.await( timeoutWaitForReader, timeSlice) ) {
				
			} // end: if ( ! abWriterIsPermitted.get() ) {

		}
		catch (InterruptedException ie) {
			assert false: " error: " + ie.toString();
	
			/**
			 * undo...
			 */	
			result = false;
		}
		finally{
			writeLock.unlock();
		}
		
		
		if ( ! result ) {
			/**
			 * Could not grab token,
			 * undo state..
			 */
			setStateNewReaders(bPriorState);
		}
	
		return result;
	}
	
	/**
	 * Part of factory design pattern interface, which is not usefull for this object.
	 * 
	 * @see prometheus.data.collection.thread.CollectionLock#getCollectionLock()
	 */
	public CollectionLock getCollectionLock() {
		assert false: "Get reference to 'this' object. Useless call.";
	
		return this;
	}
	
	/**
	 * Part of 'factory' design pattern, which is not usefull for this object.
	 * This call would lead to a clone of the setCollectionLock, which is not implemnted yet.
	 * 
	 * @see prometheus.data.collection.thread.CollectionLock#setCollectionLock(prometheus.data.collection.thread.CollectionLock)
	 */
	public boolean setCollectionLock( final CollectionLock setCollectionLock ) {
		assert false : "can not set a collection lock, bewcaus it would be a clone.";
	
		return false;
	}
}
