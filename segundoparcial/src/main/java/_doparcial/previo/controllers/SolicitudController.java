package _doparcial.previo.controllers;

import _doparcial.previo.dtos.SolicitudListDTO;
import _doparcial.previo.services.SolicitudService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

   
    @GetMapping
    public List<SolicitudListDTO> listar() {
        return solicitudService.listarSolicitudes();
    }

 
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudListDTO registrar(@RequestBody Map<String, Object> body) {
        return solicitudService.registrarSolicitud(body);
    }
}
