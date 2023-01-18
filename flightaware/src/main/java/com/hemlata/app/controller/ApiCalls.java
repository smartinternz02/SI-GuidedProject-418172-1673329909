package com.hemlata.app.controller;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
public class ApiCalls {
	
	//API Call to get list of flights
	public String fprices(String p1,String p2,String p3,String d) throws IOException, InterruptedException, ParseException
	{
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://skyscanner50.p.rapidapi.com/api/v1/searchFlights"+p1+"/"+p2+"/"+p3+"/"+d))
				.header("x-rapidapi-key", "b4991835efmsh917ca8c52269151p1fd06ajsncc7c1a1a4268")
				.header("x-rapidapi-host", "skyscanner-flights.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body());
		return response.body();
		
	}
	public String dprices(String ctry,String curr,String p1,String p2,String p3,String d,String d2) throws IOException, InterruptedException, ParseException
	{
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://skyscanner50.p.rapidapi.com/api/v1/searchFlights/"+ctry+"/"+curr+"/"+p1+"/"+p2+"/"+p3+"/"+d+"?inboundpartialdate="+d2))
				.header("x-rapidapi-key", "82a73b2c80mshf734bc4dfddebf0p13b15ajsna054caa05946")
				.header("x-rapidapi-host", "skyscanner50.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body());
		return response.body();
		
	}
	public String routes(String ctry,String curr,String p1,String p2,String p3,String d) throws IOException, InterruptedException, ParseException
	{
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://skyscanner50.p.rapidapi.com/api/v1/searchFlights?origin=LOND&destination=NYCA+&date="+d+"&adults=1&currency=USD&countryCode=US&market=en-US"))
				.header("x-rapidapi-key", "7096e68032msh474238ea0e79bfcp1e843bjsn294f82d49c71")
				.header("x-rapidapi-host", "skyscanner50.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body());
		return response.body();
		
	}
	public String curs() throws IOException, InterruptedException{
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/reference/v1.0/currencies"))
				.header("x-rapidapi-key", "4e96ca0c82msh7acc5424070fda1p1c7d82jsn9ec25a33485e")
				.header("x-rapidapi-host", "skyscanner-flights.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body());
		return response.body();
		
	}
	public String cntrys() throws IOException, InterruptedException{
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://rapidapi.com/3b-data-3b-data-default/api/skyscanner44/"))
				.header("x-rapidapi-key", "4e96ca0c82msh7acc5424070fda1p1c7d82jsn9ec25a33485e")
				.header("x-rapidapi-host", "skyscanner-flights.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body());
		return response.body();
		
	}
	public String placess(String curr,String code,String cntry,String locale) throws IOException, InterruptedException{
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/autosuggest/v1.0/"+code+"/"+curr+"/"+locale+"/?query="+cntry))
				.header("x-rapidapi-key", "4e96ca0c82msh7acc5424070fda1p1c7d82jsn9ec25a33485e")
				.header("x-rapidapi-host", "skyscanner-flights.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body());
		return response.body();
		
	}

	public String apiExample()throws IOException, InterruptedException {
		// Create the HTTP request
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://timetable-lookup.p.rapidapi.com/airports/"))
				.header("X-RapidAPI-Key", "325b8cfbe0msh5cfc15ecea24dbap10368djsnc2ff6eb8ad50")
				.header("X-RapidAPI-Host", "timetable-lookup.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();

		// Send the request and get the response
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

		// Print the response body
		System.out.println(response.body());
		return response.body();
}

	public String countryListAPI()throws IOException, InterruptedException {
		// Create the HTTP request
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://country-list5.p.rapidapi.com/countrylist/"))
				.header("X-RapidAPI-Key", "325b8cfbe0msh5cfc15ecea24dbap10368djsnc2ff6eb8ad50")
				.header("X-RapidAPI-Host", "country-list5.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();

		// Send the request and get the response
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

		// Print the response body
		System.out.println(response.body());
		return response.body();
	}

	public String currenciesListAPI() throws IOException, InterruptedException {
		// Create the HTTP request
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://currencyscoop.p.rapidapi.com/currencies"))
				.header("X-RapidAPI-Key", "325b8cfbe0msh5cfc15ecea24dbap10368djsnc2ff6eb8ad50")
				.header("X-RapidAPI-Host", "currencyscoop.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();

		// Send the request and get the response
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

		// Print the response body
		System.out.println(response.body());
		return response.body();
	}



	public String flightSchedules(String departure,String arrival,String date) throws IOException, InterruptedException, ParseException
	{
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://timetable-lookup.p.rapidapi.com/TimeTable/" + departure + "/" + arrival + "/" + date + "/"))
				.header("X-RapidAPI-Key", "325b8cfbe0msh5cfc15ecea24dbap10368djsnc2ff6eb8ad50")
				.header("X-RapidAPI-Host", "timetable-lookup.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body());
		return response.body();

	}

	public String bestFlight(String departure,String arrival,String date,String no) throws IOException, InterruptedException, ParseException
	{
		HttpRequest request = HttpRequest.newBuilder()
		.uri(URI.create("https://skyscanner44.p.rapidapi.com/search?adults="+no+"&origin="+departure+"&destination="+arrival+"&departureDate="+date+"&currency=EUR"))
				.header("X-RapidAPI-Key", "325b8cfbe0msh5cfc15ecea24dbap10368djsnc2ff6eb8ad50")
				.header("X-RapidAPI-Host", "skyscanner44.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body());
		return response.body();

	}


}