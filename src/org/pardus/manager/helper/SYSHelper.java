package org.pardus.manager.helper;

import java.io.IOException;

public class SYSHelper {
	public static String getAppFolder() {
		return System.getProperty("user.dir");
	}

	public static boolean isLinux() {
		return System.getProperty("os.name").toLowerCase().contains("linux");
	}

	public static boolean isLinuxRootUser() {
		try {
			ConsoleProcessResult r = Console.Execute("id", "-u");
			System.err.println(r.toString());
			return ("0".equals(r.firstResult()));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
