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

<acme:form>

    <acme:input-select code="customer.bookingRecord.list.label.passenger" path="passenger" choices="${passengers}"/>
    <acme:input-textbox code="customer.booking.list.label.locatorCode" path="locatorCode" readonly="true"/>

	
	<acme:link code="customer.passenger.form.link.create" action="/customer/passenger/create"/><br>
	<br>
	<jstl:choose>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="customer.bookingRecord.form.button.create" action="/customer/booking-record/create?bookingId=${booking.id}"/>
			
		</jstl:when>		
	</jstl:choose>	
	
	
</acme:form>
