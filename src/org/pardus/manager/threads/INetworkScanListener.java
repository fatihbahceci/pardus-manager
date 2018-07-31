package org.pardus.manager.threads;

import org.pardus.manager.model.NetworkItem;

public interface INetworkScanListener {
	void newItemAdded(NetworkItem item);
	void statusUpdated(int total, int current, String line);
}
