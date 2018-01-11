package util;

import data.DataAccess;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import pojos.Playlist;
import pojos.Track;

public class MenuUtil {

	public static MenuItem createContextMenuItem(String name, double size) {
		MenuItem menuItem = new MenuItem();
		Label lblName = new Label(name);
		lblName.setTextFill(Color.BLACK);
		lblName.setPrefWidth(size);
		lblName.setWrapText(true);
		lblName.setPadding(new Insets(0, 0, 0, 15));
		menuItem.setGraphic(lblName);
		return menuItem;
	}

	public static MenuItem createContextMenuItem(String name) {
		MenuItem menuItem = new MenuItem();
		Label lblName = new Label(name);
		lblName.setTextFill(Color.BLACK);
		lblName.setWrapText(true);
		lblName.setPadding(new Insets(0, 0, 0, 15));
		menuItem.setGraphic(lblName);
		return menuItem;
	}

	public static Menu createMenu(String name, double size) {
		Menu menu = new Menu();
		Label lblName = new Label(name);
		lblName.setTextFill(Color.BLACK);
		lblName.setPrefWidth(size);
		lblName.setWrapText(true);
		lblName.setPadding(new Insets(0, 0, 0, 15));
		menu.setGraphic(lblName);
		return menu;
	}

	public static Label setTextLabel(String text, double size) {
		Label lblName = new Label(text);
		lblName.setTextFill(Color.BLACK);
		lblName.setPrefWidth(size);
		lblName.setWrapText(true);
		lblName.setPadding(new Insets(0, 0, 0, 15));
		return lblName;
	}

	public static ObservableList<MenuItem> createListMenuItem(ObservableList<Track> selectedTracks,
			ObservableList<Playlist> playlists) {
		
		ObservableList<MenuItem> result = FXCollections.observableArrayList();
		for (int i = 1; i < playlists.size(); i++) {
			Playlist pl = playlists.get(i);
			MenuItem item = createContextMenuItem(pl.getName());
			item.setOnAction(e -> {
				pl.getTracks().addAll(selectedTracks);
				pl.setKeysByTracks();
				DataAccess.getInstance().updateTracksToPlaylist(pl);
			});
			result.add(item);
		}
		return result;
	}
}
