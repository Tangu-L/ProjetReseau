import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    public static void main(String[] args) {
        String serverIP = "127.0.0.1"; // Adresse du serveur
        int serverPort = 12345; // Port du serveur

        try (DatagramSocket clientSocket = new DatagramSocket()) {
            // Préparer le message à envoyer
            String message = "Hello serveur RX302";
            byte[] sendData = message.getBytes();
            InetAddress serverAddress = InetAddress.getByName(serverIP);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);

            // Envoyer le message
            clientSocket.send(sendPacket);
            System.out.println("Message envoyé au serveur : " + message);

            // Réception de la réponse
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            // Afficher la réponse
            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Réponse du serveur : " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
