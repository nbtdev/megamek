package megamek.client.ui.swing.nbt;

import com.netbattletech.nbt.*;
import megamek.client.ui.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LobbyDialog extends JDialog implements LobbySessionView.Target {
    JList playerList;
    JButton cmdReady;
    JButton cmdLaunch;
    JButton cmdRejoin;
    ChatWidget chat;
    PlayerListModel playerListModel;
    ILobbyDataModel dataModel;
    ILobbyViewControl controller;
    Boolean ready = false;

    public LobbyDialog(JFrame parentFrame, ILobbyDataModel model, ILobbyViewControl controller) {
        super(parentFrame, Messages.getString("LookingForGame.lobby.title"), false);
        this.dataModel = model;
        this.controller = controller;
        constructDialog(parentFrame);
    }

    void constructDialog(JFrame parentFrame) {
        setSize(new Dimension(800, 600));

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                close();
            }
        });

        JPanel contentPane = (JPanel)getContentPane();
        contentPane.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridx = 0;
        c.gridy = 0;

        playerListModel = new PlayerListModel(dataModel);
        playerList = new JList(playerListModel);
        contentPane.add(new JLabel(Messages.getString("LookingForGame.lobby.playerList")), c);
        c.weightx = 1.0;
        c.weighty = 5.0;
        c.gridy = 1;
        contentPane.add(playerList, c);

        chat = new ChatWidget();
        c.weighty = 0.0;
        c.gridy = 2;
        contentPane.add(chat, c);

        JPanel controlBar = new JPanel(new GridBagLayout());
        GridBagConstraints c2 = new GridBagConstraints();
        c2.weighty = 0.0;
        c2.weightx = 0.0;
        c2.anchor = GridBagConstraints.WEST;
        c2.fill = GridBagConstraints.NONE;
        c2.gridx = 0;
        c2.gridy = 0;
        c2.gridwidth = 1;
        c2.gridheight = 1;
        JButton cmdLeave = new JButton(Messages.getString("LookingForGame.lobby.leave"));
        JButton cmdKick = new JButton(Messages.getString("LookingForGame.lobby.kick"));
        cmdReady = new JButton(Messages.getString("LookingForGame.lobby.ready"));
        cmdLaunch = new JButton(Messages.getString("LookingForGame.lobby.launch"));
        cmdRejoin = new JButton(Messages.getString("LookingForGame.lobby.rejoin"));

        updateReady();

        controlBar.add(cmdLeave, c2);
        c2.gridy++;
        controlBar.add(cmdKick, c2);
        c2.gridy++;
        controlBar.add(cmdReady, c2);
        c2.gridy++;
        controlBar.add(cmdLaunch, c2);
        c2.gridy++;
        controlBar.add(cmdRejoin, c2);
        c2.gridy++;
        c2.weighty = 1.0;
        c2.fill = GridBagConstraints.BOTH;
        controlBar.add(new JPanel(), c2);
        cmdKick.setEnabled(false);
        cmdLaunch.setEnabled(false);
        cmdRejoin.setVisible(false);

        c.fill = GridBagConstraints.VERTICAL;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 2;
        c.weightx = 0;
        c.weighty = 1.0;
        contentPane.add(controlBar, c);

        cmdLeave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

        cmdKick.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onKickSelectedPlayer();
            }
        });

        cmdReady.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onReady();
            }
        });

        cmdLaunch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLaunch();
            }
        });

        cmdRejoin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRejoin();
            }
        });

        chat.addActionListener(new ChatWidget.Listener() {
            @Override
            public void onChat(String message) {
                controller.postChat(message);
            }
        });

        playerList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public synchronized Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof IPlayer) {
                    IPlayer player = (IPlayer)value;
                    if (player.ready()) {
                        setBackground(Color.GREEN);
                    }
                }
                return c;
            }
        });
    }

    private void updateReady() {
        if (ready) {
            cmdReady.setBackground(Color.green);
            cmdReady.setForeground(Color.black);
        } else {
            cmdReady.setBackground(Color.red);
            cmdReady.setForeground(Color.white);
        }
    }

    private void onKickSelectedPlayer() {
        controller.kick(dataModel.lobby(), null);
    }

    private void onReady() {
        controller.setReady(!ready);
    }

    private void onLaunch() {
        controller.launch(dataModel.lobby());
    }

    private void onRejoin() {
        controller.rejoin();
    }

    @Override
    public synchronized void onAnnouncement(String message) {
        chat.onIncomingChat(null, message);
    }

    @Override
    public synchronized void onChat(IPlayer author, String message) {
        chat.onIncomingChat(author, message);
    }

    @Override
    public synchronized void onError(String message) {
    }

    @Override
    public synchronized void closeDialog() {
        dispose();
    }

    @Override
    public synchronized void update() {
        playerListModel.forceUpdate();
        ready = dataModel.ready();
        updateReady();

        cmdLaunch.setEnabled(dataModel.isLaunchEnabled());

        if (dataModel.isRejoinEnabled()) {
            cmdRejoin.setVisible(true);
            cmdRejoin.setEnabled(true);
        }
    }

    void close() {
        controller.leaveLobby(dataModel.lobby());
        closeDialog();
    }

    private class PlayerListModel extends AbstractListModel {
        ILobbyDataModel dataModel;

        public PlayerListModel(ILobbyDataModel dataModel) {
            this.dataModel = dataModel;
        }

        public void forceUpdate() {
            fireContentsChanged(this, 0, getSize() - 1);
        }

        @Override
        public int getSize() {
            return dataModel.lobby().playerCount() + 1;
        }

        @Override
        public Object getElementAt(int index) {
            // we have N+1 players in the lobby, where "+1" is because
            // the owner is not listed among the riffraff
            if (index > dataModel.lobby().playerCount()) {
                return null;
            }

            if (index == 0) {
                return dataModel.lobby().owner();
            }

            return dataModel.lobby().player(index - 1);
        }
    }
}
