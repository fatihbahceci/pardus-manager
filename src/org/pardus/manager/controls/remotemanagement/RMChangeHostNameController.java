package org.pardus.manager.controls.remotemanagement;

import java.io.IOException;

import org.pardus.manager.controls.common.LoginResult;
import org.pardus.manager.helper.MessageBox;
import org.pardus.manager.helper.ssh.RMHelper;
import org.pardus.manager.helper.ssh.SSHRequestBase;
import org.pardus.manager.model.NetworkItem;

import com.jcraft.jsch.JSchException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RMChangeHostNameController extends BorderPane {
	@FXML
	private Label lblCurrentName;
	@FXML
	private TextField tNewName;
	@FXML
	private TextArea tLog;
	private NetworkItem item;
	private LoginResult loginResult;

	private void log(String s) {
		if (tLog != null) {
			tLog.appendText(s + "\r\n");
		} else {
			System.out.println("tLog neden null? " + s);
		}
		System.out.println(s);
	}

	// Event Listener on Button.onAction
	@FXML
	public void ActionChangeMachineName(ActionEvent event) {

		SSHRequestBase r = new SSHRequestBase(loginResult.getUserName(), item.getIpAddr(), loginResult.getPassword());
		r.setAlwaysKeepConnectionAfterCommand(true);
		try {
			String newName = tNewName.getText();
			if (MessageBox.Query("Makine adý" + newName + " olarak deðiþtirilecek", "Makine adý deðiþsin mi?")) {
				if (RMHelper.isHasRootPrivileges(r)) {
					try {
						String s = r.exec("cat /etc/hostname");
						if (r.isSuccess()) {
							log("Mevcut makine adý:" + s);
//							s = r.exec("bash -c echo " + newName + " > /etc/hostname");
							s = r.exec("echo " + newName + " > /etc/hostname");
							log(s);
							if (r.isSuccess()) {
								s = r.exec("cat /etc/hostname").replace("\r", "").replace("\n", "");
								if (r.isSuccess()) {

									if (s.equals(newName)) {
										log("Hostname deðiþtirildi. Yeni host:" + newName);
										r.exec("sed -i 2d /etc/hosts");
										r.exec("sed -i \"1 a 127.0.0.1 " + newName + "\" /etc/hosts");
										MessageBox.Show("Hostname deðiþtirildi. Yeni host:" + newName);
									} else {
										log(String.format("Hostname deðiþtirilemedi.(%s)-(%s) ", s, newName));
									}
								} else {
									log("cat2 baþarýsýz: " + r.getLastExitStatus());
								}
							} else {
								log("echo baþarýsýz: " + r.getLastExitStatus());
							}
						} else {
							log("Makine adý alýnamadý. " + r.getLastExitStatus());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					MessageBox.Error("Bu iþlem için root kullanýcýsý olmalýsýnýz!", "Yetkisiz Eriþim");
				}

			}

		} catch (JSchException e) {
			e.printStackTrace();
		} finally {
			r.disconnect();
		}

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
