import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.HermiT.Reasoner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Map;

//import org.semanticweb.owl.inference.OWLReasonerFactory;
//import org.semanticweb.owl.model.*;
//import org.mindswap.pellet.owlapi.PelletReasonerFactory;

import java.util.Iterator;
import java.util.Set;
import java.io.*;


public class Obligation{

    public static final String DOCUMENT_IRI = "file:ontology/OntologyOBL.owl";

    public static void main(String[] args) {
        try {
            Writer output = null;
            File file = new File("System_state.txt");
            output = new BufferedWriter(new FileWriter(file));

            // Create our ontology manager in the usual way.
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

            File file1 = new File("ontology/OntologyOBL.owl");
            // Now load the local copy
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(file1);
            System.out.println("Loaded ontology: " + ontology);
            // We can always obtain the location where an ontology was loaded from
            IRI documentIRI = manager.getOntologyDocumentIRI(ontology);
            System.out.println("    from: " + documentIRI);

            // We need a data factory to create various object from.
            // Each ontology has a reference to a data factory that we can use.
            OWLDataFactory factory = manager.getOWLDataFactory();

            // There are two ways we can create classes (and other entities).
            // The second is to use a prefix manager and specify abbreviated IRIs.
            // This is useful for creating lots of entities with the same prefix IRIs.
            // First create our prefix manager and specify that the default prefix IRI (bound to the empty prefix name)
            PrefixManager pm = new DefaultPrefixManager("http://www.people.lu.unisi.ch/fornaran/ontologies/OntologyOBL.owl#");
            // Now we use the prefix manager and just specify an abbreviated IRI


            OWLClass Obligation = factory.getOWLClass(":Obligation", pm);
            OWLClass Cancelled = factory.getOWLClass(":Cancelled", pm);
            OWLClass KCancelled = factory.getOWLClass(":KCancelled", pm);
            OWLClass Fulfilled = factory.getOWLClass(":Fulfilled", pm);
            OWLClass KFulfilled = factory.getOWLClass(":KFulfilled", pm);
            OWLClass Activated = factory.getOWLClass(":Activated", pm);
            OWLClass Violated = factory.getOWLClass(":Violated", pm);
            OWLClass Elapsed = factory.getOWLClass(":Elapsed", pm);

            output.write("-----------------------------------------------------------------\n");

            Set<OWLEquivalentClassesAxiom> app= ontology.getEquivalentClassesAxioms(KCancelled);
            System.out.println("Equivalent classes axioms of class KCancelled: " + app);
            output.write("Equivalent classes axioms of class KCancelled: " + app +"\n");
            app=ontology.getEquivalentClassesAxioms(KFulfilled);
            System.out.println("Equivalent classes axioms of class KFulfilled: " + app);
            output.write("Equivalent classes axioms of class KFulfilled: " + app + "\n");

            //Set<OWLIndividual> CancelledIndividuals;
            //Set<OWLIndividual> FulfilledIndividuals;

            NodeSet<OWLNamedIndividual> individualsNodeSet;
            Set<OWLNamedIndividual> individuals;
            //Set<OWLNamedIndividual> individuals;

            OWLClassAssertionAxiom classAssertion;
            OWLAxiom axiom;
            OWLClassExpression description;

            int t=0;
            int tMax=5;
            //Create an array with the list of time events of the system
            String[] instants={"inst1","inst2","inst3","inst4","inst5"};

            while (t<tMax){
                output.write("\n---------------------------------------------------------------\n");
                //System.out.println("System time = "+t);
                //output.write("System time = "+t+"\n");
                // We need to create an instance of OWLReasoner.  An OWLReasoner provides the basic
                // query functionality that we need, for example the ability obtain the subclasses
                // of a class etc.  To do this we use a reasoner factory.
                // We will use HermiT, HermiT can be downloaded from http://hermit-reasoner.com
                // Make sure you get the HermiT library and add it to your class path.
                // You can then instantiate the HermiT reasoner factory:
                // You'll also need to import the org.semanticweb.HermiT.Reasoner package.

                // Get the reference to the :inst1 individual
                //System.out.println("Add elapsed "+instants[t]+"\n");
                //output.write("Add elapsed "+instants[t]+"\n");
                OWLIndividual inst = factory.getOWLNamedIndividual(":"+instants[t], pm);

                // Now create a ClassAssertion to specify that :inst1 is an instance of :Elapsed
                classAssertion = factory.getOWLClassAssertionAxiom(Elapsed, inst);
                // Add the class assertion
                manager.addAxiom(ontology, classAssertion);


                //REASONER
                OWLReasonerFactory reasonerFactory = null;
                reasonerFactory = new Reasoner.ReasonerFactory();

                // We'll now create an instance of an OWLReasoner (the implementation being provided by HermiT as
                // we're using the HermiT reasoner factory).  The are two categories of reasoner, Buffering and
                // NonBuffering.  In our case, we'll create the buffering reasoner, which is the default kind of reasoner.
                // We'll also attach a progress monitor to the reasoner.  To do this we set up a configuration that
                // knows about a progress monitor.

                // Create a console progress monitor.  This will print the reasoner progress out to the console.
                ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();

                // Specify the progress monitor via a configuration.  We could also specify other setup parameters in
                // the configuration, and different reasoners may accept their own defined parameters this way.
                OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);

                // Create a reasoner that will reason over our ontology and its imports closure.  Pass in the configuration.
                OWLReasoner reasoner = reasonerFactory.createReasoner(ontology, config);

                // Ask the reasoner to do all the necessary work now
                reasoner.precomputeInferences();

                // We can determine if the ontology is actually consistent (in this case, it should be).
                boolean consistent = reasoner.isConsistent();
                System.out.println("Consistent: " + consistent);
                System.out.println("\n");

                //ELAPSED
                // Ask the reasoner for the instances of a class
                // See JavaDoc at http://owlapi.sourceforge.net/javadoc/index.html
                // parameter = false, if all instances should be retrieved (false);
                //parameter = true, if the direct instances should be retrieved
                individualsNodeSet = reasoner.getInstances(Elapsed, false);
                // The reasoner returns a NodeSet again.  This time the NodeSet contains individuals.
                // Again, we just want the individuals, so get a flattened set.
                individuals = individualsNodeSet.getFlattened();
                System.out.println("Instances of Elapsed: ");
                output.write("Instances of Elasped: "+"\n");
                for(OWLNamedIndividual ind : individuals) {
                    System.out.println("    " + ind +"\n");
                    output.write(" "+ ind+"\n");
                }

                //ACTIVATED
                individualsNodeSet = reasoner.getInstances(Activated, false);
                individuals = individualsNodeSet.getFlattened();
                System.out.println("Instances of Activated: ");
                output.write("Instances of Activated: "+"\n");
                for(OWLNamedIndividual ind : individuals) {
                    System.out.println("    " + ind +"\n");
                    output.write(" "+ ind+"\n");
                }

                //FULFILLED
                individualsNodeSet = reasoner.getInstances(Fulfilled, false);
                individuals = individualsNodeSet.getFlattened();
                System.out.println("Instances of Fulfilled: ");
                output.write("Instances of Fulfilled: "+"\n");
                for(OWLNamedIndividual ind : individuals) {
                    System.out.println("    " + ind +"\n");
                    output.write(" "+ ind+"\n");
                }

                //VIOLATED
                individualsNodeSet = reasoner.getInstances(Violated, false);
                individuals = individualsNodeSet.getFlattened();
                System.out.println("Instances of Violated: ");
                output.write("Instances of Violated: "+"\n");
                for(OWLNamedIndividual ind : individuals) {
                    System.out.println("    " + ind +"\n");
                    output.write(" "+ ind+"\n");
                }

                //CANCELLED
                // Ask the reasoner for the instances of a class
                individualsNodeSet = reasoner.getInstances(Cancelled, false);
                // The reasoner returns a NodeSet again.  This time the NodeSet contains individuals.
                // Again, we just want the individuals, so get a flattened set.
                individuals = individualsNodeSet.getFlattened();
                System.out.println("Instances of Cancelled: ");
                output.write("Instances of Cancelled: "+"\n");
                for(OWLNamedIndividual ind : individuals) {
                    System.out.println("    " + ind +"\n");
                    output.write(" "+ ind+"\n");
                }

                //CLOSURE: assert that KFulfilled is equivalent to the list of individuals that belong to the Cancelled class
                individualsNodeSet = reasoner.getInstances(Fulfilled, false);
                individuals = individualsNodeSet.getFlattened();

                if (individuals.size()>0) {

                    //remove the axioms that define KFulfilled
                    for(OWLEquivalentClassesAxiom a: ontology.getEquivalentClassesAxioms(KFulfilled)){
                        RemoveAxiom removeAxiom= new RemoveAxiom(ontology,a);
                        manager.applyChange(removeAxiom);
                    }

                    //create a class with all those individuals
                    description = factory.getOWLObjectOneOf(individuals);

                    axiom = factory.getOWLEquivalentClassesAxiom(KFulfilled, description);
                    AddAxiom addAxiom = new AddAxiom(ontology, axiom);
                    manager.applyChange(addAxiom);
                }

                app= ontology.getEquivalentClassesAxioms(KFulfilled);
                System.out.println("Equivalent classes axioms of class KFulfilled:\n" + app);
                output.write("Equivalent classes axioms of class KFulfilled:\n" + app +"\n");

                //CLOSURE: assert that KCancelled is equivalent to the list of individuals that belong to the Cancelled class
                individualsNodeSet = reasoner.getInstances(Cancelled, false);
                individuals = individualsNodeSet.getFlattened();

                if (individuals.size()>0) {

                    //remove the axioms that define KFulfilled
                    for(OWLEquivalentClassesAxiom a: ontology.getEquivalentClassesAxioms(KCancelled)){
                        RemoveAxiom removeAxiom= new RemoveAxiom(ontology,a);
                        manager.applyChange(removeAxiom);
                    }

                    //create a class with all those individuals
                    description = factory.getOWLObjectOneOf(individuals);

                    axiom = factory.getOWLEquivalentClassesAxiom(KCancelled, description);
                    AddAxiom addAxiom = new AddAxiom(ontology, axiom);
                    manager.applyChange(addAxiom);
                }

                app= ontology.getEquivalentClassesAxioms(KCancelled);
                System.out.println("Equivalent classes axioms of class KCancelled:\n" + app);
                output.write("Equivalent classes axioms of class KCancelled:\n" + app +"\n");

                t++;
            }
            output.close();
        }
        catch (OWLException e) {
            e.printStackTrace();
        }
        catch(UnsupportedOperationException exception) {
            System.out.println("Unsupported reasoner operation.");
        }
        catch(IOException exception) {
            System.out.println("problem with the output file");
        }

        /*catch(OWLReasonerException ex) {
            System.out.println("Reasoner error: " + ex.getMessage());
        }
        }*/
    }

}
