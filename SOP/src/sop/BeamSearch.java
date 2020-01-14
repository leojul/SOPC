package sop;

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
    protected Queue<BeamSearchState> queue;
    protected BeamSearchState best_solution;

    public BeamSearch(Parser parser, int k) {
        this.parser = parser;
        this.k = k;
        this.queue = new LinkedList<>();
    }

    private void enumererPuisFiltrerCandidats(BeamSearchState state) {
        // System.out.println(state);
        IntStream
                .range(0, parser.n)
                .filter(j -> state.visites[j] == 0)
                .mapToObj(
                        j -> new Enumerate(
                                j,
                                parser.matrix[state.current()][j]
                        )
                ).filter(
                c -> IntStream
                        .range(0, parser.n)
                        .filter(j -> parser.matrix[c.i][j] == -1)
                        .allMatch(j -> Arrays
                                .stream(state.route)
                                .anyMatch(e -> e == j)
                        )
        ).sorted()
                .limit(k)
                .forEachOrdered(c -> {
                    // System.out.println(c);
                    int[] route = Arrays.copyOf(state.route, parser.n);
                    int[] visites = Arrays
                            .stream(Arrays.copyOf(state.visites, parser.n))
                            .map(v -> v == -1 ? 0 : v)
                            .toArray();
                    int solution = state.solution + c.v;
                    route[state.nbs] = c.i;
                    int nbs = state.nbs + 1;
                    queue.offer(new BeamSearchState(nbs, route, visites, solution));
                });
    }

    public void appliquer() {
        queue.offer(new BeamSearchState(1, new int[parser.n], new int[parser.n], 0));
        while (!queue.isEmpty()) {
            BeamSearchState state = queue.poll();
            if (state.current() != parser.n - 1) {
                state.visites[state.current()] = 1;
                // System.out.println(Arrays.toString(state.visites));
                enumererPuisFiltrerCandidats(state);
            } else if (state.nbs != parser.n) {
                // System.out.println("BACKTRACK");
                state.solution -= parser.matrix[state.current() - 1][state.current()];
                state.visites[state.current()] = -1;
                --state.nbs;
                queue.offer(state);
            } else if (best_solution == null) best_solution = state;
            else best_solution = state.solution < best_solution.solution ? state : best_solution;
        }
    }

    public void appliquer(int n) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        try {

            Future<?> f = service.submit((Runnable) this::appliquer);
            f.get(n, TimeUnit.SECONDS);     // attempt the task for two minutes

        }
        catch (final InterruptedException e) {
            // The thread was interrupted during sleep, wait or join
        }
        catch (final TimeoutException e) {
            try {
                System.out.println(this);
                if (best_solution != null)
                    Checker.check(parser, best_solution.route, best_solution.solution);
            } catch (Parser.ConstrainedDisrespect constrainedDisrespect) {
                constrainedDisrespect.printStackTrace();
            }
            service.shutdown();
            System.exit(0);
            // Took too long!
        }
        catch (final ExecutionException e) {
            // An exception from within the Runnable task
        }
        finally {
            service.shutdown();
        }

        try {
            System.out.println(this);
            if (best_solution != null)
                Checker.check(parser, best_solution.route, best_solution.solution);
        } catch (Parser.ConstrainedDisrespect constrainedDisrespect) {
            constrainedDisrespect.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return best_solution == null ?
                "Pas de Solution"
                : "Solution : " + best_solution.solution + "\n"
                + Arrays.stream(best_solution.route)
                .mapToObj(Integer::toString)
                //.map(state -> new String(new char[]{(char) (state.i + 'a')}))
                .collect(Collectors.joining(" "));
    }

    public static void main(String[] args) {
        String filenameInput = new File(args[0]).getAbsolutePath();
        //String filenameSolution = new File(args[1]).getAbsolutePath();
        try {
            Parser parser = new Parser(filenameInput);
            BeamSearch beamSearch = new BeamSearch(parser, 2);
            beamSearch.appliquer(30);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
