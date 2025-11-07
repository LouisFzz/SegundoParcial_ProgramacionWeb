package _doparcial.previo.dtos;

public class SolicitudListDTO {
    private Integer id;
    private String fecha;
    private String solicitante;
    private String codeudor;
    private String estado;
    private String codigo_radicado; // nota: el nombre ya sale así en JSON

    public SolicitudListDTO(Integer id, String fecha, String solicitante, String codeudor,
                            String estado, String codigo_radicado) {
        this.id = id;
        this.fecha = fecha;
        this.solicitante = solicitante;
        this.codeudor = codeudor;
        this.estado = estado;
        this.codigo_radicado = codigo_radicado;
    }

    public Integer getId() { return id; }
    public String getFecha() { return fecha; }
    public String getSolicitante() { return solicitante; }
    public String getCodeudor() { return codeudor; }
    public String getEstado() { return estado; }
    public String getCodigo_radicado() { return codigo_radicado; }

    public void setId(Integer id) { this.id = id; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setSolicitante(String solicitante) { this.solicitante = solicitante; }
    public void setCodeudor(String codeudor) { this.codeudor = codeudor; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setCodigo_radicado(String codigo_radicado) { this.codigo_radicado = codigo_radicado; }
}
