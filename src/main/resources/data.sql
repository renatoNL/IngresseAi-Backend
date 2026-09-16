INSERT INTO eventos (titulo, descricao, tipo_evento, data_evento, vendedor_id)
SELECT 'Evento demonstrativo ' || numero,
       'Descricao do evento demonstrativo ' || numero,
       CASE MOD(numero, 4) WHEN 0 THEN 'SHOW' WHEN 1 THEN 'TEATRO' WHEN 2 THEN 'ESPORTE' ELSE 'FESTIVAL' END,
       TIMESTAMP '2026-10-01 20:00:00' + numero * INTERVAL '1 day',
       1
FROM generate_series(1, 50) AS numeros(numero)
ON CONFLICT DO NOTHING;

INSERT INTO ingressos (evento_id, quantidade, valor)
SELECT e.id, 100, 49.90 + MOD(e.id, 5) * 10
FROM eventos e
WHERE e.titulo LIKE 'Evento demonstrativo %'
ON CONFLICT DO NOTHING;
