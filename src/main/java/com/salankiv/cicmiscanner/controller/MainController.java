package com.salankiv.cicmiscanner.controller;

import com.salankiv.cicmiscanner.model.*;
import com.salankiv.cicmiscanner.repository.IataAirlineRepo;
import com.salankiv.cicmiscanner.repository.IataAirportRepo;
import com.salankiv.cicmiscanner.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class MainController {

	private Logger logger = LoggerFactory.getLogger(MainController.class);

	@Autowired
	IataAirportRepo iataAirportRepo;

	@Autowired
	IataAirlineRepo iataAirlineRepo;

	@Autowired
	AirportFinder airportFinder;

	@Autowired
	AirlineFinder airlineFinder;

	@Autowired
	ApiHandler apiHandler;

	@Autowired
	TaskScheduler taskScheduler;

	@Autowired
	EmailSender emailSender;

	//executes low-fare search

	@GetMapping(value = {"", "/"})
	public String loadMain(Model model) {
		model.addAttribute("searchRequest", new SearchRequest());
		model.addAttribute("airports", iataAirportRepo.findAll());
		return "main";
	}

	@PostMapping(value = "/search")
	public String search(@ModelAttribute SearchRequest searchRequest, Model model) {
		RestTemplate request = new RestTemplate();
		Root newRoot = request.getForObject(searchRequest.getLowFareSearchUrl(), Root.class);
		model.addAttribute("results", newRoot.getResults());
		model.addAttribute("searchRequest", searchRequest);
		model.addAttribute("airports", iataAirportRepo.findAll());
		model.addAttribute("airportFinder", airportFinder);
		model.addAttribute("airlineFinder", airlineFinder);
		return "main";
	}

	//executes inspiration search

	@GetMapping(value = "/origin")
	public String loadOrigin(Model model) {
		model.addAttribute("searchRequest", new SearchRequest());
		model.addAttribute("newRoot", new Root());
		model.addAttribute("airports", iataAirportRepo.findByInspiration(1));
		return "origin";
	}

	@PostMapping(value = "/originsearch")
	public String searchOrigin(@ModelAttribute SearchRequest searchRequest, Model model) {
		RestTemplate request = new RestTemplate();
		Root newRoot = request.getForObject(searchRequest.getInspirationSearchUrl(), Root.class);
		model.addAttribute("newRoot", newRoot);
		model.addAttribute("searchRequest", searchRequest);
		model.addAttribute("airports", iataAirportRepo.findByInspiration(1));
		model.addAttribute("airportFinder", airportFinder);
		model.addAttribute("airlineFinder", airlineFinder);
		return "origin";
	}

	//executes api calls

	@GetMapping(value = "/api")
	public String loadApi(Model model) {
		model.addAttribute("taskScheduler", taskScheduler);
		model.addAttribute("airports", iataAirportRepo.findByInspiration(1));
		return "api";
	}

	@PostMapping(value = "/apiset")
	public String loadApi(@ModelAttribute TaskScheduler newTaskScheduler, Model model) {
		taskScheduler.setOrigin(newTaskScheduler.getOrigin());
		taskScheduler.setMax_price(newTaskScheduler.getMax_price());
		model.addAttribute("taskScheduler", taskScheduler);
		model.addAttribute("airports", iataAirportRepo.findByInspiration(1));
		return "api";
	}

	@PostMapping(value = "/apisearch")
	@ResponseBody
	public String searchApi() {
		/*try {
			emailSender.sendEmail(taskScheduler.automatedSearch());
		} catch (MailException e) {
			logger.info("Error sending email" + e.getMessage());
		}*/
		return taskScheduler.automatedSearch();
	}
}