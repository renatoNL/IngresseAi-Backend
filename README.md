# IngresseAi-Backend

## Protecao de dados e banco

- Gere duas chaves distintas de 32 bytes em Base64 (`openssl rand -base64 32`) e configure `DATA_ENCRYPTION_KEY` e `DATA_INDEX_KEY` no ambiente. Mantenha os mesmos valores entre deploys; trocar as chaves sem recriptografar os registros impede a leitura dos documentos.
- Antes de iniciar com a conta limitada, crie `app_runtime` e conceda os privilégios de runtime no PostgreSQL como proprietário do banco.
- No Render, configure `DB_USERNAME=app_runtime`, `DB_PASSWORD` com a senha definida e `JPA_DDL_AUTO=validate`. O schema precisa estar atualizado antes do deploy; `validate` evita que a conta de runtime precise de privilégios de DDL.
- No Docker Compose local, `JPA_DDL_AUTO=update` permanece habilitado para o banco descartável de desenvolvimento. Configure as duas chaves de dados no ambiente local antes de subir o backend.
- No startup, o backend migra em lotes os registros antigos: gera o índice HMAC e regrava o CPF/CNPJ com AES-GCM. Faça backup antes do primeiro deploy e mantenha as chaves configuradas; sem elas, a aplicação não inicializa e os registros cifrados não podem ser lidos.
- A criptografia em repouso do volume do PostgreSQL no Render depende da configuração/plano do serviço. Confirme o estado no painel do banco, em vez de assumir que está ativo.
