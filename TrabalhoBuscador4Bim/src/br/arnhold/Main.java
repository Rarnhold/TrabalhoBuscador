package br.arnhold;

import java.util.Collection;

public class Main {

	public static void main(String[] args) throws InterruptedException {

		System.out.println("iniciando");

		final Thread threadBusca = new Thread(new Runnable() {

			public void run() {
				final BuscarEmArquivos buscador = new BuscarEmArquivos("C:/ecosis");
				String query = "rafa";
				final Collection<String> resultado = buscador.buscar(query);
				System.out.println("arquivo com correspondencia a: " + query);
				resultado.forEach(System.out::println);

			}
		});
		threadBusca.start();
		threadBusca.join();
		System.out.println("concluido a busca em arquivos");
	}



}
