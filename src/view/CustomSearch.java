package view;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import pojos.Track;
import util.PlaceHolderUtil;
import util.Search;

public class CustomSearch {

	private Stage searchPopup;
	private Stage primaryStage;
	private TableView<Track> tableTracks;
	private TextField tfSearch;

	public CustomSearch(Stage primaryStage, TableView<Track> tableTracks, TextField tfSearch) {
		this.primaryStage = primaryStage;
		this.tableTracks = tableTracks;
		this.tfSearch = tfSearch;
	}

	public void init() {
		primaryStage.xProperty().addListener((observable, oldValue, newValue) -> {
			if (searchPopup.isShowing() && !searchHideAnimation.getStatus().equals(Animation.Status.RUNNING)) {
				searchHideAnimation.play();
			}
		});
		primaryStage.yProperty().addListener((observable, oldValue, newValue) -> {
			if (searchPopup.isShowing() && !searchHideAnimation.getStatus().equals(Animation.Status.RUNNING)) {
				searchHideAnimation.play();
			}
		});

	}

	public void createSeachPopup() {
		try {
			ListView<Node> view = new ListView<>();
			view.getStyleClass().add("searchPopup");
			Stage popup = new Stage();
			popup.setScene(new Scene(view));
			popup.initStyle(StageStyle.UNDECORATED);
			popup.initStyle(StageStyle.TRANSPARENT);
			popup.initOwner(primaryStage);
			searchHideAnimation.setOnFinished(x -> popup.hide());
			popup.show();
			popup.hide();
			searchPopup = popup;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Animation searchHideAnimation = new Transition() {
		{
			setCycleDuration(Duration.millis(250));
			setInterpolator(Interpolator.EASE_BOTH);
		}

		protected void interpolate(double frac) {
			searchPopup.setOpacity(1.0 - frac);
		}
	};

	private Animation searchShowAnimation = new Transition() {
		{
			setCycleDuration(Duration.millis(250));
			setInterpolator(Interpolator.EASE_BOTH);
		}

		protected void interpolate(double frac) {
			searchPopup.setOpacity(frac);
		}
	};

	public void search() {
		ObservableList<Track> formatListSong = formatListSong();
		tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null || newValue != "") {
				String formatNewValue = deAccent(newValue);
				Search.search(formatNewValue, formatListSong);
			}
		});
		tfSearch.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (tfSearch.getText().isEmpty()) {
					searchHideAnimation.play();
				} else {
					String formatNewValue = deAccent(tfSearch.getText());
					Search.search(formatNewValue, formatListSong);
				}
			}
		});
		tableTracks.setOnMousePressed(e -> searchHideAnimation.play());

		Search.hasResultsProperty().addListener((observable, hadResults, hasResults) -> {
			if (hasResults) {
				List<Track> result = Search.getResult();
				List<Track> newListSongForSearching = new ArrayList<>();
				for (Track resultTrack : result) {
					for (Track track : tableTracks.getItems()) {
						if (track.getId() == resultTrack.getId()) {
							newListSongForSearching.add(track);
						}
					}
				}
				Platform.runLater(() -> {
					showSearchResults(tableTracks.getItems(), newListSongForSearching);
					primaryStage.toFront();
				});
			}
		});
	}

	public void showSearchResults(ObservableList<Track> listSong, List<Track> result) {
		@SuppressWarnings("unchecked")
		ListView<Track> root = (ListView<Track>) searchPopup.getScene().getRoot();
		root.setPrefWidth(295);
		root.setMaxHeight(Control.USE_PREF_SIZE);
		root.setMinHeight(Control.USE_PREF_SIZE);
		root.setOrientation(Orientation.VERTICAL);
		root.setFixedCellSize(61);
		root.getStylesheets().add(this.getClass().getResource("/css/newpl.css").toExternalForm());
		root.setCellFactory(lv -> new CustomListCellNewPl());
		root.setStyle("-fx-border-color:black");
		root.setPlaceholder(PlaceHolderUtil.createPlaceHolderSearch("No Result", Pos.TOP_CENTER));

		ObservableList<Track> list = FXCollections.observableArrayList();
		list.clear();
		list.addAll(result);
		
		if (list.size() < 0) {
			root.setPrefHeight(50);
		}

		root.setOnMouseClicked(e -> {
			scrollToSelectedItem(root.getSelectionModel().getSelectedItem());
			searchHideAnimation.play();
		});
		root.setItems(list);

		if (!searchPopup.isShowing()) {
			searchPopup.setX(tfSearch.getLayoutX() + 3);
			searchPopup.setY(tfSearch.getLayoutY() + 82);
			searchPopup.show();
			searchShowAnimation.play();
		}
	}

	private void scrollToSelectedItem(Track track) {
		Platform.runLater(() -> {
			tableTracks.getSelectionModel().clearSelection();
			tableTracks.scrollTo(track);
			tableTracks.getSelectionModel().select(track);
		});
	}

	public ObservableList<Track> formatListSong() {
		ObservableList<Track> tmpListSong = tableTracks.getItems();
		for (Track song : tableTracks.getItems()) {
			song.setName(deAccent(song.getName()));
			song.setArtist(deAccent(song.getArtist()));
			song.setAlbum(deAccent(song.getAlbum()));
		}
		return tmpListSong;
	}

	public static String deAccent(String str) {
		String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}
}
