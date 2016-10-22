package br.arnhold;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.omg.CORBA.IRObject;

public class BuscarEmArquivos {

	// ExecutorService serve para threads
	private final ExecutorService executor = Executors.newFixedThreadPool(10);
	private final String diretorioRaiz;
	private final ArrayList<String> resultado = new ArrayList<String>();

	public BuscarEmArquivos(final String diretorioRaiz) {
		this.diretorioRaiz = diretorioRaiz;
	}

	public Collection<String> buscar(final String query) {
		final File raiz = new File(this.diretorioRaiz);
		if (!raiz.exists()) {
			throw new IllegalArgumentException("Diretorio raiz não encontrado");
		}

		buscarEmArquivos(raiz, query);
		// fecha a porta para novas verificações
		executor.shutdown();

		//enquanto ele nao terminar a execução fica dentro do whiller espera 1 seg
		while (!executor.isTerminated()) {
			try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Collections.unmodifiableCollection(this.resultado);
	}

	private void buscarEmArquivos(final File raiz, final String query) {
		if (raiz.isDirectory()) {
			final File[] arquivos = raiz.listFiles();
			// Começa a recursividade
			for (final File arquivo : arquivos) {
				buscarEmArquivos(arquivo, query);
			}
			// verifica se o arquivo termina com .txt
		} else if (raiz.getName().endsWith(".txt")) {
			// execução anonima
			this.executor.submit(new Runnable() {
				public void run() {
					try {
						System.out.println("fazendo a leitura do arquivo: " + raiz.getAbsolutePath());
						final File arquivo = raiz;
						final FileReader fileReader = new FileReader(arquivo);
						final BufferedReader reader = new BufferedReader(fileReader);
						// talvez de pau aqui
						String linha = null;
						while ((linha = reader.readLine()) != null) {
							if (linha.toLowerCase().contains(query)) {
								BuscarEmArquivos.this.resultado.add(arquivo.getAbsolutePath());
								break;

							}

						}
						reader.close();
						fileReader.close();
						System.out.println("Leitura concluida do arquivo: " + raiz.getAbsolutePath());

					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
		} else {
			// iganorando o caminho iteiro do arquivo
			System.out.println("ignorando leitura do arquivo : " + raiz.getAbsolutePath());
		}
	}
}
