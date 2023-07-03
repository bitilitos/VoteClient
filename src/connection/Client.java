package connection;

import Data.LoadClientInitialConfig;
import gui.ClientWindow;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;


public class Client extends Thread{

    private Socket socket;
    private ClientWindow window;
    private boolean connected = false;
    private String electionID;
    private InetAddress serverAddress;
    private int serverPort;
    private InetAddress address;
    private String[] candidates;
    private String[] vote;
    private static final int MOVE_UP = 0;
    private static final int MOVE_DOWN = 1;
    private static final int MOVE_BEGINNING = 2;

    public Client(ClientWindow window){
        this.window = window;
        getServerConnectionConfig();
    }

    public void clearData() {
        electionID = null;
        candidates = null;
        vote = null;
        String[] data = new String[0];
        window.updateBallot(data, -1);
        window.updateResult(data);
    }

    public void run() {

        while (!isInterrupted() && connected) {
            receiveMessage();
        }
    }

    private void getServerConnectionConfig() {
        LoadClientInitialConfig loadConfig = new LoadClientInitialConfig();
        serverAddress = loadConfig.getServerAddress();
        System.out.println(serverAddress.getHostName());
        serverPort = loadConfig.getServerPort();
    }

    private boolean connectClient() {
        try {
                System.out.println("Client trying to connect");
                socket = new Socket(serverAddress, serverPort);
                System.out.println("Client connected");
                connected = true;
                address = InetAddress.getLocalHost();
                return true;

        } catch (IOException e) {
            System.out.println("Client can't connect! " + e);
            informMessage("Erro de ligação",
                    "Servidor não está à espera de ligações.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void sendVoteMessage() {
        String result = electionID + ":";

        for (int i = 0; i < vote.length; i++) {
            String[] strArr = vote[i].split(" ");
            result += strArr[0];
            if (i < vote.length - 1) result += " ";
        }

        sendMessage(result, MessageType.SEND_VOTE);

    }


    public void sendMessage(String msg, MessageType msgType) {
        if(!isConnected())
        {
            if (!connectClient()) {
                informMessage("Erro de ligação",
                        "Neste momento não é possível ligar ao servidor.",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                if (!this.getState().equals(State.RUNNABLE)) this.start(); ;
            }
        }

        try{
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println(address +" is sending a message...");
            oos.writeObject(new Message(address, socket.getInetAddress(),msgType, msg));
        } catch (IOException e) {
            System.out.println(address + " is unable to send message!");
        }
    }


    public void receiveMessage(){
        ObjectInputStream ois;
        if (!socket.isClosed()) {
            try {

                ois = new ObjectInputStream(socket.getInputStream());
                Message msg = (Message) ois.readObject();

                switch (msg.getMSG_TYPE()) {
                    case RESPONSE_BALLOT:
                        electionID = msg.getCONTENT().substring(0,msg.getCONTENT().indexOf(":"));
                        String ballotStr = msg.getCONTENT().substring(msg.getCONTENT().indexOf(":")+1);
                        String[] ballot = ballotStr.split(":");
                        window.updateBallot(ballot, 0);
                        candidates = ballot;
                        vote = ballot;

                        break;
                    case CONFIRMATION_VOTE:
                        informMessage("Confirmação de votação",
                                "O seu voto foi recebido e registado.",
                                JOptionPane.INFORMATION_MESSAGE);
                        break;
                    case ALREADY_VOTED:
                        informMessage("Erro na votação",
                                "O seu voto não foi registado por já ter votado.",
                                JOptionPane.ERROR_MESSAGE);
                        break;
                    case SEND_ELECTION:
                        String[] election = msg.getCONTENT().split(":");
                        window.updateResult(election);

                        break;
                    case CLOSING_CONNECTION:
                        if (!socket.isClosed()) socket.close();
                        informMessage("Erro de Ligação",
                                "O servidor desligou a sua ligação.",
                                JOptionPane.ERROR_MESSAGE);
                        break;
                    case SEND_MESSAGE:
                        informMessage("Informação", msg.getCONTENT(), JOptionPane.INFORMATION_MESSAGE);
                }


                System.out.println("Client received Message: " + msg.getCONTENT());
                //window.updateMessageReceivedJTextField(msg.getCONTENT());
            } catch (IOException e) {
                System.out.println("Socket is closed");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public String[] changeCandidateOrder(int move, int index) {

        if ((move == MOVE_UP && index == 0)
            || (move == MOVE_DOWN && index == vote.length - 1)) return vote;

        String temp = "";
        switch (move) {
            case MOVE_UP:
                temp = vote[index-1];
                vote[index-1] = vote[index];
                vote[index] = temp;
                window.updateBallot(vote, index - 1);
                break;
            case MOVE_DOWN:
                temp = vote[index+1];
                vote[index+1] = vote[index];
                vote[index] = temp;
                window.updateBallot(vote, index +1);
                break;
            case MOVE_BEGINNING:
                vote = candidates;
        }
        return vote;
    }


    public void closeConection() {
        if (socket == null || socket.isClosed()) return;
        try {
            sendMessage("",MessageType.CLOSING_CONNECTION);
            socket.close();
            connected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void informMessage(String title, String content, int jOptionPaneMessageType) {
        JFrame frame = new JFrame();
        JOptionPane.showMessageDialog(frame,
                content,
                title,
                jOptionPaneMessageType);
    }


    public boolean isConnected() {return (!(socket==null || !socket.isConnected()));}

    public static void main(String[] args) {
        ClientWindow window = new ClientWindow("Cliente");
        Client client = new Client(window);
        window.setVisible(true);
    }
}


