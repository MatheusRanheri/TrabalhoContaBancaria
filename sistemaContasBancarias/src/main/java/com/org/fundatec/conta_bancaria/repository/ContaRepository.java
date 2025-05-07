package com.org.fundatec.conta_bancaria.repository;

import com.org.fundatec.conta_bancaria.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContaRepository extends JpaRepository<Conta, Integer> {
    Optional<Conta> findContaByNumero(Integer numero);
}
