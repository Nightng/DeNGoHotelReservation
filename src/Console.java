
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
	
	private void checkRoomTypes(){
		try{
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select * from RoomType");
			while(rs.next()){
			       System.out.println("Room Type ID: " + rs.getString("rTypeID") +", Max number of occupants: " + rs.getString("max"));
			    }
		}
		catch (SQLException e) {
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
		 		   rs = stmt.executeQuery(" SELECT * FROM room AS r1 left join bedtype"
		 		   		+ " on r1.bedTypeID = bedType.bedTypeID WHERE r1.price < (SELECT max(price) FROM room );");
		 		  System.out.println("Where the following rooms are cheapest room that we offer: ");
		 		   while(rs.next()){
		 			  System.out.println("Room: "+ rs.getString("rName")+", Bed Type: " + rs.getString("bedName") 
		 					  + ", Price: $" + rs.getInt("price"));
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
 			fName = scan.nextLine();
 			System.out.print("Last Name: ");
 			lName = scan.nextLine();
 			System.out.print("Date-in: ");
 			dateIn = scan.nextLine();
 			System.out.print("Date-out: ");
 			dateOut = scan.nextLine();
 			stmt = connection.createStatement();
 			rs = stmt.executeQuery("select * FROM CUSTOMER WHERE fName= '" + fName + "' AND lName= '" + lName + "';");
 			if (rs.next()) {
 				id = rs.getInt("cId");
 			}
 			System.out.print("Room Number: ");
 			rName = scan.nextLine(); 		
 			rs = stmt.executeQuery("select * FROM ROOM WHERE rName= '" + rName + "';");
 			if (rs.next()) {
 				roomID = rs.getInt("roomID");
 			}
			stmt = connection.createStatement();		
			int payAMT = 0;
			boolean paid = false;			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");         	      
			PreparedStatement pstmt = null;
			String sql = "INSERT INTO RESERVATION " + "(cID, roomID, dateIN, dateOUT, payDUE, payAMT, paid)"
					+ " VALUES(?,?,?,?,?,?,?)";
			pstmt = (PreparedStatement) connection.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.setInt(2, roomID);
			pstmt.setDate(3, java.sql.Date.valueOf(dateIn));
			pstmt.setDate(4, java.sql.Date.valueOf(dateOut));
			pstmt.setDate(5, java.sql.Date.valueOf(dateIn));
			pstmt.setInt(6, payAMT);
			pstmt.setBoolean(7, paid);			
			pstmt.execute();
 			System.out.println("Success! " + fName + " " + lName + " your reservation was added."); 		
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
	}
	
	private void extendReservation() {
				try{
					Scanner sc = new Scanner(System.in);
					stmt = connection.createStatement();
					String fName;
					String lName;
					int id = 0;
					int amount = 0;
					Date dateIn = null;
					Date dateOut = null;
					System.out.println( "Please input the following to extend reservation." );
					System.out.println( "First Name: " );
					fName = sc.nextLine();
					System.out.println( "Last Name: ");
					lName = sc.nextLine();
					stmt = connection.createStatement();
					String sql = "select cID, fName, lName, rID, dateIN, dateOUT, payAMT, paid "
							+ "from customer join reservation using(cID)"
							+ "WHERE fName= '" + fName + "' AND lName= '" + lName + "';";
					rs = stmt.executeQuery( sql );
					
					while( rs.next() ) {
						System.out.println("cID= " + rs.getInt("cID") + ", fName= " + rs.getString("fName") + ", lName= " + rs.getString("lName") + ", rID= " + rs.getInt("rID") + 
								", dateIN= " + rs.getDate("dateIN") + ", dateOUT= " + rs.getDate("dateOUT") + ", payAMT= " + rs.getInt("payAMT") + ", paid= " + rs.getBoolean("paid") );
						
					}
					rs = stmt.executeQuery(sql);
					if( rs.next() ) {
						id = rs.getInt("cID");
						amount = rs.getInt("payAMT");
						dateIn = rs.getDate("dateIN");
						dateOut = rs.getDate("dateOUT");
					}
					boolean validDate = false;
					String newDate = null;
					while( !validDate ) {
						System.out.println("Enter the new end date: ");
						newDate = sc.nextLine();
						if( java.sql.Date.valueOf(newDate).after(dateOut) ) {
							validDate = true;
						} else {
							System.out.println("invalid date: new date must come after the old date.");
						}
					}
					
					PreparedStatement pstmt = null;
					sql = "Update Reservation SET dateOUT = ?, payAMT = ? WHERE cID = ?";
					pstmt = (PreparedStatement) connection.prepareStatement( sql );
					pstmt.setDate( 1, java.sql.Date.valueOf(newDate) );
					pstmt.setInt(2, amount + 150);
					pstmt.setInt(3, id);
					pstmt.executeUpdate();
					
					System.out.println("Reservation extended until " + newDate + "!");
				}
				catch (SQLException e) {
					System.out.println("Connection Failed! Check output console");
					e.printStackTrace();
				}
			}
	
	private void payDues() {
		try{
			Scanner sc = new Scanner(System.in);
			stmt = connection.createStatement();
			String fName;
			String lName;						
			System.out.println( "Please input the following in order to pay your dues" );
			System.out.println( "First Name: " );
			fName = sc.nextLine();
			System.out.println( "Last Name: ");
			lName = sc.nextLine();
			stmt = connection.createStatement();
			String sql = "select *"
					+ "FROM customer join reservation "
					+ "where customer.cID = reservation.cID and fName= '" + fName + "' AND lName= '" + lName + "' "
					+ "group by customer.cid;";
			rs = stmt.executeQuery( sql );			
			while( rs.next() ) {
				boolean paid = rs.getBoolean("paid");
				if(!paid){
					int rID = rs.getInt("rID");
					int cID = rs.getInt("cID");
					int payAMT = rs.getInt("payAMT");
					System.out.println("You owe: $" + payAMT);
					System.out.println("Cash or Credit?");
					String method = sc.nextLine();
					System.out.println("How much are you paying?");
					int amount = sc.nextInt();					
					stmt = connection.createStatement();			
		 			stmt.executeUpdate("INSERT INTO payment " + "(rID, cID, method, amount)"
		 					+ " VALUES (" + rID + ", " + cID + ", '" + method + "', " + amount + ");"); 	
		 			System.out.println("Success! your payment was submitted.");
		 			stmt.executeUpdate("UPDATE reservation SET paid=" + true + " where rId=" + rID + ";"); 	
				}
				else{
					System.out.println("You have nothing due");
				}
			}
		}
		catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
	}
	
	private void checkPayment() {
		try{
			Scanner sc = new Scanner(System.in);
			stmt = connection.createStatement();
			String fName;
			String lName;
			int id = 0;
			int amount = 0;
			System.out.println( "Please input the following in order to check your payment." );
			System.out.println( "First Name: " );
			fName = sc.nextLine();
			System.out.println( "Last Name: ");
			lName = sc.nextLine();
			stmt = connection.createStatement();
			String sql = "select *, SUM(payAMT) "
					+ "FROM customer join reservation "
					+ "where customer.cID = reservation.cID and fName= '" + fName + "' AND lName= '" + lName + "' "
					+ "group by customer.cid;";
			rs = stmt.executeQuery( sql );			
			while( rs.next() ) {
				amount = rs.getInt("SUM(payAMT)");
			}
			sql = "select *, sum(amount) FROM customer left join payment on customer.cID = payment.cID " 
					+ "where fName= '" + fName + "' AND lName= '" + lName + "';";         
			rs = stmt.executeQuery(sql);
			while( rs.next() ) {
				System.out.println("You amount due is: $: " + amount + " and you have paid: $" + rs.getInt("SUM(amount)"));			
			}
			sql = "select * from customer join (SELECT *, COUNT(cID) FROM payment GROUP BY cID HAVING COUNT(cID) >= 0) as p"
					+ " where customer.cid = p.cid and fName = '" + fName + "' and lname = '"+ lName + "';";         
			rs = stmt.executeQuery(sql);
			while( rs.next() ) {
				System.out.println("You have made the following amount of payments: " + rs.getInt("COUNT(cID)"));			
			}			
		}
		catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
	}
	
	private void changeRoom() throws ParseException {
		try {
 			String fName;
 			String lName;
 			String changedRoomName;
 			Scanner scan = new Scanner(System.in);
 			System.out.println("Please input the following to change rooms: ");
 			System.out.print("First Name: ");
 			fName = scan.nextLine();
 			System.out.print("Last Name: ");
 			lName = scan.nextLine();
 			this.openRooms();
 			System.out.println("Enter the room where you would like to change to: ");
 			changedRoomName = scan.nextLine();
 			
 			int roomIden = 0;
 			int newRoomIden = 0;
 			int custIden = 0;
 			PreparedStatement pstmt = null;
			/*String sql = "update "
					+ "select c.cID, c.fName, c.lName, res.roomID, r.reserved from Customer as c"
					+ "INNER JOIN Reservation as res on res.cID = c.cID"
					+ "INNER JOIN Room as r on r.roomID = res.roomID"
					+ "WHERE fName= '" + fName + "' AND lName= '" + lName + "';";*/
 			
			//used to get roomID and cID of the person
			String sql = "select *"
					+ "from customer join reservation using(cID)"
					+ "WHERE fName= '" + fName + "' AND lName= '" + lName + "';";
 			stmt = connection.createStatement();
 			rs = stmt.executeQuery(sql);
 			if( rs.next() ) {
 				roomIden = rs.getInt("roomID");
 				custIden = rs.getInt("cID");
 			}
 			System.out.println("1st check!!!!!!");
 			//get the new roomID given by the rName 
 			sql = "select *"
					+ "from Room"
					+ "WHERE rName= '" + changedRoomName + "';";
 			//stmt = connection.createStatement();
 			rs = stmt.executeQuery(sql);
 			if( rs.next() ) {
 				newRoomIden = rs.getInt("roomID");
 			}
 			System.out.println("2nd check!!!!!!");
 			//sets old room to empty
 			sql = "UPDATE Room SET reserved = false WHERE roomID = ?";
 			pstmt = (PreparedStatement) connection.prepareStatement(sql);
 			pstmt.setInt(1, roomIden);
 			pstmt.executeUpdate();
 			System.out.println("3rd check!!!!!");
 			//sets new changed room to reserved
 			sql = "UPDATE Room SET reserved = true WHERE roomID = ?";
 			pstmt = (PreparedStatement) connection.prepareStatement(sql);
 			pstmt.setInt(1, newRoomIden);
 			pstmt.executeUpdate();
 			System.out.println("4th check!!!!!");
 			//updates the customers room to the changedRoom
 			sql = "UPDATE Reservatoin SET roomID = ? WHERE cID = ?";
 			pstmt = (PreparedStatement) connection.prepareStatement(sql);
 			pstmt.setInt(1, newRoomIden);
 			pstmt.setInt(2, custIden);
 			pstmt.executeUpdate(); 			
 			System.out.println("Success! " + fName + " " + lName + "'s room was changed.");
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
			else if( this.cmd.equals("checkRoomTypes")){
				this.checkRoomTypes();
			}
			else if( this.cmd.equals("extendReservation")){
				this.extendReservation();
			}
			else if( this.cmd.equals("checkPayment")){
				this.checkPayment();
			}
			else if( this.cmd.equals("payDues")){
				this.payDues();
			}
			else if( this.cmd.equals("changeRoom")){
				this.changeRoom();
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
		System.out.println("checkPayment - to check user's payment");
		System.out.println("checkRoomTypes - to see the room types");
		System.out.println("getBedTypes - to see the bed types");
		System.out.println("payDues - to make a payment");		
		System.out.println("customerReservation [firstName] [lastName] - to see the customer's reservation");
		System.out.println("checkPricing - to check for pricing");		
		System.out.println("checkRoomTypes - to check for room size types");		
		System.out.println("quit - to exit");
	    Console console = new Console();
		console.repl(connection);		
	}
}