<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@page import="java.util.Iterator" %>

<jsp:useBean id="dataInputBean" class="com.synyx.greetingcard.DataInputBean" scope="page">
	<% dataInputBean.init(pageContext, request, response); %>
</jsp:useBean>
<jsp:useBean id="dateBean" class="com.synyx.greetingcard.DateBean" scope="page"/>

<fmt:setLocale value="${cms:vfs(pageContext).requestContext.locale}"/>
<fmt:setBundle basename="com.synyx.greetingcards.messages"/>

<script type="text/javascript">
function chkFormular () {
  if (document.input.author_name.value == "") {
    alert(unescape("<fmt:message key='dataInput.errorSenderName'/>%21"));
    document.input.author_name.focus();
    return false;
  }
  if (document.input.author_address.value.indexOf("@") == -1) {
    alert(unescape("<fmt:message key='dataInput.errorSenderMail'/>%21"));
    document.input.author_address.focus();
    return false;
  }
  if (document.input.receiver_name.value == "") {
    alert(unescape("<fmt:message key='dataInput.errorReceiverName'/>"));
    document.input.receiver_name.focus();
    return false;
  }
<%
    if (dataInputBean.isUseWhitelist()) {
%>
  if (document.input.receiver_address_whitelist.value.indexOf("@") != -1) {
    alert(unescape("<fmt:message key='dataInput.errorWhitelistDomain'/>"));
    document.input.receiver_address_whitelist.focus();
    return false;
  }
  if (document.input.receiver_address_whitelist.value == "") {
    alert(unescape("<fmt:message key='dataInput.errorReceiverMail'/>"));
    document.input.receiver_address_whitelist.focus();
    return false;
  }
  <%
  } else {
  %>
  if (document.input.receiver_address.value.indexOf("@") == -1) {
    alert(unescape("<fmt:message key='dataInput.errorReceiverMail'/>"));
    document.input.receiver_address.focus();
    return false;
  }
  <%
  }
  %>
  if (document.input.subject.value == "") {
    alert(unescape("<fmt:message key='dataInput.errorSubject'/>"));
    document.input.subject.focus();
    return false;
  } 
  if (document.input.text0.value.length > 300) {
    alert(unescape("<fmt:message key='dataInput.errorText'/>"));
    document.input.text0.focus();
    return false;
  }
}
// b.schlopsnies<at>waz.de
function choseSendTime() {
                 if (document.input.sendNow.value!=0){
document.input.date_day.disabled=false;

document.input.date_month.disabled=false

document.input.date_year.disabled=false;

document.input.date_hour.disabled=false;

document.input.date_minute.disabled=false;

} else {

document.input.date_day.disabled=true;

document.input.date_month.disabled=true

document.input.date_year.disabled=true;

document.input.date_hour.disabled=true;

document.input.date_minute.disabled=true;

}

}

// ende





</script>

