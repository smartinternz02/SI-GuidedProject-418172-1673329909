package com.hemlata.app.controller;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import org.json.XML;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hemlata.app.model.User;
import com.hemlata.app.model.UserIp;
import com.hemlata.app.repository.FlightRepo;
import com.hemlata.app.repository.userRepo;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

@Controller
public class FlightController {

@Autowired
FlightRepo frepo;

@Autowired
userRepo urepo;

BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
cityCode cc=new cityCode();

@GetMapping("/")
public ModelAndView homeGet(ModelAndView modelAndView,UserIp userip)
{
	modelAndView.setViewName("home");
	return modelAndView;
}
@GetMapping("/Register")
public ModelAndView RegGet(ModelAndView modelAndView,User user)
{
	modelAndView.addObject("user", user);
	modelAndView.setViewName("register");
	return modelAndView;
}
@PostMapping("/Register")
public String registerUser(ModelAndView modelAndView, User user) {
	String path = null;
	String email=user.getEmailId();
	System.out.println(email);
	User existingUser = urepo.findByEmailIdIgnoreCase(email);
	if(existingUser != null) {
		modelAndView.addObject("msg","This email already exists!");
		modelAndView.setViewName("register");
	} else {
		System.out.println(user.getPass());
		user.setPass(encoder.encode(user.getPass()));
		urepo.save(user);
		//sendEmail(user.getEmailId());
		modelAndView.addObject("emailId", user.getEmailId());
		 path="redirect:/Login";
	}
	return path;
}
@GetMapping("/Login")
public ModelAndView LogGet(ModelAndView modelAndView,User user)
{
	modelAndView.addObject("user", user);
	modelAndView.setViewName("login");
	return modelAndView;
}
@PostMapping("/Login")
public ModelAndView loginUser(ModelAndView modelAndView, User user) {
	String email=user.getEmailId();
	User existingUser = urepo.findByEmailIdIgnoreCase(email);
	System.out.println(existingUser);
	if(existingUser != null) {
		if (encoder.matches(user.getPass(), existingUser.getPass())){
			// successfully logged in
			modelAndView.addObject("msg", "You Have Successfully Logged in");
			modelAndView.setViewName("loginHome");
		} else {
			// wrong password
			modelAndView.addObject("msg", "Incorrect password. Try again.");
			modelAndView.setViewName("login");
		}
	} else {	
		modelAndView.addObject("msg", "The email provided does not exist!");
		modelAndView.setViewName("login");
	}	
	return modelAndView;
}

long loggedInUser;

ApiCalls ap=new ApiCalls();

@GetMapping("/logHome")
public ModelAndView loghomeGet(ModelAndView modelAndView,UserIp userip)
{
	modelAndView.setViewName("loginHome");
	return modelAndView;
}



@GetMapping("/uguides")
public ModelAndView uguidet(ModelAndView modelAndView,UserIp userip)
{
	modelAndView.addObject("userip", userip);
	modelAndView.setViewName("uguide");
	return modelAndView;
}

	@GetMapping("/countryList")
	public ModelAndView CountryListMVC(ModelAndView modelAndView,UserIp userip)
	{
		modelAndView.addObject("userip", userip);
		modelAndView.setViewName("countryList");
		return modelAndView;
	}

	@GetMapping("/currencyList")
	public ModelAndView currencyListMVC(ModelAndView modelAndView,UserIp userip)
	{
		modelAndView.addObject("userip", userip);
		modelAndView.setViewName("currencyList");
		return modelAndView;
	}



//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	//GetMapping for fetching IATA,Country and Name of the Airport
	@GetMapping("/apiExample")
	public ModelAndView apiExample(ModelAndView modelAndView) throws IOException, InterruptedException, ParserConfigurationException, SAXException {

		// Make the API call and get the response as a string
		String str = ap.apiExample();

		//Converting XML data to JSON object
		JSONObject xmlJSONObj = XML.toJSONObject(str);
		System.out.println(xmlJSONObj.toString());

		// Convert the JSON object to a map
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> jsonMap = mapper.readValue(xmlJSONObj.toString(), Map.class);

		Map<String, Object> airports = (Map<String, Object>) jsonMap.get("Airports");
		List<Map<String, Object>> airportList = (List<Map<String, Object>>) airports.get("Airport");

		// Iterate over the list of airports to get the names
		List<Map<String, String>> airportDetails = new ArrayList<>();
		for (Map<String, Object> airport : airportList) {
			Map<String, String> detail = new HashMap<>();
			detail.put("name", (String) airport.get("Name"));
			detail.put("code", (String) airport.get("IATACode"));
			detail.put("country", (String) airport.get("Country"));
			airportDetails.add(detail);
		}
		modelAndView.addObject("airportDetails", airportDetails);

		// Set the view name
		modelAndView.setViewName("uguide");

		// Return the ModelAndView object
		return modelAndView;

}

	@GetMapping("/countryListHelper")
	public ModelAndView CountryListHelperMVC(ModelAndView modelAndView) throws IOException, InterruptedException, ParserConfigurationException, SAXException {

		// Make the API call and get the response as a string
		String str = ap.countryListAPI();

		//Converting XML data to JSON object
		JSONObject json = new JSONObject(str);
		JSONArray countryArray = json.getJSONArray("country");
		List<Map<String, String>> CountryDetails = new ArrayList<>();
		JSONObject eachCountryObj123 = countryArray.getJSONObject(1);
		System.out.println(eachCountryObj123.get("nicename"));

		for(int i=0;i<countryArray.length();i++) {
			JSONObject eachCountryObj = countryArray.getJSONObject(i);
				Map<String, String> detail = new HashMap<>();
				detail.put("name",  eachCountryObj.get("id").toString());
				detail.put("code", eachCountryObj.get("iso3").toString());
				detail.put("country", eachCountryObj.get("nicename").toString());
				CountryDetails.add(detail);
		}

		modelAndView.addObject("CountryDetails", CountryDetails);

		// Set the view name
		modelAndView.setViewName("countryList");

		// Return the ModelAndView object
		return modelAndView;
	}

	@GetMapping("/currencyListHelper")
	public ModelAndView CurrencyListHelperMVC(ModelAndView modelAndView) throws IOException, InterruptedException, ParserConfigurationException, SAXException {

		// Make the API call and get the response as a string
		String str = ap.currenciesListAPI();

		//Converting XML data to JSON object
		JSONObject json = new JSONObject(str);
		JSONObject jsonResponse = json.getJSONObject("response");
		JSONObject jsonWithCurrencies = jsonResponse.getJSONObject("fiats");
		Iterator<String> json3 = jsonWithCurrencies.keys();
		List<Map<String, String>> currencyDetails = new ArrayList<>();

		while (json3.hasNext()) {
			JSONObject eachCountryObj = jsonWithCurrencies.getJSONObject(json3.next());
			Map<String, String> detail = new HashMap<>();
			detail.put("name",  eachCountryObj.get("currency_name").toString());
			detail.put("code", eachCountryObj.get("currency_code").toString());
			currencyDetails.add(detail);
		}

		modelAndView.addObject("currencyDetails", currencyDetails);

		// Set the view name
		modelAndView.setViewName("currencyList");

		// Return the ModelAndView object
		return modelAndView;
	}

	@GetMapping("/cities")
	public ModelAndView citiesMVC(ModelAndView modelAndView) throws IOException, InterruptedException, ParserConfigurationException, SAXException {

		List<String> detail = new ArrayList<>();


			detail.add("London"  );
			detail.add("Bengaluru" );
			detail.add("Chennai");
			detail.add("Moscow" );
			detail.add("New York City" );
			detail.add("Florida" );
			detail.add("Tokyo" );
			detail.add("Dublin");
			detail.add("Helsinki");
			detail.add("Melbourne");
			detail.add("Sydney");
			detail.add("Dubai");


		modelAndView.addObject("CitiesDetails", detail);

		// Set the view name
		modelAndView.setViewName("City");

		// Return the ModelAndView object
		return modelAndView;
	}



	//GetMapping for fetching Flight details
	@GetMapping("/flightSchedules")
	public ModelAndView flightSchedulesGet(ModelAndView modelAndView,UserIp userip)
	{
		modelAndView.addObject("userip", userip);
		modelAndView.setViewName("dprice");
		return modelAndView;
	}

