package sop;

import ilog.concert.*;
import ilog.cp.IloCP;
import ilog.cplex.IloCplex;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MIP {
    /**
     * A reference to the CPO solver
     **/
    protected IloCP solver;
    protected Parser parser;
    protected int[] route;

    /**
     * Variables of the model.
     **/
    protected IloIntVar[][] aretesVars;
    protected IloIntVar[][] precVars;
    protected IloIntVar[] indexVars;

    public MIP(Parser parser) {
        this.parser = parser;
    }

    public static void main(String[] args) {
        String filenameInput = new File(args[0]).getAbsolutePath();
        //String filenameSolution = new File(args[1]).getAbsolutePath();

        try {
            Parser parser = new Parser(filenameInput);
            Checker.check(parser, new int[] {0, 2, 3, 6, 10, 7, 8, 11, 4, 5, 1, 9, 12}, 2106);
            // MIP mip = new MIP(parser);
            // mip.stateModel();
            // mip.solve();
        } catch (IOException | Parser.ConstrainedDisrespect e) {
            e.printStackTrace();
        }
    }


    // private IloIntExpr[] getLine(int i) {
    //     return aretesVars[i];
    // }

    // private IloIntExpr[] getColumn(final int j) {
    //     return IntStream.range(0, parser.n).mapToObj(i -> aretesVars[i][j]).toArray(IloIntExpr[]::new);
    // }

    private void stateModel() {
        try {

            // 1. Create a Solver
            solver = new IloCP();

            // 2. Create the variables

            //aretesVars = new IloIntVar[parser.n][parser.n];
            precVars = new IloIntVar[parser.n][parser.n];
            //indexVars = new IloIntVar[parser.n];
            for (int i = 0; i < parser.n; i++) {
                for (int j = 0; j < parser.n; j++) {
                    precVars[i][j] = solver.boolVar();
                    //aretesVars[i][j] = solver.boolVar();
                }
                //indexVars[i] = solver.intVar(0, parser.n - 1);
            }

            // 3. Post the constraints

            // Origin Constraint

            // solver.add(
            //         solver.eq(
            //                 solver.sum(
            //                         solver.sum(getColumn(0)),
            //                         solver.prod(-1,
            //                                 solver.sum(getLine(0))
            //                         )
            //                 ), -1
            //         )
            // );

            // // Destination Constraint

            // solver.add(
            //         solver.eq(
            //                 solver.sum(
            //                         solver.sum(getColumn(parser.n - 1)),
            //                         solver.prod(-1,
            //                                 solver.sum(getLine(parser.n - 1))
            //                         )
            //                 ), 1
            //         )
            // );

            // // Entering Edges Constraints

            // for (int j = 1; j < parser.n; j++) {
            //     IloLinearIntExpr expr = solver.linearIntExpr();
            //     for (int i = 0; i < parser.n; i++) {
            //         if (i != j) {
            //             expr.addTerm(1, aretesVars[i][j]);
            //         }
            //     }
            //     solver.addEq(expr, 1);
            // }

            // // Leaving Edges Constraints

            // for (int i = 0; i < parser.n - 1; i++) {
            //     IloLinearIntExpr expr = solver.linearIntExpr();
            //     for (int j = 0; j < parser.n; j++) {
            //         if (i != j) {
            //             expr.addTerm(1, aretesVars[i][j]);
            //         }
            //     }
            //     solver.addEq(expr, 1);
            // }

            // Precedence Constraints

            int[] positions = IntStream
                    .iterate(0, i -> i + 1)
                    .limit(parser.n)
                    .toArray();

            solver.add(solver.eq(precVars[0][0], 1));
            solver.add(solver.eq(precVars[parser.n - 1][parser.n - 1], 1));

            // for (int i = 0; i < parser.n-1; i++) {
            //     for (int j = 0; j < parser.n; j++) {
            //         solver.add(solver.le(aretesVars[i][j], precVars[i][i]));
            //         solver.add(solver.le(aretesVars[i][j], precVars[i+1][j]));
            //     }
            // }

            // for (int i = 0; i < parser.n; i++)
            //     solver.add(solver.eq(indexVars[i], solver.scalProd(positions, precVars[i])));


            for (int i = 0; i < parser.n; i++) {
                IloIntExpr line = solver.intExpr();
                for (int j = 0; j < parser.n; j++) {
                    line = solver.sum(line, precVars[i][j]);
                }
                solver.add(solver.eq(line, 1));
            }

            for (int i = 0; i < parser.n; i++) {
                IloIntExpr col = solver.intExpr();
                for (int j = 0; j < parser.n; j++) {
                    col = solver.sum(col, precVars[j][i]);
                }
                solver.add(solver.eq(col, 1));
            }

            for (int i = 1; i < parser.n - 1; i++) {
                final int ind = i;
                int[] precedences = IntStream
                        .range(0, parser.n)
                        .filter(j -> parser.matrix[ind][j] == -1)
                        .toArray();
                System.out.println(Arrays.toString(precedences));
                IloIntExpr inf = solver.intExpr();
                for (int j = 0; j < precedences.length; j++) {
                    //solver.add(solver.eq(aretesVars[ind][precedences[j]], 0));
                    inf = solver.sum(inf, solver.scalProd(positions, precVars[precedences[j]]));
                }
                IloIntExpr pos = solver.scalProd(positions, precVars[i]);
                solver.add(solver.ge(pos, solver.sum(inf, 1)));
                solver.add(solver.le(pos, parser.n - 1));
            }

            // for (int i = 1; i < parser.n; i++) {
            //     for (int j = 0; j < parser.n; j++) {
            //         for (int k = 0; k < parser.n; k++) {
            //             if (j != k) {
            //                 solver.add(solver.le(aretesVars[j][k], precVars[i-1][j]));
            //                 solver.add(solver.le(aretesVars[j][k], precVars[i][k]));
            //                 solver.add(
            //                         solver.le(
            //                                 solver.sum(
            //                                         solver.sum(
            //                                                 precVars[i-1][j],
            //                                                 precVars[i][k]),
            //                                         -1),
            //                                 aretesVars[j][k]
            //                         )
            //                 );
            //             }
            //         }
            //     }
            // }

            // Subtours elimination with MTZ formulation

            // IloNumVar[] u = solver.numVarArray(parser.n, 0, Double.MAX_VALUE);
            // for (int i = 1; i < parser.n; i++) {
            //     for (int j = 1; j < parser.n; j++) {
            //         if (i != j) {
            //             IloLinearNumExpr expr = solver.linearNumExpr();
            //             expr.addTerm(1d, u[i]);
            //             expr.addTerm(-1d, u[j]);
            //             expr.addTerm(parser.n - 1, aretesVars[i][j]);
            //             solver.addLe(expr, parser.n - 2);
            //         }
            //     }
            // }

            // Objective

            // IloLinearIntExpr objective = solver.linearIntExpr();
            // for (int i = 0; i < parser.n; i++)
            //     for (int j = 0; j < parser.n; j++)
            //         objective.addTerm(parser.matrix[i][j], aretesVars[i][j]);
            // solver.addMinimize(objective);

            int[] m = Arrays
                    .stream(parser.matrix)
                    .flatMapToInt(Arrays::stream)
                    .toArray();

            IloIntExpr objective = solver.intExpr();
            for (int i = 1; i < parser.n; i++)
                objective = solver.sum(
                        objective,
                        solver.element(
                                m,
                                solver.sum(
                                        solver.prod(
                                                parser.n,
                                                solver.scalProd(positions, precVars[i - 1])
                                        ),
                                        solver.scalProd(positions, precVars[i])
                                )
                        )
                );
            solver.addMinimize(objective);

        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    public void solve() {
        try {
            if (solver.solve()) {
                route = new int[parser.n];
                System.out.println("------------------------------------------------------------");
                System.out.println("Solution : " + (int) solver.getObjValue());
                // for (int i = 0, k = 1; i < parser.n - 1; k++) {
                //     int j = 0;
                //     while (j < parser.n && (int) solver.getValue(aretesVars[i][j]) != 1) j++;
                //     route[k] = j;
                //     i = j;
                // }
                for (int i = 0; i < parser.n; i++)
                    for (int j = 0; j < parser.n; j++)
                        if (solver.getValue(precVars[i][j]) == 1)
                            route[i] = j;
                System.out.println(Arrays.toString(route));
                // System.out.println("------------------------------------------------------------");
                // System.out.println("Edge Variables :");
                // for (IloIntVar[] line : aretesVars) {
                //     int[] vars = new int[parser.n];
                //     for (int i = 0; i < parser.n; i++) {
                //         vars[i] = (int) solver.getValue(line[i]);
                //     }
                //     System.out.println(Arrays.toString(vars));
                // }
                System.out.println("------------------------------------------------------------");
                System.out.println("Precedence Variables :");
                for (IloIntVar[] line : precVars) {
                    int[] vars = new int[parser.n];
                    for (int i = 0; i < parser.n; i++) {
                        vars[i] = (int) solver.getValue(line[i]);
                    }
                    System.out.println(Arrays.toString(vars));
                }
            } else System.out.println("Pas de Solution");
        } catch (IloException e) {
            e.printStackTrace();
        }
        solver.end();
    }

}

