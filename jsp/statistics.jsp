<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.jsp.CmsJspActionElement" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.workplace.CmsDialog" %>
<%@ page import="org.opencms.file.CmsProperty" %>
<%@ page import="com.synyx.greetingcard.dao.GreetingcardConfig" %>
<%@ page import="com.synyx.greetingcard.dao.GreetingcardConfigDao" %>
<%@ page import="com.synyx.greetingcard.dao.OpenCmsGreetingcardConfigDao" %>

<fmt:setLocale value="${cms:vfs(pageContext).requestContext.locale}"/>
<fmt:setBundle basename="com.synyx.greetingcards.messages"/>

<jsp:useBean class="com.synyx.greetingcard.DateBean"
    id="dateBean" scope="session"/>

<%

CmsJspActionElement jsp = new CmsJspActionElement(pageContext, request, response);
CmsObject cms = jsp.getCmsObject();

// initialize the workplace class
CmsDialog wp = new CmsDialog(pageContext, request, response);
		
if (wp.getAction()==CmsDialog.ACTION_CANCEL) {
	wp.actionCloseDialog();
	return;
}
List<CmsResource> cards = null;
String formattedFrom = null;
String formattedTo = null;
%>

<%= wp.htmlStart() %>

<%= wp.bodyStart("dialog") %>
<%= wp.dialogStart() %>
<%= wp.dialogContentStart(wp.getParamTitle()) %>

<%
// check if the form is submitted already
if (request.getParameter("state") != null && request.getParameter("state").equals("sent")) {
    String fromDay = request.getParameter("from_day");
    String fromMonth = request.getParameter("from_month");
    String fromYear = request.getParameter("from_year");
    String fromHour = request.getParameter("from_hour");
    String fromMinute = request.getParameter("from_minute");
    String toDay = request.getParameter("to_day");
    String toMonth = request.getParameter("to_month");
    String toYear = request.getParameter("to_year");
    String toHour = request.getParameter("to_hour");
    String toMinute = request.getParameter("to_minute");
    
    String type = request.getParameter("type");
    
    Date from = dateBean.parseDate(fromDay, fromMonth, fromYear, fromHour, fromMinute);
    Date to = dateBean.parseDate(toDay, toMonth, toYear, toHour, toMinute);
    if (from != null && to != null) {
        formattedFrom = dateBean.formatDate(from);
        formattedTo = dateBean.formatDate(to);
        cards = new ArrayList<CmsResource>();
        
        GreetingcardConfigDao configDao = new OpenCmsGreetingcardConfigDao(cms);
        GreetingcardConfig greetingcardConfig = configDao.readConfig();
        
        List files = cms.getFilesInFolder(cms.getRequestContext().removeSiteRoot(greetingcardConfig.getArchiveFolder()));
        for (Iterator it = files.iterator(); it.hasNext(); ) {
            CmsResource res = (CmsResource) it.next();
            CmsProperty prop = cms.readPropertyObject(res, "greetingCardInternal", false);
            String value = "";
            if (prop != null && prop.getValue() != null) {
                value = prop.getValue();
            }
            if (type.equals("all") || (type.equals("intern") && value.equals("true"))
                || (type.equals("extern") && value.equals("false"))) {
                if (res.getDateLastModified() >= from.getTime() 
                    && res.getDateLastModified() <= to.getTime()) {
                    cards.add(res);
                }
            }
        }
    } else {
%>

<table>
    <tr>
        <td><fmt:message key="statistics.dateInvalid"/></td>
    </tr>
        
</table>
<%
    }
}

%>

