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
import java.sql.SQLException;
import java.util.stream.Collectors;

public class BackendServer {

    // Kredensial Azure SQL Server kamu
    private static final String URL = "jdbc:sqlserver://mibdlesprivat.database.windows.net:1433;database=MIBDLesPrivat;user=guguk@mibdlesprivat;password={your_password_here};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
    private static final String USER = "guguk";
    private static final String PASSWORD = "AIPastiWIN69";

    public static void main(String[] args) throws IOException {
        // 1. Menyalakan server Back-End di port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 2. Membuat "pintu gerbang" API dengan alamat /api/siswa
        server.createContext("/api/siswa", new SiswaHandler());

        server.setExecutor(null);
        System.out.println("Server Back-End Java jalan di: http://localhost:8080");
        server.start();

        // 2. TAMBAHKAN INI UNTUK TEST MANUAL
    System.out.println("Mencoba koneksi manual ke database...");
    try {
        SiswaHandler handler = new SiswaHandler();
        // Kamu bisa ganti data di bawah ini sesuai dengan kolom di database-mu
        handler.simpanKeDatabase(99, 1, "Test Manual", "2000-01-01 00:00:00", "L", "08123456789", "test@mail.com", "pass123", "Alamat Test");
        System.out.println("Koneksi sukses! Data berhasil disimpan secara manual.");
    } catch (Exception e) {
        System.err.println("Koneksi gagal! Pesan error: " + e.getMessage());
        e.printStackTrace();
    }
    }

    // Handler untuk memproses data yang dikirim oleh JavaScript
    // Handler untuk memproses data Siswa yang dikirim oleh React
    static class SiswaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String jsonInput = br.lines().collect(Collectors.joining());

                try {
                    // Cara sederhana ambil data dari JSON string
                    int idSiswa = Integer.parseInt(ambilNilaiJSON(jsonInput, "id_siswa").replaceAll("[^0-9]", ""));
                    int idJenjang = Integer.parseInt(ambilNilaiJSON(jsonInput, "id_jenjang").replaceAll("[^0-9]", ""));
                    String nama = ambilNilaiJSON(jsonInput, "nama");
                    String tglLahir = ambilNilaiJSON(jsonInput, "tanggal_lahir") + " 00:00:00";
                    String jenisKelamin = ambilNilaiJSON(jsonInput, "jenis_Kelamin");
                    String noHp = ambilNilaiJSON(jsonInput, "no_hp");
                    String email = ambilNilaiJSON(jsonInput, "email");
                    String pswrd = ambilNilaiJSON(jsonInput, "pswrd");
                    String alamat = ambilNilaiJSON(jsonInput, "alamat");

                    // Panggil fungsi eksekusi JDBC
                    simpanKeDatabase(idSiswa, idJenjang, nama, tglLahir, jenisKelamin, noHp, email, pswrd, alamat);

                    String response = "{\"status\":\"sukses\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    kirimError(exchange, "Gagal simpan ke database: " + e.getMessage());
                }
            }
        }

        // Fungsi pemotong teks JSON manual (karena kita ga pake library external)
        private String ambilNilaiJSON(String json, String key) {
            try {
                return json.split("\"" + key + "\":\\s*\"?")[1].split("[\",}]")[0].trim();
            } catch (Exception e) {
                return "";
            }
        }

        private void kirimError(HttpExchange exchange, String pesan) throws IOException {
            String response = "{\"status\":\"gagal\", \"pesan\":\"" + pesan + "\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        // Fungsi JDBC yang sudah di-update dengan kolom lengkap, termasuk alamat!
        private void simpanKeDatabase(int id, int jenjang, String nama, String tgl, String jk, String hp, String email,
                String pswrd, String alamat) throws SQLException {
            String sql = "INSERT INTO Siswa (id_siswa, id_jenjang, nama, tanggal_lahir, jenis_Kelamin, no_hp, email, pswrd, alamat) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, id);
                pstmt.setInt(2, jenjang);
                pstmt.setString(3, nama);
                pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(tgl));
                pstmt.setString(5, jk);
                pstmt.setString(6, hp);
                pstmt.setString(7, email);
                pstmt.setString(8, pswrd);
                pstmt.setString(9, alamat);

                pstmt.executeUpdate();
                System.out.println(">>> Data Siswa " + nama + " sukses masuk SQL Server beserta Alamat!");
            }
        }
    }
}