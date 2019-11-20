/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sop;

import java.util.ArrayList;

/**
 *
 * @author diardjul
 */
public class Heuristique {
    public int n;
    public int[][] mDist;          //matrix des distances entre les sommets
    public int [][] mChemin;       //les sommet accessible de chaque sommet avec les contrainte de précedence
    public ArrayList<Integer> vecVerxtex;       // les sommet par lesquels on est pas passsé
    public ArrayList<Integer> sol;             // solution trouver

    public Heuristique( Parser parser) {
        n=parser.n;
        mDist=parser.matrix;
        mChemin=new int[n][n];
        sol=new ArrayList<>(n);
        vecVerxtex=new ArrayList<>();
        init();
    }
    
    public void resolutionH() throws NoSolution{
        int sommetChoisie=0;
        sol.add(sommetChoisie);
        updateMatrix(sommetChoisie);
        for (int i = 1; i < n; i++) {
            sommetChoisie=choix(sommetChoisie);           
            sol.add(sommetChoisie);
            updateMatrix(sommetChoisie);       } 
    }
    
    
    private int choix(int u) throws NoSolution {
        int sommet=0;
        ArrayList<Integer> sommetsPossible= new ArrayList<>();
        ArrayList <Integer> sommets=new ArrayList<>();
        ArrayList <Integer> vecVerxtexSauv=new ArrayList<>();
        ensembleMin(sommetsPossible,u);
        copie(vecVerxtexSauv, vecVerxtex);
        copie(sommets, sommetsPossible);
        valide(sommetsPossible);   
        
        while(sommetsPossible.isEmpty()&&(!vecVerxtex.isEmpty())){
            vecVerxtex.removeAll(sommets);
            ensembleMin(sommetsPossible,u);
            copie(sommets, sommetsPossible);
            valide(sommetsPossible);   
            
        }
        if(vecVerxtex.isEmpty()){
            throw new NoSolution("we didn't have solution");
        }else{
            copie(vecVerxtex, vecVerxtexSauv);
            sommet=(int) sommetsPossible.get(0);
        }
        
        return sommet;
    }
    
    private void updateMatrix(Integer u) {
        for (int i = 0; i < n; i++) {
            mChemin[i][u]=0;
        }
        vecVerxtex.remove(u);
    }
    
    private ArrayList<Integer> ensembleMin(ArrayList <Integer> sommetsPossible, int u) {
        int i=0;
        int min=vecVerxtex.get(i);
        while(mChemin[u][min]==0){
            i++;
            min=vecVerxtex.get(i);
        }
        for (Integer v: vecVerxtex) {
            if ((mChemin[u][v]!=0) && (mDist[u][v]<= mDist[u][min]) ){
                min=v;  
            }
        }
        sommetsPossible.add(min);
        for (Integer v: vecVerxtex) {
            if (mDist[u][v]== mDist[u][min] && v!=min)
                sommetsPossible.add(v);
        }
        return sommetsPossible;
    }
    
    private ArrayList valide(ArrayList<Integer> sommetsPossible) {
        ArrayList<Integer> sommetsValid=new ArrayList<>(sommetsPossible.size());
        copie(sommetsValid, sommetsPossible);
        for (Integer v: sommetsPossible) {
            for (int i = 0; i < n; i++) {
                if(v!=i){
                    if ((mChemin[v][i]==0) && (vecVerxtex.contains(i))){
                        sommetsValid.remove(v);
                    }
                }
            }
        }
        sommetsPossible.removeAll(sommetsPossible);
        copie(sommetsPossible, sommetsValid);
        return sommetsValid;
    }        
        
        
    private String toString(int[][] m) {
        String s = "";
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s=s+" "+m[i][j];
            }
            s+="\n";
        }
        return s;
    }

    private void init() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if ((mDist[i][j]>=0) && (i!=j)) {           //si il existe une arrete et que l'arrete ne vas pas sur lui même on ajoute un 1 sinon un 0
                    mChemin[i][j]=1;
                }else{
                    mChemin[i][j]=0;
                }
            }
            vecVerxtex.add(i);
            
        }
    }

    private void copie(ArrayList<Integer> cible, ArrayList<Integer> original) {
        cible.removeAll(cible);
        for (Integer i : original) {
            cible.add(i);
        }
    }

    private static class NoSolution extends Exception {

        public NoSolution(String message) {
            System.out.println(message);
        }
    }







}
