package util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import pojos.Track;

import java.util.List;
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
}