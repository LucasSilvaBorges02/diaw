package br.pucminas.diaw.auth.controller;

import br.pucminas.diaw.auth.model.Usuario;
import br.pucminas.diaw.auth.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UsuarioRepository usuarios;

    public HomeController(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/home";
    }

    /** Area protegida: so chega aqui quem esta autenticado. */
    @GetMapping("/home")
    public String home(Authentication authentication, Model model) {
        Usuario usuario = usuarios.findByUsernameIgnoreCase(authentication.getName()).orElse(null);

        model.addAttribute("usuario", usuario);
        model.addAttribute("primeiroNome",
                usuario == null ? authentication.getName() : usuario.getNome().split(" ")[0]);
        return "home";
    }
}
