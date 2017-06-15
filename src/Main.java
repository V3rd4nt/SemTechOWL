import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * Created by Peter on 05.06.2017.
 */
public class Main {

    public static void main(String[] args) throws OWLOntologyCreationException {
        OWL miniproject2 = new OWL(new OutputHandler(5));
        miniproject2.execute();
    }
}
