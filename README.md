# CatalogoApp

## Descrição do Projeto
O CatalogoApp é uma aplicação robusta de gerenciamento de produtos e categorias, desenvolvida como material prático para as aulas na disciplina de "Desenvolvimento para Servidores II" no curso de Sistemas para Internet. O projeto foi construído com o objetivo principal de proporcionar aos alunos o aprendizado sobre o desenvolvimento de sistemas full-stack, focando em boas práticas de arquitetura, segurança e persistência de dados.

Através deste projeto, exploramos a transição de armazenamentos temporários em memória para soluções profissionais utilizando bancos de dados relacionais e controle de acesso baseado em perfis.

## Tecnologias Utilizadas
*   Java 21 e Spring Boot 3
*   Spring Data JPA para persistência de dados
*   PostgreSQL como banco de dados relacional
*   Spring Security para autenticação e autorização
*   Thymeleaf para renderização de telas dinâmicas
*   Bootstrap 5 para interface responsiva
*   BCrypt para criptografia de senhas

## Principais Funcionalidades
*   Gerenciamento completo (CRUD) de Produtos e Categorias.
*   Sistema de busca e filtros dinâmicos por nome e categoria.
*   Controle de acesso diferenciado:
    *   Administrador (ADMIN): Acesso total para gerenciar produtos, categorias e usuários.
    *   Usuário (USER): Acesso de consulta aos produtos cadastrados.
*   Persistência de usuários no banco de dados com senhas protegidas por hash.
*   Inicialização automática de dados (Data Seeder) para facilitar o primeiro acesso ao ambiente de desenvolvimento.

## Estrutura de Pastas e Padrões
O projeto segue o padrão de responsabilidades separadas para garantir a manutenibilidade:
*   Models: Representação das entidades do banco de dados.
*   Repositories: Interfaces para comunicação direta com o PostgreSQL.
*   Services: Camada de lógica de negócio e autenticação customizada.
*   Controllers: Gerenciamento de rotas e fluxo entre a View e a Model.
*   Config: Classes de configuração de segurança e inicialização do sistema.

## Como Executar
1. Certifique-se de ter o Java 21 e o PostgreSQL instalados.
2. Clone o repositório.
3. Ajuste as credenciais do banco de dados no arquivo application.properties.
4. Antes de iniciar pela primeira vez, aplicar a migração de esquema para garantir que as colunas
     `quantidade` e `data_atualizacao` existam e que os dados antigos sejam preenchidos.

     Opções:
     - Se você quiser que as migrações sejam aplicadas automaticamente, o projeto inclui o
         `flyway-core` (adicionado no pom). Ao iniciar a aplicação com `./mvnw spring-boot:run`,
         o Flyway executará os scripts em `src/main/resources/db/migration` automaticamente.

     - Alternativamente, você pode executar manualmente o script SQL `V2__add_quantidade_data_atualizacao.sql`
         localizado em `src/main/resources/db/migration` contra o banco PostgreSQL antes de subir a aplicação:

```bash
psql -h localhost -p 5433 -U postgres -d catalogo_db -f src/main/resources/db/migration/V2__add_quantidade_data_atualizacao.sql
```

5. Execute a aplicação via IDE (como IntelliJ) ou terminal usando `./mvnw spring-boot:run`.
6. Ao iniciar pela primeira vez, o sistema criará automaticamente um usuário administrador inicial (admin / admin123).

### Passos rápidos para avaliação

- Ajuste `application.properties` com o banco de dados local antes de rodar.
- Execute os testes automatizados do projeto:

```powershell
.\mvnw.cmd test
```

- Em seguida, inicie a aplicação:

```powershell
.\mvnw.cmd spring-boot:run
```

- Acesse no navegador:
  - `http://localhost:8080/produtos`
  - `http://localhost:8080/login`
- Faça login com `admin` / `admin123`.

### Transição de dados: do formulário ao banco

- O formulário HTML (`cadastro-produto.html`) envia os campos do produto via POST para `/produtos/salvar`.
- O `ProdutoController` recebe o `ProdutoModel` (data binding do Thymeleaf) e valida o formulário com Bean Validation.
- Caso existam erros de binding/validação, a view é reexibida com mensagens inline (`invalid-feedback`).
- A validação de regras de negócio (por exemplo: `quantidade` obrigatória e não-negativa, unicidade de nome)
    é feita na camada `ProdutoService` antes de persistir. O `ProdutoService.salvar` lança `IllegalArgumentException`
    ou `RuntimeException` com mensagens amigáveis que são convertidas em erros de campo no Controller.
- Antes de salvar, o `ProdutoService` atualiza o campo `dataAtualizacao` com `LocalDateTime.now()` para registrar
    o momento da modificação; esse valor é exibido na listagem como confirmação (mensagem de sucesso contendo o horário).

### Permissões e comportamento UX

- A tela de Auditoria (`/produtos/historico`) e os botões de edição/exclusão são visíveis apenas para `ROLE_ADMIN`.
- Usuários sem permissão não verão os botões nem conseguirão acessar as rotas (SecurityConfig aplica as restrições).

### Observações para avaliação

- O projeto implementa validação de negócio na camada `Service` (veja `ProdutoService`).
- As views usam `sec:authorize` para ocultar elementos administrativos (`lista-produtos.html`, `historico-produtos.html`).
- A migração SQL e o uso de Flyway garantem sincronização segura com PostgreSQL para a entrega.

## Créditos
Projeto desenvolvido em contexto acadêmico na FATEC Jales, sob a orientação do Professor James Campos, visando a formação em desenvolvimento multiplataforma e engenharia de software.
