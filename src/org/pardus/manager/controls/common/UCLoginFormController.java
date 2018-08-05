package org.pardus.manager.controls.common;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UCLoginFormController extends GridPane {
	@FXML
	private TextField tUserName;
	@FXML
	private PasswordField tPassword;

	public UCLoginFormController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("UCLoginForm.fxml"));
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

	public LoginResult Login() {
		loginClicked = false;
		s = new Stage();
		s.setTitle("Giriþ");
		Scene scene = new Scene(this, 800, 400);
		s.setScene(scene);
//		s.initOwner(this);
		s.initModality(Modality.APPLICATION_MODAL);

//		s.show();
		s.showAndWait();

		if (loginClicked) {
			return new LoginResult(tUserName.getText(), tPassword.getText());
		} else {
			return new LoginResult(false);
		}
	}

	boolean loginClicked;

	// Event Listener on Button.onAction
	@FXML
	public void ACLogin(ActionEvent event) {
		loginClicked = true;
		s.close();
	}

	// Event Listener on Button.onAction
	@FXML
	public void ACCancel(ActionEvent event) {
		loginClicked = false;
		s.close();
	}
}
