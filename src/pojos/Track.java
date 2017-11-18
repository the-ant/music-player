package pojos;

public class Track {

	private long id;
	private long size;
	private long year;
	private long time;
	private String name;
	private String artist;
	private String album;
	private String genre;
	private String kind;
	private String location;

	public Track() {
		this.name = "UNKNOWN";
		this.artist = "UNKNOWN";
		this.album = "UNKNOWN";
		this.genre = "UNKNOWN";
		this.kind = "UNKNOWN";
		this.location = "UNKNOWN";
	}

	public Track(long id, long size, long year, long time, String name, String artist, String album, String genre,
			String kind, String location) {
		this.id = id;
		this.size = size;
		this.year = year;
		this.time = time;
		this.name = name;
		this.artist = artist;
		this.album = album;
		this.genre = genre;
		this.kind = kind;
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
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

	public String getKind() {
		return kind;
	}

	public long getSize() {
		return size;
	}

	public long getYear() {
		return year;
	}

	public String getLocation() {
		return location;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}