import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public class UDPServer {
    private static final int PORT = 12345;
    private static final Set<ClientInfo> clients = new HashSet<>();

    public static void main(String[] args) {
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
            System.out.println("Serveur UDP en écoute sur le port " + PORT);

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                ClientInfo sender = getClient(clientAddress, clientPort);

                if (message.startsWith("___PSEUDO:")) {
                    // Gestion de l'enregistrement du pseudo
                    String pseudo = message.substring(10).trim();
                    if (sender == null) {
                        clients.add(new ClientInfo(clientAddress, clientPort, pseudo));
                        System.out.println("Nouveau client : " + pseudo + " (" + clientAddress + ":" + clientPort + ")");
                        broadcastMessage(serverSocket, "🔵 " + pseudo + " a rejoint la chatroom.", null);
                    } else {
                        sender.setPseudo(pseudo);
                        System.out.println("Mise à jour du pseudo : " + pseudo + " (" + clientAddress + ":" + clientPort + ")");
                    }
                } else if (message.equalsIgnoreCase("/quit")) {
                    // Gestion de la déconnexion
                    if (sender != null) {
                        clients.remove(sender);
                        System.out.println("Client déconnecté : " + sender.getPseudo());
                        broadcastMessage(serverSocket, "❌ " + sender.getPseudo() + " a quitté la chatroom.", null);
                    }
                } else if (message.startsWith("/mp ")) {
                    // Gestion des messages privés
                    handlePrivateMessage(serverSocket, sender, message.substring(4).trim());
                } else {
                    // Message normal diffusé à tout le monde
                    if (sender != null) {
                        String messageAvecPseudo = sender.getPseudo() + ": " + message;
                        System.out.println("Message reçu de " + sender.getPseudo() + " → " + message);
                        broadcastMessage(serverSocket, messageAvecPseudo, sender);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handlePrivateMessage(DatagramSocket serverSocket, ClientInfo sender, String content) {
        if (sender == null) return;

        String[] parts = content.split(" ", 2);
        if (parts.length < 2) {
            sendMessage(serverSocket, sender, "⚠️ Format incorrect. Utilisation : /mp [pseudo] [message]");
            return;
        }

        String targetPseudo = parts[0];
        String privateMessage = parts[1];

        ClientInfo recipient = getClientByPseudo(targetPseudo);

        if (recipient == null) {
            sendMessage(serverSocket, sender, "⚠️ Le pseudo '" + targetPseudo + "' n'existe pas.");
        } else {
            String formattedMessage = "(MP) " + sender.getPseudo() + " → " + privateMessage;
            sendMessage(serverSocket, recipient, formattedMessage);
            sendMessage(serverSocket, sender, "(MP envoyé à " + recipient.getPseudo() + ") " + privateMessage);
            System.out.println("Message privé de " + sender.getPseudo() + " à " + recipient.getPseudo() + " → " + privateMessage);
        }
    }

    private static void broadcastMessage(DatagramSocket serverSocket, String message, ClientInfo sender) {
        byte[] sendData = message.getBytes();
        for (ClientInfo client : clients) {
            if (sender == null || !client.equals(sender)) {
                sendMessage(serverSocket, client, message);
            }
        }
    }

    private static void sendMessage(DatagramSocket serverSocket, ClientInfo client, String message) {
        byte[] sendData = message.getBytes();
        try {
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, client.getAddress(), client.getPort());
            serverSocket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ClientInfo getClient(InetAddress address, int port) {
        for (ClientInfo client : clients) {
            if (client.getAddress().equals(address) && client.getPort() == port) {
                return client;
            }
        }
        return null;
    }

    private static ClientInfo getClientByPseudo(String pseudo) {
        for (ClientInfo client : clients) {
            if (client.getPseudo().equalsIgnoreCase(pseudo)) {
                return client;
            }
        }
        return null;
    }
}

class ClientInfo {
    private final InetAddress address;
    private final int port;
    private String pseudo;

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
