
package acme.features.manager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.BookingRecord;
import acme.entities.booking.BookingRepository;
import acme.entities.claim.Claim;
import acme.entities.flight.Flight;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.leg.Leg;
import acme.entities.trackingLog.TrackingLog;
import acme.entities.trackingLog.TrackingLogRepository;
import acme.features.assistanceAgent.claim.ClaimRepository;
import acme.features.customer.bookingRecord.CustomerBookingRecordRepository;
import acme.features.flightcrewmember.flightassignment.FlightAssignmentRepository;
import acme.features.manager.leg.LegRepository;
import acme.realms.manager.Manager;

@GuiService
public class FlightDeleteService extends AbstractGuiService<Manager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightRepository				repository;

	@Autowired
	private LegRepository					legRepository;

	@Autowired
	private ClaimRepository					claimRepository;

	@Autowired
	private FlightAssignmentRepository		flightAssignmentRepository;

	@Autowired
	private TrackingLogRepository			trackingLogRepository;

	@Autowired
	private BookingRepository				bookingRepository;

	@Autowired
	private CustomerBookingRecordRepository	bookingRecordRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("id", int.class);
		flight = (Flight) this.repository.findById(flightId).get();

		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		super.bindObject(flight, "tag", "selfTransfer", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		boolean isDraftMode = flight.getDraftMode();

		boolean status = isDraftMode == true;

		super.state(status, "*", "acme.validation.flight.draftMode.deleted.message");
	}

	@Override
	public void perform(final Flight flight) {
		Collection<Booking> bookings = this.bookingRepository.findBookingByFlightId(flight.getId());
		for (Booking booking : bookings) {
			Collection<BookingRecord> bookingRecords = this.bookingRecordRepository.findBookingRecordsByBookingId(booking.getId());
			this.bookingRecordRepository.deleteAll(bookingRecords);
		}

		this.bookingRepository.deleteAll(bookings);

		Collection<Leg> legs = this.legRepository.findLegsByFlightId(flight.getId());
		for (Leg leg : legs) {
			Collection<Claim> claims = this.claimRepository.findClaimsByLegId(leg.getId());
			for (Claim claim : claims) {
				Collection<TrackingLog> trackingLogs = this.trackingLogRepository.findTrackingLogsByClaimId(claim.getId());
				this.trackingLogRepository.deleteAll(trackingLogs);
			}
			Collection<FlightAssignment> legFlightAssignments = this.flightAssignmentRepository.findFlightAssignmentByLegId(leg.getId());
			this.claimRepository.deleteAll(claims);
			this.flightAssignmentRepository.deleteAll(legFlightAssignments);
		}

		this.legRepository.deleteAll(legs);

		this.repository.delete(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset;

		dataset = super.unbindObject(flight, "tag", "selfTransfer", "cost", "description", "draftMode");

		super.getResponse().addData(dataset);
	}

}
