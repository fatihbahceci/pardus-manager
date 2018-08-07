package org.pardus.manager;

import java.io.IOException;

import org.pardus.manager.helper.MessageBox;

import com.jcraft.jsch.JSchException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.pardus.manager.helper.ssh.SSHRequestBase;;

public class Main extends Application {
	
	
	static void RootTest() throws JSchException, IOException {
		System.out.println("Baþla");
		SSHRequestBase r = new SSHRequestBase("root", "192.168.229.129", "birparola");
		//Bu root kullanýcýsý mý 0: root
		System.out.println("Exec result:" + r.exec("id -u"));
		//0 Komut baþarýlý
		System.out.println("Exec status:" + r.getLastExitStatus());
		//Kasten olmayan bir komut
		System.out.println("Exec result:" + r.exec("u"));
		//127 Komut bulunamadý
		System.out.println("Exec status:" + r.getLastExitStatus());
		//Bu kullanýcý super user yetkilerine sahip mi? 
		System.out.println("Exec result:" + r.exec("sudo -v"));
		// 0 : Sahip 1: deðil
		System.out.println("Exec status:" + r.getLastExitStatus());
		System.out.println("Bitir");
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = (BorderPane) FXMLLoader.load(getClass().getResource("MainPanel.fxml"));
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);

//			Platform.setImplicitExit(false);
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					// TODO Auto-generated method stub
					if (false == MessageBox.Query("Uygulama kapatýlsýn mý?", "Program kapatýlacak")) {
						event.consume();
					}
				}
			});

			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
