package sop;

import ilog.concert.*;
import ilog.concert.cppimpl.IloIntervalVarArray;
import ilog.cp.IloCP;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ordonnancement {

    protected IloCP solver;
    protected Parser parser;

    /**
     * Variables of the model.
     **/

    IloIntervalVar[][] tasks;

    public Ordonnancement(Parser parser) {
        this.parser = parser;
    }

    /**
     * Constructor
     **/

    public static void main(String[] args) {

        String filenameInput = new File(args[0]).getAbsolutePath();
        //String filenameSolution = new File(args[1]).getAbsolutePath();

        try {
            Parser parser = new Parser(filenameInput);
            new Ordonnancement(parser).stateModel();
            //Checker.check(parser, new int[]{0, 2, 3, 6, 10, 7, 8, 11, 4, 5, 1, 9, 12}, 2106);
        } catch (IOException e) {
            e.printStackTrace();
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
            IloIntervalVar[] tasks = new IloIntervalVar[parser.n];
            for (int i = 0; i < parser.n; i++) tasks[i] = solver.intervalVar(0, "" + i);
            IloIntervalSequenceVar seq = solver.intervalSequenceVar(tasks,
                    IntStream
                    .iterate(0, i -> i + 1)
                    .limit(parser.n)
                    .toArray());
            int[][] dtable = Arrays
                    .stream(parser.matrix)
                    .map(line -> Arrays
                            .stream(line)
                            .map(v -> v == -1 ? parser.matrix[0][parser.n - 1] : v)
                            .toArray()
                    ).toArray(int[][]::new);
            IloTransitionDistance tdist = solver.transitionDistance(dtable);

            Arrays.stream(dtable).map(Arrays::toString).forEachOrdered(System.out::println);

            solver.add(solver.noOverlap(seq));
            // 2. Create the precedence contraints

            for (int i = 0; i < parser.n; i++) {
                for (int j = 0; j < parser.n; j++) {
                    if (parser.matrix[i][j] == -1) {
                        solver.add(solver.endBeforeStart(tasks[j], tasks[i]));
                    }
                }
            }

            int[] m = Arrays.stream(Arrays.copyOf(dtable, parser.n - 1)).flatMapToInt(Arrays::stream).toArray();

            IloIntExpr[] costs = new IloIntExpr[parser.n - 1];
            for (int i = 0; i < parser.n - 1; i++) {
                costs[i] = solver.element(m, solver.prod(i, solver.typeOfNext(seq, tasks[i], 0)));
            }

            // 3. Create the objective
            solver.addMinimize(solver.sum(costs));
            //solver.add(solver.minimize(solver.max(ends)));
            // 4. Solve
            if (solver.solve()) {
                System.out.println("Cmax = " + solver.getObjValue());
                for (int i = 0; i < tasks.length; i++) {
                    System.out.println(display(tasks[i], solver));
                }
            } else {
                System.out.print("No solution found");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String display(IloIntervalVar var, IloCP solver) {
        return var.getName() + " : [" + (int) solver.getStart(var) + ", "
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

