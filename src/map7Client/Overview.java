package map7Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
/**
 * L'interfaccia utente della connessione client.
 */
public class Overview
{
	final MainTest client = new MainTest();
	/**
	 * La stringa di dati recuperata.
	 */
	private String data;
	/**
	 * La connessione flag.
	 */
	private boolean connected = false;

	/**
	 * La connessione socket.
	 */
	private Socket socket;
	/**
	 * Il flusso di oggetti input al client.
	 */
	private ObjectInputStream inStream;
	/**
	 * Il flusso di oggetti output al client.
	 */
	private ObjectOutputStream outStream;
	/**
	 * Label dello stato della connessione.
	 */
	@FXML
	private Label status;

	/**
	 * Il campo di testo IP del server.
	 */
	@FXML
	private TextField ipField;
	/**
	 * Il campo di testo del numero di porta.
	 */
	@FXML
	private TextField portField;
	/**
	 * Il bottono di connessione.
	 */
	@FXML
	private Button connectButton;
	/**
	 * Il bottono di disconnessione.
	 */
	@FXML
	private Button disconnectButton;
	@FXML
	private TextField choiceField;
    /**
	 * The table name text field.
	 */
    @FXML
	private TextField tableNameField;
    /**
	 * The learn from data button.
	 */
    @FXML
	private Button learnDataButton;
    /**
	 * The learn from data button.
	 */
    @FXML
	private Button loadDataButton;
    /**
	 * L'area di testo del risultato.
	 */
    @FXML
	private TextArea area;
    /**
	 * Connessione al server.
	 * @param ip L'ip del server
	 * @param port La porta di connessione
	 * @throws IOException Lancia l'eccezione quando la connessione non � riuscita
	 */
    @FXML
    void connect() {
    	if (isConnected())
    		ClientUI.showInformation("You are already connected to the server");
    	else {
			int port = 0;
			String ip = ipField.getText();
			try {
				port = Integer.parseInt(portField.getText());
				if (port < 0 || port > 65545)
					throw new NumberFormatException("Invalid port");
			} catch (NumberFormatException e) {
				ClientUI.showError("Invalid port format", e.getMessage());
				e.printStackTrace();
			}
			try {
				InetAddress addr = InetAddress.getByName(ip);

				socket = new Socket(addr, port);

				outStream = new ObjectOutputStream(socket.getOutputStream());
				inStream = new ObjectInputStream(socket.getInputStream());

				connected = true;
			}catch (IOException e) {
				ClientUI.showError("Connection to the server failed", e.getMessage());
				e.printStackTrace();
			}
		status.setText("connected");
		status.setTextFill(Color.web("#20F020"));
		}
    }
    /**
	 * Disconnessione dal server.
	 * @throws IOException Generato quando si � verificato un errore durante la chiusura del socket
	 */
    @FXML
    void disconnect() {
    	if (!isConnected()) 
    		ClientUI.showInformation("You are already disconnected");
    	try {
    		if (socket != null) {
    			socket.close();
    			socket = null;
    			connected = false;
    		}
    	} catch (IOException e) {
    		ClientUI.showError("Failed to disconnect from the server", e.getMessage());
    	}
    	status.setText("disconnected");
		status.setTextFill(Color.web("#ff0000"));
    }
    /**
	 * Ottiene lo stato della connessione del client.
	 * @return vero se la connessione e' stabilita, falso altrimenti
	 */
    boolean isConnected() { return connected; }
    /*
     * Apprende l'albero di regressione dai dati
     */
    @FXML
    void LearningTree() throws ClassNotFoundException, IOException, ServerException, UnknownValueException{
    	if (!isConnected())
    		ClientUI.showError("Your are not connected to the server", "");
    	String tableName = tableNameField.getText();
    	try {
    		learnFromData(tableName);
    	} catch (IOException | ClassNotFoundException | ServerException e) {
    		ClientUI.showError("Learning from data failed", e.getMessage());
    		e.printStackTrace();
    	}
    	try {
    		formulateQuery(-1, 0);
    	} catch (ClassNotFoundException | IOException | UnknownValueException | ServerException e) {
    		e.printStackTrace();
    	}
    	updateResultUI();
	}
    /**
	 * Learn from data.
	 * @param tableName Il nome della tabella
	 * @throws IOException Generato quando si verifica un errore di I/O
	 * @throws ClassNotFoundException Generato quando una classe non viene trovata
	 * @throws ServerException Generato quando il risultato del server non � valido
	 */
    void learnFromData(final String tableName) throws IOException, ClassNotFoundException, ServerException {
    	storeTableFromDB(tableName);
    	learnFromDBTable();
    }
    /**
     * Store the table from database.
     * @param tableName Il nome della tabella
     * @throws IOException Generato quando si verifica un errore di I/O
     * @throws ClassNotFoundException Generato quando una classe non viene trovata
     * @throws ServerException Generato quando il risultato del server non � valido
     */
    void storeTableFromDB(final String tableName) throws IOException, ClassNotFoundException, ServerException
    {
    	outStream.writeObject(0);
    	outStream.writeObject(tableName);
    	String result = (String) inStream.readObject();
    	if (!result.equals("OK"))
    		throw new ServerException(result);
    }
    /**
     * Learn from the database table.
     * @throws IOException Generato quando si verifica un errore di I/O
     * @throws ClassNotFoundException Generato quando una classe non viene trovata
     * @throws ServerException Generato quando il risultato del server non � valido
     */
    void learnFromDBTable() throws IOException, ClassNotFoundException, ServerException
    {
    	outStream.writeObject(1);
    	String result = (String) inStream.readObject();
    	if (!result.equals("OK")) 
    		throw new ServerException(result);
    }
    /*
     * Carica l'albero di regressione dall'archivio
     */
    @FXML
    void LoadingTree() throws ClassNotFoundException, IOException, ServerException
    {
    	if (!isConnected())
    		ClientUI.showError("Your are not connected to the server", "");
    	String tableName = tableNameField.getText();
    	try {
			 loadFromFile(tableName);
		} catch (IOException| ClassNotFoundException| ServerException e) {
			ClientUI.showError("Load from file failed", e.getMessage());
		}
    	try {
    		formulateQuery(-1, 0);
    	} catch (ClassNotFoundException | IOException | UnknownValueException | ServerException e) {
    		ClientUI.showError("Formulate query error" + " ! ", e.getMessage());
    		e.printStackTrace();
    	}
    	updateResultUI();
    }
    /**
     * Store the clusters in file.
     * @param tableName Il nome della tabella
     * @throws IOException Generato quando si verifica un errore di I/O
     * @throws ClassNotFoundException Generato quando una classe non viene trovata
     * @throws ServerException Generato quando il risultato del server non � valido
     */
    void loadFromFile(String tableName) throws IOException, ClassNotFoundException, ServerException
    {
    	outStream.writeObject(2);
    	outStream.writeObject(tableName);
    	String result = (String) inStream.readObject();
    	if (!result.equals("OK"))
    		throw new ServerException(result);
    }
     /**
	 * Load a cluster set from file.
	 * @param choice Scelta fatta dall'utente
	 * @param conta
	 * @throws IOException Generato quando si verifica un errore di I/O
	 * @throws ClassNotFoundException Generato quando una classe non viene trovata
	 * @throws ServerException Generato quando il risultato del server non � valido
	 * @throws UnknownValueException Eccezione per gestire il caso di acqusizione di valore mancante
	 * o fuori range di un attributo di un nuovo esempio da classificare.
	 * @return learn apprendimento
	 */
    String formulateQuery(final int choice, final int conta) throws IOException, ClassNotFoundException, ServerException, UnknownValueException {
    	String result = "";
    	if (conta == 1) {
        	outStream.writeObject(choice);
        	result = inStream.readObject().toString();
        }
    	if (conta == 0) {
        	outStream.writeObject(3);
        	result = inStream.readObject().toString();
        }
    	if (result.equals("QUERY"))  {
        	result = inStream.readObject().toString();  // ricevo formulate query
        	String learn = new String();
        	setData((String) result); // invia il formulate query
        	return learn;
        }

        if (result.equals("OK")){
        	result = "Prediction is: " + inStream.readObject().toString();
        }
        if (result.equals("err")) {
        	result = inStream.readObject().toString();
        	ClientUI.showInformation(result);
        	throw new UnknownValueException(result);
        }
        String learn = new String();
    	setData((String) result);
    	return learn;
    }
    /*
     * Assegno un valore alla stringa di dati
     * @param data valore da assegnare alla stringa di dati
     */
    void setData(final String data){
    	this.data = data;
    }
    /*
     * Aggiorna l'area contenente il risultato
     */
    private void updateResultUI() {
    	area.setText(getData());
    	System.out.println(getData());
    }
    /**
     * Ottengo la stringa di dati recuperata.
     * @return La stringa di dati recuperata
     */
    String getData() {
    	return data;
    }
    /*
     * ottiene la predizione
     */
   	@FXML
   	void getPrediction() 
   	{
   		if (!isConnected())
			ClientUI.showError("Your are not connected to the server", "");
   		try {
   				formulateQuery(Integer.parseInt(choiceField.getText()), 1);
   			}  catch (ClassNotFoundException | IOException | ServerException | UnknownValueException e) {
   				e.printStackTrace();
   				ClientUI.showError("Formulate query error", e.getMessage());
   		}
   		updateResultUI();
   	}
}