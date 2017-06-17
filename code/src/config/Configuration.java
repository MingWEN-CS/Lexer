package config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Configuration {
	
	public static String data_dir = "C:/Users/mwena/Documents/Research/2017SummerA";
	
	public static HashMap<String, List<String>> projectVersions = new HashMap<String, List<String>>();
	public static HashMap<String, String> tagToCommits = new HashMap<String, String>();
	
	static {
		String[] versions = {"ant_182","ant_183","ant_184","ant_190","ant_193"};
		List<String> antVersions = Arrays.asList(versions);
		projectVersions.put("Ant", antVersions);
		tagToCommits.put("ant_182", "cd4c13a");
		tagToCommits.put("ant_183", "25f34e9");
		tagToCommits.put("ant_184", "7105ec7");
		tagToCommits.put("ant_190", "b47c505");
		tagToCommits.put("ant_193", "281db9e");
	}
	
}
