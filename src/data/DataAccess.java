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
import java.util.Iterator;
import java.util.List;
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
	private static final String NEXT_TRACK_ID = "next_track_id";
	private static final Object NEXT_PLAYLIST_ID = "next_playlist_id";

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
		// ObservableList<Playlist> playlists = FXCollections.observableArrayList();

		JSONObject obj = parseJSONObject();
		if (obj != null) {
			// JSONObject plObj = (JSONObject) obj.get(PLAYLISTS);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void updateTracksToPlaylist(Playlist playlist) {
		JSONObject obj = parseJSONObject();
		if (obj != null) {
			JSONArray playlistsArray = (JSONArray) obj.get(PLAYLISTS);
			for (int i = 0; i < playlistsArray.size(); i++) {
				JSONObject playlistObj = (JSONObject) playlistsArray.get(i);
				long id = (long) playlistObj.get("id");
				if (id == playlist.getId()) {
					playlistsArray.remove(i);

					JSONArray keyArray = (JSONArray) playlistObj.get("keys");
					keyArray.removeAll(keyArray);
					keyArray.addAll(playlist.getKeys());

					playlistObj.put("keys", keyArray);
					playlistsArray.add(i, playlistObj);
					obj.put(PLAYLISTS, playlistsArray);
					writeJSONToFile(obj);
					break;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void updatePlaylists(Playlist newPl) {
		JSONObject obj = parseJSONObject();
		if (obj != null) {
			long nextPlaylistId = (long) obj.get(NEXT_PLAYLIST_ID);
			obj.put(NEXT_PLAYLIST_ID, (nextPlaylistId + 1));

			JSONArray playlistsArray = (JSONArray) obj.get(PLAYLISTS);
			playlistsArray.add(createPlaylistObject(newPl));
			obj.put(PLAYLISTS, playlistsArray);

			writeJSONToFile(obj);
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject createPlaylistObject(Playlist newPl) {
		JSONObject result = new JSONObject();
		result.put("id", newPl.getId());
		result.put("name", newPl.getName());

		JSONArray keyArray = new JSONArray();
		if (newPl.getKeys() != null) {
			keyArray.addAll(newPl.getKeys());
		}
		result.put("keys", keyArray);
		return result;
	}

	public void updateLibraryFile(ObservableList<Track> tracks) {
		writeTracks(tracks);
	}

	public long getNextPlaylistId() {
		long id = 0;
		JSONObject obj = parseJSONObject();
		if (obj != null) {
			id = (long) obj.get(NEXT_PLAYLIST_ID);
		}
		return id;
	}

	public boolean isExistNamePlaylist(String name) {
		boolean exist = false;
		ObservableMap<String, Playlist> mapNamePlaylists = mapPlaylistsByName();
		if (mapNamePlaylists.containsKey(name))
			exist = true;
		return exist;
	}

	public ObservableList<Playlist> getPlaylists() {
		ObservableList<Playlist> result = FXCollections.observableArrayList();
		Playlist library = new Playlist(0, "Music", readTracks());
		result.add(library);

		JSONObject obj = parseJSONObject();
		if (obj != null) {
			JSONArray plArray = (JSONArray) obj.get(PLAYLISTS);
			if (!plArray.isEmpty()) {
				for (int i = 0; i < plArray.size(); i++) {
					JSONObject plObj = (JSONObject) plArray.get(i);

					long id = (long) plObj.get("id");
					String name = (String) plObj.get("name");

					JSONArray keyArray = (JSONArray) plObj.get("keys");
					ObservableList<Long> keys = FXCollections.observableArrayList();
					for (int j = 0; j < keyArray.size(); j++)
						keys.add((long) keyArray.get(j));

					ObservableList<Track> tracks = FXCollections.observableArrayList();
					if (keys.size() > 0) {
						ObservableMap<Long, Track> mapTracks = mapTracksById();
						for (long key : keys)
							if (mapTracks.containsKey(key))
								tracks.add(mapTracks.get(key));
					}

					Playlist playlist = new Playlist(id, name, keys, tracks);
					result.add(playlist);
				}
			}
		}
		return result;
	}

	public ObservableMap<Long, Playlist> mapPlaylistsById() {
		ObservableMap<Long, Playlist> result = FXCollections.observableHashMap();
		ObservableList<Playlist> allOfTrack = getPlaylists();
		if (allOfTrack.size() > 0) {
			for (Playlist playlist : allOfTrack)
				result.put(playlist.getId(), playlist);
		}
		return result;
	}

	public ObservableMap<String, Playlist> mapPlaylistsByName() {
		ObservableMap<String, Playlist> result = FXCollections.observableHashMap();
		ObservableList<Playlist> allOfTrack = getPlaylists();
		if (allOfTrack.size() > 0) {
			for (Playlist playlist : allOfTrack)
				result.put(playlist.getName(), playlist);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public long getNextTrackId() {
		long nextTrackId = 0;
		JSONObject obj = parseJSONObject();
		if (obj != null) {
			nextTrackId = (long) obj.get(NEXT_TRACK_ID);
			obj.replace(NEXT_TRACK_ID, nextTrackId + 1);
			writeJSONToFile(obj);
		}
		return nextTrackId;
	}

	@SuppressWarnings("unchecked")
	public void deleteTrack(Track track) {
		JSONObject obj = parseJSONObject();
		if (obj != null) {
			JSONArray tracks = (JSONArray) obj.get(TRACKS);
			for (int i = 0; i < tracks.size(); i++) {
				JSONObject element = (JSONObject) tracks.get(i);
				long id = (long) element.get("id");
				if (track.getId() == id) {
					tracks.remove(i);
					break;
				}
			}
			obj.put(TRACKS, tracks);
			writeJSONToFile(obj);
		}
	}

	public void deleteTracks(ObservableList<Track> tracks) {
		for (Track track : tracks) {
			deleteTrack(track);
		}
		updateNextTrackId();
	}

	@SuppressWarnings("unchecked")
	private void updateNextTrackId() {
		if (readTracks().size() < 1) {
			JSONObject obj = parseJSONObject();
			if (obj != null)
				obj.put(NEXT_TRACK_ID, 0);
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

		Optional<Tag> tagOpt = MetadataParser.getAudioTag(location);
		if (tagOpt != null && !tagOpt.get().isEmpty()) {
			Optional<byte[]> coverImageByte = MetadataParser.getCoverBytes(tagOpt.get());
			if (coverImageByte.isPresent()) {
				track.setCoverImage(coverImageByte.get());
			}
		}

		track.setId(id);
		track.setSize(size);
		track.setAlbum(album);
		track.setArtist(artist);
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
		ObservableList<Track> allOfTrack = readTracks();
		if (allOfTrack.size() > 0) {
			for (Track track : allOfTrack)
				result.put(track.getId(), track);
		}
		return result;
	}

	public ObservableMap<Integer, Track> mapTracksByLocation() {
		ObservableMap<Integer, Track> result = FXCollections.observableHashMap();
		ObservableList<Track> allOfTrack = readTracks();
		if (allOfTrack.size() > 0) {
			for (Track track : allOfTrack)
				result.put(track.getLocation().hashCode(), track);
		}
		return result;
	}

	public void filterExistFiles(List<File> filesChooser) {
		ObservableMap<Integer, Track> tracks = mapTracksByLocation();
		if (tracks.size() > 0) {
			Iterator<File> iterator = filesChooser.iterator();
			while (iterator.hasNext()) {
				File file = iterator.next();
				if (tracks.containsKey(file.toURI().toString().hashCode())) {
					iterator.remove();
				}
			}
		}
	}

	public boolean filterExistFile(File file) {
		ObservableMap<Integer, Track> tracks = mapTracksByLocation();
		if (tracks.size() > 0) {
			if (tracks.containsKey(file.toURI().toString().hashCode())) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public synchronized void writeTrack(Track track) {
		JSONObject obj = parseJSONObject();
		if (obj != null) {
			JSONArray tracks = (JSONArray) obj.get(TRACKS);

			tracks.add(createTrackJSONObj(track));
			obj.put(TRACKS, tracks);
			obj.put(NEXT_TRACK_ID, track.getId());
			writeJSONToFile(obj);
		}
	}

	public void writeTracks(List<Track> tracks) {
		for (Track track : tracks) {
			writeTrack(track);
		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject createTrackJSONObj(Track track) {
		JSONObject item = new JSONObject();
		item.put("id", track.getId());
		item.put("year", track.getYear());
		item.put("size", track.getSize());
		item.put("name", track.getName());
		item.put("album", track.getAlbum());
		item.put("genre", track.getGenre());
		item.put("kind", track.getEncoding());
		item.put("artist", track.getArtist());
		item.put("total_time", track.getTime());
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
	private void createLibrary() {
		File directory = new File(getPathFolderData());
		if (!directory.exists()) {
			directory.mkdir();
		}

		File libraryFile = new File(getPathFileLibData());
		if (!libraryFile.exists()) {
			JSONObject obj = new JSONObject();
			JSONArray tracks = new JSONArray();
			JSONArray playlists = new JSONArray();

			obj.put(PLAYLISTS, playlists);
			obj.put(TRACKS, tracks);
			obj.put(NEXT_TRACK_ID, 0);
			obj.put(NEXT_PLAYLIST_ID, 1);

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
