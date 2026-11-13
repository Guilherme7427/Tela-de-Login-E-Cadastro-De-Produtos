/* Lógica compartilhada das telas de produtos (Controle de Estoque).
   Os dados ficam no localStorage do navegador, na chave "produtos". */

const CHAVE = "produtos";
const CATEGORIAS = ["Alimentos", "Bebidas", "Limpeza", "Eletrônicos", "Papelaria", "Outros"];

/* ---------- Armazenamento (Create / Read / Update / Delete) ---------- */
const Produtos = {
  todos() {
    try { return JSON.parse(localStorage.getItem(CHAVE)) || []; }
    catch { return []; }
  },
  salvarTodos(lista) {
    localStorage.setItem(CHAVE, JSON.stringify(lista));
  },
  buscar(id) {
    return this.todos().find(p => p.id === id);
  },
  criar(dados) {
    const lista = this.todos();
    const produto = { ...dados, id: gerarId() };
    lista.push(produto);
    this.salvarTodos(lista);
    return produto;
  },
  atualizar(id, dados) {
    const lista = this.todos().map(p => (p.id === id ? { ...p, ...dados, id } : p));
    this.salvarTodos(lista);
  },
  excluir(id) {
    this.salvarTodos(this.todos().filter(p => p.id !== id));
  },
  codigoExiste(codigo, ignorarId) {
    const alvo = codigo.trim().toUpperCase();
    return this.todos().some(p => p.codigo.toUpperCase() === alvo && p.id !== ignorarId);
  }
};

function gerarId() {
  return (window.crypto && crypto.randomUUID) ? crypto.randomUUID() : String(Date.now()) + Math.random().toString(16).slice(2);
}

