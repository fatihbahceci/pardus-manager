package org.pardus.manager.helper.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.pardus.manager.controls.common.StringTraceListener;

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

	/**
	 * {@link #exec(String)} zaten connect metodunu çaðýrýyor
	 * 
	 * @throws JSchException
	 * @throws IOException
	 */
	public void connect() throws JSchException, IOException {
		if (session == null || !session.isConnected()) {
			JSch jsch = new JSch();
			session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setTimeout(timeOut);
			session.connect();
		}

	}

	ChannelExec currentChannel;
	int lastExitStatus;
	boolean alwaysKeepConnectionAfterCommand = false;

	public boolean isAlwaysKeepConnectionAfterCommand() {
		return alwaysKeepConnectionAfterCommand;
	}

	public void setAlwaysKeepConnectionAfterCommand(boolean alwaysKeepConnectionAfterCommand) {
		this.alwaysKeepConnectionAfterCommand = alwaysKeepConnectionAfterCommand;
	}

	private void openNewChannel() throws JSchException, IOException {
		closeExistingChannel();
		currentChannel = (ChannelExec) session.openChannel("exec");
		in = currentChannel.getInputStream();
		currentChannel.setErrStream(new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				trace(b);

			}
		});
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
				sb.append(new String(tmp, 0, i , "UTF-8"));
			}
			if (currentChannel.isClosed()) {
				lastExitStatus = currentChannel.getExitStatus();
				trace("Exit: " + lastExitStatus);
				break;
			}
			try {
				Thread.sleep(300);
			} catch (Exception ee) {
			}

		}
		// Trim ifadesi doðru bir kara mý bilemedim. Belki dönen sonuç boþluk
		// içeriyordur. O yüzden trim ifadesini kaldýrdým.
		String s = sb.toString();// .trim();
		if (s.endsWith("\n")) {
			s = s.substring(0, s.length() - 1);
		}
		if (s.endsWith("\r")) {
			s = s.substring(0, s.length() - 1);
		}
		return s;
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

		return exec(command, !isAlwaysKeepConnectionAfterCommand());
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
		trace(command);
		try {
			connect();
			executeCommand(command);
			return read();

		} finally {
			if (disconnectAfterExec)
				disconnect();
		}
	}

	public boolean isSuccess() {

		return getLastExitStatus() == 0;

	}

	private final Set<StringTraceListener> traceListener = new CopyOnWriteArraySet<StringTraceListener>();

	public final void addTraceListener(final StringTraceListener listener) {
		traceListener.add(listener);
	}

	public final void removeTraceListener(final StringTraceListener listener) {
		traceListener.remove(listener);
	}

	private final void notifyListeners(String line) {
		for (StringTraceListener listener : traceListener) {
			listener.OnStringTrace(line);
		}
	}

	void trace(String s) {
		System.out.println(s);
		notifyListeners(s);
	}

	String traceBuff = "";

	void trace(int b) {
		System.out.write(b);
		if (b == 10) {
			trace("E:" + traceBuff);
			traceBuff = "";
		} else {
			traceBuff += (char) b;
		}

	}

}
