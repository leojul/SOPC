package sop;

import one.util.streamex.IntStreamEx;
import one.util.streamex.MoreCollectors;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BeamSearch {

    protected Parser parser;
    protected int k;
    protected int ub;
    protected Queue<BeamSearchState> queue = new ArrayDeque<>();
    protected BeamSearchState best_solution;

    public BeamSearch(Parser parser, int k) {
        this(parser, k, Integer.MAX_VALUE);
    }

    public BeamSearch(Parser parser, int k, int ub) {
        this.parser = parser;
        this.k = k;
        this.ub = ub;
    }

    private void enumererPuisFiltrerCandidats(BeamSearchState state) {
        // System.out.println(state);
        // System.out.println("new state");
        IntStream
                .range(0, parser.n)
                .filter(j -> !state.route.contains((short) j))
                .mapToObj(
                        j -> new Enumerate(
                                j,
                                parser.matrix[state.current()][j]
                        )
                ).filter(
                c -> IntStream
                        .range(0, parser.n)
                        .filter(j -> parser.matrix[c.i][j] == -1)
                        .allMatch(j -> state.route
                                .stream()
                                .anyMatch(e -> e == j)
                        )
        ).collect(MoreCollectors.least(k))
                .forEach(c -> {
                    // System.out.println(c);
                    List<Short> route = new ArrayList<>(state.route);
                    int solution = state.solution + c.v;
                    route.add((short) c.i);
                    queue.offer(new BeamSearchState(route, solution));
                });
        // System.out.println("end state");
    }

    public void appliquer() {
        List<Short> tmp = new ArrayList<>(parser.n);
        tmp.add((short) 0);
        queue.offer(new BeamSearchState(tmp, 0));
        while (!queue.isEmpty()) {
            BeamSearchState state = queue.poll();
            if (state.solution <= ub) {
                if (state.current() != parser.n - 1) enumererPuisFiltrerCandidats(state);
                else if (best_solution == null) best_solution = state;
                else best_solution = state.solution < best_solution.solution ? state : best_solution;
            }
        }
    }

    public void appliquer(int n) {


        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<?> f = service.submit((Runnable) this::appliquer);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                f.cancel(true);
            }
        });

        try {
            f.get(n, TimeUnit.SECONDS);     // attempt the task for two minutes
        } catch (final InterruptedException | CancellationException e) {
            // System.out.println("INTERRUPT");
            try {
                System.out.println(this);
                if (best_solution != null)
                    Checker.check(parser, best_solution.route.stream().mapToInt(Short::shortValue).toArray(), best_solution.solution);
            } catch (Parser.ConstrainedDisrespect constrainedDisrespect) {
                constrainedDisrespect.printStackTrace();
            }
            service.shutdown();
            System.exit(0);
            // The thread was interrupted during sleep, wait or join
        } catch (final TimeoutException e) {
            System.out.println("TIMEOUT");
            try {
                System.out.println(this);
                if (best_solution != null)
                    Checker.check(parser, best_solution.route.stream().mapToInt(Short::shortValue).toArray(), best_solution.solution);
            } catch (Parser.ConstrainedDisrespect constrainedDisrespect) {
                constrainedDisrespect.printStackTrace();
            }
            service.shutdown();
            System.exit(0);
            // Took too long!
        } catch (final ExecutionException e) {
            // An exception from within the Runnable task
        } finally {
            service.shutdown();
        }
        System.out.println("RESEARCH COMPLETE");
        try {
            System.out.println(this);
            if (best_solution != null)
                Checker.check(parser, best_solution.route.stream().mapToInt(Short::shortValue).toArray(), best_solution.solution);
        } catch (Parser.ConstrainedDisrespect constrainedDisrespect) {
            constrainedDisrespect.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return best_solution == null ?
                "Pas de Solution"
                : "Solution : " + best_solution.solution + "\n"
                + best_solution.route
                .stream()
                .map(e -> Short.toString(e))
                //.map(state -> new String(new char[]{(char) (state.i + 'a')}))
                .collect(Collectors.joining(" "));
    }

    public static void main(String[] args) {
        String filenameInput = new File(args[0]).getAbsolutePath();
        //String filenameSolution = new File(args[1]).getAbsolutePath();
        try {
            Parser parser = new Parser(filenameInput);
            BeamSearch beamSearch = new BeamSearch(parser, 2, 20720);
            beamSearch.appliquer(600);
            // try {
            //     System.out.println(beamSearch);
            //     if (beamSearch.best_solution != null)
            //         Checker.check(parser, beamSearch.best_solution.route.stream().mapToInt(Short::shortValue).toArray(), beamSearch.best_solution.solution);
            // } catch (Parser.ConstrainedDisrespect constrainedDisrespect) {
            //     constrainedDisrespect.printStackTrace();
            // }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
