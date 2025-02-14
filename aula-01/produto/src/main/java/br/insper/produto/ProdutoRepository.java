package br.insper.produto;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends MongoRepository<Produto, String> {

    Optional<Produto> findByNome(String nome); // Permite buscar produtos pelo nome

}
