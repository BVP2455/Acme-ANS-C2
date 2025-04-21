
<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox path="name" code="administrator.airline.form.label.name"/>
	<acme:input-textbox path="iataCode" code="administrator.airline.form.label.iataCode"/>
	<acme:input-url path="website" code="administrator.airline.form.label.website"/>
	<acme:input-select path="type" code="administrator.airline.form.label.type" choices="${types}"/>
	<acme:input-moment path="foundationMoment" code="administrator.airline.form.label.foundationMoment"/>
	<acme:input-textbox path="email" code="administrator.airline.form.label.email"/>
	<acme:input-textbox path="phoneNumber" code="administrator.airline.form.label.phoneNumber"/>
	<acme:input-checkbox path="confirmation" code="administrator.airline.form.label.confirmation"/>
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update')}">
			<acme:submit code="administrator.airline.form.button.update" action="/administrator/airline/update"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="administrator.airline.form.button.create" action="/administrator/airline/create"/>
		</jstl:when>	
	</jstl:choose>
</acme:form>