package util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import data.DataAccess;
import data.MetadataParser;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import pojos.Track;

public class FileChooserUtil {

	private static List<String> validFiles = new ArrayList<>(Arrays.asList("mp3", "m4a", "wma", "wav"));
	private static DataAccess dataAccess = DataAccess.getInstance();

	private static List<File> filterFiles(List<File> list) {
		List<File> result = new ArrayList<>();
		if (list != null) {
			for (Iterator<File> iter = list.listIterator(); iter.hasNext();) {
				File file = iter.next();
				String ext = FilenameUtils.getExtension(file.getName());
				if (validFiles.contains(ext))
					result.add(file);
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

	public static ObservableList<Track> getTracksChooser(Stage primaryStage) {
		ObservableList<Track> listTrack = null;
		List<File> filesChooser = readFiles(primaryStage);

		if (filesChooser != null && filesChooser.size() > 0) {
			dataAccess.filterExistFiles(filesChooser);
			listTrack = MetadataParser.getMetadataTracks(filesChooser);
		}
		return listTrack;
	}

	public static ObservableList<Track> getTracksByDragFiles(List<File> files) {
		ObservableList<Track> listTrack = null;
		files = filterFiles(files);

		if (files != null && files.size() > 0) {
			dataAccess.filterExistFiles(files);
			listTrack = MetadataParser.getMetadataTracks(files);
		}
		return listTrack;
	}

	public static File getFileChooser(Stage stage, Track track) throws URISyntaxException {
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(Paths.get(new URI(track.getLocation())).getParent().toFile());

		ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All files", "*.*");
		ExtensionFilter mp3Filter = new FileChooser.ExtensionFilter("MP3 files", "*.mp3");
		ExtensionFilter wavFilter = new FileChooser.ExtensionFilter("WAV files", "*.wav");
		ExtensionFilter wmaFilter = new FileChooser.ExtensionFilter("WMA files", "*.wma");
		ExtensionFilter m4aFilter = new FileChooser.ExtensionFilter("M4A files", "*.m4a");

		fc.getExtensionFilters().addAll(allFilter, mp3Filter, m4aFilter, wavFilter, wmaFilter);
		File result = fc.showOpenDialog(stage);

		if (result != null) {
			String ext = FilenameUtils.getExtension(result.getName());
			if (validFiles.contains(ext)) {
				boolean existInLib = dataAccess.filterExistFile(result);
				if (!existInLib) {
					return result;
				}
			}
		}
		return null;
	}
}
