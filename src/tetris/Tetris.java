package tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.TimeUnit;

public class Tetris {

    private int frameHeight;
    private int frameWidth;
    private int gameSpeed;
    private String pixel;
    private String noPixel;
    private String action;
    private String[][] frame;
    private String[][] shape;
    private int positionX;
    private int positionY;
    private int gameScore;
    private boolean isGamePaused;

    public static void main(String[] args) throws InterruptedException {
        new Tetris();
    }

    public Tetris() throws InterruptedException {
        KeyListener();
        frameHeight = 20;
        frameWidth = 10;
        gameSpeed = 500; //minimum 40
        pixel = "▓▓";
        noPixel = "░░";
        action = "";
        frame = emptyFrame();
        gameScore = 0;
        isGamePaused = false;

        while (true) {
            randomShape();
            positionX = frameWidth / 2 - shape[0].length / 2; // default middle horizontal position of the shape
            for (positionY = 0; positionY < frameHeight; positionY++) {
                if (isSpaceBelow(positionY - 1)) {
                    printFrame();
                    action(); // check action request (left, right, rotate, down, start/pause)
                    if (!isSpaceBelow(positionY)) {
                        if (positionY == 0) {
                            System.exit(0);
                        }
                        combineFrame();
                        checkFilledLines();
                        break;
                    }
                }
            }
        }
    }

