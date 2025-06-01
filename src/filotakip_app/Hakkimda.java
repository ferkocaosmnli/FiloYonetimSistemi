package filotakip_app;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Hakkimda extends JPanel {

    public Hakkimda() {
        setBackground(new Color(245, 245, 245));  // Açık gri arka plan
        setLayout(new GridBagLayout());  // Ortalamak için GridBagLayout kullandık

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(700, 350));  // Kart boyutunu büyüttük
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)  // İç boşlukları artırdık
        ));
        card.setLayout(new BorderLayout(30, 0)); // 30 px yatay boşluk (fotoğraf ve yazı arası)

        // Fotoğraf paneli - yuvarlak maskeli fotoğraf
        JLabel fotoLabel = new JLabel();
        fotoLabel.setPreferredSize(new Dimension(250, 250));  // Fotoğrafı büyüttük
        ImageIcon icon = createRoundedImageIcon("/filotakip_app/ferhat2.jpg", 250);
        if (icon != null) {
            fotoLabel.setIcon(icon);
        } else {
            fotoLabel.setText("Fotoğraf bulunamadı");
            fotoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            fotoLabel.setVerticalAlignment(SwingConstants.CENTER);
            fotoLabel.setForeground(Color.RED);
        }
        fotoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30)); // Fotoğraf ve yazı arası boşluk büyütüldü
        card.add(fotoLabel, BorderLayout.WEST);

        // Yazı paneli
        JPanel yazilar = new JPanel();
        yazilar.setBackground(Color.WHITE);
        yazilar.setLayout(new BoxLayout(yazilar, BoxLayout.Y_AXIS));
        yazilar.setAlignmentY(Component.TOP_ALIGNMENT);

        JLabel baslik = new JLabel("Ferhat Kocaosmanli");
        baslik.setFont(new Font("Segoe UI", Font.BOLD, 30)); // Başlık fontunu büyüttük
        baslik.setForeground(new Color(33, 33, 33));
        baslik.setAlignmentX(Component.LEFT_ALIGNMENT);
        yazilar.add(baslik);

        yazilar.add(Box.createRigidArea(new Dimension(0, 20))); // Başlık ve metin arası boşluk arttı

        JTextArea bilgi = new JTextArea(
                "Bu program Veritabani Dersi'nin Projesi için geliştirilmiştir.\n\n" +
                "Bilgisayar Mühendisliği, Bartın Üniversitesi\n" +
                "Teşekkür ederiz."
        );
        bilgi.setFont(new Font("Segoe UI", Font.PLAIN, 20));  // Metin fontunu büyüttük
        bilgi.setForeground(new Color(60, 60, 60));
        bilgi.setBackground(Color.WHITE);
        bilgi.setEditable(false);
        bilgi.setLineWrap(true);
        bilgi.setWrapStyleWord(true);
        bilgi.setOpaque(false);
        bilgi.setAlignmentX(Component.LEFT_ALIGNMENT);
        bilgi.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // Metne daha geniş padding

        yazilar.add(bilgi);

        card.add(yazilar, BorderLayout.CENTER);

        add(card);
    }

    // Yuvarlak kesim yapan metod
    private ImageIcon createRoundedImageIcon(String path, int diameter) {
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL == null) {
                System.err.println("Dosya bulunamadı: " + path);
                return null;
            }
            ImageIcon icon = new ImageIcon(imgURL);
            Image original = icon.getImage();

            int width = icon.getIconWidth();
            int height = icon.getIconHeight();
            int size = Math.min(width, height);

            BufferedImage cropped = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = cropped.createGraphics();
            g2.drawImage(original, 0, 0, size, size,
                    (width - size) / 2, (height - size) / 2,
                    (width + size) / 2, (height + size) / 2, null);
            g2.dispose();

            BufferedImage mask = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            g2 = mask.createGraphics();
            g2.setClip(new Ellipse2D.Float(0, 0, size, size));
            g2.drawImage(cropped, 0, 0, null);
            g2.dispose();

            Image scaled = mask.getScaledInstance(diameter, diameter, Image.SCALE_SMOOTH);

            return new ImageIcon(scaled);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}