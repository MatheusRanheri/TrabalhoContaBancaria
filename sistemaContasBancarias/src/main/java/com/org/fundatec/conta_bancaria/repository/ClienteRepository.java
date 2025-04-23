package com.org.fundatec.conta_bancaria.repository;

import com.org.fundatec.conta_bancaria.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
}
