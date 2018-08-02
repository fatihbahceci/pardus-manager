package org.pardus.manager.controls;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.pardus.manager.helper.MessageBox;
import org.pardus.manager.model.NetworkItem;
import org.pardus.manager.model.NetworkItemList;
import org.pardus.manager.threads.INetworkScanListener;
import org.pardus.manager.threads.NetworkScanParams;
import org.pardus.manager.threads.NetworkScanThread;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.CheckBox;

public class UCScanNetworkController
		implements INetworkScanListener, ListChangeListener<NetworkItem>/* , ChangeListener<Number> */ {
	@FXML
	private TextField tIpRange;
	@FXML
	private Button btnScan;
	@FXML
	private TableView<NetworkItem> tIPList;
	@FXML
	TableColumn<NetworkItem, String> colIp;
	@FXML
	TableColumn<NetworkItem, String> colHost;
	@FXML
	TableColumn<NetworkItem, String> colOS;

	private ObservableList<NetworkItem> dataList;

	public ObservableList<NetworkItem> getDataList() {
		return dataList;
	}

	public void setDataList(ObservableList<NetworkItem> dataList) {
		this.dataList = dataList;
		dataList.addListener(this);
	}

	private String btnScanText = "Durdur";

	// Event Listener on Button[#btnScan].onAction
	@FXML
	public void ACScanNetwork(ActionEvent event) {
		if (scanner != null && scanner.isActive()) {
			try {
				btnScan.setText(btnScanText);
				btnScan.setDisable(true);
				scanner.abort();
				scanner.waitForIsActive(false, 5000);
			} finally {
				btnScan.setDisable(false);
			}
		} else {
			try {
				btnScanText = btnScan.getText();
				btnScan.setText("Ýptal");
				btnScan.setDisable(true);
				getDataList().clear();
				scanner = new NetworkScanThread();
				scanner.setParams(new NetworkScanParams(cbUseSSH.isSelected(), tSSHUserName.getText(),
						tSSHPassword.getText(), tIpRange.getText()));
				scanner.addListener(this);
				scanner.start();
				scanner.waitForIsActive(true, 5000);
			} finally {
				btnScan.setDisable(false);
			}
		}
	}

	@FXML
	protected void initialize() {
		tIPList.getItems().clear();
		setDataList(FXCollections.observableArrayList());
		/*
		 * String hostName; String ipAddr; String macAddr;
		 */
		colIp.setCellValueFactory(new PropertyValueFactory<>("ipAddr"));
		colHost.setCellValueFactory(new PropertyValueFactory<>("hostName"));
		colOS.setCellValueFactory(new PropertyValueFactory<>("OS"));
		tIPList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tIPList.setItems(getDataList());
//		ruknettin.widthProperty().addListener(this);
		pbStatus.prefWidthProperty().bind(ruknettin.widthProperty().subtract(220));

	}

	private NetworkScanThread scanner;
	@FXML
	Label lblStatus;
	@FXML
	ProgressBar pbStatus;
	@FXML
	BorderPane ruknettin;
	@FXML
	CheckBox cbUseSSH;
	@FXML
	TextField tSSHUserName;
	@FXML
	TextField tSSHPassword;

	@Override
	public void newItemAdded(NetworkItem item) {
		System.err.println("Ben bir item idim kendi halimde" + item.getIpAddr());
		getDataList().add(item);

	}

	@Override
	public void statusUpdated(int total, int current, String line) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				lblStatus.setText(String.format("%s (%d / %d)", line, current, total));
			}
		});

		System.out.println(String.format("%s (%d / %d)", line, current, total));
		pbStatus.setProgress((double) current / (double) total);

	}

	@FXML
	public void ACExportList() {
		FileChooser f = new FileChooser();
		f.setTitle("Dosya seçin");
		f.getExtensionFilters().add(new ExtensionFilter("Desteklenen Dosyalar", "*.json"));
		File file = f.showSaveDialog(null);
		if (file != null) {
			try {
				Files.write(file.toPath(), new NetworkItemList(getDataList()).toJson(true).getBytes(),
						StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
			} catch (IOException e) {
				MessageBox.Error(e.getMessage(), "Kayýt esnasýnda hata");
				e.printStackTrace();
			}
		}

	}

	@FXML
	public void ACImportList() {
		FileChooser f = new FileChooser();
		f.setTitle("Dosya seçin");
		f.getExtensionFilters().add(new ExtensionFilter("Desteklenen Dosyalar", "*.json"));
		File file = f.showOpenDialog(null);
		if (file != null) {
			try {
				byte[] bytes = Files.readAllBytes(file.toPath());
				String s = new String(bytes);
				NetworkItemList list = NetworkItemList.fromJson(s, NetworkItemList.class);
				this.getDataList().clear();
				this.getDataList().addAll(list.getData());
			} catch (IOException e) {
				MessageBox.Error(e.getMessage(), "Yukleme esnasýnda hata");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onChanged(Change<? extends NetworkItem> arg0) {
		lblStatus.setText("Toplam makine: " + getDataList().size());

	}

}
