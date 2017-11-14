package application;

import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.GroupLayout.SequentialGroup;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class MainController implements Initializable {
	// ==================Table Songs===========================
	@FXML
	private TableView<Song> tableSongs;
	@FXML
	private TableColumn<Song, String> tableName;
	@FXML
	private TableColumn<Song, String> tableTime;
	@FXML
	private TableColumn<Song, String> tableArtist;
	@FXML
	private TableColumn<Song, String> tableAlbum;
	@FXML
	private TableColumn<Song, String> tableGenre;
	// ========================================================

	// =====================Controller ListSong==================
	@FXML
	private VBox controller;
	@FXML
	private HBox mainListController;
	@FXML
	private ImageView mainListControllerIcon;
	@FXML
	private Text mainListControllerText;

	@FXML
	private HBox newPlaylistController;
	@FXML
	private ImageView newPlaylistControllerIcon;
	@FXML
	private Text newPlaylistControllerText;

	@FXML
	private ListView<Playlist> lvPlaylist;
	private ObservableList<Playlist> playList;

	// private ObservableList<String> listNamePlaylist;
	// ==========================================================

	public ObservableList<Song> listSong = FXCollections.observableArrayList(
			new Song("Blue", "4:03:", "Big Bang", "Bong Bong Bong Bong Bong Bong Bong Bong", "POP"));

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initMainPLaylist();
		initTableSong();
		initListSong();
		initAllNewPlaylist();

	}

	private void initAllNewPlaylist() {
		playList = FXCollections.observableArrayList();
		updateItems();
		lvPlaylist.setItems(playList);
		lvPlaylist.setOnMouseClicked(v -> {
			setSelectedMainList(false);
//			Playlist item = lvPlaylist.getSelectionModel().getSelectedItem();
		});
	}

	private void updateItems() {
		lvPlaylist.setCellFactory(new Callback<ListView<Playlist>, ListCell<Playlist>>() {

			@Override
			public ListCell<Playlist> call(ListView<Playlist> param) {
				ListCell<Playlist> cell = new ListCell<Playlist>() {
					@Override
					protected void updateItem(Playlist item, boolean empty) {
						super.updateItem(item, empty);
						if (!empty) {
							Image img = new Image(item.getPathIcon());
							ImageView icon = new ImageView(img);
							icon.setFitHeight(32);
							icon.setFitWidth(32);
							setGraphic(icon);
							setText(item.getFullName());
						} else {
							setGraphic(null);
							setText(null);
						}
					}
				};
				return cell;
			}
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
			playList.add(createNewPlaylist());
			lvPlaylist.getSelectionModel().select(playList.size() - 1);
			lvPlaylist.getFocusModel().focus(playList.size() - 1);
			lvPlaylist.scrollTo(playList.size() - 1);
		});
		newPlaylistController.setOnMouseReleased(v -> setSelectedNewPlaylist(false));
	}

	private Playlist createNewPlaylist() {
		Playlist newPlaylist = new Playlist();
		newPlaylist.setName("Playlist");
		newPlaylist.setId(setIdPlaylist());
		newPlaylist.setPathIcon("/icons/ic_playlist.png");
		newPlaylist.setFullName("Playlist" + newPlaylist.getId());
		return newPlaylist;
	}

	private int setIdPlaylist() {
		int result = 0;
		Playlist pl;
		for (int i = 0; i < playList.size(); i++) {
			pl = playList.get(i);
			if (pl.getName() == "Playlist") {
				result = pl.getId();
				break;
			}
		}
		for (int i = 1; i < playList.size(); i++) {
			pl = playList.get(i);
			if (pl.getName() == "Playlist" && pl.getId() > result) {
				result = pl.getId();
			}
		}
		return result + 1;
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

	private void initTableSong() {
		tableName.setCellValueFactory(new PropertyValueFactory<Song, String>("Name"));
		tableTime.setCellValueFactory(new PropertyValueFactory<Song, String>("Time"));
		tableArtist.setCellValueFactory(new PropertyValueFactory<Song, String>("Artist"));
		tableAlbum.setCellValueFactory(new PropertyValueFactory<Song, String>("Album"));
		tableGenre.setCellValueFactory(new PropertyValueFactory<Song, String>("Genre"));
		tableSongs.setItems(listSong);
	}

	private void initListSong() {
	}

}
