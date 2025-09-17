# FiloYönetimSistemi

**FiloYönetimSistemi**, Java ve MySQL kullanılarak geliştirilmiş bir masaüstü uygulamasıdır.  
Uygulama, araç filosu yönetimi için tasarlanmıştır; araçları, sürücüleri, bakımları ve görevleri takip etmenizi sağlar.

---

## Özellikler

- **Araç Yönetimi**
  - Araç ekleme, düzenleme ve silme.
  - Araç bilgileri: plaka, marka, model, yıl, durum ve notlar.

- **Sürücü Yönetimi**
  - Sürücü ekleme, düzenleme ve silme.
  - Sürücü bilgileri: isim, soyisim, telefon, ehliyet bilgisi ve notlar.

- **Görev / Sefer Takibi**
  - Araç ve sürücüye atanan görevler oluşturulabilir.
  - Görev detayları: tarih, saat, açıklama, durum (tamamlandı / planlandı / iptal).
  - Görev ekleme, düzenleme ve silme işlemleri.

- **Bakım / Servis Takibi**
  - Araçların bakım ve servis kayıtları tutulur.
  - Bakım türü, tarih, yapılan işlemler ve notlar kaydedilir.

- **Raporlama ve Görselleştirme**
  - Araçların, sürücülerin ve görevlerin listesi tablolar halinde görüntülenir.
  - Filtreleme ve arama özellikleri ile kolay yönetim.

- **Veritabanı Entegrasyonu**
  - MySQL kullanılarak veri saklama, güncelleme ve silme işlemleri yapılır.
  - DAO pattern kullanılarak kod yapısı düzenli tutulmuştur.

---

## Kullanılan Teknolojiler

- Java 11+
- Java Swing (GUI)
- MySQL (veritabanı)
- JDBC (DB bağlantısı)
- MVC / DAO tasarım deseni

---

## Kurulum

1. MySQL’de `filo_takip` veritabanını oluşturun.
2. `DBConnection` sınıfında kullanıcı adı ve şifreyi kendi veritabanınıza göre düzenleyin.
3. IDE’de projeyi açın ve ana sınıfı (`FiloYonetimApp` veya benzeri) çalıştırın.
4. Uygulama açıldığında araç, sürücü ve görev ekleyerek sistemi kullanabilirsiniz.
---

## Proje Sahibi

Bu proje **Ferhat Kocaosmanlı** tarafından geliştirilmiştir.
