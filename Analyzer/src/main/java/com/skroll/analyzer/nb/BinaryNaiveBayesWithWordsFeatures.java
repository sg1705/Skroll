package com.skroll.analyzer.nb;

import java.io.*;
import java.util.*;

/**
 * Created by wei2learn on 12/13/2014.
 */
public class BinaryNaiveBayesWithWordsFeatures {
    int [] categoryCount=new int[2];
    static final double PRIOR_COUNT=100;
    static final int MAX_SENTENCE_LENGTH=12;
    static final boolean USE_FIRST_CHAR=true;
    Map<String,Integer>[] wordCounts=new HashMap[2];
    static final String SPLIT_STRING="[ .,\u201c\t\u201d\"]+";//"[ .,\t]+";



    public BinaryNaiveBayesWithWordsFeatures(){
        for (int i=0;i<2;i++){
            wordCounts[i]=new HashMap<String,Integer>();

        }
    }

    public void addCategory(int category){
        categoryCount[category]++;
    }
    public void addWord(int category, String word){
        Integer c = wordCounts[category].get(word);
        if (c==null) c=0;
        wordCounts[category].put(word, c + 1);
    }
    double classProbability(int category){
        return (categoryCount[category]+PRIOR_COUNT)/(categoryCount[0]+categoryCount[1]+PRIOR_COUNT*2);
    }

    double wordCount(int category, String word){
        Integer c=wordCounts[category].get(word);
        if (c==null) c=0;
        return c+PRIOR_COUNT * classProbability(category);

    }

    public double inferJointProbability(int category, String[] words){
        double p = classProbability(category);
        for (String w:words){
            p=p* wordCount(category, w)/(categoryCount[category]+PRIOR_COUNT);
        }
        return p;
    }

    public double inferLogJointProbability(int category, String[] words){
        double logp = Math.log(categoryCount[category]+PRIOR_COUNT)- Math.log(categoryCount[0] + categoryCount[1] + PRIOR_COUNT * 2);
        for (String w:words){
            logp+= Math.log(wordCount(category, w))-Math.log(categoryCount[category]+PRIOR_COUNT);
        }
        return logp;
    }


    public double inferJointProbabilityWords(String[] words){
        return inferJointProbability(0, words)+inferJointProbability(1,words);
    }

    public double inferCategoryProbability(String[] words){
        double p0 = inferJointProbability(0, words), p1 = inferJointProbability(1,words);
        return p1 / (p0+p1);
    }
    public double inferCategoryProbabilityMoreStable(String[] words){
        double logp0 = inferLogJointProbability(0, words), logp1 = inferLogJointProbability(1, words);


        return 1/(Math.exp(logp0-logp1)+1);
    }



    public String toString(){
        String s="";
        s+="categoryCount: "+categoryCount[0]+", "+categoryCount[1]+"\n";
        for (int i=0;i<2;i++) {
            s+=("category "+i+":\n");
            s+=("---------------------------------------------------------------------\n");
            for (String word : wordCounts[i].keySet()) {
                s+=(word + "~~" + wordCounts[i].get(word)+"\n");
            }
        }
        return s;
    }

    public void trainFolder(int category, String folderName){
        File folder = new File(folderName);
        File[] listOfFiles = folder.listFiles();
        for (File file:listOfFiles){
            train(category, folderName+'\\'+file.getName());
        }
    }

    public void testFolder(String folderName){
        File folder = new File(folderName);
        File[] listOfFiles = folder.listFiles();
        for (File file:listOfFiles){
            test(folderName+'\\'+file.getName());
        }
    }


    public void crossValidationFiles(String negativeFolderName, String positiveFolderName){
        String negPrefix="nod-pdef-words";
        String posPrefix="pdef-words";
        File negativeFolder = new File(negativeFolderName);
        File positiveFolder = new File(positiveFolderName);
        File[] listOfFiles = negativeFolder.listFiles();
        for (File holdOut:listOfFiles) {
            for (File file : listOfFiles) {
                if (file.equals(holdOut)) continue;
                String negName = file.getName();
                String posName = negName.replace(negPrefix, posPrefix);
                train(0, negativeFolderName+'\\'+ negName);
                train(1,positiveFolderName+'\\'+ posName);
            }
            test(negativeFolderName+'\\'+holdOut.getName());
            test(positiveFolderName+'\\'+holdOut.getName().replace(negPrefix,posPrefix));
        }
    }


