package application;

import javafx.beans.property.SimpleStringProperty;

public class Song {
	private SimpleStringProperty name;
	private SimpleStringProperty time;
	private SimpleStringProperty artist;
	private SimpleStringProperty album;
	private SimpleStringProperty genre;

	public Song(String name, String time, String artist, String album, String genre) {
		super();
		this.name = new SimpleStringProperty(name);
		this.time = new SimpleStringProperty(time);
		this.artist = new SimpleStringProperty(artist);
		this.album = new SimpleStringProperty(album);
		this.genre = new SimpleStringProperty(genre);
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name = new SimpleStringProperty(name);
	}

	public String getTime() {
		return time.get();
	}

	public void setTime(String time) {
		this.time = new SimpleStringProperty(time);
	}

	public String getArtist() {
		return artist.get();
	}

	public void setArtist(String artist) {
		this.artist = new SimpleStringProperty(artist);
	}

	public String getAlbum() {
		return album.get();
	}

	public void setAlbum(String album) {
		this.album = new SimpleStringProperty(album);
	}

	public String getGenre() {
		return genre.get();
	}

	public void setGenre(String genre) {
		this.genre = new SimpleStringProperty(genre);
	}

}
