package application;

import java.io.File;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import data.DataAccess;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import pojos.Playlist;
import pojos.Track;
import util.CustomPlaylist;
import util.DirectoryUtil;
import util.ListViewUtil;
import util.MenuUtil;
import util.SongController;
import util.TableViewUtil;

public class MainController implements Initializable {
	@FXML
	private TableView<Track> tableSongs;
	@FXML
	private TableColumn<Track, String> tableName, tableTime, tableArtist, tableAlbum, tableGenre;

	@FXML
	private VBox controller;
	@FXML
	private HBox mainListController, newPlaylistController;
	@FXML
	private ImageView mainListControllerIcon, newPlaylistControllerIcon;
	@FXML
	private Text mainListControllerText, newPlaylistControllerText;

	@FXML
	private ListView<Playlist> lvPlaylist;
	private ObservableList<Playlist> playList;
	private ObservableList<Track> listSong;
	private Stage primaryStage = Main.getPrimaryStage();
	private Media mMedia;
	private MediaPlayer mMediaPlayer;
	private DataAccess mData = DataAccess.getInstance();

	// ==========================================================

	private int flagTypeNextSong = SongController.SONG_DEFAULT;

	@FXML
	private BorderPane bottomBar;
	@FXML
	private FontAwesomeIconView nextSongBtn, preSongBtn, playSongBtn, randomSong;
	@FXML
	private Label nameSong, artistSong, timeSong;
	@FXML
	private ImageView imageSong;
	@FXML
	private Slider sliderSong, volumeSong;
	private Duration duration;

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
		ObservableList<File> listFile = FXCollections.observableArrayList(DirectoryUtil.readPathFile(primaryStage));
		if (listFile != null) {
			ObservableList<Track> listTrack = DirectoryUtil.getInfoSong(listFile);
			for (Track track : listTrack) {
				if (!mData.isExistFile(track)) {
					mData.writeData(track);
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
			mMedia = new Media(filePath);
			mMediaPlayer = new MediaPlayer(mMedia);
			mMediaPlayer.setAutoPlay(true);

			mMediaPlayer.setOnReady(new Runnable() {
				public void run() {
					duration = mMediaPlayer.getMedia().getDuration();
					updateTimeSongBar();
				}
			});

			playSongBtn.setGlyphName("PAUSE");
			//flagShowVolumeSlider = 1;

			mMediaPlayer.setOnEndOfMedia(new Runnable() {
				public void run() {
					nextSong();
				}
			});
			int ind = tableSongs.getSelectionModel().getSelectedIndex();
			Track track = tableSongs.getItems().get(ind);
			nameSong.setText(track.getName().toString());
			artistSong.setText(track.getArtist().toString());
			// set image album file mp3
			// imageSong.setImage("");

			mMediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
				@Override
				public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
						Duration newValue) {
					updateTimeSongBar();
				}
			});
			sliderSong.valueProperty().addListener(new InvalidationListener() {
				public void invalidated(Observable ov) {
					if (sliderSong.isValueChanging()) {
						// multiply duration by percentage calculated by slider position
						if (duration != null) {
							mMediaPlayer.seek(duration.multiply(sliderSong.getValue() / 100.0));
						}
						updateTimeSongBar();
					}
				}
			});

