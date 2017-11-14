package application;

public class Song {
	private String name;
	private String time;
	private String artist;
	private String album;
	private String genre;

	public Song(String name, String time, String artist, String album, String genre) {
		this.name = name;
		this.time = time;
		this.artist = artist;
		this.album = album;
		this.genre = genre;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

}
