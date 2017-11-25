package data;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import pojos.Track;

public class MetadataParser {

	private MetadataParser() {
	}

	public static Track createTrack(File file) {
		Track track = new Track();
		try {
			AudioFile audioFile = AudioFileIO.read(file);

			track.setSize(file.length());
			track.setTime(audioFile.getAudioHeader().getTrackLength() + "");
			track.setEncoding(audioFile.getAudioHeader().getEncodingType());
			track.setLocation(file.toURI().toString());

			Tag tag = audioFile.getTag();
			parseBaseMetadata(file, track, tag);
			track.setCoverImage(getCoverBytes(tag));

		} catch (IOException | CannotReadException | ReadOnlyFileException | TagException
				| InvalidAudioFrameException e) {
			e.printStackTrace();
		}
		return track;
	}

	public static void parseBaseMetadata(File file, Track track, Tag tag) {

		if (tag.hasField(FieldKey.TITLE)) {
			track.setName(tag.getFirst(FieldKey.TITLE));
		} else {
			track.setName(file.getName().substring(0, file.getName().lastIndexOf(".")));
		}

		if (tag.hasField(FieldKey.ALBUM))
			track.setAlbum(tag.getFirst(FieldKey.ALBUM));

		if (tag.hasField(FieldKey.ARTIST))
			track.setArtist(tag.getFirst(FieldKey.ARTIST));

		if (tag.hasField(FieldKey.GENRE))
			track.setGenre(tag.getFirst(FieldKey.GENRE));

		if (tag.hasField(FieldKey.YEAR))
			track.setYear(tag.getFirst(FieldKey.YEAR));
	}

	public static Optional<byte[]> getCoverBytes(Tag tag) {
		Optional<byte[]> coverBytes = Optional.empty();
		if (!tag.getArtworkList().isEmpty())
			coverBytes = Optional.ofNullable(tag.getFirstArtwork().getBinaryData());
		return coverBytes;
	}

	public static Optional<Tag> getAudioTag(File file) {
		Optional<Tag> optionalTag;
		try {
			AudioFile audioFile = AudioFileIO.read(file);
			optionalTag = Optional.ofNullable(audioFile.getTag());
		} catch (IOException | CannotReadException | ReadOnlyFileException | TagException
				| InvalidAudioFrameException exception) {
			optionalTag = Optional.empty();
		}
		return optionalTag;
	}
}
