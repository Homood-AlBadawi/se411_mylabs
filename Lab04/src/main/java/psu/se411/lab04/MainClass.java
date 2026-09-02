package psu.se411.lab04;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * SE411 - Lab 04: Maven intro.
 *
 * A minimal JavaFX application. It is launched through the
 * javafx-maven-plugin, not by running this class directly:
 *
 * <pre>
 * mvn clean compile javafx:run
 * </pre>
 *
 * Running it as a plain Java Application fails with "JavaFX runtime
 * components are missing", because the JavaFX modules have to be put on the
 * module path — which is exactly what the plugin does for us.
 */
public class MainClass extends Application {

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("My Project");

			Label title = new Label("SE411 - Lab 04");
			Label subtitle = new Label("Maven + JavaFX are working.");

			Button closeButton = new Button("Close");
			closeButton.setOnAction(event -> primaryStage.close());

			VBox root = new VBox(15, title, subtitle, closeButton);
			root.setAlignment(Pos.CENTER);
			root.setPadding(new Insets(30));

			primaryStage.setScene(new Scene(root, 420, 220));
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
