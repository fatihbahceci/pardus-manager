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
		if (MessageBox.Query("Makine adý" + newName + " olarak deðiþtirilecek", "Makine adý deðiþsin mi?")) {
			if (SYSHelper.isLinux()) {
				if (SYSHelper.isLinuxRootUser()) {
					try {
						ConsoleProcessResult r = Console.Execute("cat", "/etc/hostname");

						if (r.isSuccess()) {
							log("Mevcut makine adý:" + r.firstResult());

							r = Console.Execute("bash", "-c", "echo " + newName + " > /etc/hostname");
							log(r.toString());
							if (r.isSuccess()) {
								r = Console.Execute("cat", "/etc/hostname");
								if (r.isSuccess()) {

									if (r.firstResult().equals(newName)) {
										log("Hostname deðiþtirildi. Yeni host:" + newName);
										r = Console.Execute("sed", "-i", "2d", "/etc/hosts");
//										log(r.toString());
										r = Console.Execute("sed", "-i", "1 a 127.0.0.1 " + newName, "/etc/hosts");
//										log(r.toString());
									} else {
										log("Hostname deðiþtirilemedi. " + r.firstResult());
									}
								} else {
									log("cat2 baþarýsýz: " + r.toString());
								}
							} else {
								log("echo baþarýsýz: " + r.toString());
							}
						} else {
							log("Makine adý alýnamadý. " + r.toString());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					MessageBox.Error("Bu iþlem için root kullanýcýsý olmalýsýnýz!", "Yetkisiz Eriþim");
				}

			} else {
				MessageBox.Dialog("Sadece Linux için yapýlabilir", AlertType.ERROR, "Baþarýsýz");
			}
		}
	}

	public void ActionChangeMachineNameOld() {
		tConsole.clear();
		if (MessageBox.Query("Makine adý" + tNewName.getText() + " olarak deðiþtirilecek", "Makine adý deðiþsin mi?")) {
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
						tConsole.appendText("Sonuç: " + result.getExitValue() + "\r\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					MessageBox.Error("Bu iþlem için root kullanýcýsý olmalýsýnýz!", "Yetkisiz Eriþim");
				}

			} else {
				MessageBox.Dialog("Sadece Linux için yapýlabilir", AlertType.ERROR, "Baþarýsýz");
			}
		}
	}
}
