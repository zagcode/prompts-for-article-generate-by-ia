package com.zagcode.atendimento.agente;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class AgenteConfig {

    @Bean
    ChatClient atendente(ChatClient.Builder builder,
                         ChatMemory chatMemory,
                         AtendimentoTools tools,
                         @Value("classpath:prompts/atendente.md") Resource instrucoes) {
        return builder
                .defaultSystem(instrucoes)                                        // quem o agente é e as regras
                .defaultTools(tools)                                              // o que ele pode fazer
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()) // do que ele lembra
                .build();
    }
}
