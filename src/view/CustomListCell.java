package view;

import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import pojos.Playlist;

public class CustomListCell extends ListCell<Playlist> {

	@Override
	protected void updateItem(Playlist item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);

		} else {
			setText(item.getName());
			setGraphic(new ImageView(new Image("/icons/ic_playlist.png")));
			setFont(new Font("FontAwesome", 14));
			setPadding(new Insets(3, 0, 3, 20));
		}
        getListView().refresh();
	}

}