    public boolean isRotationAvailable() {
        String[][] rotatedShape = new String[shape[0].length][shape.length];
        for (int y = 0; y < shape[0].length; y++) {
            for (int x = 0; x < shape.length; x++) {
                rotatedShape[y][x] = shape[x][shape[0].length - 1 - y];
            }
        }
        int rotationIndex = Math.abs(shape.length - shape[0].length);
        if (isSpaceForRotation(rotatedShape, positionX)) {
            return true;
        } else if (!isSpaceForRotation(rotatedShape, positionX) && rotationIndex == 0) {
            return false;
        } else if (!isSpaceForRotation(rotatedShape, positionX) && rotationIndex != 0) {
            for (int i = 1; i <= rotationIndex; i++) {
                if (positionX - i < 0) {
                    return false;
                }
                if (isSpaceForRotation(rotatedShape, positionX - i)) {
                    positionX -= i;
                    return true;
                }
            }
            for (int i = 1; i <= rotationIndex; i++) {
                if (isSpaceForRotation(rotatedShape, positionX + i)) {
                    positionX += i;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSpaceForRotation(String[][] rotatedShape, int positionX) {
        int rightSideIndex;
        rightSideIndex = frameWidth - positionX - shape.length;
        if (rightSideIndex < 0) {
            positionX += rightSideIndex;
        }
        for (int y = rotatedShape.length - 1; y >= 0; y--) {
            for (int x = 0; x < rotatedShape[0].length; x++) {
                if (positionY + y - rotatedShape.length + 1 < 0) {
                    continue;
                }
                if (rotatedShape[y][x].equals(pixel) && frame[positionY + y - rotatedShape.length + 1][positionX + x].equals(pixel)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void checkFilledLines() {
        int i = 0;
        int pixelsInRow = 0;
        for (int y = 0; y < frameHeight; y++) {
            for (int x = 0; x < frameWidth; x++) {
                if (frame[y][x].equals(pixel)) {
                    pixelsInRow++;
                } else {
                    pixelsInRow = 0;
                    break;
                }
                if (pixelsInRow == frameWidth) {
                    pixelsInRow = 0;
                    i++;
                    removeLine(y);
                }
            }
        }
        switch (i) {
            case (1):
                gameScore += 100;
                break;
            case (2):
                gameScore += 300;
                break;
            case (3):
                gameScore += 500;
                break;
            case (4):
                gameScore += 800;
                break;
        }
    }

    public void removeLine(int y) {
        for (int i = y; i > 0; i--) {
            for (int j = 0; j < frameWidth; j++) {
                frame[i][j] = frame[i - 1][j];
            }
        }
        for (int k = 0; k < frameWidth; k++) {
            frame[0][k] = noPixel;
        }
    }

    public void rotateShape() {
        // rightSideIndex for case when the shape is getting out of right frameside due to rotation
        int rightSideIndex;
        rightSideIndex = frameWidth - positionX - shape.length;
        if (rightSideIndex < 0) {
            positionX += rightSideIndex;
        }

        String[][] rotatedShape = new String[shape[0].length][shape.length];
        for (int y = 0; y < shape[0].length; y++) {
            for (int x = 0; x < shape.length; x++) {
                rotatedShape[y][x] = shape[x][shape[0].length - 1 - y];
            }
        }
        shape = rotatedShape;
    }

    public void combineFrame() {
        for (int y = shape.length - 1; y >= 0; y--) {
            for (int x = 0; x < shape[0].length; x++) {
                if (shape[y][x].equals(pixel) && positionY + y - shape.length + 1 >= 0) {
                    frame[positionY + y - shape.length + 1][positionX + x] = shape[y][x];
                }
            }
        }
    }

    public void printFrame() {
        System.out.print("\033\143");
        int topIndex;
        if (positionY >= shape.length - 1) {
            topIndex = 0;
        } else {
            topIndex = (shape.length - 1) - positionY;
        }
        for (int y = 0; y < frameHeight; y++) {
            for (int x = 0; x < frameWidth; x++) {
                if (x == positionX && y == positionY + topIndex - shape.length + 1 && topIndex < shape.length) {
                    for (int k = 0; k < shape[0].length; k++) {
                        if (shape[topIndex][k].equals(pixel)) {
                            System.out.print(shape[topIndex][k]);
                        } else {
                            System.out.print(frame[positionY + topIndex - shape.length + 1][positionX + k]);
                        }
                    }
                    topIndex++;
                    x += shape[0].length - 1;
                } else {
                    System.out.print(frame[y][x]);
                }
            }
            System.out.println();
        }
        System.out.println("Score: " + gameScore);
        System.out.println();
        System.out.println("Be sure GAMEPAD window is active");

    }

    public boolean isSpaceLeft() {
        int topIndex;
        if (positionX == 0) {
            return false;
        }
        if (positionY >= shape.length - 1) {
            topIndex = 0;
        } else {
            topIndex = (shape.length - 1) - positionY;
        }
        for (int y = topIndex; y < shape.length; y++) {
            for (int x = 0; x < shape[0].length; x++) {
                if (shape[y][x].equals(pixel) && frame[positionY - shape.length + 1 + y][positionX + x - 1].equals(pixel)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isSpaceRight() {
        int topIndex;
        if (positionX == frameWidth - 1 - shape[0].length + 1) {
            return false;
        }
        if (positionY >= shape.length - 1) {
            topIndex = 0;
        } else {
            topIndex = (shape.length - 1) - positionY;
        }
        for (int y = topIndex; y < shape.length; y++) {
            for (int x = 0; x < shape[0].length; x++) {
                if (shape[y][x].equals(pixel) && frame[positionY - shape.length + 1 + y][positionX + x + 1].equals(pixel)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isSpaceBelow(int positionY) {
        int topIndex;
        if (positionY == frameHeight - 1) {
            return false;
        }
        if (positionY >= shape.length - 1) {
            topIndex = 0;
        } else {
            topIndex = (shape.length - 1) - positionY - 1;
        }
        for (int y = topIndex; y < shape.length; y++) {
            for (int x = 0; x < shape[0].length; x++) {
                if (shape[y][x].equals(pixel) && frame[positionY - shape.length + 2 + y][positionX + x].equals(pixel)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void printPause(String[][] frame) {
        int centerX = frameWidth / 2 - 3;
        int centerY = frameHeight / 2;
        System.out.print("\033\143");
        for (int i = 0; i < frame.length; i++) {
            for (int j = 0; j < frame[i].length; j++) {
                if (i == centerY && j == centerX) {
                    System.out.print("  ");
                } else if (i == centerY && j == centerX + 1) {
                    System.out.print("P ");
                } else if (i == centerY && j == centerX + 2) {
                    System.out.print("A ");
                } else if (i == centerY && j == centerX + 3) {
                    System.out.print("U ");
                } else if (i == centerY && j == centerX + 4) {
                    System.out.print("S ");
                } else if (i == centerY && j == centerX + 5) {
                    System.out.print("E ");
                } else {
                    System.out.print(frame[i][j]);
                }
            }
            System.out.println();
        }
        System.out.println("Score: " + gameScore);
    }

    public void KeyListener() {
        JFrame jFrame = new JFrame("GAMEPAD");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(150, 150);
        jFrame.getContentPane().setBackground(Color.BLACK);
        JLabel jLabel = new JLabel("", JLabel.CENTER);
        jLabel.setFont(new Font("Andale", Font.PLAIN, 30));
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
                if (e.getKeyCode() == 37 && !isGamePaused) {
                    action = "left";
                }
                if (e.getKeyCode() == 39 && !isGamePaused) {
                    action = "right";
                }
                if (e.getKeyCode() == 32 && !isGamePaused) {
                    action = "rotate";
                }
                if (e.getKeyCode() == 40 && !isGamePaused) {
                    action = "down";
                }
                if (e.getKeyCode() == 83 && isGamePaused) {
                    isGamePaused = false;
                } else if (e.getKeyCode() == 83 && !isGamePaused) {
                    isGamePaused = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                jLabel.setText(e.getKeyText(e.getKeyCode()));
                if (e.getKeyCode() == 37 && !isGamePaused && action.equals("left")) {
                    action = "";
                }
                if (e.getKeyCode() == 39 && !isGamePaused && action.equals("right")) {
                    action = "";
                }
                if (e.getKeyCode() == 40 && !isGamePaused && action.equals("down")) {
                    gameSpeed = 500;
                    action = "";
                }

            }
        });
    }

    public void action() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            waiting(gameSpeed / 10);
            // LEFT
            if (action.equals("left")) {
                if (isSpaceLeft()) {
                    positionX -= 1;
                }
                printFrame();
            }
            //RIGHT
            if (action.equals("right")) {
                if (isSpaceRight()) {
                    positionX += 1;
                }
                printFrame();
            }
            //ROTATE
            if (action.equals("rotate")) {
                if (isRotationAvailable()) {
                    rotateShape();
                }
                printFrame();
                action = "";
            }
            //DOWN
            if (action.equals("down")) {
                gameSpeed = 20;
            }
            // PAUSE/START
            boolean isPausePrinted = false;
            while (isGamePaused) {
                waiting(1);
                if (!isPausePrinted) {
                    printPause(frame);
//                    System.out.print("     P A U S E     ");
                    isPausePrinted = true;
                }
            }
            if (!isGamePaused && isPausePrinted) {
                printFrame();
            }
        }
    }

    public String[][] emptyFrame() {
        String[][] field = new String[frameHeight][frameWidth];
        for (int i = 0; i < frameHeight; i++) {
            for (int j = 0; j < frameWidth; j++) {
                field[i][j] = noPixel;
            }
        }
        return field;
    }

    public void waiting(int milisecond) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(milisecond);
    }

    public void randomShape() {
        int a = (int) (Math.random() * 7);
        /*   0     1     2    3     4     5     6     7       8       9       10      11     12    test
         *   ▓▓                                                                                    ▓▓▓▓▓▓
         *   ▓▓   ▓▓    ▓▓    ▓▓    ▓▓▓▓    ▓▓  ▓▓▓▓  ▓▓▓▓▓▓    ▓▓▓▓  ▓▓▓▓      ▓▓    ▓▓▓▓   ▓▓    ▓▓  ▓▓
         *   ▓▓   ▓▓▓▓  ▓▓    ▓▓▓▓  ▓▓▓▓  ▓▓▓▓    ▓▓    ▓▓      ▓▓      ▓▓    ▓▓▓▓▓▓  ▓▓     ▓▓▓▓      ▓▓
         *   ▓▓     ▓▓  ▓▓▓▓  ▓▓          ▓▓      ▓▓    ▓▓    ▓▓▓▓      ▓▓▓▓    ▓▓    ▓▓▓▓             ▓▓
         * */
        String[][] shape0 = {{pixel}, {pixel}, {pixel}, {pixel}};
        String[][] shape1 = {{pixel, noPixel}, {pixel, pixel}, {noPixel, pixel}};
        String[][] shape2 = {{noPixel, pixel}, {noPixel, pixel}, {pixel, pixel}};
        String[][] shape3 = {{pixel, noPixel}, {pixel, pixel}, {pixel, noPixel}};
        String[][] shape4 = {{pixel, pixel}, {pixel, pixel}};
        String[][] shape5 = {{noPixel, pixel}, {pixel, pixel}, {pixel, noPixel}};
        String[][] shape6 = {{pixel, pixel}, {noPixel, pixel}, {noPixel, pixel}};
        String[][] shape7 = {{pixel, pixel, pixel}, {noPixel, pixel, noPixel}, {noPixel, pixel, noPixel}};
        String[][] shape8 = {{noPixel, pixel, pixel}, {noPixel, pixel, noPixel}, {pixel, pixel, noPixel}};
        String[][] shape9 = {{pixel, pixel, noPixel}, {noPixel, pixel, noPixel}, {noPixel, pixel, pixel}};
        String[][] shape10 = {{noPixel, pixel, noPixel}, {pixel, pixel, pixel}, {noPixel, pixel, noPixel}};
        String[][] shape11 = {{pixel, pixel}, {pixel, noPixel}, {pixel, pixel}};
        String[][] shape12 = {{pixel, noPixel}, {pixel, pixel}};

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
            case 9:
                shape = shape9;
                break;
            case 10:
                shape = shape10;
                break;
            case 11:
                shape = shape11;
                break;
            case 12:
                shape = shape12;
                break;
            default:
                shape = shape1;
        }
        randomRotate();
    }

    public void randomRotate() {
        int b = (int) (Math.random() * 3);
        for (int k = 0; k <= b; k++) {
            String[][] rotatedShape = new String[shape[0].length][shape.length];
            for (int y = 0; y < shape[0].length; y++) {
                for (int x = 0; x < shape.length; x++) {
                    rotatedShape[y][x] = shape[x][shape[0].length - 1 - y];
                }
            }
            shape = rotatedShape;
        }
    }
}
