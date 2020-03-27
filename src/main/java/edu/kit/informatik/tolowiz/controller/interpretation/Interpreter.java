/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.controller.interpretation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.javatuples.Pair;

import edu.kit.informatik.tolowiz.model.ontology.Instance;
import edu.kit.informatik.tolowiz.model.ontology.InstanceType;
import edu.kit.informatik.tolowiz.model.ontology.Ontology;
import edu.kit.informatik.tolowiz.model.ontology.RelationType;
import edu.kit.informatik.tolowiz.model.ontology.ValueType;
import edu.kit.informatik.tolowiz.view.gui.ApplicationControllerInterface;

class Interpreter implements InterpreterInterface {
    private Path filepath;
    private OntModel rdfOntology;
    private Ontology ontology;
    private String uriPrefix;
    private ConcurrentMap<Individual, Instance> instances;
    private ConcurrentMap<ObjectProperty, RelationType> relationTypes;
    private ConcurrentMap<OntClass, InstanceType> types;
    private ConcurrentMap<DatatypeProperty, ValueType> valueTypes;
    private InstanceType owlThing;
    private List<String> errors = new LinkedList<>();
    private ApplicationControllerInterface applicationController;

    /**
     * Generates a new Interpreter. It can be used to interpret RDF/XML files.
     *
     * @param filepath              The File to use
     * @param applicationController The ApplicationController connected to this
     *                              interpreter
     */
    Interpreter(Path filepath, ApplicationControllerInterface applicationController) {
        this.filepath = filepath;
        this.applicationController = applicationController;
    }

    @Override
    public List<String> getErrorMessages() {
        return this.errors;
    }

    @Override
    public Ontology buildOntology() throws OntologyFileException, FileNotFoundException {

        this.rdfOntology = this.readOntologyFile(this.fetchFile(this.filepath));
        String uriPrefix = this.getUriPrefix();
        this.ontology = new Ontology(uriPrefix, FilenameUtils.getBaseName(this.filepath.getFileName().toString()));
        this.addInstanceTypes();
        this.addValueTypes();
        this.addRelationTypes();
        this.deriveTypeHierarchy();
        this.addInstances();
        this.addRelations();

        return this.ontology;
    }

    /**
     * creates an OntModel object with all the input from the given InputStream.
     *
     * @param ontology the InputStream
     * @return an OntModel with the info from ontology
     * @throws OntologyFileException if the input file is not a valid ontology file.
     */
    private OntModel readOntologyFile(FileInputStream ontology) throws OntologyFileException {
        OntModel rdfOntology = ModelFactory.createOntologyModel();
        try {
            rdfOntology.read(ontology, null);
        } catch (Exception e) {
            throw new OntologyFileException("this ontology file is not interpretable.", e);
        }
        return rdfOntology;
    }

    /**
     * Turns a filepath into a FileInputStream object
     *
     * @param filepath The path to read.
     * @return the file from this filepath as a FileInputStream.
     * @throws FileNotFoundException
     */
    private FileInputStream fetchFile(Path filepath) throws FileNotFoundException {
        File file = filepath.toFile();
        FileInputStream stream = new FileInputStream(file);
        return stream;
    }

    /**
     * Finds the given Ontology's specific uri prefix
     *
     * @param ontology
     * @return the uri prefix of this ontology
     */
    private String getUriPrefix() {
        if (this.uriPrefix == null) {
            this.uriPrefix = this.rdfOntology.getNsPrefixURI("");
            if (this.uriPrefix == null) {
                this.errors.add("Your Ontology file doesn't specify the empty URI Prefix. "
                        + "Please choose, which URI to associate with this ontology.");
                Set<String> potentialUris = new HashSet<>();
                Map<String, String> ns = this.rdfOntology.getNsPrefixMap();
                for (Map.Entry<String, String> entry : ns.entrySet()) {
                    potentialUris.add(entry.getValue());
                }
                String uri = this.applicationController.selectURI(potentialUris);
                this.uriPrefix = uri;
            }
        }
        return this.uriPrefix;
    }

    /**
     * Adds all the instance types described in the rdf file to the ontology object.
     *
     * @param ontology you want to add the instance types to.
     */
    private void addInstanceTypes() {
        this.owlThing = this.ontology.addType("owl:Thing", null, null, null, null,
                (this.getUriPrefix() + "#owl:Thing"));
        List<OntClass> typ = this.getClasses();
        ConcurrentMap<OntClass, InstanceType> instanceTypes = new ConcurrentHashMap<>();
        for (OntClass ontClass : typ) {
            instanceTypes.put(ontClass,
                    this.ontology.addType(ontClass.getLocalName(), null, null, null, null, ontClass.getURI()));
        }
        this.types = instanceTypes;
    }

    /**
     * Sets all subType relations in the ontology
     *
     * @param ontology
     */
    private void deriveTypeHierarchy() {
        for (Map.Entry<OntClass, InstanceType> entry : this.types.entrySet()) {
            InstanceType type = entry.getValue();
            OntClass ontClass = entry.getKey();
            InstanceType superType;
            List<OntClass> superClasses = ontClass.listSuperClasses(true).toList();
            for (OntClass superClass : superClasses) {
                if (this.types.get(superClass) != null) {
                    superType = this.types.get(superClass);
                    this.ontology.addSuperType(type, superType);
                    this.ontology.addSubType(superType, type);
                }

            }
            if (type.getSuperTypes().isEmpty()) {
                this.ontology.addSuperType(type, this.owlThing);
                this.ontology.addSubType(this.owlThing, type);
            }
        }
    }

