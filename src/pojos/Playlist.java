package pojos;

public class Playlist {
	private String name;
	private String fullName;
	private String pathIcon;
	private int id;

	public Playlist() {
	}

	public Playlist(String fullName, String pathIcon) {
		this.setFullName(fullName);
		this.pathIcon = pathIcon;
	}

	public String getName() {
		return name;
	}

	public String getPathIcon() {
		return pathIcon;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPathIcon(String pathIcon) {
		this.pathIcon = pathIcon;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return name + " " + id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

}
