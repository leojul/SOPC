/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sop;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author diardjul
 */
public class Parser {

    private int[][] matrix;
    private int[] solutions;

    Parser(String fileName) throws IOException {
        readInput(fileName);
    }

    Parser(String fileName,int i) throws IOException {
        readSolution(fileName);
    }
    
    public void readInput(String fileName) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))){
            String line  = bufferedReader.readLine();
            int n = Integer.parseInt(line);
            this.matrix = new int[n][n];
            int i = 0;
            while ((line = bufferedReader.readLine()) != null){
                String [] strings = line.split("\\s+");
                for (int j = 0; j < n; j++) {
                    matrix[i][j] = Integer.parseInt(strings[j]);
                }
                i++;
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int[] line : matrix) {
            Arrays.stream(line).forEachOrdered(stringBuffer::append);
            stringBuffer.append('\n');
        }
        return stringBuffer.toString();
    }
    
    public void readSolution(String fileName) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            String line = bufferedReader.readLine();
            String[] strings = line.split(" ");
            solutions = Arrays.stream(strings).mapToInt(Integer::parseInt).toArray();
            Arrays.stream(strings).forEachOrdered(e -> System.out.print(e + ' '));
            System.out.println();
        }
    }
    
    public void checker() throws ConstrainedDisrespect{
       int result=0;
       int [] noDoublon = new int [solutions.length];   //tableau pour vérifier que l'on ne passe pas plusieur fois par un même sommet
       if(solutions.length != matrix.length){
           throw new ConstrainedDisrespect("the lenght of the solution should be equal to the number of vertices");
       }
        for (int i = 0; i < solutions.length; i++) {
            noDoublon[i]=i;
        }
        for (int i = 0; i < solutions.length-1; i++) {
            if(matrix[solutions[i]][solutions[i+1]]==-1){
                result=-1;
                throw new ConstrainedDisrespect("contraint precedence");
            }else if (noDoublon[solutions[i]]==-1){
                throw new ConstrainedDisrespect("do not go through the same vertice more than once");
            }else{
                result+=matrix[solutions[i]][solutions[i+1]];
            }
            noDoublon[solutions[i]]=-1;
        }
        System.out.println("result= "+result);
    }

    private static class ConstrainedDisrespect extends Exception {
        public ConstrainedDisrespect(String message) {
            System.out.println("Constrained Disrespect " + message);
        }
    }
    
}
