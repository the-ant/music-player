package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import pojos.Track;

public class DirectoryUtil {

	public static List<File> readPathFile(Stage stage) {
		FileChooser fc = new FileChooser();

		ExtensionFilter mp3Filter = new FileChooser.ExtensionFilter("MP3 files", "*.mp3");
		ExtensionFilter wavFilter = new FileChooser.ExtensionFilter("WAV files", "*.wav");
		ExtensionFilter wmaFilter = new FileChooser.ExtensionFilter("WMA files", "*.wma");

		fc.getExtensionFilters().addAll(mp3Filter, wavFilter, wmaFilter);
		List<File> list = fc.showOpenMultipleDialog(stage);
		return list;
	}

	public static ObservableList<Track> getInfoSong(ObservableList<File> listFile) {
		ObservableList<Track> result = FXCollections.observableArrayList();
		for (File file : listFile) {
			Track track = getInfoSong(file.toURI().toString());
			if (track != null) {
				result.add(track);
			}
		}
		return result;
	}

	public static Track getInfoSong(String pathFile) {
		Track result = null;
		try {
			File file = new File(new URI(pathFile));
			String kind = pathFile.substring(pathFile.length() - 3);

			InputStream in = new FileInputStream(file);

			result = new Track();
			if (kind.equals("wav") || kind.equals("wma")) {
				result.setName(file.getName().substring(0, file.getName().length() - 4));
				result.setLocation(pathFile);
			} else {

				ContentHandler handler = new DefaultHandler();
				Metadata metadata = new Metadata();
				Parser parser = new Mp3Parser();
				ParseContext parseContext = new ParseContext();

				parser.parse(in, handler, metadata, parseContext);
				in.close();

				result.setName(metadata.get("title"));
				result.setArtist(metadata.get("xmpDM:artist"));
				result.setAlbum(metadata.get("xmpDM:album"));
				result.setGenre(metadata.get("xmpDM:genre"));
				result.setLocation(pathFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
