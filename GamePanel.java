import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements ActionListener {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new GamePanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 175;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];

    //these are for the graphics, may need to make them smaller
    private BufferedImage appleImage;
    private BufferedImage pauseImage;
    private BufferedImage endImage;
    private BufferedImage gAppleImage;

    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    boolean paused = false; 

    Timer timer;
    Random random;

    GamePanel() { 
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter()); //researched what this means/does
    
        //for the pause button 
        JButton pauseButton = new JButton("Pause"); 
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paused = !paused; 
                if (!paused) {
                    requestFocusInWindow(); 
                }
            }
        });

        //adds the button to the top of the screen
        this.add(pauseButton, BorderLayout.SOUTH); 
    
        //used class resources and a youtube resource for this:
        try {
            appleImage = ImageIO.read(new File("apple.png")); 
            pauseImage = ImageIO.read(new File("pause.png"));
            endImage = ImageIO.read(new File("end.png"));
            gAppleImage = ImageIO.read(new File("gapple.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        startGame();
    }
    
    

    public void startGame() {
        //this loads in the apple png file 
        try {
            appleImage = ImageIO.read(new File("path/to/apple.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (paused) {
            drawPauseScreen(g); 
        } else {
            draw(g);
        }
    }

    public void draw(Graphics g) {
        if (running) {
            if (applesEaten > 0 && applesEaten % 5 == 0) { 
                //this generates a golden apple
            g.drawImage(gAppleImage, appleX, appleY, 27, 27, this);
            } else { 
                //can change the size of the apple (don't make bigger than 30x30 or snake clips into apple)
            g.drawImage(appleImage, appleX, appleY, 27, 27, this);
            }
                //snakes coloring 
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            //showcases score depending on how many apples the player has eaten
            g.setColor(Color.white);
            g.setFont(new Font("Bodoni Ornaments ITC TT", Font.ITALIC, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 5,
                    g.getFont().getSize());
        } else {
            gameOver(g);
        }

    }

    public void drawPauseScreen(Graphics g) {
        //displays the pause screen but the graphic is preferred, can be interchangeable 

        /*g.setColor(Color.white);
        g.setFont(new Font("Bodoni Ornaments ITC TT", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Paused", (SCREEN_WIDTH - metrics.stringWidth("Game Paused")) / 2, SCREEN_HEIGHT / 2);*/

        g.drawImage(pauseImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
    }

    public void newApple() {
        int randomChance = random.nextInt(20); 
        if (randomChance == 0) {
            appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        } else {
            appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        }
    }
    
    public void move() { 
        //this methods code is directly from youtube resource, was not sure how to code this myself :(
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
    
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    
    }
    
    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            if (applesEaten > 0 && applesEaten % 5 == 0) {
                // if a golden apple spawns and the player eats it, their score will go up by 3
                bodyParts++;
                applesEaten += 3; 
            } else {
                // normal apple
                bodyParts++;
                applesEaten++;
            }
            newApple();
        }
    }
    
    public void checkCollisions() {
        // referenced from stackoverflow collisions problem
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
    
        if (x[0] < 0) {
            running = false;
        }
    
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }
    
        if (y[0] < 0) {
            running = false;
        }
    
        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }
    
        if (!running) {
            timer.stop();
        }
    }
    
    public void gameOver(Graphics g) {
        // this part includes code for the players final score & gameover screen
        // it can be interchanged with the graphic!

        /*g.setColor(Color.red);
        g.setFont(new Font("Bodoni Ornaments ITC TT", Font.ITALIC, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten,
        (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 5, g.getFont().getSize());
        g.setColor(Color.red);
        g.setFont(new Font("Bodoni Ornaments ITC TT", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);*/

        g.drawImage(endImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) { //not paused here
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }
    
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            // these are keyboard inputs, used youtube resource 
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
