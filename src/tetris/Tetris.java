package tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.TimeUnit;

public class Tetris {

    private int height;
    private int width;
    private int speed;
    private String pixel;
    private String noPixel;
    private String action;
    private String[][] frame;
    private String[][] shape;
    private boolean startGame;

    public Tetris() throws InterruptedException {
        height = 20;
        width = 10;
        speed = 500; //minimum 40
        pixel = "▓▓";
        noPixel = "░░";
        action = "";
        frame = emptyFrame();
        startGame = true;

        KeyListener();

        while (true) {
            frame = fallingRender(frame, randomShape());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Tetris();
    }

    public String[][] fallingRender(String[][] frame, String[][] shape) throws InterruptedException {
        int centerOfFrame = frame[1].length / 2;
        int centerOfShape = shape[1].length / 2;
        int center = centerOfFrame - centerOfShape;
        for (int frameRow = 0; frameRow < frame.length; frameRow++) {
            if (isSpaceBelow(frame, shape, frameRow, center)) {
                for (int f = 0; f < shape[1].length; f++) {
                    // (IF) to exclude filling of existing frame-pixel ▓▓ by empty shape-pixel ░░
                    if (shape[shape.length - 1][f].equals(pixel)) {
                        frame[frameRow][center + f] = shape[shape.length - 1][f];
                    }
                }
                for (int w = 0; w < shape.length - 1; w++) {
                    if (frameRow > w) {
                        for (int f = 0; f < shape[1].length; f++) {
                            // (IF) to exclude filling of existing frame-pixel (or shape-pixel) ▓▓ by empty shape-pixel ░░
                            if (shape[shape.length - 2 - w][f].equals(pixel) || (shape[shape.length - 2 - w][f].equals(noPixel) && shape[shape.length - 1 - w][f].equals(pixel))) {
                                frame[frameRow - 1 - w][center + f] = shape[shape.length - 2 - w][f];
                            }
                        }
                    }
                }
                // filling of empty row above the shape
                if (frameRow > shape.length - 1) {
                    for (int f = 0; f < shape[1].length; f++) {
                        frame[frameRow - shape.length][center + f] = noPixel;
                    }
                }
                printFrame(frame);
                action(); //check action request (left, right, rotate, down, pause)
            } else {
                break;
            }
        }
        return frame;
    }

    public boolean isSpaceBelow(String[][] frame, String[][] shape, int frameRow, int center) {
        int k = 0;
        if (frameRow == frame.length) {
            return false;
        }
        for (int i = 0; i < shape[1].length; i++) {
            for (int j = 0; j < shape.length; j++) {
                if (shape[shape.length - 1 - j][i].equals(noPixel)) {
                    k++;
                } else {
                    break;
                }
            }
            if (k > frameRow) {
                k = frameRow;
            }
            if (frame[frameRow - k][center + i].equals(pixel)) {
                return false;
            }
            k = 0;
        }
        return true;
    }

    public void printFrame(String[][] frame) {
        System.out.print("\033\143");
        for (int i = 0; i < frame.length; i++) {
            for (int j = 0; j < frame[i].length; j++) {
                System.out.print(frame[i][j]);
            }
            System.out.println();
        }
    }

    public void printPause(String[][] frame) {
        int centerX = width/2-3;
        int centerY = height/2;
        System.out.print("\033\143");
        for (int i = 0; i < frame.length; i++) {
            for (int j = 0; j < frame[i].length; j++) {
                if (i == centerY && j == centerX) {
                    System.out.print("  ");
                } else if (i == centerY && j == centerX+1) {
                    System.out.print("P ");
                } else if (i == centerY && j == centerX+2) {
                    System.out.print("A ");
                } else if (i == centerY && j == centerX+3) {
                    System.out.print("U ");
                } else if (i == centerY && j == centerX+4) {
                    System.out.print("S ");
                } else if (i == centerY && j == centerX+5) {
                    System.out.print("E ");
                } else {
                    System.out.print(frame[i][j]);
                }
            }
            System.out.println();
        }
    }

    public void KeyListener() {
        JFrame jFrame = new JFrame("Tetris");
        jFrame.setSize(150, 150);
        jFrame.getContentPane().setBackground(Color.BLACK);
        JLabel jLabel = new JLabel("", JLabel.CENTER);
        jLabel.setFont(new Font("Andale", Font.PLAIN, 13));
        jLabel.setForeground(Color.white);
        jFrame.add(jLabel);
        jFrame.setVisible(true);

        jFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 27) {
                    System.exit(0);
                }
                if (e.getKeyCode() == 37 && startGame) {
                    action = "left";
                }
                if (e.getKeyCode() == 39 && startGame) {
                    action = "right";
                }
                if (e.getKeyCode() == 83 && !startGame) {
                    startGame = true;
                } else if (e.getKeyCode() == 83 && startGame) {
                    startGame = false;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                jLabel.setText(e.getKeyText(e.getKeyCode()));
            }
        });
    }

    public void action() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            waiting(speed / 10);
            // LEFT
            if (action.equals("left")) {
                if (frame[0][0].equals(noPixel)) {
                    frame[0][0] = pixel;
                    action = "";
                    printFrame(frame);
                } else {
                    frame[0][0] = noPixel;
                    action = "";
                    printFrame(frame);
                }
            }
            // PAUSE
            boolean isPausePrinted = false;
            while (!startGame) {
                waiting(1);
                if (!isPausePrinted) {
                    printPause(frame);
//                    System.out.print("     P A U S E     ");
                    isPausePrinted = true;
                }
            }
            if (startGame && isPausePrinted) {
                printFrame(frame);
            }
        }
    }

    public String[][] randomShape() {

        int a = (int) (Math.random() * 9);
        String[][] shape;

        /*   1     2     0     3     4     5     6     8
         *               ▓▓                            ▓▓▓▓▓▓
         *   ▓▓    ▓▓    ▓▓    ▓▓    ▓▓▓▓    ▓▓  ▓▓▓▓  ▓▓  ▓▓
         *   ▓▓▓▓  ▓▓    ▓▓    ▓▓▓▓  ▓▓▓▓  ▓▓▓▓    ▓▓      ▓▓
         *     ▓▓  ▓▓▓▓  ▓▓    ▓▓          ▓▓      ▓▓      ▓▓
         * */
        String[][] shape0 = {{"▓▓", "░░"}, {"▓▓", "░░"}, {"▓▓", "░░"}, {"▓▓", "░░"}};
        String[][] shape1 = {{"▓▓", "░░"}, {"▓▓", "▓▓"}, {"░░", "▓▓"}};
        String[][] shape2 = {{"▓▓", "░░"}, {"▓▓", "░░"}, {"▓▓", "▓▓"}};
        String[][] shape3 = {{"▓▓", "░░"}, {"▓▓", "▓▓"}, {"▓▓", "░░"}};
        String[][] shape4 = {{"▓▓", "▓▓"}, {"▓▓", "▓▓"}};
        String[][] shape5 = {{"░░", "▓▓"}, {"▓▓", "▓▓"}, {"▓▓", "░░"}};
        String[][] shape6 = {{"▓▓", "▓▓"}, {"░░", "▓▓"}, {"░░", "▓▓"}};
        String[][] shape7 = {{"░░", "░░", "░░", "░░"}, {"▓▓", "▓▓", "▓▓", "▓▓"}};
        String[][] shape8 = {{"▓▓", "▓▓", "▓▓"}, {"▓▓", "░░", "▓▓"}, {"░░", "░░", "▓▓"}, {"░░", "░░", "▓▓"}};

        switch (a) {
            case 0:
                shape = shape0;
                break;
            case 1:
                shape = shape1;
                break;
            case 2:
                shape = shape2;
                break;
            case 3:
                shape = shape3;
                break;
            case 4:
                shape = shape4;
                break;
            case 5:
                shape = shape5;
                break;
            case 6:
                shape = shape6;
                break;
            case 7:
                shape = shape7;
                break;
            case 8:
                shape = shape8;
                break;
            default:
                shape = shape0;
        }
        return shape;
    }

    public String[][] emptyFrame() {
        String[][] field = new String[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                field[i][j] = noPixel;
            }
        }
        return field;
    }

    public void waiting(int milisecond) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(milisecond);
    }
}
