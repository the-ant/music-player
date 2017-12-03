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
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import pojos.Track;
import util.ImageUtil;
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
		ListView<Node> root = (ListView<Node>) searchPopup.getScene().getRoot();
		root.setPrefWidth(295);
		root.setMaxHeight(300);
		root.setOrientation(Orientation.VERTICAL);

		ObservableList<Node> list = FXCollections.observableArrayList();
		list.clear();

		if (result.size() > 0) {
			result.forEach(track -> {
				HBox cell = new HBox();
				cell.setAlignment(Pos.CENTER_LEFT);

				ImageView image = new ImageView();
				image.setFitHeight(55);
				image.setFitWidth(55);

				byte[] coverByte = track.getCoverImage();
				Image customImage = ImageUtil.setCoverImage(coverByte);

				image.setImage(customImage);
				cell.getChildren().add(image);

				HBox.setMargin(image, new Insets(0, 5, 0, 5));

				Label lbSongName = new Label(track.getName());
				lbSongName.setTextOverrun(OverrunStyle.CLIP);
				lbSongName.getStyleClass().setAll("searchLabel");

				cell.getChildren().add(lbSongName);
				HBox.setMargin(lbSongName, new Insets(0, 5, 0, 5));

				cell.getStyleClass().add("searchResult");
				cell.setOnMouseClicked(event -> {
					Track mTrack = listSong.stream().filter(x -> x.getName().equals(track.getName())).findAny().get();
					ObservableList<Track> newlistSelection = FXCollections.observableArrayList();
					newlistSelection.add(mTrack);
					tableTracks.setItems(newlistSelection);
					tfSearch.setText("");
					searchHideAnimation.play();
				});
				list.add(cell);
			});
			root.setItems(list);

		} else if (list.size() == 0) {
			Label lbAlert = new Label("No results");
			list.add(lbAlert);
			VBox.setMargin(lbAlert, new Insets(10, 10, 10, 10));
		}
		if (!searchPopup.isShowing()) {
//			searchPopup.setX(primaryStage.getX() + 890);
//			searchPopup.setY(primaryStage.getY() + 90);
			searchPopup.setX(tfSearch.getLayoutX() + 85);
			searchPopup.setY(tfSearch.getLayoutY() + 90);
			searchPopup.show();
			searchShowAnimation.play();
		}
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

	// To transfer Vietnamese to normal style no sign.
	public static String deAccent(String str) {
		String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}
}
