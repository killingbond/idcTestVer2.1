package com.identitiecoders.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Rules {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String stringRules;

	public Rules() {
		super();
	}

	public Rules(Long id, String stringRules) {
		super();
		this.id = id;
		this.stringRules = stringRules;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStringRules() {
		return stringRules;
	}

	public void setStringRules(String stringRules) {
		this.stringRules = stringRules;
	}

}
