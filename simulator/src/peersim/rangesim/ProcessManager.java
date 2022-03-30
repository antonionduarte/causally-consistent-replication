package peersim.rangesim;

import java.util.*;

/**
 * This thread is used to kill forked processes in the case of an abnormal
 * termination of the Java virtual machine (for example, due to a signal).
 */
public class ProcessManager extends Thread {

	/**
	 * The threads that must be killed
	 */
	private final List<ProcessHandler> threads;

	public ProcessManager() {
		threads = Collections.synchronizedList(new ArrayList<>());
	}

	public void addThread(ProcessHandler p) {
		threads.add(p);
	}

	/**
	 * Assumes that the process manager
	 */
	public void joinAll() {
		int i = 0;
		while (i < threads.size()) {
			try {
				threads.get(i).join();
				i++;
			} catch (InterruptedException e) {
			}
		}
	}


	/**
	 * Kill the child process.
	 */
	public void run() {
		System.err.println("Terminating simulation.");
		for (ProcessHandler thread : threads) {
			if (thread != null)
				thread.doStop();
		}
	}

}