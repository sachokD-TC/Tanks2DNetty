package com.tanks2d.netty.client.gui;

import com.tanks2d.netty.client.SecureClient;
import com.tanks2d.netty.client.entity.RoomScores;
import com.tanks2d.netty.client.entity.Tank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Random;

import static com.tanks2d.netty.client.utils.constants.Commands.DELIMITER;
import static com.tanks2d.netty.client.utils.constants.Commands.EXIT;
import static com.tanks2d.netty.client.utils.constants.Messages.*;

public class ClientGUI extends JFrame {

    private static final String NAME_LABEL_TEXT = "Name:";
    private static final String SEND_MESSAGE = "Send message";
    private final JLabel nameLabel;
    private final JTextField nameTextField;
    private final JLabel chatTitleLabel;
    private final JTextField chatMessageTextField;
    private final JTextArea chatTextArea;
    private final JButton sendButton;
    private JLabel ipaddressLabel;
    private JLabel portLabel;
    private JScrollPane chatScrollPane;
    private static JLabel scoreLabel;
    private JTextField ipaddressText;
    private JTextField portText;
    private JButton registerButton;
    private JPanel registerPanel;
    private JPanel gameTipsPanel;
    private JLabel gameTipsLabel;
    private JPanel gameStatusPanel;
    private JPanel roomScoresPanel;
    private JLabel roomScoresLabel;
    private JTextArea roomScoresTextArea;
    private JScrollPane roomScoresScrollPane;
    private SecureClient client;
    private Tank clientTank;
    private static int score;

    int width = 990, height = 630;
    public GameBoardPanel boardPanel;

    public ClientGUI() {
        score = 0;
        this.setTitle(GAME_TITLE);
        this.setSize(width, height);
        this.setLocation(60, 100);
        this.getContentPane().setBackground(Color.BLACK);
        this.setLayout(null);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent windowEvent) {

            }

            public void windowClosing(WindowEvent e) {
                ClientGUI.this.windowClosing(e);
            }

            @Override
            public void windowClosed(WindowEvent windowEvent) {

            }

