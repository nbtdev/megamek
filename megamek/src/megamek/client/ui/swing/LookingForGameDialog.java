package megamek.client.ui.swing;

import megamek.client.ui.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LookingForGameDialog extends JDialog {
    private JButton buttonOK;
    private JButton buttonCancel;

    public LookingForGameDialog(JFrame frame) {
        super(frame, Messages.getString("MegaMek.LookingForGame.title"), true);

        // create and layout UI
        buttonOK = new JButton(Messages.getString("Okay")); //$NON-NLS-1$
        buttonOK.setSize(80, 24);
        buttonCancel = new JButton(Messages.getString("Cancel")); //$NON-NLS-1$
        buttonCancel.setSize(80, 24);

        GridBagLayout gridbag = new GridBagLayout();
        JPanel contentPane = (JPanel)getContentPane();
        contentPane.setLayout(gridbag);
        contentPane.add(buttonCancel);
        contentPane.add(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }});

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
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

        pack();
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
