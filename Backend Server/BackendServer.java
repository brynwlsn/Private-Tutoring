import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.stream.Collectors;

public class BackendServer {
    // "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;" +
    // "databaseName=MIBDLesPrivat;" +"encrypt=true;"
    // +"trustServerCertificate=true;";
    // String user = "sa";
    // String password = "passwordSQLAnda";

    // Connect Database
    private static final String URL = "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;" + "databaseName=LesPrivat;"
            + "encrypt=true;" + "trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASSWORD = "passwordSQLAnda";

    public static void main(String[] args) throws IOException {
        // Menyalakan server Back-End di port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Mendaftarkan Endpoint API Resmi
        server.createContext("/api/register", new RegisterHandler());
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/les", new BookingHandler());
        server.createContext("/api/les/siswa", new GetStudentLessonsHandler());
        server.setExecutor(null);
        System.out.println("Server Back-End Java jalan di: http://localhost:8080");
        server.start();
    }

    // ---------------------------------------------------------
    // HANDLER 1: REGISTRASI AKUN (SISWA, GURU, ADMIN)
    // ---------------------------------------------------------
    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aturCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String jsonInput = bacaBody(exchange);

                try {
                    String role = ambilNilaiJSON(jsonInput, "role");
                    String nama = ambilNilaiJSON(jsonInput, "nama");
                    String email = ambilNilaiJSON(jsonInput, "email");
                    String noHp = ambilNilaiJSON(jsonInput, "no_hp");
                    String password = ambilNilaiJSON(jsonInput, "password");

                    int newId = (int) (System.currentTimeMillis() % 100000);

                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        String sql = "";
                        PreparedStatement pstmt = null;

                        if ("student".equals(role)) {
                            String idJenjangStr = ambilNilaiJSON(jsonInput, "id_jenjang");
                            int idJenjang = idJenjangStr.isEmpty() ? 1 : Integer.parseInt(idJenjangStr);
                            String tglLahir = ambilNilaiJSON(jsonInput, "tanggal_lahir");
                            if (tglLahir.isEmpty())
                                tglLahir = "2000-01-01";
                            String jenisKelamin = ambilNilaiJSON(jsonInput, "jenis_kelamin");
                            if (jenisKelamin.isEmpty())
                                jenisKelamin = "L";

                            sql = "INSERT INTO Siswa (id_siswa, id_jenjang, nama, tgl_lahir, jenis_kelamin, no_hp, email, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                            pstmt = conn.prepareStatement(sql);
                            pstmt.setInt(1, newId);
                            pstmt.setInt(2, idJenjang);
                            pstmt.setString(3, nama);

                            java.sql.Date sqlDate = null;
                            try {
                                if (tglLahir.contains("-")) {
                                    sqlDate = java.sql.Date.valueOf(tglLahir);
                                } else if (tglLahir.contains("/")) {
                                    String[] parts = tglLahir.split("/");
                                    if (parts[2].length() == 4) {
                                        String formattedDate = parts[2] + "-" + parts[1] + "-" + parts[0];
                                        sqlDate = java.sql.Date.valueOf(formattedDate);
                                    }
                                }
                            } catch (Exception dateEx) {
                                sqlDate = java.sql.Date.valueOf("2000-01-01");
                            }

                            pstmt.setDate(4, sqlDate);
                            pstmt.setString(5, jenisKelamin);
                            pstmt.setString(6, noHp);
                            pstmt.setString(7, email);
                            pstmt.setString(8, password);

                            pstmt.executeUpdate();
                            pstmt.close();

                        } else if ("teacher".equals(role)) {
                            // 1. Simpan data utama Guru
                            sql = "INSERT INTO Guru (id_guru, nama, email, no_hp, password) VALUES (?, ?, ?, ?, ?)";
                            pstmt = conn.prepareStatement(sql);
                            pstmt.setInt(1, newId);
                            pstmt.setString(2, nama);
                            pstmt.setString(3, email);
                            pstmt.setString(4, noHp);
                            pstmt.setString(5, password);
                            pstmt.executeUpdate();
                            pstmt.close();

                            // 2. Simpan Multi-Keahlian Guru (Hasil gabungan dari React)
                            String expertisesStr = ambilNilaiJSON(jsonInput, "expertises");
                            if (!expertisesStr.isEmpty()) {
                                String[] expArr = expertisesStr.split(",");
                                String sqlKeahlian = "INSERT INTO Keahlian_Guru (id_keahlian, id_guru, id_mapel, id_jenjang) VALUES (?, ?, ?, ?)";

                                try (PreparedStatement pstmtKeahlian = conn.prepareStatement(sqlKeahlian)) {
                                    int counter = 1;
                                    for (String exp : expArr) {
                                        String[] parts = exp.split("-");
                                        if (parts.length == 2) {
                                            int idMapel = Integer.parseInt(parts[0]);
                                            int idJenjang = Integer.parseInt(parts[1]);

                                            // Tambahkan counter agar ID tidak bertabrakan saat looping super cepat
                                            int idKeahlian = (int) (System.currentTimeMillis() % 100000)
                                                    + (int) (Math.random() * 50000) + counter;

                                            pstmtKeahlian.setInt(1, idKeahlian);
                                            pstmtKeahlian.setInt(2, newId);
                                            pstmtKeahlian.setInt(3, idMapel);
                                            pstmtKeahlian.setInt(4, idJenjang);
                                            pstmtKeahlian.executeUpdate();
                                            counter++;
                                        }
                                    }
                                }
                            }

                        } else if ("admin".equals(role)) {
                            // 1. Simpan data ke tabel Admin (Seperti biasa)
                            sql = "INSERT INTO Admin (id_admin, nama, email, no_hp, password) VALUES (?, ?, ?, ?, ?)";
                            pstmt = conn.prepareStatement(sql);
                            pstmt.setInt(1, newId);
                            pstmt.setString(2, nama);
                            pstmt.setString(3, email);
                            pstmt.setString(4, noHp);
                            pstmt.setString(5, password);
                            pstmt.executeUpdate();
                            pstmt.close();

                            // 2. TAMBAHKAN KODE INI: Cek dan Update tabel Guru jika emailnya sama
                            String sqlUpdateGuru = "UPDATE Guru SET id_admin = ? WHERE email = ?";
                            try (PreparedStatement pstmtUpdateGuru = conn.prepareStatement(sqlUpdateGuru)) {
                                pstmtUpdateGuru.setInt(1, newId); // newId ini adalah id_admin yang baru di-generate
                                pstmtUpdateGuru.setString(2, email);

                                int barisTerupdate = pstmtUpdateGuru.executeUpdate();
                                if (barisTerupdate > 0) {
                                    System.out.println(
                                            "Sistem menemukan email yang sama di tabel Guru. id_admin di tabel Guru berhasil di-update!");
                                }
                            }
                        } else {
                            throw new Exception("Role tidak dikenali: " + role);
                        }

                        System.out.println("Berhasil registrasi: " + nama + " sebagai " + role);
                    }

