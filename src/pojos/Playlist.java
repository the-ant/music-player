package pojos;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Playlist {

	private long id;
	private String name;
	private int totalItem;
	private List<Long> keys = new ArrayList<>();
	private ObservableList<Track> tracks = FXCollections.observableArrayList();

	public Playlist(long id, String name, List<Long> keys, ObservableList<Track> tracks) {
		this.setId(id);
		this.setName(name);
		this.setKeys(keys);
		this.setTracks(tracks);
	}

	public Playlist() {
	}

	public Playlist(int id, String name, ObservableList<Track> tracks) {
		this.setId(id);
		this.setName(name);
		this.setTracks(tracks);
	}

	public Playlist(long id, String name) {
		this.setId(id);
		this.setName(name);
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Long> getKeys() {
		return keys;
	}

	public void setKeys(List<Long> keys) {
		this.keys = (keys);
	}

	public void setKeysByTracks() {
		keys.clear();
		for (Track track : tracks) {
			keys.add(track.getId());
		}
	}

	public int getTotalItem() {
		return totalItem;
	}

	public void setTotalItem(int totalItem) {
		this.totalItem = totalItem;
	}

	public void removeAllKey() {
		keys.clear();
		for (Track item : tracks) {
			keys.add(item.getId());
		}
	}

	public void removeAllTrack(ObservableList<Track> selectedTracks) {
		for (int j = 0; j < selectedTracks.size(); j++)
			for (int i = 0; i < tracks.size(); i++)
				if (tracks.get(i).getId() == selectedTracks.get(j).getId()) {
					System.out.println("Id: " + tracks.get(i).getId());
					tracks.remove(i);
				}
	}
}
