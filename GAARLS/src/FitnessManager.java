
import Rule.*;

import java.util.ArrayList;

/**
 * Class: FitnessManager.java
 * Intended functionality: Util class that is in charge of all fitness function calculations, using the feature database
 * for value queries.
 * Feature Owner: Shane, Peter
 */


public class FitnessManager {

    // private members
    private Database theDatabase;
    private ArrayList<Rule> theWekaRules;
    private final float baseFitnessWeight;
    private final float ext1FitnessWeight;                                                                              // Weights remain consistent for the lifespan
    private final float ext2FitnessWeight;                                                                              // of this class, once assigned
    private final float minCoverage;                                                                                    // rules with coverage below min will receive fitness of 0

    // public functions
    public FitnessManager(Database database, ArrayList<Rule> wekaRules, ConfigParameters cp)
    {
        // BAD PRACTICE. IF CP IS NULL IT SHOULD BE AN ERROR
        if(cp == null){
            baseFitnessWeight = 1.0f;
            ext1FitnessWeight = 0.0f;       // base weights for testing
            ext2FitnessWeight = 0.0f;
            minCoverage = 0.005f;
        }else{
            baseFitnessWeight = cp.baseFitnessWeight;
            ext1FitnessWeight = cp.ext1FitnessWeight;   // gather weights defined by cp 
            ext2FitnessWeight = cp.ext2FitnessWeight;
            minCoverage = cp.minCoverage;
        }

        theDatabase = database;
        theWekaRules = wekaRules;
    }

    /**
     * Calculate the fitness of a rule given internal fitness functions
     * @param rule to evaluate
     *             NOTE: information relevant to rule will be cached inside rule at the same time
     *                   will not change functionality of rule
     *             UPDATE: Now properly uses weights defined by the ConfigParameters object that is passed into
     *                     the class constructor
     * @return fitness value of rule; the value range will be completely controlled by the weights of the components
     */
    public float fitnessOf(Rule rule) {

        if (!RuleManager.IsValidRule(rule))
            return 0;

        theDatabase.EvaluateRule(rule);                                                                                 // Populates rule with accuracy, coverage and completeness

        if (rule.getCoverage() < minCoverage)
            return 0;

        return baseFitnessWeight * fitnessBasic(rule)                                                                   // Accuracy coverage and range fitness
                + (theWekaRules.isEmpty() ? 0 : ext1FitnessWeight * ext1(rule))                                         // Weka comparison
                + ext2FitnessWeight * ext2(rule);                                                                       // Completeness
    }

    /**
     * This is the most basic version of the fitness function; to be used in runs
     * of the 'Base System' version
     * @param rule
     * @return fitness value between 0.0 and 100.0
     */
    private float fitnessBasic(Rule rule){
        float coverage = rule.getCoverage();
        float accuracy = rule.getAccuracy();
        float rangeFitness = rule.getRangeCoverage();

        // The following three weights sum to 1 for legibility; not a requirement
        float w1 = 0.50f;                                                                                               // Accuracy weight
        float w2 = 0.15f;                                                                                               // Coverage weight
        float w3 = 0.35f;                                                                                               // Range weight

        float fitnessBase = ((w1*accuracy + w2*coverage + w3*rangeFitness))*100;

        return fitnessBase;
    }

    /**
     * This method computes a Hamming distance like measure between two given Rules. In this version
     * we define 'distance' as number of non-identical clauses in the two rules.
     * value, and add this to the distance value.
     *
     * @param r1: first rule
     * @param r2: second rule
     * @return  the number of elements of the featureRequirement vector 'participation' values that are different between the two rules, normalized
     * to value between 0.0 and 100.0
     */
    protected float hammingDistance(Rule r1, Rule r2){
        int distance = 0;

        FeatureRequirement[] r1FeatureVector = r1.getFeatureReqs();
        FeatureRequirement[] r2FeatureVector = r2.getFeatureReqs();
        for(int i = 0; i < r1FeatureVector.length; i++){
            if(r1FeatureVector[i].getParticipation() != r2FeatureVector[i].getParticipation())
                distance++;
        }
        return ((distance/r1.getFeatureReqs().length)*100);
    }

    protected float ext1(Rule rule){
        float smallestDistance = rule.getFeatureReqs().length;                                                          // Holds the smallest Hamming distance found to exist between rule and anu rule in known rules
        for(Rule wekaRule : theWekaRules){
            float dist = hammingDistance(rule, wekaRule);
            if(dist < smallestDistance) {
                smallestDistance = dist;
            }
        }

        return smallestDistance;
    }

    /**
     *
     * @param rule
     * @return value between 0.0 and 100.0
     */
    private float ext2(Rule rule){

        return (rule.getCompleteness()*100);
    }

}
