package application;


import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

public class Main extends Application {
	private static Stage state;
	private double xOffset = 0;
	private double yOffset = 0;
	public static Stage getPrimaryStage() {
		return Main.state;
	}
	
//	private void setPrimaryStage(Stage stage) {
//		Main.state = stage;
//	}

	@Override
	public void start(Stage primaryStage) {
		Main.state = primaryStage;
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/view/MainLayout.fxml"));
			primaryStage.setTitle("Music Player");
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			//setPrimaryStage(primaryStage);
			root.setOnMousePressed(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					xOffset = event.getSceneX();
					yOffset = event.getSceneY();
				};
			});
			root.setOnMouseDragged(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					primaryStage.setX(event.getSceneX() - xOffset);
					primaryStage.setY(event.getSceneY() - yOffset);
				};
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
