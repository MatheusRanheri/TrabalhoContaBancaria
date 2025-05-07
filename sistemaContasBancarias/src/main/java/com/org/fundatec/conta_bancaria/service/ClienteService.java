package com.org.fundatec.conta_bancaria.service;

import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
import com.org.fundatec.conta_bancaria.model.Banco;
import com.org.fundatec.conta_bancaria.model.Cliente;
import com.org.fundatec.conta_bancaria.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente cadastrarCliente(@Valid Cliente cliente){
        clienteRepository.save(cliente);
        return cliente;
    }

    public Cliente busca(Integer id) throws RegistroNaoEncontradoException {
        Optional<Cliente> busca = clienteRepository.findClienteById(id);
        return busca.orElseThrow(() -> new RegistroNaoEncontradoException("id: " + id + " não encontrado"));
    }

    public List<Cliente> buscarNomeAprox(String nome){
        return clienteRepository.findClienteByNomeContains(nome);
    }

    public Cliente editar(Integer id, Cliente cliente) throws RegistroNaoEncontradoException{
        Cliente clienteBusca = this.busca(id);

        clienteBusca.setCpf(cliente.getCpf());
        clienteBusca.setNome(cliente.getNome());

        clienteRepository.save(clienteBusca);

        return clienteBusca;
    }

    public void remover(Integer id){
        Cliente clienteBusca = this.busca(id);
        clienteRepository.delete(clienteBusca);
    }
}