            @Override
            public void windowIconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowActivated(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) {

            }

        });

        registerPanel = new JPanel();
        registerPanel.setBackground(Color.YELLOW);
        registerPanel.setSize(200, 140);
        registerPanel.setBounds(560, 50, 200, 140);
        registerPanel.setLayout(null);

        roomScoresPanel = new JPanel();
        roomScoresPanel.setBackground(Color.YELLOW);
        roomScoresPanel.setSize(200, 140);
        roomScoresPanel.setBounds(770, 50, 200, 530);
        roomScoresPanel.setLayout(null);
        roomScoresPanel.setBorder(BorderFactory.createLoweredBevelBorder());

        gameStatusPanel = new JPanel();
        gameStatusPanel.setBackground(Color.YELLOW);
        gameStatusPanel.setSize(200, 300);
        gameStatusPanel.setBounds(560, 210, 200, 311);
        gameStatusPanel.setLayout(null);

        gameTipsPanel = new JPanel();
        gameTipsPanel.setBackground(Color.YELLOW);
        gameTipsPanel.setBounds(10, 530, 750, 50);
        gameTipsPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        gameTipsPanel.setLayout(new FlowLayout());

        gameTipsLabel = new JLabel(TIP_REGISTER_MESSAGE);
        gameTipsLabel.setBounds(10, 535, 250, 50);
        gameTipsLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        gameTipsPanel.add(gameTipsLabel);


        ipaddressLabel = new JLabel("IP address: ");
        ipaddressLabel.setBounds(10, 25, 70, 25);

        portLabel = new JLabel("Port: ");
        portLabel.setBounds(10, 55, 50, 25);

        scoreLabel = new JLabel("Score : 0");
        scoreLabel.setBounds(10, 20, 100, 25);

        roomScoresLabel= new JLabel("Tanks in room");
        roomScoresLabel.setBounds(75, 10, 100, 25);
        roomScoresPanel.add(roomScoresLabel);
        roomScoresTextArea = new JTextArea();
        roomScoresTextArea.setBounds(10, 40, 180, 480);
        roomScoresPanel.add(roomScoresTextArea);

        ipaddressText = new JTextField("localhost");
        ipaddressText.setBounds(90, 25, 100, 25);

        portText = new JTextField("8992");
        portText.setBounds(90, 55, 100, 25);

        registerButton = new JButton("Register");
        registerButton.setBounds(60, 100, 90, 25);
        registerButton.addActionListener(e -> registerAction());
        registerButton.setFocusable(true);


        registerPanel.add(ipaddressLabel);
        registerPanel.add(portLabel);
        registerPanel.add(ipaddressText);
        registerPanel.add(portText);
        registerPanel.add(registerButton);

        nameLabel = new JLabel(NAME_LABEL_TEXT);
        nameLabel.setBounds(10, 40, 100, 25);
        nameTextField = new JTextField();
        nameTextField.setBounds(10, 65, 150, 25);
        nameTextField.setText("Name" + new Random().nextInt((100 - 1) + 1));


        chatTitleLabel = new JLabel("Chat room");
        chatTitleLabel.setBounds(10, 95, 150, 25);

        chatMessageTextField = new JTextField();
        chatMessageTextField.setBounds(10, 120, 150, 25);
        chatMessageTextField.setEnabled(false);

        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);
        chatScrollPane = new JScrollPane(chatTextArea);
        chatScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        chatScrollPane.setBounds(10, 150, 150, 100);

        sendButton = new JButton(SEND_MESSAGE);
        sendButton.setBounds(10, 270, 120, 25);
        sendButton.setFocusable(true);
        sendButton.addActionListener(e -> sendMessageToChatLocal(chatMessageTextField.getText()));

        chatMessageTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                setTipsText(TIP_CHAT_MESSAGE);
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                sendMessageFromKeyboard(chatMessageTextField.getText(), keyEvent);
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });

        gameStatusPanel.add(scoreLabel);
        gameStatusPanel.add(nameLabel);
        gameStatusPanel.add(nameTextField);
        gameStatusPanel.add(chatTitleLabel);
        gameStatusPanel.add(chatMessageTextField);
        gameStatusPanel.add(chatScrollPane);
        gameStatusPanel.add(sendButton);

        getContentPane().add(registerPanel);
        getContentPane().add(gameStatusPanel);
        getContentPane().add(gameTipsPanel);
        getContentPane().add(roomScoresPanel);
        setVisible(true);
    }


    public void registerAction() {
        if (nameTextField.getText().equals("")) {
            JOptionPane.showMessageDialog(this, ENTER_NAME_MESSAGE, GAME_TITLE, JOptionPane.INFORMATION_MESSAGE);
        } else {
            nameLabel.setText(NAME_LABEL_TEXT + nameTextField.getText());
            nameTextField.setEnabled(false);
            chatMessageTextField.setEnabled(true);
            registerButton.setEnabled(false);

            if (SecureClient.checkConnection(ipaddressText.getText(), Integer.parseInt(portText.getText()))) {
                setTipsText(TIP_START_MESSAGE);
                client = SecureClient.getClient(ipaddressText.getText(), Integer.parseInt(portText.getText()), this);
                clientTank = new Tank();
                clientTank.setTankName(nameTextField.getText());
                boardPanel = new GameBoardPanel(clientTank, this);
                getContentPane().add(boardPanel);
                boardPanel.setGameStatus(true);
                boardPanel.repaint();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                boardPanel.setFocusable(true);
                boardPanel.grabFocus();
            } else {
                JOptionPane.showMessageDialog(this, SERVER_NOT_RUNNING_MESSAGE, GAME_TITLE, JOptionPane.INFORMATION_MESSAGE);
                registerButton.setEnabled(true);
            }
        }
    }

    private void sendMessageFromKeyboard(String messageText, KeyEvent e) {
        if (KeyEvent.VK_F2 == e.getKeyCode()) {
            setTipsText(TIP_PLAY_GAME_MESSAGE);
            chatTextArea.setCaretPosition(chatTextArea.getDocument().getLength());
            boardPanel.setFocusable(true);
            boardPanel.setRequestFocusEnabled(true);
            boardPanel.grabFocus();
        } else if (KeyEvent.VK_ENTER == e.getKeyCode())
            sendMessageToChatLocal(messageText);
    }


    private void sendMessageToChatLocal(String messageText) {
        if (!"".equals(messageText)) {
            client.sendCommandToServer(nameTextField.getText() + ":" + chatMessageTextField.getText() + "\n");
            messageText = "You: " + messageText + "\n";
            sendMessageToServerChat(messageText);
            chatMessageTextField.setText("");
        }
    }

    public void sendMessageToServerChat(String messageText) {
        if (!"".equals(messageText)) {
            chatTextArea.append(messageText);
            chatTextArea.setCaretPosition(chatTextArea.getDocument().getLength());
        }
    }

    public void activateChat(){
        chatMessageTextField.setFocusable(true);
        chatMessageTextField.setRequestFocusEnabled(true);
        chatMessageTextField.grabFocus();
    }

    public static void setScore(int scoreInt) {
        score += scoreInt;
        scoreLabel.setText("Score : " + score);
    }

    public void windowClosing(WindowEvent e) {
        int response = JOptionPane.showConfirmDialog(this, EXIT_QUESTION_MESSAGE, GAME_TITLE, JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            if (SecureClient.getClient() != null)
                SecureClient.getClient().sendCommandToServer(EXIT + DELIMITER + clientTank.getTankName() + DELIMITER + "-");
            this.dispose();
            System.exit(0);
        }
    }


    public SecureClient getClient() {
        return client;
    }

    public static void main(String[] args) {
        new ClientGUI();
    }

    public Tank getClientTank() {
        return clientTank;
    }

    public void setTipsText(String tip) {
        gameTipsLabel.setText(tip);
    }

    public void updateRoomScores(String scores){
        roomScoresTextArea.setText(scores);
    }
}
