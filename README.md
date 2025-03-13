# UDP Chat Multithread

LASNE Tanguy
GUETTOUF Yanis

## Description
Ce projet est une application de chat utilisant le protocole UDP. Il permet à plusieurs clients de communiquer via un serveur centralisé. Chaque client est géré dans un thread séparé pour assurer une gestion efficace des connexions simultanées.

## Fonctionnement
Le serveur écoute sur un port spécifique et reçoit des messages des clients. Lorsqu’un client envoie un message, le serveur crée un thread dédié à son traitement. Chaque client est identifié par son adresse IP et son port, stocké dans une structure de données adaptée.

### Gestion des clients
- Lorsqu’un client se connecte, il envoie son pseudo au serveur.
- Le serveur stocke les clients actifs dans une structure `Set<ClientInfo>`.
- Chaque message reçu est traité dans un thread séparé.
- Le serveur relaie les messages aux autres clients en fonction de la commande reçue.

### Commandes
- **Message global** : Tout message non préfixé est diffusé à tous les clients connectés.
- **Message privé** : La commande `/mp [pseudo] [message]` permet d’envoyer un message à un utilisateur spécifique.
- **Déconnexion** : La commande `/quit` supprime le client de la liste des utilisateurs connectés et informe les autres.

## Détails techniques
### Serveur (`UDPServer.java`)
- Utilise `DatagramSocket` pour écouter les paquets entrants.
- Stocke les clients sous forme d’objets `ClientInfo` contenant leur adresse IP, port et pseudo.
- Gère les messages en créant des threads `ClientHandler` pour éviter de bloquer le serveur principal.
- Diffuse les messages aux clients connectés.
- Supprime un client lorsqu’il envoie la commande `/quit`.

### Client (`UDPClient.java`)
- Envoie un pseudo au serveur à la connexion.
- Lance un thread pour écouter les messages du serveur.
- Permet d’envoyer des messages et des commandes au serveur.
- Se déconnecte proprement avec `/quit` en fermant la connexion.

