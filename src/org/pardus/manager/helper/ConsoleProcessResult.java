package org.pardus.manager.helper;

import java.util.ArrayList;

public class ConsoleProcessResult {
	public ConsoleProcessResult() {
		result = new ArrayList<>();
		resultErr = new ArrayList<>();
		exitValue = -65535;
	}

	int exitValue;
	ArrayList<String> result;
	ArrayList<String> resultErr;

	public int getExitValue() {
		return exitValue;
	}

	public boolean isSuccess() {
		return exitValue == 0;
	}

	public void setExitValue(int exitValue) {
		this.exitValue = exitValue;
	}

	public ArrayList<String> getResult() {
		return result;
	}

	public void add(String line) {
		getResult().add(line);
	}

	public void addError(String line) {
		getResultErr().add(line);
	}

	public ArrayList<String> getResultErr() {
		return resultErr;
	}

	public String firstResult() {
		return (result.size() > 0) ? result.get(0) : null;
	}

	@Override
	public String toString() {
		return "ConsoleProcessResult [exitValue=" + exitValue + ", result=" + result + ", resultErr=" + resultErr + "]";
	}

}
