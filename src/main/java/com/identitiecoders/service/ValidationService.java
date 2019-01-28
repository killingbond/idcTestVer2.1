package com.identitiecoders.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.identitiecoders.repository.RulesRepository;

@Service
public class ValidationService {

	@Autowired
	private RulesRepository rulesRepository;

	String line = "";

	public Integer validationExtension(String filename) {
		String extension = FilenameUtils.getExtension(filename);
		if (!extension.equalsIgnoreCase("csv")) {
			return 1;
		}
		return 0;
	}

	public Integer validationRules(String rules) {
		String[] data = rules.split(",");
		for (String s : data) {
			try {
				Long.valueOf(s);
			} catch (Exception e) {
				return 1;
			}
			if (Long.valueOf(s) != null) {
				if (rulesRepository.findOne(Long.valueOf(s)) == null) {
					return 1;
				}
			} else {
				return 1;
			}
		}
		return 0;
	}
}
