package org.pardus.manager.model;

import com.google.gson.annotations.Expose;

public class NetworkItem {
	@Expose
	String hostName;
	@Expose
	String ipAddr;
	@Expose
	String OS;

	public String getOS() {
		return OS;
	}

	public NetworkItem setOS(String oS) {
		OS = oS;
		return this;
	}

	public NetworkItem(String ipAddr, String hostName) {
		this.hostName = hostName;
		this.ipAddr = ipAddr;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

}
