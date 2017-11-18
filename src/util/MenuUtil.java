package util;

import data.DataAccess;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import pojos.Track;

public class MenuUtil {

	public static void createContextMenuForTableRow(DataAccess dataAccess, TableRow<Track> row, Track track,
			ObservableList<Track> listSong) {
		ContextMenu contextMenu = new ContextMenu();
	    
		MenuItem delete = new MenuItem();
		Label lblDelete = new Label("Delete");
		lblDelete.setPrefWidth(200);
		lblDelete.setWrapText(true);
		lblDelete.setPadding(new Insets(0, 0, 0, 15));
		delete.setGraphic(lblDelete);
		
		delete.setOnAction(e -> {
			dataAccess.deleteTrack(track);
			listSong.remove(track);
		});

		contextMenu.getItems().add(delete);
		row.setContextMenu(contextMenu);
	}
}
