package pdl.repository.template.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system/")
@CrossOrigin
public class SystemController {

	@Autowired
	BuildProperties buildProperties;

	@GetMapping("/ping")
	public @ResponseBody String ping() {
		return "pong";
	}

	@GetMapping("/version")
	public @ResponseBody String version() {
		return buildProperties.getTime().toString();
	}
}
