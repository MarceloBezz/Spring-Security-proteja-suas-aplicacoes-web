package med.voll.web_application.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.validation.Valid;
import med.voll.web_application.domain.RegraDeNegocioException;
import med.voll.web_application.domain.usuario.DadosAlteracaoSenha;
import med.voll.web_application.domain.usuario.Usuario;
import med.voll.web_application.domain.usuario.UsuarioService;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    private final String FORMULARIO_ALTERACAO_SENHA = "autenticacao/formulario-alteracao-senha";
    private final UsuarioService service;

    public LoginController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping("/login")
    public String carregaPaginaLogin() {
        return "autenticacao/login";
    }

    @GetMapping("/alterar-senha")
    public String carregaPaginaAlteracao() {
        return FORMULARIO_ALTERACAO_SENHA;
    }

    @PostMapping("/alterar-senha")
    public String alterarSenha(@Valid @ModelAttribute("dados") DadosAlteracaoSenha dados, BindingResult result,
            Model model, @AuthenticationPrincipal Usuario usuariologado) {
        if (result.hasErrors()) {
            model.addAttribute("dados", dados);
            return FORMULARIO_ALTERACAO_SENHA;
        }

        try {
            service.alterarSenha(dados, usuariologado);
            return "redirect:home";
        } catch (RegraDeNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("dados", dados);
            return FORMULARIO_ALTERACAO_SENHA;
        }
    }

}
