package br.com.fatec.catalogo.controllers;

import br.com.fatec.catalogo.models.UsuarioModel;
import br.com.fatec.catalogo.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService; // Use o service que criptografa a senha

    @GetMapping("/novo")
    public String exibirFormulario(Model model) {
        // É este atributo "usuario" que o th:object lá no HTML vai procurar
        model.addAttribute("usuario", new UsuarioModel());
        return "cadastro-usuario";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid UsuarioModel usuario, BindingResult result) {
        if (result.hasErrors()) {
            return "cadastro-usuario";
        }

        // Antes era: userManager.createUser(...)
        // Agora usamos o nosso service que já faz o BCrypt:
        usuarioService.salvar(usuario);

        return "redirect:/produtos";
    }
}