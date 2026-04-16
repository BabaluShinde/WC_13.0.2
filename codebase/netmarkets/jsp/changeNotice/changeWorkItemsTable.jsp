<%@ page import="java.util.HashMap,
			     com.ptc.netmarkets.work.workResource,
 				 com.ptc.netmarkets.work.NmWorkItemCommands,
  				 com.ptc.netmarkets.actionitem.actionitemResource,
				 com.ptc.windchill.enterprise.work.assignmentslist.server.AssignmentsListHelper"
%>
<%! private static final String WORK_RESOURCE = "com.ptc.netmarkets.work.workResource";%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"              prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt"        prefix="fmt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/core"       prefix="wc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>

<%--> @COMMENTS Build the localized display messages<--%>
<fmt:setLocale value="${localeBean.locale}"/>
<jsp:include page="${mvc:getComponentURL('changeManagement.changeWorkItems')}" />
<%@ include file="/netmarkets/jsp/util/end.jspf"%>