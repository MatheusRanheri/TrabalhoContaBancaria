package com.org.fundatec.conta_bancaria.repository;

import com.org.fundatec.conta_bancaria.model.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BancoRepository extends JpaRepository<Banco, Integer> {
    Optional<Banco> findBancoById(Integer id);
    Optional<Banco> findBancoByCodigo(Integer codigo);
    List<Banco> findBancoByNomeContains(String nome);
}
