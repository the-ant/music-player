package application;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ForkJoinPool;

import org.apache.commons.io.FilenameUtils;

import data.DataAccess;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.Stage;
import javafx.util.Duration;
import pojos.Playlist;
import pojos.Track;
import util.ControllerPlaySong;
import util.DirectoryUtil;
import util.ListViewUtil;
import util.MenuUtil;
import util.TableViewUtil;

public class MainController implements Initializable {
	// ===========================Table
	// Tracks===============================================
	@FXML
	private TableView<Track> tableTracks;
	@FXML
	private TableColumn<Track, String> tcName, tcTime, tcArtist, tcAlbum, tcGenre, tcYear;
	// =====================================================================================

	// ===========================Library and play
	// lists======================================
	@FXML
	private HBox libraryHBox;
	@FXML
	private ImageView imgLib;
	@FXML
	private FontAwesomeIconView newPlaylist;
	@FXML
	private ListView<Playlist> lvPlaylists;
	@FXML
	private Label libraryLabel, nameLib;

	ForkJoinPool forkJoinPool = new ForkJoinPool(4);
	// =====================================================================================

	// ===========================Player======================================================
	@FXML
	private GridPane bottomBar;
	@FXML
	private FontAwesomeIconView nextTrack, preTrack, playTrack, optionPlayTrack;
	@FXML
	private Label nameTrack, artistTrack, timeUp, timeDown;
	@FXML
	private ImageView coverTrack;
	@FXML
	private Slider trackSlider, volumeSlider;
	@FXML
	private ProgressBar trackProgressBar, volumeProgressBar;
	// ======================================================================================

	private ObservableList<Playlist> playList;
	private ObservableList<Track> listSong;
	private Stage primaryStage = Main.getPrimaryStage();
	private Media mMedia;
	private MediaPlayer mMediaPlayer;
	private DataAccess mData = DataAccess.getInstance();

	public static final int SONG_DEFAULT = 0;
	public static final int SONG_REPEAT = SONG_DEFAULT + 1;
	public static final int SONG_RANDOM = SONG_REPEAT + 1;
	private int flagTypeNextSong = SONG_DEFAULT;
	private Duration duration;

	@FXML
	private TextField tfSearch;

