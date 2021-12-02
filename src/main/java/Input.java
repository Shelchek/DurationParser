import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Input {
    private Input() {
    }

    private static String date;
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static String requestToEnterString() throws IOException {
        date = reader.readLine();
        return date;
    }

    private static String requestForOutputFolder() throws IOException {
        System.out.println("Enter the path for folder where to store the result of parsing");
        return reader.readLine();
    }

    //Метод для создания пути чисто по дате
    public static Path PathForLog() throws IOException {
        Path parent = Paths.get("C:\\Users\\Robot\\Desktop\\Finstek\\temp\\");
        Path fileName = Paths.get(requestToEnterString() + "_meta4_transaction.log");
        Path absolute = parent.resolve(fileName);
        while (Files.notExists(absolute)) {
            if (Files.notExists(absolute)) {
                System.out.println("No such log is found. Check the date and try one more time");
                fileName = Paths.get(requestToEnterString() + "_meta4_transaction.log");
                absolute = parent.resolve(fileName);
            }
        }
        return absolute;
    }

    public static String getDate() {
        return date;
    }

    public static Path fullPathForLog() throws IOException {
        Path fullName = null;
        while (fullName == null) {
            try {
                System.out.println("Enter the full path to log file");
                fullName = Paths.get(requestToEnterString());
            } catch (InvalidPathException e) {
                System.out.println("Something went wrong. Try to enter one more time");
            }
        }
        if (Files.isDirectory(fullName) || Files.notExists(fullName)) {
            while (Files.isDirectory(fullName) || Files.notExists(fullName)) {
                System.out.println("No such log file is found. Check the entered path and try one more time");
                try {
                    fullName = Paths.get(requestToEnterString());
                } catch (InvalidPathException e) {
                    System.out.println("Something went wrong. Try to enter one more time");
                }
            }
        }
        return fullName;
    }

    public static Path pathForOutputFolder() throws IOException {
        Path folderPath = null;
        while (folderPath == null) {
            try {
                folderPath = Paths.get(requestForOutputFolder());
            } catch (InvalidPathException e) {
                System.out.println("Something went wrong. Try to enter one more time");
            }
        }
        if (Files.notExists(folderPath) || Files.isRegularFile(folderPath)) {
            while (Files.notExists(folderPath) || Files.isRegularFile(folderPath)) {
                System.out.println("No such folder is found. Check the entered path and try one more time");
                try {
                    folderPath = Paths.get(requestForOutputFolder());
                } catch (InvalidPathException e) {
                    System.out.println("Something went wrong. Try to enter one more time");
                }
            }
        }
        return folderPath;
    }
}
