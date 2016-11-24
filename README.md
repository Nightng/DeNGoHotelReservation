# DeNGoHotelReservation

The REPL loop takes the argument from the cmd and splits (by whitespace) it into an array of strings.</br></br>
The first element in the array is the Hotel function, the rest (if any) are more input if the function requires it</br></br>
To add new functions: </br>
-In the Command class' execute method, add a conditional for the function name (that you type into console)</br>
-Create a new method, in the Command class, that performs that function.</br>
