package sop;

import java.util.Arrays;

public class BeamSearchState {

    protected int nbs;
    protected int[] route;
    protected int[] visites;
    int solution;

    public BeamSearchState(int n) {
        this(0, new int[n], new int[n], 0);
    }

    public BeamSearchState(int nbs, int[] route, int[] visites, int solution) {
        this.nbs = nbs;
        this.route = route;
        this.visites = visites;
        this.solution = solution;
    }

    public int current() {
        return route[nbs - 1];
    }

    @Override
    public String toString() {
        return "BeamSearchState{" +
                "nbs=" + nbs +
                ", route=" + Arrays.toString(route) +
                ", visites=" + Arrays.toString(visites) +
                ", solution=" + solution +
                '}';
    }
}
