package _doparcial.previo.controllers;

import _doparcial.previo.dtos.SolicitudListDTO;
import _doparcial.previo.services.SolicitudService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
