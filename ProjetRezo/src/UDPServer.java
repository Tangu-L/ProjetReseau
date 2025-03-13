import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {
    public static void main(String[] args) {
        int port = 12345; // Port d'écoute

        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            System.out.println("Serveur UDP en écoute sur le port " + port);

            while (true) {
                // Réception du message
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                // Infos du client
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                System.out.println("Nouveau client : " + clientAddress + ":" + clientPort + " → " + message);

                // Répondre au client
                String response = "Serveur RX302 ready";
                byte[] sendData = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

