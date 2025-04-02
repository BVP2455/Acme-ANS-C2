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

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form readonly="${!draftMode}">
    <acme:input-select code="customer.booking.list.label.flight" path="flight" choices="${flights}"/>
    <acme:input-textbox code="customer.booking.list.label.locatorCode" path="locatorCode"/>
    <acme:input-textbox code="customer.booking.list.label.lastCardNibble" path="lastCardNibble"/>
    <acme:input-select code="customer.booking.list.label.travelClass" path="travelClass" choices="${travelClasses}"/>
    <acme:input-double code="customer.booking.list.label.price" path="price" readonly="true"/>
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish')}">
		<jstl:if test="${draftMode}">
			<acme:submit code="customer.booking.form.button.update" action="/customer/booking/update"/>
			<acme:submit code="customer.booking.form.button.publish" action="/customer/booking/publish"/>
			<jstl:if test="${_command != 'create'}">
				<acme:button code="customer.booking.form.add.passenger" action="/customer/booking-record/create?bookingId=${id}"/>
			</jstl:if>
		</jstl:if>

		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="customer.booking.form.button.create" action="/customer/booking/create"/>
		</jstl:when>		
	</jstl:choose>	
	<jstl:if test="${_command != 'create'}">
		<acme:button code="customer.booking.form.show.passengers" action="/customer/passenger/list?bookingId=${id}"/>
	</jstl:if>
	
	


	
	
</acme:form>
