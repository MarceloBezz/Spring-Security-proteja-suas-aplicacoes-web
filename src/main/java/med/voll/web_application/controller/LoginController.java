package med.voll.web_application.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.web_application.domain.RegraDeNegocioException;
import med.voll.web_application.domain.paciente.DadosCadastroPaciente;
import med.voll.web_application.domain.paciente.PacienteService;
import med.voll.web_application.domain.usuario.DadosAlteracaoSenha;
import med.voll.web_application.domain.usuario.Perfil;
import med.voll.web_application.domain.usuario.Usuario;
import med.voll.web_application.domain.usuario.UsuarioService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class LoginController {

    private final String FORMULARIO_ALTERACAO_SENHA = "autenticacao/formulario-alteracao-senha";
    private final String FORMULARIO_CADASTRO_PACIENTE = "paciente/formulario-paciente-cadastro";
    private final UsuarioService service;
    private final PacienteService pacienteService;

    public LoginController(UsuarioService service, PacienteService pacienteService) {
        this.service = service;
        this.pacienteService = pacienteService;
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

    @GetMapping("/cadastro-paciente")
    public String cadastroPaciente(Model model) {
        model.addAttribute("dados", new DadosCadastroPaciente(null, "", "", "", ""));
        return FORMULARIO_CADASTRO_PACIENTE;
    }

    @PostMapping("/cadastro-paciente")
    @Transactional
    public String postMethodName(@Valid @ModelAttribute("dados") DadosCadastroPaciente dados, BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("dados", dados);
            return FORMULARIO_CADASTRO_PACIENTE;
        }

        try {
            pacienteService.cadastroPeloUsuario(dados);
            return "redirect:cadastro-paciente?verificar";
        } catch (RegraDeNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("dados", dados);
            return FORMULARIO_CADASTRO_PACIENTE;
        }
    }

    @GetMapping("/validar-email")
    @Transactional
    public String validarEmail(@RequestParam String codigo) {
        if (codigo != null) {
            service.validarCadastro(codigo);
            return "redirect:home";
        } else {
            return "redirect:cadastro-paciente";
        }
    }
    

}
