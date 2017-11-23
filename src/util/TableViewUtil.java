package util;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class TableViewUtil {

	public static VBox createPlaceHolder() {
		VBox placeHolder = new VBox();
		
		placeHolder.getChildren().add(new ImageView(new Image("/images/img_playlist.png")));
		
		Label txtPlaceHolder = new Label("Press Ctrl + O to add items");
		txtPlaceHolder.setFont(new Font("System", 18));
		
		placeHolder.getChildren().add(txtPlaceHolder);
		placeHolder.setAlignment(Pos.CENTER);
		return placeHolder;
	}
	
}
