import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPPortScanner {

    public static void scanUDPPorts(int startPort, int endPort) {
        for (int port = startPort; port <= endPort; port++) {
            try {
                DatagramSocket socket = new DatagramSocket(port);
                socket.close(); // Si on arrive ici, le port est libre (fermé)
                System.out.println("Port " + port + " : fermé");
            } catch (SocketException e) {
                System.out.println("Port " + port + " : ouvert");
            }
        }
    }

    public static void main(String[] args) {
        int startPort = 0; // À modifier selon besoin
        int endPort = 100;   // À modifier selon besoin
        scanUDPPorts(startPort, endPort);
    }
}
