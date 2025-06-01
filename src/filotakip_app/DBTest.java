package filotakip_app;
import java.sql.Connection;

public class DBTest {
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("✅ Veritabanı bağlantısı başarılı!");
                conn.close();
            }
        } catch (Exception e) {
            System.out.println("❌ Bağlantı hatası: " + e.getMessage());
        }
    }
}