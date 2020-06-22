package databaseFrontend;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class controller {

    public enum screenStates {
        add,
        modify
    }

    public void showSubSceneCommon(String newScreenFXML, screenStates state, Control ctrl){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(newScreenFXML));
        try {
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            //stage.initStyle(StageStyle.UNDECORATED);
            //stage.setTitle("ABC");
            stage.setScene(new Scene(root1));
            stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ctrl.getScene().getWindow().hide();
    }

}
