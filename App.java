import javax.swing.JFrame;

public class App {
    public static void main(String[] args) throws Exception {
        int rows = 21;
        int columns = 19;
        int tileSize = 32;
        int boardWidth = columns * tileSize;
        int boardHeight = rows * tileSize;

        JFrame gameFrame = new JFrame("Pac Man");
        gameFrame.setSize(boardWidth, boardHeight);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setResizable(false);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Pacman game = new Pacman();
        gameFrame.add(game);
        gameFrame.pack();
        game.requestFocus();
        gameFrame.setVisible(true);
    }
}
