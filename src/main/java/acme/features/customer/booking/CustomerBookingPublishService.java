
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.realms.customer.Customer;

@GuiService
public class CustomerBookingPublishService extends AbstractGuiService<Customer, Booking> {

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

		int id = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findBookingById(id);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		super.bindObject(booking, "flight", "locatorCode", "travelClass", "price", "lastCardNibble");
	}

	@Override
	public void validate(final Booking booking) {
		if (booking.getLastCardNibble() == null || booking.getLastCardNibble().toString().isBlank())
			super.state(false, "lastCardNibble", "acme.validation.lastCardNibble.message");

		boolean flightPublished = booking.getFlight().getDraftMode() == false;
		super.state(flightPublished, "flight", "customer.booking.form.error.flightNotPublished");

		boolean emptyPassengers = !this.repository.findPassengersByBookingId(booking.getId()).isEmpty();
		super.state(emptyPassengers, "price", "customer.booking.form.error.emptyPassengers");

		boolean laterFlight = MomentHelper.isAfter(booking.getFlight().getScheduledDeparture(), MomentHelper.getCurrentMoment());
		super.state(laterFlight, "flight", "customer.booking.form.error.flightBeforeBooking");
	}

	@Override
	public void perform(final Booking booking) {
		booking.setPurchaseMoment(MomentHelper.getCurrentMoment());
		booking.setDraftMode(false);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		SelectChoices travelClasses;

		travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		Collection<acme.entities.flight.Flight> flights = this.repository.findAllFlights();
		SelectChoices flightChoices = SelectChoices.from(flights, "id", booking.getFlight());

		dataset = super.unbindObject(booking, "flight", "locatorCode", "travelClass", "price", "lastCardNibble", "draftMode", "id");
		dataset.put("travelClasses", travelClasses);
		dataset.put("flights", flightChoices);
		super.getResponse().addData(dataset);
	}

}
