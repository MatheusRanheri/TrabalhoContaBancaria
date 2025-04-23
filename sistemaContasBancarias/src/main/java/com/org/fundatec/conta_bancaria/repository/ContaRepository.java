package com.org.fundatec.conta_bancaria.repository;

import com.org.fundatec.conta_bancaria.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaRepository extends JpaRepository<Conta, Integer> {
}
