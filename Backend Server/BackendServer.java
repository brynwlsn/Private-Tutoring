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

    private static final String URL = "jdbc:sqlserver://localhost\\SQLEXPRESS:1433;"
            + "databaseName=LesPrivat;"
            + "encrypt=true;"
            + "trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASSWORD = "passwordSQLAnda";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api/register", new RegisterHandler());
        server.createContext("/api/login", new LoginHandler());
        // Booking: POST ke /api/les, GET ke /api/les/siswa?id_siswa=...
        server.createContext("/api/les/siswa", new GetStudentLessonsHandler());
        server.createContext("/api/les", new BookingHandler());
        server.createContext("/api/guru", new GetGuruHandler());
        server.createContext("/api/siswa", new GetSiswaHandler());
        server.createContext("/api/mapel", new GetMapelHandler());
        server.createContext("/api/jenjang", new GetJenjangHandler());
        server.createContext("/api/keahlian", new GetKeahlianHandler());
        server.createContext("/api/jadwal", new GetJadwalHandler());
        server.createContext("/api/admin", new GetAdminHandler());
        server.createContext("/api/les/guru", new GetTeacherLessonsHandler());

        server.setExecutor(null);
        System.out.println("Server Back-End Java jalan di: http://localhost:8080");
        server.start();
    }

    // =========================================================
    // GET HANDLERS
    // =========================================================

    static class GetGuruHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aturCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "SELECT id_guru, nama, email, no_hp, id_admin FROM Guru ORDER BY nama";
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                StringBuilder json = new StringBuilder("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first)
                        json.append(",");
                    json.append("{")
                            .append("\"id\":").append(rs.getInt("id_guru")).append(",")
                            .append("\"nama\":\"").append(escapeJson(rs.getString("nama"))).append("\",")
                            .append("\"email\":\"").append(escapeJson(nullSafe(rs.getString("email")))).append("\",")
                            .append("\"no_hp\":\"").append(escapeJson(nullSafe(rs.getString("no_hp")))).append("\",")
                            .append("\"id_admin\":")
                            .append(rs.getObject("id_admin") != null ? rs.getInt("id_admin") : "null")
                            .append("}");
                    first = false;
                }
                json.append("]");
                kirimResponJSON(exchange, 200, json.toString());

            } catch (Exception e) {
                e.printStackTrace();
                kirimResponJSON(exchange, 500, "[]");
            }
        }
    }

    static class GetSiswaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aturCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "SELECT id_siswa, nama, email, no_hp, tgl_lahir, jenis_kelamin, id_jenjang "
                        + "FROM Siswa ORDER BY nama";
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                StringBuilder json = new StringBuilder("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first)
                        json.append(",");

                    // tgl_lahir bisa null di schema
                    String tglLahir = rs.getDate("tgl_lahir") != null
                            ? rs.getDate("tgl_lahir").toString()
                            : "";

                    json.append("{")
                            .append("\"id\":").append(rs.getInt("id_siswa")).append(",")
                            .append("\"nama\":\"").append(escapeJson(rs.getString("nama"))).append("\",")
                            .append("\"email\":\"").append(escapeJson(nullSafe(rs.getString("email")))).append("\",")
                            .append("\"no_hp\":\"").append(escapeJson(nullSafe(rs.getString("no_hp")))).append("\",")
                            .append("\"tanggal_lahir\":\"").append(tglLahir).append("\",")
                            .append("\"jenis_kelamin\":\"").append(escapeJson(nullSafe(rs.getString("jenis_kelamin"))))
                            .append("\",")
                            .append("\"id_jenjang\":")
                            .append(rs.getObject("id_jenjang") != null ? rs.getInt("id_jenjang") : "null")
                            .append("}");
                    first = false;
                }
                json.append("]");
                kirimResponJSON(exchange, 200, json.toString());

            } catch (Exception e) {
                e.printStackTrace();
                kirimResponJSON(exchange, 500, "[]");
            }
        }
    }

    static class GetMapelHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aturCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                // Alias nama_mapel → nama supaya frontend tidak perlu diubah
                String sql = "SELECT id_mapel, nama_mapel AS nama FROM Mata_pelajaran ORDER BY id_mapel";
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                StringBuilder json = new StringBuilder("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first)
                        json.append(",");
                    json.append("{")
                            .append("\"id\":").append(rs.getInt("id_mapel")).append(",")
                            .append("\"nama\":\"").append(escapeJson(rs.getString("nama"))).append("\"")
                            .append("}");
                    first = false;
                }
                json.append("]");
                kirimResponJSON(exchange, 200, json.toString());

            } catch (Exception e) {
                e.printStackTrace();
                kirimResponJSON(exchange, 500, "[]");
            }
        }
    }

    static class GetJenjangHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aturCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "SELECT id_jenjang, nama_jenjang AS nama FROM Jenjang ORDER BY id_jenjang";
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                StringBuilder json = new StringBuilder("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first)
                        json.append(",");
                    json.append("{")
                            .append("\"id\":").append(rs.getInt("id_jenjang")).append(",")
                            .append("\"nama\":\"").append(escapeJson(rs.getString("nama"))).append("\"")
                            .append("}");
                    first = false;
                }
                json.append("]");
                kirimResponJSON(exchange, 200, json.toString());

            } catch (Exception e) {
                e.printStackTrace();
                kirimResponJSON(exchange, 500, "[]");
            }
        }
    }

    static class GetKeahlianHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aturCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "SELECT id_keahlian, id_guru, id_mapel, id_jenjang FROM Keahlian_Guru";
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                StringBuilder json = new StringBuilder("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first)
                        json.append(",");
                    json.append("{")
                            .append("\"id\":").append(rs.getInt("id_keahlian")).append(",")
                            .append("\"id_guru\":")
                            .append(rs.getObject("id_guru") != null ? rs.getInt("id_guru") : "null").append(",")
                            .append("\"id_mapel\":")
                            .append(rs.getObject("id_mapel") != null ? rs.getInt("id_mapel") : "null").append(",")
                            .append("\"id_jenjang\":")
                            .append(rs.getObject("id_jenjang") != null ? rs.getInt("id_jenjang") : "null")
                            .append("}");
                    first = false;
                }
                json.append("]");
                kirimResponJSON(exchange, 200, json.toString());

            } catch (Exception e) {
                e.printStackTrace();
                kirimResponJSON(exchange, 500, "[]");
            }
        }
    }
    
    static class GetTeacherLessonsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aturCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                try {
                    String query = exchange.getRequestURI().getQuery();
                    String idGuruStr = query.split("id_guru=")[1].split("&")[0];
                    int idGuru = Integer.parseInt(idGuruStr);

                    String sql = "SELECT l.id_les, l.id_siswa, s.nama AS nama_siswa, " +
                            "CONVERT(varchar(10), l.tgl_mulai, 120) AS tgl_mulai, " +
                            "CONVERT(varchar(10), l.tgl_selesai, 120) AS tgl_selesai, " +
                            "ddl.id_jadwal, ddl.id_mapel, ddl.id_jenjang, " +
                            "j.hari, " +
                            "CONVERT(varchar(5), j.jam_mulai, 108) AS jam_mulai, " +
                            "CONVERT(varchar(5), j.jam_selesai, 108) AS jam_selesai " +
                            "FROM Les l " +
                            "JOIN Siswa s ON l.id_siswa = s.id_siswa " +
                            "JOIN Detail_Daftar_Les ddl ON l.id_les = ddl.id_les " +
                            "JOIN Jadwal_Kesediaan_Guru j ON ddl.id_jadwal = j.id_jadwal " +
                            "WHERE j.id_guru = ?";

                    StringBuilder json = new StringBuilder("[");
                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                            PreparedStatement ps = conn.prepareStatement(sql)) {

                        ps.setInt(1, idGuru);
                        ResultSet rs = ps.executeQuery();

                        boolean first = true;
                        while (rs.next()) {
                            if (!first)
                                json.append(",");

                            json.append("{")
                                    .append("\"id\":").append(rs.getInt("id_les")).append(",")
                                    .append("\"id_siswa\":").append(rs.getInt("id_siswa")).append(",")
                                    .append("\"nama_siswa\":\"").append(escapeJson(rs.getString("nama_siswa")))
                                    .append("\",")
                                    .append("\"id_jadwal\":").append(rs.getInt("id_jadwal")).append(",")
                                    .append("\"id_mapel\":").append(rs.getInt("id_mapel")).append(",")
                                    .append("\"id_jenjang\":").append(rs.getInt("id_jenjang")).append(",")
                                    .append("\"hari\":\"").append(escapeJson(rs.getString("hari"))).append("\",")
                                    .append("\"jam_mulai\":\"").append(rs.getString("jam_mulai")).append("\",")
                                    .append("\"jam_selesai\":\"").append(rs.getString("jam_selesai")).append("\",")
                                    .append("\"tanggal_mulai\":\"").append(rs.getString("tgl_mulai")).append("T")
                                    .append(rs.getString("jam_mulai")).append("\",")
                                    .append("\"tanggal_selesai\":\"").append(rs.getString("tgl_selesai")).append("T")
                                    .append(rs.getString("jam_selesai")).append("\"")
                                    .append("}");

                            first = false;
                        }
                    }

                    json.append("]");
                    kirimResponJSON(exchange, 200, json.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                    kirimResponJSON(exchange, 500, "[]");
                }
            }
        }
    }

    static class GetJadwalHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aturCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "SELECT j.id_jadwal, j.hari, " +
                        "CONVERT(varchar(5), j.jam_mulai, 108) AS jam_mulai, " +
                        "CONVERT(varchar(5), j.jam_selesai, 108) AS jam_selesai, " +
                        "j.id_guru, j.id_admin, " +
                        "CASE WHEN COUNT(d.id_jadwal) > 0 THEN 'terisi' ELSE 'tersedia' END AS status " +
                        "FROM Jadwal_Kesediaan_Guru j " +
                        "LEFT JOIN Detail_Daftar_Les d ON j.id_jadwal = d.id_jadwal " +
                        "GROUP BY j.id_jadwal, j.hari, j.jam_mulai, j.jam_selesai, j.id_guru, j.id_admin " +
                        "ORDER BY j.hari, j.jam_mulai";

                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                StringBuilder json = new StringBuilder("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first)
                        json.append(",");
                    json.append("{")
                            .append("\"id\":").append(rs.getInt("id_jadwal")).append(",")
                            .append("\"hari\":\"").append(escapeJson(rs.getString("hari"))).append("\",")
                            .append("\"jam_mulai\":\"").append(escapeJson(rs.getString("jam_mulai"))).append("\",")
                            .append("\"jam_selesai\":\"").append(escapeJson(rs.getString("jam_selesai"))).append("\",")
                            .append("\"id_guru\":")
                            .append(rs.getObject("id_guru") != null ? rs.getInt("id_guru") : "null").append(",")
                            .append("\"id_admin\":")
                            .append(rs.getObject("id_admin") != null ? rs.getInt("id_admin") : "null").append(",")
                            .append("\"status\":\"").append(rs.getString("status")).append("\"")
                            .append("}");
                    first = false;
                }
                json.append("]");
                kirimResponJSON(exchange, 200, json.toString());

            } catch (Exception e) {
                e.printStackTrace();
                kirimResponJSON(exchange, 500, "[]");
            }
        }
    }

    static class GetAdminHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aturCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "SELECT id_admin, nama, email, no_hp FROM Admin ORDER BY nama";
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                StringBuilder json = new StringBuilder("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first)
                        json.append(",");
                    json.append("{")
                            .append("\"id\":").append(rs.getInt("id_admin")).append(",")
                            .append("\"nama\":\"").append(escapeJson(rs.getString("nama"))).append("\",")
                            .append("\"email\":\"").append(escapeJson(nullSafe(rs.getString("email")))).append("\",")
                            .append("\"no_hp\":\"").append(escapeJson(nullSafe(rs.getString("no_hp")))).append("\"")
                            .append("}");
                    first = false;
                }
                json.append("]");
                kirimResponJSON(exchange, 200, json.toString());

            } catch (Exception e) {
                e.printStackTrace();
                kirimResponJSON(exchange, 500, "[]");
            }
        }
    }

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
                                        sqlDate = java.sql.Date.valueOf(parts[2] + "-" + parts[1] + "-" + parts[0]);
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
                            sql = "INSERT INTO Guru (id_guru, nama, email, no_hp, password) VALUES (?, ?, ?, ?, ?)";
                            pstmt = conn.prepareStatement(sql);
                            pstmt.setInt(1, newId);
                            pstmt.setString(2, nama);
                            pstmt.setString(3, email);
                            pstmt.setString(4, noHp);
                            pstmt.setString(5, password);
                            pstmt.executeUpdate();
                            pstmt.close();

                            String expertisesStr = ambilNilaiJSON(jsonInput, "expertises");
                            if (!expertisesStr.isEmpty()) {
                                String[] expArr = expertisesStr.split(",");
                                String sqlKeahlian = "INSERT INTO Keahlian_Guru (id_keahlian, id_guru, id_mapel, id_jenjang) VALUES (?, ?, ?, ?)";
                                try (PreparedStatement pstmtKeahlian = conn.prepareStatement(sqlKeahlian)) {
                                    int counter = 1;
                                    for (String exp : expArr) {
                                        String[] parts = exp.split("-");
                                        if (parts.length == 2) {
                                            int idKeahlian = (int) (System.currentTimeMillis() % 100000)
                                                    + (int) (Math.random() * 50000) + counter;
                                            pstmtKeahlian.setInt(1, idKeahlian);
                                            pstmtKeahlian.setInt(2, newId);
                                            pstmtKeahlian.setInt(3, Integer.parseInt(parts[0]));
                                            pstmtKeahlian.setInt(4, Integer.parseInt(parts[1]));
                                            pstmtKeahlian.executeUpdate();
                                            counter++;
                                        }
                                    }
                                }
                            }

                        } else if ("admin".equals(role)) {
                            sql = "INSERT INTO Admin (id_admin, nama, email, no_hp, password) VALUES (?, ?, ?, ?, ?)";
                            pstmt = conn.prepareStatement(sql);
                            pstmt.setInt(1, newId);
                            pstmt.setString(2, nama);
                            pstmt.setString(3, email);
                            pstmt.setString(4, noHp);
                            pstmt.setString(5, password);
                            pstmt.executeUpdate();
                            pstmt.close();

                            String sqlUpdateGuru = "UPDATE Guru SET id_admin = ? WHERE email = ?";
                            try (PreparedStatement pstmtUpdateGuru = conn.prepareStatement(sqlUpdateGuru)) {
                                pstmtUpdateGuru.setInt(1, newId);
                                pstmtUpdateGuru.setString(2, email);
                                pstmtUpdateGuru.executeUpdate();
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
                    int idMapelReq = Integer.parseInt(ambilNilaiJSON(jsonInput, "id_mapel")); // Tambahkan ini
                    int idJenjangReq = Integer.parseInt(ambilNilaiJSON(jsonInput, "id_jenjang"));

                    String tglMulaiRaw = ambilNilaiJSON(jsonInput, "tanggal_mulai");
                    String tglSelesaiRaw = ambilNilaiJSON(jsonInput, "tanggal_selesai");

                    String tglMulaiStr = tglMulaiRaw.contains("T") ? tglMulaiRaw.split("T")[0] : tglMulaiRaw;
                    String tglSelesaiStr = tglSelesaiRaw.contains("T") ? tglSelesaiRaw.split("T")[0] : tglSelesaiRaw;

                    java.sql.Date tglMulai;
                    java.sql.Date tglSelesai;
                    try {
                        tglMulai = java.sql.Date.valueOf(tglMulaiStr);
                        tglSelesai = java.sql.Date.valueOf(tglSelesaiStr);
                    } catch (Exception dateEx) {
                        tglMulai = new java.sql.Date(System.currentTimeMillis());
                        tglSelesai = new java.sql.Date(System.currentTimeMillis());
                    }

                    int newIdLes = (int) (System.currentTimeMillis() % 100000) + (int) (Math.random() * 50000);
                    int newIdDetail = (int) (System.currentTimeMillis() % 100000) + (int) (Math.random() * 50000);

                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

                        String sqlCek = "SELECT COUNT(*) AS total_bentrok " +
                                "FROM Les l " +
                                "JOIN Detail_Daftar_Les ddl ON l.id_les = ddl.id_les " +
                                "WHERE ddl.id_jadwal = ? " +
                                "AND (l.tgl_mulai <= ? AND l.tgl_selesai >= ?)";

                        try (PreparedStatement pstmtCek = conn.prepareStatement(sqlCek)) {
                            pstmtCek.setInt(1, idJadwal);
                            pstmtCek.setDate(2, tglSelesai);
                            pstmtCek.setDate(3, tglMulai);
                            ResultSet rsJadwal = pstmtCek.executeQuery();
                            if (rsJadwal.next() && rsJadwal.getInt("total_bentrok") > 0) {
                                kirimResponJSON(exchange, 400,
                                        "{\"status\":\"gagal\", \"pesan\":\"Slot jadwal ini sudah terisi pada rentang tanggal tersebut!\"}");
                                return;
                            }
                        }

                        // Insert tabel induk Les
                        String sqlInsertLes = "INSERT INTO Les (id_les, tgl_mulai, tgl_selesai, id_siswa) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement pstmtLes = conn.prepareStatement(sqlInsertLes)) {
                            pstmtLes.setInt(1, newIdLes);
                            pstmtLes.setDate(2, tglMulai);
                            pstmtLes.setDate(3, tglSelesai);
                            pstmtLes.setInt(4, idSiswa);
                            pstmtLes.executeUpdate();
                        }

                        // Ambil id_mapel dan id_jenjang dari keahlian guru di jadwal tersebut
                        // int idMapel = 1;
                        // int idJenjang = 1;
                        // String sqlGetMaster = "SELECT k.id_mapel, k.id_jenjang " +
                        // "FROM Jadwal_Kesediaan_Guru j " +
                        // "JOIN Keahlian_Guru k ON j.id_guru = k.id_guru " +
                        // "WHERE j.id_jadwal = ?";
                        // try (PreparedStatement pstmtMaster = conn.prepareStatement(sqlGetMaster)) {
                        // pstmtMaster.setInt(1, idJadwal);
                        // ResultSet rsM = pstmtMaster.executeQuery();
                        // if (rsM.next()) {
                        // idMapel = rsM.getInt("id_mapel");
                        // idJenjang = rsM.getInt("id_jenjang");
                        // }
                        // }

                        // Insert Detail_Daftar_Les
                        String sqlInsertDetail = "INSERT INTO Detail_Daftar_Les (id_detail, id_les, id_jadwal, id_mapel, id_jenjang) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlInsertDetail)) {
                            pstmtDetail.setInt(1, newIdDetail);
                            pstmtDetail.setInt(2, newIdLes);
                            pstmtDetail.setInt(3, idJadwal);
                            pstmtDetail.setInt(4, idMapelReq); // Gunakan data dari React
                            pstmtDetail.setInt(5, idJenjangReq); // Gunakan data dari React
                            pstmtDetail.executeUpdate();
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
                    String query = exchange.getRequestURI().getQuery();
                    String idSiswaStr = query.split("id_siswa=")[1].split("&")[0];
                    int idSiswa = Integer.parseInt(idSiswaStr);

                    // tgl_mulai/selesai di Les adalah DATE, jam ada di Jadwal_Kesediaan_Guru
                    String sql = "SELECT l.id_les, l.id_siswa, " +
                            "CONVERT(varchar(10), l.tgl_mulai, 120) AS tgl_mulai, " +
                            "CONVERT(varchar(10), l.tgl_selesai, 120) AS tgl_selesai, " +
                            "ddl.id_jadwal, ddl.id_mapel, ddl.id_jenjang, " +
                            "j.hari, " +
                            "CONVERT(varchar(5), j.jam_mulai, 108) AS jam_mulai, " +
                            "CONVERT(varchar(5), j.jam_selesai, 108) AS jam_selesai " +
                            "FROM Les l " +
                            "JOIN Detail_Daftar_Les ddl ON l.id_les = ddl.id_les " +
                            "JOIN Jadwal_Kesediaan_Guru j ON ddl.id_jadwal = j.id_jadwal " +
                            "WHERE l.id_siswa = ?";

                    StringBuilder jsonResult = new StringBuilder("[");
                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                            PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, idSiswa);
                        ResultSet rs = pstmt.executeQuery();
                        boolean first = true;
                        while (rs.next()) {
                            if (!first)
                                jsonResult.append(",");
                            jsonResult.append("{")
                                    .append("\"id\":").append(rs.getInt("id_les")).append(",")
                                    .append("\"id_siswa\":").append(rs.getInt("id_siswa")).append(",")
                                    .append("\"id_jadwal\":").append(rs.getInt("id_jadwal")).append(",")
                                    .append("\"id_mapel\":").append(rs.getInt("id_mapel")).append(",")
                                    .append("\"id_jenjang\":").append(rs.getInt("id_jenjang")).append(",")
                                    .append("\"hari\":\"").append(escapeJson(rs.getString("hari"))).append("\",")
                                    .append("\"jam_mulai\":\"").append(rs.getString("jam_mulai")).append("\",")
                                    .append("\"jam_selesai\":\"").append(rs.getString("jam_selesai")).append("\",")
                                    // Format jadi ISO string supaya frontend bisa parse
                                    .append("\"tanggal_mulai\":\"").append(rs.getString("tgl_mulai")).append("T")
                                    .append(rs.getString("jam_mulai")).append("\",")
                                    .append("\"tanggal_selesai\":\"").append(rs.getString("tgl_selesai"))
                                    .append("T")
                                    .append(rs.getString("jam_selesai")).append("\"")
                                    .append("}");
                            first = false;
                        }
                    }
                    jsonResult.append("]");
                    kirimResponJSON(exchange, 200, jsonResult.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                    kirimResponJSON(exchange, 500, "[]");
                }
            }
        }
    }

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
        byte[] bytes = response.getBytes("utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static String escapeJson(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }
}