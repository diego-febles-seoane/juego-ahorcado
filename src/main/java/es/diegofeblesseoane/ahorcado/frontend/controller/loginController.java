package es.diegofeblesseoane.ahorcado.frontend.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import es.diegofeblesseoane.ahorcado.backend.model.UsuarioEntity;
import es.diegofeblesseoane.ahorcado.backend.model.UsuarioServiceModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class loginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private UsuarioServiceModel servicioUsuario;

    @FXML
    public void initialize() {
        try {
            URL dbUrl = getClass().getResource("/database/usuarios.db");
            if (dbUrl != null) {
                servicioUsuario = new UsuarioServiceModel(dbUrl.getPath());
            } else {
                System.err.println("No se encontró la base de datos.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAceptar(ActionEvent event) throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Campos vacíos", "Por favor, introduce usuario y contraseña.");
            return;
        }

        if (servicioUsuario == null) {
            mostrarAlerta(AlertType.ERROR, "Error interno", "No se pudo conectar a la base de datos.");
            return;
        }

        UsuarioEntity usuario = servicioUsuario.obtenerUsuarioPorNombre(username);

        if (usuario != null && usuario.getContrasenia().equals(password)) {
            cambiarEscenaGuardandoDatos(event, "/es/diegofeblesseoane/ahorcado/perfil.fxml", usuario);
        } else {
            mostrarAlerta(AlertType.ERROR, "Error de autenticación", "Usuario o contraseña incorrectos.");
        }
    }

    @FXML
    private void handleRegistrar(ActionEvent event) throws IOException {
        cambiarEscena(event, "/es/diegofeblesseoane/ahorcado/registrarUsuario.fxml");
    }

    @FXML
    private void handleListarUsuarios(ActionEvent event) throws IOException {
        cambiarEscena(event, "/es/diegofeblesseoane/ahorcado/listaUsuarios.fxml");
    }

    @FXML
    private void handleRecuperar(ActionEvent event) throws IOException {
        cambiarEscena(event, "/es/fdiegofeblesseoane/ahorcado/recuperar.fxml");
    }

    private void cambiarEscena(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void cambiarEscenaGuardandoDatos(ActionEvent event, String fxmlPath, UsuarioEntity usuario)
            throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        // Pasar el usuario al controlador de perfil
        if (fxmlPath.contains("perfil.fxml")) {
            perfilController controller = loader.getController();
            controller.setUsuario(usuario);
        }

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
