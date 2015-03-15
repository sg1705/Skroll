package com.skroll.corpus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by saurabhagarwal on 1/7/15.
 */
public interface CorpusManager {

    public Iterable<File> fileTreeTraverser(String folder);

    public File createFolder(String folderName);

    public File removeFolder(String folderName);

    public boolean copyFile(String sourceFilePath, String destinationFilePath) throws IOException;

    public boolean saveFile(String folderName,String fileID, InputStream inputStream);

    public boolean updateFile(String folderName,String fileID, String version, InputStream inputStream);

    public OutputStream retrieveFile(String folderName,String fileName, String version);

    //latest version
    public OutputStream retrieveFile(String folderName,String fileName);

    public boolean deleteFile(String folderName,String fileID, InputStream inputStream);

    public boolean listFiles(String folderName);


}
