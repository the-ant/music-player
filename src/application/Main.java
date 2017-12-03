package application;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class Main extends Application {
	private static Stage state;
	
	public static Stage getPrimaryStage() {
		return Main.state;
	}
	
	private void setPrimaryStage(Stage stage) {
		Main.state = stage;
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/util/MainLayout.fxml"));
			primaryStage.setTitle("Music Player");
			Scene scene = new Scene(root);
			scene.getStylesheets().add(this.getClass().getResource("/css/app.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image("/icons/ic_app.png"));
			primaryStage.show();
			
			setPrimaryStage(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
