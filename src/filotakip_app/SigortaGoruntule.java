package filotakip_app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SigortaGoruntule extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JComboBox<AracItem> aracComboBox;
    private JTextField baslangicTarihiField;
    private JTextField bitisTarihiField;
    private JTextField sigortaSirketiField;
    private JTextField policeNoField;

    private JButton kaydetButton;
    private JButton guncelleButton;
    private JButton silButton;
    private JButton temizleButton;

    // Güncelleme ve silme için seçili SigortaID tutulacak
    private Integer seciliSigortaID = null;

    public SigortaGoruntule() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel();
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Sütunlar
        model.addColumn("Sigorta ID");
        model.addColumn("Araç Plaka");
        model.addColumn("Başlangıç Tarihi");
        model.addColumn("Bitiş Tarihi");
        model.addColumn("Sigorta Şirketi");
        model.addColumn("Poliçe No");

        add(scrollPane, BorderLayout.CENTER);

        // Form paneli
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Araç seçimi
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Araç:"), gbc);

        aracComboBox = new JComboBox<>();
        araclariYukle();
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(aracComboBox, gbc);

        // Başlangıç Tarihi
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Başlangıç Tarihi (yyyy-MM-dd):"), gbc);

        baslangicTarihiField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(baslangicTarihiField, gbc);

        // Bitiş Tarihi
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Bitiş Tarihi (yyyy-MM-dd):"), gbc);

        bitisTarihiField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(bitisTarihiField, gbc);

        // Sigorta Şirketi
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Sigorta Şirketi:"), gbc);

        sigortaSirketiField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(sigortaSirketiField, gbc);

        // Poliçe No
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Poliçe No:"), gbc);

        policeNoField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(policeNoField, gbc);

        // Butonlar paneli
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        kaydetButton = new JButton("Yeni Kayıt Ekle");
        guncelleButton = new JButton("Güncelle");
        silButton = new JButton("Sil");
        temizleButton = new JButton("Temizle");

        btnPanel.add(kaydetButton);
        btnPanel.add(guncelleButton);
        btnPanel.add(silButton);
        btnPanel.add(temizleButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);

        // Buton eventleri
        kaydetButton.addActionListener(e -> sigortaEkle());
        guncelleButton.addActionListener(e -> sigortaGuncelle());
        silButton.addActionListener(e -> sigortaSil());
        temizleButton.addActionListener(e -> temizleForm());

        // Tablo satır seçme -> formu doldur
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int secili = table.getSelectedRow();
                if (secili != -1) {
                    seciliSigortaID = (Integer) model.getValueAt(secili, 0);
                    String plaka = (String) model.getValueAt(secili, 1);
                    Date baslangic = (Date) model.getValueAt(secili, 2);
                    Date bitis = (Date) model.getValueAt(secili, 3);
                    String sirket = (String) model.getValueAt(secili, 4);
                    String policeNo = (String) model.getValueAt(secili, 5);

                    // Araç combobox'ında plaka eşleşen öğeyi seç
                    for (int i = 0; i < aracComboBox.getItemCount(); i++) {
                        if (aracComboBox.getItemAt(i).toString().equals(plaka)) {
                            aracComboBox.setSelectedIndex(i);
                            break;
                        }
                    }

                    baslangicTarihiField.setText(baslangic.toString());
                    bitisTarihiField.setText(bitis.toString());
                    sigortaSirketiField.setText(sirket);
                    policeNoField.setText(policeNo);
                }
            }
        });

        verileriGetir();
    }

    private void araclariYukle() {
        aracComboBox.removeAllItems();
        String sorgu = "SELECT AracID, Plaka FROM Araclar";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sorgu)) {

            while (rs.next()) {
                int id = rs.getInt("AracID");
                String plaka = rs.getString("Plaka");
                aracComboBox.addItem(new AracItem(id, plaka));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Araçlar yüklenemedi: " + e.getMessage());
        }
    }

    private void sigortaEkle() {
        AracItem seciliArac = (AracItem) aracComboBox.getSelectedItem();
        if (seciliArac == null) {
            JOptionPane.showMessageDialog(this, "Lütfen araç seçiniz!");
            return;
        }

        String baslangic = baslangicTarihiField.getText().trim();
        String bitis = bitisTarihiField.getText().trim();
        String sirket = sigortaSirketiField.getText().trim();
        String policeNo = policeNoField.getText().trim();

        if (baslangic.isEmpty() || bitis.isEmpty() || sirket.isEmpty() || policeNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurunuz!");
            return;
        }

        try {
            java.sql.Date.valueOf(baslangic);
            java.sql.Date.valueOf(bitis);

            String sorgu = "INSERT INTO Sigorta (AracID, BaslangicTarihi, BitisTarihi, SigortaSirketi, PoliceNo) " +
                           "VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sorgu)) {

                pstmt.setInt(1, seciliArac.getId());
                pstmt.setDate(2, java.sql.Date.valueOf(baslangic));
                pstmt.setDate(3, java.sql.Date.valueOf(bitis));
                pstmt.setString(4, sirket);
                pstmt.setString(5, policeNo);

                int sonuc = pstmt.executeUpdate();

                if (sonuc > 0) {
                    JOptionPane.showMessageDialog(this, "Sigorta kaydı başarıyla eklendi.");
                    verileriGetir();
                    temizleForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Sigorta kaydı eklenemedi.");
                }
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Tarihleri 'yyyy-MM-dd' formatında giriniz!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + ex.getMessage());
        }
    }

    private void sigortaGuncelle() {
        if (seciliSigortaID == null) {
            JOptionPane.showMessageDialog(this, "Lütfen güncellenecek kaydı tablodan seçiniz!");
            return;
        }

        AracItem seciliArac = (AracItem) aracComboBox.getSelectedItem();
        if (seciliArac == null) {
            JOptionPane.showMessageDialog(this, "Lütfen araç seçiniz!");
            return;
        }

        String baslangic = baslangicTarihiField.getText().trim();
        String bitis = bitisTarihiField.getText().trim();
        String sirket = sigortaSirketiField.getText().trim();
        String policeNo = policeNoField.getText().trim();

        if (baslangic.isEmpty() || bitis.isEmpty() || sirket.isEmpty() || policeNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurunuz!");
            return;
        }

        try {
            java.sql.Date.valueOf(baslangic);
            java.sql.Date.valueOf(bitis);

            String sorgu = "UPDATE Sigorta SET AracID=?, BaslangicTarihi=?, BitisTarihi=?, SigortaSirketi=?, PoliceNo=? WHERE SigortaID=?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sorgu)) {

                pstmt.setInt(1, seciliArac.getId());
                pstmt.setDate(2, java.sql.Date.valueOf(baslangic));
                pstmt.setDate(3, java.sql.Date.valueOf(bitis));
                pstmt.setString(4, sirket);
                pstmt.setString(5, policeNo);
                pstmt.setInt(6, seciliSigortaID);

                int sonuc = pstmt.executeUpdate();

                if (sonuc > 0) {
                    JOptionPane.showMessageDialog(this, "Sigorta kaydı başarıyla güncellendi.");
                    verileriGetir();
                    temizleForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Sigorta kaydı güncellenemedi.");
                }
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Tarihleri 'yyyy-MM-dd' formatında giriniz!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + ex.getMessage());
        }
    }

    private void sigortaSil() {
        if (seciliSigortaID == null) {
            JOptionPane.showMessageDialog(this, "Lütfen silinecek kaydı tablodan seçiniz!");
            return;
        }

        int cevap = JOptionPane.showConfirmDialog(this, "Seçili sigorta kaydını silmek istediğinize emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION);
        if (cevap != JOptionPane.YES_OPTION) {
            return;
        }

        String sorgu = "DELETE FROM Sigorta WHERE SigortaID=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sorgu)) {

            pstmt.setInt(1, seciliSigortaID);
            int sonuc = pstmt.executeUpdate();

            if (sonuc > 0) {
                JOptionPane.showMessageDialog(this, "Sigorta kaydı başarıyla silindi.");
                verileriGetir();
                temizleForm();
            } else {
                JOptionPane.showMessageDialog(this, "Sigorta kaydı silinemedi.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + ex.getMessage());
        }
    }

    private void temizleForm() {
        seciliSigortaID = null;
        aracComboBox.setSelectedIndex(-1);
        baslangicTarihiField.setText("");
        bitisTarihiField.setText("");
        sigortaSirketiField.setText("");
        policeNoField.setText("");
        table.clearSelection();
    }

    private void verileriGetir() {
        model.setRowCount(0);
        String sorgu = "SELECT s.SigortaID, a.Plaka, s.BaslangicTarihi, s.BitisTarihi, s.SigortaSirketi, s.PoliceNo " +
                       "FROM Sigorta s JOIN Araclar a ON s.AracID = a.AracID";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sorgu)) {

            while (rs.next()) {
                int sigortaID = rs.getInt("SigortaID");
                String plaka = rs.getString("Plaka");
                Date baslangic = rs.getDate("BaslangicTarihi");
                Date bitis = rs.getDate("BitisTarihi");
                String sirket = rs.getString("SigortaSirketi");
                String policeNo = rs.getString("PoliceNo");

                model.addRow(new Object[] {sigortaID, plaka, baslangic, bitis, sirket, policeNo});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Veriler getirilemedi: " + e.getMessage());
        }
    }

    // Araç öğesi için sınıf
    private static class AracItem {
        private int id;
        private String plaka;

        public AracItem(int id, String plaka) {
            this.id = id;
            this.plaka = plaka;
        }

        public int getId() {
            return id;
        }

        public String toString() {
            return plaka;
        }
    }
}