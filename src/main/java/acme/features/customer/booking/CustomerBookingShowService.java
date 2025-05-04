
package acme.features.customer.booking;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.entities.flight.Flight;
import acme.realms.customer.Customer;

@GuiService
public class CustomerBookingShowService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		super.getResponse().setAuthorised(status);

		int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int bookingId = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findBookingById(bookingId);

		super.getResponse().setAuthorised(customerId == booking.getCustomer().getId());
	}

	@Override
	public void load() {
		Booking booking;
		int id = super.getRequest().getData("id", int.class);

		booking = this.repository.findBookingById(id);
		super.getBuffer().addData(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		assert booking != null;
		Dataset dataset;
		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		Collection<Flight> flights = this.repository.findAllFlights();
		Collection<Flight> avaiableFlights = this.repository.findAllFlights().stream().filter(f -> f.getNumberLegs() != 0 && !f.getDraftMode() && f.getScheduledDeparture().after(MomentHelper.getCurrentMoment()) && !f.getDraftMode())
			.collect(Collectors.toList());
		dataset = super.unbindObject(booking, "flight", "locatorCode", "travelClass", "price", "lastCardNibble", "draftMode", "id");

		if (!booking.getDraftMode()) {
			SelectChoices flightChoices = SelectChoices.from(flights, "label", booking.getFlight());
			dataset.put("flight", flightChoices.getSelected() != null ? flightChoices.getSelected().getKey() : "0");
			dataset.put("flights", flightChoices);
		} else {
			boolean flightStillValid = avaiableFlights.contains(booking.getFlight());

			if (!flightStillValid) {
				SelectChoices choices = SelectChoices.from(avaiableFlights, "label", booking.getFlight());
				dataset.put("flight", choices.getSelected() != null ? choices.getSelected().getKey() : "0");
				dataset.put("flights", choices);
			} else {
				SelectChoices choices = SelectChoices.from(avaiableFlights, "label", booking.getFlight());
				dataset.put("flight", booking.getFlight() != null && choices.getSelected() != null ? choices.getSelected().getKey() : "0");
				dataset.put("flights", choices);
			}
		}
		dataset.put("travelClasses", travelClasses);
		dataset.put("bookingId", booking.getId());

		super.getResponse().addData(dataset);
	}

}
