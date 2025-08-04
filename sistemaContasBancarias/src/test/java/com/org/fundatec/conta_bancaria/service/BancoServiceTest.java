package com.org.fundatec.conta_bancaria.service;

import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
import com.org.fundatec.conta_bancaria.model.Banco;
import com.org.fundatec.conta_bancaria.repository.BancoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class BancoServiceTest {

    @Mock
    private BancoRepository bancoRepository;

    @InjectMocks
    private BancoService bancoService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testaCadastroBanco(){
        Banco bancoNovo = build(1, 222, "Banco bom", "1234");

        when(bancoRepository.save(eq(bancoNovo))).thenReturn(bancoNovo);

        Banco retorno = bancoService.cadastrarBanco(bancoNovo);

        verify(bancoRepository, times(1)).save(eq(bancoNovo));
        assertThat("Não retornou o banco correto", retorno.getId(), equalTo(bancoNovo.getId()));
    }

    @Test
    void testaCadastroBancoNegativo(){
        Banco bancoNovo = build(1, 222, "Banco bom", "1234");

        when(bancoRepository.save(any(Banco.class))).thenThrow(new RegistroNaoEncontradoException("Falha ao salvar banco"));

        try {
            bancoService.cadastrarBanco(bancoNovo);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("Falha ao salvar banco"));
        }

        verify(bancoRepository, times(1)).save(any());
    }

    @Test
    void testaBuscaId(){
        Optional<Banco> bancoNovo = Optional.of(build(1, 222, "Banco bom", "1234"));

        doAnswer(invocationOnMock -> bancoNovo).when(bancoRepository).findById(1);

        Banco retorno = bancoService.busca(1);

        verify(bancoRepository, times(1)).findById(1);
        assertThat("Não retornou correto", retorno.getId(), equalTo(bancoNovo.get().getId()));
    }

    @Test
    void testaBuscaIdNegativo(){
        doAnswer(invocationOnMock -> Optional.empty()).when(bancoRepository).findById(1);

        try {
            bancoService.busca(1);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("id: 1 não encontrado"));
        }

        verify(bancoRepository, times(1)).findById(1);
    }

    @Test
    void testaBuscarNomeAprox(){
        Banco banco1 = build(1, 222, "Banco bom", "1234");
        Banco banco2 = build(2, 333, "Banco bom demais", "4321");
        List<Banco> lista = Arrays.asList(banco1, banco2);

        when(bancoRepository.findBancoByNomeContains("bom")).thenReturn(lista);

        List<Banco> retorno = bancoService.buscarNomeAprox("bom");

        verify(bancoRepository, times(1)).findBancoByNomeContains("bom");
        assertThat("Não retornou correto", retorno.get(0).getNome(), equalTo("Banco bom"));
    }

    @Test
    void testarBuscarNomeAproxNegativo(){
        Banco bancoNovo = build(1, 222, "Banco bom", "1234");

        when(bancoRepository.findBancoByNomeContains("Nao")).thenReturn(Collections.emptyList());

        try {
            bancoService.buscarNomeAprox("Nao");
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("Nenhum banco encontrado com esse nome"));
        }

        verify(bancoRepository, times(1)).findBancoByNomeContains("Nao");
    }

    @Test
    void testaBuscarCodigo(){
        Optional<Banco> bancoNovo = Optional.of(build(1, 222, "Banco bom", "1234"));

        doAnswer(invocationOnMock -> bancoNovo).when(bancoRepository).findByCodigo(222);

        Banco retorno = bancoService.buscarPorCodigo(222);

        verify(bancoRepository, times(1)).findByCodigo(222);
        assertThat("Não retornou correto", retorno.getCodigo(), equalTo(bancoNovo.get().getCodigo()));

    }

    @Test
    void testaBuscarCodigoNegativo(){
        Optional<Banco> bancoNovo = Optional.of(build(1, 222, "Banco bom", "1234"));

        doAnswer(invocationOnMock -> Optional.empty()).when(bancoRepository).findByCodigo(333);

        try {
            bancoService.buscarPorCodigo(333);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("codigo: 333 não encontrado"));
        }

        verify(bancoRepository, times(1)).findByCodigo(333);

    }
    @Test
    void testaBuscarTodos(){
        List<Banco> bancos = List.of(
          new Banco(1, 222, "Banco bom", "1234"),
          new Banco(2, 333, "Banco ruim", "4321")
        );

        when(bancoRepository.findAll()).thenReturn(bancos);

        List<Banco> retorno = bancoService.buscarTodos();

        verify(bancoRepository, times(1)).findAll();
        assertThat("Nome incorreto", retorno.get(0).getNome(), equalTo("Banco bom"));
    }

    @Test
    void testaBuscarTodosNegativo(){
        Banco bancoNovo = build(1, 222, "Banco bom", "1234");

        when(bancoRepository.findAll()).thenReturn(Collections.emptyList());

        try {
            bancoService.buscarTodos();
            assertThat("Não falhou", false);
        }catch(RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("Nenhum banco encontrado"));

            verify(bancoRepository, times(1)).findAll();
        }
    }

    @Test
    void testarEditar(){
        Banco bancoExistente = build(1, 222, "Banco bom", "1234");

        Banco  bancoAtualizado = build(null, 999, "Banco mediano", "5678");

        Banco bancoSalvo = build(1, 999, "Banco mediano", "5678");

        when(bancoRepository.findById(1)).thenReturn(Optional.of(bancoExistente));
        when(bancoRepository.save(any(Banco.class))).thenReturn(bancoSalvo);

        Banco resultado = bancoService.editar(1, bancoAtualizado);

        verify(bancoRepository, times(1)).findById(1);
        verify(bancoRepository, times(1)).save(any(Banco.class));
        assertThat("Id incorreto", resultado.getId(), equalTo(1));
        assertThat("Codigo incorreto", resultado.getCodigo(), equalTo(999));
        assertThat("Nome incorreto", resultado.getNome(), equalTo("Banco mediano"));

    }

    @Test
    void testarEdiçãoNegativo(){
        Banco bancoAtualizada = build(null, 999, "Banco mediano", "5678");

        when(bancoRepository.findById(1)).thenReturn(Optional.empty());

        try {
            bancoService.editar(1, bancoAtualizada);
            assertThat("Não falhou", false);
        }catch (RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("id: 1 não encontrado"));
        }

        verify(bancoRepository, times(1)).findById(1);
        verify(bancoRepository, times(0)).save(any());

    }

    @Test
    void testaRemover(){
        Optional<Banco> bancoNovo = Optional.of(build(1, 222, "Banco bom", "1234"));

        when(bancoRepository.findById(1)).thenReturn(bancoNovo);

        bancoService.remover(1);
        verify(bancoRepository, times(1)).findById(1);
        verify(bancoRepository, times(1)).delete(bancoNovo.get());

    }

    @Test
    void testaRemoverNegativo(){
        when(bancoRepository.findById(1)).thenReturn(Optional.empty());

        try {
            bancoService.remover(1);
            assertThat("Não falhou", false);
        }catch(RegistroNaoEncontradoException e){
            assertThat("Mensagem incorreta", e.getMessage(), equalTo("id: 1 não encontrado"));
        }

        verify(bancoRepository, times(1)).findById(1);
        verify(bancoRepository, times(0)).delete(any());

    }

    private Banco build(Integer id, Integer codigo, String nome, String cnpj){
        return new Banco( id,  codigo,  nome,  cnpj);
    }
}
