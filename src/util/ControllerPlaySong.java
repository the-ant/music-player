package util;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import pojos.Track;

public class ControllerPlaySong {
	public static String conver(String filepath) {
		File source = new File(filepath);
		File target = new File("target.mp3");
		AudioAttributes audio = new AudioAttributes();
		audio.setCodec("libmp3lame");
		audio.setBitRate(new Integer(128000));
		audio.setChannels(new Integer(2));
		audio.setSamplingRate(new Integer(44100));
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat("mp3");
		attrs.setAudioAttributes(audio);
		Encoder encoder = new Encoder();
		try {
			encoder.encode(source, target, attrs);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InputFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EncoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return target.toURI().toString();
	}

	public static String formatTime(Duration elapsed, Duration duration) {
		int intElapsed = (int) Math.floor(elapsed.toSeconds());
		int elapsedHours = intElapsed / (60 * 60);
		if (elapsedHours > 0) {
			intElapsed -= elapsedHours * 60 * 60;
		}
		int elapsedMinutes = intElapsed / 60;
		int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

		if (duration.greaterThan(Duration.ZERO)) {
			int intDuration = (int) Math.floor(duration.toSeconds());
			int durationHours = intDuration / (60 * 60);
			if (durationHours > 0) {
				intDuration -= durationHours * 60 * 60;
			}
			int durationMinutes = intDuration / 60;
			int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
			if (durationHours > 0) {
				return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds,
						durationHours, durationMinutes, durationSeconds);
			} else {
				return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds, durationMinutes,
						durationSeconds);
			}
		} else {
			if (elapsedHours > 0) {
				return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
			} else {
				return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
			}
		}
	}

	public static String formatTimeElapsed(Duration elapsed) {
		int intElapsed = (int) Math.floor(elapsed.toSeconds());
		int elapsedHours = intElapsed / (60 * 60);
		if (elapsedHours > 0) {
			intElapsed -= elapsedHours * 60 * 60;
		}
		int elapsedMinutes = intElapsed / 60;
		int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

		if (elapsedHours > 0) {
			return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
		} else {
			return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
		}
	}

	public static String formatTimeDuration(Duration duration) {
		int intDuration = (int) Math.floor(duration.toSeconds());
		int durationHours = intDuration / (60 * 60);
		if (durationHours > 0) {
			intDuration -= durationHours * 60 * 60;
		}
		int durationMinutes = intDuration / 60;
		int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
		if (durationHours > 0) {
			return String.format("%d:%02d:%02d", durationHours, durationMinutes, durationSeconds);
		} else {
			return String.format("%02d:%02d", durationMinutes, durationSeconds);
		}
	}

	public static void updatetimeTrackBar(MediaPlayer mMediaPlayer, Label timeUp, Label timeDown, Duration duration,
			Slider trackSlider, Slider volumeSlider, ProgressBar trackProgressBar, ProgressBar volumeProgressBar) {

		if (timeUp != null && trackSlider != null && volumeSlider != null && duration != null) {
			Platform.runLater(new Runnable() {
				@SuppressWarnings("deprecation")
				public void run() {
					Duration currentTime = mMediaPlayer.getCurrentTime();

					timeUp.setText(DurationUtil.formatTime(currentTime));
					timeDown.setText(DurationUtil.formatTime(duration));

					trackSlider.setDisable(duration.isUnknown());
					if (!trackSlider.isDisabled() && duration.greaterThan(Duration.ZERO)
							&& !trackSlider.isValueChanging()) {
						trackSlider.setValue(currentTime.divide(duration.toMillis()).toMillis() * 100.0);
						trackProgressBar.setProgress(trackSlider.getValue() / trackSlider.getMax());
					}
					
					if (!volumeSlider.isValueChanging()) {
						volumeSlider.setValue((int) Math.round(mMediaPlayer.getVolume() * 100));
						volumeProgressBar.setProgress(volumeSlider.getValue() / volumeSlider.getMax());
					}
				}
			});
		}
	}

	public static void volumeSlider(MediaPlayer mMediaPlayer, Slider volumeSlider, ProgressBar volumeProgressBar) {
		volumeSlider.setValue(100.0);
		mMediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
		volumeProgressBar.setProgress(volumeSlider.getValue() / volumeSlider.getMax());

		volumeSlider.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				if (volumeSlider.isValueChanging()) {
					mMediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
					volumeProgressBar.setProgress(volumeSlider.getValue() / volumeSlider.getMax());
				}
			}
		});
		volumeSlider.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			System.out.println("volumeSlider");
			Double endTime = volumeSlider.getMax();
			if (!endTime.equals(Double.POSITIVE_INFINITY) || !endTime.equals(Double.NaN)) {
				mMediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
				volumeProgressBar.setProgress(volumeSlider.getValue() / volumeSlider.getMax());
			}
		});
	}

	public static void trackSlider(MediaPlayer mMediaPlayer, ProgressBar trackProgressBar, Slider trackSlider,
			Duration duration, Slider volumeSlider, Label timeUp, Label timeDown, ProgressBar volumeProgressBar) {
		trackSlider.valueProperty().addListener(new InvalidationListener() {
			public void invalidated(Observable ov) {
				if (trackSlider.isValueChanging()) {
					// multiply duration by percentage calculated by slider position
					if (duration != null) {
						mMediaPlayer.seek(duration.multiply(trackSlider.getValue() / 100.0));
					}
					updatetimeTrackBar(mMediaPlayer, timeUp, timeDown, duration, trackSlider, volumeSlider,
							trackProgressBar, volumeProgressBar);
				}
			}
		});

		trackSlider.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			System.out.println("trackSlider");
			if (duration != null) {
				System.out.println("getValue: " + trackSlider.getValue());
				mMediaPlayer.seek(duration.multiply(trackSlider.getValue() / 100.0));
				System.out.println("duration: " + duration.multiply(trackSlider.getValue() / 100.0));
				trackSlider.setValue(mMediaPlayer.getCurrentTime().divide(duration.toMillis()).toMillis() * 100.0);
				System.out.println("getValue: " + trackSlider.getValue());
				trackProgressBar.setProgress(trackSlider.getValue() / trackSlider.getMax());
			}
		});
	}
	
}
