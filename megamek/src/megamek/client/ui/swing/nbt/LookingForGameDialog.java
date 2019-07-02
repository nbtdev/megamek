package megamek.client.ui.swing.nbt;

import com.netbattletech.nbt.*;
import com.netbattletech.nbt.model.Lobby;
import megamek.client.ui.Messages;
import megamek.common.preference.PreferenceManager;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;

public class LookingForGameDialog extends JDialog implements LobbySessionView.Target {
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton buttonJoinSelected;
    private JButton buttonCreateLobby;

    private JSpinner spinnerMaxBV;

    private JTable lobbyTable;
    private ChatWidget chat;
    private JTextField playerName;

    private LobbyTableModel lobbyTableModel;

    ISessionDataModel dataModel;
    ISessionViewControl controller;

    void setJoinButtonState(boolean enabled) {
        synchronized (buttonJoinSelected) {
            buttonJoinSelected.setEnabled(enabled);
        }
    }

    public String getPlayerName() {
        if (playerName == null) {
            return "";
        }

        return playerName.getText();
    }

    Lobby getSelectedLobby() {
        return (Lobby)lobbyTable.getValueAt(lobbyTable.getSelectedRow(), 0);
    }

    public LookingForGameDialog(JFrame frame, ISessionDataModel model, ISessionViewControl controller) {
        super(frame, Messages.getString("LookingForGame.title"), false);
        this.dataModel = model;
        this.controller = controller;

        // default size
        setSize(new Dimension(800, 400));
        JPanel lfg = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // root vertical layout
        JPanel contentPane = (JPanel)getContentPane();
        contentPane.setLayout(new GridBagLayout());

        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(1, 1, 1, 1);
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 1;

        contentPane.add(lfg, c);

        // panel to contain the table and controls
        JPanel tablePanel = new JPanel(new GridBagLayout());

        // panel to contain these two panels
        JPanel topPanel = new JPanel(new GridBagLayout());

        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.NORTHWEST;
        topPanel.add(tablePanel, c);

        // panel to contain the bottom row of buttons (OK, Cancel)
        JPanel buttonPanel = new JPanel(new GridBagLayout());

        // top-level dialog layout
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.NORTH;
        lfg.add(topPanel, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.SOUTH;
        c.gridy = 1;
        lfg.add(buttonPanel, c);

        // populate the table panel with the table and the "join selected lobby" button
        lobbyTableModel = new LobbyTableModel(dataModel);
        lobbyTable = new JTable(lobbyTableModel);
        buttonJoinSelected = new JButton(Messages.getString("LookingForGame.joinSelected")); //$NON-NLS-1$
        buttonCreateLobby = new JButton(Messages.getString("LookingForGame.createLobby")); //$NON-NLS-1$
        spinnerMaxBV = new JSpinner(new SpinnerNumberModel(5000, 1000, 100000, 250));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;

        int gridY = 0;
        c.gridy = gridY++;

        // subpanel for the callsign entry label and box
        JPanel callsignPanel = new JPanel(new GridBagLayout());
        tablePanel.add(callsignPanel, c);
        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.gridx = 0;
        c2.gridy = 0;
        c2.weightx = 0.0;
        c2.weighty = 0.0;
        callsignPanel.add(new JLabel(Messages.getString("LookingForGame.callsign") + ": "), c2);

        c2.gridx = 1;
        c2.weightx = 1.0;
        IPlayer player = model.player();
        String callsign = new String();
        if (player != null) {
            callsign = player.callsign();
        }
        playerName = new JTextField(callsign);
        callsignPanel.add(playerName, c2);

        c2.weightx = 0.0;
        c2.gridx = 2;
        JButton updateButton = new JButton(Messages.getString("LookingForGame.update"));
        callsignPanel.add(updateButton, c2);


        c.gridy = gridY++;
        tablePanel.add(new JLabel(Messages.getString("LookingForGame.availableLobbies")), c);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.WEST;
        c.weighty = 1.0;
        c.gridy = gridY++;
        tablePanel.add(new JScrollPane(lobbyTable), c);

        chat = new ChatWidget();
        c.gridy = gridY++;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        tablePanel.add(chat, c);

        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.gridy = gridY++;
        JPanel lobbyButtons = new JPanel();
        lobbyButtons.add(buttonJoinSelected);
        lobbyButtons.add(buttonCreateLobby);
        lobbyButtons.add(new JLabel(Messages.getString("LookingForGame.maxBV")));
        lobbyButtons.add(spinnerMaxBV);
        tablePanel.add(lobbyButtons, c);
        setJoinButtonState(false);

        // button panel
        buttonOK = new JButton(Messages.getString("Okay")); //$NON-NLS-1$
        buttonCancel = new JButton(Messages.getString("Cancel")); //$NON-NLS-1$

        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.EAST;
        JPanel buttons = new JPanel();
        buttons.add(buttonCancel);
        buttons.add(buttonOK);
        buttonPanel.add(buttons, c);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }});

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        buttonJoinSelected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.joinLobby(getSelectedLobby());
            }
        });

        buttonCreateLobby.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.requestLobbyCreate(null, 0, (Integer)spinnerMaxBV.getValue());
            }
        });

        lobbyTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    // wait until the changes are all done
                    return;
                }

                // we support only single row selection, so the first row is sufficient
                int row = e.getFirstIndex();
                Lobby lobby = (Lobby)lobbyTable.getValueAt(row, 0);
                setJoinButtonState(lobby!=null);
            }
        });

        lobbyTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                lobbyTable.clearSelection();
                setJoinButtonState(false);
            }
        });

        chat.addActionListener(new ChatWidget.Listener() {
            @Override
            public void onChat(String message) {
                controller.postChat(message);
            }
        });

        updateButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setCallsign(playerName.getText());
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
        controller.onViewClose();
        dispose();
    }

    private void onCancel() {
        controller.onViewClose();
        dispose();
    }
    @Override
    public void onAnnouncement(String message) {
        chat.onIncomingChat(null, message);
    }

    @Override
    public void onChat(IPlayer author, String message) {
        chat.onIncomingChat(author, message);
    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void closeDialog() {
        dispose();
    }

    @Override
    public void update() {
        lobbyTable.updateUI();
        playerName.setText(dataModel.player().callsign());
        PreferenceManager.getClientPreferences().setLastPlayerName(dataModel.player().callsign());
    }

    public class LobbyTableModel extends AbstractTableModel {
        ISessionDataModel sessionDataModel;
        public String columnNames[] = {
                Messages.getString("LookingForGame.lobbies.header.owner"),
                Messages.getString("LookingForGame.lobbies.header.nPlayers"),
                Messages.getString("LookingForGame.lobbies.header.minBV"),
                Messages.getString("LookingForGame.lobbies.header.maxBV"),
        };

        public LobbyTableModel(ISessionDataModel sessionDataModel) {
            this.sessionDataModel = sessionDataModel;
        }

        @Override
        public int getRowCount() {
            return sessionDataModel.lobbies().size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex < sessionDataModel.lobbies().size()) {
                switch (columnIndex) {
                    case 0:
                        return sessionDataModel.lobbies().get(rowIndex);
                    case 1:
                        return sessionDataModel.lobbies().get(rowIndex).playerCount() + 1;
                    case 2:
                        return sessionDataModel.lobbies().get(rowIndex).lowerLimit();
                    case 3:
                        return sessionDataModel.lobbies().get(rowIndex).upperLimit();
                }
            }

            return null;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
    }
}
