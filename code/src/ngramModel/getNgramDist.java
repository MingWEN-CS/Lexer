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

    public static String specialChars = " ,.;()[]{};\"\':<>";

    public static List<String> getNgramDist(List<String> tokens, int n) {

        System.out.println("Calc Dist for n = ");
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

        String logOneline = prefix + "logOneline.txt";
        List<String> lines = FileToLines.fileToLines(logOneline);
        List<Pair<String, Long>> commitsSorted = new ArrayList<Pair<String, Long>>();
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
            String before = file.getName();
            int pos = before.lastIndexOf(".");
            if (pos > 0) {
                before = before.substring(0, pos);
            }

            map.put(before, after);
        }

        List<String> tokensOneList = new ArrayList<String>();

        for (Pair pair : commitsSorted) {

            System.out.println("Processing: " + pair.toString());
            
            if(map.get(pair.getKey())==null){
                System.out.println("Waring: No such commit in fileinfo.txt");
                continue;
            }
            String filename = map.get(pair.getKey().toString());
            
            String fileLoc = prefix + "allSet/" + filename + ".code.java.tokens";

            System.out.println("\tReading: " + fileLoc);

            List<String> tokensLine = FileToLines.fileToLines(fileLoc);

            for (String line : tokensLine) {
                String[] tokens = line.split(" ");
                tokensOneList.addAll(Arrays.asList(tokens));
            }
        }

        String tokensOneFileLoc = prefix + "tokens.txt";
        DataWriter.writeList(tokensOneFileLoc, tokensOneList);

//        List<String> a = new ArrayList<String>();
//        a.add("A");
//        a.add("B");
//        a.add("C");
//        a.add("D");
//        a.add("1");
//        a.add("2");
//        a.add("3");
//        a.add("4");
//        a.add("A");
//        a.add("B");
//        a.add("C");
//        a.add("D");
        for (int i = 1; i <= 10; i++) {
            List<String> dist = getNgramDist(tokensOneList, 8);
            String outputName = prefix + "tokenDist_" + i + ".txt";
            DataWriter.writeList(outputName, dist);
        }
    }
}
