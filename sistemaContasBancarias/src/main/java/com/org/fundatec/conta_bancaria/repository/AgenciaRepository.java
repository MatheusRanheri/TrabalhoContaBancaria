package com.org.fundatec.conta_bancaria.repository;

import com.org.fundatec.conta_bancaria.model.Agencia;
import com.org.fundatec.conta_bancaria.model.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgenciaRepository extends JpaRepository<Agencia, Integer>{
    Optional<Agencia> findAgenciaById(Integer id);
    Optional<Agencia> findAgenciaCodigoBanco(Integer codigo);
    Optional<Agencia> findAgenciaByNumero(Integer numero);
}
