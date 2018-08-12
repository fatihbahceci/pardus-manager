package org.pardus.manager.controls.remotemanagement;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.pardus.manager.controls.common.LoginResult;
import org.pardus.manager.controls.common.StringTraceListener;
import org.pardus.manager.helper.MessageBox;
import org.pardus.manager.helper.ssh.SSHRequestBase;
import org.pardus.manager.model.NetworkItem;

import com.jcraft.jsch.JSchException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RMExecuteScriptController extends GridPane implements StringTraceListener {
	@FXML
	private TextArea tScript;
	@FXML
	private TextArea tConsole;
	@FXML
	private TextField tParams;

	private LoginResult loginResult;
	private NetworkItem item;
	private Stage s;

	public RMExecuteScriptController(NetworkItem item, LoginResult loginResult) {
		this.loginResult = loginResult;
		this.item = item;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("RMExecuteScript.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Event Listener on Hyperlink.onAction
	@FXML
	public void ACLoadScriptFromFile(ActionEvent event) {
		FileChooser f = new FileChooser();

		f.setTitle("Dosya seçin");
		f.getExtensionFilters().add(new ExtensionFilter("Script dosyalarý", "*.sh", "*.bat", "*.cmd"));
		f.getExtensionFilters().add(new ExtensionFilter("Tüm dosyalar", "*.*"));
		File file = f.showOpenDialog(null);
		if (file != null) {
			try {
				byte[] bytes = Files.readAllBytes(file.toPath());
				String s = new String(bytes, "UTF-8");
				tScript.setText(s);
			} catch (IOException e) {
				MessageBox.Error(e.getMessage(), "Yükleme esnasýnda hata");
				e.printStackTrace();
			}
		}
	}

	// Event Listener on Button.onAction
	@FXML
	public void ACExecuteScript(ActionEvent event) {
		final SSHRequestBase r = new SSHRequestBase(loginResult.getUserName(), item.getIpAddr(), loginResult.getPassword());
		r.addTraceListener(this);
		r.setAlwaysKeepConnectionAfterCommand(true);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
			
				final String fileName = "/tmp/temp.sh";
				try {
					if (MessageBox.Query(
							"Belirlenen script çalýþtýrýlacak. Eðer kullanýcýnýn root eriþimi yok ise bazý komutlar çalýþmayabilir",
							"Script çalýþtýýrlsýn mý?")) {
						// noluuuuur nolmaz
						r.exec("mkdir /tmp");
						r.exec(String.format("rm '%s'", fileName));

						String[] lines = tScript.getText().replace("\r", "").split("\n");
						for (String line : lines) {
							r.exec(String.format("echo '%s' >> %s", line.replace("'", "'\\''"), fileName));
						}

					}
					log("Kontrol...");
					log(r.exec(String.format("cat %s", fileName)));
					log("Çalýþtýlýyor...");
					r.exec(String.format("chmod 755 %s", fileName));
					String params = tParams.getText();
					if (params != null && params.trim().length() > 0) {
						log(r.exec(fileName + " " + params));
					} else {
						log(r.exec(fileName));
					}
					if (r.isSuccess()) {
						MessageBox.Show("Script çalýþtýrma iþlemi baþarýlý");
					} else {
						MessageBox.Error("Script çalýþtýrma iþlemi hata kodu verdi. Kod: " + r.getLastExitStatus());
					}

				} catch (JSchException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					r.disconnect();
				}
				
			}
		}).run();

	}

	public void ShowModal() {
		s = new Stage();
		s.setTitle(String.format("Betik çalýþtýr (%s - %s)", item.getHostName(), item.getIpAddr()));
		Scene scene = new Scene(this, 800, 400);
		s.setScene(scene);
//		s.initOwner(this);
		s.initModality(Modality.APPLICATION_MODAL);

//		s.show();
		s.showAndWait();

	}

	@Override
	public void OnStringTrace(String trace) {
		log(trace);
	}

	private void log(String s) {
//		try {
//			Thread.sleep(10);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		//Bu runlater çok baþýmýzý aðrýtacak
		if (tConsole != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					tConsole.appendText(s + "\r\n");
				}
			});

		} else {
			System.out.println("tConsole neden null? " + s);
		}
		System.out.println(s);
	}
}
