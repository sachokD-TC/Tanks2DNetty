package com.tanks2d.netty.server.gui;

import com.tanks2d.netty.server.SecureServer;
import com.tanks2d.netty.server.SecureServerInitializer;

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
    private SecureServer secureServer;
    private Thread thread;

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

    /**
     * Process pressing button - "Stop Server"
     *
     * @param actionEvent
     */
    private void stopServer(ActionEvent actionEvent) {
        statusLabel.setText("Server is stopping.....");
        SecureServerInitializer.isSomeoneRegistered.set(false);
        secureServer.shutdownServer();
        this.dispose();
    }

    /**
     * Process button - "Start server"
     *
     * @param actionEvent
     */
    public void startServer(ActionEvent actionEvent) {
        startServerButton.setEnabled(false);
        statusLabel.setText("Server is running.....");
        statusLabel.repaint();
        try {
            int numberOfTanks = Integer.parseInt(numberOfTanksPerRoomTextField.getText());
            secureServer = new SecureServer(Integer.parseInt(portTextField.getText()), numberOfTanks);
            thread = new Thread(secureServer);
            thread.start();
        } catch (CertificateException ex) {
            ex.printStackTrace();
        } catch (SSLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @return - secure Server for auto tests
     */
    public SecureServer getSecureServer() {
        return secureServer;
    }

    /**
     * Main method of Server GUI
     *
     * @param args
     */
    public static void main(String args[]) {
        new ServerGUI();
    }

    /**
     * @return button instance for FEST Swing tests
     */
    public JButton getStartServerButton() {
        return startServerButton;
    }

    /**
     * @return button instance for FEST Swing tests
     */
    public JButton getStopServerButton() {
        return stopServerButton;
    }
}
