package org.pardus.manager.helper.ssh;

import java.io.IOException;
import java.io.InputStream;

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

	private InputStream in;

	private void connect() throws JSchException, IOException {
		JSch jsch = new JSch();
		session = jsch.getSession(user, host, 22);
		session.setPassword(password);
		session.setConfig("StrictHostKeyChecking", "no");
		session.setTimeout(timeOut);
		session.connect();

	}

	ChannelExec currentChannel;
	int lastExitStatus;

	private void openNewChannel() throws JSchException, IOException {
		closeExistingChannel();
		currentChannel = (ChannelExec) session.openChannel("exec");
		in = currentChannel.getInputStream();
		currentChannel.setErrStream(System.err);
	}

	private void closeExistingChannel() {
		try {
			if (currentChannel != null) {
				currentChannel.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void executeCommand(String c) throws IOException, JSchException {
		openNewChannel();
		currentChannel.setCommand(c);
		currentChannel.connect();
		// out.write((sudo_pass + "\n").getBytes());
		// out.flush();

	}

	public int getLastExitStatus() {
		return lastExitStatus;
	}

	private String read() throws IOException, JSchException {
		StringBuilder sb = new StringBuilder();
		byte[] tmp = new byte[1024];
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if (i < 0)
					break;
				sb.append(new String(tmp, 0, i));
			}
			if (currentChannel.isClosed()) {
				lastExitStatus = currentChannel.getExitStatus();
				System.out.println("exit-status: " + lastExitStatus);
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (Exception ee) {
			}

		}
		return sb.toString();
	}

	public void disconnect() {
		try {
			closeExistingChannel();
			if (session != null)
				session.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param command
	 * @return
	 * @throws JSchException
	 * @throws IOException
	 */
	public String exec(String command) throws JSchException, IOException {

		return exec(command, true);
	}

	/**
	 * 
	 * @param command
	 * @param disconnectAfterExec
	 * @return
	 * @throws JSchException
	 * @throws IOException
	 */
	public String exec(String command, boolean disconnectAfterExec) throws JSchException, IOException {

		try {
			connect();
			executeCommand(command);
			return read();

		} finally {
			if (disconnectAfterExec)
				disconnect();
		}
	}

}
