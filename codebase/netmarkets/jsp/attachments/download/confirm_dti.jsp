<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page
    session="true"
    import="com.ptc.windchill.enterprise.servlets.AttachmentsDownloadDirectionServlet"
    import="com.ptc.windchill.enterprise.attachments.attachmentsResource"
    import="wt.util.WTProperties"
    import="wt.util.WTMessage, wt.org.WTUser,
                 wt.preference.PreferenceClient,
                 wt.preference.PreferenceHelper,
                 wt.session.SessionHelper,
                 wt.fc.WTReference,wt.org.WTOrganization,wt.util.HTMLEncoder"


%><%@ page import="com.ptc.netmarkets.user.NmUser"
%>

<%
    String strCodeBase = WTProperties.getLocalProperties().getProperty("wt.server.codebase", null);
    String strURLBase = strCodeBase + "/servlet/AttachmentsDownloadDirectionServlet/primary?";
    String paraHolderOID = HTMLEncoder.encodeForHTMLAttribute(request.getParameter(AttachmentsDownloadDirectionServlet.PARA_CONTENT_HOLDER_OID));
    boolean paramAdded = false;
    if (paraHolderOID != null) {
        strURLBase += AttachmentsDownloadDirectionServlet.PARA_CONTENT_HOLDER_OID + "=" + paraHolderOID;
        paramAdded = true;
    }
    String paraItemOIDs = HTMLEncoder.encodeForHTMLAttribute(request.getParameter(AttachmentsDownloadDirectionServlet.PARA_CONTENT_ITEM_OIDS));
    if (paraItemOIDs != null) {
        if(paramAdded){
            strURLBase += "&" + AttachmentsDownloadDirectionServlet.PARA_CONTENT_ITEM_OIDS + "=" + paraItemOIDs;
        }
        else{
            strURLBase += AttachmentsDownloadDirectionServlet.PARA_CONTENT_ITEM_OIDS + "=" + paraItemOIDs;
        }
    }
    String role = HTMLEncoder.encodeForHTMLAttribute(request.getParameter("role"));
    if (role != null) {
        strURLBase += "&" + "role"+ "=" + role;
    }
    strURLBase=HTMLEncoder.encodeURIForHTMLAttribute(strURLBase);
    String strRejectOnceURL = HTMLEncoder.encodeURIForHTMLAttribute(strURLBase + "&" + AttachmentsDownloadDirectionServlet.PARA_DTI_STATUS + "=" + AttachmentsDownloadDirectionServlet.PARA_STATUS_INSTALLATION_REJECTED_ONCE);
    String strRejectURL = HTMLEncoder.encodeURIForHTMLAttribute(strURLBase + "&" + AttachmentsDownloadDirectionServlet.PARA_DTI_STATUS + "=" + AttachmentsDownloadDirectionServlet.PARA_STATUS_INSTALLATION_REJECTED);
    String forceDTIDownloadURL =    HTMLEncoder.encodeURIForHTMLAttribute(strURLBase + "&" + AttachmentsDownloadDirectionServlet.PARA_DTI_STATUS + "=" + AttachmentsDownloadDirectionServlet.PARA_STATUS_INSTALLED);

%>


 <%
         NmOid userOid = new NmOid();
         userOid.setType(NmUser.TYPE);
         userOid.setOid(SessionHelper.getPrincipal().getPersistInfo().getObjectIdentifier());
         wt.util.WTProperties properties = wt.util.WTProperties.getLocalProperties();
         NmContextBean nmcontextbean = new NmContextBean();
         nmcontextbean.setContext( NmContext.fromString("a$b$"+userOid+"$"));
         %>

<%


    WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
    WTContainerRef wtContRef=commandBean.getContainerRef();
    Boolean forceDTIDownload=false;
    WTOrganization wtOrg = user.getOrganization();

    forceDTIDownload = (Boolean)PreferenceHelper.service.getValue(wtContRef,"/com/ptc/windchill/enterprise/attachments/forceDesktopIntegration", PreferenceClient.WINDCHILL_CLIENT_NAME,user);

    NmAction prefAction = NmActionServiceHelper.service.getAction("preference","list");

    String prefURL=NetmarketURL.convertToShellURL(prefAction.getActionUrl(actionBean,  linkBean,  objectBean,  localeBean,  urlFactoryBean, nmcontextbean, sessionBean,request));

    String[] prefInserts = new String[2];
    prefInserts[0] = "<A HREF='"+prefURL+"'>";
    prefInserts[1] = "</A>";


    String[] downloadURLInserts = new String[2];
    downloadURLInserts[0] = "<A HREF='"+forceDTIDownloadURL+"'>";
    downloadURLInserts[1] = "</A>";

 %>


<c:set var="forceDTIDownload" value="<%=forceDTIDownload%>" />

<HTML>



