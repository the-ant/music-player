package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pojos.Track;

public class DataAccess {

	private static final String PATH_LIB_HEADER = "C:\\Users\\";
	private static final String LIB_NAME = "library.json";
	private static final String FOLDER_NAME = "MusicPlayer";

	private static final String TRACKS = "tracks";
	private static final String PLAYLISTS = "playlists";
	private static final String COUNTER = "counter";

	private DataAccess() {
		createLibrary();
	}

	private static class DataAccessHelper {
		private static final DataAccess INSTANCE = new DataAccess();
	}

	public static DataAccess getInstance() {
		return DataAccessHelper.INSTANCE;
	}

	public void writeData(ObservableList<Track> listSong) {

	}

	public void replaceData() {

	}

	public void deleteTrack(Track track) {
		JSONObject obj = parseJSONObject();
		if (obj != null) {
			JSONArray tracks = (JSONArray) obj.get(TRACKS);
			for (int i = 0; i < tracks.size(); i++) {
				JSONObject element = (JSONObject) tracks.get(i);
				long id = (long) element.get("id");
				if (track.getId() == id) {
					tracks.remove(element);
					break;
				}
			}
			writeJSONToFile(obj);
		}
	}

	public ObservableList<Track> readData() {
		ObservableList<Track> result = FXCollections.observableArrayList();
		JSONObject obj = parseJSONObject();
		if (obj != null) {
			JSONArray tracks = (JSONArray) obj.get(TRACKS);
			Track track = null;
			for (int i = 0; i < tracks.size(); i++) {
				JSONObject element = (JSONObject) tracks.get(i);

				long id = (long) element.get("id");
				long size = (long) element.get("size");
				long year = (long) element.get("year");
				long time = (long) element.get("total time");
				String artist = (String) element.get("artist");
				String album = (String) element.get("album");
				String name = (String) element.get("name");
				String kind = (String) element.get("kind");
				String genre = (String) element.get("genre");
				String location = (String) element.get("location");

				track = new Track(id, size, year, time, name, artist, album, genre, kind, location);
				result.add(track);
			}
		}
		return result;
	}
	
	public boolean isExistFile(Track track) {
		boolean result = false;
		JSONObject obj = parseJSONObject();
		if (obj != null) {
			JSONArray tracks = (JSONArray) obj.get(TRACKS);
			for (int i = 0; i < tracks.size(); i++) {
				JSONObject element = (JSONObject) tracks.get(i);
				String location = (String) element.get("location");
				if (track.getLocation().equals(location)) {
					result = true;
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public void writeData(Track track) {
		JSONObject obj = parseJSONObject();
		if (obj != null) {
			JSONArray tracks = (JSONArray) obj.get(TRACKS);
			long counter = (long) obj.get(COUNTER);

			tracks.add(createJSONObject(track, counter));
			obj.put(TRACKS, tracks);
			obj.put(COUNTER, ++counter);
			writeJSONToFile(obj);
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject createJSONObject(Track song, long counter) {
		JSONObject item = new JSONObject();
		song.setId(++counter);
		item.put("id", song.getId());
		item.put("name", song.getName());
		item.put("artist", song.getArtist());
		item.put("album", song.getAlbum());
		item.put("genre", song.getGenre());
		item.put("kind", song.getKind());
		item.put("size", song.getSize());
		item.put("total time", song.getTime());
		item.put("year", song.getYear());
		item.put("location", song.getLocation());
		return item;
	}

	private JSONObject parseJSONObject() {
		JSONParser parser = new JSONParser();
		JSONObject jObject = null;
		try {
			BufferedReader in = new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(getPathFileLibData())), "UTF8"));
			Object obj = parser.parse(in);
			jObject = (JSONObject) obj;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return jObject;
	}

	@SuppressWarnings("unchecked")
	public void createLibrary() {
		File directory = new File(getPathFolderData());
		if (!directory.exists()) {
			directory.mkdir();
		}

		File library = new File(getPathFileLibData());
		if (library.exists()) {
			// System.out.println("lib existed");
		} else {
			JSONObject obj = new JSONObject();
			// Tracks
			JSONArray tracks = new JSONArray();
			obj.put(TRACKS, tracks);
			// Play lists
			JSONArray playlists = new JSONArray();
			obj.put(PLAYLISTS, playlists);
			// Counter ID
			long counter = 0;
			obj.put(COUNTER, counter);

			writeJSONToFile(obj);
		}
	}

	private void writeJSONToFile(JSONObject obj) {
		Writer writer = null;
		try {
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(getPathFileLibData())), "UTF8"));
			writer.write(obj.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getPathFileLibData() {
		return getPathFolderData() + "\\" + LIB_NAME;
	}

	public String getPathFolderData() {
		String pathFolder = PATH_LIB_HEADER + getUserName() + "\\Music\\" + FOLDER_NAME;
		return pathFolder;
	}

	public String getUserName() {
		String username = System.getProperty("user.name");
		return username;
	}
}
