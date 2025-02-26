package br.insper.loja.produto;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
public class ProdutoService {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://produtos:8082/produtos";

    public ProdutoService() {
        this.restTemplate = new RestTemplate();
    }

    // Cria um novo produto via requisição POST para o endpoint /produtos
    public Produto criarProduto(Produto produto) {
        try {
            ResponseEntity<Produto> response = restTemplate.postForEntity(baseUrl, produto, Produto.class);
            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar produto");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar produto", e);
        }
    }

    // Busca um produto pelo ID via GET em /produtos/{id}
    public Produto buscarProdutoPorId(String id) {
        try {
            ResponseEntity<Produto> response = restTemplate.getForEntity(baseUrl + "/" + id, Produto.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado", e);
        }
    }

    // Lista todos os produtos via GET em /produtos
    public List<Produto> listarProdutos() {
        try {
            ResponseEntity<Produto[]> response = restTemplate.getForEntity(baseUrl, Produto[].class);
            Produto[] produtos = response.getBody();
            return Arrays.asList(produtos != null ? produtos : new Produto[0]);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao listar produtos", e);
        }
    }

    // Diminui o estoque de um produto via requisição POST em /produtos/{id}/diminuir?quantidade=valor
    public void diminuirEstoque(String id, int quantidade) {
        try {
            String url = baseUrl + "/" + id + "/diminuir?quantidade=" + quantidade;
            ResponseEntity<Void> response = restTemplate.postForEntity(url, null, Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao diminuir estoque");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estoque insuficiente ou erro na requisição", e);
        }
    }
}
