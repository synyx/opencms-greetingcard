<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="${cms:vfs(pageContext).requestContext.locale}"/>
<fmt:setBundle basename="com.synyx.greetingcards.messages"/>

<jsp:useBean id="showPreviewBean" class="com.synyx.greetingcard.ShowPreviewBean" scope="page">
    <% showPreviewBean.init(pageContext, request, response); %>
</jsp:useBean>
<%
    if (showPreviewBean.hasError()) {
%>
        <fmt:message key="showPreview.generalError"/>
        <br/>
        <a href="javascript:window.back()"><fmt:message key="showPreview.back"/></a>
        <%
        return;
    } else if (showPreviewBean.isDateInPast()) {
 %>
        <fmt:message key="showPreview.errorDatePast"/><br />
        <a href="javascript:window.back();"><fmt:message key="showPreview.back"/></a><br />
<%  } else if (showPreviewBean.isTooManyDays()) { %>
        <fmt:message key="showPreview.errorDateInvalid"/><br />
        <a href="javascript:window.back();"><fmt:message key="showPreview.back"/></a><br />
<%  } else {
    // the selected date is correct

%>


<img src="<%=showPreviewBean.getUrl()%>" /><br />

<fmt:message key="showPreview.sender"/>: <%= (showPreviewBean.getAuthorName() + " (" + showPreviewBean.getAuthorAddress() + ")") %><br />
<fmt:message key="showPreview.receiver"/>: <%= (showPreviewBean.getReceiverName() + " (" + showPreviewBean.getReceiverAddress() + ")" )%><br />
<fmt:message key="showPreview.subject"/>: <%= showPreviewBean.getSubject() %><br />
<fmt:message key="showPreview.sendDate"/>:
<%if (!showPreviewBean.isSendNow()) { %>
     <%= showPreviewBean.getDateDay()  + "." + showPreviewBean.getDateMonth() + "." + showPreviewBean.getDateYear() + " um " + showPreviewBean.getDateHour() + "." + showPreviewBean.getDateMinute() + " Uhr" %><br />
<% } else { %>
    <fmt:message key="showPreview.sendDateNow"/><br />
<% } %>



<form name="transmit" method="post" action="<cms:link>/system/modules/com.synyx.greetingcards/elements/transmitMail.jsp</cms:link>">
    <input type="submit" value="abschicken" />
    <input type="hidden" name="author_name" value="<%= showPreviewBean.getAuthorName() %>" /> 
    <input type="hidden" name="author_address" value="<%= showPreviewBean.getAuthorAddress() %>" /> 
    <input type="hidden" name="receiver_name" value="<%= showPreviewBean.getReceiverName() %>" /> 
    <input type="hidden" name="receiver_address" value="<%= showPreviewBean.getReceiverAddress() %>" /> 
    <input type="hidden" name="subject" value="<%= showPreviewBean.getSubject() %>" />
    <input type="hidden" name="greetingcard_fileName" value='<%= request.getParameter("greetingcard_fileName") %>' />
    <% if (showPreviewBean.isSendNow()) { %>
         <input type="hidden" name="transmit_date" value="0" /> 
    <% } else { %>
         <input type="hidden" name="transmit_date" value="<%= showPreviewBean.getTransmitTime()%>" /> 
    <% } %>
    <input type="hidden" name="image_url" value="<%= showPreviewBean.getResourceName() %>" /> 
</form>    

<a href="javascript:window.back();"><fmt:message key="showPreview.backToEdit"/></a><br />
<a href="<cms:link>/system/modules/com.synyx.greetingcards/elements/chooseImage.jsp</cms:link>"><fmt:message key="showPreview.backToSelection"/></a>

<% } // The else of the timecheck ends here %>

   