import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    public static void main(String[] args) {
<<<<<<< Updated upstream
        String serverIP = "192.168.104.166"; // Adresse IP du serveur
=======
        String serverIP = "192.168.104.166"; // Adresse du serveur
>>>>>>> Stashed changes
        int serverPort = 12345; // Port du serveur

        try (DatagramSocket clientSocket = new DatagramSocket()) { // Création du socket client
            // Préparer le message à envoyer
<<<<<<< Updated upstream
            String message = "MEW MEW MEW MEW "; // Message envoyé au serveur
=======
            String message = "Bonjour ";
>>>>>>> Stashed changes
            byte[] sendData = message.getBytes();
            InetAddress serverAddress = InetAddress.getByName(serverIP); // Convertit l'adresse en objet InetAddress
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
<<<<<<< Updated upstream

            // Envoyer le message au serveur
=======
😂
            // Envoyer le message
>>>>>>> Stashed changes
            clientSocket.send(sendPacket);
            System.out.println("Message envoyé au serveur : " + message);

            // Réception de la réponse du serveur
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket); // Attente de la réponse

            // Afficher la réponse
            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Réponse du serveur : " + response);
        } catch (Exception e) {
            e.printStackTrace(); // Gestion des erreurs
        }
    }
}
