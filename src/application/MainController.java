package application;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import org.apache.commons.io.FilenameUtils;

import data.DataAccess;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import pojos.Playlist;
import pojos.Track;
import util.ControllerPlaySong;
import util.CustomImageUtil;
import util.FileChooserUtil;
import util.DurationUtil;
import util.MenuUtil;
import util.TableViewUtil;
import view.CustomHBox;
import view.CustomListCell;

public class MainController implements Initializable {

	@FXML
	private BorderPane root;
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
	private FontAwesomeIconView newPlaylist;
	@FXML
	private VBox libVBox;
	@FXML
	private BorderPane playlistsBorderPane;
	@FXML
	private ListView<Playlist> lvPlaylists;
	@FXML
	private Button doneBtn;
	@FXML
	private Text namePl;
	@FXML
	private AnchorPane navigationAnchorPane, addTracksAP;
	@FXML
	private SplitPane splitPane;
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
	private ObservableList<Track> allTracks;
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

	PseudoClass def = PseudoClass.getPseudoClass("default");
	PseudoClass selected = PseudoClass.getPseudoClass("selected");

	// =============================================================
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initTableSong();
		initListSong();
		initAllPlaylist();
		initControllSong();
	}

	public void onAddFiletoLibrary(ActionEvent e) {
		ObservableList<Track> tracksChooser = FileChooserUtil.getTracksChooser(primaryStage);
		if (tracksChooser != null && tracksChooser.size() > 0) {
			System.out.println("" + tracksChooser.size());
			allTracks.addAll(tracksChooser);
			mData.writeTracks(tracksChooser);
		}
	}

	private void playSong(Track song) {
		String filePath = song.getLocation();
		if (filePath != null) {
			if (mMediaPlayer != null) {
				mMediaPlayer.stop();
			}
			if (FilenameUtils.getExtension(filePath).equals("wma")) {
				mMedia = new Media(ControllerPlaySong.conver(filePath.substring(6)));
			} else {
				mMedia = new Media(filePath);
			}
			mMediaPlayer = new MediaPlayer(mMedia);
			mMediaPlayer.setAutoPlay(true);

			mMediaPlayer.setOnReady(new Runnable() {
				public void run() {
					duration = mMediaPlayer.getMedia().getDuration();
					ControllerPlaySong.updatetimeTrackBar(mMediaPlayer, timeUp, timeDown, duration, trackSlider,
							volumeSlider, trackProgressBar, volumeProgressBar);
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
			coverTrack.setImage(CustomImageUtil.setCoverImage(track));

			mMediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
				@Override
				public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
						Duration newValue) {
					ControllerPlaySong.updatetimeTrackBar(mMediaPlayer, timeUp, timeDown, duration, trackSlider,
							volumeSlider, trackProgressBar, volumeProgressBar);
				}
			});

			ControllerPlaySong.trackSlider(mMediaPlayer, trackProgressBar, trackSlider, duration, volumeSlider, timeUp,
					timeDown, volumeProgressBar);

			ControllerPlaySong.volumeSlider(mMediaPlayer, volumeSlider, volumeProgressBar);
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
			idx = rd.nextInt(allTracks.size() - 1);
			tableTracks.getSelectionModel().clearAndSelect(idx);
			track = tableTracks.getItems().get(idx);
			if (track != null) {
				playSong(track);
			}
			break;
		default:
			if (allTracks.size() == idx + 1) {
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
			idx = allTracks.size();
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
		tcYear.setCellValueFactory(new PropertyValueFactory<Track, String>("year"));

		tableTracks.setPlaceholder(TableViewUtil.createPlaceHolder());
		tableTracks.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	private void initListSong() {
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
					} else if (e.isSecondaryButtonDown()) {
						ObservableList<Track> listDetele = tableTracks.getSelectionModel().getSelectedItems();
						for (Track track : listDetele) {
							System.out.println(track.getName());
						}
						MenuUtil.createContextMenuForTableRow(mData, listDetele, allTracks, row);
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

	private void initAllPlaylist() {
		hideAddTracksToPl();
		CustomHBox library = new CustomHBox(libVBox);

		allTracks = library.getTracks();
		tableTracks.setItems(allTracks);

		playList = FXCollections.observableArrayList();
		lvPlaylists.setItems(playList);
		lvPlaylists.setPadding(new Insets(2, 0, 2, 15));

		library.setOnMouseClicked(e -> {
			lvPlaylists.getSelectionModel().clearSelection();
			library.setSelected(true);
		});

		newPlaylist.setOnMousePressed(e -> {
			library.setSelected(false);
			Playlist newPl = new Playlist();
			newPl.setName("Playlist");
			playList.add(newPl);
			lvPlaylists.getSelectionModel().select(newPl);
			hideNavigationPl();
			showAddTracksToPl();
		});

		lvPlaylists.setOnMouseClicked(e -> {
			library.setSelected(false);
			Playlist selectedItem = lvPlaylists.getSelectionModel().getSelectedItem();
		});

		lvPlaylists.setCellFactory(lv -> new CustomListCell());

	}

	private void initControllSong() {
		bottomBar.setVisible(false);
		bottomBar.setManaged(false);
		nextTrack.setOnMouseClicked(v -> nextSong());
		preTrack.setOnMouseClicked(v -> previousSong());

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

	public void onDoneAddTracksToPl(MouseEvent e) {
		showNavigationPl();
		hideAddTracksToPl();
	}

	private void showAddTracksToPl() {
		addTracksAP.setVisible(true);
		addTracksAP.setManaged(true);
	}

	private void hideNavigationPl() {
		splitPane.setDividerPositions(splitPane.getDividerPositions());
		navigationAnchorPane.setManaged(false);
		navigationAnchorPane.setVisible(false);
	}

	private void showNavigationPl() {
		navigationAnchorPane.setVisible(true);
		navigationAnchorPane.setManaged(true);
		splitPane.setDividerPositions(0.2);
	}

	private void hideAddTracksToPl() {
		addTracksAP.setManaged(false);
		addTracksAP.setVisible(false);
	}
}
