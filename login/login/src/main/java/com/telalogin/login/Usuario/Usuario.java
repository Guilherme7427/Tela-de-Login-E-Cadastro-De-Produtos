package com.telalogin.login.Usuario;

import com.telalogin.login.Senhas.Senha;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "cadastrodb")
public class Usuario extends Senha {

    @Column(name = "nome_usuario")
    private String nomeUsuario;

    @Column(unique = true)
    private String email;

    private String endereco;
    private String cep;
    private String numero;
    private String telefone;

    @Column(length = 300)
    private String observacao;

    public Usuario() {
    }

    public Usuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public Usuario(String senha, String nomeUsuario) {
        super(senha);
        this.nomeUsuario = nomeUsuario;
    }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
 