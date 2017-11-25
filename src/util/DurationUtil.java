package util;

import javafx.util.Duration;

public class DurationUtil {

	public static String formatTime(Duration currentTime) {
		int min = (int)(currentTime.toMinutes());
		int sec = (int)(currentTime.toSeconds() - min * 60);
		return String.format("%02d:%02d", min, sec);
	}
}
