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

    public String AverAndCountLine() {
        return "Amount of trans," + "Average Duration" + "\n" + parser.amountOfTransactions() + "," +
                parser.averageDuration() + "\n";
    }

    public String rangesDuration() {
        StringBuilder stringBuilder = new StringBuilder();
        parser.durationRanges().forEach((k, v) -> stringBuilder.append(k).append(","));
        stringBuilder.append("\n");
        parser.durationRanges().forEach((k, v) -> stringBuilder.append(v).append(","));
        return stringBuilder.toString();
    }

    public void writeData(Path outputFolder) throws IOException {
//        Path outputFileName = Paths.get("\\reports\\"+Input.getDate() + "_meta4_perfreport.csv");
//        Path absolute = Paths.get(parser.getAbsolutePath().getParent().toString() + "\\reports\\"+Input.getDate() + "_meta4_perfreport.csv");
        String date = "\\";
        if (parser.getAbsolutePath().getFileName().toString().length() > 10) {
            date = "\\" + parser.getAbsolutePath().getFileName().toString().substring(0, 10);
        }
        Path absolute = Paths.get(outputFolder.toString() + date + "_meta4_perfreport.csv");

        if (Files.notExists(absolute)) {
            Files.createFile(absolute);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(absolute)) {
            writer.write("sep=,\n");
            writer.write(this.AverAndCountLine());
            writer.write(this.rangesDuration());
        }
    }
}
