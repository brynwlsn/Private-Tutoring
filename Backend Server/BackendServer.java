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
        server.createContext("/api/jadwal/add", new PostJadwalHandler());

        // Admin Endpoints
        server.createContext("/api/admin/stats", new AdminStatsHandler());
        server.createContext("/api/admin/students", new AdminStudentsHandler());
        server.createContext("/api/admin/teachers", new AdminTeachersHandler());
        server.createContext("/api/admin/admins", new AdminAdminsHandler());
        server.createContext("/api/admin/schedules", new AdminScheduleHandler());
        server.createContext("/api/admin/schedules/update", new UpdateScheduleHandler());
        server.createContext("/api/admin/schedules/delete", new DeleteScheduleHandler());
        server.createContext("/api/admin/options", new AdminOptionsHandler()); // Untuk dropdown edit
        server.createContext("/api/admin/teachers/delete", new DeleteTeacherHandler());
        server.createContext("/api/admin/teachers/update", new UpdateTeacherHandler());
        server.createContext("/api/admin/students/delete", new DeleteStudentHandler());
        server.createContext("/api/admin/students/update", new UpdateStudentHandler());
        server.createContext("/api/admin/admins/delete", new DeleteAdminHandler());
        server.createContext("/api/admin/admins/update", new UpdateAdminHandler());
        server.createContext("/api/admin/slots", new AdminSlotGuruHandler());
        server.createContext("/api/admin/slots/update", new AdminUpdateSlotHandler());
        server.createContext("/api/admin/slots/delete", new AdminDeleteSlotHandler());
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
                        "CASE WHEN COUNT(d.id_jadwal) > 0 THEN 'filled' ELSE 'available' END AS status " +
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

    // 1. Handler Statistik Dashboard Admin
    static class AdminStatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String sql = "SELECT " +
                        "(SELECT COUNT(*) FROM Siswa) as total_siswa, " +
                        "(SELECT COUNT(*) FROM Guru) as total_guru, " +
                        "(SELECT COUNT(*) FROM Admin) as total_admin, " +
                        "(SELECT COUNT(*) FROM Detail_Daftar_Les) as total_les";
                try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String json = String.format(
                                "{\"total_siswa\":%d,\"total_guru\":%d,\"total_admin\":%d,\"total_les\":%d}",
                                rs.getInt("total_siswa"), rs.getInt("total_guru"), rs.getInt("total_admin"),
                                rs.getInt("total_les"));
                        kirimResponJSON(exchange, 200, json);
                    }
                }
            } catch (Exception e) {
                kirimResponJSON(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    // 2. Handler Ambil Data Siswa
    static class AdminStudentsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                    PreparedStatement ps = conn.prepareStatement("SELECT id_siswa, nama, email, no_hp FROM Siswa");
                    ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder("[");
                while (rs.next()) {
                    if (sb.length() > 1)
                        sb.append(",");
                    sb.append(String.format("{\"id\":%d,\"nama\":\"%s\",\"email\":\"%s\",\"no_hp\":\"%s\"}",
                            rs.getInt("id_siswa"), escapeJson(rs.getString("nama")), escapeJson(rs.getString("email")),
                            escapeJson(rs.getString("no_hp"))));
                }
                sb.append("]");
                kirimResponJSON(exchange, 200, sb.toString());
            } catch (Exception e) {
                kirimResponJSON(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    // 3. Handler Ambil Data Guru
    static class AdminTeachersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                    PreparedStatement ps = conn.prepareStatement("SELECT id_guru, nama, email, no_hp FROM Guru");
                    ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder("[");
                while (rs.next()) {
                    if (sb.length() > 1)
                        sb.append(",");
                    sb.append(String.format("{\"id\":%d,\"nama\":\"%s\",\"email\":\"%s\",\"no_hp\":\"%s\"}",
                            rs.getInt("id_guru"), escapeJson(rs.getString("nama")), escapeJson(rs.getString("email")),
                            escapeJson(rs.getString("no_hp"))));
                }
                sb.append("]");
                kirimResponJSON(exchange, 200, sb.toString());
            } catch (Exception e) {
                kirimResponJSON(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    // 4. Handler Ambil Data Admin
    static class AdminAdminsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                    PreparedStatement ps = conn.prepareStatement("SELECT id_admin, nama, email, no_hp FROM Admin");
                    ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder("[");
                while (rs.next()) {
                    if (sb.length() > 1)
                        sb.append(",");
                    sb.append(String.format("{\"id\":%d,\"nama\":\"%s\",\"email\":\"%s\",\"no_hp\":\"%s\"}",
                            rs.getInt("id_admin"), escapeJson(rs.getString("nama")), escapeJson(rs.getString("email")),
                            escapeJson(rs.getString("no_hp"))));
                }
                sb.append("]");
                kirimResponJSON(exchange, 200, sb.toString());
            } catch (Exception e) {
                kirimResponJSON(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    // 5. Handler Ambil Semua Jadwal Les Aktif (Manage Schedule)
    static class AdminScheduleHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aturCORS(exchange);
            // Query ini mengambil semua slot, baik yang sudah ada les atau belum
            String sql = "SELECT j.id_jadwal, j.hari, j.jam_mulai, j.jam_selesai, g.nama AS nama_guru, " +
                    "s.nama AS nama_siswa, m.nama_mapel, jn.nama_jenjang, d.id_detail " +
                    "FROM Jadwal_Kesediaan_Guru j " +
                    "JOIN Guru g ON j.id_guru = g.id_guru " +
                    "LEFT JOIN Detail_Daftar_Les d ON j.id_jadwal = d.id_jadwal " +
                    "LEFT JOIN Les l ON d.id_les = l.id_les " +
                    "LEFT JOIN Siswa s ON l.id_siswa = s.id_siswa " +
                    "LEFT JOIN Mata_pelajaran m ON d.id_mapel = m.id_mapel " +
                    "LEFT JOIN Jenjang jn ON d.id_jenjang = jn.id_jenjang";
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder("[");
                while (rs.next()) {
                    if (sb.length() > 1)
                        sb.append(",");
                    sb.append(String.format(
                            "{\"id_detail\":%d,\"id_les\":%d,\"id_jadwal\":%d,\"id_mapel\":%d,\"id_jenjang\":%d," +
                                    "\"nama_siswa\":\"%s\",\"nama_guru\":\"%s\",\"nama_mapel\":\"%s\",\"nama_jenjang\":\"%s\","
                                    +
                                    "\"hari\":\"%s\",\"jam_mulai\":\"%s\",\"jam_selesai\":\"%s\"}",
                            rs.getInt("id_detail"), rs.getInt("id_les"), rs.getInt("id_jadwal"), rs.getInt("id_mapel"),
                            rs.getInt("id_jenjang"),
                            escapeJson(rs.getString("nama_siswa")), escapeJson(rs.getString("nama_guru")),
                            escapeJson(rs.getString("nama_mapel")),
                            escapeJson(rs.getString("nama_jenjang")), escapeJson(rs.getString("hari")),
                            rs.getString("jam_mulai"), rs.getString("jam_selesai")));
                }
                sb.append("]");
                kirimResponJSON(exchange, 200, sb.toString());
            } catch (Exception e) {
                kirimResponJSON(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    // 6. Handler Update Jadwal Les oleh Admin
    static class UpdateScheduleHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aturCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                String body = bacaBody(exchange);
                int idDetail = Integer.parseInt(ambilNilaiJSON(body, "id_detail"));
                int idJadwal = Integer.parseInt(ambilNilaiJSON(body, "id_jadwal")); // Jika ada
                int idMapel = Integer.parseInt(ambilNilaiJSON(body, "id_mapel"));
                int idJenjang = Integer.parseInt(ambilNilaiJSON(body, "id_jenjang"));

                // Tambahkan ini: waktu manual
                String hari = ambilNilaiJSON(body, "hari");
                String jamMulai = ambilNilaiJSON(body, "jam_mulai");
                String jamSelesai = ambilNilaiJSON(body, "jam_selesai");

                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    // Update detail les (mapel/jenjang)
                    String sqlDetail = "UPDATE Detail_Daftar_Les SET id_mapel = ?, id_jenjang = ? WHERE id_detail = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlDetail)) {
                        ps.setInt(1, idMapel);
                        ps.setInt(2, idJenjang);
                        ps.setInt(3, idDetail);
                        ps.executeUpdate();
                    }

                    // Update waktu di tabel Jadwal_Kesediaan_Guru
                    String sqlJadwal = "UPDATE Jadwal_Kesediaan_Guru SET hari = ?, jam_mulai = ?, jam_selesai = ? WHERE id_jadwal = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlJadwal)) {
                        ps.setString(1, hari);
                        ps.setString(2, jamMulai);
                        ps.setString(3, jamSelesai);
                        ps.setInt(4, idJadwal);
                        ps.executeUpdate();
                    }

                    kirimResponJSON(exchange, 200, "{\"message\":\"Jadwal berhasil diatur ulang!\"}");
                }
            } catch (Exception e) {
                kirimResponJSON(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    // 7. Handler Hapus Jadwal Les oleh Admin
    static class DeleteScheduleHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody())).lines()
                        .collect(Collectors.joining("\n"));

                // PERBAIKAN DI SINI: getJsonValue diganti menjadi ambilNilaiJSON
                int idDetail = Integer.parseInt(ambilNilaiJSON(body, "id_detail"));

                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                        PreparedStatement ps = conn
                                .prepareStatement("DELETE FROM Detail_Daftar_Les WHERE id_detail = ?")) {
                    ps.setInt(1, idDetail);
                    ps.executeUpdate();
                    kirimResponJSON(exchange, 200, "{\"message\":\"Jadwal les berhasil dihapus!\"}");
                }
            } catch (Exception e) {
                kirimResponJSON(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String jsonInput = bacaBody(exchange);
                int idDetail = Integer.parseInt(ambilNilaiJSON(jsonInput, "id_detail"));

                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    // CEK: Apakah jadwal ini sedang dipakai dalam les aktif?
                    String sqlCek = "SELECT COUNT(*) FROM Detail_Daftar_Les WHERE id_detail = ? AND id_les IS NOT NULL";
                    // Atau sesuaikan dengan logic tabelmu jika id_les tersimpan di sana

                    PreparedStatement psCek = conn.prepareStatement(sqlCek);
                    psCek.setInt(1, idDetail);
                    ResultSet rs = psCek.executeQuery();

                    if (rs.next() && rs.getInt(1) > 0) {
                        kirimResponJSON(exchange, 400,
                                "{\"message\":\"Tidak bisa hapus! Jadwal ini sudah dipesan oleh siswa.\"}");
                        return;
                    }

                    // Jika aman, baru hapus
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM Detail_Daftar_Les WHERE id_detail = ?");
                    ps.setInt(1, idDetail);
                    ps.executeUpdate();
                    kirimResponJSON(exchange, 200, "{\"message\":\"Jadwal berhasil dihapus!\"}");
                } catch (Exception e) {
                    kirimResponJSON(exchange, 500, "{\"message\":\"Error database.\"}");
                }
            }
        }
    }

    // 8. Handler Pilihan Dropdown Terintegrasi untuk Keperluan Edit Form
    static class AdminOptionsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                // Ambil Mapel
                StringBuilder mapelSb = new StringBuilder("[");
                try (PreparedStatement ps = conn.prepareStatement("SELECT id_mapel, nama_mapel FROM Mata_pelajaran");
                        ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        if (mapelSb.length() > 1)
                            mapelSb.append(",");
                        mapelSb.append(String.format("{\"id\":%d,\"nama\":\"%s\"}", rs.getInt("id_mapel"),
                                escapeJson(rs.getString("nama_mapel"))));
                    }
                }
                mapelSb.append("]");

                // Ambil Jenjang
                StringBuilder jenjangSb = new StringBuilder("[");
                try (PreparedStatement ps = conn.prepareStatement("SELECT id_jenjang, nama_jenjang FROM Jenjang");
                        ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        if (jenjangSb.length() > 1)
                            jenjangSb.append(",");
                        jenjangSb.append(String.format("{\"id\":%d,\"nama\":\"%s\"}", rs.getInt("id_jenjang"),
                                escapeJson(rs.getString("nama_jenjang"))));
                    }
                }
                jenjangSb.append("]");

                // Ambil Seluruh Jadwal Slot Guru yang Tersedia
                StringBuilder jadwalSb = new StringBuilder("[");
                String jSql = "SELECT j.id_jadwal, j.hari, j.jam_mulai, j.jam_selesai, g.nama FROM Jadwal_Kesediaan_Guru j JOIN Guru g ON j.id_guru = g.id_guru";
                try (PreparedStatement ps = conn.prepareStatement(jSql); ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        if (jadwalSb.length() > 1)
                            jadwalSb.append(",");
                        jadwalSb.append(String.format(
                                "{\"id_jadwal\":%d,\"hari\":\"%s\",\"jam_mulai\":\"%s\",\"jam_selesai\":\"%s\",\"nama_guru\":\"%s\"}",
                                rs.getInt("id_jadwal"), rs.getString("hari"), rs.getString("jam_mulai"),
                                rs.getString("jam_selesai"), escapeJson(rs.getString("nama"))));
                    }
                }
                jadwalSb.append("]");

                String fullJson = String.format("{\"mapel\":%s,\"jenjang\":%s,\"jadwal_guru\":%s}", mapelSb.toString(),
                        jenjangSb.toString(), jadwalSb.toString());
                kirimResponJSON(exchange, 200, fullJson);
            } catch (Exception e) {
                kirimResponJSON(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
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
                                        "{\"status\":\"gagal\", \"pesan\":\"Slot jadwal ini sudah filled pada rentang tanggal tersebut!\"}");
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

    static class PostJadwalHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aturCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String jsonInput = bacaBody(exchange);
                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    int idJadwal = Integer.parseInt(ambilNilaiJSON(jsonInput, "id"));
                    String hari = ambilNilaiJSON(jsonInput, "hari");
                    String jamMulai = ambilNilaiJSON(jsonInput, "jam_mulai");
                    String jamSelesai = ambilNilaiJSON(jsonInput, "jam_selesai");
                    int idGuru = Integer.parseInt(ambilNilaiJSON(jsonInput, "id_guru"));

                    // id_admin diset 1 (default) karena ini diinput langsung oleh guru
                    String sql = "INSERT INTO Jadwal_Kesediaan_Guru (id_jadwal, hari, jam_mulai, jam_selesai, id_guru, id_admin) VALUES (?, ?, ?, ?, ?, 1)";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, idJadwal);
                    ps.setString(2, hari);
                    ps.setString(3, jamMulai);
                    ps.setString(4, jamSelesai);
                    ps.setInt(5, idGuru);
                    ps.executeUpdate();

                    kirimResponJSON(exchange, 200, "{\"status\":\"sukses\"}");
                } catch (Exception e) {
                    e.printStackTrace();
                    kirimResponJSON(exchange, 400, "{\"status\":\"gagal\", \"pesan\":\"" + e.getMessage() + "\"}");
                }
            }
        }
    }

    static class DeleteTeacherHandler implements HttpHandler {
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
                    int idGuru = Integer.parseInt(ambilNilaiJSON(jsonInput, "id_guru"));

                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        // 1. Hapus keahlian guru tersebut
                        try (PreparedStatement ps = conn
                                .prepareStatement("DELETE FROM Keahlian_Guru WHERE id_guru = ?")) {
                            ps.setInt(1, idGuru);
                            ps.executeUpdate();
                        }

                        // 2. Hapus jadwal kosong guru tersebut
                        try (PreparedStatement ps = conn
                                .prepareStatement("DELETE FROM Jadwal_Kesediaan_Guru WHERE id_guru = ?")) {
                            ps.setInt(1, idGuru);
                            ps.executeUpdate();
                        }

                        // 3. Hapus data guru utama
                        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Guru WHERE id_guru = ?")) {
                            ps.setInt(1, idGuru);
                            ps.executeUpdate();
                        }

                        kirimResponJSON(exchange, 200,
                                "{\"status\":\"sukses\", \"message\":\"Teacher successfully removed from Database!\"}");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String errMsg = e.getMessage();
                    // Cegah penghapusan jika guru sudah punya murid yang les (Mencegah SQL Crash)
                    if (errMsg.contains("REFERENCE") || errMsg.contains("FOREIGN KEY")) {
                        kirimResponJSON(exchange, 400,
                                "{\"status\":\"gagal\", \"pesan\":\"Cannot delete: This teacher is still tied to the student's tutoring history.\"}");
                    } else {
                        kirimResponJSON(exchange, 500,
                                "{\"status\":\"gagal\", \"pesan\":\"A server error occurred.\"}");
                    }
                }
            }
        }
    }

    // Handler Update Guru oleh Admin
    static class UpdateTeacherHandler implements HttpHandler {
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
                    int idGuru = Integer.parseInt(ambilNilaiJSON(jsonInput, "id_guru"));
                    String nama = ambilNilaiJSON(jsonInput, "nama");
                    String email = ambilNilaiJSON(jsonInput, "email");
                    String noHp = ambilNilaiJSON(jsonInput, "no_hp");
                    String expertisesStr = ambilNilaiJSON(jsonInput, "expertises");

                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        // 1. Update Data Utama Guru
                        String sqlUpdateGuru = "UPDATE Guru SET nama = ?, email = ?, no_hp = ? WHERE id_guru = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sqlUpdateGuru)) {
                            ps.setString(1, nama);
                            ps.setString(2, email);
                            ps.setString(3, noHp);
                            ps.setInt(4, idGuru);
                            ps.executeUpdate();
                        }

                        // 2. Hapus Semua Keahlian Lama
                        String sqlDeleteKeahlian = "DELETE FROM Keahlian_Guru WHERE id_guru = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sqlDeleteKeahlian)) {
                            ps.setInt(1, idGuru);
                            ps.executeUpdate();
                        }

                        // 3. Masukkan Keahlian Baru (jika ada)
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
                                        pstmtKeahlian.setInt(2, idGuru);
                                        pstmtKeahlian.setInt(3, Integer.parseInt(parts[0]));
                                        pstmtKeahlian.setInt(4, Integer.parseInt(parts[1]));
                                        pstmtKeahlian.executeUpdate();
                                        counter++;
                                    }
                                }
                            }
                        }
                        kirimResponJSON(exchange, 200,
                                "{\"status\":\"sukses\", \"message\":\"Teacher data has been updated successfully!\"}");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    kirimResponJSON(exchange, 500,
                            "{\"status\":\"gagal\", \"message\":\"Error: " + escapeJson(e.getMessage()) + "\"}");
                }
            }
        }
    }

    // Handler Hapus Siswa oleh Admin
    static class DeleteStudentHandler implements HttpHandler {
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

                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        String sql = "DELETE FROM Siswa WHERE id_siswa = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setInt(1, idSiswa);
                            ps.executeUpdate();
                        }
                        kirimResponJSON(exchange, 200,
                                "{\"status\":\"sukses\", \"message\":\"Student successfully removed from Database!\"}");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String errMsg = e.getMessage();
                    // Pencegahan jika siswa masih punya riwayat booking les
                    if (errMsg.contains("REFERENCE") || errMsg.contains("FOREIGN KEY")) {
                        kirimResponJSON(exchange, 400,
                                "{\"status\":\"gagal\", \"pesan\":\"Cannot delete: This student has an active tutoring schedule or history.\"}");
                    } else {
                        kirimResponJSON(exchange, 500,
                                "{\"status\":\"gagal\", \"pesan\":\"A server error occurred.\"}");
                    }
                }
            }
        }
    }

    // Handler Update Siswa oleh Admin
    static class UpdateStudentHandler implements HttpHandler {
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
                    String nama = ambilNilaiJSON(jsonInput, "nama");
                    String email = ambilNilaiJSON(jsonInput, "email");
                    String noHp = ambilNilaiJSON(jsonInput, "no_hp");
                    String idJenjangStr = ambilNilaiJSON(jsonInput, "id_jenjang");
                    String tglLahir = ambilNilaiJSON(jsonInput, "tanggal_lahir");
                    String jenisKelamin = ambilNilaiJSON(jsonInput, "jenis_kelamin");

                    int idJenjang = idJenjangStr.isEmpty() ? 1 : Integer.parseInt(idJenjangStr);

                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        String sql = "UPDATE Siswa SET nama = ?, email = ?, no_hp = ?, id_jenjang = ?, tgl_lahir = ?, jenis_kelamin = ? WHERE id_siswa = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setString(1, nama);
                            ps.setString(2, email);
                            ps.setString(3, noHp);
                            ps.setInt(4, idJenjang);

                            java.sql.Date sqlDate = null;
                            try {
                                if (tglLahir.contains("-")) {
                                    sqlDate = java.sql.Date.valueOf(tglLahir);
                                }
                            } catch (Exception dateEx) {
                                sqlDate = java.sql.Date.valueOf("2000-01-01");
                            }

                            ps.setDate(5, sqlDate);
                            ps.setString(6, jenisKelamin);
                            ps.setInt(7, idSiswa);
                            ps.executeUpdate();
                        }
                        kirimResponJSON(exchange, 200,
                                "{\"status\":\"sukses\", \"message\":\"Student Data successfully updated!\"}");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    kirimResponJSON(exchange, 500,
                            "{\"status\":\"gagal\", \"message\":\"Error: " + escapeJson(e.getMessage()) + "\"}");
                }
            }
        }
    }

    // Handler Hapus Admin
    static class DeleteAdminHandler implements HttpHandler {
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
                    int idAdmin = Integer.parseInt(ambilNilaiJSON(jsonInput, "id_admin"));

                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        String sql = "DELETE FROM Admin WHERE id_admin = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setInt(1, idAdmin);
                            ps.executeUpdate();
                        }
                        kirimResponJSON(exchange, 200,
                                "{\"status\":\"sukses\", \"message\":\"Admin successfully removed from Database!\"}");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String errMsg = e.getMessage();
                    if (errMsg.contains("REFERENCE") || errMsg.contains("FOREIGN KEY")) {
                        kirimResponJSON(exchange, 400,
                                "{\"status\":\"gagal\", \"pesan\":\"Cannot delete: This admin is still bound to schedule or teacher data.\"}");
                    } else {
                        kirimResponJSON(exchange, 500,
                                "{\"status\":\"gagal\", \"pesan\":\"A server error occurred.\"}");
                    }
                }
            }
        }
    }

    // Handler Update Admin
    static class UpdateAdminHandler implements HttpHandler {
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
                    int idAdmin = Integer.parseInt(ambilNilaiJSON(jsonInput, "id_admin"));
                    String nama = ambilNilaiJSON(jsonInput, "nama");
                    String email = ambilNilaiJSON(jsonInput, "email");
                    String noHp = ambilNilaiJSON(jsonInput, "no_hp");

                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        String sql = "UPDATE Admin SET nama = ?, email = ?, no_hp = ? WHERE id_admin = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setString(1, nama);
                            ps.setString(2, email);
                            ps.setString(3, noHp);
                            ps.setInt(4, idAdmin);
                            ps.executeUpdate();
                        }
                        kirimResponJSON(exchange, 200,
                                "{\"status\":\"sukses\", \"message\":\"Admin data successfully updated!\"}");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    kirimResponJSON(exchange, 500,
                            "{\"status\":\"gagal\", \"message\":\"Error: " + escapeJson(e.getMessage()) + "\"}");
                }
            }
        }
    }

    // Tambahkan Handler ini untuk mengambil semua slot ketersediaan guru
    static class AdminSlotGuruHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aturCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String sql = "SELECT j.id_jadwal, j.hari, j.jam_mulai, j.jam_selesai, j.id_guru, g.nama AS nama_guru, " +
                    "CASE WHEN l.id_les IS NOT NULL THEN 1 ELSE 0 END AS is_booked " +
                    "FROM Jadwal_Kesediaan_Guru j " +
                    "JOIN Guru g ON j.id_guru = g.id_guru " +
                    "LEFT JOIN Detail_Daftar_Les d ON j.id_jadwal = d.id_jadwal " +
                    "LEFT JOIN Les l ON d.id_les = l.id_les";

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder("[");
                while (rs.next()) {
                    if (sb.length() > 1)
                        sb.append(",");
                    // PASTIKAN NAMA KUNCI JSON INI HURUF KECIL DAN SESUAI:
                    sb.append(String.format(
                            "{\"id_jadwal\":%d,\"hari\":\"%s\",\"jam_mulai\":\"%s\",\"jam_selesai\":\"%s\",\"id_guru\":%d,\"nama_guru\":\"%s\",\"is_booked\":%d}",
                            rs.getInt("id_jadwal"), rs.getString("hari"), rs.getString("jam_mulai"),
                            rs.getString("jam_selesai"), rs.getInt("id_guru"), escapeJson(rs.getString("nama_guru")),
                            rs.getInt("is_booked")));
                }
                sb.append("]");
                kirimResponJSON(exchange, 200, sb.toString());
            } catch (Exception e) {
                kirimResponJSON(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    // Handler Update Slot Guru oleh Admin
    static class AdminUpdateSlotHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aturCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = bacaBody(exchange);
                try {
                    int idJadwal = Integer.parseInt(ambilNilaiJSON(body, "id_jadwal"));
                    String hari = ambilNilaiJSON(body, "hari");
                    String jamMulai = ambilNilaiJSON(body, "jam_mulai");
                    String jamSelesai = ambilNilaiJSON(body, "jam_selesai");

                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        String sql = "UPDATE Jadwal_Kesediaan_Guru SET hari = ?, jam_mulai = ?, jam_selesai = ? WHERE id_jadwal = ?";
                        PreparedStatement ps = conn.prepareStatement(sql);
                        ps.setString(1, hari);
                        ps.setString(2, jamMulai);
                        ps.setString(3, jamSelesai);
                        ps.setInt(4, idJadwal);
                        ps.executeUpdate();
                        kirimResponJSON(exchange, 200,
                                "{\"status\":\"sukses\",\"message\":\"Time slot changed successfully!\"}");
                    }
                } catch (Exception e) {
                    kirimResponJSON(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
                }
            }
        }
    }

    // Handler Hapus Slot Guru oleh Admin
    static class AdminDeleteSlotHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            aturCORS(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = bacaBody(exchange);
                try {
                    int idJadwal = Integer.parseInt(ambilNilaiJSON(body, "id_jadwal"));
                    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                        String sql = "DELETE FROM Jadwal_Kesediaan_Guru WHERE id_jadwal = ?";
                        PreparedStatement ps = conn.prepareStatement(sql);
                        ps.setInt(1, idJadwal);
                        ps.executeUpdate();
                        kirimResponJSON(exchange, 200,
                                "{\"status\":\"sukses\",\"message\":\"Time slot successfully deleted!\"}");
                    }
                } catch (Exception e) {
                    kirimResponJSON(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
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