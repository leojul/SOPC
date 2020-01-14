package sop;

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
