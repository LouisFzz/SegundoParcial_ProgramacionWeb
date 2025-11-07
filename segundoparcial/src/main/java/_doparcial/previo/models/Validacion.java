package _doparcial.previo.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(
    name = "validacion",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_validacion_token", columnNames = "token"),
        @UniqueConstraint(name = "uk_validacion_codigo", columnNames = "codigo")
    }
)
@Getter @Setter @NoArgsConstructor
public class Validacion {

    @Id
    private Integer id;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "documento", length = 10)
    private String documento;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "estado", length = 20)
    private String estado; 

    @Column(name = "token", length = 100)
    private String token;

    @Column(name = "codigo", length = 10)
    private String codigo;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
}
