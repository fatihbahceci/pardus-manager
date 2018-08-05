package org.pardus.manager.controls;

import java.io.IOException;

import org.pardus.manager.controls.common.LoginResult;
import org.pardus.manager.controls.common.UCLoginFormController;
import org.pardus.manager.controls.remotemanagement.RMChangeHostNameController;
import org.pardus.manager.model.NetworkItem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UCManageClientController extends FlowPane {

	private NetworkItem item;
	private Stage s;

	// Event Listener on Button.onAction
	@FXML
	public void ACChangeHostName(ActionEvent event) {
		LoginResult lr = new UCLoginFormController().Login();
		if (lr.isModalResult()) {
			new RMChangeHostNameController(item , lr).ShowModal();
		}
	}

	public UCManageClientController(NetworkItem item) {
		this.item = item;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("UCManageClient.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void ShowModal() {
		s = new Stage();
		s.setTitle(String.format("Makineyi yönet (%s - %s)", item.getHostName(), item.getIpAddr()));
		Scene scene = new Scene(this, 800, 400);
		s.setScene(scene);
//		s.initOwner(this);
		s.initModality(Modality.APPLICATION_MODAL);
//		s.show();
		s.showAndWait();
	}

}
