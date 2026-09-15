package com.github.pascalos99.quad_assignment_backend.utils;

import java.time.Duration;
import java.time.Instant;

/**
 * This is an extremely crude implementation of a waiting queue.
 * Rate-limiting with an actual package intended for it is much advised 
 * in production-ready code. However, for this example, the following 
 * works sufficiently.<br>
 * <p> Note however, that this implementation does <b>not</b> rate limit
 * access to this API's end-points. It is only used to wait for access to
 * another API's end-point. It does so by queueing calls to the 
 * {@link #waitUntil(Instant)} function behind the mutex lock
 * tied to the {@link SynchronizedWaitingQueue} instance,
 * as described by the {@code synchronized} method call.
 */
public class SynchronizedWaitingQueue {
	/**
	 * Make the thread sleep until {@link Instant#now()} 
	 * exceeds the value passed in for {@code timeoutOver}.
	 * This is used to wait for the rate-limit on an external
	 * API to drop. Use exactly <b>one</b> {@link SynchronizedWaitingQueue}
	 * object per external rate-limited end-point.
	 * @param timeoutOver
	 */
	public synchronized void waitUntil(Instant timeoutOver) {
		Instant rightNow = Instant.now();
		if (timeoutOver.isAfter(rightNow)) {
			try {
				Thread.sleep(Duration.between(rightNow, timeoutOver));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
