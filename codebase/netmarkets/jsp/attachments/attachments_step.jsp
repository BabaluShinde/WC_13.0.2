<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ page import="com.ptc.windchill.enterprise.attachments.attachmentsResource" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.StringTokenizer" %>
<%@ page import="com.ptc.core.components.util.PropagationHelper" %>
<%@ page import="com.ptc.netmarkets.model.NmOid" %>
<%@ page import="wt.content.ContentHolder" %>
<%@ page import="wt.preference.PreferenceHelper" %>
<%@ page import="wt.preference.PreferenceClient" %>
<%@ page import="com.ptc.jca.json.table.TableConfigHolder" %>
<%@ page import="com.ptc.mvc.components.FindInTableMode" %>
<%@ page import="wt.workflow.work.WorkItem" %>
<%@ page import="wt.org.WTUser" %>
<%@ include file="/netmarkets/jsp/attachments/initAttachments.jspf"%>
<%@ include file="/netmarkets/jsp/attachments/initPTCAttachments.jspf"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/core" prefix="core"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<c:if test="${param.role != null}">
   <c:set var="role" value="${param.role}" scope="request"/>
</c:if>

<%   String paraCheckinOID = request.getParameter("checkinOid");   %>
<c:if test="${param.checkinOid != null}">
   <c:set var="checkinOid" value="<%= paraCheckinOID %>" scope="request"/>
</c:if>
<c:if test="${checkinOid != null}">
  <jsp:setProperty name="commandBean" property="elemAddress" value="${checkinOid}" />
</c:if>


<!-- Get the localized strings from the resource bundle -->
<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.attachments.attachmentsResource" />

<fmt:message var="att_label"          key="<%= attachmentsResource.ATTACHMENT_LABEL %>" scope="request"/>
<fmt:message var="att_number"         key="<%= attachmentsResource.ATTACHMENT_NUMBER %>" scope="request"/>
<fmt:message var="att_name"           key="<%= attachmentsResource.ATTACHMENT_NAME %>" scope="request"/>

<%
wt.org.WTUser user = (wt.org.WTUser)wt.session.SessionHelper.getPrincipal();

%>

<fmt:message var="att_location"       key="<%= attachmentsResource.ATTACHMENT_LOCATION %>" scope="request"/>
<fmt:message var="att_description"    key="<%= attachmentsResource.ATTACHMENT_DESCRIPTION %>" scope="request"/>
<fmt:message var="att_comments"       key="<%= attachmentsResource.ATTACHMENT_COMMENTS %>" scope="request"/>
<fmt:message var="att_distributable"  key="<%= attachmentsResource.ATTACHMENT_EXTERNALDISTRIBUTION %>" scope="request"/>
<fmt:message var="att_authoredBy"     key="<%= attachmentsResource.ATTACHMENT_AUTHOREDBY %>" scope="request"/>
<fmt:message var="att_lastAuthored"   key="<%= attachmentsResource.ATTACHMENT_LASTAUTHORED %>" scope="request"/>
<fmt:message var="att_fileVersion"    key="<%= attachmentsResource.ATTACHMENT_FILEVERSION %>" scope="request"/>
<fmt:message var="att_toolName"       key="<%= attachmentsResource.ATTACHMENT_TOOLNAME %>" scope="request"/>
<fmt:message var="att_toolVersion"    key="<%= attachmentsResource.ATTACHMENT_TOOLVERSION %>" scope="request"/>

<!-- get optional column preferences -->
<%
    Boolean prefSortNumber    = (Boolean)PreferenceHelper.service.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/sortNumber",    PreferenceClient.WINDCHILL_CLIENT_NAME);
    Boolean prefComments      = (Boolean)PreferenceHelper.service.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/comments",      PreferenceClient.WINDCHILL_CLIENT_NAME);
    Boolean prefDistributable = (Boolean)PreferenceHelper.service.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/distributable", PreferenceClient.WINDCHILL_CLIENT_NAME);
    Boolean prefAuthoredBy    = (Boolean)PreferenceHelper.service.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/authoredBy",    PreferenceClient.WINDCHILL_CLIENT_NAME);
    Boolean prefLastAuthored  = (Boolean)PreferenceHelper.service.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/lastAuthored",  PreferenceClient.WINDCHILL_CLIENT_NAME);
    Boolean prefFileVersion   = (Boolean)PreferenceHelper.service.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/fileVersion",   PreferenceClient.WINDCHILL_CLIENT_NAME);
    Boolean prefToolName      = (Boolean)PreferenceHelper.service.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/toolName",      PreferenceClient.WINDCHILL_CLIENT_NAME);
    Boolean prefToolVersion   = (Boolean)PreferenceHelper.service.getValue("/com/ptc/windchill/enterprise/attachments/optionalAttributes/toolVersion",   PreferenceClient.WINDCHILL_CLIENT_NAME);
