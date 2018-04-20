package br.com.drogaria.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.omnifaces.util.Messages;

import br.com.drogaria.dao.ProdutoDAO;
import br.com.drogaria.domain.ItemVenda;
import br.com.drogaria.domain.Produto;
import br.com.drogaria.domain.Venda;

@SuppressWarnings("serial")
@ManagedBean
@ViewScoped // caso nao tenha essa anotação, não será adicionada mais de um item, devido a
			// atualização do ajax
public class VendaBean implements Serializable {

	private List<Produto> listProdutos;
	private List<ItemVenda> listItemVendas;
	private Produto produto;
	private Venda venda;

	@PostConstruct
	public void novo() {
		venda = new Venda();
		venda.setPrecoTotal(new BigDecimal("0"));
		
		try {
			ProdutoDAO dao = new ProdutoDAO();
			listProdutos = dao.listar();
			listItemVendas = new ArrayList<>();
		} catch (RuntimeException e) {
			Messages.addGlobalError("Erro para carregar os produtos");
		}
	}

	public void adicionar(ActionEvent evento) {
		produto = (Produto) evento.getComponent().getAttributes().get("produtoSelecionado");

		int posicaoEncontrada = -1;
		for (int posicao = 0; posicao < listItemVendas.size(); posicao++) {
			if (listItemVendas.get(posicao).getProduto().equals(produto)) { // o produto da linha corrente do for é
																			// igual ao produto adicionado
				posicaoEncontrada = posicao;
			}
		}
		// adicionar item novo, pois 'posicaoEncontrada' é diferente de -1;
		if (posicaoEncontrada < 0) {
			ItemVenda itemVenda = new ItemVenda();
			itemVenda.setProduto(produto);
			itemVenda.setQuantidade(new Short("1"));
			itemVenda.setValorParcial(produto.getPreco());

			listItemVendas.add(itemVenda);
		} else {
			ItemVenda itemVenda = listItemVendas.get(posicaoEncontrada);
			itemVenda.setQuantidade((short) (itemVenda.getQuantidade() + 1));
			itemVenda.setValorParcial(produto.getPreco().multiply(new BigDecimal(itemVenda.getQuantidade()))); 
			// mutiplicação valor do produto com a quantidade do item
		}
		calcular(); //calcular o valor total da compra
	}

	public void diminiur(ActionEvent evento) {
		ItemVenda itemVenda = (ItemVenda) evento.getComponent().getAttributes().get("itemSelecionado");

		int posicaoEncontrada = -1;
		for (int posicao = 0; posicao < listItemVendas.size(); posicao++) {
			if (listItemVendas.get(posicao).getProduto().equals(itemVenda.getProduto())) {
				posicaoEncontrada = posicao;
			}
		}
		 // Se o objeto existir na lista, acontecerá o efeito inverso do método adicionar
        // Diminuindo a quantidade e o valor a cada clique correspondente a uma unidade do produto
		if (posicaoEncontrada >= 0) {
			itemVenda.setQuantidade((short) (itemVenda.getQuantidade() - 1));
			itemVenda.setValorParcial(itemVenda.getValorParcial().subtract(itemVenda.getProduto().getPreco()));
            
			// se a quantidade chegar a zero o item não deve mais existir na lista (carrinho de compras)
			if (itemVenda.getQuantidade() == 0) {
				listItemVendas.remove(posicaoEncontrada);
			}
		}
		calcular(); //calcular o valor total da compra
	}

	public void remover(ActionEvent evento) {
		ItemVenda itemVenda = (ItemVenda) evento.getComponent().getAttributes().get("itemSelecionado");

		int posicaoEncontrada = -1;
		for (int posicao = 0; posicao < listItemVendas.size(); posicao++) {
			if (listItemVendas.get(posicao).getProduto().equals(itemVenda.getProduto()))
				posicaoEncontrada = posicao;
		}
	listItemVendas.remove(posicaoEncontrada);
	
	calcular(); //calcular o valor total da compra
	}
	
	public void calcular() {
		venda.setPrecoTotal(new BigDecimal("0"));
		
		for (int posicao = 0; posicao < listItemVendas.size(); posicao++) {
			ItemVenda itemVenda = listItemVendas.get(posicao); //a cada repetição, vai obtendo um item do carrinho de compra
			venda.setPrecoTotal(venda.getPrecoTotal().add(itemVenda.getValorParcial())); //soma de dois valores BigDecimais
		}
	}
	
	public List<Produto> getListProdutos() {
		return listProdutos;
	}

	public void setListProdutos(List<Produto> listProdutos) {
		this.listProdutos = listProdutos;
	}

	public List<ItemVenda> getListItemVendas() {
		return listItemVendas;
	}

	public void setListItemVendas(List<ItemVenda> listItemVendas) {
		this.listItemVendas = listItemVendas;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Venda getVenda() {
		return venda;
	}

	public void setVenda(Venda venda) {
		this.venda = venda;
	}
	
}
