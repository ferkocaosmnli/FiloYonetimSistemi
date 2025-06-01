package filotakip_app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class YakitKayitlariGoruntule extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JComboBox<AracItem> aracComboBox;
    private JTextField tarihField;
    private JTextField litreField;
    private JTextField tutarField;
    private JTextField yakitTuruField;

    // Seçili yakitID güncel veya sil işlemleri için tutulacak
    private Integer seciliYakitID = null;

    public YakitKayitlariGoruntule() {
        setLayout(new BorderLayout());

        // Tablo modeli
        model = new DefaultTableModel(new Object[]{
                "Yakıt ID", "Araç Plaka", "Tarih", "Litre", "Tutar", "Yakıt Türü"
        }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; // hücre düzenleme kapalı
            }
        };

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Sütun genişlikleri ayarı
        ayarlaSutunGenislikleri();

        // Form paneli oluştur ve ekle
        JPanel formPanel = formPanelOlustur();
        add(formPanel, BorderLayout.SOUTH);

        // Araçları yükle
        araclariYukle();

        // Tabloyu doldur
        verileriGetir();

        // Tablo satırı seçildiğinde form alanlarına verileri doldur
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                seciliYakitID = (Integer) model.getValueAt(row, 0);
                String plaka = (String) model.getValueAt(row, 1);
                String tarih = (String) model.getValueAt(row, 2);
                String litreStr = (String) model.getValueAt(row, 3);
                String tutarStr = (String) model.getValueAt(row, 4);
                String yakitTuru = (String) model.getValueAt(row, 5);

                // Araç ComboBox'ında seçili olan plaka ayarlanıyor
                for (int i = 0; i < aracComboBox.getItemCount(); i++) {
                    if (aracComboBox.getItemAt(i).toString().equals(plaka)) {
                        aracComboBox.setSelectedIndex(i);
                        break;
                    }
                }

                // Tarih alanı formatı: dd.MM.yyyy -> yyyy-MM-dd için çevir
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("dd.MM.yyyy");
                    SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date date = displayFormat.parse(tarih);
                    tarihField.setText(dbFormat.format(date));
                } catch (Exception ex) {
                    tarihField.setText("");
                }

                // Litre alanından " L" kısmını kaldır
                litreField.setText(litreStr.replace(" L", ""));

                // Tutar alanından para işaretini kaldır
                tutarField.setText(tutarStr.replaceAll("[^\\d,\\.]", "").replace(",", "."));

                yakitTuruField.setText(yakitTuru);
            }
        });
    }

    private void ayarlaSutunGenislikleri() {
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(70);  // Yakıt ID
        columnModel.getColumn(1).setPreferredWidth(100); // Plaka
        columnModel.getColumn(2).setPreferredWidth(120); // Tarih
        columnModel.getColumn(3).setPreferredWidth(80);  // Litre
        columnModel.getColumn(4).setPreferredWidth(100); // Tutar
        columnModel.getColumn(5).setPreferredWidth(100); // Yakıt Türü
        table.setRowHeight(25);
    }

    private JPanel formPanelOlustur() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Araç
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Araç:"), gbc);
        aracComboBox = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(aracComboBox, gbc);

        // Tarih
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tarih (yyyy-MM-dd):"), gbc);
        tarihField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(tarihField, gbc);

        // Litre
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Litre:"), gbc);
        litreField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(litreField, gbc);

        // Tutar
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Tutar:"), gbc);
        tutarField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(tutarField, gbc);

        // Yakıt Türü
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Yakıt Türü:"), gbc);
        yakitTuruField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 4;
        panel.add(yakitTuruField, gbc);

        // Buton paneli (Ekle, Güncelle, Sil, Oku, Temizle)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton ekleButton = new JButton("Ekle");
        JButton guncelleButton = new JButton("Güncelle");
        JButton silButton = new JButton("Sil");
        JButton okuButton = new JButton("Oku");
        JButton temizleButton = new JButton("Temizle");

        buttonPanel.add(ekleButton);
        buttonPanel.add(guncelleButton);
        buttonPanel.add(silButton);
        buttonPanel.add(okuButton);
        buttonPanel.add(temizleButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Buton eventleri
        ekleButton.addActionListener(this::yakitEkleAction);
        guncelleButton.addActionListener(this::yakitGuncelleAction);
        silButton.addActionListener(this::yakitSilAction);
        okuButton.addActionListener(e -> verileriGetir());
        temizleButton.addActionListener(e -> {
            temizleForm();
            table.clearSelection();
            seciliYakitID = null;
        });

        return panel;
    }

    private void yakitEkleAction(ActionEvent e) {
        AracItem seciliArac = (AracItem) aracComboBox.getSelectedItem();
        if (seciliArac == null) {
            JOptionPane.showMessageDialog(this, "Lütfen araç seçiniz!");
            return;
        }

        String tarih = tarihField.getText().trim();
        String litreStr = litreField.getText().trim();
        String tutarStr = tutarField.getText().trim();
        String yakitTuru = yakitTuruField.getText().trim();

        if (tarih.isEmpty() || litreStr.isEmpty() || tutarStr.isEmpty() || yakitTuru.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurunuz!");
            return;
        }

        try {
            float litre = Float.parseFloat(litreStr);
            BigDecimal tutar = new BigDecimal(tutarStr);

            String sorgu = "INSERT INTO YakitKayitlari (AracID, Tarih, Litre, Tutar, YakitTuru) VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sorgu)) {

                pstmt.setInt(1, seciliArac.getId());
                pstmt.setDate(2, java.sql.Date.valueOf(tarih));
                pstmt.setFloat(3, litre);
                pstmt.setBigDecimal(4, tutar);
                pstmt.setString(5, yakitTuru);

                int sonuc = pstmt.executeUpdate();

                if (sonuc > 0) {
                    JOptionPane.showMessageDialog(this, "Yakıt kaydı başarıyla eklendi.");
                    verileriGetir();
                    temizleForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Yakıt kaydı eklenemedi.");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Litre ve Tutar değerleri geçerli sayı olmalıdır!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Tarih yyyy-MM-dd formatında olmalıdır!");
        }
    }

    private void yakitGuncelleAction(ActionEvent e) {
        if (seciliYakitID == null) {
            JOptionPane.showMessageDialog(this, "Lütfen güncellemek için tablodan bir kayıt seçiniz!");
            return;
        }

        AracItem seciliArac = (AracItem) aracComboBox.getSelectedItem();
        if (seciliArac == null) {
            JOptionPane.showMessageDialog(this, "Lütfen araç seçiniz!");
            return;
        }

        String tarih = tarihField.getText().trim();
        String litreStr = litreField.getText().trim();
        String tutarStr = tutarField.getText().trim();
        String yakitTuru = yakitTuruField.getText().trim();

        if (tarih.isEmpty() || litreStr.isEmpty() || tutarStr.isEmpty() || yakitTuru.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurunuz!");
            return;
        }

        try {
            float litre = Float.parseFloat(litreStr);
            BigDecimal tutar = new BigDecimal(tutarStr);

            String sorgu = "UPDATE YakitKayitlari SET AracID=?, Tarih=?, Litre=?, Tutar=?, YakitTuru=? WHERE YakitID=?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sorgu)) {

                pstmt.setInt(1, seciliArac.getId());
                pstmt.setDate(2, java.sql.Date.valueOf(tarih));
                pstmt.setFloat(3, litre);
                pstmt.setBigDecimal(4, tutar);
                pstmt.setString(5, yakitTuru);
                pstmt.setInt(6, seciliYakitID);

                int sonuc = pstmt.executeUpdate();

                if (sonuc > 0) {
                    JOptionPane.showMessageDialog(this, "Yakıt kaydı başarıyla güncellendi.");
                    verileriGetir();
                    temizleForm();
                    seciliYakitID = null;
                    table.clearSelection();
                } else {
                    JOptionPane.showMessageDialog(this, "Yakıt kaydı güncellenemedi.");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Litre ve Tutar değerleri geçerli sayı olmalıdır!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Tarih yyyy-MM-dd formatında olmalıdır!");
        }
    }

    private void yakitSilAction(ActionEvent e) {
        if (seciliYakitID == null) {
            JOptionPane.showMessageDialog(this, "Lütfen silmek için tablodan bir kayıt seçiniz!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Seçili yakıt kaydını silmek istediğinize emin misiniz?",
                "Onay", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String sorgu = "DELETE FROM YakitKayitlari WHERE YakitID = ?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sorgu)) {

                pstmt.setInt(1, seciliYakitID);
                int sonuc = pstmt.executeUpdate();

                if (sonuc > 0) {
                    JOptionPane.showMessageDialog(this, "Yakıt kaydı başarıyla silindi.");
                    verileriGetir();
                    temizleForm();
                    seciliYakitID = null;
                    table.clearSelection();
                } else {
                    JOptionPane.showMessageDialog(this, "Yakıt kaydı silinemedi.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + ex.getMessage());
            }
        }
    }

    private void temizleForm() {
        aracComboBox.setSelectedIndex(-1);
        tarihField.setText("");
        litreField.setText("");
        tutarField.setText("");
        yakitTuruField.setText("");
    }

    public void araclariYukle() {
        aracComboBox.removeAllItems();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT AracID, Plaka FROM Araclar");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("AracID");
                String plaka = rs.getString("Plaka");
                aracComboBox.addItem(new AracItem(id, plaka));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        aracComboBox.setSelectedIndex(-1);
    }

    public void verileriGetir() {
        model.setRowCount(0);

        String sorgu = "SELECT Y.YakitID, A.Plaka, Y.Tarih, Y.Litre, Y.Tutar, Y.YakitTuru " +
                "FROM YakitKayitlari Y " +
                "JOIN Araclar A ON Y.AracID = A.AracID " +
                "ORDER BY Y.Tarih DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sorgu);
             ResultSet rs = pstmt.executeQuery()) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            NumberFormat litreFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
            NumberFormat tutarFormat = NumberFormat.getCurrencyInstance(new Locale("tr", "TR"));

            while (rs.next()) {
                int yakitID = rs.getInt("YakitID");
                String plaka = rs.getString("Plaka");
                Date tarih = rs.getDate("Tarih");
                float litre = rs.getFloat("Litre");
                BigDecimal tutar = rs.getBigDecimal("Tutar");
                String yakitTuru = rs.getString("YakitTuru");

                model.addRow(new Object[]{
                        yakitID,
                        plaka,
                        sdf.format(tarih),
                        litreFormat.format(litre) + " L",
                        tutarFormat.format(tutar),
                        yakitTuru
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Araç için toString metodunu plaka göstermek için override edilen yardımcı sınıf
    private static class AracItem {
        private final int id;
        private final String plaka;

        public AracItem(int id, String plaka) {
            this.id = id;
            this.plaka = plaka;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return plaka;
        }
    }
}