import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private final ArrayList<Double> durations = new ArrayList<>(20000);
    private final ArrayList<Integer> period3SecQuantity = new ArrayList<>(28000);
    private final Path logFullPath;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);


    public Parser() throws IOException, ParseException {
        this.logFullPath = Input.fullPathForLog();
    }

    public Path getAbsolutePath() {
        return logFullPath;
    }

    public ArrayList<Double> getDurations() {
        return durations;
    }

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        boolean marker = true;
        Path outputFolder = Input.pathForOutputFolder();
        while (marker) {
            Parser parser = new Parser();
            try {
                parser.fillDurations(parser.getAbsolutePath());
            } catch (IllegalArgumentException e) {
                Thread.sleep(10000);
                break;
            }
            Output output = new Output(parser);
            output.writeData(outputFolder);
            System.out.println("Log is parsed. Check output folder");
            System.out.println("Do you want to parse another log?");
            System.out.println("Enter \"Yes\" or \"No\"");
            while (true) {
                String yesOrNo = Input.requestToEnterString().toLowerCase(Locale.ROOT);
                if (yesOrNo.equals("no")) {
                    System.out.println("Bye bye!");
                    Thread.sleep(1000);
                    marker = false;
                    break;
                } else if (yesOrNo.equals("yes")) {
                    System.out.println("Sure thing bro. Output folder will be the same");
                    break;
                } else System.out.println("What?! Did you just failed to enter Yes or No? Try one more time");
            }
        }
    }
    private void fillDurations(Path pathForFile) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(pathForFile)))) {
            String line = reader.readLine();
            //Initiate with log's date in long with 1 sec starting time. It will be used to separate orders by 1 sec periods
            long trailingDate = dateFormat.parse(line.split(" ")[0] + " 00:00:01.000").getTime();
            int ordersCountIn3Sec = 0;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Duration")) {
                    ordersCountIn3Sec++;
                    String[] split = line.split(" ");
                    String parseDate = split[0].replace('T', ' ');
                    Date current = dateFormat.parse(parseDate);
                    if(current.getTime() > trailingDate) {
                        period3SecQuantity.add(ordersCountIn3Sec - 1);
                        ordersCountIn3Sec = 1;
                        long count = (current.getTime() - trailingDate) / 1000;
                        for (int i = 0; i <= count; i++) {
                            trailingDate += 1000;
                        }
                    }
                    durations.add(getDurationValue(line));
                }
            }
            if (ordersCountIn3Sec != 0) period3SecQuantity.add(ordersCountIn3Sec);
        } catch (IOException e) {
            System.out.println("Issue with reading of log file. Check the access abd rerun the parser");
        } catch (Exception e) {
            System.out.println("Something with log's format. It can't be parsed");
            throw new IllegalArgumentException();
        }
    }

    private Double getDurationValue(String source) {
        String[] testo = null;
        Matcher matcher = Pattern.compile("(Duration.*)").matcher(source);
        if (matcher.find()) testo = matcher.group(1).split(" ");
        Double duration = null;
        try {
            assert testo != null;
            duration = Double.valueOf(testo[2]);
        } catch (Exception e) {
            System.out.println("Something wrong with log's format");
        }
        return duration;
    }

    public int amountOfTransactions() {
        return (int) getDurations().stream().filter(x -> x > 0).count();
    }

    public double averageDuration() {
        double average = 0;
        if (!getDurations().isEmpty()) {
            average = getDurations().stream().mapToDouble(Double::doubleValue).filter(x -> x > 0).average().getAsDouble();
        }
        return average;
    }

    public LinkedHashMap<String, Integer> durationRanges() {
        LinkedHashMap<String, Integer> ranges = new LinkedHashMap<>();
        ranges.put("0 - 50ms", (int) getDurations().stream().filter(x -> x > 0 && x <= 50).count());
        ranges.put("50 - 100ms", (int) getDurations().stream().filter(x -> x > 50 && x <= 100).count());
        ranges.put("100 - 250ms", (int) getDurations().stream().filter(x -> x > 100 && x <= 250).count());
        ranges.put("250 - 350ms", (int) getDurations().stream().filter(x -> x > 250 && x <= 350).count());
        ranges.put("350 - 500ms", (int) getDurations().stream().filter(x -> x > 350 && x <= 500).count());
        ranges.put("500ms - 1s", (int) getDurations().stream().filter(x -> x > 500 && x <= 1000).count());
        ranges.put("1 - 2s", (int) getDurations().stream().filter(x -> x > 1000 && x <= 2000).count());
        ranges.put("2 - 3s", (int) getDurations().stream().filter(x -> x > 2000 && x <= 3000).count());
        ranges.put("3 - 4s", (int) getDurations().stream().filter(x -> x > 3000 && x <= 4000).count());
        ranges.put("4 - 5s", (int) getDurations().stream().filter(x -> x > 4000 && x <= 5000).count());
        ranges.put("5s - 60", (int) getDurations().stream().filter(x -> x > 5000 && x < 60000).count());
        ranges.put(">60", (int) getDurations().stream().filter(x -> x >= 60000).count());
        return ranges;
    }


    public LinkedHashMap<String, String[]> period3SecAnalysis () {
        LinkedHashMap<String, String[]> analysis = new LinkedHashMap<>();
        ArrayList<Long> counting = new ArrayList<>();
        counting.add(period3SecQuantity.stream().filter(x -> x >= 200 && x <= 400).count());
        counting.add(period3SecQuantity.stream().filter(x -> x > 400 && x <= 800).count());
        counting.add(period3SecQuantity.stream().filter(x -> x > 800 && x <= 1000).count());
        counting.add(period3SecQuantity.stream().filter(x -> x > 1000).count());

        //86400 - quantity of seconds for a whole day
        int percent = 86400 / 100;
        analysis.put("200 - 400", new String[] {counting.get(0).toString(), String.format("%.4f", (double)counting.get(0) / percent) });
        analysis.put("400 - 800", new String[] {counting.get(1).toString(), String.format("%.4f", (double)counting.get(1) / percent) });
        analysis.put("800 - 1000", new String[] {counting.get(2).toString(), String.format("%.4f", (double)counting.get(2) / percent) });
        analysis.put(">1000", new String[] {counting.get(3).toString(), String.format("%.4f", (double)counting.get(3) / percent) });
        return analysis;
    }
}