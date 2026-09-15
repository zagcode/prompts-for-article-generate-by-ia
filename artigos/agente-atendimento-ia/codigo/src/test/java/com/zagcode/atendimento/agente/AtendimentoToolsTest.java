package com.zagcode.atendimento.agente;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.zagcode.atendimento.pedido.PedidoRepository;

class AtendimentoToolsTest {

    private final AtendimentoTools tools = new AtendimentoTools(new PedidoRepository());

    @Test
    void consultaPedidoExistente() {
        assertThat(tools.consultarPedido("1001")).contains("ENVIADO", "Teclado mecânico");
    }

    @Test
    void informaQuandoPedidoNaoExiste() {
        assertThat(tools.consultarPedido("9999")).contains("não encontrado");
    }

    @Test
    void abreChamadoComProtocoloSequencial() {
        assertThat(tools.abrirChamado("Produto com defeito", "1003")).contains("ATD-").contains("-0001");
        assertThat(tools.abrirChamado("Quero falar com uma pessoa", null)).contains("-0002");
    }
}
