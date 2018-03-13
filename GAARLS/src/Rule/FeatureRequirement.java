package Rule;

/**
 * Class: FeatureRequirement Intended functionality: Associated with a feature
 * for a given data set. Encodes a value range that a feature is expected to
 * exist in and an evaluation function that determines if a feature value is
 * within that range.
 * 
 * Key Methods: evaluate -> is a given float acceptable for the requirements of 
 *                          this feature?
 *              copy -> make a exact copy of this FeatureRequirement object.
 * 
 * Feature Owner: James
 */
public class FeatureRequirement {

    // private members
    private final int featureID; //TODO revisit this
    
    private pFlag participation;
    private float upperBound;
    private float lowerBound;

    // Getters and Setters
    public int getFeatureID(){
        return this.featureID;
    }
    
    public int getParticipation() { // return the numeric value
        return participation.getValue();
    }

    public void setParticipation(int value) {
        switch (value) {
            case 0:
                this.participation = pFlag.IGNORE;
                break;
            case 1:
                this.participation = pFlag.ANTECEDENT;
                break;
            case 2:
                this.participation = pFlag.CONSEQUENT;
                break;
            default:
                this.participation = pFlag.IGNORE; // ignore by default
        }
    }

    public float getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(float upperBound) throws InvalidFeatReqException {
        int comparison = Float.compare(upperBound, this.lowerBound);
        if (comparison < 0) {
            throw new InvalidFeatReqException("Attempted to set invalid Upper Bound");
        }
        this.upperBound = upperBound;
    }

    public float getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(float lowerBound) throws InvalidFeatReqException{
        int comparison = Float.compare(this.upperBound, lowerBound);
        if (comparison < 0) {
            throw new InvalidFeatReqException("Attempted to set invalid Lower Bound");
        }
        this.lowerBound = lowerBound;
    }

    /**
     * Note: Assumes lowerBound and upperBound are valid values.
     * "Chose not to throw from this function because I don't want Rule package imported into lookupTable
     * -Peter
     * @param lowerBound
     * @param upperBound
     */
    public void setBoundRange(float lowerBound, float upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    // public constructor
    /**
     * Creates a FeatureRequirement (probably for usage in a Rule)
     * 
     * @param featureID
     * @param pInt
     * @param upper
     * @param lower
     * @throws FeatureRequirement.InvalidFeatReqException
     */
    public FeatureRequirement(int featureID, int pInt, float upper, float lower) throws InvalidFeatReqException {
        this.featureID = featureID;
        switch (pInt) {
            case 0:
                this.participation = pFlag.IGNORE;
                break;
            case 1:
                this.participation = pFlag.ANTECEDENT;
                break;
            case 2:
                this.participation = pFlag.CONSEQUENT;
                break;
            default:
                this.participation = pFlag.IGNORE; // ignore by default TODO: could decide to throw except
        }
        
        int comparison = Float.compare(upper, lower);
        if (comparison < 0) { // this must be enforced
            throw new InvalidFeatReqException("Upper Bound < Lower Bound which is invalid");
        }
        this.upperBound = upper;
        this.lowerBound = lower;
    }

    //private constructor (used to make copies only)
    private FeatureRequirement(int featureID, pFlag p, float upper, float lower) {
        this.featureID = featureID;
        this.participation = p;
        this.upperBound = upper;
        this.lowerBound = lower;
    }

    // public functions
    /**
     * Checks if a value is within the range required
     *
     * @param value value for feature. 
     * TODO ASSUMPTION: Does not check if the value
     * is valid for a feature set or comes from a feature that this requirement
     * is associated with
     * @return true if the value is in the range required
     *         false otherwise
     */
    public boolean evaluate(float value) {
        int compareUpper = Float.compare(this.upperBound, value);
        int compareLower = Float.compare(value, this.lowerBound);
        return (compareUpper >= 0 && compareLower >= 0);
    }

    /**
     * Helper function to duplicate state when mutating/doing crossovers
     *
     * @return a shallow copy clone of current state.
     */
    public FeatureRequirement copy() {
        return new FeatureRequirement(this.featureID, this.participation, this.upperBound, this.lowerBound);
    }

    // public fields
    // TODO may want to make changes to this for negation?
    public enum pFlag { // flag for rule participation
        IGNORE(0), ANTECEDENT(1), CONSEQUENT(2); // corresponding numerical values

        private final int value;

        private pFlag(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    // custom exceptions
    public class InvalidFeatReqException extends Exception {

        public InvalidFeatReqException(String message) {
            super(message);
        }
    }
}