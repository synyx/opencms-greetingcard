<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ page import="com.synyx.greetingcard.TransmitMailBean" %>


<fmt:setLocale value="${cms:vfs(pageContext).requestContext.locale}"/>
<fmt:setBundle basename="com.synyx.greetingcards.messages"/>

<%
        TransmitMailBean bean = new TransmitMailBean();
        bean.init(pageContext, request, response);
        if (bean.isTransmitted()) { %>
            <fmt:message key="transmitMail.success"/>
        <% } else { %>
            <fmt:message key="transmitMail.error"/>
        <%}
%>
<br />
<a href="<cms:link>/system/modules/com.synyx.greetingcards/elements/chooseImage.jsp</cms:link>"><fmt:message key="transmitMail.new"/></a>

