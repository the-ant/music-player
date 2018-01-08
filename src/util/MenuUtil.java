package util;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
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

	public static Label setTextLabel(String text, double size) {
		Label lblName = new Label(text);
		lblName.setTextFill(Color.BLACK);
		lblName.setPrefWidth(size);
		lblName.setWrapText(true);
		lblName.setPadding(new Insets(0, 0, 0, 15));
		return lblName;
	}

	public static void setOnActionMenuItems(MenuItem play, MenuItem getInfo, MenuItem showInWE, MenuItem addToPl,
			MenuItem delete, TableView<Track> tableTracks) {

		ObservableList<Track> selectedTracks = tableTracks.getSelectionModel().getSelectedItems();
		for (Track track : selectedTracks) {
			System.out.println("Name: " + track.getName());
		}

	}
}
