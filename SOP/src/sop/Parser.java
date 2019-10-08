/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sop;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author diardjul
 */
public class Parser {
    int n;
    int[][] matrix;

    Parser(String arg) {
        reader(arg);
    }
    
    public void reader(String nameFile){
        BufferedReader buff;
        String line;
        try {
            buff= new BufferedReader(new FileReader(nameFile));
            line  = buff.readLine();
            n = Integer.parseInt(line);
            matrix = new int[n][n];
            int i = 0;
            while ((line = buff.readLine())!= null){
                String [] tabS = line.split("\\s+");
                for (int j = 0; j < n; j++) {
                    matrix[i][j] = Integer.parseInt(tabS[j]);
                }
                i++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s=s+" "+matrix[i][j];
            }
            s+="\n";
        }
        return s;
    }
    
    
}
