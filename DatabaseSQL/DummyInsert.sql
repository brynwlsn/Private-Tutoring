-- =========================================
-- INSERT TABEL MASTER & MAPPING (Min. 5 Data)
-- =========================================

-- 1. Jenjang (Ditambah 2 agar minimal 5)
INSERT INTO Jenjang (id_jenjang, nama_jenjang) VALUES 
(1, 'Elementary School'), 
(2, 'Junior High School'), 
(3, 'Senior High School'),
(4, 'Kindergarten'),
(5, 'General');

-- 2. Mata Pelajaran (Ditambah 2 agar lebih variatif)
INSERT INTO Mata_pelajaran (id_mapel, nama_mapel) VALUES 
(1, 'Mathematics'), 
(2, 'Indonesian'), 
(3, 'Physics'), 
(4, 'Chemistry'), 
(5, 'English'),
(6, 'Biology'),
(7, 'Economy');

-- 3. Admin (Sudah 5 data)
INSERT INTO Admin (id_admin, nama, email, no_hp, password) VALUES 
(1, 'Dr. Ayu Rahmawati', 'ayu@admin.id', '081300000001', 'admin123'),
(2, 'Citra Dewi, M.Si', 'citra@admin.id', '081300000002', 'admin123'),
(3, 'Eko Prasetyo, M.T', 'eko@admin.id', '081300000003', 'admin123'),
(4, 'Gita Gutawa, S.S', 'gita@admin.id', '081300000004', 'admin123'),
(5, 'Bambang Sudibyo', 'bambang@educapy.com', '081300000005', 'admin123');

-- 4. Guru (Ditambah 5 agar menjadi 10 data)
INSERT INTO Guru (id_guru, nama, email, no_hp, password, id_admin) VALUES 
(1, 'Dr. Ayu Rahmawati', 'ayu@tutor.id', '081234567801', 'Ayu_Math2026', 1),
(2, 'Budi Santoso, S.Pd', 'budi@tutor.id', '081234567802', 'BudiSantoso_77', NULL),
(3, 'Citra Dewi, M.Si', 'citra@tutor.id', '081234567803', 'CitraDewi#Msi', 2),
(4, 'Dian Pratama, S.T', 'dian@tutor.id', '081234567804', 'DianP_ST2026', NULL),
(5, 'Eko Prasetyo, M.T', 'eko@tutor.id', '081234567805', 'EkoPrasetyo_88', 3),
(6, 'Fahri Hamzah, S.Kom', 'fahri@tutor.id', '081234567806', 'Fahri_Kom99', NULL),
(7, 'Gita Gutawa, S.S', 'gita@tutor.id', '081234567807', 'Gita_SS2026', 4),
(8, 'Hadi Mulyadi, S.Pd', 'hadi@tutor.id', '081234567808', 'Hadi_Pd88', NULL),
(9, 'Intan Permata, M.Pd', 'intan@tutor.id', '081234567809', 'Intan_Mpd99', NULL),
(10, 'Joko Anwar, S.Si', 'joko@tutor.id', '081234567810', 'Joko_Si2026', NULL);

-- 5. Siswa (Ditambah 5 agar menjadi 10 data)
INSERT INTO Siswa (id_siswa, id_jenjang, nama, email, no_hp, tgl_lahir, jenis_kelamin, password) VALUES 
(1, 3, 'Rani Kusuma', 'rani@mail.com', '082111111101', '2007-04-12', 'P', 'RaniKusuma2007'),
(2, 2, 'Farhan Adli', 'farhan@mail.com', '082111111102', '2009-08-22', 'L', 'FarhanAdli99'),
(3, 3, 'Sinta Maharani', 'sinta@mail.com', '082111111103', '2006-11-03', 'P', 'SintaM_2006'),
(4, 1, 'Kevin Wijaya', 'kevin@mail.com', '082111111104', '2013-01-17', 'L', 'KevinW_17'),
(5, 2, 'Nadia Putri', 'nadia@mail.com', '082111111105', '2010-06-30', 'P', 'NadiaPutri2010'),
(6, 1, 'Bagas Putra', 'bagas@mail.com', '082111111106', '2014-02-14', 'L', 'BagasPutra14'),
(7, 3, 'Clara Sinta', 'clara@mail.com', '082111111107', '2006-09-09', 'P', 'ClaraS_06'),
(8, 2, 'Deni Sumargo', 'deni@mail.com', '082111111108', '2009-12-01', 'L', 'DeniS_09'),
(9, 3, 'Erlangga', 'erlangga@mail.com', '082111111109', '2007-05-20', 'L', 'Erlangga_07'),
(10, 1, 'Fina Melinda', 'fina@mail.com', '082111111110', '2013-08-08', 'P', 'FinaM_13');

