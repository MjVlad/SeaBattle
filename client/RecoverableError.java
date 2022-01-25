package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RecoverableError extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel errorText;

    public RecoverableError() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        setResizable(false);
    }

    public void setErrorText(String text){
        errorText.setText(text);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    public static void main(String[] args) {
        RecoverableError dialog = new RecoverableError();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
