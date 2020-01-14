package sop;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Grasp {

    int best_solution = 100000;
    int[] best_route;

    protected Parser parser;
    protected Stack<State> route;
    protected Random random = new Random();
    protected int solution;
    protected int k;
    protected int n;

    public Grasp(Parser parser, int k, int n) {
        this.parser = parser;
        this.k = k;
        this.n = n;
        this.route = new Stack<>();
    }

    private Stream<Enumerate> enumererCandidats(State state) {
        return IntStream
                .range(0, parser.n)
                .filter(j -> state.visites[j] == 0)
                .mapToObj(j -> new Enumerate(j, parser.matrix[state.i][j]));
    }

    private Optional<Enumerate> filtrerCandidats(Stream<Enumerate> candidats) {
        Enumerate[] cand = candidats.filter(c -> IntStream
                .range(0, parser.n)
                .filter(j -> parser.matrix[c.i][j] == -1)
                .allMatch(j -> route.contains(new State(j))))
                .sorted()
                .toArray(Enumerate[]::new);
        if (cand.length > 0) {
            int rand = random.nextInt(Math.min(k, cand.length));
            return Optional.of(cand[rand]);
        } else {
            return Optional.empty();
        }
    }

    void appliquer() {
        for (int i = 0; i < n; i++) {
            solution = 0;
            route.clear();
            randomiserGlouton();
            System.out.println("Solution générée n°" + (i + 1) + " : " + this.solution);
            if (this.solution < best_solution) {
                best_solution = solution;
                best_route = route.stream().mapToInt(e -> e.i).toArray();
            }
        }
    }

    void randomiserGlouton() {
        route.push(new State(0, new int[parser.n]));
        while (!route.empty() && route.size() != parser.n) {
            State state = route.peek();
            if (state.i != parser.n - 1) {
                state.visites[state.i] = 1;
                //System.out.println(Arrays.toString(state.visites));
                Optional<Enumerate> optionalSuivant = filtrerCandidats(enumererCandidats(state));
                if (optionalSuivant.isPresent()) {
                    Enumerate suivant = optionalSuivant.get();
                    solution += suivant.v;
                    route.push(new State(
                            suivant.i,
                            Arrays.stream(Arrays.copyOf(state.visites, parser.n))
                                    .map(v -> v == -1 ? 0 : v)
                                    .toArray()
                    ));
                } else {
                    //System.out.println("BACKTRACK");
                    if (!route.empty()) {
                        route.pop();
                        solution -= parser.matrix[route.peek().i][state.i];
                        route.peek().visites[state.i] = -1;
                    }
                }
            } else {
                //System.out.println("BACKTRACK");
                route.pop();
                solution -= parser.matrix[route.peek().i][state.i];
                route.peek().visites[state.i] = -1;
            }
        }
        if (route.empty()) {
            solution = 0;
        }
    }

    @Override
    public String toString() {
        return best_route == null ?
                "Pas de Solution"
                : "Solution : " + best_solution + "\n"
                + Arrays.stream(best_route)
                .mapToObj(Integer::toString)
                //.map(state -> new String(new char[]{(char) (state.i + 'a')}))
                .collect(Collectors.joining(" "));
    }

    public static void main(String[] args) {
        String filenameInput = new File(args[0]).getAbsolutePath();
        //String filenameSolution = new File(args[1]).getAbsolutePath();
        try {
            Parser parser = new Parser(filenameInput);
            Grasp grasp = new Grasp(parser, 1, 1);
            grasp.appliquer();
            System.out.println(grasp);
            try {
                if (grasp.best_route != null)
                    Checker.check(parser, grasp.best_route, grasp.best_solution);
                // Checker.check(
                //         parser,
                //         new int[] {0, 1, 4, 12, 3, 2, 10, 7, 5, 6, 15, 14, 8, 9, 42, 18, 11, 65, 17, 22, 19, 13, 23, 38, 43, 39, 67, 25, 60, 33, 16, 26, 29, 20, 21, 32, 61, 47, 27, 30, 28, 34, 56, 24, 36, 31, 48, 35, 51, 57, 40, 37, 45, 59, 44, 66, 74, 53, 73, 41, 49, 68, 52, 71, 50, 54, 55, 75, 62, 63, 58, 46, 64, 77, 76, 70, 72, 78, 69, 79},
                //         20720
                // );

                // System.out.println(parser.checker(new ArrayList<Integer>(
                //         Arrays.<Integer>asList(
                //                 glouton.route.stream()
                //                         .map(e -> e.i)
                //                         .toArray(Integer[]::new)
                //         ))
                // ));
                // System.out.println(parser.checker(new ArrayList<Integer>(
                //         Arrays.<Integer>asList(
                //                 Arrays.stream(parser.solutions)
                //                         .boxed()
                //                         .toArray(Integer[]::new)
                //         ))
                // ));
            } catch (Parser.ConstrainedDisrespect constrainedDisrespect) {
                constrainedDisrespect.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
