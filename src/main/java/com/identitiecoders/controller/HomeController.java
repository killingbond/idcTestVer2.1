package com.identitiecoders.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.identitiecoders.domain.Rules;
import com.identitiecoders.repository.RulesRepository;
import com.identitiecoders.service.ValidationService;

import com.identitiecoders.service.CsvService;

@RestController
public class HomeController {

	@Autowired
	private RulesRepository rulesRepository;

	@Autowired
	private CsvService csvService;

	@Autowired
	private ValidationService validationService;

	private static String url = "C:\\Users\\Public\\";

	@GetMapping("/home")
	public ModelAndView homepage() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("getAllRules", rulesRepository.findAll());
		modelAndView.setViewName("home");
		return modelAndView;
	}

	@GetMapping("createrules")
	public ModelAndView createPage() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("rules", new Rules());
		modelAndView.setViewName("create-rules");
		return modelAndView;
	}

	@PostMapping("/save")
	public ModelAndView saveRules(Rules rules) {
		rulesRepository.save(rules);
		return new ModelAndView("redirect:" + "/home");
	}

	@GetMapping("/delete/{id}")
	public ModelAndView delete(@PathVariable Long id) {
		rulesRepository.delete(id);
		return new ModelAndView("redirect:" + "/home");
	}

	@PostMapping("/result")
	public List<String> result(@RequestParam("file") MultipartFile file, @RequestParam("rules") String rules)
			throws IllegalStateException, IOException {

		List<String> list = new ArrayList<>();
		String fileName = file.getOriginalFilename();
		// file validation
		if (fileName.equals("")) {
			list.add("please upload csv file");
			return list;
		}
		file.transferTo(new java.io.File(url, fileName));
		String filepath = url + fileName;
		// validation extension
		Integer validEx = validationService.validationExtension(fileName);
		if (validEx == 1) {
			list.add("extension must csv");
			return list;
		}
		int validRules = validationService.validationRules(rules);
		if (validRules != 0) {
			list.add("rule not exist");
			return list;
		}
		if (rules.equalsIgnoreCase("")) {
			list.add("insert the rules");
			return list;
		}
		list = csvService.retrive(filepath, rules);

		return list;

	}

}
