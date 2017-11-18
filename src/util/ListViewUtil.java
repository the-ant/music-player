package util;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import pojos.Playlist;

public class ListViewUtil {

	public static void updateItems(ListView<Playlist> lv) {
		lv.setCellFactory(new Callback<ListView<Playlist>, ListCell<Playlist>>() {

			@Override
			public ListCell<Playlist> call(ListView<Playlist> param) {
				ListCell<Playlist> cell = new ListCell<Playlist>() {
					@Override
					protected void updateItem(Playlist item, boolean empty) {
						super.updateItem(item, empty);
						if (!empty) {
							Image img = new Image(item.getPathIcon());
							ImageView icon = new ImageView(img);
							icon.setFitHeight(32);
							icon.setFitWidth(32);
							setGraphic(icon);
							setText(item.getFullName());
						} else {
							setGraphic(null);
							setText(null);
						}
					}
				};
				return cell;
			}
		});
	}
}
