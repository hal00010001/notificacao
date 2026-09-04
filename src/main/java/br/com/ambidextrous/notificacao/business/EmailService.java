package br.com.ambidextrous.notificacao.business;

import br.com.ambidextrous.notificacao.business.dto.TarefaDTO;
import br.com.ambidextrous.notificacao.infrastructure.exceptions.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${envio.email.remetente}")
    public String remetente;

    @Value("${envio.email.nomeRemetente}")
    private String nomeRemetente;

    public void enviarEmail(TarefaDTO tarefaDTO) {
        try {

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            mimeMessageHelper.setFrom(new InternetAddress(remetente,  nomeRemetente));
            mimeMessageHelper.setTo(remetente);
            mimeMessageHelper.setSubject("Notificação de Tarefa");

            System.out.println("Remetente: " + remetente);

            Context context = new Context();
            context.setVariable("nomeTarefa", tarefaDTO.getNome());
            context.setVariable("email", tarefaDTO.getEmail());
            context.setVariable("descricao", tarefaDTO.getDescricao());
            context.setVariable("status", tarefaDTO.getStatus());

            String template = templateEngine.process("notificacao", context);
            mimeMessageHelper.setText(template, true);
            mailSender.send(mimeMessage);

        } catch(MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("Erro ao enviar o email " + e.getCause());
        }
    }

}
