package info.purlin.mar.reducible;

public class FloatScalarReducible implements AllReducible {

    public float val;

    public FloatScalarReducible(float instance) {
        val = instance;
    }

    public String toString() {
        return "" + val;
    }

    @Override
    public void sum(AllReducible other) {
        val += ((FloatScalarReducible) other).val;
    }
}
