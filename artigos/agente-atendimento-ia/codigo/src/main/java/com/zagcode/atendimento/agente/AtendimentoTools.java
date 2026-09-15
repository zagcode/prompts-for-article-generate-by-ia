package com.zagcode.atendimento.agente;

import java.time.Year;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.zagcode.atendimento.pedido.PedidoRepository;

/**
 * Ferramentas que o modelo pode chamar. A descrição de cada @Tool é o que o LLM lê para decidir quando usá-la.
 */
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
