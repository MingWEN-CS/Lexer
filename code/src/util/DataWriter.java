package util;

import generics.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import util.WriteLinesToFile;

/*
 * This class provides a uniform format for saving data stored in different Collections
 * */

public class DataWriter{
	
	/*
	 * write a list of string
	 * */
	public static void writeList(String filename, List<String> data) {
		WriteLinesToFile.writeLinesToFile(data, filename);
	}

	public static void appendList(String filename, List<String> data) {
		WriteLinesToFile.appendLinesToFile(data, filename);
	}
	
	/*
	 * write a HashMap
	 * */
	
	public static void writeHashMapStringInteger(String filename, HashMap<String,Integer> data) {
		List<String> lines = new ArrayList<String>();
		for (String line : data.keySet()) {
			lines.add(line + "\t" + data.get(line));
		}
		WriteLinesToFile.writeLinesToFile(lines, filename);
	}
	
	public static void writeHashMapIntegerString(String filename, HashMap<Integer,String> data) {
		List<String> lines = new ArrayList<String>();
		for (int line : data.keySet()) {
			lines.add(line + "\t" + data.get(line));
		}
		WriteLinesToFile.writeLinesToFile(lines, filename);
	}
	
	public static void writeHashMapList(String filename, HashMap<String, List<String>> data) {
		List<String> lines = new ArrayList<String>();
		for (String key : data.keySet()) {
			String line = key;
			List<String> values = data.get(key);
			for (String value : values) {
				line += "\t" + value;
			}
			lines.add(line);
		}
		WriteLinesToFile.writeLinesToFile(lines, filename);
	}
	
	public static void writeHashMapStringListInteger(String filename, HashMap<String, List<Integer>> data) {
		List<String> lines = new ArrayList<String>();
		for (String key : data.keySet()) {
			String line = key;
			List<Integer> values = data.get(key);
			for (int value : values) {
				line += "\t" + value;
			}
			lines.add(line);
		}
		WriteLinesToFile.writeLinesToFile(lines, filename);
	}
	
	public static void writeHashMapStringSetString(String filename, HashMap<String, HashSet<String>> data) {
		List<String> lines = new ArrayList<String>();
		for (String key : data.keySet()) {
			String line = key;
			HashSet<String> values = data.get(key);
			for (String value : values) {
				line += "\t" + value;
			}
			lines.add(line);
		}
		WriteLinesToFile.writeLinesToFile(lines, filename);
	}
	
	public static void writeListListInteger(String filename, List<List<Integer>> data) {
		List<String> lines = new ArrayList<String>();
		for (int i = 0; i < data.size(); i++) {
			String line = "" + i;
			List<Integer> values = data.get(i);
			for (int value : values) {
				line += "\t" + value;
			}
			lines.add(line);
		}
		WriteLinesToFile.writeLinesToFile(lines, filename);
	}
	
	public static void writeHashMapStringLong(String filename, HashMap<String, Long> data) {
		List<String> lines = new ArrayList<String>();
		for (String line : data.keySet()) {
			lines.add(line + "\t" + data.get(line));
		}
		WriteLinesToFile.writeLinesToFile(lines, filename);
	}
	
	public static void writeHashMapStringString(String filename, HashMap<String, String> data) {
		List<String> lines = new ArrayList<String>();
		for (String line : data.keySet()) {
			lines.add(line + "\t" + data.get(line));
		}
		WriteLinesToFile.writeLinesToFile(lines, filename);
	}
		
	public static void writeHashMapIntegerListInteger(String filename, HashMap<Integer,List<Integer>> data) {
		List<String> lines = new ArrayList<String>();
		for (int id : data.keySet()) {
			List<Integer> values = data.get(id);
			String line = "" + id;
			for (int value : values) {
				line += "\t" + value;
			}
			lines.add(line);
		}
		WriteLinesToFile.writeLinesToFile(lines, filename);
	}
	
	public static void writeHashMapIntegerListPair(String filename, HashMap<Integer,List<Pair<String,Integer>>> data) {
		List<String> lines = new ArrayList<String>();
		for (int id : data.keySet()) {
			List<Pair<String,Integer>> values = data.get(id);
			String line = "" + id;
			for (Pair<String,Integer> value : values) {
				line += "\t" + value.toString();
			}
			lines.add(line);
		}
		WriteLinesToFile.writeLinesToFile(lines, filename);
	}
	
	public static void writeHashMapIntegerListPairInteger(String filename, HashMap<Integer,List<Pair<Integer,Integer>>> data) {
		List<String> lines = new ArrayList<String>();
		for (int id : data.keySet()) {
			List<Pair<Integer,Integer>> values = data.get(id);
			String line = "" + id;
			for (Pair<Integer,Integer> value : values) {
				line += "\t" + value.toString();
			}
			lines.add(line);
		}
		WriteLinesToFile.writeLinesToFile(lines, filename);
	}
}
