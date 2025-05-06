package med.voll.web_application.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


@Configuration
public class ConfiguracoesSeguranca {
    
    @Bean
    public UserDetailsService dadosUsuariosCadastrados() {
        UserDetails usuario = User.builder()
        .username("Marcelo")
        .password("{noop}123")
        .build();

        return new InMemoryUserDetailsManager(usuario);
    }
}
