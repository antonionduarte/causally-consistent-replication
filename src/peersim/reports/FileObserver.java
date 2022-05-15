package peersim.reports;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.util.IncrementalFreq;
import peersim.util.IncrementalStats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public abstract class FileObserver implements Control {

	private static final String PAR_FILENAME = "filename";
	private static final String PAR_MULTIPLE_FILES = "multifile";
	private static final String PAR_TAG_TIME = "tagtime";
	private static final String PAR_SEPARATOR = "separator";

	private final String filename;
	private boolean multiFiles;
	private final boolean tagTime;
	private PrintStream out;
	protected String separator;

	public FileObserver(String prefix) {
		filename = Configuration.getString(prefix + "." + PAR_FILENAME, null);
		if (filename == null) {
			out = System.out;
		} else {
			multiFiles = Configuration.getBoolean(prefix + "." + PAR_MULTIPLE_FILES, false);
		}
		tagTime = Configuration.getBoolean(prefix + "." + PAR_TAG_TIME, false);
		separator = Configuration.getString(prefix + "." + PAR_SEPARATOR, " ");
		if (!multiFiles) {
			try {
				out = new PrintStream(new File(filename + ".txt"));
			} catch (FileNotFoundException e) {
				out = System.out;
			}
		}
	}

	public void startObservation() {
		if (this.multiFiles) {
			try {
				out = new PrintStream(new File(filename + "-" + CommonState.getTime() + ".txt"));
			} catch (FileNotFoundException e) {
				out = System.out;
			}
		}
	}

	public void output(String s) {
		if (this.tagTime) {
			this.out.print(CommonState.getTime() + this.separator);
		}
		this.out.println(s);
	}

	public void output(IncrementalStats is) {
		if (this.tagTime) {
			this.out.print(CommonState.getTime() + this.separator);
		}
		this.out.println(is.toString(separator));
	}

	public void output(IncrementalFreq stats) {
		if (this.tagTime) {
			this.out.println(CommonState.getTime() + this.separator);
		}
		this.out.println(stats.toString(separator));
	}

	public void outputNoLine(String s) {
		this.out.print(s);
	}

	public void outputEndLine() {
		this.out.println();
	}

	public void stopObservation() {
		out.flush();
		if (this.multiFiles) {
			out.close();
		}
	}

}
