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

INSERT INTO Jenjang (id_jenjang, nama_jenjang) VALUES 
(1, 'SD'),
(2, 'SMP'),
(3, 'SMA');

-- Memasukkan 5 data siswa dummy dengan variasi jenjang pendidikan
INSERT INTO Siswa (id_siswa, id_jenjang, nama, tgl_lahir, jenis_kelamin, no_hp, email, password) 
VALUES 
(10001, 1, 'Budi Santoso', '2015-05-12', 'L', '081234567890', 'budi@email.com', 'passwordBudi'),
(10002, 1, 'Siti Aminah', '2016-08-23', 'P', '081234567891', 'siti@email.com', 'passwordSiti'),
(10003, 2, 'Andi Wijaya', '2012-02-10', 'L', '081234567892', 'andi@email.com', 'passwordAndi'),
(10004, 2, 'Citra Lestari', '2011-11-30', 'P', '081234567893', 'citra@email.com', 'passwordCitra'),
(10005, 3, 'Rian Hidayat', '2009-04-05', 'L', '081234567894', 'rian@email.com', 'passwordRian');