(() => {
    "use strict";

    const MANUAL_LANGUAGE_KEY = "demo1.language";
    const AUTO_LANGUAGE_KEY = "demo1.auto-language";
    const SUPPORTED_LANGUAGES = new Set(["tr", "en"]);
    const ATTRIBUTE_NAMES = ["placeholder", "aria-label", "title", "alt"];
    const originalTextValues = new WeakMap();
    const originalAttributeValues = new WeakMap();
    const originalDocumentTitle = document.title;

    const EN_TRANSLATIONS = {
    "Ana Sayfa": "Home",
    "Ekibimiz": "Team",
    "Ekip": "Team",
    "Projeler": "Projects",
    "Proje": "Project",
    "Yayınlar": "Publications",
    "Yayın": "Publication",
    "Haberler": "News",
    "Haber": "News",
    "İletişim": "Contact",
    "Sistem ve Ağ Araştırma Laboratuvarı": "Systems and Networks Research Laboratory",
    "Ondokuz Mayıs Üniversitesi - Bilgisayar Mühendisliği": "Ondokuz Mayıs University - Computer Engineering",
    "Ondokuz Mayıs Üniversitesi": "Ondokuz Mayıs University",
    "Bilgisayar Mühendisliği Bölümü": "Department of Computer Engineering",
    "Ana sayfaya dön": "Return to home page",
    "Ana navigasyon": "Main navigation",
    "Laboratuvar içerik başlıkları": "Laboratory content categories",
    "← Ana siteye dön": "← Return to public site",
    "← Ekibimize Dön": "← Return to Team",
    "Siteyi Aç": "Open Site",
    "Çıkış Yap": "Sign Out",
    "Araştırma, geliştirme, akademisyen ve öğrenci katkılarını tek noktada keşfedin.": "Discover research, development, academician and student contributions in one place.",
    "Sistem ve Ağ Araştırma Laboratuvarı ekibini, güncel projeleri, akademik yayınları ve laboratuvar duyurularını inceleyin.": "Explore the Systems and Networks Research Laboratory team, current projects, academic publications and laboratory announcements.",
    "Akademisyen ve öğrenci katkıları": "Academician and student contributions",
    "Aktif çalışmalar": "Active studies",
    "Akademik çıktılar": "Academic outputs",
    "Güncel gelişmeler": "Latest developments",
    "Ekibimizden Son Eklenenler": "Recently Added Team Members",
    "Tüm Ekibi Gör →": "View Full Team →",
    "Son Projeler": "Latest Projects",
    "Tüm Projeleri Gör →": "View All Projects →",
    "Son Akademik Yayınlar": "Latest Academic Publications",
    "Tüm Yayınları Gör →": "View All Publications →",
    "Son Haberler ve Duyurular": "Latest News and Announcements",
    "Tüm Haberleri Gör →": "View All News →",
    "Araştırma Ekibimiz": "Our Research Team",
    "Laboratuvar çalışmalarına katkıda bulunan akademisyenleri ve öğrencileri inceleyebilir, ekip türü ile bölüm filtrelerini kullanabilir ve kartlara tıklayarak kişisel katkı sayfalarına ulaşabilirsiniz.": "Explore the academicians and students contributing to laboratory work, use team-type and department filters, and open personal contribution pages by clicking the cards.",
    "Tüm ekip": "All team members",
    "Akademisyenler": "Academicians",
    "Öğrenciler": "Students",
    "Akademisyen": "Academician",
    "Öğrenci": "Student",
    "Tüm bölümler": "All departments",
    "Ekip üyesi ara...": "Search team member...",
    "Ekip üyesi ara": "Search team member",
    "Ekip türüne göre filtrele": "Filter by team member type",
    "Bölüme göre filtrele": "Filter by department",
    "Ekip filtreleri": "Team filters",
    "Ekip üyesi sayfaları": "Team member pages",
    "Ekip üyeleri yükleniyor...": "Loading team members...",
    "Ekip üyeleri yüklenirken bir hata oluştu.": "An error occurred while loading team members.",
    "Filtreye uygun ekip üyesi bulunmuyor.": "No team member matches the selected filters.",
    "Ekip üyesi profili yükleniyor...": "Loading team member profile...",
    "Akademisyen profili yükleniyor...": "Loading academician profile...",
    "Ekip üyesi kimliği belirtilmedi.": "No team member ID was specified.",
    "Ekip üyesi profili yüklenirken bir hata oluştu.": "An error occurred while loading the team member profile.",
    "Akademisyen Profili": "Academician Profile",
    "Öğrenci Profili": "Student Profile",
    "Ekip Üyesi Profili": "Team Member Profile",
    "Bölüm bilgisi bulunmuyor.": "Department information is unavailable.",
    "Güncel çalışma bilgisi bulunmuyor.": "Current work information is unavailable.",
    "Güncel çalışma bilgisi yok.": "No current work information.",
    "Öğrenci No:": "Student ID:",
    "Sicil / Personel No:": "Staff / Personnel ID:",
    "Sicil / personel numarası": "Staff / personnel number",
    "Öğrenci numarası": "Student number",
    "Katkıda Bulunduğu Projeler": "Projects Contributed To",
    "Yayınları ve Makaleleri": "Publications and Articles",
    "Çalışmaları ve Aktiviteleri": "Work and Activities",
    "Ekip üyesi katkı istatistikleri": "Team member contribution statistics",
    "Yayın / Makale": "Publication / Article",
    "Toplam Katkı": "Total Contributions",
    "Projelerde ara...": "Search projects...",
    "Projelerde ara": "Search projects",
    "Yayınlarda ara...": "Search publications...",
    "Yayınlarda ara": "Search publications",
    "Aktivitelerde ara...": "Search activities...",
    "Aktivitelerde ara": "Search activities",
    "Aktivite sayfaları": "Activity pages",
    "Filtreye uygun aktivite bulunmuyor.": "No activity matches the selected filters.",
    "Tarih belirtilmedi": "Date not specified",
    "Başlıksız aktivite": "Untitled activity",
    "Açıklama bulunmuyor.": "Description is unavailable.",
    "İsimsiz ekip üyesi": "Unnamed team member",
    "Projelerimiz": "Our Projects",
    "Laboratuvar projelerini durum, teknoloji ve katkıda bulunan öğrencilere göre arayabilir; öğrenci avatarlarından kişisel katkı sayfalarına geçebilirsiniz.": "Search laboratory projects by status, technology and contributors, and open personal contribution pages through contributor avatars.",
    "Proje filtreleri": "Project filters",
    "Proje, teknoloji veya öğrenci ara...": "Search project, technology or contributor...",
    "Proje ara...": "Search project...",
    "Proje ara": "Search project",
    "Proje durumuna göre filtrele": "Filter by project status",
    "Proje sayfaları": "Project pages",
    "Tüm durumlar": "All statuses",
    "Planlandı": "Planned",
    "Devam ediyor": "In progress",
    "Devam Ediyor": "In Progress",
    "Tamamlandı": "Completed",
    "Beklemede": "On Hold",
    "İptal edildi": "Cancelled",
    "İptal Edildi": "Cancelled",
    "Durum Belirtilmedi": "Status Not Specified",
    "Durum belirtilmedi": "Status not specified",
    "Projeler yükleniyor...": "Loading projects...",
    "Projeler yüklenirken bir hata oluştu.": "An error occurred while loading projects.",
    "Projeler yüklenemedi.": "Projects could not be loaded.",
    "Filtreye uygun proje bulunmuyor.": "No project matches the selected filters.",
    "İsimsiz proje": "Untitled project",
    "Proje özeti bulunmuyor.": "Project summary is unavailable.",
    "Proje Detayları": "Project Details",
    "Detayları Gizle": "Hide Details",
    "Proje Bağlantısını Aç →": "Open Project Link →",
    "Proje bağlantısını aç →": "Open project link →",
    "Projeye katkıda bulunan ekip üyeleri": "Team members contributing to the project",
    "Katkıda Bulunanlar": "Contributors",
    "Henüz ekip üyesi bağlantısı eklenmemiş.": "No team member has been linked yet.",
    "Atanmış ekip üyesi bulunmuyor.": "No assigned team member.",
    "Başlangıç:": "Start:",
    "Bitiş:": "End:",
    "Tarih bilgisi bulunmuyor.": "Date information is unavailable.",
    "Öne Çıkan": "Featured",
    "Akademik Yayınlarımız": "Our Academic Publications",
    "Makale, bildiri, tez ve teknik raporları başlık, yazar, yayın yeri veya yayın türüne göre filtreleyebilirsiniz.": "Filter articles, conference papers, theses and technical reports by title, author, venue or publication type.",
    "Yayın filtreleri": "Publication filters",
    "Başlık, yazar veya yayın yeri ara...": "Search title, author or venue...",
    "Yayın ara...": "Search publication...",
    "Yayın ara": "Search publication",
    "Yayın türüne göre filtrele": "Filter by publication type",
    "Yayın sayfaları": "Publication pages",
    "Tüm yayın türleri": "All publication types",
    "Tüm türler": "All types",
    "Dergi Makalesi": "Journal Article",
    "Konferans Bildirisi": "Conference Paper",
    "Kitap Bölümü": "Book Chapter",
    "Kitap": "Book",
    "Tez": "Thesis",
    "Teknik Rapor": "Technical Report",
    "Rapor": "Report",
    "Diğer": "Other",
    "Akademik Yayın": "Academic Publication",
    "Yayınlar yükleniyor...": "Loading publications...",
    "Yayınlar yüklenirken bir hata oluştu.": "An error occurred while loading publications.",
    "Yayınlar yüklenemedi.": "Publications could not be loaded.",
    "Filtreye uygun yayın bulunmuyor.": "No publication matches the selected filters.",
    "Başlıksız yayın": "Untitled publication",
    "Yazar bilgisi bulunmuyor.": "Author information is unavailable.",
    "Yayın yeri bilgisi bulunmuyor.": "Publication venue is unavailable.",
    "Yayın yeri ve yılı belirtilmemiş.": "Publication venue and year are unspecified.",
    "Özeti Göster": "Show Abstract",
    "Özeti Gizle": "Hide Abstract",
    "DOI Sayfası": "DOI Page",
    "DOI sayfası": "DOI page",
    "Yayını Aç →": "Open Publication →",
    "Yayını aç →": "Open publication →",
    "İlişkili ekip üyesi bulunmuyor.": "No related team member.",
    "Güncel Haberler ve Duyurular": "Latest News and Announcements",
    "Laboratuvar haberlerini, etkinlikleri, proje güncellemelerini ve duyuruları kategoriye göre filtreleyebilirsiniz.": "Filter laboratory news, events, project updates and announcements by category.",
    "Haber filtreleri": "News filters",
    "Haber veya duyuru ara...": "Search news or announcement...",
    "Haber ara...": "Search news...",
    "Haber ara": "Search news",
    "Haber kategorisine göre filtrele": "Filter by news category",
    "Haber sayfaları": "News pages",
    "Tüm kategoriler": "All categories",
    "Duyuru": "Announcement",
    "Proje Güncellemesi": "Project Update",
    "Etkinlik": "Event",
    "Öğrenci Etkinliği": "Student Activity",
    "Haberler yükleniyor...": "Loading news...",
    "Haberler yüklenirken bir hata oluştu.": "An error occurred while loading news.",
    "Haberler yüklenemedi.": "News could not be loaded.",
    "Filtreye uygun haber bulunmuyor.": "No news item matches the selected filters.",
    "Başlıksız haber": "Untitled news item",
    "Haber özeti bulunmuyor.": "News summary is unavailable.",
    "Devamını Oku →": "Read More →",
    "İçeriği Gizle": "Hide Content",
    "İlişkili kayıt:": "Related record:",
    "Bizimle İletişime Geçin": "Contact Us",
    "Laboratuvar projeleri, akademik çalışmalar, staj olanakları veya iş birlikleri hakkında bilgi almak için formu kullanabilirsiniz.": "Use the form to request information about laboratory projects, academic studies, internship opportunities or collaborations.",
    "Ad Soyad": "Full Name",
    "E-posta": "Email",
    "Konu": "Subject",
    "Mesaj": "Message",
    "En fazla 5000 karakter.": "Maximum 5000 characters.",
    "Mesaj Gönder": "Send Message",
    "Lütfen bütün alanları doldurun.": "Please complete all fields.",
    "Mesaj Gönderiliyor...": "Sending Message...",
    "Mesajınız başarıyla gönderildi. En kısa sürede sizinle iletişime geçilecektir.": "Your message was sent successfully. We will contact you as soon as possible.",
    "Mesaj gönderilirken bir hata oluştu.": "An error occurred while sending the message.",
    "Form bilgileri geçersiz. Alanları kontrol edin.": "The form data is invalid. Please check the fields.",
    "Mesaj gönderme işlemine izin verilmedi.": "Sending the message is not permitted.",
    "Sunucuda bir hata oluştu. Mesaj gönderilemedi.": "A server error occurred. The message could not be sent.",
    "Mesaj gönderilemedi.": "The message could not be sent.",
    "Önceki": "Previous",
    "Sonraki": "Next",
    "En yeni kayıtlar": "Newest records",
    "En yeni önce": "Newest first",
    "Laboratuvar Yönetim Paneli": "Laboratory Administration Panel",
    "Yönetim Paneli": "Administration Panel",
    "Laboratuvar içeriklerini yönetmek için admin hesabınızla giriş yapın.": "Sign in with your administrator account to manage laboratory content.",
    "Kullanıcı adı": "Username",
    "Parola": "Password",
    "Giriş Yap": "Sign In",
    "Giriş kontrol ediliyor...": "Checking credentials...",
    "Kullanıcı adı veya parola hatalı.": "The username or password is incorrect.",
    "Giriş sırasında bir hata oluştu.": "An error occurred during sign-in.",
    "Oturum kapatıldı.": "Session ended.",
    "Yönetici oturumu bulunamadı.": "No administrator session was found.",
    "Oturum geçersiz. Tekrar giriş yapın.": "The session is invalid. Please sign in again.",
    "Mesaj, ekip üyesi, proje, yayın ve haber yönetimi": "Manage messages, team members, projects, publications and news",
    "Yönetim Özeti": "Administration Overview",
    "Laboratuvar verilerini tek ekrandan yönetin.": "Manage laboratory data from a single screen.",
    "Toplam mesaj": "Total messages",
    "Yeni mesaj": "New messages",
    "Toplam ekip üyesi": "Total team members",
    "Aktif ekip üyesi": "Active team members",
    "Toplam proje": "Total projects",
    "Öne çıkan proje": "Featured projects",
    "Toplam yayın": "Total publications",
    "Öne çıkan yayın": "Featured publications",
    "Toplam haber": "Total news items",
    "Aktif haber": "Active news items",
    "Yönetim bölümleri": "Administration sections",
    "İletişim Mesajları": "Contact Messages",
    "Mesaj ara...": "Search message...",
    "Mesaj ara": "Search message",
    "Mesaj durumuna göre filtrele": "Filter by message status",
    "Mesaj sayfaları": "Message pages",
    "Yeni": "New",
    "Okundu": "Read",
    "Cevaplandı": "Replied",
    "Arşivlendi": "Archived",
    "Yenile": "Refresh",
    "Mesajlar yükleniyor...": "Loading messages...",
    "Mesajlar yüklenemedi.": "Messages could not be loaded.",
    "Mesajlar yüklenirken hata oluştu.": "An error occurred while loading messages.",
    "Bu filtreye uygun mesaj bulunmuyor.": "No message matches the selected filters.",
    "Bilinmeyen gönderici": "Unknown sender",
    "Okundu Yap": "Mark as Read",
    "Arşivle": "Archive",
    "Sil": "Delete",
    "E-posta ile Cevapla": "Reply by Email",
    "Cevap": "Reply",
    "Gönderilecek cevabı yazın...": "Write the reply to be sent...",
    "Vazgeç": "Cancel",
    "Gönder": "Send",
    "Gönderiliyor...": "Sending...",
    "Cevap e-postası gönderildi.": "The reply email was sent.",
    "Mesaj okundu olarak işaretlendi.": "The message was marked as read.",
    "Mesaj durumu güncellendi.": "The message status was updated.",
    "Mesaj silindi.": "The message was deleted.",
    "İşlem sırasında hata oluştu.": "An error occurred during the operation.",
    "Yeni Ekip Üyesi Ekle": "Add New Team Member",
    "Ekip üyesi türü": "Team member type",
    "Bölüm": "Department",
    "Akademik unvan": "Academic title",
    "Örn. Prof. Dr.": "e.g. Professor",
    "Ad": "First name",
    "Soyad": "Last name",
    "Güncel çalışma": "Current work",
    "Fotoğraf": "Photo",
    "Fotoğraf isteğe bağlıdır. En fazla 5 MB.": "The photo is optional. Maximum 5 MB.",
    "Ekip üyesi aktif olarak gösterilsin": "Display the team member as active",
    "Ekip Üyesini Kaydet": "Save Team Member",
    "Ekip Üyesini Güncelle": "Update Team Member",
    "Düzenlemeyi İptal Et": "Cancel Editing",
    "Ekip Üyesi Listesi": "Team Member List",
    "Ekip üyesi durumuna göre filtrele": "Filter by team member status",
    "Aktif": "Active",
    "Pasif": "Inactive",
    "Düzenle": "Edit",
    "Belirtilmemiş": "Not specified",
    "Akademisyeni Düzenle": "Edit Academician",
    "Öğrenciyi Düzenle": "Edit Student",
    "Güncelleniyor...": "Updating...",
    "Ekip üyesi başarıyla güncellendi.": "The team member was updated successfully.",
    "Ekip üyesi başarıyla eklendi.": "The team member was added successfully.",
    "Ekip üyesi kaydedilemedi.": "The team member could not be saved.",
    "Ekip üyesi başarıyla silindi.": "The team member was deleted successfully.",
    "Ekip üyesi silinemedi.": "The team member could not be deleted.",
    "İlişkili aktiviteler, proje/yayın bağlantıları ve fotoğraf da temizlenecektir.": "Related activities, project/publication links and the photo will also be removed.",
    "Yeni Proje Ekle": "Add New Project",
    "Proje adı": "Project name",
    "Durum": "Status",
    "Proje bağlantısı": "Project link",
    "Başlangıç tarihi": "Start date",
    "Bitiş tarihi": "End date",
    "Kısa özet": "Short summary",
    "Açıklama": "Description",
    "Teknolojiler": "Technologies",
    "Teknolojileri virgülle ayırın.": "Separate technologies with commas.",
    "Projede katkı sağlayan ekip üyeleri": "Team members contributing to the project",
    "Birden fazla ekip üyesi seçmek için Ctrl tuşunu kullanabilirsiniz.": "Hold Ctrl to select multiple team members.",
    "Proje görseli": "Project image",
    "Görsel isteğe bağlıdır. En fazla 5 MB.": "The image is optional. Maximum 5 MB.",
    "Projeyi öne çıkan olarak göster": "Display the project as featured",
    "Projeyi Kaydet": "Save Project",
    "Projeyi Güncelle": "Update Project",
    "Projeyi Düzenle": "Edit Project",
    "Proje Listesi": "Project List",
    "Bitiş tarihi başlangıç tarihinden önce olamaz.": "The end date cannot be earlier than the start date.",
    "Proje başarıyla güncellendi.": "The project was updated successfully.",
    "Proje başarıyla eklendi.": "The project was added successfully.",
    "Proje kaydedilemedi.": "The project could not be saved.",
    "Proje başarıyla silindi.": "The project was deleted successfully.",
    "Proje silinemedi.": "The project could not be deleted.",
    "Yeni Yayın Ekle": "Add New Publication",
    "Yayın başlığı": "Publication title",
    "Yayın türü": "Publication type",
    "Yayın yılı": "Publication year",
    "Yazarlar": "Authors",
    "Yazarları virgülle ayırın.": "Separate authors with commas.",
    "Dergi, konferans veya yayınevi": "Journal, conference or publisher",
    "Yayın bağlantısı": "Publication link",
    "Özet": "Abstract",
    "Yayınla ilişkili ekip üyeleri": "Team members related to the publication",
    "Yayını öne çıkan olarak göster": "Display the publication as featured",
    "Yayını Kaydet": "Save Publication",
    "Yayını Güncelle": "Update Publication",
    "Yayını Düzenle": "Edit Publication",
    "Yayın Listesi": "Publication List",
    "Yayın başarıyla güncellendi.": "The publication was updated successfully.",
    "Yayın başarıyla eklendi.": "The publication was added successfully.",
    "Yayın kaydedilemedi.": "The publication could not be saved.",
    "Yayın başarıyla silindi.": "The publication was deleted successfully.",
    "Yayın silinemedi.": "The publication could not be deleted.",
    "Yeni Haber Ekle": "Add News Item",
    "Başlık": "Title",
    "Kategori": "Category",
    "Yayın tarihi": "Publication date",
    "Haber içeriği": "News content",
    "İlişkili kayıt kimliği": "Related record ID",
    "İsteğe bağlı proje, yayın veya ekip üyesi ID değeri": "Optional project, publication or team member ID",
    "Haber görseli": "News image",
    "Haberi ana sitede göster": "Display the news item on the public site",
    "Haberi öne çıkan olarak işaretle": "Mark the news item as featured",
    "Haberi Kaydet": "Save News Item",
    "Haberi Güncelle": "Update News Item",
    "Haberi Düzenle": "Edit News Item",
    "Haber Listesi": "News List",
    "Haber başarıyla güncellendi.": "The news item was updated successfully.",
    "Haber başarıyla eklendi.": "The news item was added successfully.",
    "Haber kaydedilemedi.": "The news item could not be saved.",
    "Haber başarıyla silindi.": "The news item was deleted successfully.",
    "Haber silinemedi.": "The news item could not be deleted.",
    "Tarih bilgisi geçersiz.": "The date information is invalid.",
    "Gönderilen bilgiler geçersiz.": "The submitted data is invalid.",
    "İstenen kayıt bulunamadı.": "The requested record was not found.",
    "Bu bilgilerle kayıt zaten mevcut.": "A record with this information already exists.",
    "Seçilen dosya izin verilen boyutu aşıyor.": "The selected file exceeds the permitted size.",
    "Sunucuda bir hata oluştu.": "A server error occurred.",
    "İşlem başarısız oldu. HTTP:": "The operation failed. HTTP:",
    "Ana Sayfa | OMÜ Sistem ve Ağ Araştırma Laboratuvarı": "Home | OMU Systems and Networks Research Laboratory",
    "Ekibimiz | OMÜ Sistem ve Ağ Araştırma Laboratuvarı": "Team | OMU Systems and Networks Research Laboratory",
    "Projeler | OMÜ Sistem ve Ağ Araştırma Laboratuvarı": "Projects | OMU Systems and Networks Research Laboratory",
    "Yayınlar | OMÜ Sistem ve Ağ Araştırma Laboratuvarı": "Publications | OMU Systems and Networks Research Laboratory",
    "Haberler | OMÜ Sistem ve Ağ Araştırma Laboratuvarı": "News | OMU Systems and Networks Research Laboratory",
    "Ekip Üyesi Profili | OMÜ Sistem ve Ağ Araştırma Laboratuvarı": "Team Member Profile | OMU Systems and Networks Research Laboratory",
    "Akademisyen Profili | OMÜ Sistem ve Ağ Araştırma Laboratuvarı": "Academician Profile | OMU Systems and Networks Research Laboratory"
};

    let currentLanguage = resolveSynchronousLanguage();
    let observer = null;

    function resolveSynchronousLanguage() {
        const manual = localStorage.getItem(MANUAL_LANGUAGE_KEY);
        if (SUPPORTED_LANGUAGES.has(manual)) {
            return manual;
        }

        const cachedAutomatic = sessionStorage.getItem(AUTO_LANGUAGE_KEY);
        if (SUPPORTED_LANGUAGES.has(cachedAutomatic)) {
            return cachedAutomatic;
        }

        const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
        if (timezone === "Europe/Istanbul") {
            return "tr";
        }

        if (timezone) {
            return "en";
        }

        const browserLanguage = String(navigator.language || "").toLowerCase();
        return browserLanguage.startsWith("tr") ? "tr" : "en";
    }

    function getLanguage() {
        return currentLanguage;
    }

    function getLocale() {
        return currentLanguage === "tr" ? "tr-TR" : "en-US";
    }

    function normalize(value) {
        return String(value ?? "").replace(/\s+/g, " ").trim();
    }

    function preserveWhitespace(original, translated) {
        const leading = original.match(/^\s*/)?.[0] || "";
        const trailing = original.match(/\s*$/)?.[0] || "";
        return leading + translated + trailing;
    }

    function translatePattern(text) {
        let match;

        match = text.match(/^(.*) profilini aç$/);
        if (match) return match[1] + " profile";

        match = text.match(/^(.*) fotoğrafı$/);
        if (match) return "Photo of " + match[1];

        match = text.match(/^(.*) görseli$/);
        if (match) return "Image for " + match[1];

        match = text.match(/^Başlangıç:\s*(.*)$/);
        if (match) return "Start: " + match[1];

        match = text.match(/^Bitiş:\s*(.*)$/);
        if (match) return "End: " + match[1];

        match = text.match(/^Öğrenci No:\s*(.*)$/);
        if (match) return "Student ID: " + match[1];

        match = text.match(/^Sicil \/ Personel No:\s*(.*)$/);
        if (match) return "Staff / Personnel ID: " + match[1];

        match = text.match(/^İlişkili kayıt:\s*(.*)$/);
        if (match) return "Related record: " + match[1];

        match = text.match(/^(\d+) kayıt • En yeni kayıtlar$/);
        if (match) return match[1] + " records • Newest records";

        match = text.match(/^(\d+) kayıt • (\d+) kart\/sayfa • En yeni önce$/);
        if (match) return match[1] + " records • " + match[2] + " cards/page • Newest first";

        match = text.match(/^(\d+) kayıt$/);
        if (match) return match[1] + " records";

        match = text.match(/^Sayfa (\d+) \/ (\d+)$/);
        if (match) return "Page " + match[1] + " / " + match[2];

        match = text.match(/^HTTP durum kodu:\s*(\d+)$/);
        if (match) return "HTTP status code: " + match[1];

        match = text.match(/^İşlem başarısız oldu\. HTTP:\s*(\d+)$/);
        if (match) return "The operation failed. HTTP: " + match[1];

        match = text.match(/^Giriş kontrolü başarısız oldu\. HTTP:\s*(\d+)$/);
        if (match) return "Credential check failed. HTTP: " + match[1];

        match = text.match(/^"(.+)" mesajını silmek istediğinize emin misiniz\?$/);
        if (match) return "Are you sure you want to delete the message \"" + match[1] + "\"?";

        match = text.match(/^"(.+)" ekip üyesini silmek istediğinize emin misiniz\?([\s\S]*)$/);
        if (match) return "Are you sure you want to delete team member \"" + match[1] + "\"?" + match[2];

        match = text.match(/^"(.+)" projesini silmek istediğinize emin misiniz\?([\s\S]*)$/);
        if (match) return "Are you sure you want to delete project \"" + match[1] + "\"?" + match[2];

        match = text.match(/^"(.+)" yayınını silmek istediğinize emin misiniz\?$/);
        if (match) return "Are you sure you want to delete publication \"" + match[1] + "\"?";

        match = text.match(/^"(.+)" haberini silmek istediğinize emin misiniz\?([\s\S]*)$/);
        if (match) return "Are you sure you want to delete news item \"" + match[1] + "\"?" + match[2];

        return text;
    }

    function translateString(value, language = currentLanguage) {
        if (language !== "en") {
            return String(value ?? "");
        }

        const original = String(value ?? "");
        const normalized = normalize(original);
        if (!normalized) return original;

        const translated = EN_TRANSLATIONS[normalized] || translatePattern(normalized);
        return preserveWhitespace(original, translated);
    }

    function translateTextNode(node) {
        if (!node || node.nodeType !== Node.TEXT_NODE) return;
        const parent = node.parentElement;
        if (parent && ["SCRIPT", "STYLE", "NOSCRIPT"].includes(parent.tagName)) return;

        if (!originalTextValues.has(node)) {
            originalTextValues.set(node, node.nodeValue || "");
        }

        const original = originalTextValues.get(node);
        const desired = currentLanguage === "tr" ? original : translateString(original, "en");
        if (node.nodeValue !== desired) node.nodeValue = desired;
    }

    function originalAttributesFor(element) {
        if (!originalAttributeValues.has(element)) {
            originalAttributeValues.set(element, new Map());
        }
        return originalAttributeValues.get(element);
    }

    function translateAttribute(element, attributeName) {
        if (!element.hasAttribute(attributeName)) return;
        const originals = originalAttributesFor(element);
        if (!originals.has(attributeName)) {
            originals.set(attributeName, element.getAttribute(attributeName));
        }
        const original = originals.get(attributeName) || "";
        const desired = currentLanguage === "tr" ? original : translateString(original, "en");
        if (element.getAttribute(attributeName) !== desired) {
            element.setAttribute(attributeName, desired);
        }
    }

    function translateElement(element) {
        if (!element || element.nodeType !== Node.ELEMENT_NODE) return;
        ATTRIBUTE_NAMES.forEach(attribute => translateAttribute(element, attribute));
        element.childNodes.forEach(child => {
            if (child.nodeType === Node.TEXT_NODE) translateTextNode(child);
        });
    }

    function translateTree(rootNode = document) {
        if (rootNode.nodeType === Node.TEXT_NODE) {
            translateTextNode(rootNode);
            return;
        }
        if (rootNode.nodeType === Node.ELEMENT_NODE) translateElement(rootNode);
        const walker = document.createTreeWalker(
            rootNode,
            NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_TEXT
        );
        let current;
        while ((current = walker.nextNode())) {
            if (current.nodeType === Node.TEXT_NODE) translateTextNode(current);
            else translateElement(current);
        }
    }

    function applyLanguage() {
        document.documentElement.lang = currentLanguage;
        document.title = currentLanguage === "tr"
            ? originalDocumentTitle
            : translateString(originalDocumentTitle, "en");
        translateTree(document.body || document.documentElement);
        updateLanguageSwitcher();
    }

    function createLanguageSwitcher() {
        if (document.getElementById("global-language-select")) return;

        const wrapper = document.createElement("div");
        wrapper.className = "global-language-switcher";

        const globe = document.createElement("span");
        globe.className = "language-switcher-icon";
        globe.setAttribute("aria-hidden", "true");
        globe.textContent = "🌐";

        const label = document.createElement("label");
        label.className = "visually-hidden";
        label.htmlFor = "global-language-select";
        label.textContent = "Dil";

        const select = document.createElement("select");
        select.id = "global-language-select";
        select.className = "language-select";
        select.setAttribute("aria-label", "Dil seçimi");
        select.innerHTML = `
            <option value="auto">Otomatik / Auto</option>
            <option value="tr">Türkçe</option>
            <option value="en">English</option>
        `;

        select.addEventListener("change", async () => {
            const value = select.value;
            if (value === "auto") {
                localStorage.removeItem(MANUAL_LANGUAGE_KEY);
                sessionStorage.removeItem(AUTO_LANGUAGE_KEY);
                const detected = await fetchAutomaticLanguage();
                sessionStorage.setItem(AUTO_LANGUAGE_KEY, detected);
            } else {
                localStorage.setItem(MANUAL_LANGUAGE_KEY, value);
            }
            window.location.reload();
        });

        wrapper.append(globe, label, select);
        document.body.appendChild(wrapper);
        updateLanguageSwitcher();
    }

    function updateLanguageSwitcher() {
        const select = document.getElementById("global-language-select");
        if (!select) return;
        const manual = localStorage.getItem(MANUAL_LANGUAGE_KEY);
        select.value = SUPPORTED_LANGUAGES.has(manual) ? manual : "auto";
        select.setAttribute(
            "aria-label",
            currentLanguage === "tr" ? "Dil seçimi" : "Language selection"
        );
    }

    async function fetchAutomaticLanguage() {
        const controller = new AbortController();
        const timeout = window.setTimeout(() => controller.abort(), 1500);
        try {
            const response = await fetch("/api/localization/default-language", {
                headers: { "Accept": "application/json" },
                credentials: "same-origin",
                signal: controller.signal
            });
            if (response.ok) {
                const body = await response.json();
                if (body.source === "country" && SUPPORTED_LANGUAGES.has(body.language)) {
                    return body.language;
                }

                const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
                if (timezone === "Europe/Istanbul") return "tr";
                if (timezone) return "en";

                if (SUPPORTED_LANGUAGES.has(body.language)) return body.language;
            }
        } catch (error) {
            console.debug("Automatic language detection fallback was used.", error);
        } finally {
            window.clearTimeout(timeout);
        }
        return resolveBrowserFallback();
    }

    function resolveBrowserFallback() {
        const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
        if (timezone === "Europe/Istanbul") return "tr";
        if (timezone) return "en";
        const browserLanguage = String(navigator.language || "").toLowerCase();
        return browserLanguage.startsWith("tr") ? "tr" : "en";
    }

    async function synchronizeAutomaticLanguage() {
        if (SUPPORTED_LANGUAGES.has(localStorage.getItem(MANUAL_LANGUAGE_KEY))) return;
        if (SUPPORTED_LANGUAGES.has(sessionStorage.getItem(AUTO_LANGUAGE_KEY))) return;

        const detected = await fetchAutomaticLanguage();
        sessionStorage.setItem(AUTO_LANGUAGE_KEY, detected);
        if (detected !== currentLanguage) {
            currentLanguage = detected;
            window.location.reload();
        }
    }

    function observeDynamicContent() {
        if (observer) observer.disconnect();
        observer = new MutationObserver(mutations => {
            for (const mutation of mutations) {
                if (mutation.type === "attributes") {
                    translateAttribute(mutation.target, mutation.attributeName);
                    continue;
                }
                mutation.addedNodes.forEach(node => translateTree(node));
            }
        });
        observer.observe(document.documentElement, {
            childList: true,
            subtree: true,
            attributes: true,
            attributeFilter: ATTRIBUTE_NAMES
        });
    }

    const nativeAlert = window.alert.bind(window);
    const nativeConfirm = window.confirm.bind(window);
    window.alert = message => nativeAlert(translateString(message));
    window.confirm = message => nativeConfirm(translateString(message));

    window.I18n = Object.freeze({
        getLanguage,
        getLocale,
        t: translateString,
        apply: applyLanguage
    });

    document.documentElement.lang = currentLanguage;

    document.addEventListener("DOMContentLoaded", () => {
        createLanguageSwitcher();
        applyLanguage();
        observeDynamicContent();
        synchronizeAutomaticLanguage();
    });
})();
