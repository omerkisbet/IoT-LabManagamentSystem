# OMÜ Sistem ve Ağ Araştırma Laboratuvarı Web Uygulaması

Ondokuz Mayıs Üniversitesi Bilgisayar Mühendisliği bünyesindeki Sistem ve Ağ Araştırma Laboratuvarı için geliştirilmiş tam yığın bir web uygulamasıdır.

Uygulama; laboratuvar ekibini, projeleri, akademik yayınları ve haberleri ziyaretçilere sunar. Ayrıca ziyaretçilerin iletişim formu üzerinden mesaj göndermesine ve yöneticinin bütün içerikleri ayrı bir admin panelinden yönetmesine olanak sağlar.

---

## Özellikler

### Ziyaretçi tarafı

- Laboratuvar akademisyenlerini ve öğrencilerini ekip türüne göre görüntüleme
- Projeleri ve kullanılan teknolojileri görüntüleme
- Akademik yayınları görüntüleme
- Haber ve duyuruları görüntüleme
- İletişim formu üzerinden mesaj gönderme
- Mobil uyumlu arayüz
- Proje, öğrenci ve haber görsellerini görüntüleme
- Proje kartlarında akademisyen ve öğrenci katkılarını ayrı gruplarda görüntüleme
- Katkıda bulunan akademisyen veya öğrenci fotoğrafından kişisel profile geçiş
- Akademisyen ve öğrenciye özel proje, yayın ve aktivite istatistiklerini görüntüleme
- Ekip üyesi profilinde ilişkili projeleri, yayınları ve aktiviteleri görüntüleme

### Yönetim paneli

- Admin kullanıcı adı ve parolasıyla giriş
- İletişim mesajlarını listeleme
- Mesaj durumlarını yönetme: Yeni, Okundu, Cevaplandı, Arşivlendi
- Mesaj silme
- Öğrenci veya akademisyen ekleme, düzenleme ve silme
- Ekip üyesi fotoğrafı yükleme
- Ekip üyesini aktif veya pasif yapma
- Proje ekleme, düzenleme ve silme
- Projeye akademisyen ve öğrenci bağlama
- Proje görseli yükleme
- Projeyi öne çıkarma
- Akademik yayın ekleme, düzenleme ve silme
- Yayına akademisyen ve öğrenci bağlama
- Yayını öne çıkarma
- Haber ekleme, düzenleme ve silme
- Haber görseli yükleme
- Haberi aktif veya pasif yapma
- Haberi öne çıkarma
- Yönetim istatistiklerini görüntüleme

---

## Kullanılan Teknolojiler

### Backend

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data MongoDB
- Spring Security
- Jakarta Validation
- Maven
- Springdoc OpenAPI / Swagger UI

### Veritabanı

- MongoDB
- MongoDB Atlas desteği

### Frontend

- HTML5
- CSS3
- Vanilla JavaScript
- Fetch API

### Test

- JUnit
- Spring Boot Test
- Spring MVC Test
- Spring Security Test
- Mockito

---

## Mimari

Uygulama katmanlı mimari kullanır:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
MongoDB
```

API tarafında entity nesneleri doğrudan dışarı açılmaz. İstek ve yanıt işlemleri için DTO ve mapper yapısı kullanılır.

Temel katmanlar:

```text
Document / Entity
DTO
Mapper
Repository
Service
Controller
Exception Handler
Security Configuration
File Storage Service
```

---

## Proje Dizini

Genel dizin yapısı:

```text
demo1/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── Controller/
│   │   │   ├── config/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   ├── mapper/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── index.html
│   │       │   └── admin.html
│   │       └── application.properties
│   └── test/
│       └── java/com/example/demo/
├── uploads/
├── .env.example
├── .gitignore
├── pom.xml
└── README.md
```

`uploads/` klasörü Git tarafından takip edilmez.

---

## Gereksinimler

Projeyi yerel ortamda çalıştırmak için:

- Java 21
- Maven
- MongoDB Community Server veya MongoDB Atlas
- IntelliJ IDEA, Eclipse veya başka bir Java IDE
- İnternet tarayıcısı

---

## Ortam Değişkenleri

Uygulama aşağıdaki environment variable değerlerini destekler:

| Değişken | Açıklama | Yerel varsayılan |
|---|---|---|
| `MONGODB_URI` | MongoDB bağlantı adresi | `mongodb://localhost:27017/demo1_db` |
| `ADMIN_USERNAME` | Admin kullanıcı adı | `admin` |
| `ADMIN_PASSWORD` | Admin parolası | `Admin123!` |
| `UPLOAD_DIR` | Görsellerin kaydedileceği klasör | `uploads` |
| `PORT` | Uygulama portu | `8080` |

