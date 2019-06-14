package megamek.client.ui.swing.nbt;

import com.netbattletech.nbt.*;
import com.netbattletech.nbt.model.Lobby;
import megamek.client.ui.Messages;

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

    private JSpinner spinnerUpdateInterval;
    private JSpinner spinnerMaxBV;

    private JTable lobbyTable;
    private JComboBox leagueList;
    private ChatWidget chat;

    private LobbyTableModel lobbyTableModel;

    ISessionDataModel dataModel;
    ISessionViewControl controller;

    void setJoinButtonState(boolean enabled) {
        synchronized (buttonJoinSelected) {
            buttonJoinSelected.setEnabled(enabled);
        }
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

        // panel to contain the right-side button/control stack
        JPanel rightPanel = new JPanel(new GridBagLayout());

        // panel to contain these two panels
        JPanel topPanel = new JPanel(new GridBagLayout());

        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.NORTHWEST;
        topPanel.add(tablePanel, c);
        c.weightx = 0.0;
        c.anchor = GridBagConstraints.NORTHEAST;
        c.gridx = 1;
        topPanel.add(rightPanel, c);

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
        c.gridy = 0;
        tablePanel.add(new JLabel(Messages.getString("LookingForGame.availableLobbies")), c);
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.WEST;
        c.weighty = 1.0;
        c.gridy = 1;
        tablePanel.add(new JScrollPane(lobbyTable), c);

        chat = new ChatWidget();
        c.gridy = 2;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        tablePanel.add(chat, c);

        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.gridy = 3;
        JPanel lobbyButtons = new JPanel();
        lobbyButtons.add(buttonJoinSelected);
        lobbyButtons.add(buttonCreateLobby);
        lobbyButtons.add(new JLabel(Messages.getString("LookingForGame.maxBV")));
        lobbyButtons.add(spinnerMaxBV);
        tablePanel.add(lobbyButtons, c);
        setJoinButtonState(false);

        // right-side button/control stack
        JLabel leagueListLabel = new JLabel(Messages.getString("LookingForGame.leagueListing"));
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        rightPanel.add(leagueListLabel, c);

        leagueList = new JComboBox();
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        rightPanel.add(leagueList, c);

        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.EAST;
        rightPanel.add(new JLabel(Messages.getString("LookingForGame.refreshInterval")), c);
        spinnerUpdateInterval = new JSpinner(new SpinnerNumberModel(5, 1, 30, 1));
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        rightPanel.add(spinnerUpdateInterval, c);

        JPanel spacer = new JPanel();
        spacer.setMaximumSize(new Dimension(20000,20000));
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.weighty = 1.0;
        c.weightx = 1.0;
        rightPanel.add(spacer, c);

        buttonOK = new JButton(Messages.getString("LookingForGame.Okay")); //$NON-NLS-1$
        buttonCancel = new JButton(Messages.getString("LookingForGame.Cancel")); //$NON-NLS-1$

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
                        return sessionDataModel.lobbies().get(rowIndex).playerCount();
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
