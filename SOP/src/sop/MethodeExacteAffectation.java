package sop;

import ilog.concert.*;
import ilog.cp.IloCP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MethodeExacteAffectation {
    /**
     * A reference to the CPO solver
     **/
    protected IloCP solver;
    protected Parser parser;

    /**
     * Variables of the model.
     **/
    protected IloIntVar[][] boolVars;

    public MethodeExacteAffectation(String filenameInput) {
        this(filenameInput, "");
    }

    public MethodeExacteAffectation(String filenameInput, String filenameSolution) {
        try {
            parser = new Parser(filenameInput);
            System.out.println(parser);
            System.out.println("NORMAL:");
            System.out.println("MÉTHODE EXACTE:");
            stateModel();
            int[] sol = solve();
            if (sol != null) {
                System.out.println("------------------------------------------------------------");
                System.out.println("Input Matrix :");
                System.out.println(parser);
                parser.checker(Arrays
                        .stream(sol)
                        .mapToObj(Integer::new)
                        .collect(Collectors
                                .toCollection(ArrayList::new)
                        )
                );
            }
        } catch (IOException | Parser.ConstrainedDisrespect ex) {
            System.out.println("ALERT : Solution Incorrecte !");
        }
    }

    private IloIntExpr[] getLine(int i) {
        return boolVars[i];
    }

    private IloIntExpr[] getColumn(final int j) {
        return IntStream.range(0, parser.n).mapToObj(i -> boolVars[i][j]).toArray(IloIntExpr[]::new);
    }

    private void stateModel() {
        try {

            // 1. Create a Solver
            solver = new IloCP();

            // 2. Create the variables
            IloIntVar [] successors = new IloIntVar[parser.n - 1];

            for (int i = 0; i < successors.length; i++)
                successors[i] = solver.intVar(1, parser.n - 1);

            IloIntVar [] positionSuccessors = new IloIntVar[parser.n - 1];

            for (int i = 0; i < parser.n; i++)
                for (int j = 0; j < parser.n; j++)
                    boolVars[i][j] = solver.boolVar();

            // 3. Post the constraints

            // Origin Constraint

            solver.add(
                    solver.eq(
                            solver.sum(
                                    solver.sum(getColumn(0)),
                                    solver.prod(-1,
                                            solver.sum(getLine(0))
                                    )
                            ), -1
                    )
            );

            // Destination Constraint

            solver.add(
                    solver.eq(
                            solver.sum(
                                    solver.sum(getColumn(parser.n - 1)),
                                    solver.prod(-1,
                                            solver.sum(getLine(parser.n - 1))
                                    )
                            ), 1
                    )
            );

            // Entering Edges Constraints

            for (int j = 1; j < parser.n; j++) {
                IloLinearIntExpr expr = solver.linearIntExpr();
                for (int i = 0; i < parser.n; i++) {
                    if (i != j) {
                        expr.addTerm(1, boolVars[i][j]);
                    }
                }
                solver.addEq(expr, 1);
            }

            // Leaving Edges Constraints

            for (int i = 0; i < parser.n - 1; i++) {
                IloLinearIntExpr expr = solver.linearIntExpr();
                for (int j = 0; j < parser.n; j++) {
                    if (i != j) {
                        expr.addTerm(1, boolVars[i][j]);
                    }
                }
                solver.addEq(expr, 1);
            }

            // Precedence Constraints

            for (int i = 1; i < parser.n; i++) {
                final int ind = i;
                int[] precedences = IntStream
                        .range(1, parser.n)
                        .map(j -> parser.matrix[ind][j] == -1 ? j : -1)
                        .filter(j -> j != -1)
                        .toArray();
                for (int j = 0; j < precedences.length; j++)
                    for (int k = 0; k < parser.n; k++)
                        if (k != j)
                            solver.add(solver.le(boolVars[ind][precedences[j]], boolVars[k][j]));

            }

            // Subtours elimination with MTZ formulation

            IloNumVar[] u = solver.numVarArray(parser.n, 0, Double.MAX_VALUE);
            for (int i = 1; i < parser.n; i++) {
                for (int j = 1; j < parser.n; j++) {
                    if (i != j) {
                        IloLinearNumExpr expr = solver.linearNumExpr();
                        expr.addTerm(1d, u[i]);
                        expr.addTerm(-1d, u[j]);
                        expr.addTerm(parser.n - 1, boolVars[i][j]);
                        solver.addLe(expr, parser.n - 2);
                    }
                }
            }

            // Objective

            IloLinearIntExpr objectif = solver.linearIntExpr();
            for (int i = 0; i < parser.n; i++) {
                for (int j = 0; j < parser.n; j++) {
                    objectif.addTerm(parser.matrix[i][j], boolVars[i][j]);
                }
            }
            solver.addMinimize(objectif);

        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    public int[] solve() {
        try {
            if (solver.solve()) {
                int[] sol = new int[parser.n];
                sol[0] = 0;
                System.out.println("------------------------------------------------------------");
                System.out.println("Objective value : " + (int) solver.getObjValue());
                StringBuffer route = new StringBuffer("0");
                for (int i = 0, k = 1; i < parser.n - 1; k++) {
                    int j = 0;
                    while (j < parser.n && (int) solver.getValue(boolVars[i][j]) != 1) j++;
                    sol[k] = j;
                    route.append(" " + j);
                    i = j;
                }
                System.out.println(route);
                String[] s = route.toString().split(" ");
                System.out.println(
                        Arrays
                                .stream(s)
                                .map(Integer::parseInt)
                                .map(i -> new String(new char[]{(char) (i + 'a')}))
                                .collect(Collectors.joining(" "))
                );
                System.out.println("------------------------------------------------------------");
                System.out.println("Decision Variables :");
                for (IloIntVar[] line : boolVars) {
                    int[] vars = new int[parser.n];
                    for (int i = 0; i < parser.n; i++) {
                        vars[i] = (int) solver.getValue(line[i]);
                    }
                    System.out.println(Arrays.toString(vars));
                }
                return sol;
            } else System.out.println("Pas de Solution");
        } catch (IloException e) {
            e.printStackTrace();
        }
        solver.end();
        return null;
    }

}

