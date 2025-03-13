import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPPortScanner {

    // Méthode pour scanner les ports UDP
    public static void scanUDPPorts(int startPort, int endPort) {
        for (int port = startPort; port <= endPort; port++) { // Boucle de test sur une plage de ports
            try {
                DatagramSocket socket = new DatagramSocket(port); // Tente d'ouvrir un socket sur le port
                socket.close(); // Si réussi, cela signifie que le port est fermé
                System.out.println("Port " + port + " : fermé");
            } catch (SocketException e) {
                System.out.println("Port " + port + " : ouvert"); // Si erreur, alors le port est occupé (ouvert)
            }
        }
    }

    public static void main(String[] args) {
        int startPort = 0; // Premier port à tester
        int endPort = 100; // Dernier port à tester
        scanUDPPorts(startPort, endPort); // Lancer le scan des ports
    }
}
