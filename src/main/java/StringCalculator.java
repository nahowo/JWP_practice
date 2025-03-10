import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    public int add(String targetString) {
        if (targetString.matches("^[ ]+$")) {
            return 0;
        }
        return sum(splitString(targetString));
    }

    private String[] splitString(String targetString) {
        Matcher matcher = Pattern.compile("//(.)\n(.*)").matcher(targetString);
        if (matcher.find()) {
            return matcher.group(2).split(matcher.group(1));
        }
        return targetString.split(",|:");
    }

    private int sum(String[] numbers) {
        int sum = 0;
        int tmp;
        for (String i : numbers) {
            tmp = Integer.valueOf(i);
            if (tmp < 0) {
                throw new RuntimeException();
            }
            sum += tmp;
        }
        return sum;
    }
}
