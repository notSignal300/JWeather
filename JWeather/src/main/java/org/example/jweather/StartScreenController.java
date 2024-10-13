package org.example.jweather;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class StartScreenController {
    @FXML
    TextField cityTextField, apiTextField;
    @FXML
    Button searchBtn;
    @FXML
    RadioButton metricRadioBtn, imperialRadioBtn;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private void searchWeather(ActionEvent event) throws IOException{
        String city = URLencodeString(cityTextField.getText());
        final String API = apiTextField.getText();
        String unitSys = getUnitSysSelected();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        root = loader.load();

        HelloController weatherController = loader.getController();
        weatherController.start(city, API, unitSys);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private String getUnitSysSelected(){
        if(metricRadioBtn.isSelected()){ return "metric"; }
        else { return "imperial"; }
    }

    private static String URLencodeString(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }
}
