package com.org.fundatec.conta_bancaria.service;

import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
import com.org.fundatec.conta_bancaria.model.Agencia;
import com.org.fundatec.conta_bancaria.model.Banco;
import com.org.fundatec.conta_bancaria.repository.AgenciaRepository;
import com.org.fundatec.conta_bancaria.repository.BancoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
public class AgenciaServiceTest {
    @Mock
    private AgenciaRepository agenciaRepository;

    @Mock
    private BancoRepository bancoRepository;

    @InjectMocks
    private AgenciaService agenciaService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testaCadastro(){
        Banco bancoNovo = buildB(1, 222, "Banco bom", "1234");
        Agencia agenciaNova = buildA(1, 111, "Agencia boa", bancoNovo);

        when(agenciaRepository.save(eq(agenciaNova))).thenReturn(agenciaNova);

        Agencia retorno = agenciaService.cadastrarAgencia(agenciaNova);

        verify(agenciaRepository, times(1)).save(eq(agenciaNova));
        assertThat("Não retornou a agencia correta", retorno.getId(), equalTo(agenciaNova.getId()));
    }

    @Test
    void testaCadastroNegativo(){
        Banco bancoNovo = buildB(1, 222, "Banco bom", "1234");
        Agencia agenciaNova = buildA(1, null, "Agencia boa", bancoNovo);

        when(agenciaRepository.save(any(Agencia.class))).thenThrow(new RegistroNaoEncontradoException("Falha ao salvar agencia"));

        try {
            agenciaService.cadastrarAgencia(agenciaNova);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("Falha ao salvar agencia"));
        }

