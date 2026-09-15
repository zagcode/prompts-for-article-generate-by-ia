<p align="center">
  <img width="100%" src=".github/assets/banner.png" alt="Do zero ao agente: atendimento com IA em Spring Boot + OpenRouter">
</p>

<p align="center">
  <a href="https://dio.me/"><img src="https://img.shields.io/badge/DIO-Projeto%20de%20aula-28DA77" alt="DIO - Projeto de aula"></a>
  <img src="https://img.shields.io/badge/Artigo-gerado%20com%20IA-8C7BFF" alt="Artigo gerado com IA">
  <img src="https://img.shields.io/badge/Java-21-E76F00?logo=openjdk&logoColor=white" alt="Java 21">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot 4.1">
  <img src="https://img.shields.io/badge/Spring%20AI-2.0-6DB33F?logo=spring&logoColor=white" alt="Spring AI 2.0">
  <img src="https://img.shields.io/badge/OpenRouter-API-111111" alt="OpenRouter">
</p>

# Artigo técnico gerado com IA: agente de atendimento em Java

> ℹ️ **Projeto desenvolvido para a aula da [DIO](https://dio.me)** sobre criação de artigos técnicos com Inteligência Artificial.
> Este repositório é um fork do [projeto original da aula](https://github.com/felipeAguiarCode/prompts-for-article-generate-by-ia), criado pelo expert [Felipe Aguiar](https://github.com/felipeAguiarCode), com o conteúdo substituído por um novo artigo.

O objetivo é produzir um artigo técnico de qualidade seguindo o checklist da aula e registrar **os prompts, as IAs e as fontes** usados em cada etapa.

📕 **Artigo:** [Do zero ao agente: atendimento com IA em Spring Boot + OpenRouter](artigos/agente-atendimento-ia/04-artigo.md)
🌐 **Publicado na DIO:** _link em breve_

## ✅ Checklist aplicado

| # | Etapa | Prompt / como foi feito | IA / ferramenta | Resultado |
| - | ----- | ----------------------- | --------------- | --------- |
| 1 | **Definir o assunto** | "Quero escrever um artigo técnico para a DIO sobre criação de um agente de atendimento usando IA. Tutorial com código em Java (Spring Boot) + OpenRouter. Defina público-alvo, objetivo, promessa, fora do escopo e palavras-chave." | Claude | [01-assunto.md](artigos/agente-atendimento-ia/01-assunto.md) |
| 2 | **Título chamativo (headline)** | "Crie 10 headlines para um artigo da DIO sobre criação de um agente de atendimento com IA usando Java, Spring Boot e OpenRouter. Varie os estilos: curiosidade, número, 'como fazer', benefício e contraste. Máximo de ~70 caracteres." | Claude (a escolha final foi humana) | [02-headlines.md](artigos/agente-atendimento-ia/02-headlines.md) |
| 3 | **Imagem de capa** | "Crie uma capa 1200×630 em HTML/CSS com SVG inline, estilo tech, fundo escuro com gradiente, headline à esquerda e uma conversa cliente × robô mostrando as chamadas de ferramenta." Exportada em PNG com o Edge headless. | Claude + Microsoft Edge | [03-capa/](artigos/agente-atendimento-ia/03-capa/) |
| 4 | **Blocos do artigo** | "Escreva um tutorial para devs Java júnior/pleno com os blocos: gancho, chatbot × agente, por que OpenRouter, mão na massa (projeto, configuração, system prompt, tools, memória, endpoint), testes, boas práticas, alternativa em Quarkus e conclusão. Trechos de código idênticos ao projeto em `codigo/`, validados contra a documentação oficial atual." | Claude | [04-artigo.md](artigos/agente-atendimento-ia/04-artigo.md) · [código](artigos/agente-atendimento-ia/codigo/) |
| 5 | **Postar com call to action** | "Escreva o texto de publicação para a DIO: resumo para o card, tags e um CTA final pedindo comentário, curtida e star no repositório, sem soar forçado." | Claude | [05-post-cta.md](artigos/agente-atendimento-ia/05-post-cta.md) |

## 🤖 Créditos das IAs utilizadas

| IA | Fornecedor | Uso neste projeto |
| -- | ---------- | ----------------- |
| [Claude](https://claude.ai) (modelo **Claude Opus 5**, via [Claude Code](https://claude.com/claude-code)) | Anthropic | Definição do assunto, headlines, capa em HTML/SVG, texto do artigo, código de exemplo e testes, texto de publicação e este README |
| [OpenRouter](https://openrouter.ai) | OpenRouter | Gateway de LLMs usado **pelo agente do tutorial** em tempo de execução |
| [GPT-4o mini](https://openrouter.ai/openai/gpt-4o-mini) (modelo padrão do exemplo, trocável via `OPENROUTER_MODEL`) | OpenAI, via OpenRouter | Cérebro da atendente virtual "Nina" no código de exemplo |

> Todo o conteúdo gerado por IA foi revisado por uma pessoa: a headline foi escolhida entre as opções, o código foi compilado e os testes passaram.

## 📚 Fontes dos dados

**Documentação técnica consultada** (versões vigentes em setembro de 2026):

- [Spring AI: Getting Started / Dependency Management](https://docs.spring.io/spring-ai/reference/getting-started.html): compatibilidade Spring AI 2.0 × Spring Boot 4
- [Spring AI: Tool Calling](https://docs.spring.io/spring-ai/reference/api/tools.html): `@Tool`, `@ToolParam`, `defaultTools`
- [Spring AI: Chat Memory](https://docs.spring.io/spring-ai/reference/api/chat-memory.html): `MessageWindowChatMemory`, `MessageChatMemoryAdvisor`, `CONVERSATION_ID`
- [Spring AI: OpenAI Chat](https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html): `base-url`, `api-key`, `custom-headers`
- [Spring Initializr](https://start.spring.io): geração do projeto base
- [OpenRouter: Quickstart](https://openrouter.ai/docs/quickstart): endpoint compatível com OpenAI e headers opcionais
- [OpenRouter: Models API](https://openrouter.ai/api/v1/models): conferência de IDs de modelo com suporte a ferramentas
- [Quarkus LangChain4j: AI Services](https://docs.quarkiverse.io/quarkus-langchain4j/dev/ai-services.html) e [OpenAI Chat Model](https://docs.quarkiverse.io/quarkus-langchain4j/dev/openai-chat-model.html): seção de alternativa em Quarkus

Detalhes não explícitos na documentação (pacote do `MessageChatMemoryAdvisor`, propriedades de configuração e janela padrão de 20 mensagens) foram conferidos direto nos jars publicados no Maven Central.

**Dados do exemplo:** a loja **DevStore**, a atendente **Nina**, os clientes, os pedidos (`1001`, `1002`, `1003`), as políticas comerciais e os protocolos de chamado são **100% fictícios**, criados apenas para fins didáticos, e ficam em memória no código.

## 🗂️ Estrutura

```
artigos/agente-atendimento-ia/
├── 01-assunto.md        # 1. assunto, público e objetivo
├── 02-headlines.md      # 2. 10 headlines + escolhida
├── 03-capa/             # 3. capa.html, capa.png, prompts de imagem
├── 04-artigo.md         # 4. artigo completo, pronto para a DIO
├── 05-post-cta.md       # 5. publicação + call to action
└── codigo/              # projeto Spring Boot usado no artigo
```

## 🛠️ Como rodar o agente

Requer Java 21 e uma chave da [OpenRouter](https://openrouter.ai/keys).

```bash
cd artigos/agente-atendimento-ia/codigo
export OPENROUTER_API_KEY=sk-or-...
./mvnw spring-boot:run

curl -X POST localhost:8080/chat -H "Content-Type: application/json" \
  -d '{"conversaId":"ana-1","texto":"Oi! Cadê meu pedido 1001?"}'
```

Mais detalhes em [codigo/README.md](artigos/agente-atendimento-ia/codigo/README.md).

## 👨‍💻 Autor

**zagcode**: [GitHub](https://github.com/zagcode)

Projeto de aula da [DIO](https://dio.me), baseado no material do expert [Felipe Aguiar](https://github.com/felipeAguiarCode).
