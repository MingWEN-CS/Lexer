package ngramModel;


import generics.Commit;
import generics.Hunk;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.io.File;
import java.util.Calendar;
import util.FileListUnderDirectory;
import util.DataReader;
import util.DataWriter;
import java.io.BufferedReader;    
import java.io.IOException;    
import java.io.InputStream;    
import java.io.InputStreamReader; 

/**
 *
 * @author yqtian
 */
public class getTrainSet {
    
    
    
    public static String specialChars =" ,.;()[]{};\"\':<>";
    
    public static List<List<String>> getAddedCodes(String filename, Date StartDate, Date EndDate){
    
        System.out.println("Processing " + filename);
        Commit commit = DataReader.readOneCommitWithHunkGit(filename);
        
        if (commit == null) 
            return null;
        if (!(commit.commitTime.after(StartDate) && commit.commitTime.before(EndDate))){
            System.out.println("Erro Processing " + filename);
            return null;
        }
        
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
                
                if(continiousComments == false){
                    
                    if(isComment == 0){
                        if(mark == 1)
                        addedLinesHunk.add(code);
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
    
    
    public static void main(String[] args) throws Exception{
        config.ParsingArguments.parsingArguments(args);     
        String prefix = config.Configuration.DATA_DIR + config.Configuration.PROJECT + File.separator;
        String logRawDir = prefix + "logRaw/";
        List<String> files = FileListUnderDirectory.getFileListUnder(logRawDir, ".txt");
        
        String outputDir = prefix+ "input/";
        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdir();
        
        Calendar cal=Calendar.getInstance();
        //11 is Dec!
        cal.set(2010, 11, 21,0,0);
        Date StartDate = cal.getTime();
        
      
        cal.set(2013, 11, 23,13,59);
        Date EndDate = cal.getTime();
        
        for (String filename : files) {
        	
            File file = new File(filename);
            String outputFilePrefix  = file.getName();
            int pos = outputFilePrefix.lastIndexOf(".");
            
            if (pos > 0) {
                outputFilePrefix = outputFilePrefix.substring(0, pos);
            }
            
            List<List<String>> strings = getTrainSet.getAddedCodes(filename, StartDate, EndDate);
            
            if(strings!=null){
                for(int i = 0;i<strings.size();i++){

                    String output = outputDir+outputFilePrefix+".java";
                    System.out.println("Writing to file "+ output);
                    DataWriter.appendList(output, strings.get(i));  
                }
            }
        }
        Process p = Runtime.getRuntime().exec("./pythonScript/walk.py "+outputDir+" "+prefix+"trainSet");

        try    
         {    
            // //执行命令    
            //  p = Runtime.getRuntime().exec(cmd);    
            //取得命令结果的输出流    
             InputStream fis=p.getInputStream();    
            //用一个读输出流类去读    
             InputStreamReader isr=new InputStreamReader(fis);    
            //用缓冲器读行    
             BufferedReader br=new BufferedReader(isr);    
             String line=null;    
            //直到读完为止    
            while((line=br.readLine())!=null)    
             {    
                 System.out.println(line);    
             }    
         }    
        catch (IOException e)    
         {    
             e.printStackTrace();    
         } 

	}
}

