import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient {
    public static void main(String[] args) {
        String serverIP = "10.146.72.166"; // Adresse du serveur
        int serverPort = 12345; // Port du serveur

        try (DatagramSocket clientSocket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in)) {

            System.out.print("Entrez votre pseudo : ");
            String pseudo = scanner.nextLine();

            InetAddress serverAddress = InetAddress.getByName(serverIP);

            // Envoi du pseudo au serveur
            String pseudoMessage = "___PSEUDO:" + pseudo;
            byte[] pseudoData = pseudoMessage.getBytes();
            DatagramPacket pseudoPacket = new DatagramPacket(pseudoData, pseudoData.length, serverAddress, serverPort);
            clientSocket.send(pseudoPacket);

            // Thread pour écouter les messages
            Thread listenerThread = new Thread(() -> {
                try {
                    while (true) {
                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        clientSocket.receive(receivePacket);

                        String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        System.out.println("\n" + receivedMessage);
                        System.out.print("Vous : ");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            listenerThread.start();

            // Envoi des messages
            while (true) {
                System.out.print("Vous : ");
                String message = scanner.nextLine();

                byte[] sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                clientSocket.send(sendPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
