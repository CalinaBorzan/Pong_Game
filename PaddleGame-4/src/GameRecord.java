public class GameRecord {
    private int player1Score;
    private int player2Score;
    private long elapsedTimeInSeconds;

    public GameRecord(int player1Score, int player2Score, long elapsedTimeInSeconds) {
        this.player1Score = player1Score;
        this.player2Score = player2Score;
        this.elapsedTimeInSeconds = elapsedTimeInSeconds;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public long getElapsedTimeInSeconds() {
        return elapsedTimeInSeconds;
    }
}
