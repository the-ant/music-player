package pojos;

import data.DataAccess;
import javafx.collections.ObservableList;

public class Playlist {

	private String name;
	private boolean selected = false;
	private ObservableList<Track> tracks;
	private DataAccess dataAccess = DataAccess.getInstance();

	public Playlist() {
		
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
