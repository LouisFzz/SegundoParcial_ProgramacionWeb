package _doparcial.previo.dtos;

public class ValidacionCrearResponse {
    private String token;
    private String codigo;

    public ValidacionCrearResponse(String token, String codigo) {
        this.token = token;
        this.codigo = codigo;
    }

    public String getToken() { return token; }
    public String getCodigo() { return codigo; }
}
