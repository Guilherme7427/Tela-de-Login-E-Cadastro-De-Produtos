package com.telalogin.login;


import java.math.BigDecimal;

/** Objetos de entrada/saída da API de produtos. */
public final class ProdutoDTO {

    private ProdutoDTO() {
    }

    /** Usado tanto no cadastro (POST) quanto na edição (PUT). */
    public record Dados(String codigo, String nome, String categoria,
                        Integer quantidade, BigDecimal preco, String descricao) {
    }

    public record Resposta(Long id, String codigo, String nome, String categoria,
                           Integer quantidade, BigDecimal preco, String descricao) {

        public static Resposta de(com.telalogin.login.Produto p) {
            return new Resposta(p.getId(), p.getCodigo(), p.getNome(), p.getCategoria(),
                    p.getQuantidade(), p.getPreco(), p.getDescricao());
        }
    }
}
