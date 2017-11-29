package util;

import java.util.Optional;

import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DialogUtil {

	public static String createDialog() {
		TextInputDialog dialog = new TextInputDialog("Playlist");
		dialog.setTitle("New Playlist");
        dialog.setHeaderText("Type your name playlist:");
        dialog.setGraphic(new ImageView(new Image("images/img_playlist.png")));
        
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
		    return result.get();
		}		
		
		return "";
	}
}
