import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserAccount {
    private static final long serialVersionUID = 1L;
    private List<String> history;
    private String username;
    private List<GameRecord> gameHistory = new ArrayList<>();
    private List<String> friends = new ArrayList<>();
    private int gamesWon;
    private String password;
    private int highScore;
    private long bestTime;

    public UserAccount(String username, String password) {
        this.username = username;
        this.password = password;
        this.highScore = 0;
        this.bestTime = Long.MAX_VALUE;
        this.history = new ArrayList<>();
        this.gameHistory = new ArrayList<>();
    }
    public void updateGameHistory(GameRecord newRecord) {
        this.gameHistory.add(newRecord);
        // Update high score and best time if necessary
        if (newRecord.getPlayer1Score() > this.highScore) {
            this.highScore = newRecord.getPlayer1Score();
        }
        if (newRecord.getElapsedTimeInSeconds() < this.bestTime) {
            this.bestTime = newRecord.getElapsedTimeInSeconds();
        }
    }
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getHighScore() {
        return highScore;
    }

    public long getBestTime() {
        return bestTime;
    }

    public List<GameRecord> getGameHistory() {
        return gameHistory;
    }

    public void addGameRecord(GameRecord record) {
        gameHistory.add(record);
    }
    // Setters
    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public void setBestTime(long bestTime) {
        this.bestTime = bestTime;
    }
    public void addFriend(String friendUsername) {
        if (!friends.contains(friendUsername)) {
            friends.add(friendUsername);
        }
    }

    public List<String> getFriends() {
        return new ArrayList<>(friends);
    }

    public void incrementGamesWon() {
        gamesWon++;
    }

    public int getGamesWon() {
        return gamesWon;
    }
    public void addHistoryEntry(String entry) {
        history.add(entry);
    }

    public static class AccountManager {
        private static Map<String, UserAccount> accounts = new HashMap<>();
        private static final String DATA_FILE = "data.txt";

        static {
            loadAccounts(); // Load accounts from file when the class is initialized
        }
        public static List<UserAccount> getAllAccountsSortedByScore() {
            // Return a new list to avoid modifying the original map
            return accounts.values().stream()
                    .sorted(Comparator.comparingInt(UserAccount::getHighScore).reversed())
                    .collect(Collectors.toList());
        }

        public static boolean createAccount(String username, String password) {
            // Validate inputs
            if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                System.out.println("Invalid username or password.");
                return false; // Invalid input
            }

            // Check if username already exists
            if (accounts.containsKey(username)) {
                System.out.println("Username already exists.");
                return false; // Username already exists
            }

            // Create new account and add it to the map
            UserAccount newAccount = new UserAccount(username, password);
            accounts.put(username, newAccount);

            // Save the updated accounts list to the file
            saveAccounts();
            return true;
        }



        public static void saveAccounts() {
            try (PrintWriter out = new PrintWriter(new FileWriter(DATA_FILE))) {
                for (UserAccount account : accounts.values()) {
                    // Concatenating basic account info with a comma separator
                    String accountInfo = String.join(",", account.getUsername(), account.getPassword(),
                            String.valueOf(account.getHighScore()), String.valueOf(account.getBestTime()));
                    // Adding the friends list, joined by semicolons, if not empty
                    String friendsList = account.getFriends().isEmpty() ? "" : String.join(";", account.getFriends());
                    out.println(accountInfo + (friendsList.isEmpty() ? "" : "," + friendsList));
                    // Writing game records
                    for (GameRecord record : account.getGameHistory()) {
                        out.println("--GameRecordStart--"); // Start marker
                        out.println(record.getPlayer1Score() + "," + record.getPlayer2Score() + "," + record.getElapsedTimeInSeconds());
                        out.println("--GameRecordEnd--"); // End marker
                    }
                    out.println("--AccountEnd--"); // End of an account's data
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void loadAccounts() {
            File dataFile = new File(DATA_FILE);
            if (dataFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
                    String line;
                    UserAccount currentAccount = null;
                    while ((line = br.readLine()) != null) {
                        if (line.equals("--")) {
                            currentAccount = null; // Reset for the next account
                        } else if (currentAccount == null) {
                            String[] parts = line.split(",", -1); // -1 to include trailing empty strings
                            if (parts.length >= 4) { // Account info present
                                String username = parts[0];
                                String password = parts[1];
                                int highScore = Integer.parseInt(parts[2]);
                                long bestTime = Long.parseLong(parts[3]);
                                currentAccount = new UserAccount(username, password);
                                currentAccount.setHighScore(highScore);
                                currentAccount.setBestTime(bestTime);
                                accounts.put(username, currentAccount);
                                if (parts.length == 5 && !parts[4].isEmpty()) { // Friends list exists and is not empty
                                    Arrays.stream(parts[4].split(";")).forEach(currentAccount::addFriend);
                                }
                            }
                        } else {
                            String[] parts = line.split(",");
                            if (parts.length == 3) { // Game record
                                int player1Score = Integer.parseInt(parts[0]);
                                int player2Score = Integer.parseInt(parts[1]);
                                long elapsedTimeInSeconds = Long.parseLong(parts[2]);
                                GameRecord record = new GameRecord(player1Score, player2Score, elapsedTimeInSeconds);
                                currentAccount.addGameRecord(record);
                            }
                        }
                    }
                } catch (IOException | NumberFormatException e) {
                    System.err.println("Error loading accounts: " + e.getMessage());
                }
            } else {
                System.out.println("Data file not found. No accounts to load.");
            }
        }



        public static UserAccount login(String username, String password) {
            UserAccount account = accounts.get(username);
            if (account != null && account.getPassword().equals(password)) {
                // Update history or any other necessary information
                return account;
            }
            return null;
        }
        public static void updateHistory(String username, String entry) {
            UserAccount account = accounts.get(username);
            if (account != null) {
                account.addHistoryEntry(entry);
                saveAccounts(); // Save updated history
            }
        }


        public static UserAccount getAccountByUsername(String username) {
            return accounts.get(username); // This directly returns the UserAccount object or null if not found
        }

        public static List<UserAccount> getAllAccounts() {
            return new ArrayList<>(accounts.values());
        }
    }


}

