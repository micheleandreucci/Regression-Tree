package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Il MultiThread server main class.
 */
public class MultiServer extends Thread {

	/**
	 * la porta che la ServerSocket user� per accettare la connessione con il clinet
	 * e costruire la Socket.
	 */
	private static final int PORT = 8080;

	/**
	 * La ServerSocket che gestisce il MultiServer.
	 */
	private ServerSocket serverSocket;
	/**
	 * Costruttore pubblico che inizializza la porta ed invoca il metodo run().
	 * 
	 * @param port Porta su cui viene stabilita la connesione
	 * @throws IOException Eccezione I/O di qualche tipo
	 */
	public MultiServer(final int port) throws IOException {
		serverSocket = new ServerSocket(PORT);
	}

	/**
	 * Istanzia un oggetto istanza della classe ServerSocket che si pone in attesa
	 * di richieste di connessione da parte di un client. Ad ogni nuova richiesta di
	 * connessione si istanzia ServerOneClient.
	 */
	public void run() {
		try {
			System.out
					.println("Server Started : IP = " + InetAddress.getLocalHost().getHostAddress() + " Port= " + PORT);
			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("accepting " + socket);
				try {
					new ServerOneClient(socket);
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} finally {
			close();
		}
	}

	/**
	 * Chiude la ServerSocket e ogni Socket connessa con lo stesso Client.
	 */
	public void close() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
