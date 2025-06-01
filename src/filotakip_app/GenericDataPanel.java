package filotakip_app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public abstract class GenericDataPanel extends JPanel {

    protected JTable table;
    protected DefaultTableModel model;

    protected JPanel ustPanel;   // Filtre, arama vb.
    protected JPanel sagPanel;   // İşlem butonları

    public GenericDataPanel() {
        setLayout(new BorderLayout(10,10));

        // 1. Üst panel - Filtre ve veri getirme alanları (abstract, subclass belirleyecek)
        ustPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(ustPanel, BorderLayout.NORTH);

        // 2. Orta panel - Tablo
        model = new DefaultTableModel();
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Sağ panel - İşlem butonları (abstract, subclass belirleyecek)
        sagPanel = new JPanel();
        sagPanel.setLayout(new BoxLayout(sagPanel, BoxLayout.Y_AXIS));
        add(sagPanel, BorderLayout.EAST);

        // Alt panel ya da form panel istersen eklenebilir ama tavsiyem ayrı panelde olsun.
    }

    // Subclasslar tablo kolonlarını ve butonları tanımlamalı
    protected abstract void setupTableColumns();

    protected abstract void setupFilters(); // üst panel elemanlarını burada yarat

    protected abstract void setupButtons(); // sağ panel butonlarını burada yarat

    // Verileri getirme metodu (her sınıf kendi sorgusunu override edebilir)
    protected abstract void verileriGetir();

}