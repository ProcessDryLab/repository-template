package pdl.repository.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pdl.repository.miners.IMiner;
import pdl.repository.utils.ReflectionsUtils;
import pdl.repository.web.models.Miner;
import pdl.repository.web.models.MinerConfiguration;
import pdl.repository.web.models.Resource;

@RestController
@RequestMapping("/api/v1/")
@CrossOrigin
public class MinerController {

	private List<Miner> minerWebModels = null;

	@GetMapping("/miners")
	public ResponseEntity<List<Miner>> miners() {
		if (minerWebModels == null) {
			minerWebModels = new ArrayList<>();

			for (Class<? extends IMiner> minerClass : ReflectionsUtils.get().getSubTypesOf(IMiner.class)) {
				try {
					minerWebModels.add(minerClass.getDeclaredConstructor().newInstance().getMinerWebModel());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return ResponseEntity.ok(minerWebModels);
	}

	@PostMapping("/miners/instance")
	public ResponseEntity<Resource> newInstance(@RequestBody MinerConfiguration configuration) {
		IMiner miner = getMiner(configuration.getMiner());
		if (miner == null) {
			return ResponseEntity.status(404).build();
		}
		miner.mine(configuration);
		return ResponseEntity.ok(new Resource());
	}

	private static IMiner getMiner(Miner miner) {
		for (Class<? extends IMiner> minerClass : ReflectionsUtils.get().getSubTypesOf(IMiner.class)) {
			try {
				IMiner i = minerClass.getDeclaredConstructor().newInstance();
				System.out.println(i.getMinerWebModel().getId() + " -- " + miner.getId());
				if (i.getMinerWebModel().getId().equals(miner.getId())) {
					return i;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return null;
	}
}
