package fr.fistin.styx;

/**
 * Created by AstFaster
 * on 05/11/2022 at 16:53
 */
public class StyxBootstrap {

    public static void main(String[] args) {
        if (Float.parseFloat(System.getProperty("java.class.version")) < 62.0D) {
            System.err.println("*** ERROR *** Styx requires Java >= 18 to work!");
            return;
        }

        new Styx().start();
    }

}
