package Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class LoadClientInitialConfig {

    private final String FILEPATH = "Client.txt";
    private File file;
    private InetAddress serverAddress;
    private int port;


    public LoadClientInitialConfig() {

        file = new File(FILEPATH);
        if (file.exists()) System.out.println("File exists");
        else {
            System.out.println("File doesn't exist");
            return;
        }
        openFile();
    }

    private void openFile(){
        try{
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()){

                String[] line = scanner.nextLine().split(" ");
                lineToObject(line);
            }

        }catch (FileNotFoundException e) {
            System.out.println("file not found");
        }

    }

    private void lineToObject(String[] line) {

        switch (line[0]) {
            case "SERVER_ADDRESS:":
               setServerAddress(line[1]);
               break;
            case "SERVER_PORT:":
                port = Integer.parseInt(line[1]);
                break;
        }
    }


    private void setServerAddress(String address) {
        try{
            serverAddress = InetAddress.getByName(address);
            if (!serverAddress.isReachable(3)) System.out.println("Couldn't connect to : " + address);
            else {
                System.out.println("is reachable");
            }
        }catch (UnknownHostException e) {
            System.out.println("Couldn't find host: " + address);
        }catch (IOException exception) {
            System.out.println("Couldn't connect to : " + address + exception);
        }

    }

    public InetAddress getServerAddress() { return serverAddress;}

    public int getServerPort() { return port;}

}
