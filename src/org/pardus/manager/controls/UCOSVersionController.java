package org.pardus.manager.controls;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UCOSVersionController {

	@FXML
	Label lblOSName;
	@FXML
	Label lblOSVersion;

	@FXML
	protected void initialize() {
		lblOSName.setText(System.getProperty("os.name")/*.toLowerCase()*/);
		lblOSVersion.setText(System.getProperty("os.version"));

	}

}
