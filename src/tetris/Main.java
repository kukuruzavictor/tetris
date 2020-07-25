package tetris;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        int height = 20;
        int width = 10;
        int speed = 750;

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
        for (int s = 0; s < frame.length; s++) {
            for (int f = 0; f < shape[1].length; f++) {
                frame[s][center + f] = shape[shape[1].length - 1][f];
            }
            for (int w = 0; w < shape.length - 1; w++) {
                if (s > w) {
                    for (int f = 0; f < shape[1].length; f++) {
                        frame[s - 1 - w][center + f] = shape[shape[1].length - 2 - w][f];
                    }
                }
            }
            // фігура пройшла весь шлях, далі лишається порожній рядок
            if (s > shape.length - 1) {
                for (int f = 0; f < shape[1].length; f++) {
                    frame[s - shape.length][center + f] = "░░";
                }
            }
            printFrame(frame);
        }
        return frame;
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
        int a = (int) (Math.random() * 7);
        String[][] shape;

        /*   1     2     0     3     4     5     6
         *               ▓▓
         *   ▓▓    ▓▓▓▓  ▓▓    ▓▓    ▓▓▓▓    ▓▓  ▓▓▓▓
         *   ▓▓▓▓  ▓▓    ▓▓    ▓▓▓▓  ▓▓▓▓  ▓▓▓▓    ▓▓
         *     ▓▓  ▓▓    ▓▓    ▓▓          ▓▓      ▓▓
         * */
        String[][] shape0 = {{"░░", "▓▓", "░░", "░░"}, {"░░", "▓▓", "░░", "░░"}, {"░░", "▓▓", "░░", "░░"}, {"░░", "▓▓", "░░", "░░"}};
        String[][] shape1 = {{"▓▓", "░░", "░░"}, {"▓▓", "▓▓", "░░"}, {"░░", "▓▓", "░░"}};
        String[][] shape2 = {{"▓▓", "▓▓", "░░"}, {"▓▓", "░░", "░░"}, {"▓▓", "░░", "░░"}};
        String[][] shape3 = {{"▓▓", "░░", "░░"}, {"▓▓", "▓▓", "░░"}, {"▓▓", "░░", "░░"}};
        String[][] shape4 = {{"▓▓", "▓▓"}, {"▓▓", "▓▓"}};
        String[][] shape5 = {{"░░", "░░", "▓▓"}, {"░░", "▓▓", "▓▓"}, {"░░", "▓▓", "░░"}};
        String[][] shape6 = {{"░░", "▓▓", "▓▓"}, {"░░", "░░", "▓▓"}, {"░░", "░░", "▓▓"}};

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
            default:
                shape = new String[][]{{"░░", "▓▓", "░░", "░░"}, {"░░", "▓▓", "░░", "░░"}, {"░░", "▓▓", "░░", "░░"}, {"░░", "▓▓", "░░", "░░"}};
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
}
