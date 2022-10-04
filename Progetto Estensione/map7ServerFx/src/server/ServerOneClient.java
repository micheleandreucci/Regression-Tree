package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.sql.SQLException;

import data.Data;
import data.TrainingDataException;
import database.EmptySetException;
import tree.RegressionTree;

/**
 * Server one client thread class.
 */
class ServerOneClient extends Thread {

	/**
	 * La connessione socket.
	 */
	private Socket socket;

	/**
	 * Istanza di RegressionTree che calcola i dati della tabella.
	 */
	private RegressionTree tree;

	/**
	 * I dati letti da una tabella di database.
	 */
	private Data trainingSet;

	/**
	 * InputStream di oggetti connessi al Client.
	 */
	private ObjectInputStream in;

	/**
	 * OutputStream di oggetti connessi al Client.
	 */
	private ObjectOutputStream out;

	/**
	 * il nome, ricevuto dal client, della tabella che sar� cercata nel database.
	 */
	private String tableName = "";

	/**
	 * Costruzione del ServerOneClient.
	 * 
	 * @param socket La socket connessa al client
	 * @throws IOException Geneato quando si verifica un errore I/O
	 */
	ServerOneClient(Socket socket) throws IOException {
		this.socket = socket;

		in = new ObjectInputStream(socket.getInputStream());
		out = new ObjectOutputStream(socket.getOutputStream());

		start();
	}

	/**
	 * Avvio del thread.
	 */
	/**
     * Avvio del thread.
     */
	@Override
	public void run() {

		try {
			while (true) {
				Integer choice = ((Integer) in.readObject()).intValue();

				switch (choice) {
					case 0:
						storeTableFromDB();
						break;
					case 1:
						learnFromDBTable();
						break;
					case 2:
						loadFromFile();
						break;
					case 3:
						predictionPhase();
						break;
					default:
						break;
				}
			}
		} catch (IOException e) {
			System.err.println(socket + " disconnected");
		} catch (ClassNotFoundException e) {
			System.err.println(socket + ": " + e);
		} catch (UnknownValueException e) {
			e.printStackTrace();
		} catch (TrainingDataException e) {
			e.printStackTrace();
		} catch (EmptySetException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	/**
	 * Store the table from database.
	 * @throws IOException Geneato quando si verifica un errore I/O
	 * @throws ClassNotFoundException Generato quando una classe non viene trovata
	 * @throws EmptySetException Generato quando il resultset è vuoto
	 */
	private void storeTableFromDB() throws ClassNotFoundException, IOException, EmptySetException {
		String result = "OK";
		tableName = (String) in.readObject();

		try {
			trainingSet = new Data(tableName);
		} catch (SQLException e) {
			result = e.toString();
		} catch (TrainingDataException e) {
			result = e.toString();
		}
		out.writeObject(result);
	}

	/**
	 * Learn from database.
	 * @throws IOException Geneato quando si verifica un errore I/O
	 * @throws TrainingDataException Generato quando il file specificato è mancante/errato
	 * @throws UnknownValueException Generato quado un valore è sconosciuto
	 */
	private void learnFromDBTable() throws IOException, UnknownValueException, TrainingDataException {
		String result = "OK";
		tree = new RegressionTree(trainingSet);

		try {
			tree.salva(tableName + ".dmp");
		} catch (FileNotFoundException e) {
			result = e.toString();
		} catch (IOException e) {
			result = e.toString();
		}
		tree.printRules();
		tree.printTree();
		out.writeObject(result);
	}

	/**
	 * Load from file.
	 * @throws ClassNotFoundException Generato quando una classe non viene trovata
	 * @throws IOException Geneato quando si verifica un errore I/O
	 */
	private void loadFromFile() throws ClassNotFoundException, IOException {
		String result = "OK";
		tableName = (String) in.readObject();

		try {
			tree = RegressionTree.carica(tableName + ".dmp");
			System.out.println(tree);
		} catch (ClassNotFoundException | IOException e) {
			result = e.toString();
		}
		tree.printRules();
		tree.printTree();
		out.writeObject(result);
	}

	/**
	 * Fase di predizione.
	 * @throws IOException Geneato quando si verifica un errore I/O
	 * @throws UnknownValueException Generato quado un valore è sconosciuto
	 */
	private void predictionPhase() throws IOException, UnknownValueException {
		String result = "OK";
		Double predication = null;

		try {
			predication = tree.predictClass(in, out);
		} catch (ClassNotFoundException e) {
			result = e.toString();
		}

		out.writeObject(result);
		out.writeObject(predication);
	}

	/**
	 * Chiude il server in un thread client.
	 */
	private void close() {
		try {
			socket.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
