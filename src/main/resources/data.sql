-- ============================================================
-- FABRICANTES
-- ============================================================
INSERT INTO fabricante (nome) VALUES ('Intel');
INSERT INTO fabricante (nome) VALUES ('AMD');
INSERT INTO fabricante (nome) VALUES ('NVIDIA');
INSERT INTO fabricante (nome) VALUES ('Corsair');
INSERT INTO fabricante (nome) VALUES ('Samsung');
INSERT INTO fabricante (nome) VALUES ('Kingston');
INSERT INTO fabricante (nome) VALUES ('Asus');
INSERT INTO fabricante (nome) VALUES ('Cooler Master');

-- ============================================================
-- CATEGORIAS
-- ID 1: Processador
-- ID 2: Placa de Video
-- ID 3: Memoria RAM
-- ID 4: Armazenamento
-- ID 5: Placa-Mae
-- ID 6: Cooler
-- ID 7: Fonte
-- ============================================================
INSERT INTO categoria (nome) VALUES ('Processador');
INSERT INTO categoria (nome) VALUES ('Placa de Video');
INSERT INTO categoria (nome) VALUES ('Memoria RAM');
INSERT INTO categoria (nome) VALUES ('Armazenamento');
INSERT INTO categoria (nome) VALUES ('Placa-Mae');
INSERT INTO categoria (nome) VALUES ('Cooler');
INSERT INTO categoria (nome) VALUES ('Fonte');

-- ============================================================
-- COMPONENTES
-- Faixas: Muito Fraco | Fraco | Medio | Otimo | Excelente
-- tdp_watts = consumo em W (para Fonte = capacidade em W)
-- ============================================================

-- PROCESSADORES (categoria_id=1)
-- Muito Fraco: uso em escritório, navegação básica
INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('Celeron G6900', '2 cores, 2 threads, 3.4GHz, Cache 4MB', 280.00, 46, 1, 1);

-- Fraco: uso doméstico, jogos leves
INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('Core i3-12100', '4 cores, 8 threads, 3.3GHz-4.3GHz, Cache 12MB', 650.00, 89, 1, 1);

-- Medio: trabalho e jogos do cotidiano
INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('Ryzen 5 5600', '6 cores, 12 threads, 3.5GHz-4.4GHz, Cache 35MB', 800.00, 65, 1, 2);

-- Otimo: workstation e jogos de alto desempenho
INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('Core i7-13700K', '16 cores, 24 threads, 3.4GHz-5.4GHz, Cache 54MB', 1899.00, 125, 1, 1);

-- Excelente: workstation profissional, streaming, edicao 4K
INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('Ryzen 9 7950X', '16 cores, 32 threads, 4.5GHz-5.7GHz, Cache 80MB', 3499.00, 170, 1, 2);

-- PLACAS DE VIDEO (categoria_id=2)
-- Muito Fraca: multimídia básica, sem jogos
INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('GT 1030', '2GB GDDR5, 384 CUDA cores', 350.00, 30, 2, 3);

-- Fraca: jogos em 1080p, low settings
INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('GTX 1650', '4GB GDDR6, 896 CUDA cores', 800.00, 75, 2, 3);

-- Media: jogos em 1080p alto e 1440p médio
INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('RTX 3060', '12GB GDDR6, 3584 CUDA cores, Ray Tracing', 1800.00, 170, 2, 3);

-- Otima: jogos em 1440p ultra, 4K médio
INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('RTX 4070', '12GB GDDR6X, 5888 CUDA cores, DLSS 3', 3200.00, 200, 2, 3);

-- Excelente: 4K ultra, produção profissional
INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('RTX 4090', '24GB GDDR6X, 16384 CUDA cores, DLSS 3', 8000.00, 450, 2, 3);

-- MEMORIAS RAM (categoria_id=3)
INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('8GB DDR4 2666MHz', '1x8GB, CL16, 1.2V', 120.00, 5, 3, 4);

INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('16GB DDR4 3200MHz', '2x8GB Dual Channel, CL16, 1.35V', 220.00, 10, 3, 4);

INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('32GB DDR5 5200MHz', '2x16GB Dual Channel, CL38, 1.1V', 450.00, 15, 3, 4);

INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('64GB DDR5 6000MHz', '2x32GB Dual Channel, CL30, 1.35V', 900.00, 20, 3, 6);

-- ARMAZENAMENTO (categoria_id=4)
INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('HDD 1TB 7200rpm', 'SATA III, cache 64MB, velocidade 180MB/s', 200.00, 8, 4, 5);

INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('SSD 500GB SATA', 'SATA III, leitura 560MB/s, gravacao 530MB/s', 180.00, 3, 4, 5);

INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('NVMe SSD 1TB', 'PCIe 4.0, leitura 7000MB/s, gravacao 6500MB/s', 350.00, 7, 4, 5);

INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('NVMe SSD 2TB', 'PCIe 4.0, leitura 7300MB/s, gravacao 6900MB/s', 700.00, 8, 4, 5);

-- PLACAS-MAE (categoria_id=5)
INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('H610M-K', 'LGA1700, DDR4, 1x PCIe x16, 2x SATA, Micro-ATX', 400.00, 15, 5, 7);

INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('B660M TUF', 'LGA1700, DDR5, 1x PCIe 4.0 x16, M.2 NVMe, Micro-ATX', 550.00, 20, 5, 7);

INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('Z790 ROG STRIX', 'LGA1700, DDR5, 3x PCIe 5.0, 4x M.2 NVMe, ATX, Wi-Fi 6E', 1200.00, 25, 5, 7);

INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('X670E ROG Crosshair', 'AM5, DDR5, 3x PCIe 5.0, 5x M.2 NVMe, ATX, Wi-Fi 6E', 1400.00, 25, 5, 7);

-- COOLERS (categoria_id=6)
INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('Hyper 212 Black', 'Air cooler tower, 1x 120mm fan, TDP 150W', 150.00, 15, 6, 8);

INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('MasterLiquid 240', 'AIO liquido 240mm, 2x 120mm fans, TDP 250W', 400.00, 25, 6, 8);

INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('MasterLiquid 360', 'AIO liquido 360mm, 3x 120mm fans, TDP 350W', 700.00, 30, 6, 8);

-- FONTES DE ALIMENTACAO (categoria_id=7)
-- Para fontes, tdp_watts = CAPACIDADE em Watts
INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('CX400 Bronze', '400W, 80 Plus Bronze, semi-modular', 200.00, 400, 7, 4);

INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('CV550 Bronze', '550W, 80 Plus Bronze, nao-modular', 280.00, 550, 7, 4);

INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('RM650 Gold', '650W, 80 Plus Gold, totalmente modular', 450.00, 650, 7, 4);

INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('RM750 Gold', '750W, 80 Plus Gold, totalmente modular', 550.00, 750, 7, 4);

INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('HX850 Platinum', '850W, 80 Plus Platinum, totalmente modular', 800.00, 850, 7, 4);

INSERT INTO componente (nome, especificacao, preco, tdp_watts, categoria_id, fabricante_id) VALUES
('HX1000 Platinum', '1000W, 80 Plus Platinum, totalmente modular', 1100.00, 1000, 7, 4);