			volumeSong.valueProperty().addListener(new InvalidationListener() {
				public void invalidated(Observable ov) {
					if (volumeSong.isValueChanging()) {
						mMediaPlayer.setVolume(volumeSong.getValue() / 100.0);
					}
				}
			});
		}
	}

	protected void updateTimeSongBar() {
		if (timeSong != null && sliderSong != null && volumeSong != null && duration != null) {
			Platform.runLater(new Runnable() {
				public void run() {
					Duration currentTime = mMediaPlayer.getCurrentTime();
					timeSong.setText(SongController.formatTime(currentTime, duration));
					sliderSong.setDisable(duration.isUnknown());
					if (!sliderSong.isDisabled() && duration.greaterThan(Duration.ZERO)
							&& !sliderSong.isValueChanging()) {
						sliderSong.setValue(currentTime.divide(duration.toMillis()).toMillis() * 100.0);
					}
					if (!volumeSong.isValueChanging()) {
						volumeSong.setValue((int) Math.round(mMediaPlayer.getVolume() * 100));
					}
				}
			});
		}
	}

	private void nextSong() {
		int idx = tableSongs.getSelectionModel().getSelectedIndex();
		Track track = null;
		switch (flagTypeNextSong) {
		case SongController.SONG_REPEAT:
			track = tableSongs.getItems().get(idx);
			if (track != null) {
				playSong(track);
			}
			break;
		case SongController.SONG_RANDOM:
			Random rd = new Random();
			idx = rd.nextInt(listSong.size() - 1);
			tableSongs.getSelectionModel().clearAndSelect(idx);
			track = tableSongs.getItems().get(idx);
			if (track != null) {
				playSong(track);
			}
			break;
		default:
			if (listSong.size() == idx + 1) {
				idx = -1;
			}
			tableSongs.getSelectionModel().clearAndSelect(idx + 1);
			track = tableSongs.getItems().get(idx + 1);
			if (track != null) {
				playSong(track);
			}
			break;
		}
	}

	private void previousSong() {
		int idx = tableSongs.getSelectionModel().getSelectedIndex();
		if (idx == 0) {
			idx = listSong.size();
		}
		tableSongs.getSelectionModel().clearAndSelect(idx - 1);
		Track track = tableSongs.getItems().get(idx - 1);
		if (track != null) {
			playSong(track);
		}
	}

	private void initTableSong() {
		tableName.setCellValueFactory(new PropertyValueFactory<Track, String>("Name"));
		tableTime.setCellValueFactory(new PropertyValueFactory<Track, String>("Time"));
		tableArtist.setCellValueFactory(new PropertyValueFactory<Track, String>("Artist"));
		tableAlbum.setCellValueFactory(new PropertyValueFactory<Track, String>("Album"));
		tableGenre.setCellValueFactory(new PropertyValueFactory<Track, String>("Genre"));
		tableSongs.setPlaceholder(TableViewUtil.createPlaceHolder());
		tableSongs.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	private void initListSong() {
		// Read from file
		listSong = mData.readData();
		tableSongs.setItems(listSong);

		// Double click to play this song
		tableSongs.setRowFactory(tv -> {
			TableRow<Track> row = new TableRow<>();
			row.setOnMouseClicked(e -> {
				Track track = row.getItem();
				if (e.getClickCount() == 2 && !row.isEmpty()) {
					bottomBar.setVisible(true);
					bottomBar.setManaged(true);
				
					if (track != null) {
						playSong(track);
					}
				}
				if (!row.isEmpty()) {
					MenuUtil.createContextMenuForTableRow(mData, row, track, listSong);
				}
			});
			return row;
		});
	}

	private void initAllNewPlaylist() {
		playList = FXCollections.observableArrayList();
		ListViewUtil.updateItems(lvPlaylist);
		lvPlaylist.setItems(playList);
		lvPlaylist.setOnMouseClicked(v -> {
			setSelectedMainList(false);
			// Playlist item = lvPlaylist.getSelectionModel().getSelectedItem();
		});

	}

	private void initMainPLaylist() {
		mainListController.setOnMouseClicked(v -> {
			setSelectedMainList(true);
			lvPlaylist.getSelectionModel().clearSelection();
		});
		mainListController.setOnMouseReleased(v -> setSelectedMainList(false));
		newPlaylistController.setOnMousePressed(v -> {

			setSelectedMainList(false);
			setSelectedNewPlaylist(true);

			playList.add(CustomPlaylist.createNewPlaylist(playList));
			lvPlaylist.getSelectionModel().select(playList.size() - 1);
			lvPlaylist.getFocusModel().focus(playList.size() - 1);
			lvPlaylist.scrollTo(playList.size() - 1);
		});
		newPlaylistController.setOnMouseReleased(v -> setSelectedNewPlaylist(false));
	}

	private void initControllSong() {
		bottomBar.setVisible(false);
		bottomBar.setManaged(false);

		nextSongBtn.setOnMouseClicked(v -> {
			nextSong();
		});
		preSongBtn.setOnMouseClicked(v -> {
			previousSong();
		});
		playSongBtn.setOnMouseClicked(v -> {
			if (mMediaPlayer.getStatus().equals(Status.PLAYING)) {
				playSongBtn.setGlyphName("PLAY");
				mMediaPlayer.pause();
			} else if (mMediaPlayer.getStatus().equals(Status.PAUSED)) {
				playSongBtn.setGlyphName("PAUSE");
				mMediaPlayer.play();
			}
		});
		randomSong.setOnMouseClicked(v -> {
			if (flagTypeNextSong == SongController.SONG_RANDOM) {
				flagTypeNextSong = SongController.SONG_DEFAULT;
			} else {
				flagTypeNextSong++;
			}

			switch (flagTypeNextSong) {
			case SongController.SONG_REPEAT:
				randomSong.setGlyphName("REPEAT");
				break;
			case SongController.SONG_RANDOM:
				randomSong.setGlyphName("RANDOM");
				break;
			default:
				randomSong.setGlyphName("EXCHANGE");
				break;
			}
		});
	}

	private void setSelectedNewPlaylist(boolean isSelected) {
		if (isSelected) {
			newPlaylistController.setStyle("-fx-background-color:#003f7f");
			newPlaylistControllerIcon.setImage(new Image("/icons/ic_playlist_add_white.png"));
			newPlaylistControllerText.setFill(Color.WHITE);
		} else {
			newPlaylistController.setStyle("-fx-background-color:white");
			newPlaylistControllerIcon.setImage(new Image("/icons/ic_playlist_add_black.png"));
			newPlaylistControllerText.setFill(Color.BLACK);
		}
	}

	private void setSelectedMainList(boolean isSelected) {
		if (isSelected) {
			mainListController.setStyle("-fx-background-color:#003f7f");
			mainListControllerIcon.setImage(new Image("/icons/ic_music_white.png"));
			mainListControllerText.setFill(Color.WHITE);
		} else {
			mainListController.setStyle("-fx-background-color:white");
			mainListControllerIcon.setImage(new Image("/icons/ic_music_black.png"));
			mainListControllerText.setFill(Color.BLACK);
		}
	}

}
