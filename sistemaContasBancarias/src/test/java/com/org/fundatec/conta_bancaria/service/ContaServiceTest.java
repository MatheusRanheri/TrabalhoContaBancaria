package com.org.fundatec.conta_bancaria.service;

import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
import com.org.fundatec.conta_bancaria.model.Agencia;
import com.org.fundatec.conta_bancaria.model.Banco;
import com.org.fundatec.conta_bancaria.model.Cliente;
import com.org.fundatec.conta_bancaria.model.Conta;
import com.org.fundatec.conta_bancaria.repository.AgenciaRepository;
import com.org.fundatec.conta_bancaria.repository.ClienteRepository;
import com.org.fundatec.conta_bancaria.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private AgenciaRepository agenciaRepository;

    @InjectMocks
    private ContaService contaService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testaCadastrar(){
        Cliente cliente = buildCliente(1, "111", "Matheus");
        Agencia agencia = buildAgencia(1, 123, "Agencia boa", buildBanco(1, 222, "Banco bom", "1234"));
        Conta contaNova = buildConta(null, 999, 100.0, 100.0, cliente, agencia);

        Conta contaSalva = buildConta(1, 9999, 100.0, 100.0, cliente, agencia);

        when(contaRepository.save(contaNova)).thenReturn(contaSalva);

        Conta resultado = contaService.cadastrarConta(contaNova);

        verify(contaRepository, times(1)).save(contaNova);
        assertThat("Cliente incorreto", resultado.getCliente(), equalTo(cliente));
        assertThat("Agência incorreta", resultado.getAgencia(), equalTo(agencia));
    }

    @Test
    void testaCadastrarNegativo(){
        Cliente cliente = buildCliente(1, "111", "Matheus");
        Agencia agencia = buildAgencia(1, 123, "Agencia boa", buildBanco(1, 222, "Banco bom", "1234"));
        Conta contaNova = buildConta(null, 999, 100.0, 100.0, cliente, agencia);

         when(contaRepository.save(contaNova)).thenThrow(new RegistroNaoEncontradoException("Erro ao salvar"));

         try {
             contaService.cadastrarConta(contaNova);
             assertThat("Não falhou", false);
         }catch (RegistroNaoEncontradoException e){
             assertThat("Mensagem incorreta", e.getMessage(), equalTo("Erro ao salvar"));
         }

         verify(contaRepository, times(1)).save(contaNova);
    }

    @Test
    void buscaNumero(){
        Conta conta = buildConta(1, 111, 100.0, 100.0, buildCliente(1, "222", "Matheus"), buildAgencia(1, 333, "Agencia boa", buildBanco(1, 444, "Banco bom", "1234")));

        when(contaRepository.findByNumero(111)).thenReturn(Optional.of(conta));

        Conta retorno = contaService.busca(111);

        verify(contaRepository, times(1)).findByNumero(111);
        assertThat("Numero incorreto", retorno.getNumero(), equalTo(111));
    }

    @Test
    void buscaNumeroNegativo(){
        when(contaRepository.findByNumero(9999)).thenReturn(Optional.empty());

        try {
            contaService.busca(9999);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("numero: 9999 não encontrado"));
        }

        verify(contaRepository, times(1)).findByNumero(9999);
    }

    @Test
    void buscaId(){
        Conta conta = buildConta(1, 111, 100.0, 100.0, buildCliente(1, "222", "Matheus"), buildAgencia(1, 333, "Agencia boa", buildBanco(1, 444, "Banco bom", "1234")));

        when(contaRepository.findById(1)).thenReturn(Optional.of(conta));

        Conta retorno = contaService.buscaPorId(1);

        verify(contaRepository, times(1)).findById(1);
        assertThat("Numero incorreto", retorno.getId(), equalTo(1));
    }



    @Test
    void buscaIdNegativo(){
        when(contaRepository.findById(2)).thenReturn(Optional.empty());

        try {
            contaService.buscaPorId(2);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("id: 2 não encontrado"));
        }

        verify(contaRepository, times(1)).findById(2);

    }

    @Test
    void testaEditar(){
        Cliente cliente = buildCliente(1, "111", "Matheus");
        Agencia agencia = buildAgencia(1, 222, "Agencia boa", buildBanco(1, 333, "Banco bom", "1234"));

        Conta contaExistente = buildConta(1, 444, 100.0, 100.0, cliente, agencia);
        Conta novaConta = buildConta(null, 9999, 200.0, 300.0, cliente, agencia);
        Conta contaEditada = buildConta(1, 9999, 200.0, 300.0, cliente, agencia);

        when(contaRepository.findById(1)).thenReturn(Optional.of(contaExistente));
        when(contaRepository.save(any(Conta.class))).thenReturn(contaEditada);

        Conta retorno = contaService.editar(1, novaConta);

        verify(contaRepository, times(1)).findById(1);
        verify(contaRepository, times(1)).save(any(Conta.class));
        assertThat("Numero incorreto", retorno.getNumero(), equalTo(9999));
        assertThat("Saldo incorreto", retorno.getSaldo(), equalTo(300.0));
    }

    @Test
    void testaEditarNegativo(){
        Conta novaConta = buildConta(null, 111, 200.0, 300.0, buildCliente(1, "222", "Matheus"), buildAgencia(1, 333, "Agencia", buildBanco(1, 444, "Banco", "1234")));

        when(contaRepository.findById(111)).thenReturn(Optional.empty());

        try {
            contaService.editar(111, novaConta);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("id: 111 não encontrado"));

            verify(contaRepository, times(1)).findById(111);
            verify(contaRepository, times(0)).save(any(Conta.class));
        }

    }

    @Test
    void testaRemover(){
        Conta conta = buildConta(1, 111, 100.0, 100.0, buildCliente(1, "222", "Matheus"), buildAgencia(1, 333, "Agencia", buildBanco(1, 444, "Banco", "1234")));

        when(contaRepository.findById(1)).thenReturn(Optional.of(conta));

        contaService.remover(1);

        verify(contaRepository, times(1)).findById(1);
        verify(contaRepository, times(1)).delete(conta);

    }

    @Test
    void testaRemoverNegativo(){
        when(contaRepository.findById(999)).thenReturn(Optional.empty());

        try {
            contaService.remover(999);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("id: 999 não encontrado"));

            verify(contaRepository, times(1)).findById(999);
            verify(contaRepository, times(0)).delete(any(Conta.class));
        }
    }

    @Test
    void testaSacar(){
        Conta conta = buildConta(1, 111, 0.0, 500.0, buildCliente(1, "222", "Matheus"), buildAgencia(1, 333, "Agência", buildBanco(1, 444, "Banco", "1234")));

        when(contaRepository.findByNumero(111)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        Conta retorno = contaService.sacar(200.0, 111);

        assertThat(retorno.getSaldo(), equalTo(300.0));
        verify(contaRepository, times(1)).findByNumero(111);
        verify(contaRepository, times(1)).save(conta);
    }

    @Test
    void testaSacarNegativoContaNaoEncontrada(){
        when(contaRepository.findByNumero(9999)).thenReturn(Optional.empty());

        try {
            contaService.sacar(100.0, 9999);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("Conta não encontrada"));
        }

        verify(contaRepository, times(1)).findByNumero(9999);
        verify(contaRepository, times(0)).save(any());
    }

    @Test
    void testaSacarNegativoValorMaiorSaldo(){
        Conta conta = buildConta(1, 111, 0.0, 100.0, buildCliente(1, "222", "Matheus"), buildAgencia(1, 333, "Agência", buildBanco(1, 444, "Banco", "1234")));

        when(contaRepository.findByNumero(111)).thenReturn(Optional.of(conta));

        try {
            contaService.sacar(200.0, 111);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("O saque é maior que o saldo da conta"));
        }

        verify(contaRepository, times(1)).findByNumero(111);
        verify(contaRepository, times(0)).save(any());
    }

    @Test

    void testaSacarNegativoValorInvalido(){
        Conta conta = buildConta(1, 111, 0.0, 300.0, buildCliente(1, "222", "Matheus"), buildAgencia(1, 333, "Agência", buildBanco(1, 444, "Banco", "1234")));

        when(contaRepository.findByNumero(111)).thenReturn(Optional.of(conta));

        try {
            contaService.sacar(0.0, 111);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("O saque não pode ser menor ou igual a zero"));
        }

        verify(contaRepository, times(1)).findByNumero(111);
        verify(contaRepository, times(0)).save(any());

    }

    @Test
    void testaDepositar(){
        Conta conta = buildConta(1, 111, 0.0, 100.0, buildCliente(1, "222", "João"), buildAgencia(1, 333, "Agência", buildBanco(1, 444, "Banco", "123")));

        Conta contaAtualizada = buildConta(1, 111, 0.0, 150.0, conta.getCliente(), conta.getAgencia());


        when(contaRepository.findByNumero(111)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenReturn(contaAtualizada);

        Conta retorno = contaService.depositar(50.0, 111);

        assertThat(retorno.getSaldo(), equalTo(150.0));
        verify(contaRepository, times(1)).findByNumero(111);
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    @Test
    void testaDepositarContaNaoEncontradaNegativo(){
        when(contaRepository.findByNumero(111)).thenReturn(Optional.empty());

        try {
            contaService.depositar(100.0, 111);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("Conta não encontrada"));

            verify(contaRepository).findByNumero(111);
            verify(contaRepository, times(0)).save(any());
        }
    }

    @Test
    void testaDepositoValorInvalidoNegativo(){
        Conta conta = buildConta(1, 111, 0.0, 200.0, buildCliente(1, "222", "João"), buildAgencia(1, 333, "Agência", buildBanco(1, 444, "Banco", "123")));

        when(contaRepository.findByNumero(111)).thenReturn(Optional.of(conta));

        try {
            contaService.depositar(0.0, 111);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("O deposito não pode ser zero ou menor que zero"));

            verify(contaRepository).findByNumero(111);
            verify(contaRepository, times(0)).save(any());
        }
    }

    private Cliente buildCliente(Integer id, String cpf, String nome){
        return new Cliente( id,  cpf,  nome);
    }

    private Agencia buildAgencia(Integer id, Integer numero, String nome, Banco banco){
        return new Agencia( id,  numero,  nome,  banco);
    }

    private Banco buildBanco(Integer id, Integer codigo, String nome, String cnpj){
        return new Banco( id,  codigo,  nome,  cnpj);
    }

    private Conta buildConta(Integer id, Integer numero, Double valor, Double saldo, Cliente cliente, Agencia agencia){
        return new Conta(id, numero, valor, saldo, agencia, cliente);
    }
}
