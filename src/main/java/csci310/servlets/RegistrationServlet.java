package csci310.servlets;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import csci310.PostEnvelopeForRegister;
import csci310.Register;

public class RegistrationServlet extends HttpServlet {
	private final Gson gson;
	
	public RegistrationServlet()
	{
		gson = new Gson();
	}
	
	//ALERT: this code is still incomplete, it doesn't redirect the user
	//to a "successful sign up" screen
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		//Code referenced from the URL shortener demo
		String requestBody;
		
		try {
			//Add together all the pairs of lines in the request
			//So then we can read the JSON out of the request and do something useful with GSON library
			requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		//At this point we have properly read the request body into a String object
		//Now we will read the form data the user has sent to us
		//i.e. the username and password they entered
		PostEnvelopeForRegister formData = gson.fromJson(requestBody, PostEnvelopeForRegister.class);
		
		String username = formData.getUsername();
		String password = formData.getPassword();
		
		boolean userInfoIsValid = Register.validateUserInfo(username, password);
		
		//Invalid user info
		if(userInfoIsValid == false) {
			//NOTE: Improve this! Return a response telling the user that they messed up
			System.out.println("User info is not valid");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		//User already in database
		boolean userAlreadyInDatabase = Register.checkUserExists(username);
		if(userAlreadyInDatabase == false) {
			//NOTE: Improve this! Return a response saying the user already is registered
			System.out.println("User already in database");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		//Hash the password
		String hashed_password = "";
		try {
			hashed_password = Register.hashPasswordWithSHA256(password);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
		//Put the user in the database, everything is okay!
		Register.insertUser(username, hashed_password);
		
		//ALERT: this code is still incomplete, it doesn't redirect the user
		//to a "successful sign up" screen
	}
}
