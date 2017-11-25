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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jaudiotagger.tag.Tag;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import pojos.Playlist;
import pojos.Track;

public class DataAccess {

	private static final String PATH_LIB_HEADER = "C:\\Users\\";
	private static final String LIB_NAME = "library.json";
	private static final String FOLDER_NAME = "MusicPlayer";

	private static final String TRACKS = "tracks";
	private static final String PLAYLISTS = "playlists";
	private static final String COUNTER = "counter";
	private static final String LIBRARY = "library";

	private DataAccess() {
		createLibrary();
	}

	private static class DataAccessHelper {
		private static final DataAccess INSTANCE = new DataAccess();
	}

	public static DataAccess getInstance() {
		return DataAccessHelper.INSTANCE;
	}

	public ObservableMap<String, ObservableList<Playlist>> getMapPlaylists() {
		ObservableList<Playlist> playlists = FXCollections.observableArrayList();

		JSONObject obj = parseJSONObject();
		if (obj != null) {
			JSONObject plObj = (JSONObject) obj.get(PLAYLISTS);
			
		}
		return null;
	}

	public ObservableList<Track> getTracksOfLib() {
		ObservableList<Track> result = FXCollections.observableArrayList();
		ObservableMap<Long, Track> tracks = mapTracksById();
		if (tracks.size() > 0) {
			JSONObject obj = parseJSONObject();
			if (obj != null) {
				JSONArray library = (JSONArray) obj.get(LIBRARY);
				if (!library.isEmpty()) {
					for (int i = 0; i < library.size(); i++) {
						long trackId = (long) library.get(i);
						if (tracks.containsKey(trackId)) {
							result.add(tracks.get(trackId));
						}
					}
				}
			}
		}
		return result;
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

	public void deleteTracks(List<Track> tracks) {
		for (Track track : tracks) {
			deleteTrack(track);
		}
	}

	public Track readTrack(JSONObject trackObj) {
		Track track = new Track();
		long id = (long) trackObj.get("id");
		long size = (long) trackObj.get("size");
		String durationStr = (String) trackObj.get("total_time");

		String year = (String) trackObj.get("year");
		String artist = (String) trackObj.get("artist");
		String album = (String) trackObj.get("album");
		String name = (String) trackObj.get("name");
		String encoding = (String) trackObj.get("kind");
		String genre = (String) trackObj.get("genre");
		String location = (String) trackObj.get("location");

		Tag tag = MetadataParser.getAudioTag(location).get();
		Optional<byte[]> coverImageOpt = MetadataParser.getCoverBytes(tag);

		track.setId(id);
		track.setSize(size);
		track.setAlbum(album);
		track.setArtist(artist);
		track.setCoverImage(coverImageOpt);
		track.setTime(durationStr);
		track.setEncoding(encoding);
		track.setGenre(genre);
		track.setLocation(location);
		track.setName(name);
		track.setYear(year);
		return track;
	}

	public ObservableList<Track> readTracks() {
		ObservableList<Track> result = FXCollections.observableArrayList();
		JSONObject obj = parseJSONObject();
		if (obj != null) {
			JSONArray tracks = (JSONArray) obj.get(TRACKS);
			for (int i = 0; i < tracks.size(); i++) {
				JSONObject element = (JSONObject) tracks.get(i);
				Track track = readTrack(element);
				result.add(track);
			}
		}
		return result;
	}

	public ObservableMap<Long, Track> mapTracksById() {
		ObservableMap<Long, Track> result = FXCollections.observableHashMap();
		JSONObject obj = parseJSONObject();
		if (obj != null) {
			JSONArray tracks = (JSONArray) obj.get(TRACKS);
			for (int i = 0; i < tracks.size(); i++) {
				JSONObject element = (JSONObject) tracks.get(i);
				Track track = readTrack(element);
				result.put(track.getId(), track);
			}
		}
		return result;
	}

	public Map<Integer, Track> mapTracksByLocation() {
		Map<Integer, Track> result = new HashMap<Integer, Track>();
		JSONObject obj = parseJSONObject();
		if (obj != null) {
			JSONArray tracks = (JSONArray) obj.get(TRACKS);
			if (!tracks.isEmpty()) {
				for (int i = 0; i < tracks.size(); i++) {
					JSONObject element = (JSONObject) tracks.get(i);
					Track track = readTrack(element);
					result.put(track.getLocation().hashCode(), track);
				}
			}
		}
		return result;
	}

	public void filterExistFiles(List<File> filesChooser) {
		Map<Integer, Track> tracks = mapTracksByLocation();
		if (tracks.size() > 0) {
			Iterator<File> iterator = filesChooser.iterator();
			while (iterator.hasNext()) {
				File file = iterator.next();
				if (tracks.containsKey(file.toURI().toString().hashCode())) {
					iterator.remove();
					System.out.println("remove");
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized void writeTrack(Track track) {
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

	public void writeTracks(List<Track> tracks) {
		for (Track track : tracks) {
			writeTrack(track);
			addToLib(track.getId());
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized void addToLib(long id) {
		JSONObject obj = parseJSONObject();
		if (obj != null) {
			JSONArray library = (JSONArray) obj.get(LIBRARY);
			library.add(id);
			obj.put(LIBRARY, library);
			writeJSONToFile(obj);
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject createJSONObject(Track track, long counter) {
		JSONObject item = new JSONObject();
		track.setId(++counter);
		item.put("id", track.getId());
		item.put("name", track.getName());
		item.put("artist", track.getArtist());
		item.put("album", track.getAlbum());
		item.put("genre", track.getGenre());
		item.put("kind", track.getEncoding());
		item.put("size", track.getSize());
		item.put("total_time", track.getTime());
		item.put("year", track.getYear());
		item.put("location", track.getLocation());
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
			in.close();
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

		File libraryFile = new File(getPathFileLibData());
		if (!libraryFile.exists()) {
			JSONObject obj = new JSONObject();

			JSONArray tracks = new JSONArray();
			obj.put(TRACKS, tracks);

			JSONObject playlists = new JSONObject();
			obj.put(PLAYLISTS, playlists);

			JSONArray library = new JSONArray();
			obj.put(LIBRARY, library);

			long counter = 0;
			obj.put(COUNTER, counter);

			writeJSONToFile(obj);
		}
	}

	private synchronized void writeJSONToFile(JSONObject obj) {
		Writer writer = null;
		try {
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(getPathFileLibData())), "UTF8"));
			writer.write(obj.toJSONString());
			writer.close();
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
