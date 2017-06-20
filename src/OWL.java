import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * Created by Peter on 15.06.2017.
 */
public class OWL {

    OutputHandler out;
    OWLReasoner reasoner;
    Set<OWLClass> classes;
    OWLOntology owlOnt;

    public OWL(OutputHandler out) {
        this.out = out;
    }

    public void execute() throws OWLOntologyCreationException {

        out.output(2, "Start Setup");

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        manager.addIRIMapper(new AutoIRIMapper(new File("resources"), true));
        OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
        PrefixManager pm = new DefaultPrefixManager("http://www.co-ode.org/ontologies/ont.owl#");
        OWLOntologyLoaderConfiguration loaderConf = new OWLOntologyLoaderConfiguration();

        out.output(2, "Loading ontology");

        owlOnt = manager.loadOntologyFromOntologyDocument(
                new FileDocumentSource(new File("resources/ontology.owl")), loaderConf);

        reasoner = new Reasoner.ReasonerFactory().createReasoner(owlOnt);


        classes = reasoner.getSubClasses(dataFactory.getOWLClass(
                IRI.create("http://www.co-ode.org/ontologies/ont.owl#Astronomisches_Objekt")), false).getFlattened();

        out.output(2, "Loading done");
    }

    public void checkConsistency () {
        System.out.println("Ontology is consistent: " + reasoner.isConsistent());
    }

    public void displayClassesAndSubclasses () {
        out.output(2, "Start Reasoning");
        for (OWLClass owlClass : classes) {
            System.out.println("Class: [" + owlClass.getIRI().getFragment() + "]");
            displayIndividualsforClass(owlClass);
            System.out.println("Subclass(es): ");
            for (Node<OWLClass> owlNode : reasoner.getSubClasses(owlClass, true)) {
                for (OWLClass owlSubClass : owlNode.getEntities()) {
                    String subClassName = owlSubClass.getIRI().getFragment();
                    System.out.println("[" + subClassName + "]");
                    if (!subClassName.equals("Nothing"))
                        displayIndividualsforClass(owlSubClass);
                }
                System.out.println();
            }
        }
        out.output(2, "Done");
    }

    private void displayIndividualsforClass (OWLClass owlClass) {
        NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(owlClass, false);
        Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();
        System.out.println("\tInstance(s): ");
        for (OWLNamedIndividual ind : individuals) {
            System.out.println("\t\t[" + ind.getIRI().getFragment() + "]");
            displayPropertiesOfIndividual(ind);
        }
        System.out.println();
    }

    private void displayPropertiesOfIndividual (OWLNamedIndividual ind) {
        Map<OWLDataPropertyExpression, Set<OWLLiteral>> dataProperties = ind.getDataPropertyValues(owlOnt);
        for (OWLDataPropertyExpression dpe : dataProperties.keySet()) {
            System.out.print("\t\t\t[" + dpe.asOWLDataProperty().getIRI().getFragment());
            for (OWLLiteral lit : dataProperties.get(dpe)) {
                System.out.print(" " + lit.getLiteral());
            }
            System.out.print("]");
            System.out.println();
        }
        Map<OWLObjectPropertyExpression, Set<OWLIndividual>> objectProperties = ind.getObjectPropertyValues(owlOnt);
        for (OWLObjectPropertyExpression ope : objectProperties.keySet()) {
            System.out.print("\t\t\t[" + ope.asOWLObjectProperty().getIRI().getFragment() + " ");
            for (OWLIndividual i : objectProperties.get(ope)) {
                System.out.print(" " + i.asOWLNamedIndividual().getIRI().getFragment());
            }
            System.out.print("]");
            System.out.println();
        }
    }
}

