package br.pucminas.diaw.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Dados do formulario de cadastro (GET/POST /register).
 * As validacoes sao aplicadas pelo Bean Validation antes de chegar no service.
 */
public class CadastroForm {

    @NotBlank(message = "Informe seu nome.")
    @Size(min = 3, max = 120, message = "O nome deve ter entre 3 e 120 caracteres.")
    private String nome;

    @NotBlank(message = "Escolha um nome de usuario.")
    @Size(min = 3, max = 40, message = "O usuario deve ter entre 3 e 40 caracteres.")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
            message = "Use apenas letras, numeros, ponto, hifen ou underline.")
    private String username;

    @NotBlank(message = "Informe seu email.")
    @Email(message = "Esse email nao parece valido.")
    @Size(max = 160, message = "Email muito longo.")
    private String email;

    @NotBlank(message = "Crie uma senha.")
    @Size(min = 8, max = 64, message = "A senha precisa de pelo menos 8 caracteres.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "A senha precisa misturar letras e numeros.")
    private String senha;

    @NotBlank(message = "Repita a senha.")
    private String confirmacaoSenha;

    public boolean senhasConferem() {
        return senha != null && senha.equals(confirmacaoSenha);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getConfirmacaoSenha() {
        return confirmacaoSenha;
    }

    public void setConfirmacaoSenha(String confirmacaoSenha) {
        this.confirmacaoSenha = confirmacaoSenha;
    }
}
