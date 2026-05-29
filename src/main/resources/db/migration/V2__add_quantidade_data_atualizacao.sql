-- V2: adiciona colunas `quantidade` e `data_atualizacao`, faz backfill e aplica NOT NULL
-- Ajuste criado em 2026-05-29 para atividade de Gestão de Estoque e Auditoria

ALTER TABLE produto ADD COLUMN IF NOT EXISTS quantidade integer DEFAULT 0;
UPDATE produto SET quantidade = 0 WHERE quantidade IS NULL;
ALTER TABLE produto ALTER COLUMN quantidade SET NOT NULL;

ALTER TABLE produto ADD COLUMN IF NOT EXISTS data_atualizacao timestamp;
UPDATE produto SET data_atualizacao = now() WHERE data_atualizacao IS NULL;
-- Não forçar NOT NULL em data_atualizacao para evitar impacto em registros muito antigos

-- Observação: se o projeto estiver usando Hibernate com `spring.jpa.hibernate.ddl-auto=update`,
-- a migration pode ser redundante; porém este script garante backfill e valores consistentes.
