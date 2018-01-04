package util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class PlaceHolderUtil {
	
	public static final String LIB = "Press Ctrl + O to add items to \nthis library.";
	public static final String PLAYLIST = "Click the Add To button to \nadd items to this playlist.";
	public static final String ADD_TRACK_PLAYLIST = "To create a playlist \ndrag songs here";

	public static VBox createPlaceHolder(String description, Pos position) {
		VBox placeHolder = new VBox();
		placeHolder.setPadding(new Insets(20));
		
		placeHolder.getChildren().add(new ImageView(new Image("/images/img_place_holder.png")));
		
		Label txtPlaceHolder = new Label(description);
		txtPlaceHolder.setAlignment(Pos.CENTER);
		txtPlaceHolder.setFont(new Font("System", 18));
		
		placeHolder.getChildren().add(txtPlaceHolder);
		placeHolder.setAlignment(position);
		return placeHolder;
	}

	public static VBox createPlaceHolderSearch(String description, Pos position) {
		VBox placeHolder = new VBox();
		placeHolder.setPadding(new Insets(20));
		
		Label txtPlaceHolder = new Label(description);
		txtPlaceHolder.setAlignment(Pos.CENTER);
		txtPlaceHolder.setFont(new Font("System", 18));
		
		placeHolder.getChildren().add(txtPlaceHolder);
		placeHolder.setAlignment(position);
		return placeHolder;
	}
	
}
