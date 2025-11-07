package _doparcial.previo.controllers;

import _doparcial.previo.models.Validacion;
import _doparcial.previo.services.ValidacionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/validaciones")
public class ValidacionController {

    private final ValidacionService validacionService;

    public ValidacionController(ValidacionService validacionService) {
        this.validacionService = validacionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> crear(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String documento = body.get("documento");

        Validacion nueva = validacionService.crearSolicitudValidacion(email, documento);

        return Map.of(
                "token", nueva.getToken(),
                "codigo", nueva.getCodigo()
        );
    }
}
