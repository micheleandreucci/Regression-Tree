package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.net.SocketException;
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
	 * InputStream di oggetti connessi al Client.
	 */
	private ObjectInputStream in;

	/**
	 * OutputStream di oggetti connessi al Client.
	 */
	private ObjectOutputStream out;

	/**
     * Costruzione del ServerOneClient.
     * @param socket La socket connessa al client
     * @throws IOException Geneato quando si verifica un errore I/O
     */
	ServerOneClient(final Socket socket) throws IOException {
		this.socket = socket;

		in = new ObjectInputStream(socket.getInputStream());
		out = new ObjectOutputStream(socket.getOutputStream());

		start();
	}

    /**
     * Avvio del thread.
     */
	@Override
	public void run() {
		RegressionTree tree=new RegressionTree();
		Integer val=null;
		Data trainingSet=null;
		String nomeTabella=null;
		try {
			val=Integer.valueOf(in.readObject().toString());
			if(val==0) {
				nomeTabella=in.readObject().toString();
				out.writeObject("OK");
				val=Integer.valueOf(in.readObject().toString());
				try{
					
					trainingSet= new Data(nomeTabella);
				}
				catch(TrainingDataException e){System.out.println(e);return;}
				tree=new RegressionTree(trainingSet);
				if(val==1) {
					try {
						tree.salva(nomeTabella+".dmp");
					} catch (IOException e) {
						
						System.out.println(e.toString());
					} 
				}
				
			}else if(val==2) {
					nomeTabella=in.readObject().toString();							
					try {
						tree=RegressionTree.carica(nomeTabella+".dmp");
						System.out.println(tree);
					} catch (ClassNotFoundException | IOException e) {
						System.out.print(e);
						return;
					}
					System.out.println(tree);
			}
			tree.printRules();
			tree.printTree();
			out.writeObject("OK");
			val=Integer.valueOf(in.readObject().toString());
			
			if(val==3) {
				
				String risp="y";
				do{
					risp="y";
					try {
						out.writeObject(tree.predictClass(in, out));
					} catch (UnknownValueException e) {
						out.writeObject(e.getMessage());	//invio messaggio di errore al client fuori range
						e.printStackTrace();
					}
					
					try {
						risp=in.readObject().toString();
					}catch(EOFException | SocketException e) {
						risp="4";
						e.printStackTrace();
						socket.close();	
						in.close();
						out.close();
					}
					
			}while (risp.equalsIgnoreCase("3"));
		
			}				
		} catch (ClassNotFoundException | IOException e) {
			System.err.println(e.getMessage());
			try {
				out.writeObject(e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} catch (TrainingDataException e) {
			System.err.println(e.getMessage());
			try {
				out.writeObject(e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (EmptySetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}