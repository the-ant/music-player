package view;

import data.DataAccess;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import pojos.Track;

public class CustomHBox extends HBox{
	
	private ImageView imgLib;
	private Label nameLib;
	private ObservableList<Track> tracks;
	private DataAccess dataAccess = DataAccess.getInstance();

	public CustomHBox(VBox libVBox) {
		super();
		setSpacing(5);
		setMinWidth(USE_COMPUTED_SIZE);
		setMaxWidth(USE_COMPUTED_SIZE);
		setMaxHeight(USE_COMPUTED_SIZE);
		setMinHeight(USE_COMPUTED_SIZE);
		setPrefWidth(USE_COMPUTED_SIZE);
		setPrefHeight(USE_COMPUTED_SIZE);
		setPadding(new Insets(3, 0, 3, 20));
		setStyle("-fx-background-color: rgb(166.0, 158.0, 255.0)");
		setAlignment(Pos.CENTER_LEFT);
		
		imgLib = new ImageView(new Image("/icons/ic_library_white.png"));
		imgLib.setFitWidth(24);
		imgLib.setFitHeight(24);
		
		nameLib = new Label("Music");
		nameLib.setFont(new Font("FontAwesome", 14));
		nameLib.setTextFill(Color.WHITE);
		
		getChildren().addAll(imgLib, nameLib);
		libVBox.getChildren().add(this);
		
		addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			setSelected(true);
		});
		
		init();
	}
	
	private void init() {
		setTracks(dataAccess.getTracksOfLib());
	}

	public void setSelected(boolean isSelected) {
		getChildren().removeAll(imgLib, nameLib);
		if (isSelected) {
			imgLib = new ImageView(new Image("/icons/ic_library_white.png"));
			nameLib.setTextFill(Color.WHITE);
			setStyle("-fx-background-color: rgb(166.0, 158.0, 255.0)");
		} else {
			imgLib = new ImageView(new Image("/icons/ic_library_black.png"));
			nameLib.setTextFill(Color.BLACK);
			setStyle("-fx-background-color: white");
		}
		getChildren().addAll(imgLib, nameLib);
	}

	public ObservableList<Track> getTracks() {
		return tracks;
	}

	public void setTracks(ObservableList<Track> tracks) {
		this.tracks = tracks;
	}
}
