package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import data.MetadataParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import pojos.Track;

public class DirectoryUtil {

	private static List<String> validFiles = new ArrayList<>(Arrays.asList("mp3", "m4a", "wma", "wav"));

	private static List<File> filterFiles(List<File> list) {
		List<File> result = null;
		if (list != null) {
			result = new ArrayList<>();
			for (int i = 0; i < list.size(); i++) {
				String ext = FilenameUtils.getExtension(list.get(i).getName());
				if (validFiles.contains(ext)) {
					result.add(list.get(i));
				}
			}

		}
		return result;
	}

	public static List<File> readFiles(Stage stage) {
		List<File> list;
		FileChooser fc = new FileChooser();

		ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All files", "*.*");
		ExtensionFilter mp3Filter = new FileChooser.ExtensionFilter("MP3 files", "*.mp3");
		ExtensionFilter wavFilter = new FileChooser.ExtensionFilter("WAV files", "*.wav");
		ExtensionFilter wmaFilter = new FileChooser.ExtensionFilter("WMA files", "*.wma");
		ExtensionFilter m4aFilter = new FileChooser.ExtensionFilter("M4A files", "*.m4a");

		fc.getExtensionFilters().addAll(allFilter, mp3Filter, m4aFilter, wavFilter, wmaFilter);
		list = fc.showOpenMultipleDialog(stage);
		return filterFiles(list);
	}

	public static ObservableList<Track> getMetadataTracks(List<File> listFile) {
		ObservableList<Track> result = FXCollections.observableArrayList();
		for (File file : listFile) {
			Track track = MetadataParser.createTrack(file);
			if (track != null) {
				result.add(track);
			}
		}
		return result;
	}
}
