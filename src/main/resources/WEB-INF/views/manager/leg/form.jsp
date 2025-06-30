<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox path="flightNumber" code="manager.leg.form.label.flightNumber" readonly="false"/>
	<acme:input-moment path="scheduledDeparture" code="manager.leg.form.label.scheduledDeparture"/>
	<acme:input-moment path="scheduledArrival" code="manager.leg.form.label.scheduledArrival"/>
	<acme:input-select path="status" code="manager.leg.form.label.status" choices="${statuses}"/>
	<acme:input-select path="airportDeparture" code="manager.leg.form.label.originAirport" choices="${airportDepartures}"/>
	<acme:input-select path="airportArrival" code="manager.leg.form.label.destinationAirport" choices="${airportArrivals}"/>
	<acme:input-select path="aircraft" code="manager.leg.form.label.aircraft" choices="${aircrafts}"/>
	
	<jstl:choose>
	<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish')}">
		<jstl:if test="${legDraftMode}">
			<acme:submit code="manager.leg.form.button.publish" action="/manager/leg/publish"/>
			<acme:submit code="manager.leg.form.button.delete" action="/manager/leg/delete"/>
			<acme:submit code="manager.leg.form.button.update" action="/manager/leg/update"/>
		</jstl:if>
	</jstl:when>
	<jstl:when test="${_command == 'create'}">
		<acme:submit code="manager.leg.form.button.create" action="/manager/leg/create?flightId=${flightId}"/>
	</jstl:when>
</jstl:choose>


</acme:form>