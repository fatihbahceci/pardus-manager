package org.pardus.manager.threads;

public class NetworkScanParams {

	private boolean useSSH;
	private String SSHUserName;
	private String SSHUserPassword;
	private String IPRange;

	public NetworkScanParams(String iPRange) {
		this(false,null,null,iPRange);
	}

	public NetworkScanParams(boolean useSSH, String sSHUserName, String sSHUserPassword, String iPRange) {
		super();
		this.useSSH = useSSH;
		SSHUserName = sSHUserName;
		SSHUserPassword = sSHUserPassword;
		IPRange = iPRange;
	}

	public boolean isUseSSH() {
		return useSSH;
	}

	public void setUseSSH(boolean useSSH) {
		this.useSSH = useSSH;
	}

	public String getSSHUserName() {
		return SSHUserName;
	}

	public void setSSHUserName(String sSHUserName) {
		SSHUserName = sSHUserName;
	}

	public String getSSHUserPassword() {
		return SSHUserPassword;
	}

	public void setSSHUserPassword(String sSHUserPassword) {
		SSHUserPassword = sSHUserPassword;
	}

	public String getIPRange() {
		return IPRange;
	}

	public void setIPRange(String iPRange) {
		IPRange = iPRange;
	}

}
