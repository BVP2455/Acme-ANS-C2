<%--
- list.jsp
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

<acme:list>
	<acme:list-column code="assistanceAgent.claim.list.label.registrationMoment" path="registrationMoment"/>
	<acme:list-column code="assistanceAgent.claim.list.label.passengerEmail" path="passengerEmail"/>
	<acme:list-column code="assistanceAgent.claim.list.label.description" path="description"/>
	<acme:list-column code="assistanceAgent.claim.list.label.type" path="type"/>
	<acme:list-column code="assistanceAgent.claim.list.label.status" path="status"/>
	
	<acme:list-payload path="payload"/>
</acme:list>

<acme:button code="assistanceAgent.claim.list.button.create" action="/assistance-agent/claim/create"/>

