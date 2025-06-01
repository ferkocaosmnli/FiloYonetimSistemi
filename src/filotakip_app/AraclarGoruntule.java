package filotakip_app;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AraclarGoruntule extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField tfSasiNo, tfMarka, tfModel, tfYil, tfPlaka, tfVitesTuru, tfYakit, tfKilometre, tfDurum;
    private JButton btnEkle, btnGuncelle, btnSil, btnTemizle;

    public AraclarGoruntule() {
        setLayout(new BorderLayout());

        // Tablo ve model
        model = new DefaultTableModel();
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        model.addColumn("ID");
        model.addColumn("Şasi No");
        model.addColumn("Marka");
        model.addColumn("Model");
        model.addColumn("Yıl");
        model.addColumn("Plaka");
        model.addColumn("Vites Türü");
        model.addColumn("Yakıt");
        model.addColumn("Kilometre");
        model.addColumn("Durum");

        add(scrollPane, BorderLayout.CENTER);

        // Form paneli
        JPanel formPanel = new JPanel(new GridLayout(2, 10, 5, 5));

        tfSasiNo = new JTextField();
        tfMarka = new JTextField();
        tfModel = new JTextField();
        tfYil = new JTextField();
        tfPlaka = new JTextField();
        tfVitesTuru = new JTextField();
        tfYakit = new JTextField();
        tfKilometre = new JTextField();
        tfDurum = new JTextField();

        // Form etiketleri
        formPanel.add(new JLabel("Şasi No:"));
        formPanel.add(new JLabel("Marka:"));
        formPanel.add(new JLabel("Model:"));
        formPanel.add(new JLabel("Yıl:"));
        formPanel.add(new JLabel("Plaka:"));
        formPanel.add(new JLabel("Vites Türü:"));
        formPanel.add(new JLabel("Yakıt:"));
        formPanel.add(new JLabel("Kilometre:"));
        formPanel.add(new JLabel("Durum:"));
        formPanel.add(new JLabel("")); // boş hücre

        // Form alanları
        formPanel.add(tfSasiNo);
        formPanel.add(tfMarka);
        formPanel.add(tfModel);
        formPanel.add(tfYil);
        formPanel.add(tfPlaka);
        formPanel.add(tfVitesTuru);
        formPanel.add(tfYakit);
        formPanel.add(tfKilometre);
        formPanel.add(tfDurum);
        formPanel.add(new JLabel("")); // boş hücre

        add(formPanel, BorderLayout.NORTH);

        // Buton paneli
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        btnEkle = new JButton("Araç Ekle");
        btnGuncelle = new JButton("Araç Güncelle");
        btnSil = new JButton("Araç Sil");
        btnTemizle = new JButton("Temizle");

        buttonPanel.add(btnEkle);
        buttonPanel.add(btnGuncelle);
        buttonPanel.add(btnSil);
        buttonPanel.add(btnTemizle);

        add(buttonPanel, BorderLayout.EAST);

        // Buton işlemleri
        btnEkle.addActionListener(e -> aracEkle());
        btnGuncelle.addActionListener(e -> aracGuncelle());
        btnSil.addActionListener(e -> aracSil());
        btnTemizle.addActionListener(e -> temizle());

        // Tablo satır seçildiğinde verileri forma aktar
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();

                tfSasiNo.setText(model.getValueAt(row, 1) != null ? model.getValueAt(row, 1).toString() : "");
                tfMarka.setText(model.getValueAt(row, 2) != null ? model.getValueAt(row, 2).toString() : "");
                tfModel.setText(model.getValueAt(row, 3) != null ? model.getValueAt(row, 3).toString() : "");
                tfYil.setText(model.getValueAt(row, 4) != null ? model.getValueAt(row, 4).toString() : "");
                tfPlaka.setText(model.getValueAt(row, 5) != null ? model.getValueAt(row, 5).toString() : "");
                tfVitesTuru.setText(model.getValueAt(row, 6) != null ? model.getValueAt(row, 6).toString() : "");
                tfYakit.setText(model.getValueAt(row, 7) != null ? model.getValueAt(row, 7).toString() : "");
                tfKilometre.setText(model.getValueAt(row, 8) != null ? model.getValueAt(row, 8).toString() : "");
                tfDurum.setText(model.getValueAt(row, 9) != null ? model.getValueAt(row, 9).toString() : "");
            }
        });

        // Verileri yükle
        verileriGetir();
    }

    private void verileriGetir() {
        String sorgu = "SELECT * FROM Araclar";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sorgu)) {

            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("AracID"),
                        rs.getString("SasiNo"),
                        rs.getString("Marka"),
                        rs.getString("Model"),
                        rs.getInt("Yil"),
                        rs.getString("Plaka"),
                        rs.getString("VitesTuru"),
                        rs.getString("Yakit"),
                        rs.getInt("Kilometre"),
                        rs.getString("Durum")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Araç verileri çekilemedi: " + e.getMessage());
        }
    }

    private void aracEkle() {
        if (!formKontrol()) return;

        try {
            String sql = "INSERT INTO Araclar (SasiNo, Marka, Model, Yil, Plaka, VitesTuru, Yakit, Kilometre, Durum) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, tfSasiNo.getText());
                pstmt.setString(2, tfMarka.getText());
                pstmt.setString(3, tfModel.getText());
                pstmt.setInt(4, Integer.parseInt(tfYil.getText()));
                pstmt.setString(5, tfPlaka.getText());
                pstmt.setString(6, tfVitesTuru.getText());
                pstmt.setString(7, tfYakit.getText());
                pstmt.setInt(8, Integer.parseInt(tfKilometre.getText()));
                pstmt.setString(9, tfDurum.getText());

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Araç başarıyla eklendi.");
                verileriGetir();
                temizle();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Araç eklenirken hata oluştu: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Yıl ve Kilometre alanlarına sadece sayı giriniz.");
        }
    }

    private void aracGuncelle() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen güncellemek için bir araç seçin.");
            return;
        }

        if (!formKontrol()) return;

        try {
            int aracID = (int) model.getValueAt(selectedRow, 0);

            String sql = "UPDATE Araclar SET SasiNo=?, Marka=?, Model=?, Yil=?, Plaka=?, VitesTuru=?, Yakit=?, Kilometre=?, Durum=? WHERE AracID=?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, tfSasiNo.getText());
                pstmt.setString(2, tfMarka.getText());
                pstmt.setString(3, tfModel.getText());
                pstmt.setInt(4, Integer.parseInt(tfYil.getText()));
                pstmt.setString(5, tfPlaka.getText());
                pstmt.setString(6, tfVitesTuru.getText());
                pstmt.setString(7, tfYakit.getText());
                pstmt.setInt(8, Integer.parseInt(tfKilometre.getText()));
                pstmt.setString(9, tfDurum.getText());
                pstmt.setInt(10, aracID);

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Araç başarıyla güncellendi.");
                verileriGetir();
                temizle();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Araç güncellenirken hata oluştu: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Yıl ve Kilometre alanlarına sadece sayı giriniz.");
        }
    }

    private void aracSil() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen silmek için bir araç seçin.");
            return;
        }

        int aracID = (int) model.getValueAt(selectedRow, 0);

        int cevap = JOptionPane.showConfirmDialog(this, "Seçili aracı silmek istediğinize emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION);
        if (cevap != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM Araclar WHERE AracID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, aracID);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Araç başarıyla silindi.");
            verileriGetir();
            temizle();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Araç silinirken hata oluştu: " + e.getMessage());
        }
    }

    private void temizle() {
        tfSasiNo.setText("");
        tfMarka.setText("");
        tfModel.setText("");
        tfYil.setText("");
        tfPlaka.setText("");
        tfVitesTuru.setText("");
        tfYakit.setText("");
        tfKilometre.setText("");
        tfDurum.setText("");
        table.clearSelection();
    }

    // Formdaki alanların dolu ve doğru formatta olup olmadığını kontrol et
    private boolean formKontrol() {
        if (tfSasiNo.getText().isEmpty() || tfMarka.getText().isEmpty() || tfModel.getText().isEmpty() || tfYil.getText().isEmpty() ||
                tfPlaka.getText().isEmpty() || tfVitesTuru.getText().isEmpty() || tfYakit.getText().isEmpty() ||
                tfKilometre.getText().isEmpty() || tfDurum.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.");
            return false;
        }

        try {
            Integer.parseInt(tfYil.getText());
            Integer.parseInt(tfKilometre.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Yıl ve Kilometre alanlarına sadece sayı giriniz.");
            return false;
        }

        return true;
    }
}