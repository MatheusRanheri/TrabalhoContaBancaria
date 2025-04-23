package com.org.fundatec.conta_bancaria.repository;

import com.org.fundatec.conta_bancaria.model.Agencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgenciaRepository extends JpaRepository<Agencia, Integer>{
}
