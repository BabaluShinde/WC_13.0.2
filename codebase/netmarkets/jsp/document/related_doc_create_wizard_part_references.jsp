<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ page import="com.ptc.windchill.enterprise.util.PartManagementHelper"%>

<%-->
  //  This is the way it should be implemented.  I Need to pass this IsPDMMethod
  //  parameter to see whether we need to filter only ref docs
  <c:choose>
      <c:when test='${param.isPDMMethod != null}'>
        <jsp:include page="/netmarkets/jsp/document/create.jsp" flush="true">
        <jsp:param name="type" value="WCTYPE|wt.doc.WTDocument|com.ptc.ReferenceDocument"/>
        </jsp:include>
      </c:when>
      <c:otherwise>
        <%@ include file="/netmarkets/jsp/document/create.jsp"%>
      </c:otherwise>
   </c:choose>
<--%>

<%
    boolean isPDMMethod = PartManagementHelper.getWcPDMMethodPref();
    if (isPDMMethod) {
%>      
        <%@ include file="/netmarkets/jsp/document/create.jsp"%>        
<%          
    } else {
%>      
        <jsp:include page="/netmarkets/jsp/document/create.jsp" flush="true">
        <jsp:param name="typePickerSeedObj" value="WCTYPE|wt.doc.WTDocument|com.ptc.ReferenceDocument"/>
        </jsp:include>
<%      
    } 
%>
