package sop;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class Enumerate {
    protected int i;
    protected int v;

    public Enumerate(int i, int v) {
        this.i = i;
        this.v = v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Enumerate enumerate = (Enumerate) o;

        return i == enumerate.i;
    }

}

class State {
    protected int i;
    protected int[] visites;

    public State(int i, int[] visites) {
        this.i = i;
        this.visites = visites;
    }

    public State(int i) {
        this.i = i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;

        State state = (State) o;

        return i == state.i;
    }
}

public class Glouton {

    protected Parser parser;
    protected Stack<State> route;
    protected int solution;

    public Glouton(Parser parser) {
        this.parser = parser;
        this.route = new Stack<>();
    }

    private Stream<Enumerate> enumererCandidats(State state) {
        return IntStream
                .range(0, parser.n)
                .filter(j -> state.visites[j] == 0)
                .mapToObj(j -> new Enumerate(j, parser.matrix[state.i][j]));
    }

    private Optional<Enumerate> filtrerCandidats(Stream<Enumerate> candidats) {
        return candidats.filter(c -> IntStream
                .range(0, parser.n)
                .filter(j -> parser.matrix[c.i][j] == -1)
                .allMatch(j -> route.contains(new State(j))))
                .min((t1, t2) -> Integer.compare(t1.v, t2.v));
    }

    void appliquerGlouton() {
        route.push(new State(0, new int[parser.n]));
        while (!route.empty() && route.size() != parser.n) {
            State state = route.peek();
            if (state.i != parser.n - 1) {
                state.visites[state.i] = 1;
                System.out.println(Arrays.toString(state.visites));
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
                    System.out.println("BACKTRACK");
                    if (!route.empty()) {
                        route.pop();
                        solution -= parser.matrix[route.peek().i][state.i];
                        route.peek().visites[state.i] = -1;
                    }
                }
            } else {
                System.out.println("BACKTRACK");
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
        return solution == 0 ?
                "Pas de Solution"
                : "Solution : " + solution + "\n"
                + route.stream()
                .map(state -> Integer.toString(state.i))
                //.map(state -> new String(new char[]{(char) (state.i + 'a')}))
                .collect(Collectors.joining(" "));
    }

    public static void main(String[] args) {
        String filenameInput = new File(args[0]).getAbsolutePath();
        //String filenameSolution = new File(args[1]).getAbsolutePath();
        try {
            Parser parser = new Parser(filenameInput);
            Glouton glouton = new Glouton(parser);
            glouton.appliquerGlouton();
            System.out.println(glouton);
            try {
                Checker.check(parser, glouton.route.stream().mapToInt(e -> e.i).toArray(), glouton.solution);
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
