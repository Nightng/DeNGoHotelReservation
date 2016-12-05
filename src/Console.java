
import java.text.ParseException;
import java.text.SimpleDateFormat;
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





import com.mysql.jdbc.PreparedStatement;
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

	private void deleteReservation(){
		try {
			System.out.println("Please input the following in order to delete a reservation: ");
			String fName;
			String lName;			
			int cID = 0;
			Scanner scan = new Scanner(System.in);
 			System.out.print("First Name: ");
 			fName = scan.next();
 			System.out.print("Last Name: ");
 			lName = scan.next();
 			stmt = connection.createStatement();
 			rs = stmt.executeQuery("select * FROM CUSTOMER WHERE fName= '" + fName + "' AND lName= '" + lName + "';");
 			if (rs.next()) {
 				cID = rs.getInt("cId");
 			}
 			stmt = connection.createStatement();
 			stmt.executeUpdate("DELETE FROM RESERVATION WHERE cID= " + cID + ";"); 	
 			System.out.println("Success! " + fName + " " + lName + " your reservation was deleted.");			
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
		 		       System.out.println("roomID = "+rs.getString("roomID")+", "+"price = " + rs.getInt("price"));
		 		    }
		 		} catch (SQLException e) {
		 			System.out.println("Connection Failed! Check output console");
		 			e.printStackTrace();
		 		}
		 	}
	
	private void addCustomer(){
 		try {
 			String fName;
 			String lName;
 			String addrST;
 			String country;
 			String city;
 			String zipCode;
 			String phone;
 			String email; 			
 			Scanner scan = new Scanner(System.in);
 			System.out.println("Please input the following: ");
 			System.out.print("First Name: ");
 			fName = scan.next();
 			System.out.print("Last Name: ");
 			lName = scan.next();
 			System.out.print("Address: ");
 			addrST = scan.next();
 			System.out.print("Country: ");
 			country = scan.next();
 			System.out.print("City: ");
 			city = scan.next();
 			System.out.print("Zip Code: ");
 			zipCode = scan.next();
 			System.out.print("Phone: ");
 			phone = scan.next();
 			System.out.print("Email: ");
 			email = scan.next(); 			
			stmt = connection.createStatement();			
 			stmt.executeUpdate("INSERT INTO CUSTOMER " + "(fName, lName, addrST, country, city, zipCode, phone, email)"
 					+ " VALUES ('" + fName + "', '" + lName + "', '" + addrST + "', '" + country + "', '" + city + "', " + zipCode 
 					+ ", " + phone + ", '" + email + "');"); 	
 			System.out.println("Success! " + fName + " " + lName + " was added.");
 		} catch (SQLException e) {
 			System.out.println("Connection Failed! Check output console");
 			e.printStackTrace();
 		}
	}
	
	private void deleteCustomer(){
 		try {
 			String fName;
 			String lName;
 			Scanner scan = new Scanner(System.in);
 			System.out.println("Please input the following to be deleted: ");
 			System.out.print("First Name: ");
 			fName = scan.next();
 			System.out.print("Last Name: ");
 			lName = scan.next();
			stmt = connection.createStatement();			
 			stmt.executeUpdate("DELETE FROM CUSTOMER WHERE fName= '" + fName + "' AND lName= '" + lName + "';"); 	
 			System.out.println("Success! " + fName + " " + lName + " was deleted.");
 		} catch (SQLException e) {
 			System.out.println("Connection Failed! Check output console");
 			e.printStackTrace();
 		}
	}
	
	private void updateCustomer(){
 		try {
 			String oldFName;
 			String oldLName;
 			int id = 0;
 			String fName;
 			String lName;
 			String addrST;
 			String country;
 			String city;
 			String zipCode;
 			String phone;
 			String email; 			
 			Scanner scan = new Scanner(System.in);
 			System.out.println("Please input the following to find the customer to update: ");
 			System.out.print("First Name: ");
 			oldFName = scan.next();
 			System.out.print("Last Name: ");
 			oldLName = scan.next();
 			stmt = connection.createStatement();
 			
			rs = stmt.executeQuery("select * FROM CUSTOMER WHERE fName= '" + oldFName + "' AND lName= '" + oldLName + "';");
			 while(rs.next()){
				 id = rs.getInt("cId");
				 System.out.println("Name = "+rs.getString("fName")+" "+rs.getString("lName"));
				 System.out.println("Address = "+rs.getString("addrST"));
				 System.out.println("Country: "+rs.getString("country"));
				 System.out.println("City: "+rs.getString("city"));
				 System.out.println("ZipCode: "+rs.getString("zipCode"));
				 System.out.println("Phone: " +rs.getString("phone"));
				 System.out.println("Email: " +rs.getString("email"));
			 }
			System.out.println("Please input the following to update: ");
 			System.out.print("First Name: ");
 			fName = scan.next();
 			System.out.print("Last Name: ");
 			lName = scan.next();
 			System.out.print("Address: ");
 			addrST = scan.next();
 			System.out.print("Country: ");
 			country = scan.next();
 			System.out.print("City: ");
 			city = scan.next();
 			System.out.print("Zip Code: ");
 			zipCode = scan.next();
 			System.out.print("Phone: ");
 			phone = scan.next();
 			System.out.print("Email: ");
 			email = scan.next(); 			
			stmt = connection.createStatement();			
 			stmt.executeUpdate("UPDATE CUSTOMER SET	fName='" + fName + "', lName='" + lName + "', addrST='" + addrST
 					+ "', country='" + country + "', city='" + city + "', zipCode=" + zipCode 
 					+ ", phone=" + phone + ", email='" + email + "' where cId=" + id + ";"); 	
 			System.out.println("Success! " + fName + " " + lName + " was updated.");
 		} catch (SQLException e) {
 			System.out.println("Connection Failed! Check output console");
 			e.printStackTrace();
 		}
	}
	
	
	//**TODO: ADD DATE
	private void addReservation() throws ParseException{
		try {					
			this.openRooms();
			System.out.println("Please input the following in order to make a reservation: ");
			String fName;
			String lName;
			String rName;
			int id = 0;
			int roomID = 0;
			String dateIn;			             	        
			String dateOut;
			Scanner scan = new Scanner(System.in);
 			System.out.print("First Name: ");
 			fName = scan.next();
 			System.out.print("Last Name: ");
 			lName = scan.next();
 			System.out.print("Date-in: ");
 			dateIn = scan.next();
 			System.out.print("Date-out: ");
 			dateOut = scan.next();
 			stmt = connection.createStatement();
 			rs = stmt.executeQuery("select * FROM CUSTOMER WHERE fName= '" + fName + "' AND lName= '" + lName + "';");
 			if (rs.next()) {
 				id = rs.getInt("cId");
 			}
 			System.out.print("Room Number: ");
 			rName = scan.next(); 		
 			rs = stmt.executeQuery("select * FROM ROOM WHERE rName= '" + rName + "';");
 			if (rs.next()) {
 				roomID = rs.getInt("roomID");
 			}
			stmt = connection.createStatement();		
			String payDUE = "0";
			int payAMT = 0;
			boolean paid = false;			
//			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd"); 
//	        java.sql.Date dIn= new java.sql.Date(format.parse(dateIn).getTime());
//	        java.sql.Date dOut= new java.sql.Date(format.parse(dateOut).getTime());
//	        java.sql.Date dDue= new java.sql.Date(format.parse(payDUE).getTime());
 			stmt.executeUpdate("INSERT INTO RESERVATION " + "(cID, roomID, payAMT, paid)"
 					+ " VALUES (" + id + ", " + roomID + ", " + payAMT + ", " + paid + ");"); 	
 			System.out.println("Success! " + fName + " " + lName + " your reservation was added."); 		
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
			else if( this.cmd.equals("addCustomer") ) {
				 this.addCustomer();
			}			
			else if( this.cmd.equals("deleteCustomer") ) {
				 this.deleteCustomer();
			}	
			else if( this.cmd.equals("updateCustomer") ) {
				 this.updateCustomer();
			}	
			else if( this.cmd.equals("addReservation") ) {
				 this.addReservation();
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
		System.out.println("Thanks for coming! Good Bye!");
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
	    	
    	System.out.println("Welcome to DeNGoHotelReservation!");
		System.out.println("Please type the following: ");		
		System.out.println("addCustomer - to add a customer");
		System.out.println("deleteCustomer - to delete a customer");
		System.out.println("getCustomers - to see all the  customers");
		System.out.println("updateCustomer - to update customer's info");
		System.out.println("addReservation - to add a reservation");
		System.out.println("deleteReservation - to delete reservation");
		System.out.println("extendReservation - to extend reservation");
		System.out.println("changeRoom - to change a room");		
		System.out.println("openRooms - to see open rooms");
		System.out.println("updateRoomInfo - to update room's info");
		System.out.println("checkRoomTypes - to see the room types");
		System.out.println("getBedTypes - to see the bed types");
		System.out.println("checkDates - to see the dates available");		
		System.out.println("customerReservation [firstName] [lastName] - to see the customer's reservation");
		System.out.println("checkPricing - to check for pricing");		
		System.out.println("quit - to exit");
	    Console console = new Console();
		console.repl(connection);		
	}
}