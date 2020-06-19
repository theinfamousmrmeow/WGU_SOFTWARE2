package databaseFrontend;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class logger {

    static String logPath ="log.txt";


    public static void appendToLog(String appendedString){

        DateTimeFormatter dtformat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss ");

        if (!Files.exists(Paths.get(logPath))){
            //Files.createFile(logPath);
        }

        try (FileWriter fw = new FileWriter(logPath,true)) {
           BufferedWriter bw = new BufferedWriter(fw);
           bw.write(dtformat.format(LocalDateTime.now()) + appendedString);
           bw.newLine();
           bw.close();
        }
        catch (Exception e){
            e.printStackTrace();
            System.err.println("Failed to write to Login-Log.");
        }
    }

}
