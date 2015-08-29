package com.skroll.classifier;

/**
 * Created by saurabhagarwal on 8/29/15.
 */
public enum ClassifierId {
    //DocType Classifier
    DOCTYPE_CLASSIFIER(1, "DOCTYPE_CLASSIFIER"),
    //TOC Classifier
    UNIVERSAL_TOC_CLASSIFIER(100,"UNIVERSAL_TOC_CLASSIFIER"),
    TEN_K_TOC_CLASSIFIER(101,"TEN_K_TOC_CLASSIFIER"),
    TEN_Q_TOC_CLASSIFIER(102,"TEN_Q_TOC_CLASSIFIER"),
    INDENTURE_TOC_CLASSIFIER(103,"INDENTURE_TOC_CLASSIFIER"),
    //Def Classifier
    UNIVERSAL_DEF_CLASSIFIER(200,"UNIVERSAL_DEF_CLASSIFIER"),
    TEN_K_DEF_CLASSIFIER(201,"TEN_K_DEF_CLASSIFIER"),
    TEN_Q_DEF_CLASSIFIER(202,"TEN_Q_DEF_CLASSIFIER"),
    INDENTURE_DEF_CLASSIFIER (203,"INDENTURE_DEF_CLASSIFIER");

    private final String name;
    private final int id;

    ClassifierId(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public static ClassifierId fromName(String name) {
            if (name != null) {
                for (ClassifierId classifierId : values()) {
                    if (classifierId.name.equals(name)) {
                        return classifierId;
                    }
                }
            }
            throw new IllegalArgumentException("Invalid classifier: " + name);
    }

    public String getName() {
            return name;
    }
    public int getId() {
        return id;
    }
}
