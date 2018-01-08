package util;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pojos.Track;

public class DialogUtil {

	public static String createDialog(String title, String namePlaylist) {
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setGraphic(new ImageView(new Image("images/img_input_name_playlist.png")));

		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("/images/img_input_name_playlist.png"));

		ButtonType okBtnType = new ButtonType("OK", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(okBtnType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 20, 20, 20));

		Label titleName = new Label("Name:");
		TextField name = new TextField();
		name.setPrefWidth(250);
		name.setText(namePlaylist);

		grid.add(titleName, 0, 0);
		grid.add(name, 1, 0);

		Node okBtn = dialog.getDialogPane().lookupButton(okBtnType);

		name.textProperty().addListener((observable, oldValue, newValue) -> {
			okBtn.setDisable(newValue.trim().isEmpty());
		});
		dialog.getDialogPane().setContent(grid);

		Platform.runLater(() -> name.requestFocus());
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == okBtnType) {
				return name.getText();
			}
			return null;
		});

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			return result.get();
		}
		return "";
	}

	public static File locateFile(Stage primaryStage, Track track) {
		Dialog<File> dialog = new Dialog<>();
		dialog.setTitle("Music Player");
		dialog.setGraphic(new ImageView("/icons/ic_app.png"));

		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image("/icons/ic_app.png"));

		ButtonType locateType = new ButtonType("Locate", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(locateType, ButtonType.CANCEL);

		Label content = new Label(
				"The song " + "\"" + track.getName() + "\"" + " could not be used \nbecause the original file "
						+ "could not be found. Would you like to locate it?");
		content.setPadding(new Insets(10, 20, 10, 20));
		dialog.getDialogPane().setContent(content);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == locateType) {
				try {
					File fileChooser = FileChooserUtil.getFileChooser(stage, track);
					if (fileChooser != null) {
						return fileChooser;
					}
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
			return null;
		});
		Optional<File> resultOpt = dialog.showAndWait();
		return resultOpt.isPresent() ? resultOpt.get() : null;
	}
}
