package es.diegofeblesseoane.ahorcado.frontend.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import es.diegofeblesseoane.ahorcado.backend.model.UsuarioEntity;
import es.diegofeblesseoane.ahorcado.backend.model.abstractas.Conexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class juegoController extends Conexion{
    
    @FXML
    private Label usernameLabel;
    @FXML
    private Label nivelLabel;
    @FXML
    private Label palabraLabel;
    @FXML
    private TextField inputLetra;
    @FXML
    private Button btnRegresar;
    @FXML
    private Button btnProbar;

    private UsuarioEntity usuario;
    private String palabraSecreta;
    private StringBuilder palabraOculta;
    private Set<Character> letrasUsadas = new HashSet<>();
    private int errores = 0;
    private final int MAX_ERRORES = 6;

    @FXML
    private void clickbtnRegresar(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/diegofeblesseoane/ahorcado/perfil.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void initialize() {
        btnProbar.setOnAction(this::probarLetra);
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
        usernameLabel.setText(usuario.getNombre());
        nivelLabel.setText(usuario.getNivel());
        inicializarJuego();
    }

    private void inicializarJuego() {
        palabraSecreta = obtenerPalabraAleatoria(usuario.getNivel());
        palabraOculta = new StringBuilder("_");
        for (int i = 1; i < palabraSecreta.length(); i++) {
            palabraOculta.append(" _");
        }
        palabraLabel.setText("Palabra: " + palabraOculta);
        errores = 0;
        letrasUsadas.clear();
    }

    private String obtenerPalabraAleatoria(String nivel) {
        String palabra = "error";
        String sql = "SELECT palabra FROM palabras INNER JOIN niveles ON palabras.id_nivel = niveles.id WHERE niveles.nivel = ? ORDER BY RANDOM() LIMIT 1";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nivel.toLowerCase());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                palabra = rs.getString("palabra");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return palabra.toLowerCase();
    }

    private void probarLetra(ActionEvent event) {
        String letra = inputLetra.getText().trim().toLowerCase();
        if (letra.length() != 1 || !letra.matches("[a-zA-ZñÑ]")) {
            mostrarAlerta("Letra no válida", "Introduce solo una letra.");
            return;
        }
        char letraChar = letra.charAt(0);
        if (letrasUsadas.contains(letraChar)) {
            mostrarAlerta("Letra repetida", "Ya has usado esa letra.");
            return;
        }

        letrasUsadas.add(letraChar);

        boolean acierto = false;
        for (int i = 0; i < palabraSecreta.length(); i++) {
            if (palabraSecreta.charAt(i) == letraChar) {
                palabraOculta.setCharAt(i * 2, letraChar);
                acierto = true;
            }
        }

        palabraLabel.setText("Palabra: " + palabraOculta);

        if (!acierto) {
            errores++;
            if (errores >= MAX_ERRORES) {
                mostrarAlerta("Has perdido", "La palabra era: " + palabraSecreta);
                cambiarNivel(false);
                inicializarJuego();
            }
        } else if (!palabraOculta.toString().contains("_")) {
            mostrarAlerta("Felicidades!", "Has adivinado la palabra: " + palabraSecreta);
            cambiarNivel(true);
            inicializarJuego();
        }

        inputLetra.clear();
    }

    private void cambiarNivel(boolean subir) {
        int nivelActual;
        String nivelUsuario = usuario.getNivel().toLowerCase();

        switch (nivelUsuario) {
            case "facil":
                nivelActual = 1;
                break;
            case "medio":
                nivelActual = 2;
                break;
            case "dificil":
                nivelActual = 3;
                break;
            default:
                nivelActual = 1;
                break;
        }

        int nuevoNivel;
        if (subir) {
            if (nivelActual < 3) {
                nuevoNivel = nivelActual + 1;
            } else {
                nuevoNivel = 3;
            }
        } else {
            if (nivelActual > 1) {
                nuevoNivel = nivelActual - 1;
            } else {
                nuevoNivel = 1;
            }
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE usuarios SET id_nivel = ? WHERE email = ?")) {
            stmt.setInt(1, nuevoNivel);
            stmt.setString(2, usuario.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String nuevoNivelTexto;
        switch (nuevoNivel) {
            case 1:
                nuevoNivelTexto = "facil";
                break;
            case 2:
                nuevoNivelTexto = "medio";
                break;
            case 3:
                nuevoNivelTexto = "dificil";
                break;
            default:
                nuevoNivelTexto = "facil";
                break;
        }

        usuario.setNivel(nuevoNivelTexto);
        nivelLabel.setText(usuario.getNivel());
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }


    @FXML
    private void reiniciarJuego(ActionEvent event) {
        inicializarJuego();
    }

    @FXML
    private void volverAPerfil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/es/diegofeblesseoane/ahorcado/perfil.fxml"));
            Parent root = loader.load();

            perfilController controller = loader.getController();
            controller.setUsuario(usuario);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
