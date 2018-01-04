package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import pojos.Track;
import util.ImageUtil;

public class CustomListCellNewPl extends ListCell<Track> {

	@Override
	protected void updateItem(Track item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {

			HBox root = new HBox();
			root.setAlignment(Pos.CENTER_LEFT);
			root.setSpacing(15);

			Label num = new Label("" + (getIndex() + 1));
			num.setFont(new Font("Microsoft Sans Serif", 14));
			num.setPrefWidth(20);

			ImageView cover = new ImageView(ImageUtil.setCoverImage(item.getCoverImage()));
			cover.setFitWidth(50);
			cover.setFitHeight(50);

			VBox content = new VBox();
			content.setAlignment(Pos.CENTER_LEFT);
			content.setSpacing(3);

			Label name = new Label(item.getName());
			name.setFont(new Font("Microsoft Sans Serif", 14));
			name.setPrefWidth(250);

			Label artist = new Label(item.getArtist());
			artist.setFont(new Font("Microsoft Sans Serif", 12));

			content.getChildren().addAll(name, artist);
			root.getChildren().addAll(num, cover, content);

			setGraphic(root);
			setPadding(new Insets(3, 0, 3, 10));
		}
		getListView().refresh();
	}
}
