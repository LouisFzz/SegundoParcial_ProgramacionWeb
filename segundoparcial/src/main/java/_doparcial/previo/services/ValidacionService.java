package _doparcial.previo.services;

import _doparcial.previo.models.Validacion;
import _doparcial.previo.repositories.ValidacionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

@Service
public class ValidacionService {

    private final ValidacionRepository validacionRepository;

    public ValidacionService(ValidacionRepository validacionRepository) {
        this.validacionRepository = validacionRepository;
    }

    public Validacion crearSolicitudValidacion(String email, String documento) {

        int nextId = validacionRepository.findAll().stream()
                .mapToInt(Validacion::getId)
                .max()
                .orElse(0) + 1;

        String token = UUID.randomUUID().toString();
        String codigo = generarCodigo();

        Validacion v = new Validacion();
        v.setId(nextId);
        v.setEmail(email);
        v.setDocumento(documento);
        v.setFecha(LocalDate.now());
        v.setEstado("pendiente");
        v.setToken(token);
        v.setCodigo(codigo);

        return validacionRepository.save(v);
    }

    private String generarCodigo() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
}
