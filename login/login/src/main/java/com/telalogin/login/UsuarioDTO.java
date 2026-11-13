package com.telalogin.login;

import com.telalogin.login.Usuario.Usuario;

/**
 * Objetos de entrada/saída da API.
 * A resposta NUNCA inclui a senha.
 */
public final class UsuarioDTO {

    private UsuarioDTO() {
    }

    public record Cadastro(String nome, String email, String endereco, String cep,
                           String numero, String telefone, String observacao, String senha) {
    }

    public record Login(String email, String senha) {
    }

    public record Resposta(Long id, String nome, String email, String endereco, String cep,
                           String numero, String telefone, String observacao) {

        public static Resposta de(Usuario u) {
            return new Resposta(u.getId(), u.getNomeUsuario(), u.getEmail(), u.getEndereco(),
                    u.getCep(), u.getNumero(), u.getTelefone(), u.getObservacao());
        }
    }
}