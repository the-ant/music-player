package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class IntroMyTeamController implements Initializable {

	@FXML
	private Button okBtn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		okBtn.setOnMouseReleased(e -> {
			Stage stage = (Stage) okBtn.getScene().getWindow();
			stage.close();
		});
	}

}
