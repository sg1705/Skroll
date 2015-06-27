package com.skroll.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by saurabhagarwal on 6/27/15.
 */
public class HeapStats {
    //The following line needs to be added to enable log4j
    public static final Logger logger = LoggerFactory
            .getLogger(HeapStats.class);
    public static void display (){
        int mb = 1024*1024;

        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        logger.debug("##### Heap utilization statistics [MB] #####");

        //Print used memory
        logger.debug("Used Memory:{} MB",
                (runtime.totalMemory() - runtime.freeMemory()) / mb);

        //Print free memory
        logger.debug("Free Memory:{} MB", runtime.freeMemory() / mb);

        //Print total available memory
        logger.debug("Total Memory: {} MB", runtime.totalMemory() / mb);

        //Print Maximum available memory
        logger.debug("Max Memory:{} MB", runtime.maxMemory() / mb);
    }
}
