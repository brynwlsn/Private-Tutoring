-- 1. (OPSIONAL) Pastikan ada Admin dengan ID 1 dan Guru dengan ID 1, 2, 3
-- Abaikan 4 baris ini kalau Admin ID 1 dan Guru ID 1, 2, 3 sudah ada di database kamu
INSERT INTO Admin (id_admin, nama, email, no_hp, password) VALUES (1, 'Admin', '-', '-', '-');
INSERT INTO Guru (id_guru, nama, email, no_hp, password) VALUES (1, 'Guru 1', '-', '-', '-');
INSERT INTO Guru (id_guru, nama, email, no_hp, password) VALUES (2, 'Guru 2', '-', '-', '-');
INSERT INTO Guru (id_guru, nama, email, no_hp, password) VALUES (3, 'Guru 3', '-', '-', '-');

-- 2. Masukkan Jadwal Bohongan agar sama dengan React
INSERT INTO Jadwal_Kesediaan_Guru (id_jadwal, id_guru, hari, jam_mulai, jam_selesai, status, id_admin)
VALUES 
(101, 1, 'Monday', '14:00:00', '15:00:00', 'tersedia', 1),
(102, 1, 'Monday', '15:00:00', '16:00:00', 'tersedia', 1),
(103, 1, 'Wednesday', '13:00:00', '14:00:00', 'tersedia', 1),
(104, 2, 'Tuesday', '10:00:00', '11:00:00', 'tersedia', 1),
(105, 3, 'Thursday', '08:00:00', '09:00:00', 'tersedia', 1);

select *
from guru