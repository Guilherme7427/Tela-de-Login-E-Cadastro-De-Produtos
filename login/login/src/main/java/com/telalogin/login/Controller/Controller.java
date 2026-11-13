package com.telalogin.login.Controller;

import com.telalogin.login.Repository.UsuarioRepository;
import com.telalogin.login.Usuario.Usuario;
import com.telalogin.login.UsuarioDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class Controller {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Controller(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UsuarioDTO.Resposta> listarTodos() {
        return usuarioRepository.findAll().stream().map(UsuarioDTO.Resposta::de).toList();
    }

    /** Cadastro: POST /usuarios */
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody UsuarioDTO.Cadastro dto) {
        String problema = validar(dto);
        if (problema != null) {
            return erro(HttpStatus.BAD_REQUEST, problema);
        }

        String email = dto.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            return erro(HttpStatus.CONFLICT, "Este e-mail já está cadastrado.");
        }

        Usuario usuario = new Usuario(passwordEncoder.encode(dto.senha()), dto.nome().trim());
        usuario.setEmail(email);
        usuario.setEndereco(dto.endereco().trim());
        usuario.setCep(dto.cep().replaceAll("\\D", ""));
        usuario.setNumero(dto.numero().trim());
        usuario.setTelefone(dto.telefone().replaceAll("\\D", ""));
        usuario.setObservacao(dto.observacao() == null ? null : dto.observacao().trim());

        Usuario salvo = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioDTO.Resposta.de(salvo));
    }

    /** Login: POST /usuarios/login */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioDTO.Login dto) {
        if (vazio(dto.email()) || vazio(dto.senha())) {
            return erro(HttpStatus.BAD_REQUEST, "Informe e-mail e senha.");
        }

        Optional<Usuario> encontrado = usuarioRepository.findByEmail(dto.email().trim().toLowerCase());
        if (encontrado.isEmpty() || !passwordEncoder.matches(dto.senha(), encontrado.get().getSenha())) {
            return erro(HttpStatus.UNAUTHORIZED, "E-mail ou senha incorretos.");
        }
        return ResponseEntity.ok(UsuarioDTO.Resposta.de(encontrado.get()));
    }

    // ---------- auxiliares ----------

    private String validar(UsuarioDTO.Cadastro d) {
        if (vazio(d.nome()) || vazio(d.email()) || vazio(d.endereco()) || vazio(d.cep())
                || vazio(d.numero()) || vazio(d.telefone()) || vazio(d.senha())) {
            return "Preencha todos os campos obrigatórios.";
        }
        if (!d.email().contains("@")) return "E-mail inválido.";
        if (d.cep().replaceAll("\\D", "").length() != 8) return "O CEP precisa ter 8 dígitos.";
        if (d.telefone().replaceAll("\\D", "").length() < 10) return "Informe o telefone com DDD.";
        if (d.senha().length() < 8) return "A senha precisa ter pelo menos 8 caracteres.";
        return null;
    }

    private boolean vazio(String s) {
        return s == null || s.isBlank();
    }

    private ResponseEntity<Map<String, String>> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("erro", mensagem));
    }
}