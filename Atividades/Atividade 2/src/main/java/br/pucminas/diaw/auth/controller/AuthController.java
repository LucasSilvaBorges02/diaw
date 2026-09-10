package br.pucminas.diaw.auth.controller;

import br.pucminas.diaw.auth.dto.CadastroForm;
import br.pucminas.diaw.auth.dto.NovaSenhaForm;
import br.pucminas.diaw.auth.service.RecuperacaoSenhaService;
import br.pucminas.diaw.auth.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;
    private final RecuperacaoSenhaService recuperacaoService;

    public AuthController(UsuarioService usuarioService, RecuperacaoSenhaService recuperacaoService) {
        this.usuarioService = usuarioService;
        this.recuperacaoService = recuperacaoService;
    }

    // ------------------------------------------------------------------ login

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // --------------------------------------------------------------- cadastro

    @GetMapping("/register")
    public String formularioCadastro(Model model) {
        if (!model.containsAttribute("cadastroForm")) {
            model.addAttribute("cadastroForm", new CadastroForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String cadastrar(@Valid @ModelAttribute("cadastroForm") CadastroForm form,
                            BindingResult resultado,
                            RedirectAttributes redirect) {

        if (!form.senhasConferem()) {
            resultado.rejectValue("confirmacaoSenha", "senhas.diferentes",
                    "As senhas nao sao iguais.");
        }
        if (usuarioService.usernameEmUso(form.getUsername())) {
            resultado.rejectValue("username", "username.duplicado",
                    "Esse nome de usuario ja esta em uso.");
        }
        if (usuarioService.emailEmUso(form.getEmail())) {
            resultado.rejectValue("email", "email.duplicado",
                    "Ja existe uma conta com esse email.");
        }

        if (resultado.hasErrors()) {
            limparSenhas(form);
            return "register";
        }

        usuarioService.cadastrar(form);
        redirect.addFlashAttribute("sucesso", "Conta criada. Agora e so entrar.");
        return "redirect:/login";
    }

    // ----------------------------------------------- recuperacao (pedir link)

    @GetMapping("/recoverpassword")
    public String formularioRecuperacao() {
        return "recoverpassword";
    }

    @PostMapping("/recoverpassword")
    public String solicitarRecuperacao(@RequestParam("email") String email,
                                       RedirectAttributes redirect) {

        if (email == null || email.isBlank()) {
            redirect.addFlashAttribute("erro", "Informe o email da sua conta.");
            return "redirect:/recoverpassword";
        }

        recuperacaoService.solicitarRecuperacao(email);

        // Mensagem neutra: nao revela se o email existe na base.
        redirect.addFlashAttribute("sucesso",
                "Se existir uma conta com esse email, o link de redefinicao ja esta a caminho.");
        return "redirect:/recoverpassword";
    }

    // ------------------------------------------- recuperacao (definir senha)

    @GetMapping("/resetpassword")
    public String formularioNovaSenha(@RequestParam(value = "token", required = false) String token,
                                      Model model) {

        if (recuperacaoService.buscarTokenValido(token).isEmpty()) {
            model.addAttribute("tokenInvalido", true);
            return "resetpassword";
        }

        NovaSenhaForm form = new NovaSenhaForm();
        form.setToken(token);
        model.addAttribute("novaSenhaForm", form);
        return "resetpassword";
    }

    @PostMapping("/resetpassword")
    public String definirNovaSenha(@Valid @ModelAttribute("novaSenhaForm") NovaSenhaForm form,
                                   BindingResult resultado,
                                   Model model,
                                   RedirectAttributes redirect) {

        if (!form.senhasConferem()) {
            resultado.rejectValue("confirmacaoSenha", "senhas.diferentes",
                    "As senhas nao sao iguais.");
        }

        if (resultado.hasErrors()) {
            form.setSenha(null);
            form.setConfirmacaoSenha(null);
            return "resetpassword";
        }

        boolean trocou = recuperacaoService.redefinirSenha(form.getToken(), form.getSenha());
        if (!trocou) {
            model.addAttribute("tokenInvalido", true);
            return "resetpassword";
        }

        redirect.addFlashAttribute("sucesso", "Senha atualizada. Entre com a nova senha.");
        return "redirect:/login";
    }

    // ---------------------------------------------------------------- apoio

    private void limparSenhas(CadastroForm form) {
        form.setSenha(null);
        form.setConfirmacaoSenha(null);
    }

    /** Deixa a mensagem de erro de campo acessivel de forma simples no Thymeleaf. */
    @SuppressWarnings("unused")
    private String primeiroErro(BindingResult resultado, String campo) {
        FieldError erro = resultado.getFieldError(campo);
        return erro == null ? null : erro.getDefaultMessage();
    }
}
