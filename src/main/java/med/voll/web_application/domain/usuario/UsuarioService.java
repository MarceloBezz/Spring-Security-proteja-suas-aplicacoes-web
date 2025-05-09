package med.voll.web_application.domain.usuario;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import med.voll.web_application.domain.RegraDeNegocioException;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("O usuário não foi encontrado!"));
    }

    public Long salvarUsuario(String nome, String email, Perfil perfil) {
        String primeiraSenha = UUID.randomUUID().toString().substring(0, 8);
        System.out.println("Senha gerada: " + primeiraSenha);
        String senhaCriptografada = encoder.encode(primeiraSenha);
        Usuario usuarioSalvo = usuarioRepository.save(new Usuario(nome, email, senhaCriptografada, perfil));
        return usuarioSalvo.getId();
    }

    public void excluir(Long id) {
        usuarioRepository.deleteById(id);
    }

    public void alterarSenha(DadosAlteracaoSenha dados, Usuario logado) {
        if (!encoder.matches(dados.senhaAtual(), logado.getPassword())) {
            throw new RegraDeNegocioException("Senha digitada não confere com a senha atual!");
        }

        if (!dados.novaSenha().equals(dados.novaSenhaConfirmacao())) {
            throw new RegraDeNegocioException("Senhas não conferem!");
        }

        String senhaCriptografada = encoder.encode(dados.novaSenha());
        logado.alterarSenha(senhaCriptografada);
        usuarioRepository.save(logado);
    }
}