                    kirimResponJSON(exchange, 200, "{\"status\":\"sukses\"}");

                } catch (Exception e) {
                    e.printStackTrace();
                    kirimResponJSON(exchange, 400, "{\"status\":\"gagal\", \"pesan\":\"" + e.getMessage() + "\"}");
                }
            }
        }
    }

    // ---------------------------------------------------------
    // HANDLER 2: LOGIN AKUN (MENGEMBALIKAN ID USER)
    // ---------------------------------------------------------
    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aturCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String jsonInput = bacaBody(exchange);

                try {
                    String email = ambilNilaiJSON(jsonInput, "email");
                    String password = ambilNilaiJSON(jsonInput, "password");

                    String roleDitemukan = "";
                    String namaDitemukan = "";
                    int idDitemukan = 0;

                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

                        // 1. Cek di tabel Siswa
                        String sqlSiswa = "SELECT id_siswa, nama FROM Siswa WHERE email = ? AND password = ?";
                        try (PreparedStatement pstmt = conn.prepareStatement(sqlSiswa)) {
                            pstmt.setString(1, email);
                            pstmt.setString(2, password);
                            ResultSet rs = pstmt.executeQuery();
                            if (rs.next()) {
                                roleDitemukan = "student";
                                namaDitemukan = rs.getString("nama");
                                idDitemukan = rs.getInt("id_siswa");
                            }
                        }

                        // 2. Jika bukan siswa, Cek di tabel Guru
                        if (roleDitemukan.isEmpty()) {
                            String sqlGuru = "SELECT id_guru, nama FROM Guru WHERE email = ? AND password = ?";
                            try (PreparedStatement pstmt = conn.prepareStatement(sqlGuru)) {
                                pstmt.setString(1, email);
                                pstmt.setString(2, password);
                                ResultSet rs = pstmt.executeQuery();
                                if (rs.next()) {
                                    roleDitemukan = "teacher";
                                    namaDitemukan = rs.getString("nama");
                                    idDitemukan = rs.getInt("id_guru");
                                }
                            }
                        }

                        // 3. Jika bukan guru, Cek di tabel Admin
                        if (roleDitemukan.isEmpty()) {
                            String sqlAdmin = "SELECT id_admin, nama FROM Admin WHERE email = ? AND password = ?";
                            try (PreparedStatement pstmt = conn.prepareStatement(sqlAdmin)) {
                                pstmt.setString(1, email);
                                pstmt.setString(2, password);
                                ResultSet rs = pstmt.executeQuery();
                                if (rs.next()) {
                                    roleDitemukan = "admin";
                                    namaDitemukan = rs.getString("nama");
                                    idDitemukan = rs.getInt("id_admin");
                                }
                            }
                        }
                    }

                    if (!roleDitemukan.isEmpty()) {
                        String responSukses = "{\"status\":\"sukses\", \"role\":\"" + roleDitemukan +
                                "\", \"nama\":\"" + namaDitemukan +
                                "\", \"id\":" + idDitemukan + "}";
                        kirimResponJSON(exchange, 200, responSukses);
                    } else {
                        kirimResponJSON(exchange, 401,
                                "{\"status\":\"gagal\", \"pesan\":\"Email atau password salah.\"}");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    kirimResponJSON(exchange, 500,
                            "{\"status\":\"gagal\", \"pesan\":\"Terjadi kesalahan di server.\"}");
                }
            }
        }
    }

    // ---------------------------------------------------------
    // HANDLER 3: BOOKING LES (MENYIMPAN KE TABEL LES)
    // ---------------------------------------------------------
    static class BookingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aturCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String jsonInput = bacaBody(exchange);

                try {
                    int idSiswa = Integer.parseInt(ambilNilaiJSON(jsonInput, "id_siswa"));
                    int idJadwal = Integer.parseInt(ambilNilaiJSON(jsonInput, "id_jadwal"));

                    // --- POTONG DAN TIMPA KODE PARSING TANGGAL DI SINI ---
                    String tglMulaiRaw = ambilNilaiJSON(jsonInput, "tanggal_mulai");
                    String tglSelesaiRaw = ambilNilaiJSON(jsonInput, "tanggal_selesai");

                    java.sql.Timestamp tsMulai = null;
                    java.sql.Timestamp tsSelesai = null;

                    try {
                        // Jika formatnya YYYY-MM-DDTHH:MM (Format standar HTML datetime-local)
                        String tglMulaiStr = tglMulaiRaw.replace("T", " ")
                                + (tglMulaiRaw.contains(":") ? ":00" : " 00:00:00");
                        String tglSelesaiStr = tglSelesaiRaw.replace("T", " ")
                                + (tglSelesaiRaw.contains(":") ? ":00" : " 00:00:00");

                        // Jika ternyata format yang dikirim browser Anda adalah DD/MM/YYYY
                        if (tglMulaiRaw.contains("/")) {
                            String[] partsM = tglMulaiRaw.split("/");
                            String[] partsS = tglSelesaiRaw.split("/");
                            tglMulaiStr = partsM[2].trim() + "-" + partsM[1].trim() + "-" + partsM[0].trim()
                                    + " 00:00:00";
                            tglSelesaiStr = partsS[2].trim() + "-" + partsS[1].trim() + "-" + partsS[0].trim()
                                    + " 23:59:59";
                        }

                        tsMulai = java.sql.Timestamp.valueOf(tglMulaiStr);
                        tsSelesai = java.sql.Timestamp.valueOf(tglSelesaiStr);
                    } catch (Exception dateEx) {
                        // Jalur aman (fallback) agar server tidak crash jika format tidak dikenali
                        tsMulai = new java.sql.Timestamp(System.currentTimeMillis());
                        tsSelesai = new java.sql.Timestamp(System.currentTimeMillis() + 3600000);
                    }
                    // -----------------------------------------------------------------

                    int newIdLes = (int) (System.currentTimeMillis() % 100000) + (int) (Math.random() * 50000);
                    int newIdDetail = (int) (System.currentTimeMillis() % 100000) + (int) (Math.random() * 50000);

                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

                        // 1. CEK APAKAH SLOT DI JADWAL TERSEDIA
                        String sqlCekJadwal = "SELECT status FROM Jadwal_Kesediaan_Guru WHERE id_jadwal = ?";
                        try (PreparedStatement pstmtCek = conn.prepareStatement(sqlCekJadwal)) {
                            pstmtCek.setInt(1, idJadwal);
                            ResultSet rsJadwal = pstmtCek.executeQuery();
                            if (rsJadwal.next()) {
                                String statusJadwal = rsJadwal.getString("status");

                                // --- TAMBAHKAN BARIS INI SEBAGAI CCTV ---
                                System.out.println("CCTV: React mengirim id_jadwal = " + idJadwal
                                        + ". Status di Database saat ini = '" + statusJadwal + "'");
                                // ----------------------------------------

                                if ("terisi".equalsIgnoreCase(statusJadwal)) {
                                    kirimResponJSON(exchange, 400,
                                            "{\"status\":\"gagal\", \"pesan\":\"Slot jadwal ini baru saja diambil siswa lain!\"}");
                                    return;
                                }
                            }
                        }

                        // 2. JIKA AMAN, INSERT KE TABEL INDUK: Daftar_les
                        String sqlInsertLes = "INSERT INTO Les (id_les, tgl_mulai, tgl_selesai, id_siswa) VALUES (?, ?, ?, ?)";
                        // Cari bagian ini dan sesuaikan variabelnya:
                        try (PreparedStatement pstmtLes = conn.prepareStatement(sqlInsertLes)) {
                            pstmtLes.setInt(1, newIdLes);
                            pstmtLes.setTimestamp(2, tsMulai); // <--- Ganti jadi tsMulai
                            pstmtLes.setTimestamp(3, tsSelesai); // <--- Ganti jadi tsSelesai
                            pstmtLes.setInt(4, idSiswa);
                            pstmtLes.executeUpdate();
                        }

                        // 3. INSERT KE TABEL PENENGAH: Detail_Daftar_Les (Sesuaikan FK Mapel & Jenjang)
                        // Mengambil id_mapel dan id_jenjang dari relasi keahlian jadwal tersebut secara
                        // dinamis
                        int idMapel = 1;
                        int idJenjang = 1;
                        String sqlGetMaster = "SELECT k.id_mapel, k.id_jenjang FROM Jadwal_Kesediaan_Guru j " +
                                "JOIN Keahlian_Guru k ON j.id_guru = k.id_guru WHERE j.id_jadwal = ?";
                        try (PreparedStatement pstmtMaster = conn.prepareStatement(sqlGetMaster)) {
                            pstmtMaster.setInt(1, idJadwal);
                            ResultSet rsM = pstmtMaster.executeQuery();
                            if (rsM.next()) {
                                idMapel = rsM.getInt("id_mapel");
                                idJenjang = rsM.getInt("id_jenjang");
                            }
                        }

                        // Ganti id_jadwal menjadi idjadwal
                        String sqlInsertDetail = "INSERT INTO Detail_Daftar_Les (id_detail, id_les, id_jadwal, id_mapel, id_jenjang) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlInsertDetail)) {
                            pstmtDetail.setInt(1, newIdDetail);
                            pstmtDetail.setInt(2, newIdLes);
                            pstmtDetail.setInt(3, idJadwal);
                            pstmtDetail.setInt(4, idMapel);
                            pstmtDetail.setInt(5, idJenjang);
                            pstmtDetail.executeUpdate();
                        }

                        // 4. UPDATE STATUS SLOT DI TABEL JADWAL GURU MENJADI 'terisi'
                        String sqlUpdateJadwal = "UPDATE Jadwal_Kesediaan_Guru SET status = 'terisi' WHERE id_jadwal = ?";
                        try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateJadwal)) {
                            pstmtUpdate.setInt(1, idJadwal);
                            pstmtUpdate.executeUpdate();
                        }
                    }

                    kirimResponJSON(exchange, 200, "{\"status\":\"sukses\"}");

                } catch (Exception e) {
                    e.printStackTrace();
                    kirimResponJSON(exchange, 400, "{\"status\":\"gagal\", \"pesan\":\"" + e.getMessage() + "\"}");
                }
            }
        }
    }

    static class GetStudentLessonsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aturCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    // Ambil id_siswa dari query string (contoh: /api/les/siswa?id_siswa=73210)
                    String query = exchange.getRequestURI().getQuery();
                    String idSiswaStr = query.split("id_siswa=")[1].split("&")[0];
                    int idSiswa = Integer.parseInt(idSiswaStr);

                    String jsonResult = "[";
                    // GANTI MENJADI SEPERTI INI (Tambahkan l.durasi):
                    // Ganti ddl.id_jadwal menjadi ddl.idjadwal AS id_jadwal
                    // 1. Perbaiki Query SQL (Tambahkan dl.durasi agar tidak error di baris
                    // bawahnya)
                    // Menghitung selisih bulan antara tgl_mulai dan tgl_selesai
                    String sql = "SELECT dl.id_les, dl.id_siswa, dl.tgl_mulai, dl.tgl_selesai, ddl.id_jadwal, "
                            + "DATEDIFF(month, dl.tgl_mulai, dl.tgl_selesai) AS durasi " // <--- Cukup ganti ke month
                            + "FROM Les dl "
                            + "JOIN Detail_Daftar_Les ddl ON dl.id_les = ddl.id_les "
                            + "WHERE dl.id_siswa = ?";

                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                            PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, idSiswa);
                        ResultSet rs = pstmt.executeQuery();
                        boolean first = true;
                        while (rs.next()) {
                            if (!first)
                                jsonResult += ",";

                            // 2. Sesuaikan nama di dalam rs.getString() menjadi tgl_mulai dan tgl_selesai
                            jsonResult += "{" +
                                    "\"id\":" + rs.getInt("id_les") + "," +
                                    "\"id_siswa\":" + rs.getInt("id_siswa") + "," +
                                    "\"id_jadwal\":" + rs.getInt("id_jadwal") + "," +
                                    "\"tanggal_mulai\":\"" + rs.getString("tgl_mulai").replace(" ", "T") + "\"," +
                                    "\"tanggal_selesai\":\"" + rs.getString("tgl_selesai").replace(" ", "T") + "\"," +
                                    "\"durasi\":" + rs.getInt("durasi") +
                                    "}";
                            first = false;
                        }
                    }
                    jsonResult += "]";

                    kirimResponJSON(exchange, 200, jsonResult);
                } catch (Exception e) {
                    e.printStackTrace();
                    kirimResponJSON(exchange, 500, "[]");
                }
            }
        }
    }

    // ---------------------------------------------------------
    // FUNGSI BANTUAN (UTILITIES)
    // ---------------------------------------------------------

    private static void aturCORS(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private static String bacaBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        return br.lines().collect(Collectors.joining());
    }

    private static String ambilNilaiJSON(String json, String key) {
        try {
            String target = "\"" + key + "\":";
            int index = json.indexOf(target);
            if (index == -1)
                return "";

            String remainder = json.substring(index + target.length()).trim();
            if (remainder.startsWith("\"")) {
                return remainder.substring(1, remainder.indexOf("\"", 1));
            } else {
                int endDoc = remainder.indexOf(",");
                int endObj = remainder.indexOf("}");
                int end = (endDoc != -1 && endDoc < endObj) ? endDoc : endObj;
                if (end == -1)
                    end = remainder.length();
                return remainder.substring(0, end).trim();
            }
        } catch (Exception e) {
            return "";
        }
    }

    private static void kirimResponJSON(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}