    public void train(int category, String fileName){
        BufferedReader br=null;
        String line;
        try{
            br=new BufferedReader(new FileReader((fileName)));
            while ((line = br.readLine())!=null){
                String[] words = line.toLowerCase().split(SPLIT_STRING);
                line=line.replace("\u201c","\"");

                //Arrays.sort(words); //sort to help removing duplicate
                //if (words.length==0) continue;
                if (words.length<MAX_SENTENCE_LENGTH) continue; //skip short lines
                addCategory(category);


                Set<String> wordSet= new HashSet<String>(Arrays.asList(Arrays.copyOfRange(words,0,MAX_SENTENCE_LENGTH)));
                if (USE_FIRST_CHAR) wordSet.add(line.substring(0,1));
                if (wordSet.contains("")){
                    if (words.length<=MAX_SENTENCE_LENGTH) continue;
                    wordSet.remove("");
                    wordSet.add(words[MAX_SENTENCE_LENGTH]);
                }
                for (String word:wordSet) if (word.length()>0) addWord(category,word);

//                if (words[0].length()>0) addWord(category, words[0]);
//                for (int i=1;i<words.length;i++){
//                    if (words[i].equals(words[i-1])) continue; //skip duplicate words
//                    addWord(category, words[i]);
//                }
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (br!=null){
                try {
                    br.close();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void test(String fileName){
        BufferedReader br=null;
        String line;
        try{
            br=new BufferedReader(new FileReader((fileName)));
            while ((line = br.readLine())!=null){
                //String[] words = line.toLowerCase().split("[ .\"]+");
                String[] words = line.toLowerCase().split(SPLIT_STRING);
                line=line.replace("\u201c","\"");

                Set<String> wordSet= new HashSet<String>(Arrays.asList(Arrays.copyOfRange(words,0,Math.min(MAX_SENTENCE_LENGTH,words.length))));
                //Arrays.sort(words); //sort to help removing duplicate
                if (USE_FIRST_CHAR) wordSet.add(line.substring(0,1));
                if (words.length==0) continue;
//                System.out.println(inferCategoryProbabilityMoreStable(words)+ ":"+line);
                System.out.println(inferCategoryProbability(wordSet.toArray(new String[wordSet.size()]))+ ":"+line);

            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (br!=null){
                try {
                    br.close();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void testWords(String fileName){
        BufferedReader br=null;
        String line;
        try{
            br=new BufferedReader(new FileReader((fileName)));
            while ((line = br.readLine())!=null){
//                String[] words = line.toLowerCase().split("[ .\"]+");
//                String[] words = line.toLowerCase().split("[ .,\u201c\t\u201d\"]+");

                String[] words = line.toLowerCase().split(SPLIT_STRING);
                line=line.replace("\u201c","\"");

                //Arrays.sort(words); //sort to help removing duplicate
                if (words.length==0) continue;
//                System.out.println(inferCategoryProbabilityMoreStable(words)+ ":"+line);
                Set<String> wordSet= new HashSet<String>(Arrays.asList(Arrays.copyOfRange(words,0,Math.min(MAX_SENTENCE_LENGTH,words.length))));
                if (USE_FIRST_CHAR) wordSet.add(line.substring(0,1));
                System.out.println(inferCategoryProbability(wordSet.toArray(new String[wordSet.size()]))+ ":"+line);
//                for (String word:Arrays.copyOfRange(words,0,Math.min(MAX_SENTENCE_LENGTH,words.length))) System.out.print(showWordsImportance(word)+ " ");
                for (String word:wordSet) System.out.print(showWordsImportance(word)+ " ");

                System.out.println();
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (br!=null){
                try {
                    br.close();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void showMap(){
        System.out.println(wordCounts[0]);
        System.out.println(wordCounts[1]);
    }
    public void showMapSortedByValues(){
        for (int category=0;category<2;category++) {
            SortedSet<Map.Entry<String, Integer>> sortedset = new TreeSet<Map.Entry<String, Integer>>(
                    new Comparator<Map.Entry<String, Integer>>() {
                        @Override
                        public int compare(Map.Entry<String, Integer> e1,
                                           Map.Entry<String, Integer> e2) {
                            int c= e1.getValue().compareTo(e2.getValue());
                            if (c==0) return -e1.getKey().compareTo(e2.getKey());
                            return -e1.getValue().compareTo(e2.getValue());
                        }
                    });
            sortedset.addAll(wordCounts[category].entrySet());
            System.out.println(sortedset);
        }

    }

    public void showInverseMap(){
        for (int category=0;category<2;category++) {
            Map<Integer,TreeSet<String>> inverseMap=new TreeMap<Integer,TreeSet<String>>();

            for (String k : wordCounts[category].keySet()) {
                //Integer newKey=wordCounts[category].get(k);
                Integer newKey=-wordCounts[category].get(k); //make it negative as a hack to easily print in reverse order
                TreeSet<String> newValSet = inverseMap.get(newKey);
                if (newValSet==null) newValSet=new TreeSet<String>();
                newValSet.add(k);
                inverseMap.put(newKey,newValSet);
            }
            System.out.println(inverseMap);
        }
//        System.out.println(wordCounts[0].inverseBidiMap());
//        System.out.println(wordCounts[1].inverseBidiMap());
    }


    public void showWordsImportance(){
        System.out.println(categoryCount[0]+" "+categoryCount[1]);
        SortedSet<Map.Entry<String, List<Double>>> sortedset = new TreeSet<Map.Entry<String, List<Double>>>(
                new Comparator<Map.Entry<String, List<Double>>>() {
                    @Override
                    public int compare(Map.Entry<String, List<Double>> e1,
                                       Map.Entry<String, List<Double>> e2) {
                        int c= e1.getValue().get(0).compareTo(e2.getValue().get(0));
                        if (c==0) return -e1.getKey().compareTo(e2.getKey());
                        return -e1.getValue().get(0).compareTo(e2.getValue().get(0));                    }
                });
        double []classProb = new double[2];
        for (int i=0;i<2;i++) classProb[i] = classProbability(i);
        for (int category=0;category<2;category++) {
            for (String k : wordCounts[category].keySet()) {
                Double[] count=new Double[2];
                for (int i=0;i<2;i++){
                    Integer c=wordCounts[i].get(k);
                    if (c==null) c=0;
                    count[i]= c+PRIOR_COUNT*classProb[i];
                }
                double score = (double)count[1]/count[0]*(categoryCount[0]+PRIOR_COUNT)/(categoryCount[1]+PRIOR_COUNT);
                sortedset.add(new AbstractMap.SimpleEntry<String, List<Double>>(k,Arrays.asList(score,(double)count[0],(double)count[1])));
            }
        }
        System.out.println(sortedset);
    }
    public String showWordsImportance(String word){
        //System.out.println(categoryCount[0]+" "+categoryCount[1]);
        double []classProb = new double[2];
        for (int i=0;i<2;i++) classProb[i] = classProbability(i);
        Double[] count=new Double[2];
        for (int i=0;i<2;i++){
            Integer c=wordCounts[i].get(word);
            if (c==null) c=0;
            count[i]= c+PRIOR_COUNT*classProb[i];
        }
        double score = (double)count[1]/count[0]*(categoryCount[0]+PRIOR_COUNT)/(categoryCount[1]+PRIOR_COUNT);
        return (new AbstractMap.SimpleEntry<String, List<Double>>(word, Arrays.asList(score, (double) count[0], (double) count[1])) + " ");

    }


}
