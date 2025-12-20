# SPTM Backend API Dokümantasyonu

**Base URL:** `http://localhost:8080` (Development)  
**Production URL:** TBD

---

## 📋 İçindekiler

1. [Kimlik Doğrulama (Authentication)](#1-kimlik-doğrulama-authentication)
2. [Görevler (Tasks)](#2-görevler-tasks)
3. [Misyon İfadeleri (Mission Statements)](#3-misyon-ifadeleri-mission-statements)
4. [Takvim Entegrasyonu (Calendar)](#4-takvim-entegrasyonu-calendar)
5. [Analitik (Analytics)](#5-analitik-analytics)
6. [Veri Modelleri](#6-veri-modelleri)
7. [Hata Kodları](#7-hata-kodları)

---

## 1. Kimlik Doğrulama (Authentication)

### 1.1 Kullanıcı Girişi (Login)

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "username": "johndoe",
  "email": "user@example.com"
}
```

**Hata Durumları:**
- `400 Bad Request`: "Invalid password"
- `500 Internal Server Error`: "User not found"

---

### 1.2 Kullanıcı Kaydı (Register)

**Endpoint:** `POST /api/auth/register`

**Request Body:**
```json
{
  "username": "johndoe",
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
"User registered successfully!"
```

**Hata Durumları:**
- `400 Bad Request`: "Error: Username is already taken!"
- `400 Bad Request`: "Error: Email is already in use!"

---

## 2. Görevler (Tasks)

> **Not:** Tüm task endpoint'leri JWT token ile kimlik doğrulama gerektirir.  
> Header: `Authorization: Bearer {token}`

### 2.1 Görev Oluşturma

**Endpoint:** `POST /api/tasks`

**Request Body:**
```json
{
  "title": "Proje raporunu hazırla",
  "description": "Q4 performans raporu",
  "dueDate": "2025-12-25T15:00:00",
  "priority": "HIGH",
  "status": "TODO",
  "urgent": true,
  "important": true,
  "context": "WORK",
  "isInbox": false,
  "isArchived": false,
  "userId": 1,
  "subMissionId": 3
}
```

**Response (200 OK):**
```json
{
  "id": 42,
  "title": "Proje raporunu hazırla",
  "description": "Q4 performans raporu",
  "dueDate": "2025-12-25T15:00:00",
  "priority": "HIGH",
  "status": "TODO",
  "urgent": true,
  "important": true,
  "context": "WORK",
  "isInbox": false,
  "isArchived": false,
  "completedAt": null,
  "createdAt": "2025-12-20T18:00:00",
  "userId": 1,
  "subMissionId": 3
}
```

**Alan Açıklamaları:**
- `priority`: `LOW`, `MEDIUM`, `HIGH`, `URGENT`
- `status`: `TODO`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`
- `context`: Görevin bağlamı (örn: "WORK", "PERSONAL", "HEALTH")
- `urgent`: Acil mi? (Covey Matrix için)
- `important`: Önemli mi? (Covey Matrix için)
- `isInbox`: Inbox'ta mı?
- `isArchived`: Arşivlenmiş mi?

---

### 2.2 Kullanıcının Tüm Görevlerini Getirme

**Endpoint:** `GET /api/tasks/user/{userId}`

**Path Parameters:**
- `userId` (Long): Kullanıcı ID'si

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Görev 1",
    "description": "Açıklama 1",
    "dueDate": "2025-12-25T15:00:00",
    "priority": "HIGH",
    "status": "TODO",
    "urgent": true,
    "important": true,
    "context": "WORK",
    "isInbox": false,
    "isArchived": false,
    "completedAt": null,
    "createdAt": "2025-12-20T10:00:00",
    "userId": 1,
    "subMissionId": null
  },
  {
    "id": 2,
    "title": "Görev 2",
    "description": "Açıklama 2",
    "dueDate": "2025-12-26T10:00:00",
    "priority": "MEDIUM",
    "status": "COMPLETED",
    "urgent": false,
    "important": true,
    "context": "PERSONAL",
    "isInbox": false,
    "isArchived": true,
    "completedAt": "2025-12-20T14:30:00",
    "createdAt": "2025-12-19T09:00:00",
    "userId": 1,
    "subMissionId": 5
  }
]
```

---

### 2.3 Görev Güncelleme

**Endpoint:** `PUT /api/tasks/{taskId}`

**Path Parameters:**
- `taskId` (Long): Görev ID'si

**Request Body:**
```json
{
  "id": 42,
  "title": "Güncellenmiş başlık",
  "description": "Güncellenmiş açıklama",
  "dueDate": "2025-12-26T15:00:00",
  "priority": "URGENT",
  "status": "IN_PROGRESS",
  "urgent": true,
  "important": true,
  "context": "WORK",
  "isInbox": false,
  "isArchived": false,
  "userId": 1,
  "subMissionId": 3
}
```

**Response (200 OK):**
```json
{
  "id": 42,
  "title": "Güncellenmiş başlık",
  "description": "Güncellenmiş açıklama",
  "dueDate": "2025-12-26T15:00:00",
  "priority": "URGENT",
  "status": "IN_PROGRESS",
  "urgent": true,
  "important": true,
  "context": "WORK",
  "isInbox": false,
  "isArchived": false,
  "completedAt": null,
  "createdAt": "2025-12-20T18:00:00",
  "userId": 1,
  "subMissionId": 3
}
```

**Önemli Notlar:**
- Görev tamamlandığında `status: "COMPLETED"` ve `completedAt` otomatik set edilir
- Görev arşivlenirken `isArchived: true` gönderilmelidir

---

### 2.4 Görev Silme

**Endpoint:** `DELETE /api/tasks/{taskId}`

**Path Parameters:**
- `taskId` (Long): Görev ID'si

**Response (204 No Content)**

---

## 3. Misyon İfadeleri (Mission Statements)

### 3.1 Misyon Oluşturma

**Endpoint:** `POST /api/missions`

**Query Parameters:**
- `userId` (Long): Kullanıcı ID'si

**Request Body:**
```json
"Hayatımın amacı, teknoloji ile insanların yaşamını kolaylaştırmak ve sürekli gelişim göstermektir."
```

**Content-Type:** `application/json` (String olarak gönderilir)

**Response (200 OK):**
```json
{
  "id": 1,
  "content": "Hayatımın amacı, teknoloji ile insanların yaşamını kolaylaştırmak ve sürekli gelişim göstermektir.",
  "version": 1,
  "userId": 1,
  "subMissions": []
}
```

---

### 3.2 Kullanıcının Tüm Misyonlarını Getirme

**Endpoint:** `GET /api/missions/user/{userId}`

**Path Parameters:**
- `userId` (Long): Kullanıcı ID'si

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "content": "Hayatımın amacı...",
    "version": 2,
    "userId": 1,
    "subMissions": [
      {
        "id": 1,
        "title": "Kariyer Gelişimi",
        "description": "Senior developer olmak"
      },
      {
        "id": 2,
        "title": "Sağlık",
        "description": "Haftada 3 gün spor yapmak"
      }
    ]
  }
]
```

---

### 3.3 Alt Misyon Ekleme

**Endpoint:** `POST /api/missions/{missionId}/submissions`

**Path Parameters:**
- `missionId` (Long): Ana misyon ID'si

**Request Body:**
```json
{
  "title": "Yeni Alt Misyon",
  "description": "Alt misyon açıklaması"
}
```

**Response (200 OK):**
```json
{
  "id": 5,
  "title": "Yeni Alt Misyon",
  "description": "Alt misyon açıklaması"
}
```

---

### 3.4 Misyon Güncelleme

**Endpoint:** `PUT /api/missions/{missionId}`

**Path Parameters:**
- `missionId` (Long): Misyon ID'si

**Request Body:**
```json
"Güncellenmiş misyon ifadesi içeriği"
```

**Content-Type:** `application/json` (String olarak gönderilir)

**Response (200 OK):**
```json
{
  "id": 1,
  "content": "Güncellenmiş misyon ifadesi içeriği",
  "version": 3,
  "userId": 1,
  "subMissions": [...]
}
```

**Not:** Her güncelleme `version` numarasını artırır.

---

### 3.5 Misyon Silme

**Endpoint:** `DELETE /api/missions/{missionId}`

**Path Parameters:**
- `missionId` (Long): Misyon ID'si

**Response (204 No Content)**

---

### 3.6 Alt Misyon Silme

**Endpoint:** `DELETE /api/missions/submissions/{subMissionId}`

**Path Parameters:**
- `subMissionId` (Long): Alt misyon ID'si

**Response (204 No Content)**

---

## 4. Takvim Entegrasyonu (Calendar)

### 4.1 Google Calendar Yetkilendirme URL'i Alma

**Endpoint:** `GET /api/calendar/auth-url`

**Response (200 OK):**
```json
{
  "url": "https://accounts.google.com/o/oauth2/auth?client_id=..."
}
```

**Hata Durumu (500):**
```json
{
  "error": "Hata mesajı"
}
```

---

### 4.2 Takvim Senkronizasyonu

**Endpoint:** `POST /api/calendar/sync`

**Request Body:**
```json
{
  "code": "4/0AY0e-g7X..."
}
```

**Response (200 OK):**
```json
"Sync started"
```

**Hata Durumu (400):**
```json
"Auth code is required"
```

---

## 5. Analitik (Analytics)

### 5.1 Haftalık İstatistikler

**Endpoint:** `GET /api/analytics/weekly/{userId}`

**Path Parameters:**
- `userId` (Long): Kullanıcı ID'si

**Response (200 OK):**
```json
{
  "totalTasks": 25,
  "completedTasks": 18,
  "completionRate": 72
}
```

**Alan Açıklamaları:**
- `totalTasks`: Toplam görev sayısı
- `completedTasks`: Tamamlanan görev sayısı
- `completionRate`: Tamamlanma oranı (%)

---

### 5.2 Haftalık İnceleme Oluşturma

**Endpoint:** `POST /api/analytics/review`

**Query Parameters:**
- `userId` (Long): Kullanıcı ID'si

**Request Body:**
```json
"Bu hafta çok verimli geçti. 18 görev tamamladım ve yeni bir proje başlattım."
```

**Content-Type:** `application/json` (String olarak gönderilir)

**Response (200 OK):**
```json
{
  "id": 1,
  "userId": 1,
  "weekStart": "2025-12-16T00:00:00",
  "weekEnd": "2025-12-22T23:59:59",
  "notes": "Bu hafta çok verimli geçti. 18 görev tamamladım ve yeni bir proje başlattım.",
  "createdAt": "2025-12-20T18:00:00"
}
```

---

## 6. Veri Modelleri

### TaskDTO
```typescript
{
  id?: number;
  title: string;
  description?: string;
  dueDate?: string; // ISO 8601 format
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  status: 'TODO' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  urgent: boolean;
  important: boolean;
  context?: string;
  isInbox: boolean;
  isArchived: boolean;
  completedAt?: string; // ISO 8601 format
  createdAt?: string; // ISO 8601 format
  userId: number;
  subMissionId?: number;
}
```

### MissionStatementDTO
```typescript
{
  id?: number;
  content: string;
  version?: number;
  userId: number;
  subMissions?: SubMissionDTO[];
}
```

### SubMissionDTO
```typescript
{
  id?: number;
  title: string;
  description?: string;
}
```

### AuthResponse
```typescript
{
  token: string;
  id: number;
  username: string;
  email: string;
}
```

### WeeklyStatsDTO
```typescript
{
  totalTasks: number;
  completedTasks: number;
  completionRate: number; // 0-100 arası
}
```

---

## 7. Hata Kodları

| HTTP Kodu | Açıklama |
|-----------|----------|
| 200 | Başarılı istek |
| 201 | Kaynak başarıyla oluşturuldu |
| 204 | Başarılı istek, içerik yok (silme işlemleri) |
| 400 | Geçersiz istek (validation hatası) |
| 401 | Yetkisiz erişim (token geçersiz/eksik) |
| 403 | Yasak (yetki yok) |
| 404 | Kaynak bulunamadı |
| 500 | Sunucu hatası |

---

## 8. Kimlik Doğrulama (JWT)

### Token Kullanımı

Tüm korumalı endpoint'ler için HTTP header'a JWT token eklenmeli:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token Süresi
- Varsayılan: 24 saat (86400000 ms)
- Süre dolduğunda kullanıcı tekrar login olmalı

---

## 9. Önemli Notlar

### Tarih Formatı
Tüm tarihler **ISO 8601** formatında gönderilmeli:
```
2025-12-25T15:30:00
```

### Covey Matrix Kullanımı
Görevler için `urgent` ve `important` alanları kullanılarak Covey Matrix'e göre sınıflandırma yapılabilir:
- **Q1 (Acil & Önemli):** `urgent: true, important: true`
- **Q2 (Önemli ama Acil Değil):** `urgent: false, important: true`
- **Q3 (Acil ama Önemli Değil):** `urgent: true, important: false`
- **Q4 (Ne Acil Ne Önemli):** `urgent: false, important: false`

### Inbox İşleme
- Yeni görevler önce `isInbox: true` ile oluşturulabilir
- Kullanıcı işlediğinde `isInbox: false` yapılır

### Arşivleme
- Tamamlanan görevler `isArchived: true` yapılarak arşivlenebilir
- Arşivlenen görevler normal listelerde görünmez

---

## 10. Örnek Kullanım Senaryoları

### Senaryo 1: Yeni Kullanıcı Kaydı ve Login

```bash
# 1. Kayıt
POST /api/auth/register
{
  "username": "ahmet",
  "email": "ahmet@example.com",
  "password": "securepass123"
}

# 2. Login
POST /api/auth/login
{
  "email": "ahmet@example.com",
  "password": "securepass123"
}

# Response:
{
  "token": "eyJhbGc...",
  "id": 5,
  "username": "ahmet",
  "email": "ahmet@example.com"
}
```

### Senaryo 2: Görev Oluşturma ve Tamamlama

```bash
# 1. Görev oluştur
POST /api/tasks
Authorization: Bearer eyJhbGc...
{
  "title": "Rapor hazırla",
  "dueDate": "2025-12-25T17:00:00",
  "priority": "HIGH",
  "status": "TODO",
  "urgent": true,
  "important": true,
  "userId": 5
}

# 2. Görevi tamamla
PUT /api/tasks/42
Authorization: Bearer eyJhbGc...
{
  "id": 42,
  "title": "Rapor hazırla",
  "status": "COMPLETED",
  "isArchived": true,
  ...
}
```

### Senaryo 3: Misyon ve Alt Misyon Yönetimi

```bash
# 1. Misyon oluştur
POST /api/missions?userId=5
Authorization: Bearer eyJhbGc...
"2025 yılında kariyerimde ilerleme kaydetmek"

# 2. Alt misyon ekle
POST /api/missions/1/submissions
Authorization: Bearer eyJhbGc...
{
  "title": "Senior Developer",
  "description": "6 ay içinde terfi almak"
}
```

---

## 11. Test Endpoint'leri

Development ortamında test için kullanılabilecek örnek curl komutları:

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"test123"}'

# Görevleri listele
curl -X GET http://localhost:8080/api/tasks/user/1 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# Yeni görev oluştur
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test görevi",
    "priority": "MEDIUM",
    "status": "TODO",
    "urgent": false,
    "important": true,
    "isInbox": false,
    "isArchived": false,
    "userId": 1
  }'
```

---

## 12. İletişim ve Destek

Sorularınız için:
- **Backend Repository:** https://github.com/SmartPersonalTaskManager/backend
- **Issue Tracker:** GitHub Issues

---

**Son Güncelleme:** 2025-12-20  
**API Versiyonu:** 1.0.0
