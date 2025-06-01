package filotakip_app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CalisanlarGoruntule extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField tfIsim, tfSoyisim, tfTelefon, tfEposta, tfRol;
    private JButton btnEkle, btnGuncelle, btnSil;

    private int seciliCalisanID = -1;

    public CalisanlarGoruntule() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"ID", "İsim", "Soyisim", "Telefon", "Eposta", "Rol"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        tfIsim = new JTextField(10);
        tfSoyisim = new JTextField(10);
        tfTelefon = new JTextField(10);
        tfEposta = new JTextField(10);
        tfRol = new JTextField(10);

        btnEkle = new JButton("Personel Ekle");
        btnGuncelle = new JButton("Personel Güncelle");
        btnSil = new JButton("Personel Sil");

        gbc.gridy = 0;
        gbc.gridx = 0; formPanel.add(new JLabel("İsim:"), gbc);
        gbc.gridx = 1; formPanel.add(new JLabel("Soyisim:"), gbc);
        gbc.gridx = 2; formPanel.add(new JLabel("Telefon:"), gbc);
        gbc.gridx = 3; formPanel.add(new JLabel("Eposta:"), gbc);
        gbc.gridx = 4; formPanel.add(new JLabel("Rol:"), gbc);

        gbc.gridy = 1;
        gbc.gridx = 0; formPanel.add(tfIsim, gbc);
        gbc.gridx = 1; formPanel.add(tfSoyisim, gbc);
        gbc.gridx = 2; formPanel.add(tfTelefon, gbc);
        gbc.gridx = 3; formPanel.add(tfEposta, gbc);
        gbc.gridx = 4; formPanel.add(tfRol, gbc);

        gbc.gridy = 2;
        gbc.gridx = 1; formPanel.add(btnEkle, gbc);
        gbc.gridx = 2; formPanel.add(btnGuncelle, gbc);
        gbc.gridx = 3; formPanel.add(btnSil, gbc);

        add(formPanel, BorderLayout.SOUTH);

        btnEkle.addActionListener(e -> personelEkle());
        btnGuncelle.addActionListener(e -> personelGuncelle());
        btnSil.addActionListener(e -> personelSil());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                seciliCalisanID = (int) model.getValueAt(row, 0);
                tfIsim.setText((String) model.getValueAt(row, 1));
                tfSoyisim.setText((String) model.getValueAt(row, 2));
                tfTelefon.setText((String) model.getValueAt(row, 3));
                tfEposta.setText((String) model.getValueAt(row, 4));
                tfRol.setText((String) model.getValueAt(row, 5));
            }
        });

        verileriGetir();
    }

    private void verileriGetir() {
        model.setRowCount(0);

        String sql = "CALL CalisanlariListele()";  // Eğer böyle bir procedure varsa, yoksa SELECT * yapabilirsin.

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("CalisanID"),
                        rs.getString("Isim"),
                        rs.getString("Soyisim"),
                        rs.getString("Telefon"),
                        rs.getString("Eposta"),
                        rs.getString("Rol")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Veri çekme hatası: " + e.getMessage());
        }
    }

    private void personelEkle() {
        String isim = tfIsim.getText().trim();
        String soyisim = tfSoyisim.getText().trim();
        String telefon = tfTelefon.getText().trim();
        String eposta = tfEposta.getText().trim();
        String rol = tfRol.getText().trim();

        if (isim.isEmpty() || soyisim.isEmpty() || telefon.isEmpty() || eposta.isEmpty() || rol.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.");
            return;
        }

        String sql = "{CALL CalisanEkle(?, ?, ?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, isim);
            cs.setString(2, soyisim);
            cs.setString(3, telefon);
            cs.setString(4, eposta);
            cs.setString(5, rol);

            cs.executeUpdate();

            JOptionPane.showMessageDialog(this, "Personel eklendi.");
            verileriGetir();
            alanlariTemizle();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ekleme hatası: " + e.getMessage());
        }
    }

    private void personelGuncelle() {
        if (seciliCalisanID == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen güncellemek için bir personel seçin.");
            return;
        }

        String isim = tfIsim.getText().trim();
        String soyisim = tfSoyisim.getText().trim();
        String telefon = tfTelefon.getText().trim();
        String eposta = tfEposta.getText().trim();
        String rol = tfRol.getText().trim();

        if (isim.isEmpty() || soyisim.isEmpty() || telefon.isEmpty() || eposta.isEmpty() || rol.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.");
            return;
        }

        String sql = "{CALL CalisanGuncelle(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, seciliCalisanID);
            cs.setString(2, isim);
            cs.setString(3, soyisim);
            cs.setString(4, telefon);
            cs.setString(5, eposta);
            cs.setString(6, rol);

            int updatedRows = cs.executeUpdate();
            if (updatedRows > 0) {
                JOptionPane.showMessageDialog(this, "Personel güncellendi.");
                verileriGetir();
                alanlariTemizle();
                seciliCalisanID = -1;
            } else {
                JOptionPane.showMessageDialog(this, "Güncelleme başarısız.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Güncelleme hatası: " + e.getMessage());
        }
    }

    private void personelSil() {
        if (seciliCalisanID == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen silmek için bir personel seçin.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Seçili personeli silmek istediğinize emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "{CALL CalisanSil(?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, seciliCalisanID);

            int deletedRows = cs.executeUpdate();
            if (deletedRows > 0) {
                JOptionPane.showMessageDialog(this, "Personel silindi.");
                verileriGetir();
                alanlariTemizle();
                seciliCalisanID = -1;
            } else {
                JOptionPane.showMessageDialog(this, "Silme işlemi başarısız.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Silme hatası: " + e.getMessage());
        }
    }

    private void alanlariTemizle() {
        tfIsim.setText("");
        tfSoyisim.setText("");
        tfTelefon.setText("");
        tfEposta.setText("");
        tfRol.setText("");
    }
}