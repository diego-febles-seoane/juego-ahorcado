package es.diegofeblesseoane.ahorcado.frontend.controller;

import java.io.IOException;

import es.diegofeblesseoane.ahorcado.backend.model.UsuarioEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class perfilController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField nivelField;

    private UsuarioEntity usuario;

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        if (usuario != null) {
            usernameField.setText(usuario.getNombre());
            emailField.setText(usuario.getEmail());
            nivelField.setText(usuario.getNivel()); 
        }
    }

    @FXML
    private void handleEditar(ActionEvent event) throws IOException {
        cambiarEscena(event, "/es/diegofeblesseoane/ahorcado/editarUsuario.fxml");
    }

    @FXML
    private void handleJugar(ActionEvent event) throws IOException {
        cambiarEscena(event, "/es/diegofeblesseoane/ahorcado/juego.fxml");
    }

    @FXML
    private void handleRegresar(ActionEvent event) throws IOException {
        cambiarEscena(event, "/es/diegofeblesseoane/ahorcado/login.fxml");
    }

    private void cambiarEscena(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
