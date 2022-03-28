public class SportsCar extends Automobile {
    private int doors;

    public SportsCar(int gears, int topSpeed, int doors) {
        super(gears, topSpeed);
        this.doors = doors;
    }

    @Override
    public String accept(Visitor visitor) {
        return visitor.visit(this);
    }

    public int getDoors() {
        return doors;
    }
}
