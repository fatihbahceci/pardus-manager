package org.pardus.manager.controls.remotemanagement;

import java.io.IOException;

import org.pardus.manager.controls.common.LoginResult;
import org.pardus.manager.helper.Console;
import org.pardus.manager.helper.ConsoleProcessResult;
import org.pardus.manager.helper.MessageBox;
import org.pardus.manager.helper.SYSHelper;
import org.pardus.manager.helper.ssh.RMHelper;
import org.pardus.manager.helper.ssh.SSHRequestBase;
import org.pardus.manager.model.NetworkItem;

import com.jcraft.jsch.JSchException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
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

	private void log(String s) {
		System.out.println(s);
	}

	// Event Listener on Button.onAction
	@FXML
	public void ActionChangeMachineName(ActionEvent event) {

		SSHRequestBase r = new SSHRequestBase(loginResult.getUserName(), item.getIpAddr(), loginResult.getPassword());
		r.setAlwaysKeepConnectionAfterCommand(true);
		try {
			String newName = tNewName.getText();
			if (MessageBox.Query("Makine ad�" + newName + " olarak de�i�tirilecek", "Makine ad� de�i�sin mi?")) {

				if (RMHelper.isHasRootPrivileges(r)) {
					try {
						String s = r.exec("cat /etc/hostname");
						if (r.isSuccess()) {
							log("Mevcut makine ad�:" + s);
							s = r.exec("bash -c echo " + newName + " > /etc/hostname");
							log(s);
							if (r.isSuccess()) {
								s = r.exec("cat /etc/hostname");
								if (r.isSuccess()) {

									if (s.equals(newName)) {
										log("Hostname de�i�tirildi. Yeni host:" + newName);
										r.exec("sed -i 2d /etc/hosts");
										r.exec("sed -i 1 a 127.0.0.1 " + newName + "/etc/hosts");
									} else {
										log("Hostname de�i�tirilemedi. " + s);
									}
								} else {
									log("cat2 ba�ar�s�z: " + r.getLastExitStatus());
								}
							} else {
								log("echo ba�ar�s�z: " + r.getLastExitStatus());
							}
						} else {
							log("Makine ad� al�namad�. " + r.getLastExitStatus());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					MessageBox.Error("Bu i�lem i�in root kullan�c�s� olmal�s�n�z!", "Yetkisiz Eri�im");
				}

			}

		} catch (JSchException e) {
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
		s.setTitle(String.format("�sim de�i�tir (%s - %s)", item.getHostName(), item.getIpAddr()));
		Scene scene = new Scene(this, 800, 400);
		s.setScene(scene);
//		s.initOwner(this);
		s.initModality(Modality.APPLICATION_MODAL);

//		s.show();
		s.showAndWait();
	}
}
