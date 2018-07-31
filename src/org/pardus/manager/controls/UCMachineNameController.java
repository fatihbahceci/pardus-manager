package org.pardus.manager.controls;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.pardus.manager.helper.Console;
import org.pardus.manager.helper.ConsoleProcessResult;
import org.pardus.manager.helper.MessageBox;
import org.pardus.manager.helper.SYSHelper;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class UCMachineNameController {

	@FXML
	Label lblCurrentName;
	@FXML
	TextField tNewName;
	@FXML
	TextArea tConsole;

	@FXML
	protected void initialize() {

		try {
			InetAddress addr;
			addr = InetAddress.getLocalHost();
			lblCurrentName.setText(addr.getHostName());
		} catch (UnknownHostException ex) {
			lblCurrentName.setText("Hata:" + ex.getMessage());
			ex.printStackTrace();
		}

		// lblOSVersion.setText(System.getProperty("os.version"));

	}

	private void log(String s) {
		tConsole.appendText(s + "\r\n");
	}

	@FXML
	public void ActionChangeMachineName() {
		tConsole.clear();
		String newName = tNewName.getText();
		if (MessageBox.Query("Makine ad�" + newName + " olarak de�i�tirilecek", "Makine ad� de�i�sin mi?")) {
			if (SYSHelper.isLinux()) {
				if (SYSHelper.isLinuxRootUser()) {
					try {
						ConsoleProcessResult r = Console.Execute("cat", "/etc/hostname");

						if (r.isSuccess()) {
							log("Mevcut makine ad�:" + r.firstResult());

							r = Console.Execute("bash", "-c", "echo " + newName + " > /etc/hostname");
							log(r.toString());
							if (r.isSuccess()) {
								r = Console.Execute("cat", "/etc/hostname");
								if (r.isSuccess()) {

									if (r.firstResult().equals(newName)) {
										log("Hostname de�i�tirildi. Yeni host:" + newName);
										r = Console.Execute("sed", "-i", "2d", "/etc/hosts");
//										log(r.toString());
										r = Console.Execute("sed", "-i", "1 a 127.0.0.1 " + newName, "/etc/hosts");
//										log(r.toString());
									} else {
										log("Hostname de�i�tirilemedi. " + r.firstResult());
									}
								} else {
									log("cat2 ba�ar�s�z: " + r.toString());
								}
							} else {
								log("echo ba�ar�s�z: " + r.toString());
							}
						} else {
							log("Makine ad� al�namad�. " + r.toString());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					MessageBox.Error("Bu i�lem i�in root kullan�c�s� olmal�s�n�z!", "Yetkisiz Eri�im");
				}

			} else {
				MessageBox.Dialog("Sadece Linux i�in yap�labilir", AlertType.ERROR, "Ba�ar�s�z");
			}
		}
	}

	public void ActionChangeMachineNameOld() {
		tConsole.clear();
		if (MessageBox.Query("Makine ad�" + tNewName.getText() + " olarak de�i�tirilecek", "Makine ad� de�i�sin mi?")) {
			if (SYSHelper.isLinux()) {
				if (SYSHelper.isLinuxRootUser()) {
					String path = SYSHelper.getAppFolder() + "/";
					String cmd = String.format("%s%s", path, "chname.sh");
					try {
						ConsoleProcessResult result = Console.Execute("/bin/bash", cmd, tNewName.getText());
						for (String s : result.getResult()) {
							tConsole.appendText(s + "\r\n");
						}
						for (String s : result.getResultErr()) {
							tConsole.appendText("E: " + s + "\r\n");
						}
						tConsole.appendText("Sonu�: " + result.getExitValue() + "\r\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					MessageBox.Error("Bu i�lem i�in root kullan�c�s� olmal�s�n�z!", "Yetkisiz Eri�im");
				}

			} else {
				MessageBox.Dialog("Sadece Linux i�in yap�labilir", AlertType.ERROR, "Ba�ar�s�z");
			}
		}
	}
}
