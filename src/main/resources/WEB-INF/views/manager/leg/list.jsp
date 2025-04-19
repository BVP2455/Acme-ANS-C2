<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="manager.leg.list.label.flightNumber" path="flightNumber" width="10%" sortable="false"/>
	<acme:list-column code="manager.leg.list.label.scheduledDeparture" path="scheduledDeparture" width="20%"/>
	<acme:list-column code="manager.leg.list.label.scheduledArrival" path="scheduledArrival" width="20%"/>
	<acme:list-column code="manager.leg.list.label.status" path="status" width="10%" sortable="false"/>
	<acme:list-column code="manager.leg.list.label.airportDeparture" path="airportDeparture" width="10%" sortable="false"/>
	<acme:list-column code="manager.leg.list.label.airportArrival" path="airportArrival" width="10%" sortable="false"/>
	<acme:list-column code="manager.leg.list.label.aircraft" path="aircraft" width="20%" sortable="false"/>
</acme:list>

<jstl:if test="${flightDraftMode == true}">
	<acme:button code="manager.leg.list.button.create" action="/manager/leg/create"/>
</jstl:if>