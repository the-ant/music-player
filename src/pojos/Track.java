package pojos;

import java.util.Optional;

public class Track {

	private long id;
	private long size;
	private String year;
	private String name;
	private String artist;
	private String album;
	private String genre;
	private String location;
	private String encoding = "";
	private Optional<byte[]> coverImage;
	private double time;

	public Track() {
		this.name = "";
		this.artist = "";
		this.album = "";
		this.genre = "";
		this.location = "";
		this.year = "";
		this.encoding = "";
	}

	public Track(long id, long size, String year, double time, String name, String artist, String album,
			String genre, String encoding, String location) {
		this.setId(id);
		this.setAlbum(album);
		this.setSize(size);
		this.setName(name);
		this.setYear(year);
		this.setTime(time);
		this.setArtist(artist);
		this.setGenre(genre);
		this.setEncoding(encoding);
		this.setLocation(location);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
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

	public long getId() {
		return id;
	}

	public long getSize() {
		return size;
	}

	public String getYear() {
		return year;
	}

	public String getLocation() {
		return location;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setSize(long l) {
		this.size = l;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Optional<byte[]> getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(Optional<byte[]> coverImage) {
		this.coverImage = coverImage;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

}
