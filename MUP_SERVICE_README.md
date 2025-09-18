# MUP Service - Instrukcije za testiranje

## Pokretanje servisa

1. **Pokretanje svih servisa:**
```bash
docker-compose up --build
```

2. **Servisi će biti dostupni na:**
   - Auth Service: http://localhost:8080
   - MUP Service: http://localhost:8081
   - PostgreSQL: localhost:5432

## Testiranje preko Postman-a

### 1. Registracija korisnika

**POST** `http://localhost:8080/api/auth/register`

**Body (JSON):**
```json
{
  "jmbg": "1234567890123",
  "name": "Marko",
  "lastname": "Marković",
  "username": "marko123",
  "password": "password123",
  "birthday": "1990-01-01",
  "placeOfBirth": "Beograd",
  "role": "GRADJANIN",
  "gender": "MALE"
}
```

**Registracija policajca:**
```json
{
  "jmbg": "9876543210987",
  "name": "Petar",
  "lastname": "Petrović",
  "username": "petar123",
  "password": "password123",
  "birthday": "1985-05-15",
  "placeOfBirth": "Novi Sad",
  "role": "POLICAJAC",
  "gender": "MALE"
}
```

### 2. Login korisnika

**POST** `http://localhost:8080/api/auth/login`

**Body (JSON):**
```json
{
  "username": "marko123",
  "password": "password123"
}
```

**Response će sadržavati JWT token:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 3. Kreiranje zahteva za ličnu kartu (GRADJANIN)

**POST** `http://localhost:8081/api/zahtevi/kreiraj`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "tipDokumenta": "LICNA_KARTA",
  "razlog": "Prva lična karta"
}
```

### 4. Pregled svojih zahteva (GRADJANIN)

**GET** `http://localhost:8081/api/zahtevi/moji-zahtevi`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

### 5. Pregled zahteva na čekanju (POLICAJAC)

**GET** `http://localhost:8081/api/zahtevi/na-cekanju`

**Headers:**
```
Authorization: Bearer <POLICAJAC_JWT_TOKEN>
```

### 6. Odobravanje/odbijanje zahteva (POLICAJAC)

**POST** `http://localhost:8081/api/zahtevi/{zahtevId}/odobri`

**Headers:**
```
Authorization: Bearer <POLICAJAC_JWT_TOKEN>
Content-Type: application/json
```

**Body za odobravanje:**
```json
{
  "status": "ODOBREN",
  "komentar": "Zahtev je odobren"
}
```

**Body za odbijanje:**
```json
{
  "status": "ODBIJEN",
  "komentar": "Nedostaju potrebni dokumenti"
}
```

### 7. Pregled pojedinačnog zahteva

**GET** `http://localhost:8081/api/zahtevi/{zahtevId}`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

## Scenariji testiranja

### Scenario 1: Uspešan tok
1. Registruj građanina
2. Login kao građanin
3. Kreiraj zahtev za ličnu kartu
4. Registruj policajca
5. Login kao policajac
6. Pregledaj zahteve na čekanju
7. Odobri zahtev
8. Login kao građanin i pregledaj svoje zahteve

### Scenario 2: Odbijanje zahteva
1. Kreiraj zahtev kao građanin
2. Login kao policajac
3. Odbij zahtev sa komentarom
4. Login kao građanin i pregledaj status

## Napomene

- JWT token je važeći 24 sata
- Lična karta se automatski kreira kada je zahtev odobren
- Broj lične karte se generiše automatski
- Svi endpoint-ovi zahtevaju validan JWT token
- Građani mogu samo kreirati zahteve i pregledati svoje
- Policajci mogu pregledati sve zahteve na čekanju i odobravati/odbijati ih

