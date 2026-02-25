# Vcard API – Backend + Frontend

Application de cartes de visite numériques : backend Spring Boot (PostgreSQL), frontend Angular, partage par lien et QR code.

## Prérequis

- **Développement local** : Java 17+, Node 20+, PostgreSQL 16 (ou Docker pour la base seule)
- **Docker** : Docker et Docker Compose (ou `docker-compose` v1)

## Développement local

### Base de données PostgreSQL

Créer une base et un utilisateur :

```sql
CREATE DATABASE vcard;
CREATE USER vcard WITH PASSWORD 'vcard';
GRANT ALL PRIVILEGES ON DATABASE vcard TO vcard;
```

Ou démarrer uniquement PostgreSQL avec Docker :

```bash
docker run -d --name postgres-vcard -e POSTGRES_DB=vcard -e POSTGRES_USER=vcard -e POSTGRES_PASSWORD=vcard -p 5432:5432 postgres:16-alpine
```

### Backend (Spring Boot)

```bash
# À la racine du repo (vcard_api)
./mvnw spring-boot:run
```

API disponible sur **http://localhost:8888**. Les variables d’environnement optionnelles : `POSTGRES_HOST`, `POSTGRES_PORT`, `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `SERVER_PORT`.

### Frontend (Angular)

```bash
cd vcard
npm install
npm start
```

Ouvrir **http://localhost:4200**. En mode développement, le front appelle le backend sur `http://localhost:8888` (voir `vcard/src/app/core/environments/environment.development.ts`).

## Docker (stack complète)

À la racine du repo :

```bash
# Avec Docker Compose v2
docker compose up -d --build

# Ou avec Docker Compose v1
docker-compose up -d --build
```

- **Frontend** : http://localhost:4201 (nginx sert l’app et proxy `/api` vers le backend)
- **Backend** : http://localhost:8888
- **PostgreSQL** : port 5432 (interne au réseau Docker)

Arrêt :

```bash
docker compose down
# ou
docker-compose down
```

Les données PostgreSQL sont conservées dans le volume `postgres_data`.

## API (résumé)

| Méthode | Chemin | Description |
|--------|--------|-------------|
| POST | `/api/cards` | Créer une carte (retourne `ownerToken` à stocker côté client) |
| GET | `/api/cards/me` | Récupérer « ma carte » (header `X-Owner-Token`) |
| PUT | `/api/cards/me` | Mettre à jour « ma carte » (header `X-Owner-Token`) |
| GET | `/api/cards/{id}` | Récupérer une carte par id (public, lien partagé / QR) |
| POST | `/api/cards/{id}/save` | Enregistrer la carte dans « mes contacts » (header `X-Device-Id`) |
| GET | `/api/cards/{id}/saved` | Vérifier si la carte est déjà enregistrée (header `X-Device-Id`) |
| GET | `/api/contacts` | Liste des contacts enregistrés (header `X-Device-Id`) |
| DELETE | `/api/contacts/{id}` | Supprimer un contact (header `X-Device-Id`) |

Le frontend envoie automatiquement `X-Owner-Token` (après création de carte) et `X-Device-Id` (généré une fois par appareil).
