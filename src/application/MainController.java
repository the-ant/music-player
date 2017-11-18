package application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import data.DataAccess;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import pojos.Playlist;
import pojos.Track;
import util.CustomPlaylist;
import util.DirectoryUtil;
import util.ListViewUtil;
import util.MenuUtil;
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initMainPLaylist();
		initTableSong();
		initListSong();
		initAllNewPlaylist();
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

	private void play(Track song) {
		String filePath = song.getLocation();
		if (filePath != null) {
			if (mMediaPlayer != null) {
				mMediaPlayer.stop();
			}
			mMedia = new Media(filePath);
			mMediaPlayer = new MediaPlayer(mMedia);
			mMediaPlayer.setAutoPlay(true);
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
					if (track != null) {
						play(track);
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
