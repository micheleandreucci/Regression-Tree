package map7Client;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Il main della classe applicazione.
 */
public class MainTest extends Application
{
	private Stage primaryStage;
    private BorderPane rootLayout;
    /**
	 * Il punto di ingresso.
	 * @param args L'argomento del programma
	 */
	public static void main(final String[] args) {
		launch(args);
	}

	/**
	 * Avvio applicazione client.
	 * @param stage The javafx stage
	 */
	@Override
	public void start(Stage stage) throws ServerException {

		this.primaryStage = stage;
        this.primaryStage.setTitle("Regression Tree");

       // Set the application icon.
        this.primaryStage.getIcons().add(new Image("file:resources/images/icofinder.png"));

        initLayout();

        showTreeOverview();
	}
	/*
	 * Inizializza il layout
	 */
	public void initLayout()
    {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainTest.class
                    .getResource("InformationLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            //InformationUI controller = loader.getController();
            //controller.setMainApp(this);
//            primaryStage.setOnCloseRequest(event -> {
//    			try {
//    				disconnect();
//    			} catch (IOException e) {
//    				System.err.println(e.getMessage());
//    			}
//    		});

            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

	public Stage getPrimaryStage() {
		return primaryStage;
	}
	/**
     * Mostra una overview dell'albero presente in Overview
     */
    public void showTreeOverview()
    {
        try
        {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainTest.class.getResource("TreeOverview.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(personOverview);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}