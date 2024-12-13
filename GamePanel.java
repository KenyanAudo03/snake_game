import java.awt.*;
import javax.swing.*;
import java.util.Random;
import java.awt.event.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 100;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    private Clip clip;
    private Clip collisionClip;
    JButton newGameButton; // Add a new JButton

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame(false);

        try {
            File soundFile = new File("Snake/pow-90398.wav");
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundFile);
            clip = AudioSystem.getClip();
            clip.open(audioInput);

            File collisionSoundFile = new File("Snake/129219606-jingle-end-game.wav");
            AudioInputStream collisionAudioInput = AudioSystem.getAudioInputStream(collisionSoundFile);
            collisionClip = AudioSystem.getClip();
            collisionClip.open(collisionAudioInput);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize the new game button
        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
        newGameButton.setFocusable(false);

        // Add the new game button to the panel
        this.add(newGameButton);
        newGameButton.setVisible(false); // Initially, the button is invisible
    }

    public void startGame(boolean initialRunning) {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        
        if (running){
         timer.start();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect((x[i]), y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2,
                    g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
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
            bodyParts++;
            applesEaten++;
            newApple();
            playSound();
        }
    }

    private void playSound() {
        new Thread(() -> {
            try {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void checkCollisions() {
      for (int i = bodyParts; i > 0; i--) {
          if ((x[0] == x[i]) && (y[0] == y[i])) {
              running = false;
              playCollisionSound();
          }
      }
  
      // check if head touches left border or right border
      if (x[0] < 0 || x[0] >= SCREEN_WIDTH) {
          running = false;
          playCollisionSound();
      }
  
      // check if head touches top border or bottom border
      if (y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
          running = false;
          playCollisionSound();
      }
  
      if (!running) {
          timer.stop();
          newGameButton.setVisible(true);
      }
  }
  

    private void playCollisionSound() {
        new Thread(() -> {
            try {
                if (collisionClip.isRunning()) {
                    collisionClip.stop();
                }
                collisionClip.setFramePosition(0);
                collisionClip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten,
                (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());

        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        // Display the new game button
        newGameButton.setBounds(SCREEN_WIDTH / 4, SCREEN_HEIGHT / 2 + 50, SCREEN_WIDTH / 2, 50);
        newGameButton.setVisible(true);
    }

    // Method to reset the game
    private void resetGame() {
      newGameButton.setVisible(false);
      running = false;
      bodyParts = 6;
      applesEaten = 0;
      direction = 'R';
  
      // Stop the timer if it is running
      if (timer != null && timer.isRunning()) {
          timer.stop();
      }
  
      // Reset the snake coordinates
      for (int i = 0; i < bodyParts; i++) {
          x[i] = SCREEN_WIDTH / 2 - i * UNIT_SIZE;
          y[i] = SCREEN_HEIGHT / 2;
      }
  
      // Start the game immediately
      startGame(false);
      repaint();
  }
  

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
      @Override
      public void keyPressed(KeyEvent e) {
        if (!running) {
            startGame(false);
        }
          switch (e.getKeyCode()) {
              case KeyEvent.VK_LEFT:
              case KeyEvent.VK_RIGHT:
              case KeyEvent.VK_UP:
              case KeyEvent.VK_DOWN:
                  // Start the game when a direction key is pressed
                  
                  // Handle direction changes as usual
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
                  break;
            }
        }
    }
}
