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
import java.util.ArrayList;
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
    int[] tabSol;

    Parser(String arg) {
        reader(arg);
    }
    Parser(String arg,int i){
        readerSol(arg);
    }
    
    public void reader(String nameFile){
        BufferedReader buff;
        String line;
        try {
            buff= new BufferedReader(new FileReader(nameFile));
            line  = buff.readLine();
            this.n = Integer.parseInt(line);
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
    
    public void readerSol(String solFile){
        BufferedReader buff;
        String line;
        try {
            buff= new BufferedReader(new FileReader(solFile));
            line= buff.readLine();
            String [] tabS = line.split(" ");
            tabSol= new int[tabS.length];
            for (int i = 0; i < tabS.length; i++) {
                tabSol[i]=Integer.parseInt(tabS[i]);
            }
            for (int i = 0; i < tabS.length; i++) {
                System.out.print(tabSol[i]+" ");
            }
            System.out.println("");
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void checker() throws ConstrainedDisrespect{
       int result=0;
       int [] noDoublon =new int [tabSol.length];   //tableau pour vérifier que l'on ne passe pas plusieur fois par un même sommet
       if(tabSol.length!=n){
           throw new ConstrainedDisrespect("the lenght of the solution should be equal to the number of vertices");
       }
        for (int i = 0; i < tabSol.length; i++) {
            noDoublon[i]=i;
        }
        for (int i = 0; i < tabSol.length-1; i++) {
            if(matrix[tabSol[i]][tabSol[i+1]]==-1){
                result=-1;
                throw new ConstrainedDisrespect("contraint precedence");
            }else if (noDoublon[tabSol[i]]==-1){
                throw new ConstrainedDisrespect("do not go through the same vertice more than once");
            }else{
                result+=matrix[tabSol[i]][tabSol[i+1]];
            }
            noDoublon[tabSol[i]]=-1;
        }
        System.out.println("result= "+result);
    }
    
    public void checker(ArrayList<Integer> sol) throws ConstrainedDisrespect{
        int[] tab = new int[n];
        for (int i = 0; i < n; i++) {
            tab[i]=sol.get(i);
        }
       int result=0;
       int [] noDoublon =new int [tab.length];   //tableau pour vérifier que l'on ne passe pas plusieur fois par un même sommet
       if(tab.length!=n){
           throw new ConstrainedDisrespect("the lenght of the solution should be equal to the number of vertices");
       }
        for (int i = 0; i < tab.length; i++) {
            noDoublon[i]=i;
        }
        for (int i = 0; i < tab.length-1; i++) {
            if(matrix[tab[i]][tab[i+1]]==-1){
                result=-1;
                throw new ConstrainedDisrespect("contraint precedence");
            }else if (noDoublon[tab[i]]==-1){
                throw new ConstrainedDisrespect("do not go through the same vertice more than once");
            }else{
                result+=matrix[tab[i]][tab[i+1]];
            }
            noDoublon[tab[i]]=-1;
        }
        System.out.println("result heuristique= "+result);
    }

    private static class ConstrainedDisrespect extends Exception {

        public ConstrainedDisrespect(String message) {
            System.out.println("Constrained Disrespect " + message);
        }
    }
    
}
