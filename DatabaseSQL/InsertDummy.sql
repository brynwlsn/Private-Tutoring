INSERT INTO Jenjang (id_jenjang, nama_jenjang) VALUES 
(1, 'SD'), (2, 'SMP'), (3, 'SMA')

INSERT INTO Mata_pelajaran (id_mapel, nama_mapel) VALUES 
(1, 'Matematika'), (2, 'Bahasa Indonesia'), (3, 'Fisika'), (4, 'Kimia'), (5, 'Bahasa Inggris');

INSERT INTO Admin (id_admin, nama, email, no_hp, password) VALUES 
(1, 'Reza Firmansyah', 'reza@educapy.com', '081300000001', 'Reza!2026'),
(2, 'Sarah Wijaya', 'sarah@educapy.com', '081300000002', 'Sarah#99'),
(3, 'Dwi Cahyono', 'dwi@educapy.com', '081300000003', 'DwiCahyono_88'),
(4, 'Lestari Putri', 'lestari@educapy.com', '081300000004', 'Lestari$2026'),
(5, 'Bambang Sudibyo', 'bambang@educapy.com', '081300000005', 'Bambang_Pass!');

INSERT INTO Guru (id_guru, nama, email, no_hp, password, id_admin) VALUES 
(1, 'Dr. Ayu Rahmawati', 'ayu@tutor.id', '081234567801', 'Ayu_Math2026', 1),
(2, 'Budi Santoso, S.Pd', 'budi@tutor.id', '081234567802', 'BudiSantoso_77', 1),
(3, 'Citra Dewi, M.Si', 'citra@tutor.id', '081234567803', 'CitraDewi#Msi', 2),
(4, 'Dian Pratama, S.T', 'dian@tutor.id', '081234567804', 'DianP_ST2026', 2),
(5, 'Eko Prasetyo, M.T', 'eko@tutor.id', '081234567805', 'EkoPrasetyo_88', 3);

INSERT INTO Siswa (id_siswa, id_jenjang, nama, email, no_hp, tgl_lahir, jenis_kelamin, password) VALUES 
(1, 3, 'Rani Kusuma', 'rani@mail.com', '082111111101', '2007-04-12', 'P', 'RaniKusuma2007'),
(2, 2, 'Farhan Adli', 'farhan@mail.com', '082111111102', '2009-08-22', 'L', 'FarhanAdli99'),
(3, 3, 'Sinta Maharani', 'sinta@mail.com', '082111111103', '2006-11-03', 'P', 'SintaM_2006'),
(4, 1, 'Kevin Wijaya', 'kevin@mail.com', '082111111104', '2013-01-17', 'L', 'KevinW_17'),
(5, 2, 'Nadia Putri', 'nadia@mail.com', '082111111105', '2010-06-30', 'P', 'NadiaPutri2010');

-- Keahlian: Guru apa bisa mengajar mapel apa
INSERT INTO Keahlian_Guru (id_keahlian, id_guru, id_mapel, id_jenjang) VALUES 
(1, 1, 1, 1), (2, 2, 3, 3), (3, 3, 2, 2), (4, 4, 4, 3), (5, 5, 5, 1);

-- Jadwal Kesediaan:
INSERT INTO Jadwal_Kesediaan_Guru (id_jadwal, hari, jam_mulai, jam_selesai, id_guru, id_admin) VALUES 
(101, 'Monday', '08:00:00', '09:00:00', 1, 1),
(102, 'Tuesday', '10:00:00', '11:00:00', 2, 1),
(103, 'Wednesday', '13:00:00', '14:00:00', 3, 2),
(104, 'Thursday', '15:00:00', '16:00:00', 4, 2),
(105, 'Friday', '09:00:00', '10:00:00', 5, 3);


select * from Jadwal_Kesediaan_Guru