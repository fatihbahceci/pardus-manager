package org.pardus.manager.helper.ssh;

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

}
