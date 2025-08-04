package com.org.fundatec.conta_bancaria.repository;

import com.org.fundatec.conta_bancaria.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findClienteById(Integer id);
    List<Cliente> findClienteByNomeContains(String nome);
}
