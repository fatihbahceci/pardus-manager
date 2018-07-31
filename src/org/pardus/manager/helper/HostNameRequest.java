package org.pardus.manager.helper;

import java.io.IOException;
import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class HostNameRequest {

	private String user;
	private String host;
	private String password;
	private int timeOut;

	public HostNameRequest(String user, String host, String password) {
		this(user, host, password, 2000);
	}

	public HostNameRequest(String user, String host, String password, int timeOut) {

		this.user = user;
		this.host = host;
		this.password = password;
		this.timeOut = timeOut;
	}

	public String ReadHostName() throws JSchException, IOException {
//		long t = System.currentTimeMillis();
		Session session = null;
		Channel channel = null;
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setTimeout(timeOut);
			session.connect();
			channel = session.openChannel("exec");

//		 ((ChannelExec)channel).setCommand("sudo -S -p '' "+command);
			((ChannelExec) channel).setCommand("cat /etc/hostname");
			InputStream in = channel.getInputStream();
//	      OutputStream out=channel.getOutputStream();
			((ChannelExec) channel).setErrStream(System.err);
			channel.connect();
//	      out.write((sudo_pass+"\n").getBytes());
//	      out.flush();

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

		} finally {
			try {
				if (channel != null)
					channel.disconnect();
				if (session != null)
					session.disconnect();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
	}
}
