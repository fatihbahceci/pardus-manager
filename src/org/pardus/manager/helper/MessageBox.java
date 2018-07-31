package org.pardus.manager.helper;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class MessageBox {

	public static void Test() {
		new Alert(Alert.AlertType.ERROR, "Deneme Mesajý", ButtonType.OK, ButtonType.CANCEL).showAndWait();
	}

	public static void Dialog(String message, Alert.AlertType alertType, String... headers) {
		Alert alert = new Alert(alertType, message, ButtonType.OK);
		if (headers != null && headers.length > 0) {
			alert.setHeaderText(headers[0]);
			if (headers.length > 1)
				alert.setTitle(headers[1]);
		}
		alert.showAndWait();
	}

	public static boolean Query(String message, String... headers) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
		if (headers != null && headers.length > 0) {
			alert.setHeaderText(headers[0]);
			if (headers.length > 1)
				alert.setTitle(headers[1]);
		}
		return alert.showAndWait().get() == ButtonType.YES;
	}

	public static void Error(String message, String... headers) {
		Dialog(message, Alert.AlertType.ERROR, headers);
	}
}
