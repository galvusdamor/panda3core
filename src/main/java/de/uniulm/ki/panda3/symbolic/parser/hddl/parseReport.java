package de.uniulm.ki.panda3.symbolic.parser.hddl;

/**
 * Created by dhoeller on 24.06.16.
 */
public class parseReport {
    private boolean foundNumeric;
    private boolean numericEffect = false;

    public void reportNumericEffect() {
        this.foundNumeric = true;
        this.numericEffect = true;
    }

    public void printReport() {
        if (foundNumeric) {
            System.out.print("There are numeric elements in the domain: ");
            if (numericEffect)
                System.out.print("numeric effects, ");
            System.out.println(" they will be IGNORED.");
        }
    }

    public void reportConditionalEffects() {

    }

    public void reportForallEffect() {

    }
}