-- 6. Keahlian Guru (Ditambah 5)
INSERT INTO Keahlian_Guru (id_keahlian, id_guru, id_mapel, id_jenjang) VALUES 
(1, 1, 1, 1), 
(2, 2, 3, 3), 
(3, 3, 2, 2), 
(4, 4, 4, 3), 
(5, 5, 5, 1),
(6, 6, 1, 3), 
(7, 7, 5, 3), 
(8, 8, 6, 2), 
(9, 9, 2, 1), 
(10, 10, 3, 2);

-- 7. Jadwal Kesediaan Guru (Ditambah 5)
INSERT INTO Jadwal_Kesediaan_Guru (id_jadwal, hari, jam_mulai, jam_selesai, id_guru, id_admin) VALUES 
-- Guru 1 (Dr. Ayu)
(101, 'Monday', '08:00:00', '09:00:00', 1, 1),
(102, 'Wednesday', '10:00:00', '11:00:00', 1, 1),
(103, 'Friday', '14:00:00', '15:00:00', 1, 1),
-- Guru 2 (Budi Santoso)
(104, 'Tuesday', '09:00:00', '10:00:00', 2, 1),
(105, 'Thursday', '13:00:00', '14:00:00', 2, 1),
(106, 'Saturday', '10:00:00', '11:00:00', 2, 1),
-- Guru 3 (Citra Dewi)
(107, 'Monday', '13:00:00', '14:00:00', 3, 2),
(108, 'Wednesday', '15:00:00', '16:00:00', 3, 2),
(109, 'Friday', '09:00:00', '10:00:00', 3, 2),
-- Guru 4 (Dian Pratama)
(110, 'Tuesday', '14:00:00', '15:00:00', 4, 2),
(111, 'Thursday', '08:00:00', '09:00:00', 4, 2),
(112, 'Saturday', '13:00:00', '14:00:00', 4, 2),
-- Guru 5 (Eko Prasetyo)
(113, 'Monday', '10:00:00', '11:00:00', 5, 3),
(114, 'Wednesday', '14:00:00', '15:00:00', 5, 3),
(115, 'Friday', '16:00:00', '17:00:00', 5, 3),
-- Guru 6 (Fahri Hamzah)
(116, 'Tuesday', '10:00:00', '11:00:00', 6, 3),
(117, 'Thursday', '15:00:00', '16:00:00', 6, 3),
(118, 'Saturday', '15:00:00', '16:00:00', 6, 3),
-- Guru 7 (Gita Gutawa)
(119, 'Monday', '15:00:00', '16:00:00', 7, 4),
(120, 'Wednesday', '08:00:00', '09:00:00', 7, 4),
(121, 'Friday', '13:00:00', '14:00:00', 7, 4),
-- Guru 8 (Hadi Mulyadi)
(122, 'Tuesday', '16:00:00', '17:00:00', 8, 4),
(123, 'Thursday', '10:00:00', '11:00:00', 8, 4),
(124, 'Saturday', '08:00:00', '09:00:00', 8, 4),
-- Guru 9 (Intan Permata)
(125, 'Monday', '16:00:00', '17:00:00', 9, 5),
(126, 'Wednesday', '16:00:00', '17:00:00', 9, 5),
(127, 'Friday', '10:00:00', '11:00:00', 9, 5),
-- Guru 10 (Joko Anwar)
(128, 'Tuesday', '08:00:00', '09:00:00', 10, 5),
(129, 'Thursday', '16:00:00', '17:00:00', 10, 5),
(130, 'Saturday', '16:00:00', '17:00:00', 10, 5),

(201, 'Tuesday', '08:00:00', '09:00:00', 5, 3),   -- Jadwal baru Eko Prasetyo
(202, 'Thursday', '09:00:00', '10:00:00', 5, 3),  -- Jadwal baru Eko Prasetyo
(203, 'Sunday', '10:00:00', '11:00:00', 1, 1),    -- Jadwal baru Dr. Ayu Rahmawati
(204, 'Sunday', '13:00:00', '14:00:00', 2, 1);    -- Jadwal baru Budi Santoso


