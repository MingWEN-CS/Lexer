package config;

import java.util.List;

import util.FileToLines;

public class ParsingArguments {
	
	public static void parsingArguments(String[] args) {
//		System.out.println(args.length);
		String pro = config.Configuration.PROJECT;
		List<String> lines = FileToLines.fileToLines("config");
		if (lines.size() > 0) {
			config.Configuration.DATA_DIR = lines.get(0);
		}
		
		
		boolean isUpdate = false;
		if (args.length == 0) {
			showHelp();
		} else {
			int i = 0;
			while (i < args.length) {
				if (args[i].equals("-p")) {
					pro = args[++i];
				} else if (args[i].equals("-update")) {
					isUpdate = args[++i].equals("true");
				}
				i++;
			}			
		}
		
		if (pro.length() > 0)
			config.Configuration.PROJECT = pro;

		config.Configuration.updateData = isUpdate;
	}
	
	private static void showHelp() {
		System.out.println("Using the default setting..");
	}
}
