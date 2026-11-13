package com.telalogin.login.Controller;

import com.telalogin.login.Produto;
import com.telalogin.login.ProdutoDTO;
import com.telalogin.login.Repository.ProdutoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    /** Listar: GET /produtos */
    @GetMapping
    public List<ProdutoDTO.Resposta> listar() {
        return produtoRepository.findAll(Sort.by("nome")).stream().map(ProdutoDTO.Resposta::de).toList();
    }

    /** Cadastrar: POST /produtos */
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody ProdutoDTO.Dados dto) {
        String problema = validar(dto);
        if (problema != null) {
            return erro(HttpStatus.BAD_REQUEST, problema);
        }
        if (produtoRepository.existsByCodigoIgnoreCase(dto.codigo().trim())) {
            return erro(HttpStatus.CONFLICT, "Já existe um produto com esse código.");
        }

        Produto produto = new Produto();
        preencher(produto, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProdutoDTO.Resposta.de(produtoRepository.save(produto)));
    }

    /** Atualizar: PUT /produtos/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody ProdutoDTO.Dados dto) {
        Optional<Produto> encontrado = produtoRepository.findById(id);
        if (encontrado.isEmpty()) {
            return erro(HttpStatus.NOT_FOUND, "Produto não encontrado.");
        }
        String problema = validar(dto);
        if (problema != null) {
            return erro(HttpStatus.BAD_REQUEST, problema);
        }
        if (produtoRepository.existsByCodigoIgnoreCaseAndIdNot(dto.codigo().trim(), id)) {
            return erro(HttpStatus.CONFLICT, "Já existe um produto com esse código.");
        }

        Produto produto = encontrado.get();
        preencher(produto, dto);
        return ResponseEntity.ok(ProdutoDTO.Resposta.de(produtoRepository.save(produto)));
    }

    /** Excluir: DELETE /produtos/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (!produtoRepository.existsById(id)) {
            return erro(HttpStatus.NOT_FOUND, "Produto não encontrado.");
        }
        produtoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- auxiliares ----------

    private void preencher(Produto p, ProdutoDTO.Dados d) {
        p.setCodigo(d.codigo().trim());
        p.setNome(d.nome().trim());
        p.setCategoria(d.categoria().trim());
        p.setQuantidade(d.quantidade());
        p.setPreco(d.preco());
        p.setDescricao(vazio(d.descricao()) ? null : d.descricao().trim());
    }

    private String validar(ProdutoDTO.Dados d) {
        if (vazio(d.codigo())) return "Informe o código do produto.";
        if (vazio(d.nome())) return "Informe o nome do produto.";
        if (vazio(d.categoria())) return "Informe a categoria.";
        if (d.quantidade() == null || d.quantidade() < 0) return "A quantidade deve ser um número inteiro, 0 ou mais.";
        if (d.preco() == null || d.preco().compareTo(BigDecimal.ZERO) <= 0) return "Informe um preço maior que zero.";
        if (d.codigo().trim().length() > 20) return "O código pode ter no máximo 20 caracteres.";
        if (d.nome().trim().length() > 60) return "O nome pode ter no máximo 60 caracteres.";
        if (d.categoria().trim().length() > 30) return "A categoria pode ter no máximo 30 caracteres.";
        if (d.descricao() != null && d.descricao().trim().length() > 200) return "A descrição pode ter no máximo 200 caracteres.";
        return null;
    }

    private boolean vazio(String s) {
        return s == null || s.isBlank();
    }

    private ResponseEntity<Map<String, String>> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("erro", mensagem));
    }
}
