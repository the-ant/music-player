package util;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import pojos.Track;

public class MenuUtil {

	public static ContextMenu createContextMenu() {
		ContextMenu contextMenu = new ContextMenu();

		MenuItem play = createContextMenuItem("Play");
		MenuItem getInfo = createContextMenuItem("Get Info");
		MenuItem showInWE = createContextMenuItem("Show in Window Explorer");
		MenuItem addToPl = createContextMenuItem("Add to Playlist");
		MenuItem delete = createContextMenuItem("Delete");

		contextMenu.getItems().addAll(play, getInfo, showInWE, addToPl, delete);
		return contextMenu;
	}

	public static MenuItem createContextMenuItem(String name) {
		MenuItem menuItem = new MenuItem();
		Label lblName = new Label(name);

		lblName.setPrefWidth(200);
		lblName.setWrapText(true);
		lblName.setPadding(new Insets(0, 0, 0, 15));
		menuItem.setGraphic(lblName);

		return menuItem;
	}

	public static void setOnActionMenuItems(MenuItem play, MenuItem getInfo, MenuItem showInWE, MenuItem addToPl,
			MenuItem delete, TableView<Track> tableTracks) {
		
		ObservableList<Track> selectedTracks = tableTracks.getSelectionModel().getSelectedItems();
		for (Track track : selectedTracks) {
			System.out.println("Name: " + track.getName());
		}
		
		
	}
}
