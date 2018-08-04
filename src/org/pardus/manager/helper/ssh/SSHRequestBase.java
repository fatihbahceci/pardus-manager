package org.pardus.manager.helper.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHRequestBase {

	private String user;
	private String host;
	private String password;
	private int timeOut;

	public SSHRequestBase(String user, String host, String password) {
		this(user, host, password, 2000);
	}

	public SSHRequestBase(String user, String host, String password, int timeOut) {

		this.user = user;
		this.host = host;
		this.password = password;
		this.timeOut = timeOut;
	}

	Session session = null;
	Channel channel = null;
	private InputStream in;
	private OutputStream out;

	private void connect() throws JSchException, IOException {
		JSch jsch = new JSch();
		session = jsch.getSession(user, host, 22);
		session.setPassword(password);
		session.setConfig("StrictHostKeyChecking", "no");
		session.setTimeout(timeOut);
		session.connect();
		channel = session.openChannel("exec");

		in = channel.getInputStream();
		out = channel.getOutputStream();
		((ChannelExec) channel).setErrStream(System.err);
		channel.connect();
	}

	private void executeCommand(String c) throws IOException {
//		((ChannelExec) channel).setCommand(c);
		// out.write((sudo_pass + "\n").getBytes());
		out.write((c + "\n").getBytes());
		out.flush();
	}

	private String read() throws IOException {
		StringBuilder sb = new StringBuilder();
		byte[] tmp = new byte[1024];
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if (i < 0)
					break;
				sb.append(new String(tmp, 0, i));
			}
			if (channel.isClosed()) {
				System.out.println("exit-status: " + channel.getExitStatus());
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (Exception ee) {
			}

		}
		return sb.toString();
	}

	void disconnect() {
		try {
			if (channel != null)
				channel.disconnect();
			if (session != null)
				session.disconnect();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public String Test() throws JSchException, IOException {
//		long t = System.currentTimeMillis();

		try {
			connect();
			executeCommand("cat /etc/hostname");
			return read();

		} finally {
			disconnect();
		}

	}
}
