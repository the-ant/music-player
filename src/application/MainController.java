package application;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import org.apache.commons.io.FilenameUtils;

import data.DataAccess;
import data.MetadataParser;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
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
import util.DialogUtil;
import util.DurationUtil;
import util.FileChooserUtil;
import util.ImageUtil;
import util.MenuUtil;
import util.PlaceHolderUtil;
import view.CustomListCell;
import view.CustomListCellNewPl;
import view.CustomSearch;

public class MainController implements Initializable {
	@FXML
	private BorderPane root;
	@FXML
	private TableView<Track> tableTracks;
	@FXML
	private TableColumn<Track, String> tcName, tcTime, tcArtist, tcAlbum, tcGenre, tcYear;
	@FXML
	private FontAwesomeIconView newPlaylist;
	@FXML
	private VBox libVBox;
	@FXML
	private BorderPane playlistsBorderPane;
	@FXML
	private ListView<Playlist> lvPlaylists;
	@FXML
	private ListView<Track> lvAddTracksToPl;
	@FXML
	private Button doneBtn;
	@FXML
	private Text namePlToAddTracks, totalItemNewPl;
	@FXML
	private AnchorPane navigationAnchorPane, addTracksAP;
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
	@FXML
	private TextField tfSearch;
	@FXML
	private BorderPane titlePlaylistBorderPane;
	@FXML
	private Label namePlaylist, totalTracksPl;
	@FXML
	private MenuBar menuMain;

	private static Stage primaryStage = Main.getPrimaryStage();
	private static final DataFormat dragTracksFormat = new DataFormat("tracks");

	public static final int SONG_DEFAULT = 0;
	public static final int SONG_REPEAT = SONG_DEFAULT + 1;
	public static final int SONG_RANDOM = SONG_REPEAT + 1;

	private int flagTypeNextSong = SONG_DEFAULT;
	private Duration duration;
	private ObservableList<Playlist> playlists;
	private Media mMedia;
	private MediaPlayer mMediaPlayer;
	private Track playingTrack;
	private DataAccess mData = DataAccess.getInstance();

