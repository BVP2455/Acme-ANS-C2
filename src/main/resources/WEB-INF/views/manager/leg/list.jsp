<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="manager.leg.list.label.flightNumber" path="flightNumber" width="14%"/>
	<acme:list-column code="manager.leg.list.label.scheduledDeparture" path="scheduledDeparture" width="14%"/>
	<acme:list-column code="manager.leg.list.label.scheduledArrival" path="scheduledArrival" width="14%"/>
	<acme:list-column code="manager.leg.list.label.status" path="status" width="14%"/>
	<acme:list-column code="manager.leg.list.label.departureAirport" path="departureAirport" width="14%"/>
	<acme:list-column code="manager.leg.list.label.arrivalAirport" path="arrivalAirport" width="14%"/>
	<acme:list-column code="manager.leg.list.label.aircraft" path="aircraft" width="14%"/>
</acme:list>

<acme:button code="manager.leg.list.button.create" action="/manager/leg/create"/>