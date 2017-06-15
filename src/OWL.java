import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.AutoIRIMapper;

import java.io.File;
import java.util.Set;

/**
 * Created by Peter on 15.06.2017.
 */
public class OWL {

    OutputHandler out;

    public OWL(OutputHandler out) {
        this.out = out;
    }

    public void execute()  throws OWLOntologyCreationException {

        out.output(2, "Start Setup");

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        manager.addIRIMapper(new AutoIRIMapper(new File("resources"), true));
        OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
        OWLOntologyLoaderConfiguration loaderConf = new OWLOntologyLoaderConfiguration();

        out.output(2, "Start Loading");

        OWLOntology owlOnt = manager.loadOntologyFromOntologyDocument(
                new FileDocumentSource(new File("resources/ontology.owl")), loaderConf);

        out.output(2, "Start Reasoning");

        OWLReasoner reasoner = new Reasoner.ReasonerFactory().createReasoner(owlOnt);
        System.out.println("Ontology is consistent: " + reasoner.isConsistent());

        Set<OWLClass> classes = reasoner.getSubClasses(dataFactory.getOWLClass(IRI.create("http://example.org/ontology")), false).getFlattened();
        for(OWLClass c : classes) {
            System.out.println("Class: " + c.getIRI().getFragment());
            System.out.print("  Subclasses: ");
            for(Node<OWLClass> n : reasoner.getSubClasses(c, true))
                for(OWLClass s : n.getEntities()) {
                    System.out.print(" " + s.getIRI().getFragment());
                }
            System.out.println();

            System.out.print("  Disjoint clases: ");
            for(Node<OWLClass> n : reasoner.getDisjointClasses(c))
                for(OWLClass s : n.getEntities()) {
                    System.out.print(" " + s.getIRI().getFragment());
                }
            System.out.println();
        }

        out.output(2, "END");
    }
}
