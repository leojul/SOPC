package sop;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MethodeExacte {
    public static void main(String[] args) {
        //String projectPath = System.getProperty("user.dir");
        String filenameInput = new File(args[0]).getAbsolutePath();
        String filenameSolution = new File(args[1]).getAbsolutePath();
        new MethodeExacteOrdonnancement(filenameInput, filenameSolution);
    }
}