        verify(agenciaRepository, times(1)).save(any());
    }

    @Test
    void testaBuscaId(){
        Banco bancoNovo = buildB(1, 222, "Banco bom", "1234");
        Optional<Agencia> agencia = Optional.of(buildA(1, 111, "Agencia boa", bancoNovo));

        doAnswer(invocationOnMock -> agencia).when(agenciaRepository).findById(1);

        Agencia retorno = agenciaService.busca(1);

        verify(agenciaRepository, times(1)).findById(1);
        assertThat("Não retornou correto", retorno.getId(), equalTo(agencia.get().getId()));
    }

    @Test
    void testaBuscaIdNegativo(){

        doAnswer(invocationOnMock -> Optional.empty()).when(agenciaRepository).findById(1);

        try {
            agenciaService.busca(1);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("id: 1 não encontrado"));
        }

        verify(agenciaRepository, times(1)).findById(1);
    }

    @Test
    void testaBuscarNumero(){
        Banco bancoNovo = buildB(1, 222, "Banco bom", "1234");
        Optional<Agencia> agencia = Optional.of(buildA(1, 111, "Agencia boa", bancoNovo));

        doAnswer(invocationOnMock -> agencia).when(agenciaRepository).findByNumero(111);

        Agencia retorno = agenciaService.buscaPorNumero(111);

        verify(agenciaRepository, times(1)).findByNumero(111);
        assertThat("Não retornou correto", retorno.getNumero(), equalTo(agencia.get().getNumero()));
    }

    @Test
    void testaBuscarNumeroNegativo(){
        doAnswer(invocationOnMock -> Optional.empty()).when(agenciaRepository).findByNumero(111);

        try {
            agenciaService.buscaPorNumero(111);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("numero: 111 não encontrado"));
        }

        verify(agenciaRepository, times(1)).findByNumero(111);
    }

    @Test
    void testaBuscaBanco(){
        Banco bancoNovo = buildB(1, 222, "Banco bom", "1234");
        Optional<Agencia> agencia = Optional.of(buildA(1, 111, "Agencia boa", bancoNovo));

        doAnswer(invocationOnMock -> agencia).when(agenciaRepository).findByBancoCodigo(222);

        Agencia retorno = agenciaService.buscaPorBanco(222);

        verify(agenciaRepository, times(1)).findByBancoCodigo(222);
        assertThat("Não retornou correto", retorno.getBanco(), equalTo(agencia.get().getBanco()));
    }

    @Test
    void testaBuscaBancoNegativo(){
        doAnswer(invocationOnMock -> Optional.empty()).when(agenciaRepository).findByBancoCodigo(222);

        try {
            agenciaService.buscaPorBanco(222);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("banco: 222 não encontrado"));
        }

        verify(agenciaRepository, times(1)).findByBancoCodigo(222);
    }


    @Test
    void testaEditar(){
        Banco bancoNovo = buildB(1, 222, "Banco bom", "1234");
        Agencia agenciaNova = buildA(1, 111, "Agencia boa", bancoNovo);

        Banco bancoNovo2 = buildB(2, 333, "Banco ruim", "4321");
        Agencia agenciaAtualizada = buildA(null, 999, "Agencia ruim", bancoNovo2);

        Agencia agenciaEsperada = buildA(1, 999, "Agencia ruim", bancoNovo2);

        when(agenciaRepository.findById(1)).thenReturn(Optional.of(agenciaNova));
        when(bancoRepository.findByCodigo(bancoNovo2.getCodigo())).thenReturn(Optional.of(bancoNovo2));
        when(agenciaRepository.save(any(Agencia.class))).thenReturn(agenciaEsperada);

        Agencia resultado = agenciaService.editar(1, agenciaAtualizada);

        verify(agenciaRepository, times(1)).findById(1);
        verify(bancoRepository, times(1)).findByCodigo(bancoNovo2.getCodigo());
        verify(agenciaRepository, times(1)).save(any(Agencia.class));
        assertThat("Id incorreto", resultado.getId(), equalTo(1));
        assertThat("Banco incorreto", resultado.getBanco().getCodigo(), equalTo(333));
    }

    @Test
    void testaEditarNegarivo(){
        Banco bancoNovo = buildB(1, 222, "Banco bom", "1234");
        Agencia agenciaNova = buildA(1, 111, "Agencia boa", bancoNovo);

        Banco bancoNovo2 = buildB(2, 333, "Banco ruim", "4321");
        Agencia agenciaAtualizada = buildA(null, 999, "Agencia ruim", bancoNovo2);

        when(agenciaRepository.findById(1)).thenReturn(Optional.of(agenciaNova));
        when(bancoRepository.findByCodigo(bancoNovo2.getCodigo())).thenReturn(Optional.empty());

        try {
            agenciaService.editar(1, agenciaAtualizada);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("Banco não encontrado"));
        }

        verify(agenciaRepository, times(1)).findById(1);
        verify(bancoRepository, times(1)).findByCodigo(bancoNovo2.getCodigo());
        verify(agenciaRepository, times(0)).save(any());

    }

    @Test
    void testaRemover(){
        Banco bancoNovo = buildB(1, 222, "Banco bom", "1234");
        Optional<Agencia> agencia = Optional.of(buildA(1, 111, "Agencia boa", bancoNovo));

        when(agenciaRepository.findById(1)).thenReturn(agencia);

        agenciaService.remover(1);
        verify(agenciaRepository, times(1)).findById(1);
        verify(agenciaRepository, times(1)).delete(agencia.get());

    }

    @Test
    void testaRemoverNegativo(){
        when(agenciaRepository.findById(1)).thenReturn(Optional.empty());

        try {
            agenciaService.remover(1);
            assertThat("Não falhou", false);
        }catch(RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("id: 1 não encontrado"));
        }

        verify(agenciaRepository, times(1)).findById(1);
        verify(agenciaRepository, times(0)).delete(any());
    }

    private Agencia buildA(Integer id, Integer numero , String nome, Banco banco){
        return new Agencia(id,numero,nome,banco);
    }
    private Banco buildB(Integer id, Integer codigo, String nome, String cnpj){
        return new Banco( id,  codigo,  nome,  cnpj);
    }

}
