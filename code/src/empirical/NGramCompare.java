package empirical;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.sun.java.swing.plaf.windows.WindowsTreeUI.CollapsedIcon;

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
			String filename = config.Configuration.data_dir + File.separator + "projects" + File.separator + project + File.separator + targetName;
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
	
	public static void main(String[] args) {
		NGramCompare nc = new NGramCompare();
		nc.compareGrams(3);
	}
}
