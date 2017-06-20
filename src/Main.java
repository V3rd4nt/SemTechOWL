import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Peter on 05.06.2017.
 */
public class Main {

    private final static String mainMenuText = "\n<-MAIN-MENU->\n" +
            "What do you like to do?\n" +
            "\t(1) Check consistency of ontology\n" +
            "\t(2) List all classes, individuals and their properties\n" +
            "\t(3) List all individuals and their properties for a given class\n";
    private final static String errorMsg = "Sorry, that's not a valid input. Please try again: ";

    public static void main(String[] args) throws OWLOntologyCreationException, IOException {
        OWL miniproject2 = new OWL(new OutputHandler(5));
        miniproject2.create();
        System.out.print(mainMenuText);
        char input;
        do {
            switch (input = createChar()) {
                case '1':
                    miniproject2.checkConsistency();
                    break;
                case '2':
                    miniproject2.displayClassesAndSubclasses();
                    break;
                case '3':
                    System.out.print("Name of class or subclass: ");
                    miniproject2.displayIndividualsOfClassByClassname(createString());
                    break;
                default:
                System.out.println(errorMsg);
                break;
            }
        System.out.print(mainMenuText);
        } while (input != '0');
    }

    public static char createChar() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine();
        if (line.length() == 0) return '\u0000';
        else return line.charAt(0);
    }

    public static String createString() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
    }
}
