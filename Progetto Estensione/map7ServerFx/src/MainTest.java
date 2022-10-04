import java.io.IOException;
import java.sql.SQLException;
import data.TrainingDataException;
import database.EmptySetException;
import server.MultiServer;
import server.UnknownValueException;

public class MainTest {
	private static final int PORT = 8080;

	public static void main(String[] args) throws TrainingDataException, InstantiationException, ClassNotFoundException,IllegalAccessException, EmptySetException, SQLException, UnknownValueException, IOException 
	{
		int port;

		if (args.length == 0) {
			port = PORT;
		} else {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println(e.toString());
				return;
			}
		}

		if (port < 0 || port >= 65536) {
			System.err.println("Invalid port: " + port);
			return;
		}

		try {
			new MultiServer(port).run();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}