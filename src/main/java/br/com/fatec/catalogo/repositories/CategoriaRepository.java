package br.com.fatec.catalogo.repositories;

import br.com.fatec.catalogo.models.CategoriaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<CategoriaModel, Long> {
    // Aqui você poderia adicionar métodos como:
    // List<CategoriaModel> findByNomeContainingIgnoreCase(String nome);
}