-- =========================================
-- INSERT TABEL TRANSAKSI (Min. 30 Data)
-- =========================================

-- 8. Transaksi Les (30 Data)
INSERT INTO Les (id_les, tgl_mulai, tgl_selesai, id_siswa) VALUES 
(1, '2026-06-01', '2026-07-01', 1), (2, '2026-06-01', '2026-07-01', 1), (3, '2026-06-01', '2026-07-01', 1),  -- Rani Kusuma
(4, '2026-06-02', '2026-07-02', 2), (5, '2026-06-02', '2026-07-02', 2), (6, '2026-06-02', '2026-07-02', 2),  -- Farhan Adli
(7, '2026-06-03', '2026-07-03', 3), (8, '2026-06-03', '2026-07-03', 3), (9, '2026-06-03', '2026-07-03', 3),  -- Sinta Maharani
(10, '2026-06-04', '2026-07-04', 4), (11, '2026-06-04', '2026-07-04', 4), (12, '2026-06-04', '2026-07-04', 4), -- Kevin Wijaya
(13, '2026-06-05', '2026-07-05', 5), (14, '2026-06-05', '2026-07-05', 5), (15, '2026-06-05', '2026-07-05', 5), -- Nadia Putri
(16, '2026-06-06', '2026-07-06', 6), (17, '2026-06-06', '2026-07-06', 6), (18, '2026-06-06', '2026-07-06', 6), -- Bagas Putra
(19, '2026-06-07', '2026-07-07', 7), (20, '2026-06-07', '2026-07-07', 7), (21, '2026-06-07', '2026-07-07', 7), -- Clara Sinta
(22, '2026-06-08', '2026-07-08', 8), (23, '2026-06-08', '2026-07-08', 8), (24, '2026-06-08', '2026-07-08', 8), -- Deni Sumargo
(25, '2026-06-09', '2026-07-09', 9), (26, '2026-06-09', '2026-07-09', 9), (27, '2026-06-09', '2026-07-09', 9), -- Erlangga
(28, '2026-06-10', '2026-07-10', 10), (29, '2026-06-10', '2026-07-10', 10), (30, '2026-06-10', '2026-07-10', 10); -- Fina Melinda

-- 9. Transaksi Detail Daftar Les (30 Data, direlasikan ke id_les di atas)
INSERT INTO Detail_Daftar_Les (id_detail, id_les, id_jadwal, id_mapel, id_jenjang) VALUES 
-- 3 Jadwal untuk Rani (id_les 1, 2, 3 ke id_jadwal 101, 102, 103)
(1, 1, 101, 1, 3), (2, 2, 102, 1, 3), (3, 3, 103, 1, 3),
-- 3 Jadwal untuk Farhan (id_les 4, 5, 6 ke id_jadwal 104, 105, 106)
(4, 4, 104, 3, 3), (5, 5, 105, 3, 3), (6, 6, 106, 3, 3),
-- 3 Jadwal untuk Sinta (107, 108, 109)
(7, 7, 107, 2, 2), (8, 8, 108, 2, 2), (9, 9, 109, 2, 2),
-- 3 Jadwal untuk Kevin (110, 111, 112)
(10, 10, 110, 4, 3), (11, 11, 111, 4, 3), (12, 12, 112, 4, 3),
-- 3 Jadwal untuk Nadia (113, 114, 115)
(13, 13, 113, 5, 1), (14, 14, 114, 5, 1), (15, 15, 115, 5, 1),
-- 3 Jadwal untuk Bagas (116, 117, 118)
(16, 16, 116, 1, 3), (17, 17, 117, 1, 3), (18, 18, 118, 1, 3),
-- 3 Jadwal untuk Clara (119, 120, 121)
(19, 19, 119, 5, 3), (20, 20, 120, 5, 3), (21, 21, 121, 5, 3),
-- 3 Jadwal untuk Deni (122, 123, 124)
(22, 22, 122, 6, 2), (23, 23, 123, 6, 2), (24, 24, 124, 6, 2),
-- 3 Jadwal untuk Erlangga (125, 126, 127)
(25, 25, 125, 2, 1), (26, 26, 126, 2, 1), (27, 27, 127, 2, 1),
-- 3 Jadwal untuk Fina (128, 129, 130)
(28, 28, 128, 3, 2), (29, 29, 129, 3, 2), (30, 30, 130, 3, 2);