package util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import pojos.Track;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Search {
	private static BooleanProperty hasResults = new SimpleBooleanProperty(false);
	private static List<Track> result;
	private static Thread searchThread;

	public static BooleanProperty hasResultsProperty() {
		return hasResults;
	}

	public static List<Track> getResult() {
		hasResults.set(false);
		return result;
	}

	public static void search(String searchText, ObservableList<Track> listSong) {
		if (searchThread != null && searchThread.isAlive()) {
			searchThread.interrupt();
		}
		String formatText = searchText.toLowerCase();
		searchThread = new Thread(() -> {
			try {
				hasResults.set(false);
				List<Track> tracks = listSong.stream().filter(track -> {
					if (track.getName().toLowerCase().contains(formatText)) {
						return true;
					} else if (track.getArtist().toLowerCase().contains(formatText)) {
						return true;
					} else if (track.getAlbum().toLowerCase().contains(formatText)) {
						return true;
					}
					return false;
				}).collect(Collectors.toList());
				if (searchThread.isInterrupted()) {
					throw new InterruptedException();
				}
				result = tracks;
				hasResults.set(true);
			} catch (InterruptedException ex) {
				System.out.println(ex);
			}
		});
		searchThread.start();
	}


	public static ObservableList<Track> formatListSong(ObservableList<Track> tmpListSong) {
		for (Track song : tmpListSong) {
			song.setName(deAccent(song.getName()));
			song.setArtist(deAccent(song.getArtist()));
			song.setAlbum(deAccent(song.getAlbum()));
		}
		return tmpListSong;
	}

	public static String deAccent(String str) {
		String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}
}