Örnek `.env.example` içeriği:

```env
MONGODB_URI=mongodb://localhost:27017/demo1_db
ADMIN_USERNAME=admin
ADMIN_PASSWORD=change-this-password
UPLOAD_DIR=uploads
PORT=8080
```

> Gerçek parola ve bağlantı bilgileri `.env.example`, GitHub veya kaynak kod içine yazılmamalıdır.

---

## MongoDB Atlas Bağlantısı

MongoDB Atlas kullanılırken bağlantı adresi environment variable olarak verilmelidir:

```text
MONGODB_URI=mongodb+srv://DATABASE_USER:DATABASE_PASSWORD@CLUSTER_ADDRESS/demo1_db?retryWrites=true&w=majority
```

Atlas tarafında aşağıdakiler yapılandırılmalıdır:

1. Database user oluşturulması
2. IP Access List ayarının yapılması
3. `demo1_db` veritabanının kullanılması
4. Connection string içindeki kullanıcı adı ve parolanın doğru girilmesi

Kullanılan ağ, MongoDB Atlas'ın `27017` numaralı portuna çıkışa izin vermelidir.

---

## application.properties

Önerilen yapı:

```properties
spring.application.name=demo1

server.port=${PORT:8080}
server.forward-headers-strategy=framework
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=20s

spring.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/demo1_db}

app.upload.dir=${UPLOAD_DIR:uploads}
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB

app.security.admin.username=${ADMIN_USERNAME:admin}
app.security.admin.password=${ADMIN_PASSWORD:Admin123!}

springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operations-sorter=method
springdoc.swagger-ui.tags-sorter=alpha
springdoc.swagger-ui.persist-authorization=true

server.error.include-message=never
server.error.include-stacktrace=never
server.error.include-binding-errors=never

logging.level.root=INFO
logging.level.com.example.demo=INFO
```

---

## Projeyi Çalıştırma

### IDE üzerinden

1. Projeyi IntelliJ IDEA ile açın.
2. Maven bağımlılıklarının yüklenmesini bekleyin.
3. MongoDB'nin çalıştığından veya Atlas bağlantısının hazır olduğundan emin olun.
4. `Demo1Application.java` dosyasını çalıştırın.

### Terminal üzerinden

Windows:

```powershell
mvnw.cmd spring-boot:run
```

Linux veya macOS:

```bash
./mvnw spring-boot:run
```

Maven Wrapper kullanılmıyorsa:

```bash
mvn spring-boot:run
```

---

## Uygulama Adresleri

Uygulama çalıştıktan sonra:

### Ana site

```text
http://localhost:8080/
```

### Admin paneli

```text
http://localhost:8080/admin.html
```

### Öğrenci profili

```text
http://localhost:8080/student.html?id=STUDENT_ID
```

### Swagger UI

