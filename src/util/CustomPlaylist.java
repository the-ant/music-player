package util;

import javafx.collections.ObservableList;
import pojos.Playlist;

public class CustomPlaylist {

	public static Playlist createNewPlaylist(ObservableList<Playlist> res) {
		Playlist newPlaylist = new Playlist();
		newPlaylist.setName("Playlist");
		newPlaylist.setId(setIdPlaylist(res));
		newPlaylist.setPathIcon("/icons/ic_playlist.png");
		newPlaylist.setFullName("Playlist" + newPlaylist.getId());
		return newPlaylist;
	}

	public static int setIdPlaylist(ObservableList<Playlist> res) {
		int result = 0;
		Playlist pl;
		for (int i = 0; i < res.size(); i++) {
			pl = res.get(i);
			if (pl.getName() == "Playlist") {
				result = pl.getId();
				break;
			}
		}
		for (int i = 1; i < res.size(); i++) {
			pl = res.get(i);
			if (pl.getName() == "Playlist" && pl.getId() > result) {
				result = pl.getId();
			}
		}
		return result + 1;
	}
}
