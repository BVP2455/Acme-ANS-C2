<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox path="flightNumber" code="manager.leg.form.label.flightNumber"/>
	<acme:input-moment path="scheduledDeparture" code="manager.leg.form.label.scheduledDeparture"/>
	<acme:input-moment path="scheduledArrival" code="manager.leg.form.label.scheduledArrival"/>
	<acme:input-select path="status" code="manager.flight.leg.label.status" choices="${statuses}"/>
	<acme:input-select path="airportDeparture" code="manager.leg.form.label.originAirport" choices="${airportDepartures}"/>
	<acme:input-select path="airportArrival" code="manager.leg.form.label.destinationAirport" choices="${airportArrivals}"/>
	<acme:input-select path="aircraft" code="manager.leg.form.label.aircraft" choices="${aircrafts}"/>
	<acme:input-checkbox path="confirmation" code="manager.leg.form.label.confirmation"/>
</acme:form>