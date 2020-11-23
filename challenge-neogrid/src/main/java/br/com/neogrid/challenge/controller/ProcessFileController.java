package br.com.neogrid.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.neogrid.challenge.domain.exception.ProcessException;
import br.com.neogrid.challenge.domain.model.Response;
import br.com.neogrid.challenge.domain.service.ProcessFileService;

@Controller
public class ProcessFileController {
	
	@Autowired
	private ProcessFileService service;
	
	@GetMapping("/")
	public String index(Model model) {
		try {
			Response response = service.start();
		
			model.addAttribute("input", response.getInput());
			model.addAttribute("output", response.getOutput());
			model.addAttribute("titles", response.getTitles());
			model.addAttribute("assemblyLines", response.getAssemblyLines());
		} catch (ProcessException ex) {
			Response response = ex.getResponse();
			
			model.addAttribute(
					"messageError", 
					String.format("%s %s", ex.getMessage(), "Open the output file for more details.")
				);
			
			if(response != null) {
				model.addAttribute("titles", response.getTitles());
				model.addAttribute("input", response.getInput());
				
				if(response.getOutput() != null) {
					model.addAttribute("output", response.getOutput());
				}
			}
		}
		return "index";
	}
}
