package nl.cerios.cdbt;

import nl.cerios.cdbt.app.Application;
/**
 * Created by dwhelan on 13/11/2017.
 */
public class main {
    //Main simply instantiates the app and passes args
    public static void main(String[] args) {
        Application app = new Application();
        app.run(args);
    }
}