	// =============================================================
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initMainPLaylist();
		initTableSong();
		initListSong();
		initAllNewPlaylist();
		initControllSong();

	}

	public void onAddFiletoLibrary(ActionEvent e) {
		List<File> filesChooser = DirectoryUtil.readFiles(primaryStage);
		if (filesChooser != null) {
			ObservableList<Track> listTrack = DirectoryUtil.getMetadataTracks(filesChooser);
			for (Track track : listTrack) {
				if (!mData.isExistFile(track)) {
					mData.writeTrack(track);
					listSong.add(track);
				}
			}
		}
	}

	private void playSong(Track song) {
		String filePath = song.getLocation();
		if (filePath != null) {
			if (mMediaPlayer != null) {
				mMediaPlayer.stop();
			}
			if(FilenameUtils.getExtension(filePath).equals("wma")){
				mMedia = new Media(ControllerPlaySong.conver(filePath.substring(6)));
			}else {
				mMedia = new Media(filePath);
			}
			
			mMediaPlayer = new MediaPlayer(mMedia);
			mMediaPlayer.setAutoPlay(true);

			mMediaPlayer.setOnReady(new Runnable() {
				public void run() {
					duration = mMediaPlayer.getMedia().getDuration();
					updatetimeTrackBar();
				}
			});

			playTrack.setGlyphName("PAUSE");

			mMediaPlayer.setOnEndOfMedia(new Runnable() {
				public void run() {
					nextSong();
				}
			});
			int index = tableTracks.getSelectionModel().getSelectedIndex();
			Track track = tableTracks.getItems().get(index);
			nameTrack.setText(track.getName().toString());
			artistTrack.setText(track.getArtist().toString());
			// set image album file mp3
			// imageSong.setImage(track.setImgAlbumCover(new
			// Image("/icons/ic_music_black.png")));

			mMediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
				@Override
				public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
						Duration newValue) {
					updatetimeTrackBar();
				}
			});

			trackSlider.valueProperty().addListener(new InvalidationListener() {
				public void invalidated(Observable ov) {
					if (trackSlider.isValueChanging()) {
						// multiply duration by percentage calculated by slider position
						if (duration != null) {
							mMediaPlayer.seek(duration.multiply(trackSlider.getValue() / 100.0));
						}
						updatetimeTrackBar();
					}
				}
			});
			
			volumeSlider.valueProperty().addListener(new InvalidationListener() {
				public void invalidated(Observable ov) {
					if (volumeSlider.isValueChanging()) {
						mMediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
						volumeProgressBar.setProgress(volumeSlider.getValue() / volumeSlider.getMax());
					}
				}
			});
		}
	}

	protected void updatetimeTrackBar() {
		if (timeUp != null && timeDown != null && trackSlider != null && volumeSlider != null && duration != null) {
			Platform.runLater(new Runnable() {
				@SuppressWarnings("deprecation")
				public void run() {
					Duration currentTime = mMediaPlayer.getCurrentTime();
					timeUp.setText(ControllerPlaySong.formatTimeElapsed(currentTime));
					timeDown.setText(ControllerPlaySong.formatTimeElapsed(duration));
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

	

	private void nextSong() {
		int idx = tableTracks.getSelectionModel().getSelectedIndex();
		Track track = null;
		switch (flagTypeNextSong) {
		case SONG_REPEAT:
			track = tableTracks.getItems().get(idx);
			if (track != null) {
				playSong(track);
			}
			break;
		case SONG_RANDOM:
			Random rd = new Random();
			idx = rd.nextInt(listSong.size() - 1);
			tableTracks.getSelectionModel().clearAndSelect(idx);
			track = tableTracks.getItems().get(idx);
			if (track != null) {
				playSong(track);
			}
			break;
		default:
			if (listSong.size() == idx + 1) {
				idx = -1;
			}
			tableTracks.getSelectionModel().clearAndSelect(idx + 1);
			track = tableTracks.getItems().get(idx + 1);
			if (track != null) {
				playSong(track);
			}
			break;
		}
	}

	private void previousSong() {
		int idx = tableTracks.getSelectionModel().getSelectedIndex();
		if (idx == 0) {
			idx = listSong.size();
		}
		tableTracks.getSelectionModel().clearAndSelect(idx - 1);
		Track track = tableTracks.getItems().get(idx - 1);
		if (track != null) {
			playSong(track);
		}
	}

	private void initTableSong() {
		tcName.setCellValueFactory(new PropertyValueFactory<Track, String>("name"));
		tcTime.setCellValueFactory(new PropertyValueFactory<Track, String>("time"));
		tcArtist.setCellValueFactory(new PropertyValueFactory<Track, String>("artist"));
		tcAlbum.setCellValueFactory(new PropertyValueFactory<Track, String>("album"));
		tcGenre.setCellValueFactory(new PropertyValueFactory<Track, String>("genre"));

		tableTracks.setPlaceholder(TableViewUtil.createPlaceHolder());
		tableTracks.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	private void initListSong() {
		// Read from file
		listSong = mData.readData();
		tableTracks.setItems(listSong);

		// Double click to play this song
		tableTracks.setRowFactory(tv -> {
			TableRow<Track> row = new TableRow<>();
			row.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
				if (!row.isEmpty()) {
					if (e.getClickCount() == 2) {
						Track track = row.getItem();
						bottomBar.setVisible(true);
						bottomBar.setManaged(true);
						if (track != null) {
							playSong(track);
						}
					} else if(e.isSecondaryButtonDown()) {
						ObservableList<Track> listDetele = tableTracks.getSelectionModel().getSelectedItems();
						for (Track track : listDetele) {
							System.out.println(track.getName());
						}
						MenuUtil.createContextMenuForTableRow(mData, listDetele, listSong, row);
					}
				}
			});

			row.setOnMouseClicked(e -> {
				Track track = row.getItem();
				if (e.getClickCount() == 2 && !row.isEmpty()) {
					bottomBar.setVisible(true);
					bottomBar.setManaged(true);
					if (track != null) {
						playSong(track);
					}
				}
			});
			return row;
		});
	}

	private void initAllNewPlaylist() {
		playList = FXCollections.observableArrayList();
		ListViewUtil.updateItems(lvPlaylists);
		lvPlaylists.setItems(playList);

		lvPlaylists.setOnMouseClicked(v -> {
			// setSelectedMainList(false);
			// Playlist item = lvPlaylists.getSelectionModel().getSelectedItem();
		});

	}

	private void initMainPLaylist() {
		// newPlaylistController.setOnMousePressed(v -> {
		//
		// setSelectedMainList(false);
		// setSelectedNewPlaylist(true);
		//
		// playList.add(CustomPlaylist.createNewPlaylist(playList));
		// lvPlaylists.getSelectionModel().select(playList.size() - 1);
		// lvPlaylists.getFocusModel().focus(playList.size() - 1);
		// lvPlaylists.scrollTo(playList.size() - 1);
		// });
		// newPlaylistController.setOnMouseReleased(v -> setSelectedNewPlaylist(false));
	}

	private void initControllSong() {
		bottomBar.setVisible(false);
		bottomBar.setManaged(false);

		nextTrack.setOnMouseClicked(v -> {
			nextSong();
		});
		preTrack.setOnMouseClicked(v -> {
			previousSong();
		});
		playTrack.setOnMouseClicked(v -> {
			if (mMediaPlayer.getStatus().equals(Status.PLAYING)) {
				playTrack.setGlyphName("PLAY");
				mMediaPlayer.pause();
			} else if (mMediaPlayer.getStatus().equals(Status.PAUSED)) {
				playTrack.setGlyphName("PAUSE");
				mMediaPlayer.play();
			}
		});

		optionPlayTrack.setOnMouseClicked(v -> {
			if (flagTypeNextSong == SONG_RANDOM) {
				flagTypeNextSong = SONG_DEFAULT;
			} else {
				flagTypeNextSong++;
			}

			switch (flagTypeNextSong) {
			case SONG_REPEAT:
				optionPlayTrack.setGlyphName("REPEAT");
				break;
			case SONG_RANDOM:
				optionPlayTrack.setGlyphName("RANDOM");
				break;
			default:
				optionPlayTrack.setGlyphName("EXCHANGE");
				break;
			}
		});

	}
}
