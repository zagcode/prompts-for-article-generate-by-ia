# Do zero ao agente: atendimento com IA em Spring Boot + OpenRouter

![Capa do artigo](03-capa/capa.png)

"Qual o status do meu pedido?" Se você já trabalhou num e-commerce, sabe que essa pergunta chega centenas de vezes por dia, de madrugada, no fim de semana e no feriado. O chatbot tradicional, aquele de menu "digite 1, digite 2", não resolve: o cliente quer escrever do jeito dele e receber uma resposta de verdade.

Neste artigo vamos construir, **do zero**, um **agente de atendimento com IA** em Java que:

- entende perguntas em linguagem natural;
- **consulta pedidos de verdade** no seu sistema, sem inventar nada;
- **lembra da conversa**;
- sabe a hora de **passar o atendimento para um humano**.

Tudo com **Spring Boot 4**, **Spring AI 2** e a **OpenRouter**, em cerca de 170 linhas de Java. O código completo está no repositório indicado no final.

---

## 1. Chatbot × agente: qual a diferença?

Um chatbot comum segue um roteiro. Um **agente** usa um LLM (modelo de linguagem) para decidir **o que fazer** a cada mensagem. Um agente de atendimento tem quatro peças:

| Peça | O que faz | No nosso código |
| --- | --- | --- |
| **Cérebro** | O LLM que interpreta e responde | Modelo via OpenRouter |
| **Instruções** | Quem o agente é, o tom e as regras | `prompts/atendente.md` |
| **Ferramentas** | Ações no mundo real (consultar pedido, abrir chamado) | `@Tool` em `AtendimentoTools` |
| **Memória** | O que já foi dito na conversa | `MessageChatMemoryAdvisor` |

O segredo está nas **ferramentas** (*tool calling*). O modelo não "sabe" o status do pedido 1001. Ele percebe que precisa dessa informação, pede para a aplicação executar `consultarPedido("1001")`, recebe o resultado e só então responde. É isso que impede a IA de inventar dados.

```
Cliente ──► /chat ──► ChatClient ──► LLM (OpenRouter)
                          ▲              │ "preciso chamar consultarPedido(1001)"
                          │              ▼
                          └──── AtendimentoTools ──► PedidoRepository
```

## 2. Por que OpenRouter?

