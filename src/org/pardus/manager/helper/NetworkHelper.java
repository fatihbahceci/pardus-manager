package org.pardus.manager.helper;

import java.net.Socket;

public class NetworkHelper {
	public static boolean IsServerListening(String host, int port) {
		Socket s = null;
		try {
			s = new Socket(host, port);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (s != null)
				try {
					s.close();

				} catch (Exception e) {
				}
		}
	}

	public static boolean IsLinuxHost(String host) {
		return IsServerListening(host, 22);
	}
}
