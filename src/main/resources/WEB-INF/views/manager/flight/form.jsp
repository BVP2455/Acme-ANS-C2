<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox path="tag" code="manager.flight.form.label.tag"/>
	<acme:input-checkbox path="selfTransfer" code="manager.flight.form.label.selfTransfer"/>
	<acme:input-money path="cost" code="manager.flight.form.label.cost"/>
	<acme:input-textbox path="description" code="manager.flight.form.label.description"/>
	<acme:input-checkbox path="confirmation" code="manager.flight.form.label.confirmation"/>
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish')}">
		<jstl:if test="${flightDraftMode == true}">
			<acme:submit code="manager.flight.form.button.update" action="/manager/flight/update"/>
			<acme:submit code="manager.flight.form.button.delete" action="/manager/flight/delete"/>
			<acme:submit code="manager.flight.form.button.publish" action="/manager/flight/publish"/>
		</jstl:if>
			<acme:button code="manager.flight.form.button.legs" action="/manager/leg/list?flightId=${id}"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="manager.flight.form.button.create" action="/manager/flight/create"/>
		</jstl:when>
	</jstl:choose>
	
</acme:form>