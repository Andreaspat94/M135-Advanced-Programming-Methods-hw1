public interface Visitor {
    String visit(Sedan sedan);
    String visit(Automobile automobile);
    String visit(Motorcycle motorcycle);
    String visit(SportsCar sportsCar);
}