A [OpenRouter](https://openrouter.ai) é um gateway que dá acesso a centenas de modelos (OpenAI, Anthropic, Google, Meta e outros) com **uma única chave e uma única API**. E essa API é **compatível com a da OpenAI**.

Na prática:

- você usa o starter **OpenAI** do Spring AI, só trocando a URL;
- troca de modelo **mudando uma linha de configuração**, sem recompilar;
- compara custo e qualidade entre modelos sem reescrever nada.

## 3. Mão na massa

### 3.1 Criando o projeto

No [Spring Initializr](https://start.spring.io), selecione:

- **Project:** Maven · **Language:** Java · **Spring Boot:** 4.1.x · **Java:** 21
- **Dependencies:** Spring Web, Validation e **OpenAI** (Spring AI)

O `pom.xml` gerado já traz o BOM do Spring AI e o starter:

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
```

Crie sua chave em [openrouter.ai/keys](https://openrouter.ai/keys) e exporte como variável de ambiente:

```bash
export OPENROUTER_API_KEY=sk-or-...
```

> ⚠️ Nunca coloque a chave direto no código ou no Git.

### 3.2 Apontando o Spring AI para a OpenRouter

`src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: agente-atendimento
  ai:
    openai:
      # A OpenRouter fala o mesmo "idioma" da API da OpenAI: basta trocar a URL e a chave
      api-key: ${OPENROUTER_API_KEY}
      base-url: https://openrouter.ai/api/v1
      chat:
        # Troque o modelo sem mexer no código: https://openrouter.ai/models
        model: ${OPENROUTER_MODEL:openai/gpt-4o-mini}
        temperature: 0.2
        custom-headers:
          # Opcionais: identificam sua aplicação no painel da OpenRouter
          "[HTTP-Referer]": https://github.com/zagcode/prompts-for-article-generate-by-ia
          "[X-OpenRouter-Title]": Agente de Atendimento DIO
```

Três detalhes importantes:

- **`base-url` termina em `/v1`.** No Spring AI 2 o módulo OpenAI usa o SDK oficial `openai-java`, que só acrescenta `/chat/completions`. Sem o `/v1`, a chamada volta 404.
- **`temperature: 0.2`**: atendimento pede respostas consistentes, não criativas.
- **Escolha um modelo com suporte a ferramentas.** No site da OpenRouter, filtre por *tools*. O `openai/gpt-4o-mini` é barato e funciona bem para começar.

### 3.3 As instruções do agente (system prompt)

O comportamento do agente mora num arquivo de texto, fora do Java. Assim qualquer pessoa do time de atendimento consegue revisar. `src/main/resources/prompts/atendente.md`:

```markdown
Você é a Nina, atendente virtual da DevStore, uma loja online fictícia de produtos para desenvolvedores.

## Como atender
- Responda em português do Brasil, com cordialidade e objetividade (no máximo 4 frases).
- Para qualquer pergunta sobre um pedido, use a ferramenta consultarPedido. Nunca invente status, prazos ou valores.
- Se o cliente não informou o número do pedido, peça antes de consultar.
- Se não souber a resposta, se o cliente pedir para falar com uma pessoa, ou se ele estiver irritado,
  use a ferramenta abrirChamado e informe o número do protocolo.
- Não peça nem repita dados sensíveis (senha, número de cartão, CPF completo).
- Assuntos fora da loja: diga educadamente que só pode ajudar com a DevStore.

## Políticas da loja
- Frete grátis em compras acima de R$ 199,00.
- Troca ou devolução em até 7 dias corridos após o recebimento.
- Pagamento via Pix, boleto ou cartão em até 10x sem juros.
- Atendimento humano: segunda a sexta, das 9h às 18h.
```

Repare que as regras mais importantes são sobre **o que não fazer**: não inventar dados, não pedir dados sensíveis, não sair do assunto.

> 💡 Evite chaves `{ }` nesse arquivo: o Spring AI trata o system prompt como template e interpretaria o conteúdo como variável.

### 3.4 Os dados (fictícios) dos pedidos

Para o exemplo, os pedidos ficam em memória. No seu projeto, aqui entraria o banco de dados ou a API do e-commerce.

```java
public record Pedido(String numero, String cliente, String status, LocalDate previsaoEntrega, List<String> itens) {
}
```

```java
@Repository
public class PedidoRepository {

    private final Map<String, Pedido> pedidos = Map.of(
            "1001", new Pedido("1001", "Ana", "ENVIADO", LocalDate.now().plusDays(2),
                    List.of("Teclado mecânico", "Mouse pad XL")),
            "1002", new Pedido("1002", "Bruno", "EM_SEPARACAO", LocalDate.now().plusDays(5),
                    List.of("Monitor 27\"")),
            "1003", new Pedido("1003", "Carla", "ENTREGUE", LocalDate.now().minusDays(3),
                    List.of("Caneca Java", "Adesivos Spring")));

    public Optional<Pedido> buscarPorNumero(String numero) {
        return Optional.ofNullable(pedidos.get(numero.trim()));
    }
}
```

### 3.5 As ferramentas: onde a mágica acontece

Com `@Tool`, um método Java comum vira uma ação que o modelo pode chamar. A **descrição** é o que o LLM lê para decidir quando usar a ferramenta, então capriche nela.

```java
@Component
public class AtendimentoTools {

    private static final Logger log = LoggerFactory.getLogger(AtendimentoTools.class);

    private final PedidoRepository pedidoRepository;
    private final AtomicInteger sequenciaChamados = new AtomicInteger();

    public AtendimentoTools(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Tool(description = "Consulta status, itens e previsão de entrega de um pedido da DevStore pelo número do pedido")
    public String consultarPedido(@ToolParam(description = "Número do pedido, por exemplo 1001") String numeroPedido) {
        log.info("Tool consultarPedido chamada para o pedido {}", numeroPedido);
        return pedidoRepository.buscarPorNumero(numeroPedido)
                .map(p -> "Pedido %s | status: %s | previsão/entrega: %s | itens: %s"
                        .formatted(p.numero(), p.status(), p.previsaoEntrega(), String.join(", ", p.itens())))
                .orElse("Pedido " + numeroPedido + " não encontrado. Peça ao cliente para conferir o número.");
    }

    @Tool(description = "Abre um chamado para um atendente humano quando o agente não consegue resolver ou o cliente pede")
    public String abrirChamado(
            @ToolParam(description = "Resumo do problema do cliente em uma frase") String motivo,
            @ToolParam(description = "Número do pedido relacionado, se houver", required = false) String numeroPedido) {
        String protocolo = "ATD-%d-%04d".formatted(Year.now().getValue(), sequenciaChamados.incrementAndGet());
        log.info("Chamado {} aberto | pedido: {} | motivo: {}", protocolo, numeroPedido, motivo);
        return "Chamado " + protocolo + " aberto. Um atendente humano retorna em até 1 dia útil.";
    }
}
```

Dois cuidados que fazem diferença:

1. **Pedido não encontrado não lança exceção.** A ferramenta devolve uma mensagem que orienta o próximo passo do modelo ("peça ao cliente para conferir o número").
2. **`abrirChamado` é o escalonamento para humano.** Um bom agente reconhece o próprio limite. Num sistema real, esse método criaria o ticket no Zendesk, Jira ou no seu CRM.

### 3.6 Montando o agente: instruções + ferramentas + memória

Um único bean junta as peças no `ChatClient`:

```java
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
```

O `ChatMemory` já vem configurado pelo Spring AI: uma **janela com as 20 últimas mensagens** de cada conversa, guardada em memória. Para produção, troque o repositório por JDBC, Redis ou outro armazenamento persistente.

### 3.7 O endpoint de chat

```java
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
```

O `conversaId` separa a memória de cada cliente. Sem ele, a Ana veria o contexto da conversa do Bruno. Num app real, derive esse valor do usuário logado ou da sessão, nunca de algo que o cliente possa trocar livremente.

## 4. Testando

```bash
./mvnw spring-boot:run
```

Primeira mensagem:

```bash
curl -X POST localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"conversaId":"ana-1","texto":"Oi! Cadê meu pedido 1001?"}'
```

No log, você vê o agente usando a ferramenta:

```
INFO ... c.z.a.agente.AtendimentoTools : Tool consultarPedido chamada para o pedido 1001
```

A resposta vem com os dados reais do pedido. O texto exato varia conforme o modelo; um exemplo:

```json
{"conversaId":"ana-1","texto":"Oi, Ana! Seu pedido 1001 já foi enviado e a previsão de entrega é em 2 dias. Ele contém um teclado mecânico e um mouse pad XL."}
```

Agora teste a **memória** e o **escalonamento**, na mesma conversa:

```bash
curl -X POST localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"conversaId":"ana-1","texto":"O teclado desse pedido veio com defeito, quero falar com alguém"}'
```

O agente lembra que "esse pedido" é o 1001, chama `abrirChamado` e devolve um protocolo como `ATD-2026-0001`.

Vale testar também:

- um pedido que não existe (`9999`): o agente deve pedir para conferir o número;
- uma pergunta sem número ("meu pedido atrasou"): ele deve perguntar qual é o pedido;
- um assunto aleatório ("me conta uma piada sobre Python"): ele deve voltar ao escopo da loja.

## 5. Boas práticas antes de ir para produção

- **Dados vêm de ferramentas, não do modelo.** Tudo que é fato (status, preço, prazo) passa por uma `@Tool`. O prompt diz "nunca invente", e a arquitetura garante.
- **Ferramentas com o mínimo de poder.** `consultarPedido` só lê. Se criar ferramentas que alteram dados (cancelar, reembolsar), valide a identidade do cliente **no código Java**, nunca só no prompt.
- **Sempre tenha uma saída para humano.** Frustração não resolvida custa mais caro do que um chamado.
- **Cuidado com dados pessoais (LGPD).** Não registre conversas inteiras em log e evite mandar ao modelo dados que ele não precisa.
- **Controle os custos.** Defina limites de gasto no painel da OpenRouter, use modelos menores para perguntas simples e mantenha a janela de memória curta.
- **Trate falhas.** O `@ExceptionHandler` garante uma resposta educada se o provedor cair. Os `retries` do Spring AI (3 por padrão) cuidam de falhas momentâneas.
- **Teste as ferramentas sem IA.** Elas são métodos Java comuns, dá para cobrir com JUnit sem gastar tokens (o repositório tem exemplos).

## 6. E se eu usar Quarkus?

Com a extensão **Quarkus LangChain4j** (`io.quarkiverse.langchain4j:quarkus-langchain4j-openai`), a ideia é a mesma, mas declarativa, com uma interface. Um esboço:

```java
@RegisterAiService
public interface Atendente {

    @SystemMessage(fromResource = "prompts/atendente.md")
    @ToolBox(AtendimentoTools.class)
    String conversar(@MemoryId String conversaId, @UserMessage String texto);
}
```

```properties
quarkus.langchain4j.openai.api-key=${OPENROUTER_API_KEY}
quarkus.langchain4j.openai.base-url=https://openrouter.ai/api/v1
quarkus.langchain4j.openai.chat-model.model-name=openai/gpt-4o-mini
```

Nas ferramentas, a anotação é `@Tool` do pacote `dev.langchain4j.agent.tool`, numa classe `@ApplicationScoped`.

## Conclusão

Com quatro peças (**modelo, instruções, ferramentas e memória**) você sai de um chatbot engessado para um agente que conversa naturalmente, consulta seus sistemas e sabe pedir ajuda. O Spring AI cuida da parte chata, e a OpenRouter deixa você trocar de modelo quando quiser.

**Próximo passo natural:** conectar uma base de conhecimento (FAQ, políticas, manuais) com **RAG**, para o agente responder sobre qualquer documento da empresa, e não só sobre o que está no prompt.

---

### 🚀 Agora é com você

📂 **O código completo** (com testes) está em: **[github.com/zagcode/prompts-for-article-generate-by-ia](https://github.com/zagcode/prompts-for-article-generate-by-ia/tree/main/artigos/agente-atendimento-ia/codigo)**. Clone, coloque sua chave da OpenRouter e converse com a Nina.

💬 **Me conta nos comentários:** em qual processo da sua empresa (ou do seu projeto) um agente desses ajudaria? Suporte, vendas, RH?

👍 Se o artigo te ajudou, **deixa sua curtida** e **dá uma ⭐ no repositório**. Isso ajuda o conteúdo a chegar em mais devs da comunidade DIO!

---

<sub>Artigo produzido com apoio de IA (Claude, da Anthropic) seguindo o checklist de artigos da DIO. O código foi compilado e testado, e os dados de pedidos e da loja DevStore são fictícios. Referências: [Spring AI: Tool Calling](https://docs.spring.io/spring-ai/reference/api/tools.html) · [Spring AI: Chat Memory](https://docs.spring.io/spring-ai/reference/api/chat-memory.html) · [Spring AI: OpenAI Chat](https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html) · [OpenRouter: Quickstart](https://openrouter.ai/docs/quickstart) · [Quarkus LangChain4j: AI Services](https://docs.quarkiverse.io/quarkus-langchain4j/dev/ai-services.html)</sub>