%>
<c:set var="cSortNumber"    value="<%= prefSortNumber    %>" scope="request"/>
<c:set var="cComments"      value="<%= prefComments      %>" scope="request"/>
<c:set var="cDistributable" value="<%= prefDistributable %>" scope="request"/>
<c:set var="cAuthoredBy"    value="<%= prefAuthoredBy    %>" scope="request"/>
<c:set var="cLastAuthored"  value="<%= prefLastAuthored  %>" scope="request"/>
<c:set var="cFileVersion"   value="<%= prefFileVersion   %>" scope="request"/>
<c:set var="cToolName"      value="<%= prefToolName      %>" scope="request"/>
<c:set var="cToolVersion"   value="<%= prefToolVersion   %>" scope="request"/>


<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<input type="hidden" name="newFiles" id="newFiles" value="" />
<input type="hidden" name="fileSep" id="fileSep" value="\" />
<input type="hidden" name="fileAttachmentCount" id="fileAttachmentCount" value="0" />
<input type="hidden" name="scmResults" value="" id="scmResults">

<%
String scmiPickerAddress ="";
Object refObject = null;
String paraoid = request.getParameter("oid");
if (paraoid != null && paraoid.length() > 0) {
    NmOid oid = NmOid.newNmOid(paraoid);
    paraoid = oid.getOid().toString();
    refObject = oid.getRefObject();
}
if( wt.facade.scm.ScmFacade.getInstance().isInstalled())
{
    if (refObject != null)
    {
         // SPR 2099982. Added a check for WorkItem
         // SPR 2173767. Added a check for WTUser
         if(! (refObject instanceof WorkItem || refObject instanceof WTUser))
         {
        	 scmiPickerAddress = wt.facade.scm.gui.ScmGuiHelper.getBrowserURL(paraoid);
         }
    }
}
%>
<script Language="JavaScript">
      scmpickerlocation = '<%=scmiPickerAddress%>';
</script>

<jsp:include page="/netmarkets/jsp/attachments/attachments_step_include.jsp" />

<!-- Below script is added for auto resizing of the table column -->
<script Language="JavaScript">
    //This attachment_step table is being used in differnet functionalities like create document, edit, checkout and edit
    //This table many be empty or have some rows when its launched. This is based on where its used. Hence it requires both 'add' and 'datachanged' callbacks
    //In some cases, if table is already available, 'onAvailable' is not applicable. And in other case, table will be availabe, and here it requires onAvailable
    var grid =Ext.getCmp('${tableId}');
    if(grid)
    {
		grid.getStore().on('add', function(store) {
			grid.clearStickyConfig(); // reset values
			PTC.jca.ColumnUtils.resizeAllColumns(grid);
			return true;
		}, null, {single: true, delay:100}); // only run this function one time
	
		grid.getStore().on('datachanged', function(store) {
			grid.clearStickyConfig(); // reset values
			PTC.jca.ColumnUtils.resizeAllColumns(grid);
			return true;
		}, null, {single: true, delay:100}); // only run this function one time
    }
    else
    {
        Ext.ComponentMgr.onAvailable('${tableId}',function (){
            var grid =Ext.getCmp('${tableId}');
            if(grid){
                grid.getStore().on('add', function(store) {
                    grid.clearStickyConfig(); // reset values
                    PTC.jca.ColumnUtils.resizeAllColumns(grid);
                    return true;
                }, null, {single: true, delay:100}); // only run this function one time
                
                grid.getStore().on('datachanged', function(store) {
                    grid.clearStickyConfig(); // reset values
                    PTC.jca.ColumnUtils.resizeAllColumns(grid);
                    return true;
                }, null, {single: true, delay:100}); // only run this function one time
            }
        });
    }
</script>

<input type="hidden" name="scmi_picker" id="scmi_picker" value="<%=scmiPickerAddress%>"/>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
