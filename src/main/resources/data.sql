-- Testdata voor AutoMaat.
-- Draait automatisch na het aanmaken van het schema
-- (spring.sql.init.mode=always + spring.jpa.defer-datasource-initialization=true).
-- Volgorde volgt de foreign keys: customers -> cars -> parts -> inspections
-- -> repairs -> repair_parts -> invoices.

-- Klanten
INSERT INTO customers (id, name, phone_number, email) VALUES
  (1, 'Pieter Bakker',    '0624817392', 'p.bakker@gmail.com'),
  (2, 'Marloes van Dijk', '0653129847', 'marloes.vandijk@hotmail.com'),
  (3, 'Youssef Ouali',    '0618446205', 'y.ouali@outlook.com'),
  (4, 'Ingrid Hofman',    '0641209386', 'ingrid.hofman@ziggo.nl');

-- Auto's (customer_id verwijst naar customers)
INSERT INTO cars (id, license_plate, brand, model, build_year, registration_document_path, customer_id) VALUES
  (1, '84-RFL-8', 'Volkswagen', 'Golf',   2017, NULL, 1),
  (2, 'GVJ-12-K', 'Opel',       'Corsa',  2020, NULL, 1),
  (3, '76-ZXN-3', 'Renault',    'Clio',   2015, NULL, 2),
  (4, 'PDR-45-B', 'Peugeot',    '208',    2019, NULL, 3),
  (5, '1-KZD-38', 'Toyota',     'Yaris',  2021, NULL, 4);

-- Onderdelen
INSERT INTO parts (id, name, price, stock_quantity) VALUES
  (1, 'Remblokken voor',      59.95, 18),
  (2, 'Remblokken achter',    54.95, 16),
  (3, 'Remschijven set',      89.95, 10),
  (4, 'Koplampunit links',    74.50, 6),
  (5, 'Oliefilter',           14.50, 40),
  (6, 'Ruitenwisserset',      24.90, 35);

-- Keuringen (APK); status = InspectionStatus
INSERT INTO inspections (id, car_id, inspection_date, status, issues) VALUES
  (1, 1, DATE '2026-03-10', 'APPROVED', 'APK goedgekeurd, geen gebreken'),
  (2, 3, DATE '2026-04-02', 'REJECTED', 'Remschijven onder minimumdikte, linkerkoplamp defect');

-- Reparaties; status = RepairStatus
INSERT INTO repairs (id, car_id, repair_date, status, notes) VALUES
  (1, 1, DATE '2026-03-15', 'COMPLETED', 'Remblokken voor en achter vervangen'),
  (2, 3, DATE '2026-04-08', 'OPEN',      'Reparatie na afgekeurde APK: remschijven en koplamp vervangen');

-- Koppeltabel reparatie <-> onderdeel (M:N)
INSERT INTO repair_parts (repair_id, part_id) VALUES
  (1, 1),
  (1, 2),
  (2, 3),
  (2, 4);

-- Bonnen (repair_id verwijst naar repairs, 1:1); payment_status = PaymentStatus.
-- Alleen voor de afgeronde reparatie 1: 59.95 + 54.95 = 114.90 netto,
-- 21% btw = 24.13, totaal = 139.03. Reparatie 2 staat nog open, dus geen bon.
INSERT INTO invoices (id, repair_id, total_amount, vat_amount, payment_status) VALUES
  (1, 1, 139.03, 24.13, 'PAID');

-- Identity-sequences bijwerken zodat nieuwe records via de API niet botsen
-- met de seed-id's (PostgreSQL).
ALTER TABLE customers   ALTER COLUMN id RESTART WITH 5;
ALTER TABLE cars        ALTER COLUMN id RESTART WITH 6;
ALTER TABLE parts       ALTER COLUMN id RESTART WITH 7;
ALTER TABLE inspections ALTER COLUMN id RESTART WITH 3;
ALTER TABLE repairs     ALTER COLUMN id RESTART WITH 3;
ALTER TABLE invoices    ALTER COLUMN id RESTART WITH 2;
