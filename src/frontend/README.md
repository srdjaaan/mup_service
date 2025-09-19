# eUprava Frontend

Frontend aplikacija za eUprava sistem - upravljanje dokumentima i zahtevima.

## Funkcionalnosti

### Za sve korisnike:
- **Login/Registracija** - Prijava i registracija korisnika
- **JWT Token Management** - Automatsko upravljanje tokenima
- **Profil stranica** - Pregled ličnih podataka i dokumenata
- **Responsive Design** - Prilagođeno za sve uređaje

### Za građane (GRADJANIN):
- **Kreiranje zahteva** - Slanje zahteva za kreiranje lične karte
- **Produženje lične karte** - Slanje zahteva za produženje istekle lične karte
- **Pregled zahteva** - Pregled svih poslatih zahteva sa statusom
- **Validacija** - Automatska validacija pre slanja zahteva

### Za policajce (POLICAJAC):
- **Pregled zahteva** - Pregled svih zahteva na čekanju
- **Odobravanje/Odbijanje** - Obrađivanje zahteva građana
- **Modal forma** - Detaljno pregledanje i komentarisanje zahteva
- **Automatsko osvežavanje** - Lista se automatski osvežava nakon obrađivanja

## Tehnologije

- **React 18** - Frontend framework
- **React Router** - Ruting
- **Axios** - HTTP klijent
- **CSS3** - Stilizovanje
- **JWT** - Autentifikacija

## Instalacija i pokretanje

1. Instaliraj dependencies:
```bash
npm install
```

2. Pokreni development server:
```bash
npm start
```

3. Aplikacija će biti dostupna na `http://localhost:3000`

## API Endpoints

### Auth Service (port 8080)
- `POST /api/auth/login` - Prijava
- `POST /api/auth/register` - Registracija
- `GET /api/auth/users` - Lista korisnika (policajci)
- `GET /api/auth/user/{jmbg}` - Korisnik po JMBG
- `GET /api/auth/user/{jmbg}/documents` - Korisnik sa dokumentima

### MUP Service (port 8081)
- `POST /api/zahtevi/kreiraj` - Kreiranje zahteva
- `GET /api/zahtevi/moji-zahtevi` - Moji zahtevi (građani)
- `GET /api/zahtevi/na-cekanju` - Zahtevi na čekanju (policajci)
- `POST /api/zahtevi/{id}/odobri` - Odobravanje zahteva
- `GET /api/zahtevi/{id}` - Zahtev po ID
- `GET /api/zahtevi/korisnik/{jmbg}/validiraj-licnu-kartu` - Validacija lične karte
- `GET /api/zahtevi/korisnik/{jmbg}/dokumenti` - Dokumenti korisnika

## Struktura projekta

```
src/
├── api/
│   ├── axios.js          # Osnovni axios instance sa interceptorima
│   └── mupApi.js         # MUP service API funkcije
├── components/
│   ├── AuthForms.css     # Stilovi za login/register forme
│   ├── LoginForm.js      # Login komponenta
│   ├── Navbar.css        # Stilovi za navigaciju
│   ├── Navbar.js         # Navigaciona komponenta
│   ├── PolicajacPanel.css # Stilovi za policajski panel
│   ├── PolicajacPanel.js  # Policajski panel komponenta
│   ├── Profile.css       # Stilovi za profil stranicu
│   ├── Profile.js        # Profil komponenta
│   ├── RegisterForm.js   # Registracija komponenta
│   ├── ZahtevCard.css    # Stilovi za karticu zahteva
│   └── ZahtevCard.js     # Karta za slanje zahteva
├── context/
│   └── AuthContext.js    # Context za upravljanje autentifikacijom
├── pages/
│   ├── Home.css          # Stilovi za home stranicu
│   └── Home.js           # Home stranica
├── App.js                # Glavna App komponenta
└── index.js              # Entry point
```

## Autentifikacija

Aplikacija koristi JWT token za autentifikaciju. Token se automatski:
- Dodaje u sve API zahteve
- Čuva u localStorage
- Briše pri logout-u ili isteku
- Dekodira za prikaz korisničkih podataka

## Responsive Design

Aplikacija je potpuno responzivna i optimizovana za:
- Desktop računare
- Tablete
- Mobilne telefone

## Sigurnost

- Automatsko brisanje tokena pri 401 grešci
- Validacija podataka na frontend-u
- Sigurno čuvanje tokena u localStorage
- CSRF zaštita kroz JWT token