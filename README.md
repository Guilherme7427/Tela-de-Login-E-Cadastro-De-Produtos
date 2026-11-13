# 🛒 Sistema de Autenticação e Gestão de Produtos

Aplicação web desenvolvida com **Java (Spring Boot)** no back-end e **HTML/CSS** no front-end. O sistema conta com tela de login, autopreenchimento de endereço via integração com a **API Pública ViaCEP** e um CRUD completo de produtos com persistência e atualização automática no banco de dados.

---

## ✨ Funcionalidades

- **Tela de Login & Cadastro:** Autenticação de usuários e formulário de registro.
- **Integração com ViaCEP:** Busca automática de endereço (logradouro, bairro, cidade e estado) a partir do CEP informado.
- **Gestão de Produtos (CRUD):**
  - Cadastro de novos produtos diretamente pela interface.
  - Listagem dinâmica com retorno em tempo real dos dados salvos no banco.
  - Edição de produtos existentes com atualização automática no banco de dados.

---

## 🛠️ Tecnologias Utilizadas

- **Back-end:** Java, Spring Boot (Spring Web, Spring Data JPA)
- **Front-end:** HTML5, CSS3, JavaScript (consumo de API)
- **API Externa:** [ViaCEP](https://viacep.com.br/)
- **Banco de Dados:** Relacional (MySQL)

---

## ⚙️ Pré-requisitos e Configuração para Execução

Para que o projeto funcione corretamente em sua máquina, siga os passos abaixo:

### 1. Importação do Banco de Dados
Antes de iniciar a aplicação, certifique-se de criar o schema e importar o arquivo `.sql` (dump/script do banco de dados) disponibilizado neste repositório para o seu SGBD (MySQL Workbench, DBeaver, pgAdmin, etc.).

### 2. Conexão com o Banco no Código Java
Abra o arquivo de configuração do Spring Boot localizado em:
`src/main/resources/application.properties`

Configure as credenciais de acordo com o seu banco de dados local:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/nome_do_seu_banco
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