function escapar(texto) {
  return String(texto).replace(/[&<>"']/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
}

/* ---------- Preço ---------- */
function parsePreco(texto) {
  const limpo = String(texto).trim().replace(/[R$\s]/g, "").replace(/\./g, "").replace(",", ".");
  const numero = Number(limpo);
  return limpo !== "" && isFinite(numero) ? numero : NaN;
}

function formatarPreco(numero) {
  return Number(numero).toFixed(2).replace(".", ",");
}

/* ---------- Formulário ---------- */
function montarFormulario(destino, botoesHTML) {
  const opcoes = CATEGORIAS.map(c => `<option value="${escapar(c)}">${escapar(c)}</option>`).join("");

  destino.innerHTML = `
    <form id="form-produto" novalidate>
      <div class="linha">
        <div class="campo">
          <label for="codigo">Código</label>
          <div class="entrada"><span class="icone">#</span>
            <input id="codigo" name="codigo" type="text" maxlength="20" placeholder="Ex.: P-001" autocomplete="off">
          </div>
          <small class="erro" data-erro="codigo"></small>
        </div>
        <div class="campo">
          <label for="categoria">Categoria</label>
          <div class="entrada"><span class="icone">☰</span>
            <select id="categoria" name="categoria">
              <option value="">Selecione</option>${opcoes}
            </select>
          </div>
          <small class="erro" data-erro="categoria"></small>
        </div>
      </div>

      <div class="campo">
        <label for="nome">Nome do produto</label>
        <div class="entrada"><span class="icone">▣</span>
          <input id="nome" name="nome" type="text" maxlength="80" autocomplete="off">
        </div>
        <small class="erro" data-erro="nome"></small>
      </div>

      <div class="linha">
        <div class="campo">
          <label for="quantidade">Quantidade</label>
          <div class="entrada"><span class="icone">Qtd</span>
            <input id="quantidade" name="quantidade" type="text" inputmode="numeric" placeholder="0" autocomplete="off">
          </div>
          <small class="erro" data-erro="quantidade"></small>
        </div>
        <div class="campo">
          <label for="preco">Preço</label>
          <div class="entrada"><span class="icone">R$</span>
            <input id="preco" name="preco" type="text" inputmode="decimal" placeholder="0,00" autocomplete="off">
          </div>
          <small class="erro" data-erro="preco"></small>
        </div>
      </div>

      <div class="campo">
        <label for="descricao">Descrição</label>
        <div class="entrada area"><span class="icone">✎</span>
          <textarea id="descricao" name="descricao" maxlength="300"></textarea>
        </div>
      </div>

      <div class="acoes">${botoesHTML}</div>
    </form>`;

  const form = destino.querySelector("#form-produto");

  // Quantidade: só dígitos
  form.quantidade.addEventListener("input", () => {
    form.quantidade.value = form.quantidade.value.replace(/\D/g, "");
  });
  // Preço: formata com duas casas ao sair do campo
  form.preco.addEventListener("blur", () => {
    const n = parsePreco(form.preco.value);
    if (!isNaN(n)) form.preco.value = formatarPreco(n);
  });
  // Limpa o erro do campo ao digitar
  form.addEventListener("input", e => limparErro(form, e.target.name));
  form.addEventListener("change", e => limparErro(form, e.target.name));

  return form;
}

function lerFormulario(form) {
  return {
    codigo: form.codigo.value,
    nome: form.nome.value,
    categoria: form.categoria.value,
    quantidade: form.quantidade.value,
    preco: form.preco.value,
    descricao: form.descricao.value
  };
}

function preencherFormulario(form, produto) {
  form.codigo.value = produto.codigo;
  form.nome.value = produto.nome;
  form.categoria.value = produto.categoria;
  form.quantidade.value = produto.quantidade;
  form.preco.value = formatarPreco(produto.preco);
  form.descricao.value = produto.descricao || "";
}

function normalizar(dados) {
  return {
    codigo: dados.codigo.trim().toUpperCase(),
    nome: dados.nome.trim(),
    categoria: dados.categoria,
    quantidade: Number(dados.quantidade),
    preco: parsePreco(dados.preco),
    descricao: dados.descricao.trim()
  };
}

/* ---------- Validação ---------- */
function validarProduto(dados, ignorarId) {
  const erros = {};
  const codigo = dados.codigo.trim();

  if (!codigo) erros.codigo = "Informe o código.";
  else if (!/^[A-Za-z0-9._-]+$/.test(codigo)) erros.codigo = "Use apenas letras, números, ponto, hífen ou _.";
  else if (Produtos.codigoExiste(codigo, ignorarId)) erros.codigo = "Já existe um produto com esse código.";

  if (!dados.nome.trim()) erros.nome = "Informe o nome do produto.";
  else if (dados.nome.trim().length < 2) erros.nome = "O nome deve ter ao menos 2 caracteres.";

  if (!dados.categoria) erros.categoria = "Escolha uma categoria.";

  if (dados.quantidade === "") erros.quantidade = "Informe a quantidade.";
  else if (!/^\d+$/.test(dados.quantidade)) erros.quantidade = "Use um número inteiro.";

  const preco = parsePreco(dados.preco);
  if (dados.preco.trim() === "") erros.preco = "Informe o preço.";
  else if (isNaN(preco) || preco <= 0) erros.preco = "Informe um valor maior que zero.";

  return erros;
}

function mostrarErros(form, erros) {
  form.querySelectorAll("[data-erro]").forEach(el => (el.textContent = ""));
  form.querySelectorAll(".entrada").forEach(el => el.classList.remove("invalido"));

  const campos = Object.keys(erros);
  campos.forEach(nome => {
    form.querySelector(`[data-erro="${nome}"]`).textContent = erros[nome];
    form[nome].closest(".entrada").classList.add("invalido");
  });
  if (campos.length) form[campos[0]].focus();
}

function limparErro(form, nome) {
  const msg = form.querySelector(`[data-erro="${nome}"]`);
  if (!msg) return;
  msg.textContent = "";
  form[nome].closest(".entrada").classList.remove("invalido");
}

/* ---------- Aviso rápido ---------- */
let temporizadorToast;
function toast(mensagem, tipo) {
  let el = document.getElementById("toast");
  if (!el) {
    el = document.createElement("div");
    el.id = "toast";
    document.body.appendChild(el);
  }
  el.className = "toast" + (tipo === "erro" ? " erro-toast" : "");
  el.textContent = mensagem;
  requestAnimationFrame(() => el.classList.add("mostrar"));
  clearTimeout(temporizadorToast);
  temporizadorToast = setTimeout(() => el.classList.remove("mostrar"), 2600);
}