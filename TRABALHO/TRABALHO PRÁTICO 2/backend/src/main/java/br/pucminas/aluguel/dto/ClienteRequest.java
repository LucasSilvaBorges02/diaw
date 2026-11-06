package br.pucminas.aluguel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

/** A senha só é obrigatória no cadastro; na edição, em branco mantém a atual. */
public record ClienteRequest(
        @NotBlank(message = "informe o nome") String nome,
        @NotBlank(message = "informe o usuário") @Size(min = 3, max = 40, message = "o usuário precisa ter de 3 a 40 caracteres") String login,
        @Size(min = 6, max = 72, message = "a senha precisa ter pelo menos 6 caracteres") String senha,
        @NotBlank(message = "informe o RG") String rg,
        @NotBlank(message = "informe o CPF") @Pattern(regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}", message = "CPF inválido") String cpf,
        @NotBlank(message = "informe o endereço") String endereco,
        String profissao,
        @Size(max = 3, message = "no máximo 3 rendimentos") List<@Valid RendimentoDto> rendimentos) {
}
