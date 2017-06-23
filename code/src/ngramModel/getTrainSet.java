package ngramModel;


import generics.Commit;
import generics.Hunk;
import generics.Pair;

import java.util.ArrayList;
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
public class getTrainSet {
    
    
    
    public static String specialChars =" ,.;()[]{};\"\':<>";
    
    public static List<List<String>> getAddedCodes(String filename){
    
        System.out.println("Processing " + filename);
        Commit commit = DataReader.readOneCommitWithHunkGit(filename);
        
        if (commit == null) 
            return null;
//        if (!(commit.commitTime.after(StartDate) && commit.commitTime.before(EndDate))){
//            System.out.println("Erro Processing " + filename);
//            return null;
//        }
        
        List<Hunk> hunks = commit.getAllHunks();
//        System.out.println(hash + "\t" + hunks.size());
        List<List<String>> addedLinesCommit = new ArrayList<>();
        for (Hunk hunk : hunks) {
            if (hunk == null) continue;
            String file = hunk.sourceFile;
            // this place ignored a case when the file was deleted 
            // If the file is deleted, the postFile is /dev/null
            if (hunk.sourceFile == null || !file.endsWith(".java")) continue;
            
            List<String> codes = hunk.codes;
            List<Integer> marks = hunk.mark;
            List<String> addedLinesHunk = new ArrayList<String>();
            
            boolean continiousComments = false;
            // 1 for last line is a commnet and it has not finished
            // 0 for other cases
            
            for (int i = 0; i < codes.size();i++){
                
                String code = codes.get(i);
                int mark = marks.get(i);

                int isComment = isComment(code);
                
                //if(isComment==0)
                //    code = removeInlineComment(code);

                if(continiousComments == false){
                    
                    if(isComment == 0){
                        if(mark == 1)
                        addedLinesHunk.add(removeInlineComment(code));
                    }
                    else if(isComment == 1 || isComment == 3 )
                       continue;
                    else if (isComment == 2){
                        continiousComments = true;
                        continue;
                    }
                    
                }
                else if(continiousComments){
                    
                    if(isComment ==3)
                        continiousComments = false;
                    
                   continue;
                   
                }
                
            }
                
            addedLinesCommit.add(addedLinesHunk);
        }
                        
        return addedLinesCommit;
    } 
        public static int isComment(String string){
        
        string = string.trim();
        int len = string.length();
//      System.out.println(string);
        int isComment = 0;
        
        //0 for non-commnets
        //1 for a complete commnets i.e.// or /* */
        //2 for a beginning of comments i.e. /*
        //3 for a end of comments i.e. */
        //it wont find pattern like: "code // comments"
        
        if(len < 2)
            isComment = 0;
        else if(string.charAt(0)=='/' && string.charAt(1)=='/')
            isComment = 1;
        else if(string.charAt(0)=='/' && string.charAt(1)=='*' && string.charAt(len-2) == '*' && string.charAt(len-1) == '/')
            isComment = 1;
        else if(string.charAt(0)=='/' && string.charAt(1)=='*')
            isComment = 2;
        else if(string.charAt(len-2) == '*' && string.charAt(len-1) == '/')
            isComment = 3;
        else 
            isComment = 0;
            
        return isComment;
    }
    
    public static String removeInlineComment(String string){
        string = string.trim();
	    int idx = -1;
        idx= string.indexOf("//");
        if(idx != -1 && (idx==0 || ( idx!=0 && string.charAt(idx-1)==' '))){
            System.out.println("// Before:"+string);
            string = string.substring(0, idx);
            System.out.println("End:"+string);
        }
        
        idx= string.indexOf("<!--");
        if(idx != -1){
            System.out.println("<!-- Before:"+string);
            string = string.substring(0, idx);
            System.out.println("End:"+string);
        }
        
        idx= string.indexOf("/*");
        if(idx != -1){
            System.out.println("/* Before:"+string);
            string = string.substring(0, idx);
            System.out.println("End:"+string);
        }
        
        
        
        return string;
    }
    
    public static void main(String[] args) throws Exception{
        config.ParsingArguments.parsingArguments(args);     
        String prefix = config.Configuration.DATA_DIR + config.Configuration.PROJECT + File.separator;
        String logRawDir = prefix + "logRaw/";
        System.out.println(logRawDir);
        List<String> files = FileListUnderDirectory.getFileListUnder(logRawDir, ".txt");
        
        String trainDir = prefix+ "trainInput/";
        String testDir = prefix + "testInput/";
        File dir = new File(trainDir);
        if (!dir.exists()) dir.mkdir();
        dir = new File(testDir);
        if (!dir.exists()) dir.mkdir();
        
//        Calendar cal=Calendar.getInstance();
//        //11 is Dec!
//        cal.set(2010, 11, 21,0,0);
//        Date StartDate = cal.getTime();  
//        cal.set(2013, 11, 23,13,59);
//        Date EndDate = cal.getTime();
        
        String logOneline = prefix + "logOneline.txt";
        List<String> lines = FileToLines.fileToLines(logOneline);
        HashMap<String, Long> commitTime = new HashMap<String, Long>();
        List<Pair<String, Long>> commitsSorted = new ArrayList<Pair<String,Long>>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        for (String line : lines) {
        	String[] split = line.split("\t");
        	long time = format.parse(split[2]).getTime();
        	commitTime.put(split[0], time);
        	commitsSorted.add(new Pair<String, Long>(split[0], time));
        }
        
        Collections.sort(commitsSorted);
        // using 90% of the commits for training and the remaining 10% for testing
        long cutTime = commitsSorted.get(commitsSorted.size() / 10 * 9).getValue();
        
        for (String filename : files) {
            File file = new File(filename);
            String outputFilePrefix  = file.getName();
            int pos = outputFilePrefix.lastIndexOf(".");
            if (pos > 0) {
                outputFilePrefix = outputFilePrefix.substring(0, pos);
            }
            if (!commitTime.containsKey(outputFilePrefix))
            	continue;
            List<List<String>> strings = getTrainSet.getAddedCodes(filename);
            if(strings!=null){
                for(int i = 0; i < strings.size(); i++){
                	String output = trainDir + outputFilePrefix + ".java";
                	if (commitTime.get(outputFilePrefix) > cutTime)
                		output = testDir + outputFilePrefix + ".java";
                    System.out.println("Writing to file "+ output);
                    DataWriter.appendList(output, strings.get(i));  
                }
            }
        }
        Process p = Runtime.getRuntime().exec("./pythonScript/walk.py "+ trainDir + " " + prefix + "trainSet");
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
        p = Runtime.getRuntime().exec("./pythonScript/walk.py "+ testDir + " " + prefix + "testSet");
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
}

