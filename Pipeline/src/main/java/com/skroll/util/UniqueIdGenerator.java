package com.skroll.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

/** Generate unique Id is using MD5. It is primarily used to generate document id.
 * Created by saurabhagarwal on 8/11/15.
 */
public class UniqueIdGenerator {
    public static final Logger logger = LoggerFactory
            .getLogger(UniqueIdGenerator.class);
    public static String generateId (String inputText)throws Exception
    {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(inputText.getBytes());

        byte digestedId[] = md.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < digestedId.length; i++) {
            sb.append(Integer.toString((digestedId[i] & 0xff) + 0x100, 16).substring(1));
        }

        logger.debug(" UniqueId: {}", sb.toString());
        return sb.toString();
    }
}
