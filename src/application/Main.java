package application;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

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
			Parent root = FXMLLoader.load(getClass().getResource("/view/MainLayout.fxml"));
			primaryStage.setTitle("Music Player");
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
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
