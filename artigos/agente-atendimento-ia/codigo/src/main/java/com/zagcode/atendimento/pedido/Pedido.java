package com.zagcode.atendimento.pedido;

import java.time.LocalDate;
import java.util.List;

public record Pedido(String numero, String cliente, String status, LocalDate previsaoEntrega, List<String> itens) {
}