    /**
     * Adds all the value types described in the rdf file to the ontology object.
     *
     * @param ontology you want to add the value types to.
     */
    private void addValueTypes() {
        List<DatatypeProperty> dat = this.getDataProperties();
        ConcurrentMap<DatatypeProperty, ValueType> valueTypes = new ConcurrentHashMap<>();
        for (DatatypeProperty datatypeProperty : dat) {
            valueTypes.put(datatypeProperty,
                    this.ontology.addValueType(datatypeProperty.getLocalName(), datatypeProperty.getURI()));
        }
        this.valueTypes = valueTypes;
    }

    /**
     * Adds all the relation types described in the rdf file to the ontology object.
     *
     * @param ontology you want to add the relation types to.
     */
    private void addRelationTypes() {
        List<ObjectProperty> obj = this.getObjectProperties();
        ConcurrentMap<ObjectProperty, RelationType> relationTypes = new ConcurrentHashMap<>();
        for (ObjectProperty objectProperty : obj) {
            relationTypes.put(objectProperty,
                    this.ontology.addRelationType(objectProperty.getLocalName(), objectProperty.getURI()));
        }
        this.relationTypes = relationTypes;
    }

    private void addInstances() {
        List<Individual> ind = new LinkedList<>();
        ind.addAll(this.getIndividuals());
        this.instances = new ConcurrentHashMap<>();

        List<Thread> workers = new LinkedList<>();

        int threads = Runtime.getRuntime().availableProcessors();
        threads = (threads > 6 ? threads - 6 : 1);

        if (ind.size() == 0) {
            return;
        }
        List<List<Individual>> parts = ListUtils.partition(ind,
                (int) Math.ceil((double) ind.size() / (double) threads));

        for (List<Individual> list : parts) {
            Runnable work = () -> {
                for (Individual individual : list) {
                    Set<InstanceType> types = new HashSet<>();
                    List<OntClass> classes = individual.listOntClasses(true).toList();
                    for (OntClass ontClass : classes) {
                        if (this.types.get(ontClass) != null) {
                            types.add(this.types.get(ontClass));
                        }
                    }
                    Set<Pair<ValueType, String>> values = this.getValues(individual);
                    String uri = individual.getURI();
                    Instance instance = null;
                    synchronized (this) {
                        instance = this.ontology.addInstance(individual.getLocalName(), uri, types, null, values);
                    }
                    this.instances.put(individual, instance);
                }
            };

            Thread thread = new Thread(work);
            workers.add(thread);
            thread.start();
        }

        for (Thread thread : workers) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns all values of a given individual
     *
     * @param individual from the rdf file
     */
    private Set<Pair<ValueType, String>> getValues(Individual ind) {
        Set<Pair<ValueType, String>> values = new HashSet<>();
        for (Map.Entry<DatatypeProperty, ValueType> entry : this.valueTypes.entrySet()) {
            DatatypeProperty prop = entry.getKey();
            List<RDFNode> vals = ind.listPropertyValues(prop).toList();
            for (RDFNode val : vals) {
                String value = val.asLiteral().getLexicalForm();
                values.add(new Pair<>(this.valueTypes.get(prop), value));
            }
        }
        return values;
    }

    /**
     * Adds all the relations described in the rdf file to the ontology object.
     *
     * @param ontology you want to add the relations to.
     */
    private void addRelations() {
        for (Map.Entry<Individual, Instance> entry : this.instances.entrySet()) {
            Individual ind = entry.getKey();
            Instance ins = entry.getValue();
            for (Map.Entry<ObjectProperty, RelationType> rEntry : this.relationTypes.entrySet()) {
                ObjectProperty prop = rEntry.getKey();
                List<RDFNode> nodes = ind.listPropertyValues(prop).toList();
                for (RDFNode val : nodes) {
                    OntResource res = (OntResource) val.asResource();
                    Individual value = res.asIndividual();
                    Instance dest = this.instances.get(value);
                    String uri = prop.getURI() + ":"
                            + (ind.getURI() + "->" + dest.getURI()).replace(this.getUriPrefix(), "");
                    this.ontology.addRelation(uri, this.relationTypes.get(prop), ins, dest);
                }
            }
        }
    }

    /**
     * Lists all classes in the ontology;
     *
     * @param ontology
     * @return a List of all classes
     */
    private List<OntClass> getClasses() {
        List<OntClass> classes = this.rdfOntology.listNamedClasses().toList();
        return classes;
    }

    /**
     * Lists all individuals in the ontology.
     *
     * @param ontology
     * @return List with individuals
     */
    private Set<Individual> getIndividuals() {
        Set<Individual> ind = new HashSet<>();

        for (Map.Entry<OntClass, InstanceType> entry : this.types.entrySet()) {
            OntClass ontClass = entry.getKey();
            List<? extends OntResource> resources = ontClass.listInstances(true).toList();
            for (OntResource ontRes : resources) {
                Individual individual = ontRes.asIndividual();
                ind.add(individual);
            }
        }

        return ind;
    }

    /**
     * Lists all datatype properties in the ontology.
     *
     * @param ontology
     * @return List with datatype properties
     */
    private List<DatatypeProperty> getDataProperties() {
        List<DatatypeProperty> data = this.rdfOntology.listDatatypeProperties().toList();
        return data;
    }

    /**
     * Lists all object properties in the ontology
     *
     * @param ontology
     * @return List with all object properties
     */
    private List<ObjectProperty> getObjectProperties() {
        List<ObjectProperty> obj = this.rdfOntology.listObjectProperties().toList();
        return obj;
    }
}
