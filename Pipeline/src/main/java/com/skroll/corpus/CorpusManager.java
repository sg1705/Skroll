package com.skroll.corpus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by saurabhagarwal on 1/7/15.
 */
public interface CorpusManager {

    public Iterable<File> fileTreeTraverser(String folder);

    public File createFolder(String parentFolderName, String folderName);

    public File removeFolder(String parentFolderName, String folderName);

    public List<File> searchFolder(String folderName, int linesToSearch, String regex);

    public boolean searchInputStream(InputStream is, int linesToSearch, String regex);

    public boolean copyFile(String sourceFilePath, String destinationFilePath) throws IOException;
}
