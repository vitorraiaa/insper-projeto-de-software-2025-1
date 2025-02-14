package br.insper.produto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public Produto criarProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Produto buscarProdutoPorId(String id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
    }

    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }

    public void diminuirEstoque(String id, int quantidade) {
        Produto produto = buscarProdutoPorId(id);
        if (produto.getQuantidadeEmEstoque() < quantidade) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estoque insuficiente");
        }
        produto.setQuantidadeEmEstoque(produto.getQuantidadeEmEstoque() - quantidade);
        produtoRepository.save(produto);
    }
}
