public class Motorcycle implements Vehicle {
    private int gears;
    private int topSpeed;

    public Motorcycle(int gears, int topSpeed) {
        this.gears = gears;
        this.topSpeed = topSpeed;
    }

    @Override
    public String accept(Visitor visitor) {
        return visitor.visit(this);
    }

    public int getGears() {
        return gears;
    }

    public int getTopSpeed() {
        return topSpeed;
    }
}
