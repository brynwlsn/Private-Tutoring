-- =========================================
-- DROP TABLE IF EXIST
-- =========================================
DROP TABLE if exists Les;
DROP TABLE if exists Siswa ;
DROP TABLE if exists Guru
DROP TABLE if exists Jadwal_Kesediaan_Guru
DROP TABLE if exists Keahlian_Guru
DROP TABLE if exists Detail_Daftar_Les
DROP TABLE if exists Jenjang
DROP TABLE if exists Mata_pelajaran
DROP TABLE if exists Admin



-- =========================================
-- 1. Tabel Admin
-- =========================================
CREATE TABLE Admin (
    id_admin INT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    password VARCHAR(100)
);

-- =========================================
-- 2. Tabel Mata_pelajaran
-- =========================================
CREATE TABLE Mata_pelajaran (
    id_mapel INT PRIMARY KEY,
    nama_mapel VARCHAR(100) NOT NULL
);

-- =========================================
-- 3. Tabel Jenjang
-- =========================================
CREATE TABLE Jenjang (
    id_jenjang INT PRIMARY KEY,
    nama_jenjang VARCHAR(50) NOT NULL
);

-- =========================================
-- 4. Tabel Guru
-- =========================================
CREATE TABLE Guru (
    id_guru INT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    no_hp VARCHAR(20),
    password VARCHAR(100),
    id_admin INT,
    FOREIGN KEY (id_admin) REFERENCES Admin(id_admin)
);

-- =========================================
-- 5. Tabel Siswa
-- =========================================
CREATE TABLE Siswa (
    id_siswa INT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    no_hp VARCHAR(20),
    tgl_lahir DATE,
    jenis_kelamin VARCHAR(15),
    password VARCHAR(100),
    id_jenjang INT,
    FOREIGN KEY (id_jenjang) REFERENCES Jenjang(id_jenjang)
);

-- =========================================
-- 6. Tabel Jadwal_Kesediaan_Guru
-- =========================================
CREATE TABLE Jadwal_Kesediaan_Guru (
    id_jadwal INT PRIMARY KEY,
    hari VARCHAR(20) NOT NULL,
    jam_mulai TIME NOT NULL,
    jam_selesai TIME NOT NULL,
    status VARCHAR(20) NOT NULL, -- Contoh isi: 'tersedia' atau 'terisi'
    id_guru INT,
    id_admin INT,
    FOREIGN KEY (id_guru) REFERENCES Guru(id_guru),
    FOREIGN KEY (id_admin) REFERENCES Admin(id_admin)
);

-- =========================================
-- 7. Tabel Keahlian_Guru
-- =========================================
CREATE TABLE Keahlian_Guru (
    id_keahlian INT PRIMARY KEY,
    id_guru INT,
    id_mapel INT,
    id_jenjang INT,
    FOREIGN KEY (id_guru) REFERENCES Guru(id_guru),
    FOREIGN KEY (id_mapel) REFERENCES Mata_pelajaran(id_mapel),
    FOREIGN KEY (id_jenjang) REFERENCES Jenjang(id_jenjang)
);

-- =========================================
-- 7. Tabel Les
-- =========================================
CREATE TABLE Les (
    id_les INT PRIMARY KEY,
    tgl_mulai DATE NOT NULL,
    tgl_selesai DATE NOT NULL,
    id_siswa INT,
    FOREIGN KEY (id_siswa) REFERENCES Siswa(id_siswa)
);

-- =========================================
-- 8. Tabel Detail_Daftar_Les
-- =========================================
CREATE TABLE Detail_Daftar_Les (
    id_detail INT PRIMARY KEY,
    id_les INT,
    id_jadwal INT,
    id_mapel INT,
    id_jenjang INT,
    FOREIGN KEY (id_les) REFERENCES Les(id_les),
    FOREIGN KEY (id_jadwal) REFERENCES Jadwal_Kesediaan_Guru(id_jadwal),
    FOREIGN KEY (id_mapel) REFERENCES Mata_pelajaran(id_mapel),
    FOREIGN KEY (id_jenjang) REFERENCES Jenjang(id_jenjang)
);