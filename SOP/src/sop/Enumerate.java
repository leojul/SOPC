package sop;

class Enumerate implements Comparable<Enumerate> {
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

    @Override
    public int compareTo(Enumerate enumerate) {
        return Integer.compare(v, enumerate.v);
    }

    @Override
    public String toString() {
        return "Enumerate{" +
                "i=" + i +
                ", v=" + v +
                '}';
    }
}
