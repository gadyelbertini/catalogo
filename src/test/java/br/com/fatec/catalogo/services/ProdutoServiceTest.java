package br.com.fatec.catalogo.services;

import br.com.fatec.catalogo.models.ProdutoModel;
import br.com.fatec.catalogo.repositories.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    private ProdutoService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new ProdutoService();
        // inject mock via reflection (field is package-private in original)
        try {
            java.lang.reflect.Field f = ProdutoService.class.getDeclaredField("repository");
            f.setAccessible(true);
            f.set(service, repository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void salvar_shouldThrowWhenQuantidadeNull() {
        ProdutoModel p = new ProdutoModel();
        p.setNome("Teste");
        p.setQuantidade(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.salvar(p));
        assertEquals("A quantidade é obrigatória.", ex.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    void salvar_shouldThrowWhenQuantidadeNegative() {
        ProdutoModel p = new ProdutoModel();
        p.setNome("Teste");
        p.setQuantidade(-5);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.salvar(p));
        assertEquals("A quantidade não pode ser negativa.", ex.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    void salvar_shouldThrowWhenDuplicateNameOnCreate() {
        ProdutoModel p = new ProdutoModel();
        p.setIdProduto(0);
        p.setNome("Existente");
        p.setQuantidade(1);

        when(repository.existsByNome("Existente")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.salvar(p));
        assertEquals("Já existe um produto com este nome.", ex.getMessage());
        verify(repository, times(1)).existsByNome("Existente");
        verify(repository, never()).save(any());
    }

    @Test
    void salvar_shouldSetDataAtualizacaoAndSave() {
        ProdutoModel p = new ProdutoModel();
        p.setIdProduto(0);
        p.setNome("Novo");
        p.setQuantidade(3);

        when(repository.existsByNome("Novo")).thenReturn(false);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.salvar(p);

        ArgumentCaptor<ProdutoModel> cap = ArgumentCaptor.forClass(ProdutoModel.class);
        verify(repository).save(cap.capture());
        ProdutoModel saved = cap.getValue();
        assertNotNull(saved.getDataAtualizacao());
        assertTrue(saved.getDataAtualizacao().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}
