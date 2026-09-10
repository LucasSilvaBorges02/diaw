package br.pucminas.diaw.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Dados do formulario de redefinicao de senha (GET/POST /resetpassword). */
public class NovaSenhaForm {

    @NotBlank
    private String token;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
