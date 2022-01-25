package client;

import client.subsrs.ClientGameListener;
import client.subsrs.ClientListener;
import client.view.MainFrame;
import supp.InDebug;
import supp.InetAddressesAndPort;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class LobbiesList extends JDialog {
    private InDebug inDebug = new InDebug();
    private JPanel contentPane;
    private JButton buttonOK;
    private ArrayList<String> lobbiesList = new ArrayList<>();
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> list1;
    private JButton startNewLobeButton;
    private JScrollPane scrollPane;
    private JTextField textField1;
    private JButton getMyWinRateButton;

    private ClientListener clientListener;
    private Socket server;

    public LobbiesList(Socket server) {
        this.server = server;
        setBounds(600, 200, -1, -1);
        setTitle("Lobbies list");

        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        list1.setModel(listModel);

        list1.setLayoutOrientation(JList.VERTICAL);
        list1.setBounds(0, 0, list1.getWidth(), list1.getHeight());

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        startNewLobeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onStartNewLobby();
            }
        });

        getMyWinRateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onGetMyWinRate();
            }
        });

        setContentPane(contentPane);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public void setClientListener(ClientListener clientListener) {
        this.clientListener = clientListener;
    }

    public void initLobbyList(ArrayList<String> input){//need to test
        if (inDebug.inDebug){
            System.out.println("Printing in GUI");
            System.out.println(input);
            for (String st : input){
                System.out.println(st);
            }
        }
        listModel.clear();
        listModel.addAll(input);

        list1.revalidate();
        list1.repaint();

        if (inDebug.inDebug){
            System.out.println("End print");
        }
    }

    public void permissionOnStartNewLobby(boolean permission){
        if (!permission){
            RecoverableError dialog = new RecoverableError();
            dialog.setErrorText("A lobby with the same name already exists on the server. Please choose other lobby name.");
            dialog.pack();
            dialog.setVisible(true);
            textField1.setText("");
            return;
        }
        MainFrame mainFrame = new MainFrame(server, true);
        clientListener.setMainFrame(mainFrame);
        setVisible(false);
    }

    private void onGetMyWinRate(){
        try {
            PrintWriter out = new PrintWriter(server.getOutputStream(), true);
            out.println("WR");
        } catch (IOException exception) {
            WinInfo dialog = new WinInfo();
            dialog.setText("Unexpected error");
            dialog.pack();
            dialog.setVisible(true);
            System.exit(0);
        }
    }

    private void onStartNewLobby(){
        try {
            if (textField1.getText().isEmpty()) return;
            PrintWriter out = new PrintWriter(server.getOutputStream(), true);
            out.println("NLN " + textField1.getText());
        } catch (IOException exception) {
            WinInfo dialog = new WinInfo();
            dialog.setText("Unexpected error");
            dialog.pack();
            dialog.setVisible(true);
            System.exit(0);
        }
    }

    private void onOK() {
        //String selected = list1.getSelectedValue();
        try {
            if (list1.getSelectedValue() == null) return;
            if (list1.getSelectedValue().isEmpty()) return;
            PrintWriter out = new PrintWriter(server.getOutputStream(), true);
            MainFrame mainFrame = new MainFrame(server, false);
            clientListener.setMainFrame(mainFrame);
            out.println("ALN " + list1.getSelectedValue());
        } catch (IOException exception) {
            WinInfo dialog = new WinInfo();
            dialog.setText("Unexpected error");
            dialog.pack();
            dialog.setVisible(true);
            System.exit(0);
        }
        setVisible(false);
    }

    private void onCancel() {
        dispose();
        System.exit(1);
    }

//    public static void main(String[] args) {
//        LobbiesList dialog = new LobbiesList();
//        dialog.pack();
//        dialog.setVisible(true);
//        //System.exit(0);
//    }
}
