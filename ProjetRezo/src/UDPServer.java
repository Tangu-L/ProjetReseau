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

                // Stocke l'adresse du client si nouveau
                ClientInfo newClient = new ClientInfo(clientAddress, clientPort);
                clients.add(newClient);

                System.out.println("Message reçu de " + clientAddress + ":" + clientPort + " → " + message);

                // Envoie le message à tous les autres clients
                broadcastMessage(serverSocket, message, newClient);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void broadcastMessage(DatagramSocket serverSocket, String message, ClientInfo sender) {
        byte[] sendData = message.getBytes();
        for (ClientInfo client : clients) {
            // Ne pas renvoyer le message à l'expéditeur
            if (!client.equals(sender)) {
                try {
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, client.getAddress(), client.getPort());
                    serverSocket.send(sendPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

// Classe pour stocker les infos d'un client (IP + port)
class ClientInfo {
    private final InetAddress address;
    private final int port;

    public ClientInfo(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
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
