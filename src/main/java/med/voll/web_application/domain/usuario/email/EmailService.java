package med.voll.web_application.domain.usuario.email;

import java.io.UnsupportedEncodingException;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import med.voll.web_application.domain.RegraDeNegocioException;
import med.voll.web_application.domain.usuario.Usuario;

@Service
public class EmailService {

    private final JavaMailSender enviadorEmail;
    private static final String EMAIL_ORIGEM = "vollmed@email.com";
    private static final String NOME_ENVIADOR = "Clínica Voll Med";

    public static final String URL_SITE = "http://localhost:8080"; // "voll.med.com.br"

    public EmailService(JavaMailSender enviadorEmail) {
        this.enviadorEmail = enviadorEmail;
    }

    @Async
    private void enviarEmail(String emailUsuario, String assunto, String conteudo) {
        MimeMessage message = enviadorEmail.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(EMAIL_ORIGEM, NOME_ENVIADOR);
            helper.setTo(emailUsuario);
            helper.setSubject(assunto);
            helper.setText(conteudo, true);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RegraDeNegocioException("Erro ao enviar email");
        }

        enviadorEmail.send(message);
    }

    public void enviarEmailSenha(Usuario usuario) {
        String assunto = "Aqui está seu link para alterar a senha";
        String conteudo = gerarConteudoEmail("Olá [[name]],<br>"
                + "Por favor clique no link abaixo para alterar a senha:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">ALTERAR</a></h3>"
                + "Obrigado,<br>"
                + "Clínica Voll Med.", usuario.getNome(), URL_SITE + "/recuperar-conta?codigo=" + usuario.getToken());

        enviarEmail(usuario.getUsername(), assunto, conteudo);
    }

    private String gerarConteudoEmail(String template, String nome, String url) {
        return template.replace("[[name]]", nome).replace("[[URL]]", url);
    }

    public void enviarPrimeiraSenha(String primeiraSenha, Usuario usuario) {
        String assunto = "Seja bem vindo ao nosso sistema!";
        String conteudo = gerarConteudoEmail("Olá [[name]],<br>"
                + "Aqui estão suas informações de login:<br>"
                + "<strong>Email: " + usuario.getUsername() +"</strong><br>"
                + "<strong>Senha: " + primeiraSenha +"</strong><br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">ACESSAR SUA CONTA</a></h3>"
                + "Conte com nossa equipe para o que precisar!<br>"
                + "Obrigado,<br>"
                + "Clínica Voll Med.", usuario.getNome(), URL_SITE + "/login");

        enviarEmail(usuario.getUsername(), assunto, conteudo);
    }
}