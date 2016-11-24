
import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

//import javax.sql.DataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;


class DataSourceFactory {
    public static DataSource getMySQLDataSource() {
        
    	Properties props = new Properties();
        FileInputStream fis = null;
        MysqlDataSource mysqlDS = null;
        try {
            fis = new FileInputStream("db.properties");
            props.load(fis);
            mysqlDS = new MysqlDataSource();
            mysqlDS.setURL(props.getProperty("MYSQL_DB_URL"));
            mysqlDS.setUser(props.getProperty("MYSQL_DB_USERNAME"));
            mysqlDS.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));
        } catch (IOException e) {
        	System.out.println("db.properties is not found");
            e.printStackTrace();
        }
        return mysqlDS;
      
    }
}

class Command {

     
    
    private Connection connection =  null; 
    private Statement stmt = null;
    private ResultSet rs = null;
	private String cmd;
	private List<String> input;

	public Command(String cmd, List<String> input, Connection conn) {
			this.cmd = cmd;
			this.input = input;
			this.connection = conn;
	}

	private void getCustomers(){
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from customer");
		    while(rs.next()){
		       System.out.println("Name="+rs.getString("fName")+" "+rs.getString("lName"));
		    }
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
	    
	}
	public void execute() throws Exception {
		
			if(this.cmd.equals("getCustomers")){
				this.getCustomers();
			}
			else{
				throw new Exception("unrecognized command: " + this.cmd);
			}
	}
}

public class Console {
	private Scanner kbd = new Scanner(System.in);
	

	private List<String> scan(String exp) {
		String[] input = exp.split(" ");
		List<String> tokens = new LinkedList<String>(Arrays.asList(input));
		return tokens;
		
	}


	private Command parse(List<String> tokens, Connection conn) {
		String op = tokens.remove(0);
		Command c = new Command(op, tokens, conn);
		return c;
	}
	// read-execute-print loop
	public void repl(Connection conn) {
		while(true) {
			try {
				System.out.print("-> ");
				String input = kbd.nextLine();
				if (input.equals("quit")) break;
				Command cmmd = parse(scan(input), conn);
				cmmd.execute();
			} catch(Exception e) {
				System.out.println("Error, " + e.getMessage());
			}
		}
		System.out.println("bye");
	}

	public static void main(String args[]) {

		 DataSource ds = DataSourceFactory.getMySQLDataSource();     
	     
	     Connection connection =  null; 
	     Statement stmt = null;
	     ResultSet rs = null;
	     
	        try {
	    		connection = ds.getConnection(); 
	    	} catch (SQLException e) {
	    		System.out.println("Connection Failed! Check output console");
	    		e.printStackTrace();
	    		return;
	    	}
	     
	    	if (connection != null) {
	    		System.out.println("Connected to DeNGoHotelReservation!");
	    	} else {
	    		System.out.println("Failed to make connection!");
	    	}
		Console console = new Console();
		console.repl(connection);
	}
}