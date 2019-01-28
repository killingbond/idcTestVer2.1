package com.identitiecoders.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.identitiecoders.domain.Rules;
import com.identitiecoders.repository.RulesRepository;

@Service
public class CsvService {

	@Autowired
	private RulesRepository rulesRepository;

	public List<String> retrive(String filepath, String rules) throws IOException {
		List<String> list = new ArrayList<>();

		HashMap<Integer, String> dataHash = new HashMap<>();
		String[] column = scanColumn(filepath);
		String line = "";
		int i = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			while ((line = br.readLine()) != null) {
				if (i == 0) {
					list.add(line.replaceAll(";", " "));
				} else {
					dataHash.put(i, line);
				}
				/*
				 * String[] data = line.split(";"); String ruleSolver = ruleSolver(data, column,
				 * rules); String adit = ruleSolver; String lul = adit; int adit1 = 1;
				 */
				i++;

			}
		} catch (FileNotFoundException e) {
		}
		String[] splitRules = rules.split(",");
		for (String rulesId : splitRules) {
			for (Iterator<Map.Entry<Integer, String>> idx = dataHash.entrySet().iterator(); idx.hasNext();) {
				Map.Entry<Integer, String> vl = idx.next();
				Rules r = rulesRepository.findOne(Long.valueOf(rulesId));
				String stringRules = r.getStringRules();
				String[] data = vl.getValue().split(";");
				String ruleResolver = ruleResolver(data, column, stringRules);
				ScriptEngineManager sem = new ScriptEngineManager();
				ScriptEngine se = sem.getEngineByName("JavaScript");
				try {
					if (!se.eval(ruleResolver).equals(true)) {
						idx.remove();
					}
				} catch (ScriptException e) {
					return new ArrayList<>();
				}
			}
		}

		for (Map.Entry<Integer, String> entry : dataHash.entrySet()) {
			String result = entry.getValue().replaceAll(";"," ");
			list.add(result);
		}
		return list;
	}

	public String[] scanColumn(String filepath) throws IOException {
		String line = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			while ((line = br.readLine()) != null) {
				String[] column = line.split(";");
				return column;
			}
		} catch (FileNotFoundException e) {
			return null;
		}
		return null;
	}

	public String ruleResolver(String[] data, String[] column, String rules) {
		String result = "";
		Boolean bool = true;
		String fixRules = fixRules(rules);
		String[] dataRules = fixRules.split(" ");
		for (int u = 0; u < dataRules.length; u++) {
			for (int i = 0; i < column.length; i++) {
				if (dataRules[u].equalsIgnoreCase(column[i])) {
					if (dataRules[u + 1].equalsIgnoreCase("startsWith")) {
						result += "\"" + data[i].substring(0, 1).toLowerCase() + "\"" + " ";
					} else if (dataRules[u + 1].equalsIgnoreCase("endsWith")) {
						result += "\"" + data[i].charAt(data[i].length() - 1) + "\"" + " ";
					} else {
						result += "\"" + data[i].toLowerCase() + "\"" + " ";
					}
					bool = false;
				}
			}
			if (dataRules[u].equalsIgnoreCase("equals") || dataRules[u].equalsIgnoreCase("=")
					|| dataRules[u].equalsIgnoreCase("startsWith") || dataRules[u].equalsIgnoreCase("endsWith")) {
				result += "== ";
			} else if (dataRules[u].equalsIgnoreCase("AND")) {
				result += "&& ";
			} else if (dataRules[u].equalsIgnoreCase("OR")) {
				result += "|| ";
			} else if (bool == true) {
				result += dataRules[u].toLowerCase() + " ";
			} else {
				bool = true;
			}

		}
		return result;
	}

	public String fixRules(String rules) {
		String a = "";
		String[] str = rules.split("");
		for (String strCv : str) {
			if (strCv.equals("(")) {
				a += " ( ";
			} else if (strCv.equals(")")) {
				a += " ) ";
			} else {
				a += strCv;
			}
		}
		return a;
	}
}
