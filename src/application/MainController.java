package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainController implements Initializable{
	
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

	public ObservableList<Song> listSong = FXCollections.observableArrayList(
			new Song("Blue", "4:03:", "Big Bang", "Bong Bong", "POP"),
			new Song("Blue", "4:03:", "Big Bang", "Bong Bong", "POP"),
			new Song("Blue", "4:03:", "Big Bang", "Bong Bong", "POP"),
			new Song("Blue", "4:03:", "Big Bang", "Bong Bong", "POP"),
			new Song("Blue", "4:03:", "Big Bang", "Bong Bong", "POP"),
			new Song("Blue", "4:03:", "Big Bang", "Bong Bong", "POP"),
			new Song("Blue", "4:03:", "Big Bang", "Bong Bong", "POP"),
			new Song("Blue", "4:03:", "Big Bang", "Bong Bong", "POP"),
			new Song("Blue", "4:03:", "Big Bang", "Bong Bong", "POP"),
			new Song("Blue", "4:03:", "Big Bang", "Bong Bong", "POP"),
			new Song("Blue", "4:03:", "Big Bang", "Bong Bong", "POP"),
			new Song("Blue", "4:03:", "Big Bang", "Bong Bong", "POP"),
			new Song("Blue", "4:03:", "Big Bang", "Bong Bong", "POP"),
			new Song("Blue", "4:03:", "Big Bang", "Bong Bong", "POP"),
			new Song("Blue", "4:03:", "Big Bang", "Bong Bong", "POP"),
			new Song("Blue", "4:03:", "Big Bang", "Bong Bong", "POP"),
			new Song("Blue", "4:03:", "Big Bang", "Bong Bong", "POP")
			);
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tableName.setCellValueFactory(new PropertyValueFactory<Song, String>("Name"));
		tableTime.setCellValueFactory(new PropertyValueFactory<Song, String>("Time"));
		tableArtist.setCellValueFactory(new PropertyValueFactory<Song, String>("Artist"));
		tableAlbum.setCellValueFactory(new PropertyValueFactory<Song, String>("Album"));
		tableGenre.setCellValueFactory(new PropertyValueFactory<Song, String>("Genre"));
		
		tableSongs.setItems(listSong);
	}
	
}