<SCRIPT LANGUGE="JavaScript">

function cancelClicked() {
    window.close();
}

function okClicked() {

    var updateURL = "<%= strURLBase %>";
    var rejectOnceURL = "<%= strRejectOnceURL %>";
    var rejectDTIDownloadOnce=document.getElementById("useBrowser");
    if(rejectDTIDownloadOnce!=null && rejectDTIDownloadOnce !='undefined' && rejectDTIDownloadOnce.checked){

    window.location.replace(rejectOnceURL);

}else{


    window.location.replace(updateURL);
}



}

</SCRIPT>


<BODY  >

<FORM name="theform">
  <TABLE border="0">
    <TR>
        <TD><IMG SRC="<%= strCodeBase %>/netmarkets/images/mssg_confirm.gif" /></TD>
        <TD><B><%= WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_CONFIRM_MSG_ATTENTION, null) %></B></TD>
    </TR>
<c:choose>
 <c:when test='${forceDTIDownload}'>
    <TR>
        <TD />
        <TD>
            <%= WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_CONFIRM_MSG_FORCE_DTI_INSTRUCTION, null) %>
        </TD>
    </TR>
    <TR>
        <TD />
        <TD>
            <%= WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.INSTALL_DTI_MSG, null) %>
            <a href="<%=urlFactoryBean.getFullyQualifiedHREF("/install/msoi/setup.msi")%>"><%=WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.INSTALL_DTI_32BIT, null) %></a>
             <%=WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_OR_MSG_SMALL, null) %>

            <a href="<%=urlFactoryBean.getFullyQualifiedHREF("/install/msoi/x64/setup.msi")%>"><%=WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.INSTALL_DTI_64BIT, null) %></a>
        </TD>
    </TR>
    <TR>
        <TD />
        <TD>
            <%=WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_ADMIN_INSTALL_MSG, null) %>
        </TD>
    </TR>
    <TR>
        <TD />
        <TD>
            <%=WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_INSTALLATION_CONFIRM_MSG, null) %>
        </TD>
    </TR>
    </BR>

  </c:when>
  <c:otherwise>
    <TR>
        <TD />
        <TD>
            <%= WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_CONFIRM_MSG_DTI_INSTRUCTION, null) %>
        </TD>
    </TR>
    <TR><TD />
      <TD>
        <%=WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_EITHER_MSG, null) %>
      </TD>
    </TR>
    </BR>
    <TR>
        <TD />
        <TD>
          <TABLE border="0">
            <TR>
                <TD></TD>
                <TD>
                     <%= WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.INSTALL_DTI_MSG, null) %>
                    <a href="<%=urlFactoryBean.getFullyQualifiedHREF("/install/msoi/setup.msi")%>"><%=WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.INSTALL_DTI_32BIT, null) %></a>
                     <%=WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_OR_MSG_SMALL, null) %>
                    <a href="<%=urlFactoryBean.getFullyQualifiedHREF("/install/msoi/x64/setup.msi")%>"><%=WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.INSTALL_DTI_64BIT, null) %></a>
                </TD>
                <TD></TD>
            </TR>
            <TR>
                <TD />
                <TD>
                    <%=WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_ADMIN_INSTALL_MSG, null) %>
                </TD>
            </TR>
            <TR>
                <TD />
                <TD>
                    <%=WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_INSTALLATION_CONFIRM_MSG, null) %>
                </TD>
            </TR>
        </TABLE>
      </TD>
    </TR>
    <TR>
        <TD />
        <TD>
        </TD>
    </TR>

    </BR>

    <TR>
        <TD />
        <TD>
            <%=WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_OR_MSG, null) %>
        </TD>
    </TR>
    <TR>
        <TD />
        <TD>
            <TABLE border="0">
                <TR>
                    <TD></TD>
                    <TD>
                        <INPUT TYPE="checkbox" NAME="useBrowser" id="useBrowser" />
                        <%=WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_CONFIRM_MSG_REJECT, null) %></TD>
                </TR>
            </TABLE>
        </TD>
    </TR>
    </c:otherwise>
</c:choose>
    <TR>
        <TD />
        <TD>
            <INPUT TYPE="button" NAME="ok" ONCLICK="JavaScript:okClicked()"
                value="<%= WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_CONFIRM_LABEL_OK, null) %>" />
            <INPUT TYPE="button" NAME="cancel" ONCLICK="JavaScript:cancelClicked()"
                value="<%= WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_CONFIRM_LABEL_CANCEL, null) %>" />
        </TD>
    </TR>
  </TABLE>
</FORM>

  <P>
    <%= WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_CONFIRM_MSG_PREF, prefInserts) %>
  </P>
  <P>
    <%= WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.FORCE_DTI_DOWNLOAD_MSG, downloadURLInserts) %>
  </P>
</BODY>

</HTML>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
