package com.zagcode.atendimento.pedido;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

/**
 * Dados fictícios em memória. Num projeto real, aqui entraria o banco de dados ou a API do e-commerce.
 */
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
