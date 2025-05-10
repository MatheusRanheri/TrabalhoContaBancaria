package com.org.fundatec.conta_bancaria.controller;

import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
import com.org.fundatec.conta_bancaria.model.Agencia;
import com.org.fundatec.conta_bancaria.model.Cliente;
import com.org.fundatec.conta_bancaria.model.Conta;
import com.org.fundatec.conta_bancaria.service.AgenciaService;
import com.org.fundatec.conta_bancaria.service.ClienteService;
import com.org.fundatec.conta_bancaria.service.ContaService;
import jakarta.servlet.http.PushBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @Autowired
    private AgenciaService agenciaService;

    @Autowired
    private ClienteService clienteService;

    @PostMapping( consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Conta> salvarConta(@RequestBody @Valid Conta conta)throws RegistroNaoEncontradoException{
        Integer numeroAgencia = conta.getAgencia().getNumero();
        Agencia agencia = agenciaService.buscaPorNumero(numeroAgencia);
        conta.setAgencia(agencia);

        Integer idCliente = conta.getCliente().getId();
        Cliente cliente = clienteService.busca(idCliente);
        conta.setCliente(cliente);

        Conta novaConta = contaService.cadastrarConta(conta);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaConta);
    }

    @GetMapping(value = { "consulta-numero/{numero}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Conta> buscaPorNumero(@PathVariable("numero") Integer numero)throws RegistroNaoEncontradoException {
        Conta retorno = contaService.busca(numero);
        return ResponseEntity.status(HttpStatus.OK).body(retorno);
    }

    @PutMapping(value = { "conta/{id}" }, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Conta> editar(@PathVariable("id") Integer id, @RequestBody Conta conta) throws RegistroNaoEncontradoException{
        Conta contaNova = contaService.editar(id, conta);
        return ResponseEntity.status(HttpStatus.OK).body(contaNova);
    }

    @DeleteMapping(value = { "{conta}" }, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Conta> remover(@PathVariable("conta") Integer id) throws RegistroNaoEncontradoException{
        contaService.remover(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = {"/{numero}/sacar"}, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Conta> sacar(@PathVariable Integer numero, @RequestBody Conta conta) throws RegistroNaoEncontradoException{
        Conta contaAtualizada = contaService.sacar(conta.getValor(), numero);
        return ResponseEntity.status(HttpStatus.OK).body(contaAtualizada);
    }

    @PostMapping(value = {"/{numero}/depositar"}, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Conta> depositar(@PathVariable Integer numero,@RequestBody Conta conta)throws RegistroNaoEncontradoException{
        Conta contaAtualizada = contaService.depositar(conta.getValor(), numero);
        return ResponseEntity.status(HttpStatus.OK).body(contaAtualizada);
    }
}
