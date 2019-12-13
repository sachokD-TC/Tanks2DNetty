package com.tanks2d.netty.server.gui;

import com.tanks2d.netty.server.SecureServer;

import javax.net.ssl.SSLException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.security.cert.CertificateException;

public class ServerGUI extends JFrame {
    public static final String NUMBER_OF_TANKS_DEFAULT = "2";
    private JButton startServerButton;
    private JButton stopServerButton;
    private JLabel statusLabel;
    private JLabel numberOfTanksPerRoomLabel;
    private JTextField numberOfTanksPerRoomTextField;
    private JLabel portLabel;
    private JTextField portTextField;


    public ServerGUI() {
        setTitle("Tanks 2D Server");
        setBounds(350, 240, 300, 225);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(null);
        startServerButton = new JButton("Start Server");
        startServerButton.setBounds(20, 80, 120, 25);
        startServerButton.addActionListener(actionEvent -> startServer(actionEvent));

        stopServerButton = new JButton("Stop Server");
        stopServerButton.setBounds(150, 80, 120, 25);
        stopServerButton.addActionListener(actionEvent -> stopServer(actionEvent));

        statusLabel = new JLabel();
        statusLabel.setText("Status of Server");
        statusLabel.setBounds(100, 150, 200, 25);

        portLabel = new JLabel("Port:");
        portLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        portLabel.setBounds(20, 40, 200, 25);

        portTextField = new JTextField("8992");
        portTextField.setBounds(150, 40, 80, 25);

        numberOfTanksPerRoomLabel = new JLabel("Number of Tanks per room");
        numberOfTanksPerRoomLabel.setBounds(20, 120, 170, 25);
        numberOfTanksPerRoomTextField = new JTextField();
        numberOfTanksPerRoomTextField.setBounds(190, 120, 40, 25);
        numberOfTanksPerRoomTextField.setText(NUMBER_OF_TANKS_DEFAULT);

        getContentPane().add(startServerButton);
        getContentPane().add(stopServerButton);
        getContentPane().add(portLabel);
        getContentPane().add(portTextField);
        getContentPane().add(statusLabel);
        getContentPane().add(numberOfTanksPerRoomLabel);
        getContentPane().add(numberOfTanksPerRoomTextField);
        setVisible(true);
    }

    private void stopServer(ActionEvent actionEvent) {
        statusLabel.setText("Server is stopping.....");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }

    public void startServer(ActionEvent actionEvent) {
        startServerButton.setEnabled(false);
        statusLabel.setText("Server is running.....");
        statusLabel.repaint();
        try {
            int numberOfTanks = Integer.parseInt(numberOfTanksPerRoomTextField.getText());
            Thread thread = new Thread(new SecureServer(Integer.parseInt(portTextField.getText()), numberOfTanks));
            thread.start();
        } catch (CertificateException ex) {
            ex.printStackTrace();
        } catch (SSLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String args[]) {
        new ServerGUI();
    }
}
