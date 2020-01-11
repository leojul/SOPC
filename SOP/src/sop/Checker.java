package sop;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Checker {
    public static void check(Parser parser, int[] route, int solution) throws Parser.ConstrainedDisrespect {
        int resultat = 0;
        int[] visites = IntStream.generate(() -> -1).limit(parser.n).toArray();
        if (route[0] != 0) {
            throw new Parser.ConstrainedDisrespect("Le chemin ne commence pas par 0.");
        }
        visites[0] = 0;
        if (route[parser.n - 1] != parser.n - 1) {
            throw new Parser.ConstrainedDisrespect("Le chemin ne finit pas par " + parser.n);
        }
        for (int i = 1; i < parser.n; i++) {
            final int ind = i;
            if (visites[route[i]] > 0)
                throw new Parser.ConstrainedDisrespect("Le sommet " + route[i] + " d'indice " + i
                        + "a déja été traversé à l'indice " + visites[route[i]] + ".");
            visites[route[i]] = i;
            int[] prec = IntStream
                    .range(0, parser.n)
                    .filter(j -> parser.matrix[route[ind]][j] == -1)
                    .toArray();
            for (int j : prec) {
                int k = 0;
                for (; k < i && route[k] != j; k++) ;
                if (k == i)
                    throw new Parser.ConstrainedDisrespect("La contrainte de précédence "
                            + j + " < " + route[i] + " n'a pas été respectée.");
            }
            resultat += parser.matrix[route[i - 1]][route[i]];
        }
        if (resultat != solution)
            throw new Parser.ConstrainedDisrespect("Somme des arêtes incorrecte :\nResultat : "
                    + resultat + "\nSolution : " + solution);
    }
}
