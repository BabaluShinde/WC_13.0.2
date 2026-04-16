<%@ page import="wt.content.ContentHolder,wt.content.ContentHelper,wt.content.ContentRoleType,
                 wt.fc.QueryResult,wt.fc.ReferenceFactory,wt.util.WTException,
                 wt.content.ApplicationData,wt.fc.WTObject" %>

<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/jcaMvc" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    System.out.println(">>> customAttachmentTable.jsp rendering <<<");

    String oid = request.getParameter("oid");

    boolean hasAttachment = false;
    boolean hasDistributable = false;

    if (oid != null) {
        try {
            Object refObj = new ReferenceFactory().getReference(oid).getObject();

            if (refObj instanceof ContentHolder) {

                ContentHolder holder = (ContentHolder) refObj;

                //  CHECK PRIMARY + SECONDARY
                ContentRoleType[] roles = { ContentRoleType.PRIMARY, ContentRoleType.SECONDARY };

                for (ContentRoleType role : roles) {

                    QueryResult qr = ContentHelper.service.getContentsByRole(holder, role);

                    while (qr.hasMoreElements()) {

                        ApplicationData appData = (ApplicationData) qr.nextElement();

                        //  JUST LIKE isDistributable → existence check
                        hasAttachment = true;

                        System.out.println("File: " + appData.getFileName() +
                                           " | Role: " + role +
                                           " | Distributable: " + appData.isDistributable());

                        //  EXISTING LOGIC
                        if (appData.isDistributable()) {
                            hasDistributable = true;
                        }
                    }
                }

            } else {
                System.out.println("Object is not a ContentHolder: " + refObj.getClass().getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    request.setAttribute("hasAttachment", hasAttachment);
    request.setAttribute("hasDistributable", hasDistributable);
%>

<!--  VALIDATION 1: NO ATTACHMENT -->
<c:if test="${!hasAttachment}">
    <div style="color:red; font-weight:bold; font-size:16px; margin-bottom:10px;">
        Warning: No attachment exists on this object.
    </div>
</c:if>

<!--  VALIDATION 2: NO DISTRIBUTABLE -->
<c:if test="${hasAttachment && !hasDistributable}">
    <div style="color:red; font-weight:bold; font-size:16px; margin-bottom:10px;">
        Warning: Externally distributable attachment not found.
    </div>
</c:if>

<!--  SUCCESS -->
<c:if test="${hasDistributable}">
    <div style="color:green; font-weight:bold; font-size:16px; margin-bottom:10px;">
        Externally distributable attachment is already selected.
    </div>
</c:if>

<!--  OOTB TABLE -->
<mvc:table compId="attachments.table.secondary" />

<script>

function validateAttachment() {

    var hasAttachment = "<c:out value='${hasAttachment}'/>";

    if (hasAttachment !== "true") {
        alert("At least one attachment is required.");
        return false;
    }

    return true;
}

function validateComments() {

           var elements = document.getElementsByTagName("textarea");
           var docomplete;
           for (var m = 0; m < elements.length; m++) {
                      if (elements[m].id == "workitem_comment") {
                                 docomplete = elements[m].value.length>0;
                                 break;
                      }
           }
           if (!docomplete) {
                      alert("Comments is required for submit.");
           } 
           return docomplete;

}

                // Called ONLY when wizard Complete/Finish button is clicked
               function validateOnComplete() {
                    var fields = getAllParticipantFields();
                    var missing = [];
                    fields.forEach(function(el) {
                        if (!el.value || el.value.trim() === "") {
                            missing.push(el.id);
                        }
                    });
                    if (missing.length > 0) {
                        alert("Please select participants for:\n\n" + missing.join("\n"));
                        return false;
                    }
                    return true;
                }
        

function validateAll() {

    if (!validateAttachment()) {
        return false;
    }

    if (!validateComments()) {
        return false;
    }
	
	 if (!validateOnComplete()) {
        return false;
    }

    return true;
}

</script>