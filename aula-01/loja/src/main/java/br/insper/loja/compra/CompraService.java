package br.insper.loja.compra;

import br.insper.loja.usuario.Usuario;
import br.insper.loja.usuario.UsuarioService;
import br.insper.loja.evento.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service public class CompraService {
    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EventoService eventoService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String produtoServiceUrl = "http://localhost:8082/api/produto";

    public static class Produto {
        private String id;
        private String nome;
        private double preco;
        private int quantidadeEmEstoque;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public double getPreco() { return preco; }
        public void setPreco(double preco) { this.preco = preco; }
        public int getQuantidadeEmEstoque() { return quantidadeEmEstoque; }
        public void setQuantidadeEmEstoque(int quantidadeEmEstoque) { this.quantidadeEmEstoque = quantidadeEmEstoque; }
    }

    public Compra salvarCompra(Compra compra) {
        // Recupera os dados do usuário e atualiza a compra
        Usuario usuario = usuarioService.getUsuario(compra.getUsuario());
        compra.setNome(usuario.getNome());
        compra.setDataCompra(LocalDateTime.now());

        double precoTotal = 0;

        // Primeira varredura: valida se os produtos existem e possuem estoque suficiente
        for (String produtoId : compra.getProdutos()) {
            String produtoUrl = produtoServiceUrl + "/" + produtoId;
            Produto produto = restTemplate.getForObject(produtoUrl, Produto.class);
            if (produto == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado: " + produtoId);
            }
            if (produto.getQuantidadeEmEstoque() < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estoque insuficiente para o produto: " + produtoId);
            }
            precoTotal += produto.getPreco();
        }

        // Segunda varredura: decrementa o estoque de cada produto
        for (String produtoId : compra.getProdutos()) {
            String decrementUrl = produtoServiceUrl + "/" + produtoId + "/diminuir?quantidade=1";
            restTemplate.put(decrementUrl, null);
        }


        // Registra o evento da compra realizada
        eventoService.salvarEvento(usuario.getEmail(), "Compra realizada");

        // Salva a compra no repositório
        return compraRepository.save(compra);
    }

    public List<Compra> getCompras() {
        return compraRepository.findAll();
    }
}
