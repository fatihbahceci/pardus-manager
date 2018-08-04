package org.pardus.manager.threads;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.pardus.manager.helper.NetworkHelper;
import org.pardus.manager.helper.ssh.HostNameRequest;
import org.pardus.manager.model.NetworkItem;

import javafx.fxml.FXML;

public class NetworkScanThread extends Thread {

	private NetworkScanParams params;

	public NetworkScanParams getParams() {
		return params;
	}

	public void setParams(NetworkScanParams params) {
		this.params = params;
	}

	public List<INetworkScanListener> getListeners() {
		return listeners;
	}

	public void setListeners(List<INetworkScanListener> listeners) {
		this.listeners = listeners;
	}

	private List<INetworkScanListener> listeners = new ArrayList<INetworkScanListener>();

	public synchronized void addListener(INetworkScanListener listener) {
		listeners.add(listener);
	}

	/**
	 * Özelleþtirilmiþ olayý yakalamak isteyen tüm nesneler burada uyarýlýyor.
	 */
	private synchronized void itemAdded(NetworkItem item) {

		for (INetworkScanListener listener : listeners) {
			listener.newItemAdded(item);
		}
	}

	private synchronized void statusUpdated(int total, int current, String line) {
		for (INetworkScanListener listener : listeners) {
			listener.statusUpdated(total, current, line);
		}
	}

	@Override
	public void run() {
		ACScanNetwork();

	}

	private NetworkItem getFromIp(String ipAddr) {
		try {
			InetAddress ip = InetAddress.getByName(ipAddr);
//			InetAddress ip = InetAddress.getByAddress( new byte[]{10, 16, 108, 114});
			if (ip.isReachable(500)) {
				boolean isLinux = NetworkHelper.IsLinuxHost(ipAddr);
				String hostName = null;
				if (isLinux && params.isUseSSH()) {
					System.out.println("Is linux");
					HostNameRequest r = new HostNameRequest(params.getSSHUserName(), ipAddr,
							params.getSSHUserPassword(), 1000);

					try {
						hostName = r.ReadHostName();
						System.out.println("SSH ile aldým:" + hostName);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				if (hostName == null || hostName.trim().isEmpty()) {
					hostName = ip.getHostName();
					System.out.println("Get Host ile aldým:" + hostName);
				}
				return new NetworkItem(ipAddr, hostName).setOS(isLinux ? "Linux" : "Unknown");
			}

		}

		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

//	private synchronized boolean is_alive = false;
//	public synchronized boolean isAlive() {
//		
//		return is_alive;
//	}

	private volatile boolean stop = false;
	private volatile boolean active = false;

	public synchronized boolean isActive() {
		return active;
	}

	public synchronized void abort() {
		stop = true;
	}

	// Event Listener on Button[#btnScan].onAction
	@FXML
	private void ACScanNetwork() {
		stop = false;
		active = true;
		try {
			String[] parts = params.getIPRange().split(",");
			// Birden fazla ip aralýðý var ise böl
			if (parts != null && parts.length > 0) {
				for (String range : parts) {
					// Ip tarama içinböl
					String[] ranges = range.split("-");
					if (ranges != null && ranges.length >= 1) {
						boolean toEnd = true;
						String range1 = ranges[0];
						String range2 = range1;
						// Eðer aralýk verilmemiþ ise ayný ip aralýðýný al.
						if (ranges.length > 1) {
							range2 = ranges[1];
							toEnd = false;
						}
						String[] r1Dim = range1.split("\\.");
						String[] r2Dim = range2.split("\\.");
						if (toEnd) {
							r2Dim[3] = "255";
						}
						int[][] levels = new int[4][2];
						levels[0][0] = Math.min(255, Math.max(0, Integer.parseInt(r1Dim[0])));
						levels[0][1] = Math.min(255, Math.max(0, Integer.parseInt(r2Dim[0])));

						levels[1][0] = Math.min(255, Math.max(0, Integer.parseInt(r1Dim[1])));
						levels[1][1] = Math.min(255, Math.max(0, Integer.parseInt(r2Dim[1])));

						levels[2][0] = Math.min(255, Math.max(0, Integer.parseInt(r1Dim[2])));
						levels[2][1] = Math.min(255, Math.max(0, Integer.parseInt(r2Dim[2])));

						levels[3][0] = Math.min(255, Math.max(0, Integer.parseInt(r1Dim[3])));
						levels[3][1] = Math.min(255, Math.max(0, Integer.parseInt(r2Dim[3])));
						int total = (levels[0][1] - levels[0][0] + 1) * (levels[1][1] - levels[1][0] + 1)
								* (levels[2][1] - levels[2][0] + 1) * (levels[3][1] - levels[3][0] + 1);
						int current = 0;
						NetworkItem addr = null;
						for (int level1 = levels[0][0]; level1 <= levels[0][1]; level1++) {
							for (int level2 = levels[1][0]; level2 <= levels[1][1]; level2++) {
								for (int level3 = levels[2][0]; level3 <= levels[2][1]; level3++) {
									for (int level4 = levels[3][0]; level4 <= levels[3][1]; level4++) {
										if (stop) {
											return;
										}
										String ip = String.format("%s.%s.%s.%s", level1, level2, level3, level4);
										current++;
										statusUpdated(total, current, ip);
										try {
											addr = getFromIp(ip);
										} catch (Exception e) {
											// TODO: handle exception
										}

										if (addr != null) {
											itemAdded(addr);
										}

										try {
											Thread.sleep(100);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}

									}
								}
							}
						}
					}
				}
			}
		} finally {
			if (stop) {
				System.err.println("Ýþlem kullanýcý tarafýndan iptal edildi!");
			}
			active = false;
			stop = false;
		}
	}

	public void waitForIsActive(boolean b, int i) {
		long current = System.currentTimeMillis();
		while (isActive() != b && System.currentTimeMillis() - current < i) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
