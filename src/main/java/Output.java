import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Output {
    private final Parser parser;

    public Output(Parser parser) {
        this.parser = parser;
    }

    private String AverAndCountLine() {
        return "Amount of trans," + "Average Duration," + "Median Duration" + "\n" + parser.amountOfTransactions() + "," +
                parser.averageDuration() + "," + parser.getMedian() + "\n";
    }

    private String rangesDuration() {
        StringBuilder stringBuilder = new StringBuilder();
        parser.durationRanges().forEach((k, v) -> stringBuilder.append(k).append(","));
        stringBuilder.append("\n");
        parser.durationRanges().forEach((k, v) -> stringBuilder.append(v).append(","));
        return stringBuilder.toString();
    }

    private String string3SecPeriod() {
        StringBuilder stringBuilder = new StringBuilder();
        parser.periodSecAnalysis().forEach(
                (k,v) -> stringBuilder.append(k).append(" count").append(",").append("Day Burstiness Score ").append(k).append(",")
        );
        stringBuilder.append("\n");
        parser.periodSecAnalysis().forEach(
                (k,v) -> stringBuilder.append(v[0]).append(",").append(v[1]).append(",")
        );
        return stringBuilder.toString();
    }

    private String over55SecDurationsAndId() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("requestID,Duration\n");
        parser.getTimeOutIdAndDuration().forEach(
                x -> stringBuilder.append(x).append("\n")
        );
        return stringBuilder.toString();
    }

    public void writeData(Path outputFolder) throws IOException {
        String date = "\\";
        //Now you know why you shouldn't use the magic number? The whole code is awful...
        if (parser.getAbsolutePath().getFileName().toString().length() > 10) {
            date = "\\" + parser.getAbsolutePath().getFileName().toString().substring(0, 10);
        }
        Path filePath = Paths.get(outputFolder.toString() + date + "_meta4_perfreport.csv");

        int count = 1;
        while (Files.exists(filePath)) {
            filePath = Paths.get(outputFolder.toString() + date + "_meta4_perfreport_"+count+".csv");
            count++;
        }
        Files.createFile(filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("sep=,\n");
            writer.write(this.AverAndCountLine());
            writer.write(this.rangesDuration());
            writer.write("\n");
            writer.write(this.string3SecPeriod());
            writer.write("\n");
            writer.write(over55SecDurationsAndId());
        }
    }
}
