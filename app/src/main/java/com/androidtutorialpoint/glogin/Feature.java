package com.androidtutorialpoint.glogin;
public class Feature {

    private String featureName;

    // Image name (Without extension)
    private String featureIcon;
    private String discription;

    public Feature(String featureName, String featureIcon, String discription) {
        this.featureName= featureName;
        this.featureIcon= featureIcon;
        this.discription= discription;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureIcon() {
        return featureIcon;
    }

    public void setFeatureIcon(String featureIcon) {
        this.featureIcon = featureIcon;
    }

    @Override
    public String toString()  {
        return this.featureName+" (discription: "+ this.discription+")";
    }
}