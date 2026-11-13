package com.telalogin.login.Repository;

import com.telalogin.login.Usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByNomeUsuario(String nomeUsuario);

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
}