# Agente de atendimento com IA: Spring Boot + Spring AI + OpenRouter

Código do artigo [Do zero ao agente: atendimento com IA em Spring Boot + OpenRouter](../04-artigo.md).

A **Nina** é a atendente virtual da DevStore, uma loja fictícia. Ela responde dúvidas, consulta pedidos por *tool calling*, lembra da conversa e abre chamado para um humano quando precisa.

## Stack

- Java 21
- Spring Boot 4.1
- Spring AI 2.0 (`spring-ai-starter-model-openai`)
- OpenRouter (API compatível com OpenAI)

## Como rodar

1. Crie uma chave em https://openrouter.ai/keys
2. Exporte a chave e suba a aplicação:

```bash
export OPENROUTER_API_KEY=sk-or-...
# opcional: outro modelo com suporte a tools
export OPENROUTER_MODEL=openai/gpt-4o-mini

./mvnw spring-boot:run
```

No Windows (PowerShell): `$env:OPENROUTER_API_KEY="sk-or-..."; .\mvnw.cmd spring-boot:run`

3. Converse:

```bash
curl -X POST localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"conversaId":"ana-1","texto":"Oi! Cadê meu pedido 1001?"}'
```

Pedidos fictícios disponíveis: `1001` (enviado), `1002` (em separação) e `1003` (entregue).

## Testes

```bash
./mvnw test
```

Os testes não chamam a OpenRouter: as ferramentas são testadas como métodos Java comuns, e o contexto sobe com uma chave fictícia.

## Estrutura

```
src/main/java/com/zagcode/atendimento
├── agente/AgenteConfig.java       # ChatClient = instruções + ferramentas + memória
├── agente/AtendimentoTools.java   # @Tool consultarPedido e abrirChamado
├── chat/ChatController.java       # POST /chat
└── pedido/                        # Pedido + PedidoRepository (dados fictícios em memória)
src/main/resources
├── application.yml                # OpenRouter como provedor
└── prompts/atendente.md           # system prompt da Nina
```
