package org.pardus.manager;

import java.io.IOException;

import org.pardus.manager.helper.MessageBox;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainPanelController {
	@FXML
	private Pane root;
	@FXML
	BorderPane contentPanel;

	@FXML
	public void ActionCloseApplication() {

		if (MessageBox.Query("Uygulama kapatýlsýn mý?", "Program kapatýlacak")) {
//			Platform.exit();
			Stage st = (Stage) root.getScene().getWindow();
			st.close();

		}

	}

	void initPane(String path) {
		try {
			contentPanel.getChildren().clear();
			Node node = FXMLLoader.load(getClass().getResource(path));
//			contentPanel.getChildren().add(node);
			contentPanel.setCenter(node);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void GetOSInfo() {
		initPane("controls/UCOSVersion.fxml");
	}

	@FXML
	public void ChangeMachineName() {
		initPane("controls/UCMachineName.fxml");
	}

	@FXML
	public void ActionScanNetwork() {
		initPane("controls/UCScanNetwork.fxml");
	}

}
