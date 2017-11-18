package pojos;

import javafx.collections.ObservableList;

public class MusicPlayer{

	private String path;
	private ObservableList<String> listPath;

	public MusicPlayer(String path) {
		this.setPath(path);
	}

	public MusicPlayer(ObservableList<String> listPath) {
		this.setListPath(listPath);
	}
	
	public void play() {
		
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public ObservableList<String> getListPath() {
		return listPath;
	}

	public void setListPath(ObservableList<String> listPath) {
		this.listPath = listPath;
	}
}
