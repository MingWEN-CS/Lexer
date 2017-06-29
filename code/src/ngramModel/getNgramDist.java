package ngramModel;


import generics.Commit;
import generics.Hunk;
import generics.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.io.File;
import java.util.Calendar;
import java.util.Collections;

import util.FileListUnderDirectory;
import util.FileToLines;
import util.DataReader;
import util.DataWriter;
import java.io.BufferedReader;    
import java.io.IOException;    
import java.io.InputStream;    
import java.io.InputStreamReader;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat; 

/**
 *
 * @author yqtian
 */
public class getNgramDist {
    
    
    
    public static String specialChars =" ,.;()[]{};\"\':<>";
    
    public static List<List<String>> getAddedCodes(String filename){
    

                        
        return null;
    } 

    public static void main(String[] args) throws Exception{
        config.ParsingArguments.parsingArguments(args);     
        String prefix = config.Configuration.DATA_DIR + config.Configuration.PROJECT + File.separator;
       
//        Calendar cal=Calendar.getInstance();
//        11 is Dec!
//        cal.set(2010, 11, 21,0,0);
//        Date StartDate = cal.getTime();  
//        cal.set(2013, 11, 23,13,59);
//        Date EndDate = cal.getTime();
        
        String logOneline = prefix + "logOneline.txt";
        List<String> lines = FileToLines.fileToLines(logOneline);
        List<Pair<String, Long>> commitsSorted = new ArrayList<Pair<String,Long>>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        for (String line : lines) {
            String[] split = line.split("\t");
            long time = format.parse(split[2]).getTime();
            commitsSorted.add(new Pair<String, Long>(split[0], time));
        }
        
        Collections.sort(commitsSorted);
        
        String mapfile = prefix + "allSet/fileinfo.txt";
        List<String> maplines = FileToLines.fileToLines(mapfile);
        HashMap<String, String> map = new HashMap<String, String>();
        for (String line : maplines) {
            String[] split = line.split("\t");
            String after = split[0];

            File file = new File(split[1]);
            String before  = file.getName();
            int pos = before.lastIndexOf(".");
            if (pos > 0) {
                before = before.substring(0, pos);
            }

            map.put(before, after);
        }
        
        List<String> tokensOneFile = new ArrayList<String>();
        
        for (Pair pair : commitsSorted) {
            
            System.out.println("Processing: "+pair.toString());
            
            String filename = map.get(pair.getKey().toString());
            String fileLoc = prefix +"allSet/"+filename+".code.java.tokens";
            
             System.out.println("\tReading: "+fileLoc);
            
            List<String> tokensLine = FileToLines.fileToLines(fileLoc);
            
            for(String line : tokensLine){
                String[] tokens = line.split(" ");
                tokensOneFile.addAll(Arrays.asList(tokens));
            }
        }
        
        String tokensOneFileLoc = prefix + "tokens.txt";
        DataWriter.writeList(tokensOneFileLoc, tokensOneFile);
        
    }
}

