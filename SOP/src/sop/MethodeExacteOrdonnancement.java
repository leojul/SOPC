package sop;

import ilog.concert.*;
import ilog.cp.IloCP;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MethodeExacteOrdonnancement {

    protected IloCP solver;
    protected Parser parser;

    /**
     * Variables of the model.
     **/

    IloIntervalVar[][] tasks;

    /**
     * Constructor
     **/
    public MethodeExacteOrdonnancement(String filenameInput) {
        this(filenameInput, "");
    }

    public MethodeExacteOrdonnancement(String filenameInput, String filenameSolution) {
        try {
            parser = new Parser(filenameInput, filenameSolution);
            System.out.println(parser);
            System.out.println("NORMAL:");
            parser.checker();
            System.out.println("MÉTHODE EXACTE:");
            stateModel();
        } catch (IOException | Parser.ConstrainedDisrespect ex) {
            Logger.getLogger(SOP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the CPO solver so automatic tests can be performed on it WARNING:
     * this method is required by the caseine activity
     **/
    public IloCP getSolver() {
        return solver;
    }

    private void stateModel() {
        try {

            // 1. Create a Solver
            solver = new IloCP();

            // 2. Create the variables
            int[] durations = IntStream
                    .range(0, parser.n * parser.n)
                    .filter(i -> parser.matrix[i / parser.n][i % parser.n] != -1)
                    .toArray();

            int[] prec = IntStream
                    .range(0, parser.n * parser.n)
                    .filter(i -> parser.matrix[i / parser.n][i % parser.n] == -1)
                    .toArray();

            System.out.println(Arrays
                    .stream(prec)
                    .mapToObj(Integer::toString)
                    .collect(Collectors.joining(" ")));

            tasks = new IloIntervalVar[parser.n][parser.n];
            IloIntExpr[][] ends = new IloIntExpr[parser.n][parser.n];
            for (int i = 0; i < parser.n; i++)
                for (int j = 0; j < parser.n; j++) {
                    if (i != j) {
                        IloIntervalVar t = solver.intervalVar(parser.matrix[i][j]);
                        if (parser.matrix[i][j] != -1) {
                            tasks[i][j] = t;
                            ends[i][j] = solver.endOf(t);
                        } else
                            tasks[i][j] = null;
                    }
                }

            for (int i = 0; i < parser.n; i++)
                for (int j = 0; j < parser.n; j++)
                    if (i != j && tasks[i][j] == null)
                        for (int k = 0; k < parser.n; k++) {
                            if (parser.matrix[i][k] > 0) {
                                solver.add(solver.endBeforeStart(tasks[j][i], tasks[i][k]));
                            }
                        }

            solver.add(solver.minimize(solver.min(Arrays.stream(ends).flatMap(Arrays::stream).toArray(IloIntExpr[]::new))));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String display(IloIntervalVar var, IloCP solver) {
        return var.getName() + "[" + (int) solver.getStart(var) + ", "
                + (int) solver.getEnd(var) + "]";
    }

    /**
     * This method allows the use of next() for enumerating all the solutions
     * <p>
     * WARNING: this method is required by the caseine activity
     */
    public void solve() {
        try {
            int[][] sol = null;
            //System.out.println("Cmax = " + solver.getObjValue());
            if (solver.solve()) {
                sol = new int[parser.n][parser.n];
                for (int i = 0; i < parser.n; ++i) {
                    for (int j = 0; j < parser.n; ++j) {
                        if (i != j && tasks[i][j] != null)
                            sol[i][j] = (int) solver.getStartMin(tasks[i][j]);
                    }
                }
            } else {
                System.out.println("Pas de Solution");
            }
        } catch (IloException e) {
            e.printStackTrace();
        }
    }
}

