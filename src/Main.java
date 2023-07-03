import connection.Client;
import gui.ClientWindow;

import java.net.InetAddress;

public class Main {
    public static void main(String[] args) {

        ClientWindow windowsClient = new ClientWindow("Janela de voto");
        Client client = new Client(windowsClient);
        windowsClient.setClient(client);

        windowsClient.setVisible(true);


    }
}
