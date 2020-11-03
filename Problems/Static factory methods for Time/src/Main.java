import java.util.Scanner;

class Time {
    private static final long DAYS_IN_SECONDS = 60 * 60 * 24;
    private static final int HOURS_IN_SECONDS = 60 * 60;

    int hour;
    int minute;
    int second;

    Time(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public static Time noon() {
        // write your code here
        return new Time(12, 0, 0);
    }

    public static Time midnight() {
        // write your code here
        return new Time(0, 0, 0);
    }

    public static Time ofSeconds(long seconds) {
        // write your code here
        long actualSeconds = seconds;
        if (actualSeconds >= DAYS_IN_SECONDS) {
            actualSeconds = actualSeconds % DAYS_IN_SECONDS;
        }
        int hours = (int) actualSeconds / HOURS_IN_SECONDS;
        int remainingSeconds = (int) actualSeconds % HOURS_IN_SECONDS;
        int minutes = remainingSeconds / 60;
        int s = remainingSeconds % 60;
        return new Time(hours, minutes, s);
    }

    private static boolean isBetween(int number, int lower, int upper) {
        return number < upper && number >= lower;
    }

    public static Time of(int hour, int minute, int second) {
        // write your code here
        if (isBetween(hour, 0, 24) && isBetween(minute, 0, 60) && isBetween(second, 0, 60)) {
            return new Time(hour, minute, second);
        } else {
            return null;
        }
    }
}

/* Do not change code below */
public class Main {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);

        final String type = scanner.next();
        Time time = null;

        switch (type) {
            case "noon":
                time = Time.noon();
                break;
            case "midnight":
                time = Time.midnight();
                break;
            case "hms":
                int h = scanner.nextInt();
                int m = scanner.nextInt();
                int s = scanner.nextInt();
                time = Time.of(h, m, s);
                break;
            case "seconds":
                time = Time.ofSeconds(scanner.nextInt());
                break;
            default:
                time = null;
                break;
        }

        if (time == null) {
            System.out.println(time);
        } else {
            System.out.println(String.format("%s %s %s", time.hour, time.minute, time.second));
        }
    }
}
