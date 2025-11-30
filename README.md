# Sistema de Egressos

Aplicação desktop em Java/Swing para gestão de egressos acadêmicos. O sistema permite cadastro, pesquisa e acompanhamento de ex-alunos, gerenciamento de eventos e emissão de relatórios em PDF, com controle de acesso por perfis (egresso, docente e coordenação).

## Funcionalidades principais
- Autenticação e criação de usuários com política de senha e fluxo de recuperação via token temporário.
- Cadastro e edição de perfis de egressos, incluindo filtros de pesquisa e registro de visualizações por docentes.
- Divulgação e inscrição em eventos, com aprovação/gestão pela coordenação.
- Emissão de relatórios em PDF sobre egressos e atividades.
- Persistência em arquivos CSV simples para facilitar execução local.

## Requisitos
- Java 17+
- Maven 3.9+

## Configuração e execução
1. Instale as dependências e compile o projeto:
   ```bash
   mvn clean compile
   ```
2. Execute a aplicação (o plugin `exec` será baixado automaticamente pelo Maven):
   ```bash
   mvn -Dexec.mainClass=com.egressos.Main exec:java
   ```
3. Na primeira execução um usuário padrão é criado automaticamente:
   - Usuário: `admin@fct.br`
   - Senha: `Administrador@123`

Os dados de exemplo ficam em `src/main/java/data` e são populados/atualizados conforme as ações na interface.

## Testes
Execute a suíte de testes JUnit:
```bash
mvn test
```

## Estrutura do projeto
- `src/main/java/com/egressos` — código-fonte principal (controllers, services, modelos e telas Swing).
- `src/main/java/data` — arquivos CSV usados como armazenamento local.
- `src/test/java` — testes automatizados JUnit.
- `pom.xml` — dependências e configuração do Maven (Java 17, JUnit 5, OpenPDF).

## Licença
Este projeto é fornecido apenas para fins acadêmicos e pode ser adaptado conforme necessário.
