package _doparcial.previo.services;

import _doparcial.previo.dtos.SolicitudListDTO;
import _doparcial.previo.models.Estado;
import _doparcial.previo.models.Persona;
import _doparcial.previo.models.Solicitud;
import _doparcial.previo.models.Validacion;
import _doparcial.previo.repositories.EstadoRepository;
import _doparcial.previo.repositories.PersonaRepository;
import _doparcial.previo.repositories.SolicitudRepository;
import _doparcial.previo.repositories.ValidacionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final PersonaRepository personaRepository;
    private final EstadoRepository estadoRepository;
    private final ValidacionRepository validacionRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public SolicitudService(SolicitudRepository solicitudRepository,
                            PersonaRepository personaRepository,
                            EstadoRepository estadoRepository,
                            ValidacionRepository validacionRepository) {
        this.solicitudRepository = solicitudRepository;
        this.personaRepository = personaRepository;
        this.estadoRepository = estadoRepository;
        this.validacionRepository = validacionRepository;
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
        )).collect(Collectors.toList());
    }

    
    @SuppressWarnings("unchecked")
    public Map<String, Object> registrarSolicitud(Map<String, Object> body) {
        Map<String, Object> solicitanteMap = (Map<String, Object>) body.get("solicitante");
        Map<String, Object> codeudorMap   = (Map<String, Object>) body.get("codeudor");
        String observacion = (String) body.get("observacion");

        if (solicitanteMap == null || codeudorMap == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "solicitante y codeudor son obligatorios");
        }

        // Datos básicos
        String docSol   = (String) solicitanteMap.get("documento");
        String docCod   = (String) codeudorMap.get("documento");
        String emailSol = (String) solicitanteMap.get("email");
        String emailCod = (String) codeudorMap.get("email");
        String telSol   = (String) solicitanteMap.get("telefono");
        String telCod   = (String) codeudorMap.get("telefono");

        
        if (docSol != null && docSol.equals(docCod)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "solicitante y codeudor deben ser distintos");
        }
        
        if ((emailSol != null && emailSol.equalsIgnoreCase(emailCod)) ||
            (telSol != null && telSol.equals(telCod))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "solicitante y codeudor no pueden tener el mismo correo ni el mismo teléfono");
        }

       
        boolean correoValidado = validacionRepository.findAll().stream().anyMatch(v ->
                equalsIgnoreCase(v.getEmail(), emailSol) &&
                Objects.equals(v.getDocumento(), docSol) &&
                equalsIgnoreCase(v.getEstado(), "validada")
        );
        if (!correoValidado) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "correo del solicitante no está validado");
        }

       
        Persona solicitante = upsertPersonaDesdeMapa(solicitanteMap);
        Persona codeudor    = upsertPersonaDesdeMapa(codeudorMap);

        
        List<Estado> estados = estadoRepository.findAll();
        Estado eSolicitud = buscarEstadoPorDescripcion(estados, "solicitud");
        Estado eAprobada  = buscarEstadoPorDescripcion(estados, "aprobada");
        Estado eRechazada = buscarEstadoPorDescripcion(estados, "rechazada");

       
        boolean tieneBloqueante = solicitudRepository.findAll().stream().anyMatch(s ->
                mismoDocumento(s.getSolicitante(), docSol) &&
                esUnoDe(s.getEstado(), e -> equalsIgnoreCase(e.getDescripcion(), "aprobada")
                                         || equalsIgnoreCase(e.getDescripcion(), "solicitud"))
        );
        if (tieneBloqueante) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "el solicitante ya tiene una solicitud en estado 'aprobada' o 'solicitud'");
        }

       
        boolean tieneRechazada = solicitudRepository.findAll().stream().anyMatch(s ->
                mismoDocumento(s.getSolicitante(), docSol) &&
                esUnoDe(s.getEstado(), e -> equalsIgnoreCase(e.getDescripcion(), "rechazada"))
        );
        Estado estadoNueva = tieneRechazada ? eRechazada : eSolicitud;

       
        int nextIdSolicitud = solicitudRepository.findAll().stream()
                .mapToInt(Solicitud::getId)
                .max()
                .orElse(0) + 1;

        
        String codigoRadicado = generarCodigo(8);

        Solicitud s = new Solicitud();
        s.setId(nextIdSolicitud);
        s.setFecha(LocalDate.now());
        s.setSolicitante(solicitante);
        s.setCodeudor(codeudor);
        s.setValor(null); 
        s.setEstado(estadoNueva);
        s.setObservacion(observacion);
        s.setCodigoRadicado(codigoRadicado);

        solicitudRepository.save(s);

       
        return Map.of(
                "id", s.getId(),
                "fecha", s.getFecha() != null ? s.getFecha().format(FMT) : null,
                "codigo_radicado", s.getCodigoRadicado()
        );
    }

 
    private Persona upsertPersonaDesdeMapa(Map<String, Object> data) {
        if (data == null) return null;

        String documento = (String) data.get("documento");
        String nombre    = (String) data.get("nombre");
        String email     = (String) data.get("email");
        String telefono  = (String) data.get("telefono");
        String fnacStr   = (String) data.get("fecha_nacimiento");

       
        Persona existente = personaRepository.findAll().stream()
                .filter(p -> Objects.equals(p.getDocumento(), documento))
                .findFirst()
                .orElse(null);

        if (existente != null) {
            if (nombre != null)   existente.setNombre(nombre);
            if (email != null)    existente.setEmail(email);
            if (telefono != null) existente.setTelefono(telefono);
            if (fnacStr != null && !fnacStr.isBlank()) existente.setFechaNacimiento(LocalDate.parse(fnacStr));
            return personaRepository.save(existente);
        }

        
        int nextIdPersona = personaRepository.findAll().stream()
                .mapToInt(Persona::getId)
                .max()
                .orElse(0) + 1;

        Persona p = new Persona();
        p.setId(nextIdPersona);
        p.setDocumento(documento);
        p.setNombre(nombre);
        p.setEmail(email);
        p.setTelefono(telefono);
        if (fnacStr != null && !fnacStr.isBlank()) {
            p.setFechaNacimiento(LocalDate.parse(fnacStr));
        }
        return personaRepository.save(p);
    }

    private static boolean equalsIgnoreCase(String a, String b) {
        return a != null && b != null && a.equalsIgnoreCase(b);
    }

    private static boolean mismoDocumento(Persona p, String doc) {
        return p != null && p.getDocumento() != null && p.getDocumento().equals(doc);
    }

    private static boolean esUnoDe(Estado e, java.util.function.Predicate<Estado> predicate) {
        return e != null && predicate.test(e);
    }

    private static Estado buscarEstadoPorDescripcion(List<Estado> estados, String descripcion) {
        if (estados == null) return null;
        return estados.stream()
                .filter(e -> e.getDescripcion() != null && e.getDescripcion().equalsIgnoreCase(descripcion))
                .findFirst()
                .orElse(null);
    }

    private static String generarCodigo(int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
}
