package filotakip_app;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class RezervasyonlarGoruntule extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JComboBox<String> aracBox, calisanBox;
    private JTextField baslangicTarihiField, bitisTarihiField, aciklamaField, durumField;
    private JButton ekleButton, guncelleButton, silButton, temizleButton;

    public RezervasyonlarGoruntule() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel();
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        model.addColumn("Rezervasyon ID");
        model.addColumn("Araç Plaka");
        model.addColumn("Çalışan Adı");
        model.addColumn("Başlangıç Tarihi");
        model.addColumn("Bitiş Tarihi");
        model.addColumn("Açıklama");
        model.addColumn("Durum");

        JPanel formPanel = new JPanel(new GridLayout(2, 7, 5, 5));
        aracBox = new JComboBox<>();
        calisanBox = new JComboBox<>();
        baslangicTarihiField = new JTextField();
        bitisTarihiField = new JTextField();
        aciklamaField = new JTextField();
        durumField = new JTextField();

        ekleButton = new JButton("Ekle");
        guncelleButton = new JButton("Güncelle");
        silButton = new JButton("Sil");
        temizleButton = new JButton("Temizle");

        formPanel.add(new JLabel("Araç"));
        formPanel.add(new JLabel("Çalışan"));
        formPanel.add(new JLabel("Başlangıç (YYYY-MM-DD HH:MM:SS)"));
        formPanel.add(new JLabel("Bitiş (YYYY-MM-DD HH:MM:SS)"));
        formPanel.add(new JLabel("Açıklama"));
        formPanel.add(new JLabel("Durum"));
        formPanel.add(new JLabel(""));

        formPanel.add(aracBox);
        formPanel.add(calisanBox);
        formPanel.add(baslangicTarihiField);
        formPanel.add(bitisTarihiField);
        formPanel.add(aciklamaField);
        formPanel.add(durumField);
        formPanel.add(new JLabel(""));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(ekleButton);
        buttonPanel.add(guncelleButton);
        buttonPanel.add(silButton);
        buttonPanel.add(temizleButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        araclariYukle();
        calisanlariYukle();
        verileriGetir();

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                tabloSatiriniFormaYukle(table.getSelectedRow());
            }
        });

        ekleButton.addActionListener(e -> rezervasyonEkle());
        guncelleButton.addActionListener(e -> rezervasyonGuncelle());
        silButton.addActionListener(e -> rezervasyonSil());
        temizleButton.addActionListener(e -> formuTemizle());
    }

    private void verileriGetir() {
        String sorgu = "SELECT r.RezervasyonID, a.Plaka, CONCAT(c.Isim, ' ', c.Soyisim) AS CalisanAdi, " +
                "r.BaslangicTarihi, r.BitisTarihi, r.Aciklama, r.Durum " +
                "FROM Rezervasyonlar r " +
                "JOIN Araclar a ON r.AracID = a.AracID " +
                "JOIN Calisanlar c ON r.CalisanID = c.CalisanID";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sorgu)) {

            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("RezervasyonID"),
                        rs.getString("Plaka"),
                        rs.getString("CalisanAdi"),
                        rs.getTimestamp("BaslangicTarihi"),
                        rs.getTimestamp("BitisTarihi"),
                        rs.getString("Aciklama"),
                        rs.getString("Durum")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Rezervasyon verileri çekilemedi: " + e.getMessage());
        }
    }

    private void araclariYukle() {
        aracBox.removeAllItems();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT AracID, Plaka FROM Araclar")) {
            while (rs.next()) {
                aracBox.addItem(rs.getInt("AracID") + " - " + rs.getString("Plaka"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Araçlar yüklenemedi: " + e.getMessage());
        }
    }

    private void calisanlariYukle() {
        calisanBox.removeAllItems();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT CalisanID, Isim, Soyisim FROM Calisanlar")) {
            while (rs.next()) {
                calisanBox.addItem(rs.getInt("CalisanID") + " - " + rs.getString("Isim") + " " + rs.getString("Soyisim"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Çalışanlar yüklenemedi: " + e.getMessage());
        }
    }

    private void tabloSatiriniFormaYukle(int row) {
        aracBox.setSelectedItem(getComboBoxItemByPrefix(aracBox, getAracIDByPlaka((String) model.getValueAt(row, 1))));
        calisanBox.setSelectedItem(getComboBoxItemByPrefix(calisanBox, getCalisanIDByAd((String) model.getValueAt(row, 2))));
        baslangicTarihiField.setText(model.getValueAt(row, 3).toString());
        bitisTarihiField.setText(model.getValueAt(row, 4).toString());
        aciklamaField.setText(model.getValueAt(row, 5).toString());
        durumField.setText(model.getValueAt(row, 6).toString());
    }

    private String getComboBoxItemByPrefix(JComboBox<String> box, int id) {
        for (int i = 0; i < box.getItemCount(); i++) {
            String item = box.getItemAt(i);
            if (item.startsWith(id + " - ")) {
                return item;
            }
        }
        return null;
    }

    private int getAracIDByPlaka(String plaka) {
        for (int i = 0; i < aracBox.getItemCount(); i++) {
            String item = aracBox.getItemAt(i);
            if (item.endsWith(plaka)) {
                return Integer.parseInt(item.split(" - ")[0]);
            }
        }
        return -1;
    }

    private int getCalisanIDByAd(String calisanAdi) {
        for (int i = 0; i < calisanBox.getItemCount(); i++) {
            String item = calisanBox.getItemAt(i);
            if (item.endsWith(calisanAdi)) {
                return Integer.parseInt(item.split(" - ")[0]);
            }
        }
        return -1;
    }

    private void rezervasyonEkle() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO Rezervasyonlar (AracID, CalisanID, BaslangicTarihi, BitisTarihi, Aciklama, Durum) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                int aracID = Integer.parseInt(aracBox.getSelectedItem().toString().split(" - ")[0]);
                int calisanID = Integer.parseInt(calisanBox.getSelectedItem().toString().split(" - ")[0]);

                pstmt.setInt(1, aracID);
                pstmt.setInt(2, calisanID);
                pstmt.setTimestamp(3, Timestamp.valueOf(baslangicTarihiField.getText()));
                pstmt.setTimestamp(4, Timestamp.valueOf(bitisTarihiField.getText()));
                pstmt.setString(5, aciklamaField.getText());
                pstmt.setString(6, durumField.getText());

                pstmt.executeUpdate();
                verileriGetir();
                formuTemizle();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Rezervasyon eklenemedi: " + e.getMessage());
        }
    }

    private void rezervasyonGuncelle() {
        int seciliSatir = table.getSelectedRow();
        if (seciliSatir == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen güncellenecek satırı seçin.");
            return;
        }
        int rezervasyonID = (int) model.getValueAt(seciliSatir, 0);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE Rezervasyonlar SET AracID=?, CalisanID=?, BaslangicTarihi=?, BitisTarihi=?, Aciklama=?, Durum=? WHERE RezervasyonID=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                int aracID = Integer.parseInt(aracBox.getSelectedItem().toString().split(" - ")[0]);
                int calisanID = Integer.parseInt(calisanBox.getSelectedItem().toString().split(" - ")[0]);

                pstmt.setInt(1, aracID);
                pstmt.setInt(2, calisanID);
                pstmt.setTimestamp(3, Timestamp.valueOf(baslangicTarihiField.getText()));
                pstmt.setTimestamp(4, Timestamp.valueOf(bitisTarihiField.getText()));
                pstmt.setString(5, aciklamaField.getText());
                pstmt.setString(6, durumField.getText());
                pstmt.setInt(7, rezervasyonID);

                pstmt.executeUpdate();
                verileriGetir();
                formuTemizle();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Rezervasyon güncellenemedi: " + e.getMessage());
        }
    }

    private void rezervasyonSil() {
        int seciliSatir = table.getSelectedRow();
        if (seciliSatir == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen silinecek satırı seçin.");
            return;
        }
        int rezervasyonID = (int) model.getValueAt(seciliSatir, 0);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Rezervasyonlar WHERE RezervasyonID=?")) {
            pstmt.setInt(1, rezervasyonID);
            pstmt.executeUpdate();
            verileriGetir();
            formuTemizle();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Rezervasyon silinemedi: " + e.getMessage());
        }
    }

    private void formuTemizle() {
        aracBox.setSelectedIndex(0);
        calisanBox.setSelectedIndex(0);
        baslangicTarihiField.setText("");
        bitisTarihiField.setText("");
        aciklamaField.setText("");
        durumField.setText("");
        table.clearSelection();
    }
}
