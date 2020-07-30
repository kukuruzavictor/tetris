package tetris;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        int height = 20;
        int width = 10;
        int speed = 2000;

        // patterns ░░ ▒▒ ▓▓ ▉▋ ▉▊ ▉▉
        String pixel = "▉▋";

        String[][] frame = emptyField(width, height);
        while (true) {
            frame = fallingRender(frame, shape());
        }
    }

    public static String[][] fallingRender(String[][] frame, String[][] shape) throws InterruptedException {
        int centerOfFrame = frame[1].length / 2;
        int centerOfShape = shape[1].length / 2;
        int center = centerOfFrame - centerOfShape;
        boolean test = true;
        for (int frameRow = 0; frameRow < frame.length; frameRow++) {
            if (isSpaceBelow(frame, shape, frameRow, center)) {
                for (int f = 0; f < shape[1].length; f++) {
                    // (IF) to exclude filling of existing frame-pixel ▓▓ by empty shape-pixel ░░
                    if (shape[shape.length - 1][f].equals("▓▓")) {
                        frame[frameRow][center + f] = shape[shape.length - 1][f];
                    }
                }
                for (int w = 0; w < shape.length - 1; w++) {
                    if (frameRow > w) {
                        for (int f = 0; f < shape[1].length; f++) {
                            // (IF) to exclude filling of existing frame-pixel (or shape-pixel) ▓▓ by empty shape-pixel ░░
                            if (shape[shape.length - 2 - w][f].equals("▓▓") || (shape[shape.length - 2 - w][f].equals("░░") && shape[shape.length - 1 - w][f].equals("▓▓"))) {
                                frame[frameRow - 1 - w][center + f] = shape[shape.length - 2 - w][f];
                            }
                        }
                    }
                }
                // filling of empty row above the shape
                if (frameRow > shape.length - 1) {
                    for (int f = 0; f < shape[1].length; f++) {
                        frame[frameRow - shape.length][center + f] = "░░";
                    }
                }
                printFrame(frame);
            } else {
                break;
            }
        }
        return frame;
    }

    public static boolean isSpaceBelow(String[][] frame, String[][] shape, int frameRow, int center) {
        int k = 0;
        if (frameRow == frame.length) {
            return false;
        }
        for (int i = 0; i < shape[1].length; i++) {
            for (int j = 0; j < shape.length; j++) {
                if (shape[shape.length - 1 - j][i].equals("░░")) {
                    k++;
                } else {
                    break;
                }
            }
            if (k > frameRow) {
                k = frameRow;
            }
            if (frame[frameRow - k][center + i].equals("▓▓")) {
                return false;
            }
            k = 0;
        }
        return true;
    }

    public static void printFrame(String[][] frame) throws InterruptedException {
        System.out.print("\033\143");
        for (int i = 0; i < frame.length; i++) {
            for (int j = 0; j < frame[i].length; j++) {
                System.out.print(frame[i][j]);
            }
            System.out.println();
        }
        TimeUnit.MILLISECONDS.sleep(50);
    }


    public static String[][] shape() {

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

    public static String[][] emptyField(int width, int height) {
        String[][] field = new String[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                field[i][j] = "░░";
            }
        }
        return field;
    }

    // NOT WORKS. ALGORITHM IS WRONG.
    public static boolean isGroundPlaceOLD(String[][] frame, String[][] shape, int frameRow, int center) {
        int cycle;
        if (frameRow == frame.length) {
            return false;
        }
        if (frameRow < shape.length) {
            cycle = frameRow + 1;
        } else {
            cycle = shape.length;
        }

        for (int i = 0; i < cycle; i++) {
            for (int j = 0; j < shape[1].length; j++) {
                if (shape[shape.length - 1 - i][j].equals("▓▓") && frame[frameRow - i][center + j].equals("▓▓")) {
                    return false;
                }
            }
        }
        return true;
    }

    // NOT WORKS. ALGORITHM IS WRONG.
    public static int checkGround(String[][] frame, String[][] shape, int frameRow, int center) {
        int distance = 20;
        for (int i = 0; i < shape[1].length; i++) {
            int sh = 0;
            int fr = 0;
            for (int j = 0; j < shape.length - 1; j++) {
                //shape distance
                if (shape[shape.length - 1 - j][i].equals("░░")) {
                    sh += 1;
                }
            }
            for (int k = frameRow; k < frame.length; k++) {
                // frame distance
                if (frame[k][center + i].equals("░░")) {
                    fr += 1;
                } else {
                    break;
                }
            }
            if (fr + sh < distance) {
                distance = sh + fr;
            }
        }
        return distance;
    }
}
