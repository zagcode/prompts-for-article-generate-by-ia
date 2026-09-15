package com.zagcode.atendimento;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Chave fictícia: o contexto sobe sem chamar a OpenRouter
@SpringBootTest(properties = "spring.ai.openai.api-key=chave-de-teste")
class AgenteAtendimentoApplicationTests {

	@Test
	void contextLoads() {
	}

}
