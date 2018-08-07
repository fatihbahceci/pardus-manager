package org.pardus.manager.helper.ssh;

import org.pardus.manager.controls.common.LoginResult;

public class RMHelper {

	public static boolean isHasRootPrivileges(SSHRequestBase connection) {
		try {
			if ("0".equals(connection.exec("id -u", false))) {
				return true;
			} else {
				connection.exec("sudo -v", false);
				if (connection.getLastExitStatus() == 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean Login(String host, LoginResult login) {
		SSHRequestBase r = new SSHRequestBase(login.getUserName(), host, login.getPassword());
		try {
			r.connect();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		finally {
			r.disconnect();
		}
	}
}
