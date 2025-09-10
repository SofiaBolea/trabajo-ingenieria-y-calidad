// MODIFICACIÓN HECHA EN BRANCH RELEASE-1.

import java.util.Map;
import java.util.HashMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import com.ing_y_calidad.TP_INTEGRADOR_DDS.models.Paciente;
import com.ing_y_calidad.TP_INTEGRADOR_DDS.models.Usuario;
import com.ing_y_calidad.TP_INTEGRADOR_DDS.DTOs.UsuarioDTO;
import com.ing_y_calidad.TP_INTEGRADOR_DDS.models.Secretaria;
import com.ing_y_calidad.TP_INTEGRADOR_DDS.services.GestorDeSesiones;

@RestController
@RequestMapping("/usuario") // Ruta base para todas las operaciones de Usuario.
public class UsuarioController {

    private final GestorDeSesiones gestorDeSesiones;

    // Inyección de dependencia:
    @Autowired
    public InicioSesion (GestorDeSesiones gestorDeSesiones) {
        this.gestorDeSesiones = gestorDeSesiones;
    }

    @PostMapping("/autenticar")
    public ResponseEntity<Map<String, String>> autenticarUsuario(@RequestBody UsuarioDTO usuarioDTO) {

        Map<String, String> response = new HashMap<>();

        try {

            Usuario usuario = gestorDeSesiones.autenticarUsuario(usuarioDTO);

            if (usuario != null) {

                response.put("message", "Usuario autenticado exitosamente.");
                response.put("nombreUsuario", usuario.getNombreUsuario());

                // Determinamos el tipo de usuario en base a la subclase:
                if (usuario instanceof Paciente) {
                    response.put("tipoUsuario", "Paciente");
                } else if (usuario instanceof Secretaria) {
                    response.put("tipoUsuario", "Secretaria");
                }

                return ResponseEntity.ok(response);

            } else {
                response.put("message", "Usuario o contraseña incorrecta.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (IllegalArgumentException ex) { // Capturamos errores de validación con el mensaje específico según tipo de error:
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception ex) { // Manejo de otros errores genéricos:
            ex.printStackTrace();
            response.put("message", "Error interno en el servidor.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}