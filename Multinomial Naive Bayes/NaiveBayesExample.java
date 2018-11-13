package mnb;


import mnb.NaiveBayes;
import mnb.NaiveBayesKnowledgeBase;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NaiveBayesExample {

    
     
    public static String[] readLines(URL url) throws IOException {

        Reader fileReader = new InputStreamReader(url.openStream(), Charset.forName("UTF-8"));
        List<String> lines;
        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            lines = new ArrayList<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines.toArray(new String[lines.size()]);
    }
    public static void listFilesForFolder(final File folder) {
    	Map<String, URL> trainingFiles = new HashMap<>();
        for (final File fileEntry : folder.listFiles()) {
        	
        	trainingFiles.put("ham", NaiveBayesExample.class.getResource(fileEntry.getName()));
        }
    }
    public static void listFilesForFolder1(final File folder1) {
    	Map<String, URL> trainingFiles = new HashMap<>();
        for (final File fileEntry : folder1.listFiles()) {
        	
        	trainingFiles.put("spam", NaiveBayesExample.class.getResource(fileEntry.getName()));
        }
    }
     public static void listFilesForFolder2(final File folder1) {
    	Map<String, URL> trainingFiles = new HashMap<>();
        for (final File fileEntry : folder2.listFiles()) {
        	nb.predict(toString.(fileentry));
        }
    }
    
    public static void main(String[] args) throws IOException {
        //map of dataset files
    	final File folder= new File("C:/Users/FARHEEN/workspace/train/ham/");
    	listFilesForFolder(folder);
    	final File folder1 = new File("C:/Users/FARHEEN/workspace/train/spam/");
    	listFilesForFolder1(folder1);
    	
        Map<String, URL> trainingFiles = new HashMap<>();
        
        
        //loading examples in memory
        Map<String, String[]> trainingExamples = new HashMap<>();
        for(Map.Entry<String, URL> entry : trainingFiles.entrySet()) {
            trainingExamples.put(entry.getKey(), readLines(entry.getValue()));
        }
        
        //train classifier
        NaiveBayes nb = new NaiveBayes();
        nb.setChisquareCriticalValue(6.63); //0.01 pvalue
        nb.train(trainingExamples);
        
        //get trained classifier knowledgeBase
        NaiveBayesKnowledgeBase knowledgeBase = nb.getKnowledgeBase();
        
        nb = null;
        trainingExamples = null;
        
        
        //Use classifier
        nb = new NaiveBayes(knowledgeBase);
        final File folder2 = new File("C:/Users/FARHEEN/workspace/test/spam/");
    	listFilesForFolder2(folder2);

    }
    
}
