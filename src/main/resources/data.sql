-- Tipi di terreno
INSERT INTO tipo_terreno (nome, descrizione) VALUES ('ARGILLOSO', 'Terreno pesante, trattiene molta acqua');
INSERT INTO tipo_terreno (nome, descrizione) VALUES ('SABBIOSO', 'Terreno leggero, drena velocemente');
INSERT INTO tipo_terreno (nome, descrizione) VALUES ('CALCAREO', 'Ricco di calcare, tendenzialmente basico');
INSERT INTO tipo_terreno (nome, descrizione) VALUES ('MEDIO_IMPASTO', 'Equilibrato tra sabbia, limo e argilla');

-- Cultivar
INSERT INTO cultivar (id, nome, specie, ph_min, ph_max, fabbisogno_idrico, tipo_terreno_id)
VALUES (NEXT VALUE FOR SEQ_CULTIVAR, 'Frantoio', 'Olivo', 6.5, 8.5, 'BASSO', (SELECT id FROM tipo_terreno WHERE nome = 'CALCAREO'));
INSERT INTO cultivar (id, nome, specie, ph_min, ph_max, fabbisogno_idrico, tipo_terreno_id)
VALUES (NEXT VALUE FOR SEQ_CULTIVAR, 'Leccino', 'Olivo', 6.0, 8.0, 'BASSO', (SELECT id FROM tipo_terreno WHERE nome = 'MEDIO_IMPASTO'));
INSERT INTO cultivar (id, nome, specie, ph_min, ph_max, fabbisogno_idrico, tipo_terreno_id)
VALUES (NEXT VALUE FOR SEQ_CULTIVAR, 'Sangiovese', 'Vite', 6.5, 8.0, 'MEDIO', (SELECT id FROM tipo_terreno WHERE nome = 'ARGILLOSO'));
INSERT INTO cultivar (id, nome, specie, ph_min, ph_max, fabbisogno_idrico, tipo_terreno_id)
VALUES (NEXT VALUE FOR SEQ_CULTIVAR, 'Vermentino', 'Vite', 5.5, 7.5, 'BASSO', (SELECT id FROM tipo_terreno WHERE nome = 'SABBIOSO'));
INSERT INTO cultivar (id, nome, specie, ph_min, ph_max, fabbisogno_idrico, tipo_terreno_id)
VALUES (NEXT VALUE FOR SEQ_CULTIVAR, 'Trebbiano', 'Vite', 6.0, 7.5, 'MEDIO', (SELECT id FROM tipo_terreno WHERE nome = 'MEDIO_IMPASTO'));
INSERT INTO cultivar (id, nome, specie, ph_min, ph_max, fabbisogno_idrico, tipo_terreno_id)
VALUES (NEXT VALUE FOR SEQ_CULTIVAR, 'San Marzano', 'Pomodoro', 6.0, 7.0, 'ALTO', (SELECT id FROM tipo_terreno WHERE nome = 'MEDIO_IMPASTO'));
INSERT INTO cultivar (id, nome, specie, ph_min, ph_max, fabbisogno_idrico, tipo_terreno_id)
VALUES (NEXT VALUE FOR SEQ_CULTIVAR, 'Senatore Cappelli', 'Grano duro', 6.0, 8.0, 'BASSO', (SELECT id FROM tipo_terreno WHERE nome = 'ARGILLOSO'));
INSERT INTO cultivar (id, nome, specie, ph_min, ph_max, fabbisogno_idrico, tipo_terreno_id)
VALUES (NEXT VALUE FOR SEQ_CULTIVAR, 'Carota Nantese', 'Carota', 6.0, 6.8, 'MEDIO', (SELECT id FROM tipo_terreno WHERE nome = 'SABBIOSO'));