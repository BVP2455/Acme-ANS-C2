
package acme.entities.flights;

import java.beans.Transient;
import java.util.Date;

import javax.persistence.Entity;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.entities.legs.Leg;
import acme.entities.legs.LegRepository;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Flight extends AbstractEntity {

	// Serialisation version ----------------------------

	private static final long	serialVersionUID	= 1L;

	// Mandatory atributes ------------------------------
	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				tag;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				selfTransfer;

	@Mandatory
	@ValidMoney(min = 0.0, max = 1000000.00)
	@Automapped
	private Money				cost;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				draftMode;

	// Optional atributes -------------------------------
	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				description;

	// Derivated atributes ------------------------------


	public Date getScheduledDeparture() {
		Date result;
		Leg wrapper;
		LegRepository repository;
		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLegsByFlightOrderedByDeparture(this.getId()).getFirst();
		result = wrapper.getScheduledDeparture();

		return result;
	}

	public Date getScheduledArrival() {
		Date result;
		Leg wrapper;
		LegRepository repository;

		repository = SpringHelper.getBean(LegRepository.class);
		wrapper = repository.findLegsByFlightOrderedByDeparture(this.getId()).getLast();
		result = wrapper.getScheduledArrival();

		return result;
	}

	public String getOriginCity() {
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		List<Leg> legs = repository.findLegsByFlightOrderedByDeparture(this.getId());

		if (legs.isEmpty())
			return "Desconocido";

		Leg lastLeg = legs.getLast();

		if (lastLeg == null)
			return "Desconocido";

		return lastLeg.getArrivalAirport().getCity();
	}

	public String getDestinationCity() {
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		List<Leg> legs = repository.findLegsByFlightOrderedByDeparture(this.getId());

		if (legs.isEmpty())
			return "Desconocido";

		Leg firstLeg = legs.getFirst();

		if (firstLeg == null)
			return "Desconocido";

		return firstLeg.getArrivalAirport().getCity();
	}

	public Integer getNumberLayovers() {
		Integer result;
		LegRepository repository;

		repository = SpringHelper.getBean(LegRepository.class);
		result = repository.countNumberOfLegsOfFlight(this.getId()) - 1;

		return result;

	}

	@Transient()
	public String getLabel() {
		String origin = this.getOriginCity();
		String destination = this.getDestinationCity();
		return origin + "-" + destination;
	}

}
