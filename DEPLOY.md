# Déploiement sur serveur Ubuntu

Guide pour lancer l’application sur un serveur Ubuntu (ex. `/opt/testVcard/`) et y accéder depuis votre machine.

---

## 1. Sur le serveur Ubuntu

### 1.1 Installer Docker et Docker Compose

```bash
# Mise à jour et paquets de base
sudo apt update && sudo apt install -y ca-certificates curl

# Docker (script officiel)
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER
# Déconnexion/reconnexion ou « newgrp docker » pour prendre le groupe

# Docker Compose v2 (inclus avec Docker sur les versions récentes)
sudo apt install -y docker-compose-plugin
# Ou v1 :
# sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
# sudo chmod +x /usr/local/bin/docker-compose
```

### 1.2 Lancer l’application

```bash
cd /opt/testVcard

# Premier lancement (build + démarrage)
sudo docker compose up -d --build

# Ou avec l’ancienne commande
sudo docker-compose up -d --build
```

Le build peut prendre plusieurs minutes (Maven + npm). Une fois terminé :

- **Frontend** : port **4201**
- **Backend** : port **8888** (HTTP uniquement ; ne pas ouvrir en `https://` dans le navigateur, sinon erreur « Invalid character in method name »)
- **PostgreSQL** : port **5433** (optionnel, pour accès direct à la base)

### 1.3 Ouvrir les ports dans le pare-feu (si UFW est actif)

Pour accéder à l’app depuis une autre machine :

```bash
sudo ufw allow 4201/tcp   # application web
sudo ufw allow 8888/tcp   # optionnel, si vous voulez l’API directement
sudo ufw reload
sudo ufw status
```

---

## 2. Accéder depuis votre machine

Depuis un navigateur sur **votre PC** (ou tout appareil sur le même réseau / internet selon votre config) :

- **URL de l’application** :  
  **`http://<IP_DU_SERVEUR>:4201`**

Exemples :

- Serveur en local (même réseau) : `http://192.168.1.100:4201`
- Serveur avec IP publique : `http://203.0.113.50:4201`
- Si vous êtes déjà sur le serveur : `http://localhost:4201`

Pour connaître l’IP du serveur :

```bash
ip -4 addr show | grep inet
# ou
hostname -I
```

---

## 3. Commandes utiles

| Action | Commande |
|--------|----------|
| Voir les conteneurs | `sudo docker compose ps` |
| Voir les logs | `sudo docker compose logs -f` |
| Arrêter | `sudo docker compose down` |
| Redémarrer (sans rebuild) | `sudo docker compose restart` |
| Rebuild + redémarrer | `sudo docker compose up -d --build` |

---

## 4. Dépannage

**Tomcat affiche « started on port 8080 » au lieu de 8888**  
→ Reconstruisez l’image et redémarrez : `sudo docker compose up -d --build`. Vérifiez que `SERVER_PORT: 8888` est bien défini pour le service `backend` dans `docker-compose.yml`.

**Erreur « Invalid character found in method name » (octets 0x16 0x03 0x01…)**  
→ Le backend reçoit du **HTTPS** alors qu’il n’accepte que du **HTTP**. Ne pas ouvrir l’API directement en `https://...:8888` dans le navigateur. Utiliser l’application via le frontend en **`http://<IP>:4201`** : les appels `/api` passent alors en HTTP entre nginx et le backend.

**« Enregistrer et générer » ne réagit pas ou erreur**  
Le front envoie les requêtes vers **`/api/cards`** (même origine). Vérifier que le backend est up : `sudo docker compose ps`. Voir les logs : `sudo docker compose logs backend`. Dans le navigateur (F12 → Console), un message `[API] POST /api/cards → erreur 502` signifie que le backend est injoignable.

---

## 4. Démarrer au boot (optionnel)

Pour que la stack redémarre après un reboot du serveur :

```bash
cd /opt/testVcard
sudo docker compose up -d --build
```

Puis activez le service Docker au démarrage (souvent déjà activé) :

```bash
sudo systemctl enable docker
sudo systemctl start docker
```

Les conteneurs lancés avec `docker compose up -d` peuvent être configurés pour redémarrer seuls : on peut ajouter `restart: unless-stopped` dans le `docker-compose.yml` si besoin.

---

## 5. En cas de problème

- **Impossible d’accéder à `http://IP:4201`**  
  Vérifiez que les conteneurs tournent (`docker compose ps`), que le port 4201 est ouvert (ufw, pare-feu cloud), et que vous utilisez la bonne IP.

- **Erreur de build**  
  Vérifiez les logs : `sudo docker compose logs backend` ou `sudo docker compose logs frontend`. Pour le frontend, un échec réseau pendant `npm ci` peut nécessiter un nouveau lancement ou un miroir npm (voir README / Dockerfile).

- **Base de données réinitialisée**  
  `docker compose down -v` supprime les volumes (données PostgreSQL). Pour conserver les données, utilisez uniquement `docker compose down` (sans `-v`).

- **Ça marche en local mais pas sur le serveur (cache)**  
  Le navigateur peut garder en cache l’ancienne version de l’app (ancien `index.html` et anciens JS). Après un déploiement :
  1. **Rebuild** : `sudo docker compose up -d --build` pour reconstruire le frontend avec la dernière version.
  2. **Forcer le rechargement** côté utilisateur : Ctrl+Shift+R (ou Cmd+Shift+R sur Mac), ou vider le cache du navigateur pour le site.
  3. Le serveur nginx envoie maintenant `Cache-Control: no-store` sur `index.html`, donc à partir du prochain déploiement les visiteurs récupéreront la nouvelle version sans ancien cache.
