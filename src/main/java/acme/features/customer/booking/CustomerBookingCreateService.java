
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
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Customer customer = (Customer) super.getRequest().getPrincipal().getActiveRealm();
		Booking booking;

		booking = new Booking();
		booking.setPurchaseMoment(MomentHelper.getCurrentMoment());
		booking.setDraftMode(true);
		booking.setCustomer(customer);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		super.bindObject(booking, "flight", "locatorCode", "travelClass", "lastCardNibble");
	}

	@Override
	public void validate(final Booking booking) {

		if (booking.getFlight() != null) {
			boolean laterFlight = MomentHelper.isAfter(booking.getFlight().getScheduledDeparture(), MomentHelper.getCurrentMoment());
			super.state(laterFlight, "flight", "customer.booking.form.error.flightBeforeBooking");

		}
	}

	@Override
	public void perform(final Booking booking) {
		booking.setDraftMode(true);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		assert booking != null;
		Dataset dataset;
		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		Collection<Flight> flights = this.repository.findAllFlights().stream().filter(f -> f.getNumberLegs() != 0).collect(Collectors.toList());
		Collection<Flight> flightsAvaiables = flights.stream().filter(f -> f.getScheduledDeparture().after(MomentHelper.getCurrentMoment())).collect(Collectors.toList());

		SelectChoices flightChoices = SelectChoices.from(flightsAvaiables, "label", booking.getFlight());

		dataset = super.unbindObject(booking, "flight", "locatorCode", "travelClass", "lastCardNibble", "draftMode", "id");
		dataset.put("travelClasses", travelClasses);
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);

	}
}
