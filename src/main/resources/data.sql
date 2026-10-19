-- Tipi di terreno
INSERT INTO tipo_terreno (nome, descrizione) VALUES ('ARGILLOSO', 'Terreno pesante, trattiene molta acqua');
INSERT INTO tipo_terreno (nome, descrizione) VALUES ('SABBIOSO', 'Terreno leggero, drena velocemente');
INSERT INTO tipo_terreno (nome, descrizione) VALUES ('CALCAREO', 'Ricco di calcare, tendenzialmente basico');
INSERT INTO tipo_terreno (nome, descrizione) VALUES ('MEDIO_IMPASTO', 'Equilibrato tra sabbia, limo e argilla');

-- Cultivar
INSERT INTO cultivar (nome, specie, ph_min, ph_max, fabbisogno_idrico, tipo_terreno_id)
VALUES ('Frantoio', 'Olivo', 6.5, 8.5, 'BASSO', (SELECT id FROM tipo_terreno WHERE nome = 'CALCAREO'));
INSERT INTO cultivar (nome, specie, ph_min, ph_max, fabbisogno_idrico, tipo_terreno_id)
VALUES ('Leccino', 'Olivo', 6.0, 8.0, 'BASSO', (SELECT id FROM tipo_terreno WHERE nome = 'MEDIO_IMPASTO'));
INSERT INTO cultivar (nome, specie, ph_min, ph_max, fabbisogno_idrico, tipo_terreno_id)
VALUES ('Sangiovese', 'Vite', 6.5, 8.0, 'MEDIO', (SELECT id FROM tipo_terreno WHERE nome = 'ARGILLOSO'));
INSERT INTO cultivar (nome, specie, ph_min, ph_max, fabbisogno_idrico, tipo_terreno_id)
VALUES ('Vermentino', 'Vite', 5.5, 7.5, 'BASSO', (SELECT id FROM tipo_terreno WHERE nome = 'SABBIOSO'));
INSERT INTO cultivar (nome, specie, ph_min, ph_max, fabbisogno_idrico, tipo_terreno_id)
VALUES ('Trebbiano', 'Vite', 6.0, 7.5, 'MEDIO', (SELECT id FROM tipo_terreno WHERE nome = 'MEDIO_IMPASTO'));
INSERT INTO cultivar (nome, specie, ph_min, ph_max, fabbisogno_idrico, tipo_terreno_id)
VALUES ('San Marzano', 'Pomodoro', 6.0, 7.0, 'ALTO', (SELECT id FROM tipo_terreno WHERE nome = 'MEDIO_IMPASTO'));
INSERT INTO cultivar (nome, specie, ph_min, ph_max, fabbisogno_idrico, tipo_terreno_id)
VALUES ('Senatore Cappelli', 'Grano duro', 6.0, 8.0, 'BASSO', (SELECT id FROM tipo_terreno WHERE nome = 'ARGILLOSO'));
INSERT INTO cultivar (nome, specie, ph_min, ph_max, fabbisogno_idrico, tipo_terreno_id)
VALUES ('Carota Nantese', 'Carota', 6.0, 6.8, 'MEDIO', (SELECT id FROM tipo_terreno WHERE nome = 'SABBIOSO'));