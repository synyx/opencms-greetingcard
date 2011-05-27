<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ page import="org.opencms.jsp.CmsJspNavElement" %>
<%@ page import="java.util.List" %>
<%@ page import="com.synyx.greetingcard.ScaledImage" %>

<jsp:useBean id="chooseImageBean" class="com.synyx.greetingcard.ChooseImageBean" scope="page">
    <% chooseImageBean.init(pageContext, request, response); %>
</jsp:useBean>

<fmt:setLocale value="${cms:vfs(pageContext).requestContext.locale}"/>
<fmt:setBundle basename="com.synyx.greetingcards.messages"/>

<%
    List subFolders = chooseImageBean.getSubFolders();
    if (subFolders.size() > 0) {
        %>
        <h3><fmt:message key="chooseImage.chooseCategory"/>:</h3> 
        <%        
        // this variable is for counting the forms to identify them later
        for (int formularCounter = 0; formularCounter < subFolders.size(); formularCounter ++) {
            CmsJspNavElement subFolderNavElement = (CmsJspNavElement)subFolders.get(formularCounter); 
            // print out the possible selecteable subfolders
            // with the formularCounter variable all formulars get a unique id and so it is possible to submit them with java script 
            %>      
                <form name="nextSubFolder<%= formularCounter %>" method="post" action="#">           
                    <a href="javascript:document.nextSubFolder<%= formularCounter %>.submit()"><%= subFolderNavElement.getNavText() %></a>
                    <input type="hidden" name="greetingcard_path" value="<%= subFolderNavElement.getResourceName() %>" />
                </form>
            <%
        }  
    }


    List<ScaledImage> images = chooseImageBean.getImages();
    if (images.size() > 0) {
        %>
        <h3><fmt:message key="chooseImage.chooseImage"/>:</h3> 
        <%

        for (int formularCounter = 0; formularCounter < images.size(); formularCounter++) {
            ScaledImage image = images.get(formularCounter);
                       %>      
                <form name="detailView<%= formularCounter %>" method="post" action="<cms:link>/system/modules/com.synyx.greetingcards/elements/dataInput.jsp</cms:link>">           
                    <a href="javascript:document.detailView<%= formularCounter %>.submit()"><%= image.getImageTag() %></a> 
                    <input type="hidden" name="greetingcard_fileName" value="<%= image.getPathToTemplate() %>" />
                </form>
            <%                       
        }
        %>       
       <br /> <a href="javascript:history.back()"><fmt:message key="chooseImage.back"/></a>
        <%
    } 
    
%>           
