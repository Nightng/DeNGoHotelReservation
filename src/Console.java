
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
	//doesnt work...preparedstatement?
	private void deleteReservation(){
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("DELETE FROM Reservation WHERE rID =" + this.input.get(0));
			rs = stmt.executeQuery("select * from Reservation");
		    while(rs.next()){
		       System.out.println(rs.getString("rID"));
		    }
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
	}
	//Check Available Rooms
	private void openRooms(){
		try {
			System.out.println("Rooms currently available:");
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select rName, bedTypeID from Room where reserved = 0");
		    while(rs.next()){
		       System.out.print("Room Number: " + rs.getString("rName") +", Bed Type: ");
		       Statement tmpStmt = connection.createStatement();
		       ResultSet rs2 = tmpStmt.executeQuery("select bedName from BedType where bedTypeID = " + rs.getString("bedTypeID"));
		       rs2.next();
		       System.out.println(rs2.getString("bedName"));
		    }
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
	}
	//Get bed types
	private void getBedTypes(){
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from BedType");
		    while(rs.next()){
		       System.out.println("Bed Type ID: " + rs.getString("bedTypeID") +", Bed Type: " + rs.getString("bedName"));
		    }
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
	}
	private void customerReservation(){
		try {
			if(input.size() != 2){
				System.out.println("Error: Please input both firstname and lastname of customer");
			}else{
			stmt = connection.createStatement();
			//Get cID of customer
			ResultSet cust = stmt.executeQuery("select * from Customer where fName = \'" +input.get(0) + "\' and lName = \'" + input.get(1) + "\'");
			cust.next();
			rs = stmt.executeQuery("select * from Reservation where cID = " + cust.getString("cID"));
			if(!rs.next()){
				System.out.println("The customer, " + input.get(0) + " " + input.get(1) + " does not current have a reservation at the DeNGoHotel.");
			}
			rs.beforeFirst();
		    while(rs.next()){
				    System.out.print("Customer " +input.get(0) + " " +input.get(1)
				    		+ " currently has a reservation from " + rs.getString("dateIN")
				    		+ " until " +rs.getString("dateOUT") + " in Room Number ");
				    Statement tmpStmt = connection.createStatement();
				    ResultSet room = tmpStmt.executeQuery("select * from Room where roomID = " + rs.getString("roomID"));
				    room.next();
				    System.out.println(room.getString("rName"));
		    	}
		    }
		} catch (SQLException e) {
			System.out.println("Error in command, please try again.");
			e.printStackTrace();
		}
	}
	private void checkPricing() {
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from room");
		    while(rs.next()){
		       System.out.println("roomID= "+rs.getString("roomID")+", "+"price= " + rs.getInt("price"));
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
			else if(this.cmd.equals("deleteReservation")){
				this.deleteReservation();
			}
			else if(this.cmd.equals("openRooms")){
				this.openRooms();
			}
			else if(this.cmd.equals("getBedTypes")){
				this.getBedTypes();
			}
			else if(this.cmd.equals("customerReservation")){
				this.customerReservation();
			}
			else if( this.cmd.equals("checkPricing") ) {
				this.checkPricing();
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