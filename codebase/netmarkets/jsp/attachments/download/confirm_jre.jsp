<%@ page 
	session="false"
	import="com.ptc.windchill.enterprise.servlets.AttachmentsDownloadDirectionServlet"
	import="com.ptc.windchill.enterprise.attachments.attachmentsResource"
	import="wt.util.WTProperties"
	import="wt.util.WTMessage"
%>
<%@page contentType="text/html;charset=UTF-8"%>
<%
 	String strCodeBase = WTProperties.getLocalProperties().getProperty("wt.server.codebase", null);
	String strURLBase = strCodeBase + "/servlet/AttachmentsDownloadDirectionServlet?";
	String paraHolderOID = request.getParameter(AttachmentsDownloadDirectionServlet.PARA_CONTENT_HOLDER_OID);
	if (paraHolderOID != null) {
		strURLBase += "&" + AttachmentsDownloadDirectionServlet.PARA_CONTENT_HOLDER_OID + "=" + paraHolderOID;
	}
	String paraItemOIDs = request.getParameter(AttachmentsDownloadDirectionServlet.PARA_CONTENT_ITEM_OIDS);
	if (paraItemOIDs != null) {
		strURLBase += "&" + AttachmentsDownloadDirectionServlet.PARA_CONTENT_ITEM_OIDS + "=" + paraItemOIDs;
	}
    String role = request.getParameter("role");
	if (role != null) {
		strURLBase += "&" + "role"+ "=" + role;
	}
	String strRejectOnceURL = strURLBase + "&" + AttachmentsDownloadDirectionServlet.PARA_JRE_STATUS + "=" + AttachmentsDownloadDirectionServlet.PARA_STATUS_INSTALLATION_REJECTED_ONCE;
	String strRejectURL = strURLBase + "&" + AttachmentsDownloadDirectionServlet.PARA_JRE_STATUS + "=" + AttachmentsDownloadDirectionServlet.PARA_STATUS_INSTALLATION_REJECTED;

	String[] prefInserts = new String[2];
	prefInserts[0] = "<A HREF='JavaScript:gotoUtilitiesPage()'>";
	prefInserts[1] = "</A>";
%>
<HTML>

<HEADER>

<SCRIPT LANGUGE="JavaScript">

function gotoUtilitiesPage() {
    window.opener.top.location.href="<%= strCodeBase %>/app/#ptc1/comp/preference.tree";
    window.close();
}

function cancelClicked() {
    window.close();
}

function okClicked() {
    if (document.theform.install.checked) {
    	gotoUtilitiesPage();
    } else if (document.theform.apply.checked) {
    	window.location.href="<%= strRejectURL %>";
    } else { // reject one time 
    	window.location.href="<%= strRejectOnceURL %>";
    } // end if checked
}

function rejectClicked() {
    document.theform.install.checked = false;
}

function installClicked() {
    document.theform.reject.checked = false;
    document.theform.apply.checked = false;
}

function onLoad() {
    window.resizeTo(600, 600);
}

</SCRIPT>

</HEADER>

<BODY ONLOAD="JavaScript:onLoad()">

<FORM name="theform">
  <TABLE>
    <TR>
      <TD><IMG SRC="<%= strCodeBase %>/netmarkets/images/mssg_confirm.gif" /></TD>
      <TD><B><%= WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_CONFIRM_MSG_ATTENTION, null) %></B></TD>
    </TR>
    <TR><TD />
      <TD>
      	<%= WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_CONFIRM_MSG_JRE_INSTRUCTION, null) %>
      </TD>
    </TR>
    <TR><TD />
      <TD>
	<INPUT TYPE="radio" NAME="install" ONCLICK="JavaScript:installClicked()" />
	<%= WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_CONFIRM_MSG_JRE_INSTALL, null) %>
      </TD>
    </TR>
    <TR><TD />
      <TD>
	<INPUT TYPE="radio" NAME="reject" CHECKED ONCLICK="JavaScript:rejectClicked()" />
	<%= WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_CONFIRM_MSG_REJECT, null) %>
      </TD>
    </TR>
    <TR><TD />
      <TD>
        <TABLE><TR><TD WIDTH="20" /><TD>
          <INPUT TYPE="checkbox" NAME="apply" />
          <%= WTMessage.getLocalizedMessage(attachmentsResource.class.getName(), attachmentsResource.DOWNLOAD_CONFIRM_MSG_APPLY, null) %>
        </TD></TR></TABLE>
      </TD>
    </TR>
    <TR><TD />
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
  
</BODY>

</HTML>
