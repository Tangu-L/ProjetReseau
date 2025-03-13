import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Serveur UDP permettant la communication entre plusieurs clients.
 * Utilise un pool de threads pour gérer chaque client indépendamment.
 */
public class UDPServer {
    private static final int PORT = 12345;
    private static final Set<ClientInfo> clients = new HashSet<>();
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
            System.out.println("Serveur UDP en écoute sur le port " + PORT);

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                // Gère chaque client dans un thread séparé
                threadPool.execute(new ClientHandler(serverSocket, receivePacket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Diffuse un message à tous les clients sauf l'expéditeur.
     *
     * @param serverSocket Socket du serveur.
     * @param message      Message à diffuser.
     * @param sender       Client qui envoie le message.
     */
    private static void broadcastMessage(DatagramSocket serverSocket, String message, ClientInfo sender) {
        byte[] sendData = message.getBytes();
        for (ClientInfo client : clients) {
            if (!client.equals(sender)) {
                sendMessage(serverSocket, client, message);
            }
        }
    }

    /**
     * Envoie un message à un client spécifique.
     *
     * @param serverSocket Socket du serveur.
     * @param client       Destinataire du message.
     * @param message      Contenu du message.
     */
    private static void sendMessage(DatagramSocket serverSocket, ClientInfo client, String message) {
        byte[] sendData = message.getBytes();
        try {
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, client.getAddress(), client.getPort());
            serverSocket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Recherche un client par son pseudo.
     *
     * @param pseudo Pseudo du client recherché.
     * @return ClientInfo correspondant au pseudo, ou null si inexistant.
     */
    private static ClientInfo getClientByPseudo(String pseudo) {
        for (ClientInfo client : clients) {
            if (client.getPseudo().equalsIgnoreCase(pseudo)) {
                return client;
            }
        }
        return null;
    }

    /**
     * Classe interne pour gérer chaque client dans un thread séparé.
     */
    private static class ClientHandler implements Runnable {
        private final DatagramSocket serverSocket;
        private final DatagramPacket receivePacket;

        /**
         * Constructeur du gestionnaire de client.
         *
         * @param serverSocket Socket du serveur.
         * @param receivePacket Paquet reçu contenant le message du client.
         */
        public ClientHandler(DatagramSocket serverSocket, DatagramPacket receivePacket) {
            this.serverSocket = serverSocket;
            this.receivePacket = receivePacket;
        }

        @Override
        public void run() {
            try {
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                ClientInfo sender = null;
                for (ClientInfo client : clients) {
                    if (client.getAddress().equals(clientAddress) && client.getPort() == clientPort) {
                        sender = client;
                        break;
                    }
                }

                if (message.startsWith("___PSEUDO:")) {
                    String pseudo = message.substring(10).trim();
                    if (sender == null) {
                        sender = new ClientInfo(clientAddress, clientPort, pseudo);
                        clients.add(sender);
                        System.out.println("Nouveau client : " + pseudo);
                        broadcastMessage(serverSocket, "🔵 " + pseudo + " a rejoint la chatroom.", null);
                    } else {
                        sender.setPseudo(pseudo);
                        System.out.println("Mise à jour du pseudo : " + pseudo);
                    }
                } else if (message.equalsIgnoreCase("/quit")) {
                    if (sender != null) {
                        clients.remove(sender);
                        System.out.println("Client déconnecté : " + sender.getPseudo());
                        broadcastMessage(serverSocket, "❌ " + sender.getPseudo() + " a quitté la chatroom.", null);
                    }
                } else if (message.startsWith("/mp ")) {
                    if (sender != null) {
                        String[] parts = message.substring(4).trim().split(" ", 2);
                        if (parts.length < 2) {
                            sendMessage(serverSocket, sender, "⚠️ Format incorrect : /mp [pseudo] [message]");
                        } else {
                            ClientInfo recipient = getClientByPseudo(parts[0]);
                            if (recipient == null) {
                                sendMessage(serverSocket, sender, "⚠️ Le pseudo '" + parts[0] + "' n'existe pas.");
                            } else {
                                String privateMessage = "(MP) " + sender.getPseudo() + " → " + parts[1];
                                sendMessage(serverSocket, recipient, privateMessage);
                                sendMessage(serverSocket, sender, "(MP envoyé à " + recipient.getPseudo() + ") " + parts[1]);
                            }
                        }
                    }
                } else {
                    if (sender != null) {
                        String formattedMessage = sender.getPseudo() + ": " + message;
                        System.out.println("Message de " + sender.getPseudo() + " → " + message);
                        broadcastMessage(serverSocket, formattedMessage, sender);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * Classe représentant un client avec son adresse, port et pseudo.
 */
class ClientInfo {
    private final InetAddress address;
    private final int port;
    private String pseudo;

    /**
     * Constructeur d'un client.
     *
     * @param address Adresse IP du client.
     * @param port Port UDP du client.
     * @param pseudo Pseudo du client.
     */
    public ClientInfo(InetAddress address, int port, String pseudo) {
        this.address = address;
        this.port = port;
        this.pseudo = pseudo;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ClientInfo that = (ClientInfo) obj;
        return port == that.port && address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return address.hashCode() + port;
    }
}
