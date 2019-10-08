/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sop;

/**
 *
 * @author diardjul
 */
public class SOP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Parser parser = new Parser(args[0]);
        parser.readerSol(args[1]);
        parser.checker();
        //System.out.println(parser);
    }
    
}
