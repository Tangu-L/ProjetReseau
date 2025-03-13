import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {
    public static void main(String[] args) {
        int port = 12345; // Port d'écoute du serveur

        try (DatagramSocket serverSocket = new DatagramSocket(port)) { // Création du socket serveur
            System.out.println("Serveur UDP en écoute sur le port " + port);

            while (true) { // Boucle infinie pour écouter plusieurs clients
                // Réception du message
                byte[] receiveData = new byte[1024]; //stocker les données reçues
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket); // Attente d'un message client

                // Infos du client
                InetAddress clientAddress = receivePacket.getAddress(); // Adresse IP du client
                int clientPort = receivePacket.getPort(); // Port du client
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                System.out.println("Nouveau client : " + clientAddress + ":" + clientPort + " → " + message);

                // Répondre au client
                String response = "Serveur RX302 ready"; // Message de réponse
                byte[] sendData = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                serverSocket.send(sendPacket); // Envoi de la réponse au client
            }
        } catch (Exception e) {
            e.printStackTrace(); // Gestion des erreurs
        }
    }
}
