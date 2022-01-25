package client;

import client.subsrs.ClientListener;
import supp.InDebug;
import supp.InetAddressesAndPort;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

public class Menu extends JDialog {
    private InDebug inDebug = new InDebug();
    private JPanel contentPane;
    private JTextField textField1;
    private JButton okButton;
    private JCheckBox checkBox1;
    private JComboBox<String> comboBox1;
    private JTextField textField2;

    private Socket server;

    public Menu() {
        setTitle("Start menu");
        setBounds(600, 200, -1, -1);
        setResizable(false);
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getRootPane().setDefaultButton(okButton);
        comboBox1.setVisible(true);
        comboBox1.addItem("Random AI");

        checkBox1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (checkBox1.isSelected()) {
                    comboBox1.setVisible(false);
                } else {
                    comboBox1.setVisible(true);
                }
            }
        });

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
    }

    private void onOK() {
        InetAddressesAndPort addressesAndPort;
        try {
            addressesAndPort = new InetAddressesAndPort("localhost:7777");
            server = new Socket(addressesAndPort.getAddress(), addressesAndPort.getPort());
            PrintWriter out = new PrintWriter(server.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            if (textField2.getText().isEmpty()) return;
            //out.println("CPN " + textField2.getText());
            LobbiesList lobbiesList = new LobbiesList(server);
            if (inDebug.inDebug){
                System.out.println("Ready to send on server");
            }
            //Thread.sleep(100);
            out.println("CPN " + textField2.getText());
//            if (inDebug.inDebug){
//                System.out.println("I'm here");
//            }

            String answer = in.readLine();
            if (inDebug.inDebug){
                System.out.println("I'm here");
            }
            ClientListener clientListener = new ClientListener(server, lobbiesList);
            lobbiesList.setClientListener(clientListener);

            if (answer.equals("IF")){
                RecoverableError dialog = new RecoverableError();
                dialog.setErrorText("A user with the same name already exists on the server. Please choose other name.");
                dialog.pack();
                dialog.setVisible(true);
                return;
            }
            clientListener.start();

            if (inDebug.inDebug){
                System.out.println("message is sent");
            }
            lobbiesList.pack();
            dispose();
            if (checkBox1.isSelected())
                lobbiesList.setVisible(true);
            else{
                out.println("NLN RAI 1");
            }
        }catch (ConnectException e){
            WinInfo dialog = new WinInfo();
            dialog.setText("Server shutdown, please connect later.");
            dialog.pack();
            dialog.setVisible(true);
            System.exit(0);
        }
        catch (IOException e) {
            WinInfo dialog = new WinInfo();
            dialog.setText("Unexpected error");
            dialog.pack();
            dialog.setVisible(true);
            System.exit(0);
        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        dispose();
    }
}
