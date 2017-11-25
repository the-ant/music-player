package view;

import javafx.scene.control.ListView;
import pojos.Playlist;

public class ListViewUtil {

	public static void setUnSelectedPlaylists(ListView<Playlist> lvPlaylists) {
		for (int i = 0; i < lvPlaylists.getItems().size(); i++) {
			if (lvPlaylists.getItems().get(i).isSelected()) {
				lvPlaylists.getItems().get(i).setSelected(false);
			}
		}
	}
}
