package pdl.repository.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class MyErrorController implements ErrorController {

	@RequestMapping("/error")
	public String handleError() {
		return "";
	}
}