```text
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON

```text
http://localhost:8080/v3/api-docs
```

---

## Admin Girişi

Yerel varsayılan giriş bilgileri:

```text
Kullanıcı adı: admin
Parola: Admin123!
```

Gerçek veya production ortamında varsayılan parola kullanılmamalıdır.

Admin bilgileri environment variable olarak ayarlanmalıdır:

```text
ADMIN_USERNAME
ADMIN_PASSWORD
```

---

## Temel API Endpointleri

### Öğrenciler

| Metot | Endpoint | Açıklama |
|---|---|---|
| `GET` | `/api/students` | Öğrencileri listeler |
| `GET` | `/api/students/{id}/profile` | Öğrencinin profilini, istatistiklerini ve katkılarını getirir |
| `POST` | `/api/students` | Öğrenci ekler |
| `PUT` | `/api/students/{id}` | Öğrenci günceller |
| `DELETE` | `/api/students/{id}` | Öğrenci siler |

### Projeler

| Metot | Endpoint | Açıklama |
|---|---|---|
| `GET` | `/api/projects` | Projeleri listeler |
| `GET` | `/api/projects/featured` | Öne çıkan projeleri listeler |
| `POST` | `/api/projects` | Proje ekler |
| `PUT` | `/api/projects/{id}` | Proje günceller |
| `DELETE` | `/api/projects/{id}` | Proje siler |

### Yayınlar

| Metot | Endpoint | Açıklama |
|---|---|---|
| `GET` | `/api/publications` | Yayınları listeler |
| `GET` | `/api/publications/featured` | Öne çıkan yayınları listeler |
| `POST` | `/api/publications` | Yayın ekler |
| `PUT` | `/api/publications/{id}` | Yayın günceller |
| `DELETE` | `/api/publications/{id}` | Yayın siler |

### Haberler

| Metot | Endpoint | Açıklama |
|---|---|---|
| `GET` | `/api/news` | Bütün haberleri listeler |
| `GET` | `/api/news/active` | Aktif haberleri listeler |
| `POST` | `/api/news` | Haber ekler |
| `PUT` | `/api/news/{id}` | Haber günceller |
| `DELETE` | `/api/news/{id}` | Haber siler |

### İletişim mesajları

| Metot | Endpoint | Açıklama |
|---|---|---|
| `POST` | `/api/contact-messages` | Ziyaretçi mesajı gönderir |
| `GET` | `/api/contact-messages` | Mesajları listeler |
| `PATCH` | `/api/contact-messages/{id}/read` | Mesajı okundu yapar |
| `PATCH` | `/api/contact-messages/{id}/status` | Mesaj durumunu değiştirir |
| `DELETE` | `/api/contact-messages/{id}` | Mesajı siler |

### Görsel yükleme

| Metot | Endpoint | Açıklama |
|---|---|---|
| `POST` | `/api/media/students/{id}/photo` | Öğrenci fotoğrafı yükler |
| `POST` | `/api/media/projects/{id}/image` | Proje görseli yükler |
| `POST` | `/api/media/news/{id}/image` | Haber görseli yükler |
| `GET` | `/api/media/files/{folder}/{filename}` | Görseli sunar |

Tam endpoint listesi Swagger UI üzerinden görülebilir.

---

## Güvenlik

Uygulamada:

- Spring Security kullanılır.
- Admin işlemleri `ADMIN` rolü gerektirir.
- Admin isteklerinde HTTP Basic Authentication kullanılır.
- Ana sayfa ve statik dosyalar herkese açıktır.
- İletişim formu herkese açıktır.
- Ekleme, güncelleme ve silme işlemleri admin yetkisi gerektirir.
- Parolalar kaynak kod dışında environment variable olarak tutulabilir.
- API hata yanıtlarında production ortamında stack trace gösterilmez.
- Dosya yüklemelerinde klasör ve dosya türü kontrolü uygulanır.
- Dosya adları UUID kullanılarak oluşturulur.
- Path traversal kontrolleri uygulanır.

### Production öncesi önerilen ek güvenlikler

- Basic Auth yerine güvenli session veya JWT tabanlı giriş
- CSRF koruması
- Giriş denemesi sınırı
- Rate limiting
- İletişim formunda spam koruması
- CAPTCHA
- HTTPS zorunluluğu
- Daha dar MongoDB kullanıcı yetkisi
- Kalıcı bulut görsel depolaması
- Düzenli veritabanı yedekleme

---

## Dosya Yükleme Kuralları

Desteklenen görsel formatları:

```text
JPG
JPEG
PNG
WEBP
```

Varsayılan maksimum dosya boyutu:

```text
5 MB
```

Yerel geliştirmede görseller:

```text
uploads/
```

klasöründe saklanır.

> Geçici dosya sistemi kullanan bulut servislerinde `uploads/` klasörü kalıcı değildir. Production ortamında Cloudinary, Amazon S3 veya benzeri bir object storage kullanılmalıdır.

---

## Testler

Projede kritik backend akışları için testler bulunur:

- `StudentControllerTest`
- `StudentServiceTest`
- `FileStorageServiceTest`
- `ContactMessageControllerTest`

Testleri çalıştırmak için:

Windows:

```powershell
mvnw.cmd test
```

Linux veya macOS:

```bash
./mvnw test
```

Maven Wrapper yoksa:

```bash
mvn test
```

---

## Mevcut Durum

Tamamlanan bölümler:

- Backend CRUD işlemleri
- MongoDB entegrasyonu
- DTO ve mapper yapısı
- Global exception handling
- Spring Security
- Swagger/OpenAPI
- Görsel yükleme sistemi
- Ana kullanıcı arayüzü
- Proje katkıda bulunanlar görünümü
- Öğrenci kişisel profil sayfası
- Öğrenci proje, yayın ve aktivite istatistikleri
- Admin paneli
- İletişim formu
- MongoDB Atlas bağlantısı
- Kritik otomatik testler
- Uçtan uca yerel testler

Henüz tamamlanmayan production işlemleri:

- Kalıcı bulut görsel depolaması
- GitHub repository yayını
- Sunucuya deployment
- Gerçek alan adı bağlantısı
- HTTPS ve production güvenlik sıkılaştırması

Deployment işlemi, proje sorumlusunun veya öğretim elemanının onayı sonrasında gerçekleştirilecektir.

---

## Gelecek Geliştirmeler

- Cloudinary veya S3 tabanlı kalıcı görsel depolama
- Kullanıcı oturumu tabanlı admin girişi
- İletişim mesajlarına panel üzerinden e-posta ile cevap verme
- Haber ve proje arama
- Sayfalama
- İçerik filtreleme
- Aktivite yönetim ekranı
- Otomatik veritabanı yedekleme
- Docker desteği
- CI/CD
- Gerçek alan adı ve HTTPS
- Loglama ve izleme sistemi
- Türkçe ve İngilizce çoklu dil desteği

---

## Not

Bu proje eğitim, staj ve laboratuvar yönetimi amacıyla geliştirilmiştir. Production ortamına alınmadan önce kurum onayı, güvenlik kontrolleri ve veri gizliliği değerlendirmesi yapılmalıdır.


## Latest Interface Requirements (v1.3)

The public website, student profile pages, and administration lists now include:

- Client-side pagination with 10 cards per page.
- Newest-first sorting based on creation, publication, activity, or message date.
- Date and time labels in the upper-right corner of cards.
- Search and category/status/type filters for all data collections.
- Text wrapping and line clamping to prevent long content from overlapping card layouts.
- Swagger/OpenAPI links removed from the public navigation. API documentation now requires the `ADMIN` role.

Older MongoDB documents that do not yet contain a `createdAt` field use the timestamp encoded in their MongoDB ObjectId as a display fallback. New and updated student, project, and publication documents are timestamped automatically through MongoDB auditing.


## Public Page Structure

The public interface uses separate pages instead of loading all collections on one page:

- `/` – Home page with the three newest records from each section
- `/team.html` – Team directory with member-type and department filters plus adaptive pagination
- `/projects.html` – Project directory with status filtering, contributors and pagination
- `/publications.html` – Publication directory with type filtering and pagination
- `/news.html` – News directory with category filtering and pagination
- `/student.html?id=...` – Individual student contribution profile
- `/academic.html?id=...` – Individual academician contribution profile

The contact form is available at the bottom of every public page. Card timestamps are displayed in the lower-right corner.

Current Docker image tag: `omerut/demo1-app:v1.8`.


## Admin Panel Email Reply

Administrators can reply directly to contact messages from the admin panel.
The feature uses SMTP and is disabled until mail environment variables are configured.

```env
MAIL_ENABLED=true
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@example.com
MAIL_PASSWORD=your-smtp-or-app-password
MAIL_FROM=your-email@example.com
MAIL_SMTP_AUTH=true
MAIL_STARTTLS=true
```

For Gmail, use an App Password instead of the normal account password.
Do not commit real mail credentials to Git.


## Team Member and Adaptive Pagination Update (v1.6)

Version 1.6 extends the existing student model with two team-member types:

- `STUDENT`
- `ACADEMICIAN`

Existing MongoDB records without a `memberType` value are treated as students for backward compatibility. Administrators can select the member type, enter an academic title for academicians, and use the existing photo, department, activity, project, and publication workflows for both types.

Project contributor cards display academicians in a separate upper group and students in a lower group. Contributor avatars open `/academic.html` or `/student.html` according to the selected member type.

Public lists, profile lists, and administration lists use adaptive page sizes. The number of cards per page is calculated from the available list width and expected grid columns so complete rows are shown instead of leaving avoidable empty spaces.


## Localization Update (v1.7)

The public site and the administration panel support Turkish and English user-interface localization.

- Turkey is selected as Turkish when a trusted reverse proxy supplies a country header such as `CF-IPCountry: TR`.
- Other country codes default to English.
- When no country header exists, the application falls back to the browser `Accept-Language`, browser language and time zone.
- Visitors can override the automatic choice with the fixed language selector. The manual preference is stored in the browser.
- Dates, times, statuses, filters, buttons, form labels, validation feedback and administration controls use the selected locale.

Database content written by administrators, such as project descriptions and news articles, remains in the language in which it was entered. Fully bilingual editorial content requires Turkish and English content fields or a translation-service integration.


## Self-hosted country detection (v1.7.1)

The application can determine the visitor country without requiring Cloudflare. It resolves the public client IP and looks it up in a locally mounted `GeoLite2-Country.mmdb` database.

Resolution order:

1. Trusted CDN/reverse-proxy country header, only when `TRUST_PROXY_HEADERS=true`.
2. Local GeoLite2 Country database lookup using the visitor public IP.
3. Browser `Accept-Language` fallback.
4. Manual Turkish/English selection stored in the browser always has priority on later visits.

Create a free MaxMind GeoLite account and place the account ID and license key in `.env`:

```env
MAXMIND_ACCOUNT_ID=your-account-id
MAXMIND_LICENSE_KEY=your-license-key
GEOIP_ENABLED=true
TRUST_PROXY_HEADERS=false
```

Start the application together with the optional official database updater profile:

```powershell
docker compose --profile geoip up --build -d
```

The updater writes `GeoLite2-Country.mmdb` to the shared `demo1_geoip` volume. The application detects the file when it appears and reloads it when it changes.

Keep `TRUST_PROXY_HEADERS=false` when the Spring Boot port is directly exposed to the internet. Set it to `true` only when requests can reach the application exclusively through a trusted reverse proxy or CDN that overwrites forwarding headers.


## Compile fix (v1.7.2)

Fixed invalid Java escaping in `ClientIpResolver` for:

- quoted `Forwarded` header values,
- IPv4 addresses with ports,
- IPv4 splitting,
- numeric IPv4 segment validation.



## Version 1.8 fixes

- Rebuilt profile project cards with a stable footer so links and timestamps no longer overlap.
- Improved long-title, long-summary, technology-tag and mobile card layout behavior.
- Complete SMTP settings now activate admin email replies even when an older `.env` still contains `MAIL_ENABLED=false`.
- Fresh configurations default to `MAIL_ENABLED=true`.
- SMTP validation now reports the exact missing configuration group.