	//Post Mapping for the form for searching flight api
	@PostMapping(value="/flightSchedules")
	public ModelAndView flightSchedulesForm(ModelAndView modelAndView, UserIp userip) throws InterruptedException, IOException, ParseException {

		//Using Model to fetch data
		String depature, arrival, date;

		depature = userip.getDepature();
		arrival = userip.getArrival();
		date = userip.getDate();

		// Make the API call and get the response as a string

		String str = ap.flightSchedules(depature, arrival, date);
		System.out.println("data is" + depature + " " + arrival + " " + date);

		// Convert the XML document to a JSON object
		JSONObject xmlJSONObj = XML.toJSONObject(str);

		// Convert the JSON object to a map
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(xmlJSONObj.toString());

		// Get the "FlightDetails" array
		JsonNode flightDetails = root.get("OTA_AirDetailsRS").get("FlightDetails");
		if (flightDetails == null) {
			modelAndView.addObject("flightDetails", null);
			modelAndView.addObject("userip", userip);
			modelAndView.setViewName("dprice");
			return modelAndView;
		} else {

			//Iterating "FlightDetail" json
			List<Map<String, String>> flightDetailsSet = new ArrayList<>();
			for (JsonNode flightDetail : flightDetails) {
				Map<String, String> detail = new HashMap<>();

				//fetching required data from "FlightDetail" api
				String FLSDepartureName = flightDetail.get("FLSDepartureName").toString();
				String FLSArrivalName = flightDetail.get("FLSArrivalName").toString();
				String FLSDepartureDateTime = flightDetail.get("FLSDepartureDateTime").toString();
				String FLSArrivalDateTime = flightDetail.get("FLSArrivalDateTime").toString();


				detail.put("startDate", FLSDepartureDateTime);
				detail.put("endDate", FLSArrivalDateTime);


				// Get the "FlightLegDetails" array inside "FlightDetail"
				JsonNode flightLegDetails = flightDetail.get("FlightLegDetails");


//			 Iterate over the "FlightLegDetails" array
				for (JsonNode flightLegDetail : flightLegDetails) {

					//Fetch flight number details
					String flightNumber = flightLegDetail.get("FlightNumber") == null ? "NA" : flightLegDetail.get("FlightNumber").toString();
					detail.put("flightNumber", flightNumber);


					//Fetching Depature Airport Name
					JsonNode flightLegDetailsDepatureMap = flightLegDetail.get("DepartureAirport");

					if(flightLegDetailsDepatureMap==null||flightLegDetailsDepatureMap.get("FLSLocationName")==null){
						detail.put("depatureAirport", "NA");
					}
					else {

							String depatureAirport = flightLegDetailsDepatureMap.get("FLSLocationName").toString();
							detail.put("depatureAirport", depatureAirport + "," + FLSDepartureName);

						}


					//Fetching Arrival Airport Name

					JsonNode flightLegDetailsArrivalMap= flightLegDetail.get("ArrivalAirport");
					if(flightLegDetailsArrivalMap==null||flightLegDetailsArrivalMap.get("FLSLocationName")==null){
						detail.put("arrivalAirport", "NA");
					}
					else {

							String arrivalAirport = flightLegDetailsArrivalMap.get("FLSLocationName").toString();
							detail.put("arrivalAirport", arrivalAirport + "," + FLSArrivalName);

						}


					//Fetching Airlines Name
					JsonNode flightLegDetailsAirlinesMap= flightLegDetail.get("MarketingAirline");
					if(flightLegDetailsAirlinesMap==null||flightLegDetailsAirlinesMap.get("CompanyShortName")==null){
						detail.put("airlinesName", "NA");
					}
					else {
							String airlinesName = flightLegDetailsAirlinesMap.get("CompanyShortName").toString();
							detail.put("airlinesName", airlinesName);
					}




//				JsonNode flightLegDetails0 = flightDetail.get(0);
//
//				// Iterate over the "FlightLegDetails" array
//				for (JsonNode flightLegDetail0 : flightLegDetails0) {
//
//					// Get the "FlightNumber" field
//
//					System.out.println(flightNumber);
//
//					detail.put("flightNumber", flightNumber);
				}

				flightDetailsSet.add(detail);
			}




		System.out.println("answer" + flightDetails.get(0).get("FLSArrivalName"));
		modelAndView.addObject("flightDetails", flightDetailsSet);
		modelAndView.addObject("userip", userip);
		modelAndView.setViewName("dprice");
		return modelAndView;
	}
	}

	@GetMapping("/bestFlight")
	public ModelAndView bestFlightGet(ModelAndView modelAndView,UserIp userip)
	{
		modelAndView.addObject("userip", userip);
		modelAndView.setViewName("bestFlight");
		return modelAndView;
	}

