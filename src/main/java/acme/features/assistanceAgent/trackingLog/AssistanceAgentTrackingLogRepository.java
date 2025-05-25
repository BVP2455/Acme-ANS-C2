
package acme.features.assistanceAgent.trackingLog;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.entities.trackingLog.TrackingLog;

@Repository
public interface AssistanceAgentTrackingLogRepository extends AbstractRepository {

	@Query("Select c from Claim c where c.id=:claimId")
	Claim findClaimById(int claimId);

	@Query("select t.claim from TrackingLog t where t.id = :trackingLogId")
	Claim findClaimByTrackingLogId(int trackingLogId);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId")
	Collection<TrackingLog> findTrackingLogsByClaimId(int claimId);

	@Query("SELECT t FROM TrackingLog t WHERE t.id = :trackingLogId")
	TrackingLog findTrackingLogById(int trackingLogId);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.assistanceAgent.id = :assistanceAgentId")
	Collection<TrackingLog> findAllTrackingLogs(int assistanceAgentId);

	@Query("Select c from Claim c where c.assistanceAgent.id=:agentId")
	List<Claim> findClaimsByAssistanceAgent(int agentId);

	@Query("select t from TrackingLog t where t.claim.id = :claimId order by t.lastUpdateMoment desc")
	List<TrackingLog> findLastTrackingLogByClaimId(Integer claimId);

	@Query("select t from TrackingLog t where t.claim.id = :claimId and t.draftMode = false order by t.resolutionPercentage desc")
	List<TrackingLog> findLastPercentageTrackingLogPublished(Integer claimId);

}
