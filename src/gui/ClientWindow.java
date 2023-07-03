package gui;

import connection.Client;
import connection.MessageType;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.metal.MetalScrollBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientWindow {
    private Client client = null;
    private JFrame frame;
    private JTextField messageReceived;
    private JTextField messageToSend;
    private JList<String> ballot = new JList<>();
    private JList<String> electionResult = new JList<>();
    private Button sendVoteButton;
    private static final Dimension JLIST_SIZE = new Dimension(400, 350);
    private static final Font BUTTON_FONT = new Font("Arial", Font.PLAIN, 18);
    private static final Font TEXT_FONT = new Font("Calibri", Font.PLAIN, 18);
    private static final Color BACKGROUND_COLOR = new Color(87, 93, 66);
    private static final Color TEXT_COLOR = new Color(223, 218, 203);
    private static final Color BUTTON_COLOR = new Color(223, 218, 203);
    private static final Color BUTTON_TEXT_COLOR = new Color(74, 50, 47);
    private static final Color JLIST_BACKGROUND_COLOR = new Color(223, 218, 203);
    private static final Color JLIST_SELECTION_BACKGROUND_COLOR = new Color(156, 144, 108);

    private static final Font TITLE_FONT = new Font("Calibri", Font.BOLD, 18);
    private static final int MOVE_UP = 0;
    private static final int MOVE_DOWN = 1;

    public ClientWindow(String name) {
        frame = new JFrame(name);
        frame.setResizable(false);
        frame.setSize(900,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("res/icon.png")));
        frame.getContentPane().setBackground(BACKGROUND_COLOR);

        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 5);


        gbc.gridx = 0;
        gbc.gridy = 0;

        gbc.anchor = GridBagConstraints.WEST;

        frame.add(createBeginVotingButton(), gbc);

        gbc.insets = new Insets(10, 5, 10, 10);
        gbc.gridx = 1;
        JLabel oneImg = new JLabel(
                new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("res/1_passo.png"))));
        frame.add(oneImg, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 30, 20, 0);
        frame.add(titleContainer("Exército Português", "Gabinete do Chefe do Estado-Maior do Exército"), gbc);

        gbc.insets = new Insets(10, 0, 10, 10);

        gbc.gridwidth = 1;
        gbc.gridx = 3;
        Container exercitoImg = logoContainer("res/logo.png");
        frame.add(exercitoImg,gbc);


        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(createJListContainer(ballot, true, "Boletim de voto:"), gbc);


        gbc.gridx = 2;
        gbc.gridy = 2;
        frame.add(createJListContainer(electionResult, false, "Resultado da eleição:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;

        gbc.insets = new Insets(10, 10, 15, 0);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 2;
        JLabel threeImg = new JLabel(
                new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("res/3_passo.png"))));
        frame.add(threeImg, gbc);

        gbc.gridx = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 0, 20, 10);
        frame.add(createSendVoteButton(), gbc);


        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                client.closeConection();
                client.interrupt();
                e.getWindow().dispose();
            }
        });


        frame.pack();
    }




    private Container logoContainer (String logoPathName) {

        Container container =  new Container();
        FlowLayout fl = new FlowLayout();
        container.setLayout(fl);
        fl.setAlignment(FlowLayout.RIGHT);


        JLabel exercitoImg = new JLabel(
                new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(logoPathName))));

        container.add(exercitoImg, fl);
        return container;
    }

    private Container titleContainer(String title, String subTitle) {

        Container titleContainer = new Container();
        titleContainer.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(0,10,0,0);
        gbc.gridheight = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleContainer.add(titleLabel, gbc);

        gbc.gridy = 1;
        JLabel subTitleLabel = new JLabel(subTitle);
        subTitleLabel.setFont(TITLE_FONT);
        subTitleLabel.setForeground(TEXT_COLOR);
        titleContainer.add(subTitleLabel, gbc);
        return titleContainer;
    }

    private Container createContainer() {
        Container container =  new Container();
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10,10,10,10);
        gbc.gridy = 0;
        gbc.gridx = 0;
        messageToSend = createMessageToSendJTextField();
        container.add(messageToSend,gbc);

        gbc.gridy = 2;
        messageReceived = createMessageReceivedJTextField();
        container.add(messageReceived,gbc);

        gbc.gridy = 3;
        Button sndButton = createSendButton();
        container.add(sndButton);

        return container;
    }


    private Container createJListContainer(JList jList, Boolean buttons, String title) {
        Container container = new Container();
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,2,10);
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TEXT_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setHorizontalAlignment(JLabel.LEFT);
        container.add(titleLabel, gbc);

        gbc.insets = new Insets(0,10,10,10);
        gbc.gridy = 1;
        gbc.gridx = 0;
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(JLIST_SIZE);
        scrollPane.setViewportView(createCandidatesJList(jList));
        scrollPane.setBackground(JLIST_BACKGROUND_COLOR);
        scrollPane.setForeground(JLIST_SELECTION_BACKGROUND_COLOR);
        //scrollPane.getVerticalScrollBar().setForeground(JLIST_SELECTION_BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors(){
                this.thumbColor = JLIST_SELECTION_BACKGROUND_COLOR;
            }
        });

        container.add(scrollPane, gbc);

        if (buttons) {
            Container buttonContainer = new Container();
            buttonContainer.setLayout(new GridBagLayout());
            GridBagConstraints gbcButton = new GridBagConstraints();
            gbcButton.insets = new Insets(10,10,50,10);

            gbcButton.gridy = 0;
            gbcButton.gridx = 0;


            buttonContainer.add(createUpButton(), gbcButton);

            gbc.insets = new Insets(0, 0, 0, 10);
            gbc.gridx = 1;
            JLabel twoImg = new JLabel(
                    new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("res/2_passo.png"))));
            container.add(twoImg, gbc);

            gbc.insets = new Insets(40, 10, 0, 10);
            gbcButton.gridy = 2;
            buttonContainer.add(createDownButton(),gbcButton);
            gbc.gridx = 1;
            container.add(buttonContainer, gbc);
        }


        return container;
    }


    private JList<String> createCandidatesJList(JList jList) {
        JScrollPane scrollPane = new JScrollPane();
        jList.setLayoutOrientation(JList.VERTICAL);
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList.setFont(TEXT_FONT);
        jList.setBackground(JLIST_BACKGROUND_COLOR);
        jList.setSelectionBackground(JLIST_SELECTION_BACKGROUND_COLOR);
        //jList.setForeground(JLIST_TEXT_COLOR);
        scrollPane.add(jList);
        return jList;
    }

    private Button createBeginVotingButton() {
        Button beginVotingButton = new Button("Pedir Boletim");
        beginVotingButton.setFont(BUTTON_FONT);
        beginVotingButton.setBackground(BUTTON_COLOR);
        beginVotingButton.setForeground(BUTTON_TEXT_COLOR);
        beginVotingButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        beginVotingButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                    client.clearData();
                    client.sendMessage("", MessageType.REQUEST_BALLOT);
            }

        });
        return beginVotingButton;
    }

    private Button createSendVoteButton() {
        sendVoteButton = new Button("Enviar Voto");
        sendVoteButton.setFont(BUTTON_FONT);
        sendVoteButton.setBackground(BUTTON_COLOR);
        sendVoteButton.setForeground(BUTTON_TEXT_COLOR);
        sendVoteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendVoteButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){

                if (isBallotEmpty()) {

                    client.informMessage("Aviso",
                            "Clique em Pedir Boletim.", JOptionPane.WARNING_MESSAGE);


                } else {
                    JFrame frame = new JFrame();
                    Object[] options = {"Sim", "Não"};
                    int result = JOptionPane.showOptionDialog(
                            frame,
                            "Confirma o envio do seu boletim de voto?",
                            "Enviar votação?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1]
                    );
                    switch (result) {
                        case JOptionPane.YES_OPTION:
                            client.sendVoteMessage();
                    }
                }

            }
        });
        return sendVoteButton;
    }

    private JButton createUpButton() {
//        Button upButton = new Button("Up");
//        upButton.setFont(BUTTON_FONT);
        JButton upButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("res/up.png")));
        upButton.setBackground(BUTTON_COLOR);
        upButton.setBorderPainted(true);
        upButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        upButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if (ballot.isSelectionEmpty()) {
                    client.informMessage("Aviso",
                            "Selecione um candidato.", JOptionPane.WARNING_MESSAGE);
                }else if (ballot.getModel().getSize() == 0) {
                    client.informMessage("Aviso",
                            "Clique em Pedir Boletim", JOptionPane.WARNING_MESSAGE);
                } else {
                    client.changeCandidateOrder(MOVE_UP, ballot.getSelectedIndex());
                }
            }
        });
        return upButton;
    }

    private JButton createDownButton() {
        JButton downButton = new JButton(new ImageIcon(getClass().getClassLoader().getResource("res/down.png")));
        downButton.setBackground(BUTTON_COLOR);
        downButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        downButton.setBorderPainted(true);
        downButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if (ballot.isSelectionEmpty()) {
                    client.informMessage("Aviso",
                            "Selecione um candidato.", JOptionPane.WARNING_MESSAGE);

                }else if (ballot.getModel().getSize() == 0) {
                    client.informMessage("Aviso",
                            "Clique em Pedir Boletim", JOptionPane.WARNING_MESSAGE);
                } else {
                    client.changeCandidateOrder(MOVE_DOWN,ballot.getSelectedIndex());
                }
            }
        });
        return downButton;
    }


    private JTextField createMessageToSendJTextField() {
        messageToSend = new JTextField("messageToSend");
        messageToSend.setText("Mensagem a enviar");
        return messageToSend;
    }



    private JTextField createMessageReceivedJTextField() {
        messageReceived = new JTextField("messageReceived");
        messageReceived.setText("Mensagem a receber");
        return messageReceived;
    }

//    public void updateMessageReceivedJTextField(String msg) {
//        messageReceived.setText(msg);
//    }

    private Button createSendButton() {
        Button sendButton = new Button("Send");
        sendButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                client.sendMessage(messageToSend.getText(), MessageType.SEND_MESSAGE);
            }


        });
        return sendButton;
    }


    public void setClient (Client client){this.client = client; }

    public void setVisible(Boolean visible) {frame.setVisible(visible);}

    public void updateBallot(String[] ballotArray, int selectIndex) {
         ballot.setListData(ballotArray);
        if (selectIndex != -1) ballot.setSelectedIndex(selectIndex);

    }

    public void updateResult(String[] ballotArray) {
        electionResult.setListData(ballotArray);
    }


    public boolean isBallotEmpty() { return ballot.getModel().getSize()==0;}

    public void setVoteButtonEnable(boolean enable) { sendVoteButton.setEnabled((enable));}
}
