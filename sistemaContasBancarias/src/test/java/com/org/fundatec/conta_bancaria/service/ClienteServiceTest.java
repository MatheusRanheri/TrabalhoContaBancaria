package com.org.fundatec.conta_bancaria.service;

import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
import com.org.fundatec.conta_bancaria.model.Cliente;
import com.org.fundatec.conta_bancaria.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testaCadastro(){
        Cliente clienteNovo = build(1, "111", "Matheus");

        when(clienteRepository.save(eq(clienteNovo))).thenReturn(clienteNovo);

        Cliente retorno = clienteService.cadastrarCliente(clienteNovo);

        verify(clienteRepository, times(1)).save(eq(clienteNovo));
        assertThat("Não retornou o cliente correto", retorno.getId(), equalTo(clienteNovo.getId()));

    }

    @Test
    void testaCadastroNegativo(){
        Cliente clienteNovo = build(1, "111", "Matheus");

        when(clienteRepository.save(any(Cliente.class))).thenThrow(new RegistroNaoEncontradoException("Falha ao salvar cliente"));

        try {
            clienteService.cadastrarCliente(clienteNovo);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("Falha ao salvar cliente"));
        }

        verify(clienteRepository, times(1)).save(any());
    }

    @Test
    void testaBuscaId(){
        Optional<Cliente> clienteNovo = Optional.of(build(1, "111", "Matheus"));

        when(clienteRepository.findClienteById(1)).thenReturn(clienteNovo);

        Cliente retorno = clienteService.busca(1);

        verify(clienteRepository, times(1)).findClienteById(1);
        assertThat("Não retornou correto", retorno.getId(), equalTo(clienteNovo.get().getId()));

    }

    @Test
    void testaBuscaIdNegativo(){
        when(clienteRepository.findClienteById(1)).thenReturn(Optional.empty());

        try {
            clienteService.busca(1);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("id: 1 não encontrado"));
        }

        verify(clienteRepository, times(1)).findClienteById(1);

    }

    @Test
    void testaBuscarNomeAprox(){
        Cliente cliente1 = build(1, "222", "Matheus");
        Cliente cliente2 = build(2, "333", "Martim");
        List<Cliente> lista = Arrays.asList(cliente1, cliente2);

        when(clienteRepository.findClienteByNomeContains("Ma")).thenReturn(lista);

        List<Cliente> retorno = clienteService.buscarNomeAprox("Ma");

        verify(clienteRepository, times(1)).findClienteByNomeContains("Ma");
        assertThat("Não retornou correto", retorno.get(0).getNome(), equalTo("Matheus"));

    }

    @Test
    void testaBuscarNomeAproxNegativo(){
        when(clienteRepository.findClienteByNomeContains("Ma")).thenReturn(Collections.emptyList());

        try {
            clienteService.buscarNomeAprox("Ma");
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("Nenhum cliente encontrado com esse nome"));
        }

        verify(clienteRepository, times(1)).findClienteByNomeContains("Ma");

    }

    @Test
    void testarEditar(){
        Cliente clienteExistente = build(1, "222", "Matheus");

        Cliente  clienteAtualizado = build(null, "999", "Marcos");

        Cliente clienteSalvo = build(1, "999", "Marcos");

        when(clienteRepository.findClienteById(1)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        Cliente resultado = clienteService.editar(1, clienteAtualizado);

        verify(clienteRepository, times(1)).findClienteById(1);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
        assertThat("Id incorreto", resultado.getId(), equalTo(1));
        assertThat("cpf incorreto", resultado.getCpf(), equalTo("999"));
        assertThat("Nome incorreto", resultado.getNome(), equalTo("Marcos"));

    }

    @Test
    void testarEditarNegativo(){
        Cliente clienteAtualizado = build(null, "999", "Matheus");

        when(clienteRepository.findClienteById(1)).thenReturn(Optional.empty());

        try {
            clienteService.editar(1, clienteAtualizado);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("id: 1 não encontrado"));
        }

        verify(clienteRepository, times(1)).findClienteById(1);
        verify(clienteRepository, times(0)).save(any());

    }

    @Test
    void testarRemover(){
        Optional<Cliente> clienteNovo = Optional.of(build(1, "111", "Matheus"));

        when(clienteRepository.findClienteById(1)).thenReturn(clienteNovo);

        clienteService.remover(1);
        verify(clienteRepository, times(1)).findClienteById(1);
        verify(clienteRepository, times(1)).delete(clienteNovo.get());

    }

    @Test
    void testarRemoverNegativo(){
        when(clienteRepository.findClienteById(1)).thenReturn(Optional.empty());

        try {
            clienteService.remover(1);
            assertThat("Não falhou", false);
        }catch(RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("id: 1 não encontrado"));
        }

        verify(clienteRepository, times(1)).findClienteById(1);
        verify(clienteRepository, times(0)).delete(any());

    }

    private Cliente build(Integer id, String cpf, String nome){
        return new Cliente( id,  cpf,  nome);
    }
}
