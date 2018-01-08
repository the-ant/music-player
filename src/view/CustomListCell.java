package view;

import application.MainController;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import pojos.Playlist;
import util.MenuUtil;

public class CustomListCell extends ListCell<Playlist> {

	private MainController mainController;

	public CustomListCell(MainController mainController) {
		this.mainController = mainController;
	}

	@Override
	protected void updateItem(Playlist item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
			setContextMenu(null);

		} else {
			setText(item.getName());
			setGraphic(new ImageView(new Image("/icons/ic_playlist.png")));
			setFont(new Font("FontAwesome", 14));
			setPadding(new Insets(3, 0, 3, 20));

			if (getIndex() > 0) {
				ContextMenu menu = new ContextMenu();
				
				MenuItem edit = MenuUtil.createContextMenuItem("Edit", 100);
				edit.setOnAction(e -> mainController.editPlaylist(getIndex()));
				
				MenuItem rename = MenuUtil.createContextMenuItem("Rename", 100);
				rename.setOnAction(e -> mainController.renamePlaylist(getIndex()));

				MenuItem delete = MenuUtil.createContextMenuItem("Delete", 100);
				delete.setOnAction(e -> mainController.deletePlaylist(getIndex()));
				
				menu.getItems().addAll(edit, rename, delete);
				setContextMenu(menu);
			}
		}
	}

}
