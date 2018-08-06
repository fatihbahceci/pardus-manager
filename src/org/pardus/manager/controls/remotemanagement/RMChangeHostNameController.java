package org.pardus.manager.controls.remotemanagement;

import java.io.IOException;

import org.pardus.manager.controls.common.LoginResult;
import org.pardus.manager.helper.ssh.SSHRequestBase;
import org.pardus.manager.model.NetworkItem;

import com.jcraft.jsch.JSchException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RMChangeHostNameController extends AnchorPane {
	@FXML
	private Label lblCurrentName;
	@FXML
	private TextField tNewName;
	private NetworkItem item;
	private LoginResult loginResult;

	// Event Listener on Button.onAction
	@FXML
	public void ActionChangeMachineName(ActionEvent event) {
		SSHRequestBase r = new SSHRequestBase(loginResult.getUserName(), item.getIpAddr(), loginResult.getPassword());
		try {
			String s = r.exec("su", false);
			System.out.println(s + " " + r.getLastExitStatus());
			
		} catch (JSchException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		r.disconnect();
	}

	public RMChangeHostNameController(NetworkItem item, LoginResult loginResult) {
		this.item = item;
		this.loginResult = loginResult;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("RMChangeHostName.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	Stage s;

	public void ShowModal() {
		lblCurrentName.setText(item.getHostName());

		s = new Stage();
		s.setTitle(String.format("Ýsim deðiþtir (%s - %s)", item.getHostName(), item.getIpAddr()));
		Scene scene = new Scene(this, 800, 400);
		s.setScene(scene);
//		s.initOwner(this);
		s.initModality(Modality.APPLICATION_MODAL);

//		s.show();
		s.showAndWait();
	}
}
