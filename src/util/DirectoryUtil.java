package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pojos.Track;

public class DirectoryUtil {

	public static File readPathFile(Stage stage) {
		FileChooser fc = new FileChooser();
		// fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.mp3"));
		File file = fc.showOpenDialog(stage);
		return file;
	}

	public static Track getInfoSong(String pathFile) {
		Track result = null;
		try {
			File file = new File(new URI(pathFile));
			InputStream in = new FileInputStream(file);
			ContentHandler handler = new DefaultHandler();
			Metadata metadata = new Metadata();
			Parser parser = new Mp3Parser();
			ParseContext parseContext = new ParseContext();

			parser.parse(in, handler, metadata, parseContext);
			in.close();

			result = new Track();
			result.setName(metadata.get("title"));
			result.setArtist(metadata.get("xmpDM:artist"));
			result.setAlbum(metadata.get("xmpDM:album"));
			result.setGenre(metadata.get("xmpDM:genre"));
			result.setLocation(pathFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
