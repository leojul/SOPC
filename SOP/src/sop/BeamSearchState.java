package sop;

import java.util.List;

public class BeamSearchState {

    protected List<Short> route;
    int solution;

    public BeamSearchState(List<Short> route, int solution) {
        this.route = route;
        this.solution = solution;
    }

    public int current() {
        return route.get(route.size() - 1);
    }

    @Override
    public String toString() {
        return "BeamSearchState{" +
                "route=" + route +
                ", solution=" + solution +
                '}';
    }
}
