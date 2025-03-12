
package acme.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.claim.Claim;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TrackingLog extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	// Atributos

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				lastUpdateMoment;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				step;

	@Mandatory
	@ValidNumber(min = 0, max = 100, integer = 3, fraction = 2)
	@Automapped
	private Double				resolutionPercentage;

	@Mandatory
	@Automapped
	private Boolean				accepted;

	@Mandatory
	@ValidString(max = 255)
	@Automapped
	private String				resolution;

	// Relaciones

	@Mandatory
	@ManyToOne
	private Claim				claim;

}
