package filotakip_app;

import javax.swing.*;

public class AnaPencere extends JFrame {

    public AnaPencere() {
        setTitle("Filo Takip Yönetim Sistemi");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabPane = new JTabbedPane();

        // CalisanlarGoruntule JPanel olduğu için direkt ekle
        CalisanlarGoruntule calisanlarPanel = new CalisanlarGoruntule();
        tabPane.addTab("Çalışanlar", calisanlarPanel);

        // AraclarGoruntule JPanel olduğu için direkt ekle
        AraclarGoruntule araclarPanel = new AraclarGoruntule();
        tabPane.addTab("Araçlar", araclarPanel);

        RezervasyonlarGoruntule rezervasyonlarPanel = new RezervasyonlarGoruntule();
        tabPane.addTab("Rezervasyonlar", rezervasyonlarPanel);
        
        // AnaPencere constructor'unda:
        YakitKayitlariGoruntule yakitKayitlariPanel = new YakitKayitlariGoruntule();
        tabPane.addTab("Yakıt Kayıtları", yakitKayitlariPanel);
        
        SigortaGoruntule sigortaPanel = new SigortaGoruntule();
        tabPane.addTab("Sigortalar", sigortaPanel);
        
        Hakkimda hakkimdaPanel = new Hakkimda();
        tabPane.addTab("Hakkımda", hakkimdaPanel);  

        add(tabPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AnaPencere().setVisible(true);
        });
    }
}