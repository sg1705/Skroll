package com.skroll.util;

import com.google.common.base.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
/**
 * Created by saurabhagarwal on 1/11/15.
 */
public class Configuration {

    protected Map<String, String> confMap = new HashMap<String, String>();

    //The following line needs to be added to enable log4j
    public static final Logger logger = LoggerFactory
           .getLogger(Configuration.class);

    public Configuration() {
         this("skroll.properties");
    }

    public Configuration(String fileName)  {
        Properties prop = null;
        try {
            prop = getProperties (fileName);
        } catch (IOException e) {
            logger.debug("Configuration is empty. unable to read property file");
        }
        Enumeration<Object> keys = prop.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            confMap.put(key, prop.getProperty(key));
        }
        logger.debug("ConfMap:"+confMap);
    }

    public String get(String key) {
        return confMap.get(key);
    }
    public String get(String key, String defaultValue) {
        if (confMap.get(key)==null) {
            return defaultValue;
        } else {
            return confMap.get(key);
        }
    }
    public Properties getProperties (String fileName) throws IOException {

        InputStream inStream = null;
        Properties prop = new Properties();

        try {
            // Load the properties from properties file in classpath
            ClassLoader clazzLoader = Thread.currentThread()
                    .getContextClassLoader();
            inStream = clazzLoader.getResourceAsStream(fileName);
            if (inStream == null) {
                //check in file system
                inStream = new FileInputStream(fileName);
                if (inStream == null) {
                    logger.error("Cannot find file {}", fileName);
                    System.exit(-1);
                }
            }
            prop.load(inStream);
            for (Object key :prop.keySet()) {
                // Properties that is defined in properties file can be overridden by -D options on command line
                String override = System.getProperty((String) key);
                if (override != null) {
                    prop.put(key, override);
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Number of properties read {}", prop.size());
            }
        } catch (IOException e) {
            // logger.error("systemConf.properties is not loaded correctly.", e);
            e.printStackTrace();
            throw e;
        } finally {
            inStream.close();
        }

        return prop;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("confMap", confMap)
                .toString();
    }
}