<form name="input" method="post" action="<cms:link>/system/modules/com.synyx.greetingcards/elements/showPreview.jsp</cms:link>" onsubmit="return chkFormular()">
	<input type="hidden" name="greetingcard_fileName" value="<%=dataInputBean.getGreetingcardPath()%>"/>
	<table name="input" controller="none">
		<tr>
			<td>
				<fmt:message key="dataInput.name"/>: 
			</td>
			<td>
				<input type="text" name="author_name" value="" size="40"/>
			</td>
		</tr>
		<tr>
			<td>
				<fmt:message key="dataInput.mail"/>:
			</td>
			<td>
				<input type="text" name="author_address" value="" size="40"/>
			</td>
		</tr>
		<tr>
			<td>
				<fmt:message key="dataInput.receiverName"/>: 
			</td>
			<td>
				<input type="text" name="receiver_name" value="" size="40"/>
			</td>
		</tr>
		<tr>
			<td>
				<fmt:message key="dataInput.receiverMail"/>: 
			</td>
			<td>
				<%
				if (dataInputBean.isUseWhitelist()) {
				%>
				<input type="text" name="receiver_address_whitelist" size="40"/>
				<select name="receiver_domain" >
					<%
					for(Iterator it = dataInputBean.getDomains().iterator(); it.hasNext(); ) {
						
						String domain = (String) it.next();
					%>
					<option value="<%=domain%>">@<%=domain%></option>
					<%
					}
					%>
				</select>
				<%
				} else {
				%>
				<input type="text" name="receiver_address" size="40"/>
				<%
				}
				%>
			</td>
		</tr>
		<tr>
			<td>
				<fmt:message key="dataInput.subject"/>:
			</td>
			<td>
				<input type="text" name="subject" value="Eine Gru&szlig;karte f&uuml;r Sie" size="40"/>
			</td>
		</tr>
		
		<%
		pageContext.setAttribute("greetingcard_content_path", dataInputBean.getGreetingcardPath());
		int whichTextField = 0;
		// to identify the text the counter is set
		%>
		<cms:contentload collector="singleFile" param="%(pageContext.greetingcard_content_path)">
			<cms:contentloop element="TextField">
				<tr>
					<td>
                                             <fmt:message key="dataInput.text">
                                                 <fmt:param value="<%= whichTextField + 1 %>"/>
                                             </fmt:message>:
                                        </td>
					<td>
						<textarea name="text<%= whichTextField%>" rows="8" cols="42" maxlength="299"><cms:contentshow element="Description" /></textarea>
					</td>
				</tr>
				<%whichTextField++;%>
			</cms:contentloop>
		</cms:contentload>
		
		<tr>
			<td>
				<fmt:message key="dataInput.sendDate"/>: 
			</td>
			<td>
				<select name="sendNow">
					<option value="0" selected="selected"><fmt:message key="dataInput.sendNow"/></option>
					<option value="sendLater"><fmt:message key="dataInput.sendLater"/>: </option>
				</select>
				<br />
				<select name="date_day" >
					<%
					for (int day = 1; day <= 31; day++) {
					if (day == dateBean.getCurrentDay()) {
						out.print("<option value=\"" + day + "\" title=\"\" selected=\"selected\">" + day + "</option>");
					} else {
						out.print("<option value=\"" + day + "\" title=\"\">" + day + "</option>");
					}
					}
					%> 
				</select>
				<select name="date_month" > 
					<%
					for (int month = 1; month <= 12; month++) {
					if (month == dateBean.getCurrentMonth()) {
						out.print("<option value=\"" + month + "\" title=\"\" selected=\"selected\">" + month + "</option>");
					} else {
						out.print("<option value=\"" + month + "\" title=\"\">" + month + "</option>");
					}
					}
					%> 
				</select>
				<select name="date_year" > 
					<%
					for (int year = dateBean.getCurrentYear(); year	<= dateBean.getCurrentYear() + 2; year++) {
					if (year == dateBean.getCurrentYear()) {
						out.print("<option value=\"" + year + "\" title=\"\" selected=\"selected\">" + year + "</option>");
					} else {
						out.print("<option value=\"" + year + "\" title=\"\">" + year + "</option>");
					}
					}
					%> 
				</select>
				<select name="date_hour" >
					<%
					int currentHour = dateBean.getCurrentHour();
					if ((dateBean.getCurrentMinute() / 15) == 3) {
						currentHour += 1;
					}
					for (int hour = 1; hour <= 24; hour++) {
						if (hour == currentHour) {
							out.print("<option value=\"" + hour + "\" title=\"\" selected=\"selected\">" + hour + "</option>");
						} else {
							out.print("<option value=\"" + hour + "\" title=\"\">" + hour + "</option>");
						}
					}
					%> 
				</select>
				<select name="date_minute" >
					<%
					for (int quarterHour = 0; quarterHour <= 3;	quarterHour++) {
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
	</table>
	<input type="hidden" name="greetingcard_fileName" value="<%=dataInputBean.getGreetingcardPath()%>" />
	<input type="submit" value="<fmt:message key='dataInput.preview'/>" />
</form>

<a href="javascript:history.back()"><fmt:message key='dataInput.back'/></a>

