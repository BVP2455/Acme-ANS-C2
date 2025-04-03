
package acme.features.assistanceAgent.claim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.claim.Claim;
import acme.realms.assistanceAgents.AssistanceAgent;

@GuiController
public class AssistanceAgentClaimController extends AbstractGuiController<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimResolvedListService	listService;

	@Autowired
	private AssistanceAgentClaimPendingListService	listPendingService;

	@Autowired
	private AssistanceAgentClaimShowService			showService;

	@Autowired
	private AssistanceAgentClaimCreateService		createService;

	@Autowired
	private AssistanceAgentClaimPublishService		publishService;

	@Autowired
	private AssistanceAgentClaimUpdateService		updateService;

	@Autowired
	private AssistanceAgentClaimDeleteService		deleteService;


	// Constructors -----------------------------------------------------------
	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);
		super.addCustomCommand("pending", "list", this.listPendingService);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}
