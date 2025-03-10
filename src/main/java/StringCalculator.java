import java.util.regex.Pattern;

public class StringCalculator {
    public String[] checkDelimiter(String targetString) {
        String delimiter;
        String[] numbers;
        if (targetString.matches("^[0-9, ]+$")) {
            delimiter = ",";
        }
        else if (targetString.matches("^[0-9:]+$")) {
            delimiter = ":";
        }
        else {
            int checkStart = targetString.indexOf("//");
            int checkEnd = targetString.indexOf("\n");
            if (checkStart != -1 && checkEnd != -1 && checkStart <= checkEnd) {
                delimiter = targetString.substring(checkStart + 2, checkEnd);
                if (! targetString.substring(checkEnd + 1).matches("^[0-9" + Pattern.quote(delimiter) + "]+$")) {
                    throw new RuntimeException();
                }
                targetString = targetString.substring(checkEnd + 1);
            }
            else {
                throw new RuntimeException();
            }
        }
        numbers = targetString.split(delimiter);
        return numbers;
    }

    public int add(String[] numbers) {
        int sum = 0;
        for (String i : numbers) {
            try {
                sum += Integer.valueOf(i);
            }
            catch (NumberFormatException e) {
                continue;
            }
        }
        return sum;
    }
}
