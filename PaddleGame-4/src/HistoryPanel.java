import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class HistoryPanel extends JPanel {
    private JTextArea historyTextArea;
    private JButton backButton;
    private GameFrame frame;

    public HistoryPanel(UserAccount user, GameFrame frame) {
        this.frame = frame; // Store the reference to the GameFrame
        setLayout(new GridBagLayout());

        historyTextArea = new JTextArea(1000, 1000); // Increase the initial size
        historyTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyTextArea);

        List<GameRecord> gameHistory = user.getGameHistory();
        for (GameRecord record : gameHistory) {
            historyTextArea.append("Score: Player 1 - " + record.getPlayer1Score() +
                    ", Time: " + record.getElapsedTimeInSeconds() + " seconds\n");
        }

        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.showStartPanel();
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0; // Allow horizontal expansion
        c.weighty = 1.0; // Allow vertical expansion
        add(scrollPane, c);

        c.gridy = 1;
        c.weighty = 0.0; // Don't allow vertical expansion for the button
        add(backButton, c);

        populateHistory(user);
    }

    private void populateHistory(UserAccount user) {
        historyTextArea.setText(""); // Clear previous text
        for (GameRecord record : user.getGameHistory()) {
            historyTextArea.append(user.getUsername() + ": Score - " + record.getPlayer1Score() +
                    ", Time: " + record.getElapsedTimeInSeconds() + " seconds\n");
        }
    }
}
