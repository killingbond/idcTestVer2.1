package com.identitiecoders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.identitiecoders.domain.Rules;

public interface RulesRepository extends JpaRepository<Rules, Long> {

}
