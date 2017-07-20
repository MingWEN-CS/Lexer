package ngramModel;

import generics.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.io.File;
import java.util.Collections;

import util.FileToLines;
import util.DataWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

/**
 *
 * @author yqtian
 */
public class getNgramDistSingleVersion {

    public static String specialChars = " ,.;()[]{};\"\':<>";

    public static List<String> getNgramDist(List<String> tokens, int n) {

        System.out.println("Calc Dist for n = " + n);
        HashMap<String, Integer> count = new HashMap<>();
        HashMap<String, Integer> firstLoc = new HashMap<>();
        HashMap<String, Integer> lastLoc = new HashMap<>();

        for (int i = 0; i <= tokens.size() - n; i++) {
            String token = tokens.subList(i, i + n).toString();

            if (count.containsKey(token) == false) {
                count.put(token, 1);
                firstLoc.put(token, i);
            } else {
                int value = count.get(token);
                count.put(token, value + 1);
            }
            lastLoc.put(token, i);

        }

        List<String> distList = new ArrayList<>();
        for (String token : count.keySet()) {

            int countValue = count.get(token);
            double dist = 0.0;
            if (countValue != 1) {
                dist = (lastLoc.get(token) - firstLoc.get(token)) / (double) (countValue - 1);
            }
            distList.add(token + "\t" + dist);
        }
        return distList;
    }

    public static void main(String[] args) throws Exception {
        config.ParsingArguments.parsingArguments(args);
        String prefix = config.Configuration.DATA_DIR + config.Configuration.PROJECT + File.separator;
        
        String singleVersionTokensDir = prefix + "singleVersionTokens/";
        String repo= config.Configuration.Repo_DIR+config.Configuration.PROJECT + File.separator;
         
        File dir = new File(singleVersionTokensDir);
        if (!dir.exists()){ 
            Process p = Runtime.getRuntime().exec("./pythonScript/walk.py "+ repo + " " + singleVersionTokensDir);
            try {     
                 InputStream fis=p.getInputStream(); 
                 InputStreamReader isr=new InputStreamReader(fis);
                 BufferedReader br=new BufferedReader(isr);    
                 String line=null;  
                 while((line=br.readLine())!=null)    
                 {    
                     System.out.println(line);    
                 }    
            } catch (IOException e) {    
                 e.printStackTrace();    
            }
        }
        
        String mapfile = singleVersionTokensDir + "fileinfo.txt";
        List<String> lines = FileToLines.fileToLines(mapfile);
        List<Pair<String, String>> filePairs = new ArrayList<Pair<String, String>>();
        
        for (String line : lines) {
            String[] split = line.split("\t");
            filePairs.add(new Pair<String, String>(split[0], split[1]));
        }

        
        List<String> tokensOneList = new ArrayList<String>();

        for (Pair pair : filePairs) {

            System.out.println("Processing: " + pair.toString());
            
            String filename = pair.getKey().toString();
            
            String fileLoc = singleVersionTokensDir + filename + ".code.java.tokens";

            System.out.println("\tReading: " + fileLoc);

            List<String> tokensLine = FileToLines.fileToLines(fileLoc);

            for (String line : tokensLine) {
                String[] tokens = line.split(" ");
                tokensOneList.addAll(Arrays.asList(tokens));
            }
        }

        String tokensOneFileLoc = prefix + "singleVersionTokens.txt";
        DataWriter.writeList(tokensOneFileLoc, tokensOneList);

        String outputDir = prefix + "singleVersionDist/";
        dir = new File(outputDir);
        if (!dir.exists()) dir.mkdir();
        for (int i = 1; i <= 10; i++) {
            List<String> dist = getNgramDist(tokensOneList, 8);
            String outputName = outputDir + "singleVersion_tokenDist_" + i + ".txt";
            DataWriter.writeList(outputName, dist);
        }
    }
}
