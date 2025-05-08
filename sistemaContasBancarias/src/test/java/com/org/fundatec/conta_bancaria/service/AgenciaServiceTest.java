package com.org.fundatec.conta_bancaria.service;

import com.org.fundatec.conta_bancaria.model.Agencia;
import com.org.fundatec.conta_bancaria.model.Banco;
import com.org.fundatec.conta_bancaria.repository.AgenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
public class AgenciaServiceTest {
    @Mock
    private AgenciaRepository agenciaRepository;

    @InjectMocks
    private AgenciaService agenciaService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testaCadastro(){
        Agencia cadastrar = buildA(1, 111, "Agencia boa", new Banco(1, 222, "Banco bom", "1234"));

        Banco bancoNovo = buildB(1, 222, "Banco bom", "1234");
    }

    private Agencia buildA(Integer id, Integer numero , String nome, Banco banco){
        return new Agencia(id,numero,nome,banco);
    }
    private Banco buildB(Integer id, Integer codigo, String nome, String cnpj){
        return new Banco( id,  codigo,  nome,  cnpj);
    }

}
