package org.pardus.manager.helper.ssh;

import java.io.IOException;

import com.jcraft.jsch.JSchException;

public class HostNameRequest extends SSHRequestBase {

	public HostNameRequest(String user, String host, String password) {
		super(user, host, password);
	}

	public HostNameRequest(String user, String host, String password, int timeOut) {
		super(user, host, password, timeOut);
	}

	public String ReadHostName() throws JSchException, IOException {
		return exec("cat /etc/hostname", true);
	}
}
