import javax.swing.*;
import java.util.HashSet;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;

public class Pacman extends JPanel implements ActionListener, KeyListener {

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int beginX;
        int beginY;
        char direction = 'U';
        int speedX = 0;
        int speedY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.beginX = x;
            this.beginY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();

            this.x += this.speedX;
            this.y += this.speedY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.speedX;
                    this.y -= this.speedY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if (this.direction == 'U') {
                this.speedX = 0;
                this.speedY = -tileSize / 4;
            } else if (this.direction == 'D') {
                this.speedX = 0;
                this.speedY = tileSize / 4;
            } else if (this.direction == 'L') {
                this.speedX = -tileSize / 4;
                this.speedY = 0;
            } else if (this.direction == 'R') {
                this.speedX = tileSize / 4;
                this.speedY = 0;
            }
        }

        void reset() {
            this.x = this.beginX;
            this.y = this.beginY;
        }
    }

    private int rows = 21;
    private int columns = 19;
    private int tileSize = 32;
    private int boardWidth = columns * tileSize;
    private int boardHeight = rows * tileSize;

    private Image wallPicture;
    private Image blueGhostPicture;
    private Image pinkGhostPicture;
    private Image redGhostPicture;
    private Image orangeGhostPicture;
    private Image pacmanRightPicture;
    private Image pacmanLeftPicture;
    private Image pacmanUpPicture;
    private Image pacmanDownPicture;

    private String[] tiles = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacMan;

    Timer gameLoop;
    char[] directions = { 'U', 'D', 'L', 'R' };
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;
    boolean paused = false;

    Pacman() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        wallPicture = new ImageIcon(getClass().getResource("./wall.png")).getImage(); // use ./ to check within the src
                                                                                      // // folder
        blueGhostPicture = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        pinkGhostPicture = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostPicture = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        orangeGhostPicture = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pacmanRightPicture = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();
        pacmanLeftPicture = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanUpPicture = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownPicture = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();

        loadMap();
        gameLoop = new Timer(50, this);
        gameLoop.start();
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }

    }

    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                String row = tiles[i];
                char tilesChar = row.charAt(j);

                int x = j * tileSize;
                int y = i * tileSize;

                if (tilesChar == 'X') {
                    Block wall = new Block(wallPicture, x, y, tileSize, tileSize);
                    walls.add(wall); // add to the walls hashset
                } else if (tilesChar == 'b') {
                    Block ghost = new Block(blueGhostPicture, x, y, tileSize, tileSize);
                    ghosts.add(ghost); // add to ghosts hashset
                } else if (tilesChar == 'o') {
                    Block ghost = new Block(orangeGhostPicture, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tilesChar == 'p') {
                    Block ghost = new Block(pinkGhostPicture, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tilesChar == 'r') {
                    Block ghost = new Block(redGhostPicture, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                } else if (tilesChar == 'P') {
                    pacMan = new Block(pacmanRightPicture, x, y, tileSize, tileSize);
                } else if (tilesChar == ' ') {
                    Block food = new Block(null, x + 14, y + 14, 4, 4); // draw a small rectangle
                    foods.add(food); // add to foods hashset
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // draw the map with all images
    public void draw(Graphics g) {
        g.drawImage(pacMan.image, pacMan.x, pacMan.y, pacMan.width, pacMan.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), tileSize / 2, tileSize / 2);
        } else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize / 2, tileSize / 2);
        }

    }

    public void move() {
        pacMan.x += pacMan.speedX;
        pacMan.y += pacMan.speedY;

        // pacman should be able to travel through the middle and reappear on the other
        // side
        if (pacMan.x < 0) {
            pacMan.x = boardWidth - tileSize; // Move to the right right side
        } else if (pacMan.x >= boardWidth) {
            pacMan.x = 0; // Move to the left side
        }

        for (Block wall : walls) {
            if (collision(pacMan, wall)) {
                pacMan.x -= pacMan.speedX;
                pacMan.y -= pacMan.speedY;
                break; // break after collision
            }
        }

        for (Block ghost : ghosts) {
            if (collision(ghost, pacMan)) {
                lives -= 1;
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }

            if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }

            // ghost should be able to move through the gate in the middle and reappear on
            // the other side
            if (ghost.x < 0) {
                ghost.x = boardWidth - tileSize; // Move to the right side
            } else if (ghost.x >= boardWidth) {
                ghost.x = 0; // Move to the left side
            }

            ghost.x += ghost.speedX;
            ghost.y += ghost.speedY;
            for (Block wall : walls) {
                if (collision(ghost, wall)) { // don't allow
                                              // moving through
                                              // gate in th
                                              // middle
                    ghost.x -= ghost.speedX;
                    ghost.y -= ghost.speedY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        // remove eaten food
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacMan, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public boolean isDirectionValid(Block block) {
        int originalX = block.x;
        int originalY = block.y;

        block.x += block.speedX;
        block.y += block.speedY;

        boolean valid = true;
        for (Block wall : walls) {
            if (collision(block, wall)) {
                valid = false;
                break;
            }
        }

        // Revert to the original position
        block.x = originalX;
        block.y = originalY;

        return valid;
    }

    public void resetPositions() {
        pacMan.reset();
        pacMan.speedX = 0;
        pacMan.speedY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!paused && !gameOver) {
            move();
            repaint();
        }
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {

        // pause game
        if (e.getKeyCode() == KeyEvent.VK_P) {
            paused = !paused;
            if (paused) {
                gameLoop.stop();
            } else {
                gameLoop.start();
            }
        }

        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacMan.updateDirection('U');
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacMan.updateDirection('D');
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacMan.updateDirection('L');
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacMan.updateDirection('R');
        }

        if (pacMan.direction == 'U') {
            pacMan.image = pacmanUpPicture;
        } else if (pacMan.direction == 'D') {
            pacMan.image = pacmanDownPicture;
        } else if (pacMan.direction == 'L') {
            pacMan.image = pacmanLeftPicture;
        } else if (pacMan.direction == 'R') {
            pacMan.image = pacmanRightPicture;
        }
    }
}
