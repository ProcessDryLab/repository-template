package pdl.repository.miners;

import pdl.repository.web.models.Miner;
import pdl.repository.web.models.MinerConfiguration;

public interface IMiner {

	public Miner getMinerWebModel();

	public void mine(MinerConfiguration configuration);

}
