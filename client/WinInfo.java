package client;

import javax.swing.*;
import java.awt.event.*;

public class WinInfo extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    //private JButton buttonCancel;
    private JLabel info;

    public WinInfo() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        setResizable(false);
    }

    public void setText(String text){
        info.setText(text);
    }

    public void setInfo(int state){
        String text;
        if (state == 1){
            text = "Congratulation!!!\nYou won!!!";
        }
        else {
            text = "Congratulation!!!\nYou lose!!!";
        }
        info.setText(text);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        WinInfo dialog = new WinInfo();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
