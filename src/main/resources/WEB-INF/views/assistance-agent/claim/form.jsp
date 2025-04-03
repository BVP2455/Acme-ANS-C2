<%--
- form.jsp
-
- Copyright (C) 2012-2025 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form readonly="${false}">
	<acme:input-moment code="RegistrationMoment" path="registrationMoment" readonly="true"/>	
	<acme:input-textbox code="PassengerEmail" path="passengerEmail"/>	
	<acme:input-textarea code="Description" path="description"/>	
	<acme:input-select code="Type" path="type" choices="${types}"/>
	<acme:input-select code="Leg" path="leg" choices="${legs}"/>
	<acme:input-textbox code="Indicator" path="indicator" readonly="true"/>
		
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish')}">
		<jstl:if test="${draftMode}">
			<acme:submit code="Update" action="/assistance-agent/claim/update"/>
			<acme:submit code="Publish" action="/assistance-agent/claim/publish"/>
			<acme:submit code="Delete" action="/assistance-agent/claim/delete"/>
			
		</jstl:if>

		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="Create" action="/assistance-agent/claim/create"/>
		</jstl:when>		
	</jstl:choose>	
		<jstl:if test="${_command != 'create'}">
		<acme:button code="Show trackingLogs" action="/assistance-agent/tracking-log/list?claimId=${id}"/>
	</jstl:if>
</acme:form>
