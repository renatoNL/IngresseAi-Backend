INSERT IGNORE INTO eventos (titulo, descricao, tipo_evento, data_evento, vendedor_id)
SELECT CONCAT('Evento demonstrativo ', numero),
       CONCAT('Descricao do evento demonstrativo ', numero),
       CASE MOD(numero, 4) WHEN 0 THEN 'SHOW' WHEN 1 THEN 'TEATRO' WHEN 2 THEN 'ESPORTE' ELSE 'FESTIVAL' END,
       DATE_ADD('2026-10-01 20:00:00', INTERVAL numero DAY),
       1
FROM (
    SELECT dezenas.numero * 10 + unidades.numero AS numero
    FROM (
        SELECT 0 AS numero UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
        UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
    ) dezenas
    CROSS JOIN (
        SELECT 0 AS numero UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
        UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
    ) unidades
    WHERE dezenas.numero * 10 + unidades.numero BETWEEN 1 AND 50
) numeros;

INSERT IGNORE INTO ingressos (evento_id, quantidade, valor)
SELECT e.id, 100, 49.90 + MOD(e.id, 5) * 10
FROM eventos e
WHERE e.titulo LIKE 'Evento demonstrativo %';