<table>
    <form action="#">
        <input type="hidden" name="state" value="sent"/>
        <tr>
            <td><fmt:message key="statistics.type"/>:</td>
            <td colspan="3">
                <select name="type">
                    <option selected="selected" value="all"><fmt:message key="statistics.typeAll"/></option>
                    <option value="intern"/><fmt:message key="statistics.typeInternal"/></option>
                    <option value="extern"/><fmt:message key="statistics.typeExternal"/></option>
                </select>
            </td>
        </tr>
        <tr>
            <td><fmt:message key="statistics.from"/>:</td>
            <td>
                <select name="from_day">
                    <%
                    for (int day = 1; day <= 31; day++) {
                    out.println("<option value=\"" + dateBean.formatTwoDigits(day) + "\" title=\"\">" + dateBean.formatTwoDigits(day) + "</option>");
                    }
                    %>
                </select>
                <select name="from_month">
                    <%
                    for (int month = 1; month <= 12; month++) {
                    out.println("<option value=\"" + dateBean.formatTwoDigits(month) + "\" title=\"\">" + dateBean.formatTwoDigits(month) + "</option>");
                    }
                    %>
                </select>
                <select name="from_year">
                    <%
                    for (Iterator<Integer> it = dateBean.getYearsForStatistic().iterator(); it.hasNext(); ) {
                    int year = it.next();
                    out.println("<option value=\"" + year + "\" title=\"\">" + year + "</option>");
                    }
                    %>
                </select>
                <select name="from_hour">
                    <%
                    for (int hour = 0; hour < 24; hour++) {
                    out.println("<option value=\"" + dateBean.formatTwoDigits(hour) + "\" title=\"\">" + dateBean.formatTwoDigits(hour) + "</option>");
                    }
                    %>
                </select>
                <select name="from_minute">
                    <%
                    for (int minute = 0; minute < 60; minute = minute + 15) {
                    out.println("<option value=\"" + dateBean.formatTwoDigits(minute) + "\" title=\"\">" + dateBean.formatTwoDigits(minute) + "</option>");
                    }
                    %>
                </select>
            </td>
            <td><fmt:message key="statistics.to"/>:</td>
            <td>
                <select name="to_day" >
                    <%
                    for (int day = 1; day <= 31; day++) {
                        String formattedDay = dateBean.formatTwoDigits(day);
                        if (day == dateBean.getCurrentDay()) {
                            out.print("<option value=\"" + formattedDay + "\" title=\"\" selected=\"selected\">" + formattedDay + "</option>");
                        } else {
                            out.print("<option value=\"" + formattedDay + "\" title=\"\">" + formattedDay + "</option>");
                        }
                        }
                    %>                     
                </select>
                <select name="to_month" > 
                    <%
                    for (int month = 1; month <= 12; month++) {
                        String formattedMonth = dateBean.formatTwoDigits(month);
                        if (month == dateBean.getCurrentMonth()) {
                            out.print("<option value=\"" + formattedMonth + "\" title=\"\" selected=\"selected\">" + formattedMonth + "</option>");
                        } else {
                            out.print("<option value=\"" + formattedMonth + "\" title=\"\">" + formattedMonth + "</option>");
                        }
                        }
                    %>                       
                </select>
                <select name="to_year" > 
                    <%
                    for (Iterator<Integer> it = dateBean.getYearsForStatistic().iterator(); it.hasNext(); ) {
                    int year = it.next();
                        if (year == dateBean.getCurrentYear()) {
                            out.print("<option value=\"" + year + "\" title=\"\" selected=\"selected\">" + year + "</option>");
                        } else {
                            out.print("<option value=\"" + year + "\" title=\"\">" + year + "</option>");
                        }
                        }
                    %> 
                </select>
                <select name="to_hour" >
                    <%
                    int currentHour = dateBean.getCurrentHour();
                    if ((dateBean.getCurrentMinute() / 15) == 3) {
                        currentHour += 1;
                    }
                    for (int hour = 0; hour < 24; hour++) {
                        String formattedHour = dateBean.formatTwoDigits(hour);
                        if (hour == currentHour) {
                            out.print("<option value=\"" + formattedHour + "\" title=\"\" selected=\"selected\">" + formattedHour + "</option>");
                        } else {
                            out.print("<option value=\"" + formattedHour + "\" title=\"\">" + formattedHour + "</option>");
                        }
                    }
                    %>                      
                </select>
                <select name="to_minute" >
                    <%
                    for (int quarterHour = 0; quarterHour <= 3; quarterHour++) {
                        String minute = "00";
                        if (quarterHour > 0) {
                            minute = "" + quarterHour * 15;
                        }
                        if (quarterHour == (dateBean.getCurrentMinute() / 15) + 1) {
                            out.print("<option value=\"" + minute + "\" title=\"\" selected=\"selected\">" + minute + "</option>");
                        } else {
                            out.print("<option value=\"" + minute + "\" title=\"\">" + minute + "</option>");
                        }
                    }
                    %>                     
                </select>
            </td>
        </tr>
            <tr>
                <td colspan="3">
                    <%= wp.dialogButtonRowStart() %>
                    <%= wp.dialogButtonsOk() %>
                    <%= wp.dialogButtonRowEnd() %>
                </td>
            </tr>
        </form>
</table>
<%
    if(cards != null) {
%>
<table>
    <tr>
        <td colspan="2">
            <fmt:message key="statistics.result">
                <fmt:param value="<%= formattedFrom %>"/>
                <fmt:param value="<%= formattedTo %>"/>
                <fmt:param value="<%= cards.size() %>"/>
            </fmt:message>
            </td>
    </tr>
    <tr>
        <th><fmt:message key="statistics.path"/></th>
        <th><fmt:message key="statistics.date"/></th>
    </tr>
    <%
        for (Iterator<CmsResource> it = cards.iterator(); it.hasNext(); ) {
            CmsResource res = it.next();
    %>
            <tr>
                <td><%= res.getRootPath() %></td>
                <td><%= dateBean.formatDate(res.getDateCreated()) %></td>
            </tr>
    <%
        }
    %>
</table>
<%
    }
%>
<%= wp.dialogContentEnd() %>
<%= wp.dialogEnd() %>
<%= wp.bodyEnd() %>
<%= wp.htmlEnd() %>
