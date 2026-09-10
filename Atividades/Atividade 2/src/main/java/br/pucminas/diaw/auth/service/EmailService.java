package br.pucminas.diaw.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Envio de email via SMTP (spring-boot-starter-mail).
 * Se app.mail.enabled=false, o conteudo do email e apenas impresso no console,
 * o que permite testar a recuperacao de senha sem configurar credenciais.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean envioHabilitado;

    @Value("${app.mail.from:nao-responda@horizonte.local}")
    private String remetente;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviar(String destinatario, String assunto, String corpo) {
        if (!envioHabilitado) {
            log.info("""

                    ================= EMAIL SIMULADO =================
                    Para:     {}
                    Assunto:  {}
                    ---------------------------------------------------
                    {}
                    ===================================================
                    """, destinatario, assunto, corpo);
            return;
        }

        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom(remetente);
            mensagem.setTo(destinatario);
            mensagem.setSubject(assunto);
            mensagem.setText(corpo);
            mailSender.send(mensagem);
            log.info("Email de recuperacao enviado para {}", destinatario);
        } catch (Exception e) {
            // Nao propaga: a tela nunca deve revelar se o email existe ou se o envio falhou.
            log.error("Falha ao enviar email para {}: {}", destinatario, e.getMessage());
        }
    }
}
