package sop;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MethodeExacte {
    public static void main(String[] args) {
        //String projectPath = System.getProperty("user.dir");
        try {
            String filenameInput = new File(args[0]).getCanonicalPath();
            String filenameSolution = new File(args[1]).getCanonicalPath();
            new MethodeExacteMIP(filenameInput, filenameSolution);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
