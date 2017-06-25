package empirical;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import config.ParsingArguments;
import util.FileToLines;
import util.Pair;
import util.WriteLinesToFile;

public class NGramCompare {
	
	public List<HashMap<String, Double>> loadGramProbability(String filename) {
		List<HashMap<String, Double>> gramProbs = new ArrayList<HashMap<String,Double>>();
		List<String> lines = FileToLines.fileToLines(filename);
		System.out.println(lines.size());
		HashMap<String, Double> probs = new HashMap<String, Double>();
		for (String line : lines) {
			if (line.endsWith("-grams:")) {
				if (probs.size() > 0) {
					gramProbs.add(new HashMap<String, Double>(probs));
					probs.clear();
				}
			}
			String[] split = line.split("\t");
			if (split.length >= 2) {
				probs.put(split[1], Double.parseDouble(split[0]));
			}
		}
		gramProbs.add(new HashMap<String, Double>(probs));
		return gramProbs;
	} 
	
	public void compareGrams(int targetGram) {
		String[] projects = {"ant_182","ant_183","ant_184","ant_190","ant_193"};
		String targetName = "all.train.3grams";
		List<List<HashMap<String, Double>>> gramProbsDifferentVersions = new ArrayList<List<HashMap<String,Double>>>();
		List<String> saveLines = new ArrayList<String>();
		String title = "gram\tchange";
		for (String project : projects) {
			title += "\t" + project;
			String filename = config.Configuration.DATA_DIR + File.separator + "Projects" + File.separator + project + File.separator + targetName;
			System.out.println(filename);
			List<HashMap<String, Double>> gramProbs = loadGramProbability(filename);
			
			gramProbsDifferentVersions.add(gramProbs);
		}
		saveLines.add(title);
		HashSet<String> gramSet = new HashSet<String>();
		for (int i = 0; i < gramProbsDifferentVersions.size(); i++) {
			if (gramSet.size() == 0)
				gramSet.addAll(gramProbsDifferentVersions.get(i).get(targetGram - 1).keySet());
			else gramSet.retainAll(gramProbsDifferentVersions.get(i).get(targetGram - 1).keySet());
		}
		System.out.println(gramSet.size());
		String line;
		List<Pair<String,Double>> gramsDifferences = new ArrayList<Pair<String,Double>>();
		for (String gram : gramSet) {
			double gap = 0;
			for (int i = 1; i < gramProbsDifferentVersions.size(); i++) {
				gap += Math.abs(gramProbsDifferentVersions.get(i).get(targetGram - 1).get(gram) 
						- gramProbsDifferentVersions.get(i - 1).get(targetGram - 1).get(gram));
			}
			gramsDifferences.add(new Pair<String,Double>(gram, gap));
		}
		
		Collections.sort(gramsDifferences);
		for (int i = 0; i < gramsDifferences.size(); i++) {
			int index = gramsDifferences.size() - i - 1;
			String gram = gramsDifferences.get(index).getKey();
			line = gram + "\t" + gramsDifferences.get(index).getValue();
			for (int j = 0; j < gramProbsDifferentVersions.size(); j++) 
				line += "\t" + gramProbsDifferentVersions.get(j).get(targetGram - 1).get(gram);
			saveLines.add(line);
			
		}
		
		WriteLinesToFile.writeLinesToFile(saveLines, "grams_compare_3.txt");
	}
	
	public void getGramEntropies() {
		String[] projects = {"ant_182","ant_183","ant_184","ant_190","ant_193","ant_changes"};
		String targetName = "all.train.8grams";
		
		List<String> saveLines = new ArrayList<String>();
		for (String project : projects) {
			saveLines.clear();
			String filename = config.Configuration.DATA_DIR + File.separator + "projects" + File.separator + project + File.separator + targetName;
			System.out.println(filename);
			List<HashMap<String, Double>> gramProbs = loadGramProbability(filename);
			System.out.println(gramProbs.size());
			String line = "";
			int max = gramProbs.get(0).size();
			line = "1-gram";
			for (int i = 2; i <= 8; i++) {
				line += "," + i + "-gram";
				if (gramProbs.get(i - 1).size() > max)
					max = gramProbs.get(i - 1).size();
			}
			saveLines.add(line);
			
			for (int i = 0; i < max; i++) {
				
				if (gramProbs.get(0).size() > i)
					line = gramProbs.get(0).get(i) + "";
				else line = "";
				
				for (int j = 1; j < 8; j++) {
					if (gramProbs.get(j).size() > i)
						line += "," + gramProbs.get(j).get(i);
					else line += ",";
				}
				saveLines.add(line);
			}
			WriteLinesToFile.writeLinesToFile(saveLines, project + ".txt");
		}
	}
	
	public static void main(String[] args) {
		ParsingArguments.parsingArguments(args);
		NGramCompare nc = new NGramCompare();
		nc.getGramEntropies();
//		nc.compareGrams(3);
	}
}
