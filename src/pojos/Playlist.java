package pojos;

import data.DataAccess;
import javafx.collections.ObservableList;

public class Playlist {

	private int id;
	private String name;
	private ObservableList<Track> tracks;
	private DataAccess dataAccess = DataAccess.getInstance();

	public Playlist() {
		dataAccess.createPlaylist();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ObservableList<Track> getTracks() {
		return tracks;
	}

	public void setTracks(ObservableList<Track> tracks) {
		this.tracks = tracks;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
