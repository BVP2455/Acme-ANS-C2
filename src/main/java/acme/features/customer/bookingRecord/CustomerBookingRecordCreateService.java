
package acme.features.customer.bookingRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Passenger;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingRecord;
import acme.realms.customer.Customer;

@GuiService
public class CustomerBookingRecordCreateService extends AbstractGuiService<Customer, BookingRecord> {

	@Autowired
	private CustomerBookingRecordRepository repository;


	@Override
	public void authorise() {
		boolean status = true;
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		int bookingId = super.getRequest().getData("bookingId", int.class);
		Booking booking = this.repository.findBookingById(bookingId);

		status = customerId == booking.getCustomer().getId();

		if (status && super.getRequest().getMethod().equals("POST")) {

			Integer passengerId = super.getRequest().getData("passenger", Integer.class);
			Passenger passenger = this.repository.findPassengerById(passengerId);

			Collection<Passenger> avaiablePassengers = this.repository.findAllPassengersOfCustomer(customerId);

			if (passengerId != 0 && !avaiablePassengers.contains(passenger))
				status = false;

			Collection<Passenger> includedPassengers = this.repository.findPassengersOfBooking(bookingId);

			if (includedPassengers.contains(passenger))
				status = false;
		}

		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		int bookingId = super.getRequest().getData("bookingId", int.class);
		Booking booking = this.repository.findBookingById(bookingId);
		BookingRecord bookingRecord = new BookingRecord();
		bookingRecord.setBooking(booking);
		super.getBuffer().addData(bookingRecord);
	}

	@Override
	public void bind(final BookingRecord bookingRecord) {
		super.bindObject(bookingRecord, "passenger");
	}

	@Override
	public void validate(final BookingRecord bookingRecord) {
		;
	}

	@Override
	public void perform(final BookingRecord bookingRecord) {
		this.repository.save(bookingRecord);
	}

	@Override
	public void unbind(final BookingRecord bookingRecord) {
		assert bookingRecord != null;
		Dataset dataset;

		dataset = super.unbindObject(bookingRecord, "passenger", "booking");
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		int bookingId = super.getRequest().getData("bookingId", int.class);
		Collection<Passenger> addedPassengers = this.repository.findPassengersOfBooking(bookingId);

		Collection<Passenger> passengers = this.repository.findAllPassengersOfCustomer(customerId).stream().filter(p -> !addedPassengers.contains(p)).toList();
		SelectChoices passengerChoices = SelectChoices.from(passengers, "name", bookingRecord.getPassenger());
		dataset.put("passengers", passengerChoices);
		dataset.put("locatorCode", bookingRecord.getBooking().getLocatorCode());

		super.getResponse().addData(dataset);

	}

}
