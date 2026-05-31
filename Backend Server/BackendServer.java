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

    // Kredensial Azure SQL Server
    private static final String URL = "jdbc:sqlserver://mibdlesprivat.database.windows.net:1433;database=MIBDLesPrivat;user=guguk@mibdlesprivat;password=AIPastiWIN69;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
    private static final String USER = "guguk";
    private static final String PASSWORD = "AIPastiWIN69";

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
                    String pswrd = ambilNilaiJSON(jsonInput, "password");

                    // Generate ID acak sederhana
                    int newId = (int) (System.currentTimeMillis() % 100000);

                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        String sql = "";
                        PreparedStatement pstmt = null;

                        if ("student".equals(role)) {
                            // Registrasi Siswa
                            String idJenjangStr = ambilNilaiJSON(jsonInput, "id_jenjang");
                            int idJenjang = idJenjangStr.isEmpty() ? 1 : Integer.parseInt(idJenjangStr);
                            String tglLahir = ambilNilaiJSON(jsonInput, "tanggal_lahir");
                            if (tglLahir.isEmpty())
                                tglLahir = "2000-01-01";
                            String jenisKelamin = ambilNilaiJSON(jsonInput, "jenis_kelamin");
                            if (jenisKelamin.isEmpty())
                                jenisKelamin = "L";

                            sql = "INSERT INTO Siswa (id_siswa, id_jenjang, nama, tanggal_lahir, jenis_kelamin, no_hp, email, pswrd, alamat) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                            pstmt = conn.prepareStatement(sql);
                            pstmt.setInt(1, newId);
                            pstmt.setInt(2, idJenjang);
                            pstmt.setString(3, nama);
                            pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(tglLahir + " 00:00:00"));
                            pstmt.setString(5, jenisKelamin);
                            pstmt.setString(6, noHp);
                            pstmt.setString(7, email);
                            pstmt.setString(8, pswrd);
                            pstmt.setString(9, "Belum diisi");

                        } else if ("teacher".equals(role)) {
                            // Registrasi Guru
                            sql = "INSERT INTO Guru (id_guru, nama, email, no_hp, pswrd) VALUES (?, ?, ?, ?, ?)";
                            pstmt = conn.prepareStatement(sql);
                            pstmt.setInt(1, newId);
                            pstmt.setString(2, nama);
                            pstmt.setString(3, email);
                            pstmt.setString(4, noHp);
                            pstmt.setString(5, pswrd);

                        } else if ("admin".equals(role)) {
                            // Registrasi Admin
                            sql = "INSERT INTO Admin (id_admin, nama, email, no_hp, pswrd) VALUES (?, ?, ?, ?, ?)";
                            pstmt = conn.prepareStatement(sql);
                            pstmt.setInt(1, newId);
                            pstmt.setString(2, nama);
                            pstmt.setString(3, email);
                            pstmt.setString(4, noHp);
                            pstmt.setString(5, pswrd);
                        } else {
                            throw new Exception("Role tidak dikenali: " + role);
                        }

                        pstmt.executeUpdate();
                        pstmt.close();
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
                        String sqlSiswa = "SELECT id_siswa, nama FROM Siswa WHERE email = ? AND pswrd = ?";
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
                            String sqlGuru = "SELECT id_guru, nama FROM Guru WHERE email = ? AND pswrd = ?";
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
                            String sqlAdmin = "SELECT id_admin, nama FROM Admin WHERE email = ? AND pswrd = ?";
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
                    int durasi = Integer.parseInt(ambilNilaiJSON(jsonInput, "durasi")); // <--- AMBIL DURASI DARI REACT

                    // Format dari React: "2025-01-20T09:00" -> SQL butuh: "2025-01-20 09:00:00"
                    String tglMulaiStr = ambilNilaiJSON(jsonInput, "tanggal_mulai").replace("T", " ") + ":00";
                    String tglSelesaiStr = ambilNilaiJSON(jsonInput, "tanggal_selesai").replace("T", " ") + ":00";

                    int newIdLes = (int) (System.currentTimeMillis() % 100000) + (int) (Math.random() * 50000);

                    // Tambahkan kolom durasi ke dalam query INSERT
                    String sql = "INSERT INTO Les (id_les, id_siswa, id_jadwal, tanggal_mulai, tanggal_selesai, durasi) VALUES (?, ?, ?, ?, ?, ?)";

                    // Buka koneksi database satu kali untuk cek dan insert
                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

                        // --- 1. CEK BENTROK DULU (NOMOR 3) ---
                        String sqlCek = "SELECT COUNT(*) AS total_bentrok FROM Les " +
                                "WHERE id_jadwal = ? AND " +
                                "(tanggal_mulai < ? AND tanggal_selesai > ?)";

                        try (PreparedStatement pstmtCek = conn.prepareStatement(sqlCek)) {
                            pstmtCek.setInt(1, idJadwal);
                            pstmtCek.setTimestamp(2, java.sql.Timestamp.valueOf(tglSelesaiStr));
                            pstmtCek.setTimestamp(3, java.sql.Timestamp.valueOf(tglMulaiStr));

                            ResultSet rsCek = pstmtCek.executeQuery();
                            if (rsCek.next() && rsCek.getInt("total_bentrok") > 0) {
                                // JIKA BENTROK, TOLAK DAN LEMPAR ERROR
                                kirimResponJSON(exchange, 400,
                                        "{\"status\":\"gagal\", \"pesan\":\"Waktu tersebut sudah di-booking oleh siswa lain. Silakan pilih jam lain!\"}");
                                return; // Hentikan eksekusi di sini, jangan lanjut ke INSERT
                            }
                        }

                        // --- 2. JIKA AMAN, LANJUTKAN PROSES INSERT ---
                        String sqlInsert = "INSERT INTO Les (id_les, id_siswa, id_jadwal, tanggal_mulai, tanggal_selesai, durasi) VALUES (?, ?, ?, ?, ?, ?)";

                        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                            pstmt.setInt(1, newIdLes);
                            pstmt.setInt(2, idSiswa);
                            pstmt.setInt(3, idJadwal);
                            pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(tglMulaiStr));
                            pstmt.setTimestamp(5, java.sql.Timestamp.valueOf(tglSelesaiStr));
                            pstmt.setInt(6, durasi);

                            pstmt.executeUpdate();
                        }
                    }
                    System.out.println("DEBUG: Sukses booking! Siswa: " + idSiswa + ", Jadwal: " + idJadwal
                            + ", Durasi: " + durasi + " menit");
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
                    String sql = "SELECT l.id_les, l.id_siswa, l.id_jadwal, l.tanggal_mulai, l.tanggal_selesai, l.durasi "
                            +
                            "FROM Les l WHERE l.id_siswa = ?";

                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                            PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, idSiswa);
                        ResultSet rs = pstmt.executeQuery();
                        boolean first = true;
                        while (rs.next()) {
                            if (!first)
                                jsonResult += ",";
                            // Di dalam GetStudentLessonsHandler bagian rs.next() update string JSON-nya:
                            jsonResult += "{" +
                                    "\"id\":" + rs.getInt("id_les") + "," +
                                    "\"id_siswa\":" + rs.getInt("id_siswa") + "," +
                                    "\"id_jadwal\":" + rs.getInt("id_jadwal") + "," +
                                    "\"tanggal_mulai\":\"" + rs.getString("tanggal_mulai").replace(" ", "T") + "\"," +
                                    "\"tanggal_selesai\":\"" + rs.getString("tanggal_selesai").replace(" ", "T") + "\","
                                    +
                                    "\"durasi\":" + rs.getInt("durasi") + // <--- TAMBAHKAN INI
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