package util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import pojos.Track;

import java.text.Normalizer;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Search {
    private static BooleanProperty hasResults = new SimpleBooleanProperty(false);
    private static List<Long> result;
    private static Thread searchThread;
    public static BooleanProperty hasResultsProperty() { return hasResults; }
    public static List<Long> getResult() {
        hasResults.set(false);
        return result;
    }
    // searching.
    public static void search(String searchText, ObservableList<Track> listSong) {
        if (searchThread != null && searchThread.isAlive()) {
            searchThread.interrupt();
        }
        String formatText = searchText.toLowerCase();
        searchThread = new Thread(() -> {
            try {
                hasResults.set(false);
                List<Long> ids = listSong.stream()
                        .filter(track -> {
                            if (track.getName().toLowerCase().contains(formatText)) {
                                return true;
                            } else if (track.getArtist().toLowerCase().contains(formatText)) {
                                return true;
                            } else if (track.getAlbum().toLowerCase().contains(formatText)){
                                return true;
                            }
                            return false;
                        }).map(Track::getId)
                        .collect(Collectors.toList());

                if (searchThread.isInterrupted()) { throw new InterruptedException(); }
                result = ids;
                hasResults.set(true);
            } catch (InterruptedException ex) {
                //terminate thread.
            }
        });
        searchThread.start();
    }

}
