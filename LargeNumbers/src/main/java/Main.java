import adder.Adder;
import multiplier.Multiplier;

/**
 * @author Adam Wasiljew
 */
public class Main {

    public static void main( String[] args ) {
        Multiplier multiplier = new Multiplier(new Adder());
        String a = "98137987342987498723947239479238749237498237489";
        String b = "9813798398472398748372498";
        System.out.println(a+"\n*\n"+b+"\n=\n"+multiplier.multiply(a, b));
    }
}
