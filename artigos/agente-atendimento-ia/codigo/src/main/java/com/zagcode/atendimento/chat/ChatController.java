package com.zagcode.atendimento.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    public record Mensagem(@NotBlank String conversaId, @NotBlank String texto) {
    }

    public record Resposta(String conversaId, String texto) {
    }

    private final ChatClient atendente;

    public ChatController(ChatClient atendente) {
        this.atendente = atendente;
    }

    @PostMapping("/chat")
    public Resposta conversar(@Valid @RequestBody Mensagem mensagem) {
        String resposta = atendente.prompt()
                .user(mensagem.texto())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, mensagem.conversaId()))
                .call()
                .content();
        return new Resposta(mensagem.conversaId(), resposta);
    }

    // Se o modelo cair (chave inválida, limite, timeout), o cliente recebe uma saída em vez de um erro 500
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Resposta falhaNoAgente(RuntimeException e) {
        log.error("Falha ao consultar o modelo", e);
        return new Resposta(null, "Estou com instabilidade agora. Tente de novo em instantes "
                + "ou fale com a equipe de segunda a sexta, das 9h às 18h.");
    }
}
