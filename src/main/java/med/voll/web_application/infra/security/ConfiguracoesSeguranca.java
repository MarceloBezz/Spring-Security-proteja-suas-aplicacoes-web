package med.voll.web_application.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ConfiguracoesSeguranca {

    @Bean
    public UserDetailsService dadosUsuariosCadastrados() {
        UserDetails usuario = User.builder()
                .username("marcelo@email.com")
                .password("{noop}123")
                .build();

        return new InMemoryUserDetailsManager(usuario);
    }

    @Bean
    public SecurityFilterChain filtrosDeSeguranca(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(req -> {
                    req.requestMatchers("/css/**", "/js/**", "/assets/**").permitAll();
                    req.anyRequest().authenticated();
                })
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .build();
    }
}
