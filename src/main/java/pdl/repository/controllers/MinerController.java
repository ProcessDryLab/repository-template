package pdl.repository.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pdl.repository.types.TypeBPMN;
import pdl.repository.types.TypeXES;
import pdl.repository.web.models.Miner;
import pdl.repository.web.models.Parameter;
import pdl.repository.web.models.Parameter.TYPES;

@RestController
@RequestMapping("/api/v1/")
@CrossOrigin
public class MinerController {

	@GetMapping("/miners")
	public ResponseEntity<List<Miner>> miners() {
		List<Miner> miners = new ArrayList<>();

		Miner m1 = new Miner();
		m1.setName("Miner 1");
		m1.setDescription("This is a dummy miner");
		m1.addInput("source", new TypeXES().getResourceType());
		m1.addParameter(new Parameter("Threshold 1", TYPES.DOUBLE, 0.8d));
		m1.addParameter(new Parameter("Threshold 2", TYPES.INTEGER, 10));
		m1.addOutput(new TypeBPMN().getResourceType());

		Miner m2 = new Miner();
		m2.setName("Another miner");
		m2.setDescription("This is a second dummy miner");
		m2.addOutput(new TypeBPMN().getResourceType());

		Miner m3 = new Miner();
		m3.setName("Miner 3");
		m3.setDescription("This is a dummy miner");
		m3.addInput("source", new TypeXES().getResourceType());
		m3.addInput("Another input model", new TypeBPMN().getResourceType());
		m3.addParameter(new Parameter("Threshold 1", TYPES.DOUBLE, 0.8d));
		m3.addParameter(new Parameter("Threshold 2", TYPES.INTEGER, 10));
		m3.addParameter(new Parameter("Threshold 3", TYPES.INTEGER, 10));
		m3.addOutput(new TypeBPMN().getResourceType());

		miners.add(m1);
		miners.add(m2);
		miners.add(m3);

		return ResponseEntity.ok(miners);
	}
}
