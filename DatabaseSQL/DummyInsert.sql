-- =========================================
-- INSERT TABEL MASTER & MAPPING (Min. 5 Data)
-- =========================================

-- 1. Jenjang (Ditambah 2 agar minimal 5)
INSERT INTO Jenjang (id_jenjang, nama_jenjang) VALUES 
(1, 'SD'), 
(2, 'SMP'), 
(3, 'SMA'),
(4, 'TK'),
(5, 'UMUM');

-- 2. Mata Pelajaran (Ditambah 2 agar lebih variatif)
INSERT INTO Mata_pelajaran (id_mapel, nama_mapel) VALUES 
(1, 'Matematika'), 
(2, 'Bahasa Indonesia'), 
(3, 'Fisika'), 
(4, 'Kimia'), 
(5, 'Bahasa Inggris'),
(6, 'Biologi'),
(7, 'Ekonomi');

-- 3. Admin (Sudah 5 data)
INSERT INTO Admin (id_admin, nama, email, no_hp, password) VALUES 
(1, 'Reza Firmansyah', 'reza@educapy.com', '081300000001', 'Reza!2026'),
(2, 'Sarah Wijaya', 'sarah@educapy.com', '081300000002', 'Sarah#99'),
(3, 'Dwi Cahyono', 'dwi@educapy.com', '081300000003', 'DwiCahyono_88'),
(4, 'Lestari Putri', 'lestari@educapy.com', '081300000004', 'Lestari$2026'),
(5, 'Bambang Sudibyo', 'bambang@educapy.com', '081300000005', 'Bambang_Pass!');

-- 4. Guru (Ditambah 5 agar menjadi 10 data)
INSERT INTO Guru (id_guru, nama, email, no_hp, password, id_admin) VALUES 
(1, 'Dr. Ayu Rahmawati', 'ayu@tutor.id', '081234567801', 'Ayu_Math2026', 1),
(2, 'Budi Santoso, S.Pd', 'budi@tutor.id', '081234567802', 'BudiSantoso_77', 1),
(3, 'Citra Dewi, M.Si', 'citra@tutor.id', '081234567803', 'CitraDewi#Msi', 2),
(4, 'Dian Pratama, S.T', 'dian@tutor.id', '081234567804', 'DianP_ST2026', 2),
(5, 'Eko Prasetyo, M.T', 'eko@tutor.id', '081234567805', 'EkoPrasetyo_88', 3),
(6, 'Fahri Hamzah, S.Kom', 'fahri@tutor.id', '081234567806', 'Fahri_Kom99', 3),
(7, 'Gita Gutawa, S.S', 'gita@tutor.id', '081234567807', 'Gita_SS2026', 4),
(8, 'Hadi Mulyadi, S.Pd', 'hadi@tutor.id', '081234567808', 'Hadi_Pd88', 4),
(9, 'Intan Permata, M.Pd', 'intan@tutor.id', '081234567809', 'Intan_Mpd99', 5),
(10, 'Joko Anwar, S.Si', 'joko@tutor.id', '081234567810', 'Joko_Si2026', 5);

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
(101, 'Monday', '08:00:00', '09:00:00', 1, 1),
(102, 'Tuesday', '10:00:00', '11:00:00', 2, 1),
(103, 'Wednesday', '13:00:00', '14:00:00', 3, 2),
(104, 'Thursday', '15:00:00', '16:00:00', 4, 2),
(105, 'Friday', '09:00:00', '10:00:00', 5, 3),
(106, 'Monday', '16:00:00', '17:00:00', 6, 3),
(107, 'Tuesday', '14:00:00', '15:00:00', 7, 4),
(108, 'Wednesday', '09:00:00', '10:00:00', 8, 4),
(109, 'Thursday', '13:00:00', '14:00:00', 9, 5),
(110, 'Friday', '15:00:00', '16:00:00', 10, 5);


-- =========================================
-- INSERT TABEL TRANSAKSI (Min. 30 Data)
-- =========================================

-- 8. Transaksi Les (30 Data)
INSERT INTO Les (id_les, tgl_mulai, tgl_selesai, id_siswa) VALUES 
(1, '2026-06-01', '2026-07-01', 1),
(2, '2026-06-02', '2026-07-02', 2),
(3, '2026-06-03', '2026-07-03', 3),
(4, '2026-06-04', '2026-07-04', 4),
(5, '2026-06-05', '2026-07-05', 5),
(6, '2026-06-06', '2026-07-06', 6),
(7, '2026-06-07', '2026-07-07', 7),
(8, '2026-06-08', '2026-07-08', 8),
(9, '2026-06-09', '2026-07-09', 9),
(10, '2026-06-10', '2026-07-10', 10),
(11, '2026-06-11', '2026-07-11', 1),
(12, '2026-06-12', '2026-07-12', 2),
(13, '2026-06-13', '2026-07-13', 3),
(14, '2026-06-14', '2026-07-14', 4),
(15, '2026-06-15', '2026-07-15', 5),
(16, '2026-06-16', '2026-07-16', 6),
(17, '2026-06-17', '2026-07-17', 7),
(18, '2026-06-18', '2026-07-18', 8),
(19, '2026-06-19', '2026-07-19', 9),
(20, '2026-06-20', '2026-07-20', 10),
(21, '2026-06-21', '2026-07-21', 1),
(22, '2026-06-22', '2026-07-22', 2),
(23, '2026-06-23', '2026-07-23', 3),
(24, '2026-06-24', '2026-07-24', 4),
(25, '2026-06-25', '2026-07-25', 5),
(26, '2026-06-26', '2026-07-26', 6),
(27, '2026-06-27', '2026-07-27', 7),
(28, '2026-06-28', '2026-07-28', 8),
(29, '2026-06-29', '2026-07-29', 9),
(30, '2026-06-30', '2026-07-30', 10);

-- 9. Transaksi Detail Daftar Les (30 Data, direlasikan ke id_les di atas)
INSERT INTO Detail_Daftar_Les (id_detail, id_les, id_jadwal, id_mapel, id_jenjang) VALUES 
(1, 1, 101, 1, 1),
(2, 2, 102, 3, 3),
(3, 3, 103, 2, 2),
(4, 4, 104, 4, 3),
(5, 5, 105, 5, 1),
(6, 6, 106, 1, 3),
(7, 7, 107, 5, 3),
(8, 8, 108, 6, 2),
(9, 9, 109, 2, 1),
(10, 10, 110, 3, 2),
(11, 11, 101, 1, 1),
(12, 12, 102, 3, 3),
(13, 13, 103, 2, 2),
(14, 14, 104, 4, 3),
(15, 15, 105, 5, 1),
(16, 16, 106, 1, 3),
(17, 17, 107, 5, 3),
(18, 18, 108, 6, 2),
(19, 19, 109, 2, 1),
(20, 20, 110, 3, 2),
(21, 21, 101, 1, 1),
(22, 22, 102, 3, 3),
(23, 23, 103, 2, 2),
(24, 24, 104, 4, 3),
(25, 25, 105, 5, 1),
(26, 26, 106, 1, 3),
(27, 27, 107, 5, 3),
(28, 28, 108, 6, 2),
(29, 29, 109, 2, 1),
(30, 30, 110, 3, 2);