	private MenuItem play, stop, previous, next;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initAllPlaylist();
		initTableSong();
		initControllSong();
		initSearch();
		initMenu();
	}

	private void initMenu() {
		createMainMenu();
	}

	private void createMainMenu() {
		Menu file = new Menu("File");
		Menu control = new Menu("Control");
		Menu help = new Menu("Help");

		/*
		 * File
		 */
		MenuItem newPlaylist = MenuUtil.createContextMenuItem("New Playlist", 200);
		newPlaylist.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		newPlaylist.setOnAction(e -> createNewPlaylist());

		MenuItem addFile = MenuUtil.createContextMenuItem("Add File to Library...", 200);
		addFile.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
		addFile.setOnAction(e -> addFileToLib());

		MenuItem addFolder = MenuUtil.createContextMenuItem("Add Folder to Library...", 200);
		addFolder.setOnAction(e -> addFolderToLib());

		MenuItem exit = MenuUtil.createContextMenuItem("Exit", 200);
		exit.setOnAction(e -> Platform.exit());

		file.getItems().addAll(newPlaylist, addFile, addFolder, exit);

		/*
		 * Control
		 */
		play = MenuUtil.createContextMenuItem("Play", 150);
		play.setAccelerator(new KeyCodeCombination(KeyCode.S));
		play.setOnAction(e -> pressPlayTrack());
		play.setDisable(true);

		next = MenuUtil.createContextMenuItem("Next", 150);
		next.setAccelerator(new KeyCodeCombination(KeyCode.D));
		next.setOnAction(e -> nextSong());
		next.setDisable(true);

		previous = MenuUtil.createContextMenuItem("Previous", 150);
		previous.setAccelerator(new KeyCodeCombination(KeyCode.A));
		previous.setOnAction(e -> previousSong());
		previous.setDisable(true);

		stop = MenuUtil.createContextMenuItem("Stop", 150);
		stop.setOnAction(e -> System.out.println("stop"));
		stop.setDisable(true);

		control.getItems().addAll(play, next, previous, stop);

		/*
		 * Help
		 */
		MenuItem about = MenuUtil.createContextMenuItem("About", 100);
		help.getItems().addAll(about);

		menuMain.getMenus().addAll(file, control, help);
	}

	private void initSearch() {
		CustomSearch customSearch = new CustomSearch(primaryStage, tableTracks, tfSearch);
		customSearch.createSeachPopup();
		customSearch.search();
		customSearch.init();
	}

	private void initTableSong() {
		tcName.setCellValueFactory(new PropertyValueFactory<Track, String>("name"));
		tcTime.setCellValueFactory(new PropertyValueFactory<Track, String>("time"));
		tcArtist.setCellValueFactory(new PropertyValueFactory<Track, String>("artist"));
		tcAlbum.setCellValueFactory(new PropertyValueFactory<Track, String>("album"));
		tcGenre.setCellValueFactory(new PropertyValueFactory<Track, String>("genre"));
		tcYear.setCellValueFactory(new PropertyValueFactory<Track, String>("year"));

		setupTableTracks(getPlaylistTracks(0), PlaceHolderUtil.LIB);
		tableTracks.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		tableTracks.setRowFactory(tv -> {
			TableRow<Track> row = new TableRow<>();
			ContextMenu contextMenu = new ContextMenu();
			MenuItem play = MenuUtil.createContextMenuItem("Play", 200);
			MenuItem getInfo = MenuUtil.createContextMenuItem("Get Info", 200);
			MenuItem showInWE = MenuUtil.createContextMenuItem("Show in Window Explorer", 200);
			MenuItem addToPl = MenuUtil.createContextMenuItem("Add to Playlist", 200);
			MenuItem delete = MenuUtil.createContextMenuItem("Delete", 200);

			ObservableList<Track> selectedTracks = tableTracks.getSelectionModel().getSelectedItems();
			play.setOnAction(e -> playSelectedTracks(tableTracks.getSelectionModel().getSelectedItem()));
			getInfo.setOnAction(e -> getInfoSelectedTracks(selectedTracks));
			showInWE.setOnAction(e -> showTrackInWE(selectedTracks));
			addToPl.setOnAction(e -> addTracksToPl(selectedTracks));
			delete.setOnAction(e -> deleteSelectedTracks(selectedTracks));

			// if (tableTracks.getSelectionModel().getSelectedItems().size() <= 1) {
			contextMenu.getItems().addAll(play, addToPl, delete);
			// } /*else {
			// contextMenu.getItems().addAll(play, getInfo, showInWE, addToPl, delete);
			// }*/
			row.contextMenuProperty()
					.bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));

			row.setOnMouseClicked(e -> onDoubleClickToPlayTrack(e, row));
			return row;
		});
	}

	private void initAllPlaylist() {
		hideAddTracksToPl();
		hideTitlePlaylist();

		playlists = mData.getPlaylists();
		lvPlaylists.setItems(playlists);
		Collections.sort(getPlaylistTracks(0));

		lvPlaylists.setPadding(new Insets(2, 0, 2, 15));
		lvPlaylists.setCellFactory(lv -> {
			CustomListCell cell = new CustomListCell(this);
			return cell;
		});

		scrollToSelectedItemListView(0);
		lvPlaylists.setOnMouseClicked(e -> selectPlaylist());

		lvAddTracksToPl.setCellFactory(lv -> new CustomListCellNewPl());
		lvAddTracksToPl
				.setPlaceholder(PlaceHolderUtil.createPlaceHolder(PlaceHolderUtil.ADD_TRACK_PLAYLIST, Pos.TOP_CENTER));

	}

	private void selectPlaylist() {
		int index = lvPlaylists.getSelectionModel().getSelectedIndex();
		if (index == 0) {
			setupTableTracks(getPlaylistTracks(index), PlaceHolderUtil.LIB);
			hideTitlePlaylist();
		} else {
			setupTableTracks(getPlaylistTracks(index), PlaceHolderUtil.PLAYLIST);
			showTitlePlaylist();
		}
		setupInfoPlaylist();
	}

	private void initControllSong() {
		bottomBar.setVisible(false);
		bottomBar.setManaged(false);

		volumeSlider.setValue(50);
		nextTrack.setOnMouseClicked(v -> nextSong());
		preTrack.setOnMouseClicked(v -> previousSong());
		playTrack.setOnMouseClicked(v -> pressPlayTrack());
		optionPlayTrack.setOnMouseClicked(v -> onChangeOptionPlayTrack());
	}

	private void onChangeOptionPlayTrack() {
		if (flagTypeNextSong == SONG_RANDOM)
			flagTypeNextSong = SONG_DEFAULT;
		else
			flagTypeNextSong++;

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
	}

	private void pressPlayTrack() {
		if (mMediaPlayer.getStatus().equals(Status.PLAYING)) {
			playTrack.setGlyphName("PLAY");
			mMediaPlayer.pause();
			play.setGraphic(MenuUtil.setTextLabel("Play", 150));
		} else if (mMediaPlayer.getStatus().equals(Status.PAUSED)) {
			playTrack.setGlyphName("PAUSE");
			mMediaPlayer.play();
			play.setGraphic(MenuUtil.setTextLabel("Pause", 150));
		}
	}

	public void onAddFiletoLibrary(ActionEvent e) {
		addFileToLib();
	}

	private void addFileToLib() {
		ObservableList<Track> tracksChooser = FileChooserUtil.getTracksChooser(primaryStage);
		if (tracksChooser != null && tracksChooser.size() > 0) {
			getPlaylistTracks(0).addAll(tracksChooser);
			Collections.sort(getPlaylistTracks(0));
			mData.updateLibraryFile(tracksChooser);
		}
	}

	private void addFolderToLib() {
		ObservableList<Track> tracksChooser = FileChooserUtil.addTracksFromFolder(primaryStage);
		if (tracksChooser != null && tracksChooser.size() > 0) {
			getPlaylistTracks(0).addAll(tracksChooser);
			Collections.sort(getPlaylistTracks(0));
			mData.updateLibraryFile(tracksChooser);
		}
	}

	private void onDoubleClickToPlayTrack(MouseEvent e, TableRow<Track> row) {
		if (e.getClickCount() == 2 && !row.isEmpty() && e.getButton() == MouseButton.PRIMARY) {
			if (row.getItem() != null)
				if (checkFile(row.getItem())) {
					playSong(row.getItem());

					play.setDisable(false);
					next.setDisable(false);
					stop.setDisable(false);
					previous.setDisable(false);
				}
		}
	}

	public void showListViewAddTracks(MouseEvent e) {
		showAddTracks();
	}

	private void showAddTracks() {
		hideTitlePlaylist();
		hideNavigationPl();
		showAddTracksToPl();
		setupTableTracks(getPlaylistTracks(0), PlaceHolderUtil.LIB);
		setupInfoPlaylist();
	}

	private void setupInfoPlaylist() {
		lvAddTracksToPl.setItems(getSelectedPlaylist().getTracks());
		updateTotalItemPlaylist();
	}

	public void onCreateNewPl(MouseEvent e) {
		createNewPlaylist();
	}

	private void createNewPlaylist() {
		String name = DialogUtil.createDialog("New Playlist", "Playlist");
		if (!name.isEmpty()) {
			showTitlePlaylist();

			long id = mData.getNextPlaylistId();
			Playlist newPl = new Playlist(id, "Playlist");
			newPl.setName(name);
			playlists.add(newPl);

			mData.updatePlaylist(newPl);
			scrollToSelectedItemListView(playlists.size() - 1);
			setupTableTracks(newPl.getTracks(), PlaceHolderUtil.PLAYLIST);
		}
	}

	public void handleDragTracksToNewPl(MouseEvent e) {
		ArrayList<Track> tracks = new ArrayList<>(tableTracks.getSelectionModel().getSelectedItems());
		if (tracks.size() > 0) {
			Dragboard board = tableTracks.startDragAndDrop(TransferMode.COPY);
			ClipboardContent cb = new ClipboardContent();
			cb.put(dragTracksFormat, tracks);
			board.setContent(cb);
			e.consume();
		}
	}

	@SuppressWarnings("unchecked")
	public void handleDragTracksDropped(DragEvent event) {
		ArrayList<Track> tracks = (ArrayList<Track>) event.getDragboard().getContent(dragTracksFormat);
		getSelectedPlaylist().getTracks().addAll(tracks);
		getSelectedPlaylist().setKeysByTracks(tracks);

		if (tracks.size() > 0) {
			lvAddTracksToPl.setItems(getSelectedPlaylist().getTracks());
			Platform.runLater(() -> {
				lvAddTracksToPl.scrollTo(lvAddTracksToPl.getItems().size() - 1);
				lvAddTracksToPl.getSelectionModel().select(lvAddTracksToPl.getItems().size() - 1);
				updateTotalItemPlaylist();
			});
			mData.updateTracksToPlaylist(getSelectedPlaylist());
		}
	}

	public void handleDragTracksOverNewPl(DragEvent event) {
		if (event.getDragboard().hasContent(dragTracksFormat))
			event.acceptTransferModes(TransferMode.COPY);
	}

	public void onDoneAddTracksToPl(MouseEvent e) {
		showNavigationPl();
		hideAddTracksToPl();
		setupTableTracks(getSelectedPlaylist().getTracks(), PlaceHolderUtil.PLAYLIST);
	}

	public void handleDragFilesOverTableView(DragEvent event) {
		if (event.getDragboard().hasFiles())
			event.acceptTransferModes(TransferMode.LINK);
	}

	public void handleDragFilesDropped(DragEvent event) {
		List<File> files = event.getDragboard().getFiles();
		List<File> listFiles = new ArrayList<File>();
		if (files.size() > 0) {
			for (File file : files) {
				if (file.isDirectory()) {
					FileChooserUtil.readFilesFromFolder(listFiles, file);
				} else {
					listFiles.add(file);
				}
			}
			addFilesToLib(listFiles);
		}
	}

	private void addFilesToLib(List<File> listFiles) {
		ObservableList<Track> listTracks = FileChooserUtil.getTracksByDragFiles(listFiles);
		if (listTracks != null && listTracks.size() > 0) {
			getPlaylistTracks(0).addAll(listTracks);
			mData.updateLibraryFile(listTracks);
		}
	}

	private void updateTotalItemPlaylist() {
		String numSongs = (getSelectedPlaylist().getTracks().size() == 0) ? ("Zero items")
				: ((getSelectedPlaylist().getTracks().size() == 1) ? "1 song"
						: (getSelectedPlaylist().getTracks().size() + " songs"));

		totalItemNewPl.setText(numSongs);
		totalTracksPl.setText(numSongs);
		namePlaylist.setText(getSelectedPlaylist().getName());
		namePlToAddTracks.setText(getSelectedPlaylist().getName());
	}

	private void playSong(Track song) {
		playingTrack = song;
		bottomBar.setVisible(true);
		bottomBar.setManaged(true);

		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
		}

		File file = null;
		try {
			file = new File(new URI(song.getLocation()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		if (FilenameUtils.getExtension(file.getName()).equals("wma")) {
			mMedia = new Media(ControllerPlaySong.conver(song.getLocation().substring(6)));
		} else {
			mMedia = new Media(song.getLocation());
		}
		mMediaPlayer = new MediaPlayer(mMedia);
		mMediaPlayer.setAutoPlay(true);

		mMediaPlayer.setOnReady(() -> {
			duration = mMediaPlayer.getMedia().getDuration();
			updateTimeTrackBar();
		});

		playTrack.setGlyphName("PAUSE");
		mMediaPlayer.setOnEndOfMedia(() -> nextSong());

		int index = tableTracks.getSelectionModel().getSelectedIndex();
		Track track = tableTracks.getItems().get(index);
		nameTrack.setText(track.getName().toString());
		artistTrack.setText(track.getArtist().toString());
		coverTrack.setImage(ImageUtil.setCoverImage(track.getCoverImage()));

		mMediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> updateTimeTrackBar());

		trackSlider.valueProperty().addListener((observable) -> {
			if (trackSlider.isValueChanging()) {
				if (duration != null)
					mMediaPlayer.seek(duration.multiply(trackSlider.getValue() / trackSlider.getMax()));
				updateTimeTrackBar();
			}
		});

		trackSlider.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			updateTimeTrackBar();
		});

		updatePlayer();

		volumeSlider.valueProperty().addListener((observable) -> {
			if (volumeSlider.isValueChanging())
				updatePlayer();
		});

		volumeSlider.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> updatePlayer());
	}

	private boolean checkFile(Track track) {
		try {
			File result = new File(new URI(track.getLocation()));
			if (result == null || !result.exists()) {
				result = DialogUtil.locateFile(primaryStage, track);
				if (result != null) {
					mData.deleteTrack(track);
					getPlaylistTracks(0).remove(track);

					Track newTrack = MetadataParser.createTrack(result);
					mData.writeTrack(newTrack);
					getPlaylistTracks(0).add(newTrack);
					Collections.sort(getPlaylistTracks(0));
				}
				return false;
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return true;
	}

	private void updatePlayer() {
		mMediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
		volumeProgressBar.setProgress(volumeSlider.getValue() / volumeSlider.getMax());
	}

	protected void updateTimeTrackBar() {
		if (timeUp != null && trackSlider != null && duration != null) {
			Platform.runLater(() -> {
				Duration currentTime = mMediaPlayer.getCurrentTime();
				timeUp.setText(DurationUtil.formatTime(currentTime));
				timeDown.setText(DurationUtil.formatTime(duration));

				trackSlider.setDisable(duration.isUnknown());
				trackSlider.setValue(currentTime.toMillis() * trackSlider.getMax() / duration.toMillis());
				trackProgressBar.setProgress(trackSlider.getValue() / trackSlider.getMax());
			});
		}
	}

	private void nextSong() {
		int idx = tableTracks.getSelectionModel().getSelectedIndex();
		Track track = null;
		switch (flagTypeNextSong) {
		case SONG_REPEAT:
			track = getSelectedPlaylist().getTracks().get(idx);
			if (track != null) {
				playSong(track);
			}
			break;

		case SONG_RANDOM:
			Random rd = new Random();
			idx = rd.nextInt(getSelectedPlaylist().getTracks().size() - 1);
			tableTracks.getSelectionModel().clearAndSelect(idx);
			track = tableTracks.getItems().get(idx);
			if (track != null) {
				playSong(track);
			}
			break;

		default:
			if (getSelectedPlaylist().getTracks().size() == idx + 1)
				idx = -1;

			tableTracks.getSelectionModel().clearAndSelect(idx + 1);
			track = tableTracks.getItems().get(idx + 1);
			if (track != null) {
				playSong(track);
			}
			break;
		}
		scrollToSelectedItemListView(idx);
	}

	private void previousSong() {
		int idx = tableTracks.getSelectionModel().getSelectedIndex();
		if (idx == 0) {
			idx = getPlaylistTracks(0).size();
		}
		tableTracks.getSelectionModel().clearAndSelect(idx - 1);
		Track track = tableTracks.getItems().get(idx - 1);
		if (track != null) {
			playSong(track);
		}
	}

	private void deleteSelectedTracks(ObservableList<Track> selectedTracks) {
		int index = lvPlaylists.getSelectionModel().getSelectedIndex();

		if (mMediaPlayer != null && selectedTracks.contains(playingTrack)) {
			mMediaPlayer.stop();
			bottomBar.setVisible(false);
			bottomBar.setManaged(false);
		}

		updatePlaylistsFromLib(selectedTracks);
		if (index == 0) {
			mData.deleteTracks(selectedTracks);
			lvPlaylists.getItems().get(0).getTracks().removeAll(selectedTracks);
		} else {
			mData.updateTracksToPlaylist(getSelectedPlaylist());
			updateTotalItemPlaylist();
		}
	}

	private void updatePlaylistsFromLib(ObservableList<Track> selectedTracks) {
		for (int i = 1; i < lvPlaylists.getItems().size(); i++) {
			Playlist playlist = lvPlaylists.getItems().get(i);
			playlist.removeAllTrack(selectedTracks);
			playlist.removeAllKey();
			mData.updateTracksToPlaylist(playlist);
		}
	}

	private void addTracksToPl(ObservableList<Track> selectedTracks) {
	}

	private void showTrackInWE(ObservableList<Track> selectedTracks) {
	}

	private void getInfoSelectedTracks(ObservableList<Track> selectedTracks) {
	}

	private void playSelectedTracks(Track selectedTrack) {
		if (checkFile(selectedTrack))
			playSong(selectedTrack);
	}

	private void scrollToSelectedItemListView(int pos) {
		Platform.runLater(() -> {
			lvPlaylists.scrollTo(pos);
			lvPlaylists.getSelectionModel().select(pos);
			updateTotalItemPlaylist();
		});
	}

	private ObservableList<Track> getPlaylistTracks(int index) {
		return playlists.get(index).getTracks();
	}

	private Playlist getSelectedPlaylist() {
		return lvPlaylists.getSelectionModel().getSelectedItem();
	}

	private void setupTableTracks(ObservableList<Track> items, String description) {
		tableTracks.setItems(items);
		tableTracks.setPlaceholder(PlaceHolderUtil.createPlaceHolder(description, Pos.CENTER));
	}

	private void showAddTracksToPl() {
		addTracksAP.setVisible(true);
		addTracksAP.setManaged(true);
	}

	private void hideNavigationPl() {
		navigationAnchorPane.setManaged(false);
		navigationAnchorPane.setVisible(false);
	}

	private void showNavigationPl() {
		navigationAnchorPane.setVisible(true);
		navigationAnchorPane.setManaged(true);
	}

	private void hideAddTracksToPl() {
		addTracksAP.setManaged(false);
		addTracksAP.setVisible(false);
		titlePlaylistBorderPane.setManaged(true);
		titlePlaylistBorderPane.setVisible(true);
	}

	private void showTitlePlaylist() {
		titlePlaylistBorderPane.setManaged(true);
		titlePlaylistBorderPane.setVisible(true);
	}

	private void hideTitlePlaylist() {
		titlePlaylistBorderPane.setManaged(false);
		titlePlaylistBorderPane.setVisible(false);
	}

	public void editPlaylist(int index) {
		if (index == lvPlaylists.getSelectionModel().getSelectedIndex()) {
			showAddTracks();
		}
	}

	public void deletePlaylist(int index) {
		if (index == lvPlaylists.getSelectionModel().getSelectedIndex()) {
			mData.deletePlaylist(getSelectedPlaylist());
			playlists.remove(index);
			
			if (playlists.size() > 1) {
				if (playlists.size() > index) {
					scrollToSelectedItemListView(index);
				} else {
					index = playlists.size() - 1;
					scrollToSelectedItemListView(index);
				}
				setupTableTracks(getPlaylistTracks(index), PlaceHolderUtil.PLAYLIST);
			} else {
				scrollToSelectedItemListView(0);
				setupTableTracks(getPlaylistTracks(0), PlaceHolderUtil.LIB);
			}
		}
	}

	public void renamePlaylist(int index) {
		if (index == lvPlaylists.getSelectionModel().getSelectedIndex()) {
			String name = DialogUtil.createDialog("Rename Playlist", getSelectedPlaylist().getName());
			if (!name.isEmpty()) {
				getSelectedPlaylist().setName(name);
				mData.renamePlaylist(getSelectedPlaylist());
				lvPlaylists.setItems(playlists);
				lvPlaylists.setCellFactory(lv -> new CustomListCell(this));
				updateTotalItemPlaylist();
			}
		}
	}
}
