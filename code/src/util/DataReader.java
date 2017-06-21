package util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import generics.Bug;
import generics.Commit;
import generics.Pair;
import generics.Patch;
import util.FileToLines;

public class DataReader {
	
	public static List<String> getListString(String filename) {
		List<String> lines = FileToLines.fileToLines(filename);
		return lines;
	}
	
	public static List<String> getSourceFilesOriginal(String filename) {
		List<String> lines = FileToLines.fileToLines(filename);
		List<String> fileIndex = new ArrayList<String>();
		for (String line : lines) {
			String[] tmp = line.split("\t");
			fileIndex.add(tmp[1]);
		}
		return fileIndex;
	}
	
	public static List<String> getSourceFiles(String filename, HashMap<String,Integer> sourceFileInverseIndex) {
		List<String> lines = FileToLines.fileToLines(filename);
		List<String> fileIndex = new ArrayList<String>();
		int count = 0;
		for (String line : lines) {
			fileIndex.add(line);
			sourceFileInverseIndex.put(line, count);
			count++;
		}
		return fileIndex;
	}
	
	public static Bug readOneBugBugzilla(String filename) {
		Bug bug = null;
		try {
			String xml = "";
			String line;
			BufferedReader br = new BufferedReader(new FileReader(filename));
			while ((line = br.readLine()) != null) 
				xml += line;
			br.close();
			
			Document doc = DocumentHelper.parseText(xml);
			Element item = doc.getRootElement().element("bug");
			
//			System.out.println(item.toString());
//			Element item = rootElt.element("bug");
			String key = item.elementTextTrim("bug_id");
//			System.out.println(key);
			
			String summary = item.elementTextTrim("short_desc");
			if (summary == null) return bug;
			String type = item.elementTextTrim("classification");
			String priority = item.elementTextTrim("bug_severity");
			String status = item.elementTextTrim("bug_status");
			String version = item.elementTextTrim("version");
			String component = item.elementTextTrim("component");
			String labels = item.elementTextTrim("keywords");
			String platform = "";
			String product = "";
			
			List<Element> describs = item.elements("long_desc");
			String description = describs.get(0).elementTextTrim("thetext");
			String format = "yyyy-MM-dd HH:mm:ss z";
			Date reportTime = new SimpleDateFormat(format).parse(item.elementTextTrim("creation_ts"));
			Date resolveTime = null;
			if (item.element("delta_ts") != null && !item.elementTextTrim("delta_ts").isEmpty())
				resolveTime = new SimpleDateFormat(format).parse(item.elementTextTrim("delta_ts"));
			Date updateTime = null;
			if (item.element("delta_ts") != null && !item.elementTextTrim("delta_ts").isEmpty())
				updateTime = new SimpleDateFormat(format).parse(item.elementTextTrim("delta_ts"));
			bug = new Bug(key, type, priority, status, labels, reportTime, resolveTime, updateTime, summary, description);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println(bug.getDescription());
		return bug;
	}
	
	public static Bug readOneBug(String filename) {
		Bug bug = null;
		try {
			String xml = "";
			String line;
			BufferedReader br = new BufferedReader(new FileReader(filename));
			while ((line = br.readLine()) != null) 
				xml += line;
			br.close();
			Document doc = DocumentHelper.parseText(xml);
			Element rootElt = doc.getRootElement().element("channel");
			@SuppressWarnings("unchecked")
			List<Element> items = rootElt.elements("item");
			Element item = items.get(0);
			String key = item.elementTextTrim("key");
			String summary = item.elementTextTrim("summary");
			String description = item.elementTextTrim("description");
			String type = item.elementTextTrim("type");
			String priority = item.elementTextTrim("priority");
			String status = item.elementTextTrim("status");
			String version = item.elementTextTrim("version");
			String component = item.elementTextTrim("component");
			String labels = item.elementTextTrim("labels");
			String platform = "";
			String product = "";
			String format = "EEE, dd MMM yyyy HH:mm:ss z";
			Date reportTime = new SimpleDateFormat(format).parse(item.elementTextTrim("created"));
			Date resolveTime = null;
			if (item.element("resolved") != null && !item.elementTextTrim("resolved").isEmpty())
				resolveTime = new SimpleDateFormat(format).parse(item.elementTextTrim("resolved"));
			Date updateTime = null;
			if (item.element("updated") != null && !item.elementTextTrim("updated").isEmpty())
				updateTime = new SimpleDateFormat(format).parse(item.elementTextTrim("updated"));
			bug = new Bug(key, type, priority, status, labels, reportTime, resolveTime, updateTime, summary, description);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println(bug);
		return bug;
	}
	
	public static HashSet<String> getHistoryFilePath(String loc) {
		HashSet<String> pathes = new HashSet<String>();
		List<String> lines = FileToLines.fileToLines(loc);
		for (String line : lines) {
			if (line.endsWith(".java"))
				pathes.add(line);
		}
		return pathes;
	}
	
	// get the information of commit time and commit author
	// commitAuthor is returned by parameters
	public static HashMap<String,Long> getCommitTimeAndAuthor(String filename, HashMap<String,String> commitAuthor) {
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
		HashMap<String,Long> commitTime = new HashMap<String, Long>();
		List<String> lines = FileToLines.fileToLines(filename);
		try {
			for (String line : lines) {
				String[] splits = line.split("\t");
				Date date = formatter.parse(splits[2]);
				commitTime.put(splits[0], date.getTime());
				commitAuthor.put(splits[0], splits[1]);
			}
		} catch (Exception e) {
			System.out.println("Parsing time error");
			e.printStackTrace();
		}
		return commitTime;
	}
	
	public static HashMap<String,String> readHashMapStringString(String filename) {
		HashMap<String,String> data = new HashMap<String,String>();
		List<String> lines = FileToLines.fileToLines(filename);
		for (String line : lines) {
			String[] tmp = line.split("\t");
			if (tmp.length > 1)
				data.put(tmp[0], tmp[1]);
			else data.put(tmp[0], "empty");
		}
		return data;
	}
	
	public static HashMap<String,Long> readHashMapStringLong(String filename) {
		HashMap<String,Long> data = new HashMap<String,Long>();
		List<String> lines = FileToLines.fileToLines(filename);
		for (String line : lines) {
			String[] tmp = line.split("\t");
			data.put(tmp[0], Long.parseLong(tmp[1]));
		}
		return data;
	}
	
	public static HashMap<String,Integer> readHashMapStringInteger(String filename) {
		HashMap<String,Integer> data = new HashMap<String,Integer>();
		List<String> lines = FileToLines.fileToLines(filename);
		for (String line : lines) {
			String[] tmp = line.split("\t");
			data.put(tmp[0], Integer.parseInt(tmp[1]));
		}
		return data;
	}
	
	public static HashMap<Integer,String> readHashMapIntegerString(String filename) {
		HashMap<Integer,String> data = new HashMap<Integer,String>();
		List<String> lines = FileToLines.fileToLines(filename);
		for (String line : lines) {
			String[] tmp = line.split("\t");
			data.put(Integer.parseInt(tmp[0]), tmp[1]);
		}
		return data;
	}
	
	public static HashMap<Integer,List<Integer>> readHashMapIntegerListInteger(String filename) {
		HashMap<Integer,List<Integer>> data = new HashMap<Integer,List<Integer>>();
		List<String> lines = FileToLines.fileToLines(filename);
		for (String line : lines) {
			String[] tmp = line.split("\t");
			int index = Integer.parseInt(tmp[0]);
			data.put(index, new ArrayList<Integer>());
			for (int i = 1; i < tmp.length; i++) {
				data.get(index).add(Integer.parseInt(tmp[i]));
			}
		}
		return data;
	}
	
	public static HashMap<Integer,List<Integer>> readHashMapIntegerListIntegerFromSet(String filename) {
		HashMap<Integer,List<Integer>> data = new HashMap<Integer,List<Integer>>();
		List<String> lines = FileToLines.fileToLines(filename);
		for (String line : lines) {
			String[] tmp = line.split("\t");
			int index = Integer.parseInt(tmp[0]);
//			System.out.println(line);
			data.put(index, turnStringToListInteger(tmp[1]));
		}
		return data;
	}
	
	public static HashMap<Integer,List<Pair<String,Integer>>> readHashMapIntegerListPair(String filename) {
		HashMap<Integer,List<Pair<String,Integer>>> data = new HashMap<Integer,List<Pair<String,Integer>>>();
		List<String> lines = FileToLines.fileToLines(filename);
		for (String line : lines) {
			String[] tmp = line.split("\t");
			int index = Integer.parseInt(tmp[0]);
			data.put(index, new ArrayList<Pair<String,Integer>>());
			for (int i = 1; i < tmp.length; i++) {
				String[] tmp2 = tmp[i].split(":");
				data.get(index).add(new Pair<String,Integer>(tmp2[0], Integer.parseInt(tmp2[1])));
			}
		}
		return data;
	}
	
	public static HashMap<Integer,List<Pair<Integer,Integer>>> readHashMapIntegerListPairInteger(String filename) {
		HashMap<Integer,List<Pair<Integer,Integer>>> data = new HashMap<Integer,List<Pair<Integer,Integer>>>();
		List<String> lines = FileToLines.fileToLines(filename);
		for (String line : lines) {
			String[] tmp = line.split("\t");
			int index = Integer.parseInt(tmp[0]);
			data.put(index, new ArrayList<Pair<Integer,Integer>>());
			for (int i = 1; i < tmp.length; i++) {
				String[] tmp2 = tmp[i].split(":");
				data.get(index).add(new Pair<Integer,Integer>(Integer.parseInt(tmp2[0]), Integer.parseInt(tmp2[1])));
			}
		}
		return data;
	}
	
	public static HashMap<String, HashSet<String>> readHashMapStringHashSetString(String filename) {
		HashMap<String, HashSet<String>> data = new HashMap<String, HashSet<String>>();
		List<String> lines = FileToLines.fileToLines(filename);
		for (String line : lines) {
			String[] tmp = line.split("\t");
			data.put(tmp[0], new HashSet<String>());
			for (int i = 1; i < tmp.length; i++) {
				data.get(tmp[0]).add(tmp[i]);
			}
		}
		return data;
	}
	
	public static HashMap<String, HashSet<Integer>> readHashMapStringHashSetInteger(String filename) {
		HashMap<String, HashSet<Integer>> data = new HashMap<String, HashSet<Integer>>();
		List<String> lines = FileToLines.fileToLines(filename);
		for (String line : lines) {
			String[] tmp = line.split("\t");
			data.put(tmp[0], new HashSet<Integer>());
			for (int i = 1; i < tmp.length; i++) {
				data.get(tmp[0]).add(Integer.parseInt(tmp[i]));
			}
		}
		return data;
	}
	
	public static HashSet<String> turnStringToHashSetString(String content) {
		content = content.substring(1, content.length() - 1);
		HashSet<String> elements = new HashSet<String>();
		if (!content.contains(",")) {
			if (!content.trim().equals(""))
				elements.add(content);
			return elements;
		}
		String[] tmp = content.split(",");
		
		for (String element : tmp)
			elements.add(element.trim());
		return elements;
	}
	
	public static HashSet<Integer> turnStringToHashSetInteger(String content) {
		HashSet<Integer> elements = new HashSet<Integer>();
		content = content.trim().substring(1, content.length() - 1);
		if (!content.contains(",")) {
			if (!content.trim().equals(""))
				elements.add(Integer.parseInt(content));
			return elements;
		}
		String[] tmp = content.split(",");
//		System.out.println(content);
		for (String element : tmp)
			elements.add(Integer.parseInt(element.trim()));
		return elements;
	}
	
	public static List<Integer> turnStringToListInteger(String content) {
		List<Integer> elements = new ArrayList<Integer>();
		content = content.trim().substring(1, content.length() - 1);
		if (!content.contains(",")) {
			if (!content.trim().equals(""))
				elements.add(Integer.parseInt(content));
			return elements;
		}
		String[] tmp = content.split(",");
		
//		System.out.println(content);
		for (String element : tmp)
			elements.add(Integer.parseInt(element.trim()));
		return elements;
	}
	
	public static HashMap<String,Long> readBuggyCleanFilesForTag(String filename,HashMap<String,HashSet<Integer>> allAllFiles,HashMap<String,HashSet<Integer>> allCleanFiles) {
		HashMap<String,Long> allPredictTime = new HashMap<String,Long>();
		List<String> lines = FileToLines.fileToLines(filename);
		int count = 0;
		String[] tmp;
		String line = "";
		System.out.println(filename + "\t" + lines.size());
		for (int i = 1; i <= lines.size() / 3; i++) {
			line = lines.get(count++);
			tmp = line.split("\t");
			String tag = tmp[0];
			allPredictTime.put(tag, Long.parseLong(tmp[1]));
			allAllFiles.put(tag, turnStringToHashSetInteger(lines.get(count++)));
			allCleanFiles.put(tag, turnStringToHashSetInteger(lines.get(count++)));
		}
		return allPredictTime;
	}
	
	public static HashMap<Integer,Long> readBuggyCleanFiles(String filename, HashMap<Integer,HashSet<String>> allInducingChanges,HashMap<Integer,HashSet<String>> allFixingChanges,HashMap<Integer,HashSet<Integer>> allAllFiles,HashMap<Integer,HashSet<Integer>> allCleanFiles) {
		HashMap<Integer,Long> allPredictTime = new HashMap<Integer,Long>();
		List<String> lines = FileToLines.fileToLines(filename);
		int count = 0;
		String[] tmp;
		String line = "";
		System.out.println(filename + "\t" + lines.size());
		for (int i = 1; i <= lines.size() / 5; i++) {
			line = lines.get(count++);
			tmp = line.split("\t");
			int phaseId = Integer.parseInt(tmp[0]);
			allPredictTime.put(phaseId, Long.parseLong(tmp[1]));
			allInducingChanges.put(phaseId, turnStringToHashSetString(lines.get(count++).split("\t")[1]));
			allFixingChanges.put(phaseId, turnStringToHashSetString(lines.get(count++).split("\t")[1]));
			allAllFiles.put(phaseId, turnStringToHashSetInteger(lines.get(count++).split("\t")[1]));
			allCleanFiles.put(phaseId, turnStringToHashSetInteger(lines.get(count++).split("\t")[1]));
		}
		return allPredictTime;
	}
	
	public static HashMap<Integer,Pair<Integer,Integer>> readHashMapIntegerPairIntegerInteger(String filename) {
		HashMap<Integer,Pair<Integer,Integer>> hunkAddDeleteLine = new HashMap<Integer,Pair<Integer,Integer>>();
		List<String> lines = FileToLines.fileToLines(filename);
		for (String line : lines) {
			String[] tmp = line.split("\t");
			hunkAddDeleteLine.put(Integer.parseInt(tmp[0]), new Pair<Integer,Integer>(Integer.parseInt(tmp[1]),Integer.parseInt(tmp[2])));
		}
		return hunkAddDeleteLine;
	}
	
	public static String getFromUrl(String urlString){
		try {
			StringBuffer html = new StringBuffer();
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStreamReader isr = new InputStreamReader(conn.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String temp;
			while ((temp = br.readLine()) != null){
				html.append(temp).append('\n');
			}
			br.close();
			isr.close();
			return html.toString();
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static Commit readOneCommitWithHunkGit(String file) {
		Commit commit = null;
		
		//Variables for commit
		String changeSet = "";
		String authorName = "";
		String authorEmail = "";
		Date date = null;
		String description = "";
		String line;
		
		// Variables for patch
		String preFile = null;
		String postFile = null;
		String command = null;
		Patch patch = null;
		List<String> content = new ArrayList<String>();
		List<String> rawCommit = FileToLines.fileToLines(file);
		int size = rawCommit.size();
		if (size == 0) return commit;
		int index = 0;
		try {
			boolean isContent = false;
			line = rawCommit.get(index++);
			changeSet = line.substring(7);
			line = rawCommit.get(index++);
			if (!line.startsWith("Author")) line = rawCommit.get(index++);	
			int nameStart = line.indexOf(":") + 1;
			int nameEnd = line.indexOf("<");
			int emailEnd = line.indexOf(">");
			//System.out.println(line);
			authorName = line.substring(nameStart, nameEnd).trim();
			authorEmail = line.substring(nameEnd + 1,emailEnd);
			line = rawCommit.get(index++);
			date = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z").parse(line.substring(line.indexOf(":") + 1).trim());
			
//			line = rawCommit.get(index++);
			description = "";
			while (index < rawCommit.size() && line != null && !line.startsWith("diff")) {
				line = rawCommit.get(index);
				if (!line.startsWith("diff"))
					description += "\n" + line;
				index++;
				if (index >= rawCommit.size()) break;
			}
			description.trim();
			commit = new Commit(changeSet,authorName,authorEmail,date,description);
			while (line != null) {
				if (line.startsWith("diff")) {
					if ( patch != null) {
						patch.addContent(content);
						commit.addPatch(patch);
						content.clear();
						isContent = false;
					}
					command = line;
				} else if (line.startsWith("---")) {
//					System.out.println(line);
					try {
						preFile = line.substring(6);
					} catch (IndexOutOfBoundsException e) {
						System.out.println("Exception\t" + line);
					}
				} else if (line.startsWith("+++")) {
					try {
						postFile = line.substring(6);
					} catch (IndexOutOfBoundsException e) {
						System.out.println("Exception\t" + line);
					}
					patch = new Patch(command,preFile,postFile);
//					System.out.println(patch);
					isContent = true;
				} else if (line.startsWith("index")) {
					String[] tmp = line.split(" ");
//					System.out.println(tmp[1]);
//					if (tmp[1].contains("..")) System.out.println(true);
					String[] tmp2 = tmp[1].split("\\.\\.");
//					System.out.println(tmp2[1]);
					String preIndex = tmp2[0];
					String postIndex = tmp2[1];
					tmp = command.split(" ");
					command = "diff -r " + preIndex + " -r " + postIndex + " " + tmp[2];	
				}
				else {
					if (isContent) content.add(line);
				}
				if (index >= rawCommit.size()) break;
				line = rawCommit.get(index++);
//				System.out.println(size + "\t" + index);
			}
			if (command != null) {
				patch = new Patch(command,preFile,postFile);
				patch.addContent(content);
				commit.addPatch(patch);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return commit;
	}
	
	
	public static void main(String[] args) {
//		String filename = Configures.DATA_DIR + Configures.PROJECT + File.separator + "bugs" + File.separator + "23066.xml";
//		readOneBugBugzilla(filename);
	}
}
