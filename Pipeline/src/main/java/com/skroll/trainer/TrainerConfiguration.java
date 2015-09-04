package com.skroll.trainer;

import com.skroll.util.Configuration;

/**
 * TrainerConfiguration is the configuration used for command line training utility.
 * Created by saurabh on 6/15/15.
 */
public class TrainerConfiguration extends Configuration {
    public TrainerConfiguration() {
        super("src/main/resources/skroll-trainer.properties");
    }
}