INSERT IGNORE INTO eventos (titulo, descricao, tipo_evento, data_evento, vendedor_id)
WITH RECURSIVE numeros AS (
    SELECT 1 AS numero
    UNION ALL
    SELECT numero + 1 FROM numeros WHERE numero < 50
)
SELECT CONCAT('Evento demonstrativo ', numero),
       CONCAT('Descricao do evento demonstrativo ', numero),
       CASE MOD(numero, 4) WHEN 0 THEN 'SHOW' WHEN 1 THEN 'TEATRO' WHEN 2 THEN 'ESPORTE' ELSE 'FESTIVAL' END,
       DATE_ADD('2026-10-01 20:00:00', INTERVAL numero DAY),
       1
FROM numeros;

INSERT IGNORE INTO ingressos (evento_id, quantidade, valor)
SELECT e.id, 100, 49.90 + MOD(e.id, 5) * 10
FROM eventos e
WHERE e.titulo LIKE 'Evento demonstrativo %';
