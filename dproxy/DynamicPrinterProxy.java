import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicPrinterProxy {
    @SuppressWarnings("unchecked")
    private static <T> T createProxyInstance(Class<T> interf) {
        return (T) Proxy.newProxyInstance(
                interf.getClassLoader(),
                new Class<?>[]{interf},
                new PrintDispatcher());
    }

    public static void main(String[] args) {
        Motorcycle motorcycle = new Motorcycle(5, 200);
        Automobile auto = new Automobile(5, 150);
        Visitor proxyInstance = createProxyInstance(Visitor.class);
        proxyInstance.visit(motorcycle);
        proxyInstance.visit(auto);

    }
}

class PrintDispatcher implements InvocationHandler {

    public PrintDispatcher(){};

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        String result;
        Object target = args[0];
        PrintHandler handler = new PrintHandler();

            if (method.getName().equals("visit") && args.length == 1) {
                if (target instanceof Motorcycle){
                    result = ((Motorcycle) target).accept(handler);
                    System.out.println(result);
                } else if (target instanceof Sedan) {
                    result = ((Sedan) target).accept(handler);
                    System.out.println(result);
                } else if (target instanceof SportsCar) {
                    result = ((SportsCar) target).accept(handler);
                    System.out.println(result);
                } else if (target instanceof Automobile) {
                    result = ((Automobile) target).accept(handler);
                    System.out.println(result);
                }
            } else {
                System.err.println("Unsupported method: " + method.getName());
            }
        return "OK";
    }
}

class PrintHandler implements Visitor {

    @Override
    public String visit(Sedan sedan) {
        return "Sedan: \n" +
                "Gears: " + sedan.getGears() + "\n" +
                "Top speed: " + sedan.getTopSpeed() + "km/h" +
                "Number of doors: " + sedan.getDoors();
    }

    @Override
    public String visit(Automobile automobile) {
        return "Automobile: \n" +
                "Gears: " + automobile.getGears() + "\n" +
                "Top speed: " + automobile.getTopSpeed() + "km/h";
    }

    @Override
    public String visit(Motorcycle motorcycle) {
        return "Motorcycle: \n" +
                "Gears: " + motorcycle.getGears() + "\n" +
                "Top speed: " + motorcycle.getTopSpeed() + "km/h";
    }

    @Override
    public String visit(SportsCar sportsCar) {
        return "SportsCar: \n" +
                "Gears: " + sportsCar.getGears() + "\n" +
                "Top speed: " + sportsCar.getTopSpeed() + "km/h\n" +
                "Number of doors: " + sportsCar.getDoors();
    }
}

