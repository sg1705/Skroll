package com.skroll.corpus;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by saurabhagarwal on 3/15/15.
 */
public class CorpusManagerImpl implements CorpusManager{

    String rootFolder =null;
    public static final Logger logger = LoggerFactory
            .getLogger(CorpusManagerImpl.class);

    public CorpusManagerImpl(){
        rootFolder ="/tmp";
    }

    public CorpusManagerImpl(String rootFolder){
        this.rootFolder = rootFolder;
    }

    @Override
    public Iterable<File> fileTreeTraverser(String folder) {
        return Files.fileTreeTraverser().breadthFirstTraversal(new File(folder));

    }

    @Override
    public File createFolder(String folderName) {
        return null;
    }

    @Override
    public File removeFolder(String folderName) {
        return null;
    }

    @Override
    public boolean copyFile(String sourceFilePath, String destinationFilePath) throws IOException {
        return false;
    }

    @Override
    public boolean saveFile(String folderName, String fileName, InputStream inputStream) {
        try {
            Files.createParentDirs(new File(folderName));
        } catch (IOException e) {
            logger.error("Fail to create parent directory");
            return false;
        }

        return false;
    }

    @Override
    public boolean updateFile(String folderName, String fileID, String version, InputStream inputStream) {
        return false;
    }

    @Override
    public OutputStream retrieveFile(String folderName, String fileName, String version) {
        return null;
    }

    @Override
    public OutputStream retrieveFile(String folderName, String fileName) {
        return null;
    }

    @Override
    public boolean deleteFile(String folderName, String fileID, InputStream inputStream) {
        return false;
    }

    @Override
    public boolean listFiles(String folderName) {
        return false;
    }
}
