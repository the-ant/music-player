package view;

import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import pojos.Playlist;

public class CustomListCell extends ListCell<Playlist>{
	

	PseudoClass def = PseudoClass.getPseudoClass("default");
	PseudoClass selected = PseudoClass.getPseudoClass("selected");

	@Override
	protected void updateItem(Playlist item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
			pseudoClassStateChanged(selected, false);
			pseudoClassStateChanged(def, false);
			
		} else {
			if (item.isSelected()) {
				setText(item.getName());
				setGraphic(new ImageView(new Image("/icons/ic_playlist_white.png")));
				pseudoClassStateChanged(selected, true);
			} else {
				setText(item.getName());
				setGraphic(new ImageView(new Image("/icons/ic_playlist_black.png")));
				pseudoClassStateChanged(def, true);
			}
			setFont(new Font("FontAwesome", 14));
			setPadding(new Insets(3, 0, 3, 20));
		}
	}

}
