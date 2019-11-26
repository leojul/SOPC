/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sop;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author diardjul
 */
public class SOP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        Parser parser = new Parser(args[0]);
        parser.readerSolution(args[1]);
        System.out.println("NORMAL:");
       try {
            parser.checker();
        } catch (Exception ex) {
            Logger.getLogger(SOP.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("HEURISTIQUE:");
       Heuristique H = new Heuristique(parser);
        System.out.println("solution heuristique:");
        try {
            H.resolutionH();
        } catch (Exception ex) {
            Logger.getLogger(SOP.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < H.n; i++) {
            System.out.print(H.sol.get(i));
            System.out.print(" ");
        }
        System.out.println("");
        try {
            parser.checker(H.sol);
        } catch (Exception ex) {
            Logger.getLogger(SOP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
