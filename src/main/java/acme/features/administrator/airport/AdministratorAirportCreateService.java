
package acme.features.administrator.airport;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.controllers.GuiController;
import acme.client.services.AbstractGuiService;
import acme.entities.airport.Airport;
import acme.entities.airport.OperationalType;

@GuiController
public class AdministratorAirportCreateService extends AbstractGuiService<Administrator, Airport> {

	@Autowired
	private AdministratorAirportRepository repository;


	@Override
	public void authorise() {
		boolean status = false;
		if (super.getRequest().getPrincipal().hasRealmOfType(Administrator.class)) {
			status = true;
			if (super.getRequest().getMethod().equals("POST")) {
				String operationalScopeInput = super.getRequest().getData("operationalScope", String.class);
				boolean operationalScopeValid = false;

				if (operationalScopeInput != null) {
					String trimmedInput = operationalScopeInput.trim();
					if (trimmedInput.equals("0"))
						operationalScopeValid = true;
					else
						for (OperationalType ot : OperationalType.values())
							if (ot.name().equalsIgnoreCase(trimmedInput)) {
								operationalScopeValid = true;
								break;
							}
				}

				status = status && operationalScopeValid;
			}
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Airport airport;

		airport = new Airport();

		super.getBuffer().addData(airport);
	}

	@Override
	public void bind(final Airport airport) {
		super.bindObject(airport, "name", "iataCode", "operationalScope", "country", "city", "website", "email", "phoneNumber");
	}

	@Override
	public void validate(final Airport airport) {
		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Airport airport) {
		this.repository.save(airport);
	}

	@Override
	public void unbind(final Airport airport) {
		Dataset dataset;
		SelectChoices choices;

		choices = SelectChoices.from(OperationalType.class, airport.getOperationalScope());
		dataset = super.unbindObject(airport, "name", "iataCode", "operationalScope", "country", "city", "website", "email", "phoneNumber");
		dataset.put("operationalScopes", choices);
		super.getResponse().addData(dataset);
	}
}
