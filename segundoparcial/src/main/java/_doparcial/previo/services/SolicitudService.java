package _doparcial.previo.services;

import _doparcial.previo.dtos.SolicitudListDTO;
import _doparcial.previo.models.Solicitud;
import _doparcial.previo.repositories.SolicitudRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SolicitudService {

    private SolicitudRepository solicitudRepository;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public SolicitudService(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    public List<SolicitudListDTO> listarSolicitudes() {
        List<Solicitud> entidades = solicitudRepository.findAll();

        return entidades.stream().map(s -> new SolicitudListDTO(
                s.getId(),
                s.getFecha() != null ? s.getFecha().format(FMT) : null,
                s.getSolicitante() != null ? s.getSolicitante().getNombre() : null,
                s.getCodeudor() != null ? s.getCodeudor().getNombre() : null,
                s.getEstado() != null ? s.getEstado().getDescripcion() : null,
                s.getCodigoRadicado()
        )).toList();
    }
}
