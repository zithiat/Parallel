import java.util.Random;

public class ConsoleProgress {

    private static String CURSOR_STRING = "0%.......10%.......20%.......30%.......40%.......50%.......60%.......70%.......80%.......90%.....100%";

    private static final double MAX_STEP = CURSOR_STRING.length() - 1;

    private double max;
    private double step;
    private double cursor;
    private double lastCursor;

    public static void main(String[] args) throws InterruptedException {
        // ---------------------------------------------------------------------------------
        int max = new Random().nextInt(400) + 1;
        // ---------------------------------------------------------------------------------
        // Example of use :
        // ---------------------------------------------------------------------------------
        ConsoleProgress progress = new ConsoleProgress("Progress (" + max + ") : ", max);
        for (int i = 1; i <= max; i++, progress.nextProgress()) {
            Thread.sleep(3L); // a task with no prints
        }
    }

    public ConsoleProgress(String title, int maxCounts) {
        cursor = 0.;
        max = maxCounts;
        step = MAX_STEP / max;
        System.out.print(title);
        printCursor();
        nextProgress();
    }

    public void nextProgress() {
        printCursor();
        cursor += step;
    }

    private void printCursor() {
        int intCursor = (int) Math.round(cursor) + 1;
        System.out.print(CURSOR_STRING.substring((int) lastCursor, intCursor));
        if (lastCursor != intCursor && intCursor == CURSOR_STRING.length())
            System.out.println(); // final print
        lastCursor = intCursor;
    }
}