	//Post Mapping for the form for searching flight api
	@PostMapping(value="/bestFlight")
	public ModelAndView bestFlightForm(ModelAndView modelAndView, UserIp userip) throws InterruptedException, IOException, ParseException {

		//Using Model to fetch data
		String depature, arrival, date, no;


		depature = userip.getDepature();
		arrival = userip.getArrival();
		date = userip.getDate();
		no = userip.getNo();

		// Make the API call and get the response as a string

		String str = ap.bestFlight(depature,arrival,date,no);
		// String str = "{\n" +
		// 		"  \"itineraries\": {\n" +
		// 		"    \"buckets\": [\n" +
		// 		"      {\n" +
		// 		"        \"id\": \"Best\",\n" +
		// 		"        \"name\": \"Best\",\n" +
		// 		"        \"items\": [\n" +
		// 		"          {\n" +
		// 		"            \"id\": \"14385-2210111500--32090-0-9828-2210111605\",\n" +
		// 		"            \"price\": {\n" +
		// 		"              \"raw\": 94,\n" +
		// 		"              \"formatted\": \"94 €\"\n" +
		// 		"            },\n" +
		// 		"            \"legs\": [\n" +
		// 		"              {\n" +
		// 		"                \"id\": \"14385-2210111500--32090-0-9828-2210111605\",\n" +
		// 		"                \"origin\": {\n" +
		// 		"                  \"id\": \"MUC\",\n" +
		// 		"                  \"name\": \"Munich\",\n" +
		// 		"                  \"displayCode\": \"MUC\",\n" +
		// 		"                  \"city\": \"Munich\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"destination\": {\n" +
		// 		"                  \"id\": \"BER\",\n" +
		// 		"                  \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                  \"displayCode\": \"BER\",\n" +
		// 		"                  \"city\": \"Berlin\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"durationInMinutes\": 65,\n" +
		// 		"                \"stopCount\": 0,\n" +
		// 		"                \"isSmallestStops\": true,\n" +
		// 		"                \"departure\": \"2022-10-11T15:00:00\",\n" +
		// 		"                \"arrival\": \"2022-10-11T16:05:00\",\n" +
		// 		"                \"timeDeltaInDays\": 0,\n" +
		// 		"                \"carriers\": {\n" +
		// 		"                  \"marketing\": [\n" +
		// 		"                    {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"logoUrl\": \"https://logos.skyscnr.com/images/airlines/favicon/LH.png\",\n" +
		// 		"                      \"name\": \"Lufthansa\"\n" +
		// 		"                    }\n" +
		// 		"                  ],\n" +
		// 		"                  \"operationType\": \"fully_operated\"\n" +
		// 		"                },\n" +
		// 		"                \"segments\": [\n" +
		// 		"                  {\n" +
		// 		"                    \"id\": \"14385-9828-2210111500-2210111605--32090\",\n" +
		// 		"                    \"origin\": {\n" +
		// 		"                      \"flightPlaceId\": \"MUC\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"MUNI\",\n" +
		// 		"                        \"name\": \"Munich\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Munich\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"destination\": {\n" +
		// 		"                      \"flightPlaceId\": \"BER\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"BERL\",\n" +
		// 		"                        \"name\": \"Berlin\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"departure\": \"2022-10-11T15:00:00\",\n" +
		// 		"                    \"arrival\": \"2022-10-11T16:05:00\",\n" +
		// 		"                    \"durationInMinutes\": 65,\n" +
		// 		"                    \"flightNumber\": \"1944\",\n" +
		// 		"                    \"marketingCarrier\": {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"name\": \"Lufthansa\",\n" +
		// 		"                      \"alternateId\": \"LH\",\n" +
		// 		"                      \"allianceId\": -31999\n" +
		// 		"                    },\n" +
		// 		"                    \"operatingCarrier\": {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"name\": \"Lufthansa\",\n" +
		// 		"                      \"alternateId\": \"LH\",\n" +
		// 		"                      \"allianceId\": -31999\n" +
		// 		"                    }\n" +
		// 		"                  }\n" +
		// 		"                ]\n" +
		// 		"              }\n" +
		// 		"            ],\n" +
		// 		"            \"isSelfTransfer\": false,\n" +
		// 		"            \"tags\": [\n" +
		// 		"              \"shortest\"\n" +
		// 		"            ],\n" +
		// 		"            \"isMashUp\": false,\n" +
		// 		"            \"hasFlexibleOptions\": false,\n" +
		// 		"            \"score\": 7.59495,\n" +
		// 		"            \"deeplink\": \"https://www.skyscanner.net/transport/flights/muc/ber/221011/config/14385-2210111500--32090-0-9828-2210111605?adults=1&adultsv2=1&cabinclass=economy&children=0&childrenv2=&destinationentityid=27547053&originentityid=27545034&inboundaltsenabled=false&infants=0&outboundaltsenabled=false&preferdirects=false&ref=home&rtn=0\"\n" +
		// 		"          },\n" +
		// 		"          {\n" +
		// 		"            \"id\": \"14385-2210111900--32090-0-9828-2210112005\",\n" +
		// 		"            \"price\": {\n" +
		// 		"              \"raw\": 94,\n" +
		// 		"              \"formatted\": \"94 €\"\n" +
		// 		"            },\n" +
		// 		"            \"legs\": [\n" +
		// 		"              {\n" +
		// 		"                \"id\": \"14385-2210111900--32090-0-9828-2210112005\",\n" +
		// 		"                \"origin\": {\n" +
		// 		"                  \"id\": \"MUC\",\n" +
		// 		"                  \"name\": \"Munich\",\n" +
		// 		"                  \"displayCode\": \"MUC\",\n" +
		// 		"                  \"city\": \"Munich\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"destination\": {\n" +
		// 		"                  \"id\": \"BER\",\n" +
		// 		"                  \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                  \"displayCode\": \"BER\",\n" +
		// 		"                  \"city\": \"Berlin\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"durationInMinutes\": 65,\n" +
		// 		"                \"stopCount\": 0,\n" +
		// 		"                \"isSmallestStops\": true,\n" +
		// 		"                \"departure\": \"2022-10-11T19:00:00\",\n" +
		// 		"                \"arrival\": \"2022-10-11T20:05:00\",\n" +
		// 		"                \"timeDeltaInDays\": 0,\n" +
		// 		"                \"carriers\": {\n" +
		// 		"                  \"marketing\": [\n" +
		// 		"                    {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"logoUrl\": \"https://logos.skyscnr.com/images/airlines/favicon/LH.png\",\n" +
		// 		"                      \"name\": \"Lufthansa\"\n" +
		// 		"                    }\n" +
		// 		"                  ],\n" +
		// 		"                  \"operationType\": \"fully_operated\"\n" +
		// 		"                },\n" +
		// 		"                \"segments\": [\n" +
		// 		"                  {\n" +
		// 		"                    \"id\": \"14385-9828-2210111900-2210112005--32090\",\n" +
		// 		"                    \"origin\": {\n" +
		// 		"                      \"flightPlaceId\": \"MUC\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"MUNI\",\n" +
		// 		"                        \"name\": \"Munich\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Munich\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"destination\": {\n" +
		// 		"                      \"flightPlaceId\": \"BER\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"BERL\",\n" +
		// 		"                        \"name\": \"Berlin\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"departure\": \"2022-10-11T19:00:00\",\n" +
		// 		"                    \"arrival\": \"2022-10-11T20:05:00\",\n" +
		// 		"                    \"durationInMinutes\": 65,\n" +
		// 		"                    \"flightNumber\": \"1942\",\n" +
		// 		"                    \"marketingCarrier\": {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"name\": \"Lufthansa\",\n" +
		// 		"                      \"alternateId\": \"LH\",\n" +
		// 		"                      \"allianceId\": -31999\n" +
		// 		"                    },\n" +
		// 		"                    \"operatingCarrier\": {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"name\": \"Lufthansa\",\n" +
		// 		"                      \"alternateId\": \"LH\",\n" +
		// 		"                      \"allianceId\": -31999\n" +
		// 		"                    }\n" +
		// 		"                  }\n" +
		// 		"                ]\n" +
		// 		"              }\n" +
		// 		"            ],\n" +
		// 		"            \"isSelfTransfer\": false,\n" +
		// 		"            \"tags\": [\n" +
		// 		"              \"shortest\"\n" +
		// 		"            ],\n" +
		// 		"            \"isMashUp\": false,\n" +
		// 		"            \"hasFlexibleOptions\": false,\n" +
		// 		"            \"score\": 7.58894,\n" +
		// 		"            \"deeplink\": \"https://www.skyscanner.net/transport/flights/muc/ber/221011/config/14385-2210111900--32090-0-9828-2210112005?adults=1&adultsv2=1&cabinclass=economy&children=0&childrenv2=&destinationentityid=27547053&originentityid=27545034&inboundaltsenabled=false&infants=0&outboundaltsenabled=false&preferdirects=false&ref=home&rtn=0\"\n" +
		// 		"          },\n" +
		// 		"          {\n" +
		// 		"            \"id\": \"14385-2210111100--32090-0-9828-2210111205\",\n" +
		// 		"            \"price\": {\n" +
		// 		"              \"raw\": 94,\n" +
		// 		"              \"formatted\": \"94 €\"\n" +
		// 		"            },\n" +
		// 		"            \"legs\": [\n" +
		// 		"              {\n" +
		// 		"                \"id\": \"14385-2210111100--32090-0-9828-2210111205\",\n" +
		// 		"                \"origin\": {\n" +
		// 		"                  \"id\": \"MUC\",\n" +
		// 		"                  \"name\": \"Munich\",\n" +
		// 		"                  \"displayCode\": \"MUC\",\n" +
		// 		"                  \"city\": \"Munich\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"destination\": {\n" +
		// 		"                  \"id\": \"BER\",\n" +
		// 		"                  \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                  \"displayCode\": \"BER\",\n" +
		// 		"                  \"city\": \"Berlin\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"durationInMinutes\": 65,\n" +
		// 		"                \"stopCount\": 0,\n" +
		// 		"                \"isSmallestStops\": true,\n" +
		// 		"                \"departure\": \"2022-10-11T11:00:00\",\n" +
		// 		"                \"arrival\": \"2022-10-11T12:05:00\",\n" +
		// 		"                \"timeDeltaInDays\": 0,\n" +
		// 		"                \"carriers\": {\n" +
		// 		"                  \"marketing\": [\n" +
		// 		"                    {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"logoUrl\": \"https://logos.skyscnr.com/images/airlines/favicon/LH.png\",\n" +
		// 		"                      \"name\": \"Lufthansa\"\n" +
		// 		"                    }\n" +
		// 		"                  ],\n" +
		// 		"                  \"operationType\": \"fully_operated\"\n" +
		// 		"                },\n" +
		// 		"                \"segments\": [\n" +
		// 		"                  {\n" +
		// 		"                    \"id\": \"14385-9828-2210111100-2210111205--32090\",\n" +
		// 		"                    \"origin\": {\n" +
		// 		"                      \"flightPlaceId\": \"MUC\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"MUNI\",\n" +
		// 		"                        \"name\": \"Munich\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Munich\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"destination\": {\n" +
		// 		"                      \"flightPlaceId\": \"BER\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"BERL\",\n" +
		// 		"                        \"name\": \"Berlin\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"departure\": \"2022-10-11T11:00:00\",\n" +
		// 		"                    \"arrival\": \"2022-10-11T12:05:00\",\n" +
		// 		"                    \"durationInMinutes\": 65,\n" +
		// 		"                    \"flightNumber\": \"1928\",\n" +
		// 		"                    \"marketingCarrier\": {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"name\": \"Lufthansa\",\n" +
		// 		"                      \"alternateId\": \"LH\",\n" +
		// 		"                      \"allianceId\": -31999\n" +
		// 		"                    },\n" +
		// 		"                    \"operatingCarrier\": {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"name\": \"Lufthansa\",\n" +
		// 		"                      \"alternateId\": \"LH\",\n" +
		// 		"                      \"allianceId\": -31999\n" +
		// 		"                    }\n" +
		// 		"                  }\n" +
		// 		"                ]\n" +
		// 		"              }\n" +
		// 		"            ],\n" +
		// 		"            \"isSelfTransfer\": false,\n" +
		// 		"            \"tags\": [\n" +
		// 		"              \"shortest\"\n" +
		// 		"            ],\n" +
		// 		"            \"isMashUp\": false,\n" +
		// 		"            \"hasFlexibleOptions\": false,\n" +
		// 		"            \"score\": 7.58729,\n" +
		// 		"            \"deeplink\": \"https://www.skyscanner.net/transport/flights/muc/ber/221011/config/14385-2210111100--32090-0-9828-2210111205?adults=1&adultsv2=1&cabinclass=economy&children=0&childrenv2=&destinationentityid=27547053&originentityid=27545034&inboundaltsenabled=false&infants=0&outboundaltsenabled=false&preferdirects=false&ref=home&rtn=0\"\n" +
		// 		"          }\n" +
		// 		"        ]\n" +
		// 		"      },\n" +
		// 		"      {\n" +
		// 		"        \"id\": \"Fastest\",\n" +
		// 		"        \"name\": \"Fastest\",\n" +
		// 		"        \"items\": [\n" +
		// 		"          {\n" +
		// 		"            \"id\": \"14385-2210111900--32090-0-9828-2210112005\",\n" +
		// 		"            \"price\": {\n" +
		// 		"              \"raw\": 94,\n" +
		// 		"              \"formatted\": \"94 €\"\n" +
		// 		"            },\n" +
		// 		"            \"legs\": [\n" +
		// 		"              {\n" +
		// 		"                \"id\": \"14385-2210111900--32090-0-9828-2210112005\",\n" +
		// 		"                \"origin\": {\n" +
		// 		"                  \"id\": \"MUC\",\n" +
		// 		"                  \"name\": \"Munich\",\n" +
		// 		"                  \"displayCode\": \"MUC\",\n" +
		// 		"                  \"city\": \"Munich\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"destination\": {\n" +
		// 		"                  \"id\": \"BER\",\n" +
		// 		"                  \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                  \"displayCode\": \"BER\",\n" +
		// 		"                  \"city\": \"Berlin\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"durationInMinutes\": 65,\n" +
		// 		"                \"stopCount\": 0,\n" +
		// 		"                \"isSmallestStops\": true,\n" +
		// 		"                \"departure\": \"2022-10-11T19:00:00\",\n" +
		// 		"                \"arrival\": \"2022-10-11T20:05:00\",\n" +
		// 		"                \"timeDeltaInDays\": 0,\n" +
		// 		"                \"carriers\": {\n" +
		// 		"                  \"marketing\": [\n" +
		// 		"                    {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"logoUrl\": \"https://logos.skyscnr.com/images/airlines/favicon/LH.png\",\n" +
		// 		"                      \"name\": \"Lufthansa\"\n" +
		// 		"                    }\n" +
		// 		"                  ],\n" +
		// 		"                  \"operationType\": \"fully_operated\"\n" +
		// 		"                },\n" +
		// 		"                \"segments\": [\n" +
		// 		"                  {\n" +
		// 		"                    \"id\": \"14385-9828-2210111900-2210112005--32090\",\n" +
		// 		"                    \"origin\": {\n" +
		// 		"                      \"flightPlaceId\": \"MUC\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"MUNI\",\n" +
		// 		"                        \"name\": \"Munich\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Munich\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"destination\": {\n" +
		// 		"                      \"flightPlaceId\": \"BER\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"BERL\",\n" +
		// 		"                        \"name\": \"Berlin\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"departure\": \"2022-10-11T19:00:00\",\n" +
		// 		"                    \"arrival\": \"2022-10-11T20:05:00\",\n" +
		// 		"                    \"durationInMinutes\": 65,\n" +
		// 		"                    \"flightNumber\": \"1942\",\n" +
		// 		"                    \"marketingCarrier\": {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"name\": \"Lufthansa\",\n" +
		// 		"                      \"alternateId\": \"LH\",\n" +
		// 		"                      \"allianceId\": -31999\n" +
		// 		"                    },\n" +
		// 		"                    \"operatingCarrier\": {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"name\": \"Lufthansa\",\n" +
		// 		"                      \"alternateId\": \"LH\",\n" +
		// 		"                      \"allianceId\": -31999\n" +
		// 		"                    }\n" +
		// 		"                  }\n" +
		// 		"                ]\n" +
		// 		"              }\n" +
		// 		"            ],\n" +
		// 		"            \"isSelfTransfer\": false,\n" +
		// 		"            \"tags\": [\n" +
		// 		"              \"shortest\"\n" +
		// 		"            ],\n" +
		// 		"            \"isMashUp\": false,\n" +
		// 		"            \"hasFlexibleOptions\": false,\n" +
		// 		"            \"score\": 7.58894,\n" +
		// 		"            \"deeplink\": \"https://www.skyscanner.net/transport/flights/muc/ber/221011/config/14385-2210111900--32090-0-9828-2210112005?adults=1&adultsv2=1&cabinclass=economy&children=0&childrenv2=&destinationentityid=27547053&originentityid=27545034&inboundaltsenabled=false&infants=0&outboundaltsenabled=false&preferdirects=false&ref=home&rtn=0\"\n" +
		// 		"          },\n" +
		// 		"          {\n" +
		// 		"            \"id\": \"14385-2210111800--32090-0-9828-2210111905\",\n" +
		// 		"            \"price\": {\n" +
		// 		"              \"raw\": 100,\n" +
		// 		"              \"formatted\": \"100 €\"\n" +
		// 		"            },\n" +
		// 		"            \"legs\": [\n" +
		// 		"              {\n" +
		// 		"                \"id\": \"14385-2210111800--32090-0-9828-2210111905\",\n" +
		// 		"                \"origin\": {\n" +
		// 		"                  \"id\": \"MUC\",\n" +
		// 		"                  \"name\": \"Munich\",\n" +
		// 		"                  \"displayCode\": \"MUC\",\n" +
		// 		"                  \"city\": \"Munich\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"destination\": {\n" +
		// 		"                  \"id\": \"BER\",\n" +
		// 		"                  \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                  \"displayCode\": \"BER\",\n" +
		// 		"                  \"city\": \"Berlin\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"durationInMinutes\": 65,\n" +
		// 		"                \"stopCount\": 0,\n" +
		// 		"                \"isSmallestStops\": true,\n" +
		// 		"                \"departure\": \"2022-10-11T18:00:00\",\n" +
		// 		"                \"arrival\": \"2022-10-11T19:05:00\",\n" +
		// 		"                \"timeDeltaInDays\": 0,\n" +
		// 		"                \"carriers\": {\n" +
		// 		"                  \"marketing\": [\n" +
		// 		"                    {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"logoUrl\": \"https://logos.skyscnr.com/images/airlines/favicon/LH.png\",\n" +
		// 		"                      \"name\": \"Lufthansa\"\n" +
		// 		"                    }\n" +
		// 		"                  ],\n" +
		// 		"                  \"operationType\": \"fully_operated\"\n" +
		// 		"                },\n" +
		// 		"                \"segments\": [\n" +
		// 		"                  {\n" +
		// 		"                    \"id\": \"14385-9828-2210111800-2210111905--32090\",\n" +
		// 		"                    \"origin\": {\n" +
		// 		"                      \"flightPlaceId\": \"MUC\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"MUNI\",\n" +
		// 		"                        \"name\": \"Munich\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Munich\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"destination\": {\n" +
		// 		"                      \"flightPlaceId\": \"BER\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"BERL\",\n" +
		// 		"                        \"name\": \"Berlin\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"departure\": \"2022-10-11T18:00:00\",\n" +
		// 		"                    \"arrival\": \"2022-10-11T19:05:00\",\n" +
		// 		"                    \"durationInMinutes\": 65,\n" +
		// 		"                    \"flightNumber\": \"1952\",\n" +
		// 		"                    \"marketingCarrier\": {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"name\": \"Lufthansa\",\n" +
		// 		"                      \"alternateId\": \"LH\",\n" +
		// 		"                      \"allianceId\": -31999\n" +
		// 		"                    },\n" +
		// 		"                    \"operatingCarrier\": {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"name\": \"Lufthansa\",\n" +
		// 		"                      \"alternateId\": \"LH\",\n" +
		// 		"                      \"allianceId\": -31999\n" +
		// 		"                    }\n" +
		// 		"                  }\n" +
		// 		"                ]\n" +
		// 		"              }\n" +
		// 		"            ],\n" +
		// 		"            \"isSelfTransfer\": false,\n" +
		// 		"            \"tags\": [\n" +
		// 		"              \"shortest\"\n" +
		// 		"            ],\n" +
		// 		"            \"isMashUp\": false,\n" +
		// 		"            \"hasFlexibleOptions\": false,\n" +
		// 		"            \"score\": 7.12473,\n" +
		// 		"            \"deeplink\": \"https://www.skyscanner.net/transport/flights/muc/ber/221011/config/14385-2210111800--32090-0-9828-2210111905?adults=1&adultsv2=1&cabinclass=economy&children=0&childrenv2=&destinationentityid=27547053&originentityid=27545034&inboundaltsenabled=false&infants=0&outboundaltsenabled=false&preferdirects=false&ref=home&rtn=0\"\n" +
		// 		"          },\n" +
		// 		"          {\n" +
		// 		"            \"id\": \"14385-2210111300--32090-0-9828-2210111405\",\n" +
		// 		"            \"price\": {\n" +
		// 		"              \"raw\": 235,\n" +
		// 		"              \"formatted\": \"235 €\"\n" +
		// 		"            },\n" +
		// 		"            \"legs\": [\n" +
		// 		"              {\n" +
		// 		"                \"id\": \"14385-2210111300--32090-0-9828-2210111405\",\n" +
		// 		"                \"origin\": {\n" +
		// 		"                  \"id\": \"MUC\",\n" +
		// 		"                  \"name\": \"Munich\",\n" +
		// 		"                  \"displayCode\": \"MUC\",\n" +
		// 		"                  \"city\": \"Munich\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"destination\": {\n" +
		// 		"                  \"id\": \"BER\",\n" +
		// 		"                  \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                  \"displayCode\": \"BER\",\n" +
		// 		"                  \"city\": \"Berlin\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"durationInMinutes\": 65,\n" +
		// 		"                \"stopCount\": 0,\n" +
		// 		"                \"isSmallestStops\": true,\n" +
		// 		"                \"departure\": \"2022-10-11T13:00:00\",\n" +
		// 		"                \"arrival\": \"2022-10-11T14:05:00\",\n" +
		// 		"                \"timeDeltaInDays\": 0,\n" +
		// 		"                \"carriers\": {\n" +
		// 		"                  \"marketing\": [\n" +
		// 		"                    {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"logoUrl\": \"https://logos.skyscnr.com/images/airlines/favicon/LH.png\",\n" +
		// 		"                      \"name\": \"Lufthansa\"\n" +
		// 		"                    }\n" +
		// 		"                  ],\n" +
		// 		"                  \"operating\": [\n" +
		// 		"                    {\n" +
		// 		"                      \"id\": -32089,\n" +
		// 		"                      \"logoUrl\": \"https://logos.skyscnr.com/images/airlines/favicon/CL.png\",\n" +
		// 		"                      \"name\": \"Lufthansa CityLine\"\n" +
		// 		"                    }\n" +
		// 		"                  ],\n" +
		// 		"                  \"operationType\": \"not_operated\"\n" +
		// 		"                },\n" +
		// 		"                \"segments\": [\n" +
		// 		"                  {\n" +
		// 		"                    \"id\": \"14385-9828-2210111300-2210111405--32090\",\n" +
		// 		"                    \"origin\": {\n" +
		// 		"                      \"flightPlaceId\": \"MUC\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"MUNI\",\n" +
		// 		"                        \"name\": \"Munich\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Munich\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"destination\": {\n" +
		// 		"                      \"flightPlaceId\": \"BER\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"BERL\",\n" +
		// 		"                        \"name\": \"Berlin\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"departure\": \"2022-10-11T13:00:00\",\n" +
		// 		"                    \"arrival\": \"2022-10-11T14:05:00\",\n" +
		// 		"                    \"durationInMinutes\": 65,\n" +
		// 		"                    \"flightNumber\": \"1956\",\n" +
		// 		"                    \"marketingCarrier\": {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"name\": \"Lufthansa\",\n" +
		// 		"                      \"alternateId\": \"LH\",\n" +
		// 		"                      \"allianceId\": -31999\n" +
		// 		"                    },\n" +
		// 		"                    \"operatingCarrier\": {\n" +
		// 		"                      \"id\": -32089,\n" +
		// 		"                      \"name\": \"Lufthansa CityLine\",\n" +
		// 		"                      \"alternateId\": \"CL\",\n" +
		// 		"                      \"allianceId\": 0\n" +
		// 		"                    }\n" +
		// 		"                  }\n" +
		// 		"                ]\n" +
		// 		"              }\n" +
		// 		"            ],\n" +
		// 		"            \"isSelfTransfer\": false,\n" +
		// 		"            \"tags\": [\n" +
		// 		"              \"shortest\"\n" +
		// 		"            ],\n" +
		// 		"            \"isMashUp\": false,\n" +
		// 		"            \"hasFlexibleOptions\": false,\n" +
		// 		"            \"score\": 3.00613,\n" +
		// 		"            \"deeplink\": \"https://www.skyscanner.net/transport/flights/muc/ber/221011/config/14385-2210111300--32090-0-9828-2210111405?adults=1&adultsv2=1&cabinclass=economy&children=0&childrenv2=&destinationentityid=27547053&originentityid=27545034&inboundaltsenabled=false&infants=0&outboundaltsenabled=false&preferdirects=false&ref=home&rtn=0\"\n" +
		// 		"          }\n" +
		// 		"        ]\n" +
		// 		"      },\n" +
		// 		"      {\n" +
		// 		"        \"id\": \"Cheapest\",\n" +
		// 		"        \"name\": \"Cheapest\",\n" +
		// 		"        \"items\": [\n" +
		// 		"          {\n" +
		// 		"            \"id\": \"14385-2210110930--32332-1-9828-2210111600\",\n" +
		// 		"            \"price\": {\n" +
		// 		"              \"raw\": 71,\n" +
		// 		"              \"formatted\": \"71 €\"\n" +
		// 		"            },\n" +
		// 		"            \"legs\": [\n" +
		// 		"              {\n" +
		// 		"                \"id\": \"14385-2210110930--32332-1-9828-2210111600\",\n" +
		// 		"                \"origin\": {\n" +
		// 		"                  \"id\": \"MUC\",\n" +
		// 		"                  \"name\": \"Munich\",\n" +
		// 		"                  \"displayCode\": \"MUC\",\n" +
		// 		"                  \"city\": \"Munich\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"destination\": {\n" +
		// 		"                  \"id\": \"BER\",\n" +
		// 		"                  \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                  \"displayCode\": \"BER\",\n" +
		// 		"                  \"city\": \"Berlin\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"durationInMinutes\": 390,\n" +
		// 		"                \"stopCount\": 1,\n" +
		// 		"                \"isSmallestStops\": false,\n" +
		// 		"                \"departure\": \"2022-10-11T09:30:00\",\n" +
		// 		"                \"arrival\": \"2022-10-11T16:00:00\",\n" +
		// 		"                \"timeDeltaInDays\": 0,\n" +
		// 		"                \"carriers\": {\n" +
		// 		"                  \"marketing\": [\n" +
		// 		"                    {\n" +
		// 		"                      \"id\": -32332,\n" +
		// 		"                      \"logoUrl\": \"https://logos.skyscnr.com/images/airlines/favicon/EW.png\",\n" +
		// 		"                      \"name\": \"Eurowings\"\n" +
		// 		"                    }\n" +
		// 		"                  ],\n" +
		// 		"                  \"operationType\": \"fully_operated\"\n" +
		// 		"                },\n" +
		// 		"                \"segments\": [\n" +
		// 		"                  {\n" +
		// 		"                    \"id\": \"14385-11165-2210110930-2210111045--32332\",\n" +
		// 		"                    \"origin\": {\n" +
		// 		"                      \"flightPlaceId\": \"MUC\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"MUNI\",\n" +
		// 		"                        \"name\": \"Munich\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Munich\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"destination\": {\n" +
		// 		"                      \"flightPlaceId\": \"DUS\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"DUSS\",\n" +
		// 		"                        \"name\": \"Dusseldorf\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Dusseldorf International\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"departure\": \"2022-10-11T09:30:00\",\n" +
		// 		"                    \"arrival\": \"2022-10-11T10:45:00\",\n" +
		// 		"                    \"durationInMinutes\": 75,\n" +
		// 		"                    \"flightNumber\": \"9083\",\n" +
		// 		"                    \"marketingCarrier\": {\n" +
		// 		"                      \"id\": -32332,\n" +
		// 		"                      \"name\": \"Eurowings\",\n" +
		// 		"                      \"alternateId\": \"EW\",\n" +
		// 		"                      \"allianceId\": 0\n" +
		// 		"                    },\n" +
		// 		"                    \"operatingCarrier\": {\n" +
		// 		"                      \"id\": -32332,\n" +
		// 		"                      \"name\": \"Eurowings\",\n" +
		// 		"                      \"alternateId\": \"EW\",\n" +
		// 		"                      \"allianceId\": 0\n" +
		// 		"                    }\n" +
		// 		"                  },\n" +
		// 		"                  {\n" +
		// 		"                    \"id\": \"11165-9828-2210111445-2210111600--32332\",\n" +
		// 		"                    \"origin\": {\n" +
		// 		"                      \"flightPlaceId\": \"DUS\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"DUSS\",\n" +
		// 		"                        \"name\": \"Dusseldorf\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Dusseldorf International\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"destination\": {\n" +
		// 		"                      \"flightPlaceId\": \"BER\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"BERL\",\n" +
		// 		"                        \"name\": \"Berlin\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"departure\": \"2022-10-11T14:45:00\",\n" +
		// 		"                    \"arrival\": \"2022-10-11T16:00:00\",\n" +
		// 		"                    \"durationInMinutes\": 75,\n" +
		// 		"                    \"flightNumber\": \"9042\",\n" +
		// 		"                    \"marketingCarrier\": {\n" +
		// 		"                      \"id\": -32332,\n" +
		// 		"                      \"name\": \"Eurowings\",\n" +
		// 		"                      \"alternateId\": \"EW\",\n" +
		// 		"                      \"allianceId\": 0\n" +
		// 		"                    },\n" +
		// 		"                    \"operatingCarrier\": {\n" +
		// 		"                      \"id\": -32332,\n" +
		// 		"                      \"name\": \"Eurowings\",\n" +
		// 		"                      \"alternateId\": \"EW\",\n" +
		// 		"                      \"allianceId\": 0\n" +
		// 		"                    }\n" +
		// 		"                  }\n" +
		// 		"                ]\n" +
		// 		"              }\n" +
		// 		"            ],\n" +
		// 		"            \"isSelfTransfer\": false,\n" +
		// 		"            \"isMashUp\": false,\n" +
		// 		"            \"hasFlexibleOptions\": false,\n" +
		// 		"            \"score\": 1.66417,\n" +
		// 		"            \"deeplink\": \"https://www.skyscanner.net/transport/flights/muc/ber/221011/config/14385-2210110930--32332-1-9828-2210111600?adults=1&adultsv2=1&cabinclass=economy&children=0&childrenv2=&destinationentityid=27547053&originentityid=27545034&inboundaltsenabled=false&infants=0&outboundaltsenabled=false&preferdirects=false&ref=home&rtn=0\"\n" +
		// 		"          },\n" +
		// 		"          {\n" +
		// 		"            \"id\": \"14385-2210110800--32332-1-9828-2210111540\",\n" +
		// 		"            \"price\": {\n" +
		// 		"              \"raw\": 71,\n" +
		// 		"              \"formatted\": \"71 €\"\n" +
		// 		"            },\n" +
		// 		"            \"legs\": [\n" +
		// 		"              {\n" +
		// 		"                \"id\": \"14385-2210110800--32332-1-9828-2210111540\",\n" +
		// 		"                \"origin\": {\n" +
		// 		"                  \"id\": \"MUC\",\n" +
		// 		"                  \"name\": \"Munich\",\n" +
		// 		"                  \"displayCode\": \"MUC\",\n" +
		// 		"                  \"city\": \"Munich\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"destination\": {\n" +
		// 		"                  \"id\": \"BER\",\n" +
		// 		"                  \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                  \"displayCode\": \"BER\",\n" +
		// 		"                  \"city\": \"Berlin\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"durationInMinutes\": 460,\n" +
		// 		"                \"stopCount\": 1,\n" +
		// 		"                \"isSmallestStops\": false,\n" +
		// 		"                \"departure\": \"2022-10-11T08:00:00\",\n" +
		// 		"                \"arrival\": \"2022-10-11T15:40:00\",\n" +
		// 		"                \"timeDeltaInDays\": 0,\n" +
		// 		"                \"carriers\": {\n" +
		// 		"                  \"marketing\": [\n" +
		// 		"                    {\n" +
		// 		"                      \"id\": -32332,\n" +
		// 		"                      \"logoUrl\": \"https://logos.skyscnr.com/images/airlines/favicon/EW.png\",\n" +
		// 		"                      \"name\": \"Eurowings\"\n" +
		// 		"                    }\n" +
		// 		"                  ],\n" +
		// 		"                  \"operationType\": \"fully_operated\"\n" +
		// 		"                },\n" +
		// 		"                \"segments\": [\n" +
		// 		"                  {\n" +
		// 		"                    \"id\": \"14385-10487-2210110800-2210110910--32332\",\n" +
		// 		"                    \"origin\": {\n" +
		// 		"                      \"flightPlaceId\": \"MUC\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"MUNI\",\n" +
		// 		"                        \"name\": \"Munich\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Munich\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"destination\": {\n" +
		// 		"                      \"flightPlaceId\": \"CGN\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"COLO\",\n" +
		// 		"                        \"name\": \"Cologne\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Cologne\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"departure\": \"2022-10-11T08:00:00\",\n" +
		// 		"                    \"arrival\": \"2022-10-11T09:10:00\",\n" +
		// 		"                    \"durationInMinutes\": 70,\n" +
		// 		"                    \"flightNumber\": \"91\",\n" +
		// 		"                    \"marketingCarrier\": {\n" +
		// 		"                      \"id\": -32332,\n" +
		// 		"                      \"name\": \"Eurowings\",\n" +
		// 		"                      \"alternateId\": \"EW\",\n" +
		// 		"                      \"allianceId\": 0\n" +
		// 		"                    },\n" +
		// 		"                    \"operatingCarrier\": {\n" +
		// 		"                      \"id\": -32332,\n" +
		// 		"                      \"name\": \"Eurowings\",\n" +
		// 		"                      \"alternateId\": \"EW\",\n" +
		// 		"                      \"allianceId\": 0\n" +
		// 		"                    }\n" +
		// 		"                  },\n" +
		// 		"                  {\n" +
		// 		"                    \"id\": \"10487-9828-2210111430-2210111540--32332\",\n" +
		// 		"                    \"origin\": {\n" +
		// 		"                      \"flightPlaceId\": \"CGN\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"COLO\",\n" +
		// 		"                        \"name\": \"Cologne\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Cologne\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"destination\": {\n" +
		// 		"                      \"flightPlaceId\": \"BER\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"BERL\",\n" +
		// 		"                        \"name\": \"Berlin\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"departure\": \"2022-10-11T14:30:00\",\n" +
		// 		"                    \"arrival\": \"2022-10-11T15:40:00\",\n" +
		// 		"                    \"durationInMinutes\": 70,\n" +
		// 		"                    \"flightNumber\": \"50\",\n" +
		// 		"                    \"marketingCarrier\": {\n" +
		// 		"                      \"id\": -32332,\n" +
		// 		"                      \"name\": \"Eurowings\",\n" +
		// 		"                      \"alternateId\": \"EW\",\n" +
		// 		"                      \"allianceId\": 0\n" +
		// 		"                    },\n" +
		// 		"                    \"operatingCarrier\": {\n" +
		// 		"                      \"id\": -32332,\n" +
		// 		"                      \"name\": \"Eurowings\",\n" +
		// 		"                      \"alternateId\": \"EW\",\n" +
		// 		"                      \"allianceId\": 0\n" +
		// 		"                    }\n" +
		// 		"                  }\n" +
		// 		"                ]\n" +
		// 		"              }\n" +
		// 		"            ],\n" +
		// 		"            \"isSelfTransfer\": false,\n" +
		// 		"            \"isMashUp\": false,\n" +
		// 		"            \"hasFlexibleOptions\": false,\n" +
		// 		"            \"score\": 1.41093,\n" +
		// 		"            \"deeplink\": \"https://www.skyscanner.net/transport/flights/muc/ber/221011/config/14385-2210110800--32332-1-9828-2210111540?adults=1&adultsv2=1&cabinclass=economy&children=0&childrenv2=&destinationentityid=27547053&originentityid=27545034&inboundaltsenabled=false&infants=0&outboundaltsenabled=false&preferdirects=false&ref=home&rtn=0\"\n" +
		// 		"          },\n" +
		// 		"          {\n" +
		// 		"            \"id\": \"14385-2210111720--32332,-32356-1-9828-2210112210\",\n" +
		// 		"            \"price\": {\n" +
		// 		"              \"raw\": 78,\n" +
		// 		"              \"formatted\": \"78 €\"\n" +
		// 		"            },\n" +
		// 		"            \"legs\": [\n" +
		// 		"              {\n" +
		// 		"                \"id\": \"14385-2210111720--32332,-32356-1-9828-2210112210\",\n" +
		// 		"                \"origin\": {\n" +
		// 		"                  \"id\": \"MUC\",\n" +
		// 		"                  \"name\": \"Munich\",\n" +
		// 		"                  \"displayCode\": \"MUC\",\n" +
		// 		"                  \"city\": \"Munich\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"destination\": {\n" +
		// 		"                  \"id\": \"BER\",\n" +
		// 		"                  \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                  \"displayCode\": \"BER\",\n" +
		// 		"                  \"city\": \"Berlin\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"durationInMinutes\": 290,\n" +
		// 		"                \"stopCount\": 1,\n" +
		// 		"                \"isSmallestStops\": false,\n" +
		// 		"                \"departure\": \"2022-10-11T17:20:00\",\n" +
		// 		"                \"arrival\": \"2022-10-11T22:10:00\",\n" +
		// 		"                \"timeDeltaInDays\": 0,\n" +
		// 		"                \"carriers\": {\n" +
		// 		"                  \"marketing\": [\n" +
		// 		"                    {\n" +
		// 		"                      \"id\": -32332,\n" +
		// 		"                      \"logoUrl\": \"https://logos.skyscnr.com/images/airlines/favicon/EW.png\",\n" +
		// 		"                      \"name\": \"Eurowings\"\n" +
		// 		"                    },\n" +
		// 		"                    {\n" +
		// 		"                      \"id\": -32356,\n" +
		// 		"                      \"logoUrl\": \"https://logos.skyscnr.com/images/airlines/favicon/EZ.png\",\n" +
		// 		"                      \"name\": \"easyJet\"\n" +
		// 		"                    }\n" +
		// 		"                  ],\n" +
		// 		"                  \"operationType\": \"fully_operated\"\n" +
		// 		"                },\n" +
		// 		"                \"segments\": [\n" +
		// 		"                  {\n" +
		// 		"                    \"id\": \"14385-10487-2210111720-2210111830--32332\",\n" +
		// 		"                    \"origin\": {\n" +
		// 		"                      \"flightPlaceId\": \"MUC\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"MUNI\",\n" +
		// 		"                        \"name\": \"Munich\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Munich\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"destination\": {\n" +
		// 		"                      \"flightPlaceId\": \"CGN\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"COLO\",\n" +
		// 		"                        \"name\": \"Cologne\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Cologne\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"departure\": \"2022-10-11T17:20:00\",\n" +
		// 		"                    \"arrival\": \"2022-10-11T18:30:00\",\n" +
		// 		"                    \"durationInMinutes\": 70,\n" +
		// 		"                    \"flightNumber\": \"85\",\n" +
		// 		"                    \"marketingCarrier\": {\n" +
		// 		"                      \"id\": -32332,\n" +
		// 		"                      \"name\": \"Eurowings\",\n" +
		// 		"                      \"alternateId\": \"EW\",\n" +
		// 		"                      \"allianceId\": 0\n" +
		// 		"                    },\n" +
		// 		"                    \"operatingCarrier\": {\n" +
		// 		"                      \"id\": -32332,\n" +
		// 		"                      \"name\": \"Eurowings\",\n" +
		// 		"                      \"alternateId\": \"EW\",\n" +
		// 		"                      \"allianceId\": 0\n" +
		// 		"                    }\n" +
		// 		"                  },\n" +
		// 		"                  {\n" +
		// 		"                    \"id\": \"10487-9828-2210112055-2210112210--32356\",\n" +
		// 		"                    \"origin\": {\n" +
		// 		"                      \"flightPlaceId\": \"CGN\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"COLO\",\n" +
		// 		"                        \"name\": \"Cologne\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Cologne\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"destination\": {\n" +
		// 		"                      \"flightPlaceId\": \"BER\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"BERL\",\n" +
		// 		"                        \"name\": \"Berlin\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"departure\": \"2022-10-11T20:55:00\",\n" +
		// 		"                    \"arrival\": \"2022-10-11T22:10:00\",\n" +
		// 		"                    \"durationInMinutes\": 75,\n" +
		// 		"                    \"flightNumber\": \"5514\",\n" +
		// 		"                    \"marketingCarrier\": {\n" +
		// 		"                      \"id\": -32356,\n" +
		// 		"                      \"name\": \"easyJet\",\n" +
		// 		"                      \"alternateId\": \"EZ\",\n" +
		// 		"                      \"allianceId\": 0\n" +
		// 		"                    },\n" +
		// 		"                    \"operatingCarrier\": {\n" +
		// 		"                      \"id\": -32356,\n" +
		// 		"                      \"name\": \"easyJet\",\n" +
		// 		"                      \"alternateId\": \"EZ\",\n" +
		// 		"                      \"allianceId\": 0\n" +
		// 		"                    }\n" +
		// 		"                  }\n" +
		// 		"                ]\n" +
		// 		"              }\n" +
		// 		"            ],\n" +
		// 		"            \"isSelfTransfer\": true,\n" +
		// 		"            \"isMashUp\": false,\n" +
		// 		"            \"hasFlexibleOptions\": false,\n" +
		// 		"            \"score\": 1.85533,\n" +
		// 		"            \"deeplink\": \"https://www.skyscanner.net/transport/flights/muc/ber/221011/config/14385-2210111720--32332,-32356-1-9828-2210112210?adults=1&adultsv2=1&cabinclass=economy&children=0&childrenv2=&destinationentityid=27547053&originentityid=27545034&inboundaltsenabled=false&infants=0&outboundaltsenabled=false&preferdirects=false&ref=home&rtn=0\"\n" +
		// 		"          }\n" +
		// 		"        ]\n" +
		// 		"      },\n" +
		// 		"      {\n" +
		// 		"        \"id\": \"Direct\",\n" +
		// 		"        \"name\": \"Direct\",\n" +
		// 		"        \"items\": [\n" +
		// 		"          {\n" +
		// 		"            \"id\": \"14385-2210111500--32090-0-9828-2210111605\",\n" +
		// 		"            \"price\": {\n" +
		// 		"              \"raw\": 94,\n" +
		// 		"              \"formatted\": \"94 €\"\n" +
		// 		"            },\n" +
		// 		"            \"legs\": [\n" +
		// 		"              {\n" +
		// 		"                \"id\": \"14385-2210111500--32090-0-9828-2210111605\",\n" +
		// 		"                \"origin\": {\n" +
		// 		"                  \"id\": \"MUC\",\n" +
		// 		"                  \"name\": \"Munich\",\n" +
		// 		"                  \"displayCode\": \"MUC\",\n" +
		// 		"                  \"city\": \"Munich\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"destination\": {\n" +
		// 		"                  \"id\": \"BER\",\n" +
		// 		"                  \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                  \"displayCode\": \"BER\",\n" +
		// 		"                  \"city\": \"Berlin\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"durationInMinutes\": 65,\n" +
		// 		"                \"stopCount\": 0,\n" +
		// 		"                \"isSmallestStops\": true,\n" +
		// 		"                \"departure\": \"2022-10-11T15:00:00\",\n" +
		// 		"                \"arrival\": \"2022-10-11T16:05:00\",\n" +
		// 		"                \"timeDeltaInDays\": 0,\n" +
		// 		"                \"carriers\": {\n" +
		// 		"                  \"marketing\": [\n" +
		// 		"                    {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"logoUrl\": \"https://logos.skyscnr.com/images/airlines/favicon/LH.png\",\n" +
		// 		"                      \"name\": \"Lufthansa\"\n" +
		// 		"                    }\n" +
		// 		"                  ],\n" +
		// 		"                  \"operationType\": \"fully_operated\"\n" +
		// 		"                },\n" +
		// 		"                \"segments\": [\n" +
		// 		"                  {\n" +
		// 		"                    \"id\": \"14385-9828-2210111500-2210111605--32090\",\n" +
		// 		"                    \"origin\": {\n" +
		// 		"                      \"flightPlaceId\": \"MUC\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"MUNI\",\n" +
		// 		"                        \"name\": \"Munich\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Munich\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"destination\": {\n" +
		// 		"                      \"flightPlaceId\": \"BER\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"BERL\",\n" +
		// 		"                        \"name\": \"Berlin\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"departure\": \"2022-10-11T15:00:00\",\n" +
		// 		"                    \"arrival\": \"2022-10-11T16:05:00\",\n" +
		// 		"                    \"durationInMinutes\": 65,\n" +
		// 		"                    \"flightNumber\": \"1944\",\n" +
		// 		"                    \"marketingCarrier\": {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"name\": \"Lufthansa\",\n" +
		// 		"                      \"alternateId\": \"LH\",\n" +
		// 		"                      \"allianceId\": -31999\n" +
		// 		"                    },\n" +
		// 		"                    \"operatingCarrier\": {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"name\": \"Lufthansa\",\n" +
		// 		"                      \"alternateId\": \"LH\",\n" +
		// 		"                      \"allianceId\": -31999\n" +
		// 		"                    }\n" +
		// 		"                  }\n" +
		// 		"                ]\n" +
		// 		"              }\n" +
		// 		"            ],\n" +
		// 		"            \"isSelfTransfer\": false,\n" +
		// 		"            \"tags\": [\n" +
		// 		"              \"shortest\"\n" +
		// 		"            ],\n" +
		// 		"            \"isMashUp\": false,\n" +
		// 		"            \"hasFlexibleOptions\": false,\n" +
		// 		"            \"score\": 7.59495,\n" +
		// 		"            \"deeplink\": \"https://www.skyscanner.net/transport/flights/muc/ber/221011/config/14385-2210111500--32090-0-9828-2210111605?adults=1&adultsv2=1&cabinclass=economy&children=0&childrenv2=&destinationentityid=27547053&originentityid=27545034&inboundaltsenabled=false&infants=0&outboundaltsenabled=false&preferdirects=false&ref=home&rtn=0\"\n" +
		// 		"          },\n" +
		// 		"          {\n" +
		// 		"            \"id\": \"14385-2210111900--32090-0-9828-2210112005\",\n" +
		// 		"            \"price\": {\n" +
		// 		"              \"raw\": 94,\n" +
		// 		"              \"formatted\": \"94 €\"\n" +
		// 		"            },\n" +
		// 		"            \"legs\": [\n" +
		// 		"              {\n" +
		// 		"                \"id\": \"14385-2210111900--32090-0-9828-2210112005\",\n" +
		// 		"                \"origin\": {\n" +
		// 		"                  \"id\": \"MUC\",\n" +
		// 		"                  \"name\": \"Munich\",\n" +
		// 		"                  \"displayCode\": \"MUC\",\n" +
		// 		"                  \"city\": \"Munich\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"destination\": {\n" +
		// 		"                  \"id\": \"BER\",\n" +
		// 		"                  \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                  \"displayCode\": \"BER\",\n" +
		// 		"                  \"city\": \"Berlin\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"durationInMinutes\": 65,\n" +
		// 		"                \"stopCount\": 0,\n" +
		// 		"                \"isSmallestStops\": true,\n" +
		// 		"                \"departure\": \"2022-10-11T19:00:00\",\n" +
		// 		"                \"arrival\": \"2022-10-11T20:05:00\",\n" +
		// 		"                \"timeDeltaInDays\": 0,\n" +
		// 		"                \"carriers\": {\n" +
		// 		"                  \"marketing\": [\n" +
		// 		"                    {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"logoUrl\": \"https://logos.skyscnr.com/images/airlines/favicon/LH.png\",\n" +
		// 		"                      \"name\": \"Lufthansa\"\n" +
		// 		"                    }\n" +
		// 		"                  ],\n" +
		// 		"                  \"operationType\": \"fully_operated\"\n" +
		// 		"                },\n" +
		// 		"                \"segments\": [\n" +
		// 		"                  {\n" +
		// 		"                    \"id\": \"14385-9828-2210111900-2210112005--32090\",\n" +
		// 		"                    \"origin\": {\n" +
		// 		"                      \"flightPlaceId\": \"MUC\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"MUNI\",\n" +
		// 		"                        \"name\": \"Munich\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Munich\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"destination\": {\n" +
		// 		"                      \"flightPlaceId\": \"BER\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"BERL\",\n" +
		// 		"                        \"name\": \"Berlin\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"departure\": \"2022-10-11T19:00:00\",\n" +
		// 		"                    \"arrival\": \"2022-10-11T20:05:00\",\n" +
		// 		"                    \"durationInMinutes\": 65,\n" +
		// 		"                    \"flightNumber\": \"1942\",\n" +
		// 		"                    \"marketingCarrier\": {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"name\": \"Lufthansa\",\n" +
		// 		"                      \"alternateId\": \"LH\",\n" +
		// 		"                      \"allianceId\": -31999\n" +
		// 		"                    },\n" +
		// 		"                    \"operatingCarrier\": {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"name\": \"Lufthansa\",\n" +
		// 		"                      \"alternateId\": \"LH\",\n" +
		// 		"                      \"allianceId\": -31999\n" +
		// 		"                    }\n" +
		// 		"                  }\n" +
		// 		"                ]\n" +
		// 		"              }\n" +
		// 		"            ],\n" +
		// 		"            \"isSelfTransfer\": false,\n" +
		// 		"            \"tags\": [\n" +
		// 		"              \"shortest\"\n" +
		// 		"            ],\n" +
		// 		"            \"isMashUp\": false,\n" +
		// 		"            \"hasFlexibleOptions\": false,\n" +
		// 		"            \"score\": 7.58894,\n" +
		// 		"            \"deeplink\": \"https://www.skyscanner.net/transport/flights/muc/ber/221011/config/14385-2210111900--32090-0-9828-2210112005?adults=1&adultsv2=1&cabinclass=economy&children=0&childrenv2=&destinationentityid=27547053&originentityid=27545034&inboundaltsenabled=false&infants=0&outboundaltsenabled=false&preferdirects=false&ref=home&rtn=0\"\n" +
		// 		"          },\n" +
		// 		"          {\n" +
		// 		"            \"id\": \"14385-2210111100--32090-0-9828-2210111205\",\n" +
		// 		"            \"price\": {\n" +
		// 		"              \"raw\": 94,\n" +
		// 		"              \"formatted\": \"94 €\"\n" +
		// 		"            },\n" +
		// 		"            \"legs\": [\n" +
		// 		"              {\n" +
		// 		"                \"id\": \"14385-2210111100--32090-0-9828-2210111205\",\n" +
		// 		"                \"origin\": {\n" +
		// 		"                  \"id\": \"MUC\",\n" +
		// 		"                  \"name\": \"Munich\",\n" +
		// 		"                  \"displayCode\": \"MUC\",\n" +
		// 		"                  \"city\": \"Munich\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"destination\": {\n" +
		// 		"                  \"id\": \"BER\",\n" +
		// 		"                  \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                  \"displayCode\": \"BER\",\n" +
		// 		"                  \"city\": \"Berlin\",\n" +
		// 		"                  \"isHighlighted\": false\n" +
		// 		"                },\n" +
		// 		"                \"durationInMinutes\": 65,\n" +
		// 		"                \"stopCount\": 0,\n" +
		// 		"                \"isSmallestStops\": true,\n" +
		// 		"                \"departure\": \"2022-10-11T11:00:00\",\n" +
		// 		"                \"arrival\": \"2022-10-11T12:05:00\",\n" +
		// 		"                \"timeDeltaInDays\": 0,\n" +
		// 		"                \"carriers\": {\n" +
		// 		"                  \"marketing\": [\n" +
		// 		"                    {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"logoUrl\": \"https://logos.skyscnr.com/images/airlines/favicon/LH.png\",\n" +
		// 		"                      \"name\": \"Lufthansa\"\n" +
		// 		"                    }\n" +
		// 		"                  ],\n" +
		// 		"                  \"operationType\": \"fully_operated\"\n" +
		// 		"                },\n" +
		// 		"                \"segments\": [\n" +
		// 		"                  {\n" +
		// 		"                    \"id\": \"14385-9828-2210111100-2210111205--32090\",\n" +
		// 		"                    \"origin\": {\n" +
		// 		"                      \"flightPlaceId\": \"MUC\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"MUNI\",\n" +
		// 		"                        \"name\": \"Munich\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Munich\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"destination\": {\n" +
		// 		"                      \"flightPlaceId\": \"BER\",\n" +
		// 		"                      \"parent\": {\n" +
		// 		"                        \"flightPlaceId\": \"BERL\",\n" +
		// 		"                        \"name\": \"Berlin\",\n" +
		// 		"                        \"type\": \"City\"\n" +
		// 		"                      },\n" +
		// 		"                      \"name\": \"Berlin Brandenburg\",\n" +
		// 		"                      \"type\": \"Airport\"\n" +
		// 		"                    },\n" +
		// 		"                    \"departure\": \"2022-10-11T11:00:00\",\n" +
		// 		"                    \"arrival\": \"2022-10-11T12:05:00\",\n" +
		// 		"                    \"durationInMinutes\": 65,\n" +
		// 		"                    \"flightNumber\": \"1928\",\n" +
		// 		"                    \"marketingCarrier\": {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"name\": \"Lufthansa\",\n" +
		// 		"                      \"alternateId\": \"LH\",\n" +
		// 		"                      \"allianceId\": -31999\n" +
		// 		"                    },\n" +
		// 		"                    \"operatingCarrier\": {\n" +
		// 		"                      \"id\": -32090,\n" +
		// 		"                      \"name\": \"Lufthansa\",\n" +
		// 		"                      \"alternateId\": \"LH\",\n" +
		// 		"                      \"allianceId\": -31999\n" +
		// 		"                    }\n" +
		// 		"                  }\n" +
		// 		"                ]\n" +
		// 		"              }\n" +
		// 		"            ],\n" +
		// 		"            \"isSelfTransfer\": false,\n" +
		// 		"            \"tags\": [\n" +
		// 		"              \"shortest\"\n" +
		// 		"            ],\n" +
		// 		"            \"isMashUp\": false,\n" +
		// 		"            \"hasFlexibleOptions\": false,\n" +
		// 		"            \"score\": 7.58729,\n" +
		// 		"            \"deeplink\": \"https://www.skyscanner.net/transport/flights/muc/ber/221011/config/14385-2210111100--32090-0-9828-2210111205?adults=1&adultsv2=1&cabinclass=economy&children=0&childrenv2=&destinationentityid=27547053&originentityid=27545034&inboundaltsenabled=false&infants=0&outboundaltsenabled=false&preferdirects=false&ref=home&rtn=0\"\n" +
		// 		"          }\n" +
		// 		"        ]\n" +
		// 		"      }\n" +
		// 		"    ]\n" +
		// 		"  },\n" +
		// 		"  \"context\": {\n" +
		// 		"    \"status\": \"complete\",\n" +
		// 		"    \"sessionId\": \"792381a9-fefd-4ad5-a3ef-0fa74fdb6d7f\",\n" +
		// 		"    \"totalResults\": 97\n" +
		// 		"  }\n" +
		// 		"}";

		System.out.println("data is" + depature + " " + arrival + " " + date + " " + no);

		JSONObject json = new JSONObject(str);
		JSONObject jsonItineraries = json.getJSONObject("itineraries");
		JSONArray typesArray = jsonItineraries.getJSONArray("buckets");

		JSONObject bestTypeObj = typesArray.getJSONObject(0);
		JSONArray jsonItems = bestTypeObj.getJSONArray("items");

		JSONObject zeroThObj = jsonItems.getJSONObject(0);
		JSONObject priceObj = zeroThObj.getJSONObject("price");

		JSONArray legsObj = zeroThObj.getJSONArray("legs");

		JSONObject valiObj = legsObj.getJSONObject((0));

		JSONArray segObj = valiObj.getJSONArray("segments");
		JSONObject segObjZero = segObj.getJSONObject(0);

		JSONObject market = segObjZero.getJSONObject("marketingCarrier");


		Map<String, String> detail = new HashMap<>();
		detail.put("price",priceObj.get("raw").toString());
		detail.put("departure", valiObj.get("departure").toString());
		detail.put("arrival", valiObj.get("arrival").toString());
		detail.put("flightNo", segObjZero.get("flightNumber").toString());
		detail.put("flightName", market.get("name").toString());

		List<Map<String, String>> flightDetailsSet = new ArrayList<>();



			modelAndView.addObject("flightDetails", detail);
			modelAndView.addObject("userip", userip);
			modelAndView.setViewName("bestFlight");
			return modelAndView;

		}
	}





