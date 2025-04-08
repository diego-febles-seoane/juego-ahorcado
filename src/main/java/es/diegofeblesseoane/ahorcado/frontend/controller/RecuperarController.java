package es.diegofeblesseoane.ahorcado.frontend.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RecuperarController {

    @FXML
    private TextField emailOrUserField;

    @FXML
    private void handleRecuperar(ActionEvent event) {
        String input = emailOrUserField.getText().trim();

        if (input.isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Campo vacío", "Por favor, introduce tu usuario o correo.");
            return;
        }

        mostrarAlerta(AlertType.INFORMATION, "Recuperación iniciada", "Se ha enviado un correo");
    }

    @FXML
    private void clickVolver(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/diegofeblesseoane/ahorcado/login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
