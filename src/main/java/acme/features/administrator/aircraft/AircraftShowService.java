
package acme.features.administrator.aircraft;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.Status;
import acme.entities.airline.Airline;

@GuiService
public class AircraftShowService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state -------------------------------------------------------------------

	@Autowired
	private AircraftRepository repository;

	// AbstractGuiService interface -----------------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {

		Aircraft aircraft;

		int aircraftId;

		aircraftId = super.getRequest().getData("id", int.class);

		aircraft = this.repository.findAircraftById(aircraftId);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {

		SelectChoices choiceStatus;
		SelectChoices choicesAirline;
		Collection<Airline> airlines;

		Dataset dataset;

		airlines = this.repository.findAllAirlines();
		choicesAirline = SelectChoices.from(airlines, "iataCode", aircraft.getAirline());

		choiceStatus = SelectChoices.from(Status.class, aircraft.getStatus());

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");
		dataset.put("airlineChoice", choicesAirline);
		dataset.put("statusChoice", choiceStatus);
		dataset.put("confirmation", false);

		super.getResponse().addData(dataset);

	}
}
