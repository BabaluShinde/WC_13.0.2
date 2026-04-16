package ext.asi.workflow;

import java.util.Iterator;
import java.util.Date;

import wt.fc.ObjectReference;
import wt.fc.WTObject;
import wt.fc.collections.WTCollection;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.HTMLEncoder;
import wt.util.WTException;
import wt.workflow.definer.UserEventVector;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfVotingEventAudit;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class WorkflowCommentUtil {

    private static ReferenceFactory rf = new ReferenceFactory();

    public static String buildInstructions(ObjectReference selfRef, WTObject obj, String eventName) throws WTException {
        WTReference objRef = rf.getReference(obj);
        StringBuilder result = new StringBuilder(4000);

        // Get current process from self reference
        WfProcess wfProcess = (WfProcess) selfRef.getObject();

        // Get current user as reference
        WTUser currentUser = (WTUser) SessionHelper.manager.getPrincipal();
        WTPrincipalReference userRef = WTPrincipalReference.newWTPrincipalReference(currentUser);

        // Use WfEngineHelper to get all voting events for the object in the workflow
        WTCollection votingEvents = WfEngineHelper.service.getVotingEvents(wfProcess, null, null, objRef);

        Iterator<?> it = votingEvents.iterator();
        while (it.hasNext()) {
            ObjectReference ref = (ObjectReference) it.next();
            WfVotingEventAudit audit = (WfVotingEventAudit) ref.getObject();

            UserEventVector userEvents = audit.getEventList();
            if (userEvents.contains(eventName)) {
                String comment = audit.getUserComment();

                // Get timestamp
                Date ts = audit.getModifyTimestamp();
                SimpleDateFormat estFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                estFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                String estTime = estFormat.format(ts);
                
                if (comment != null && !comment.trim().isEmpty()) {
                    result.append("[")
                          .append(audit.getUserRef().getFullName())
                          .append(" - ")
                          .append(estTime)   
                          .append("]: ")
                          .append(comment)
                          .append("\r\n");
                }
            }
        }

        // Truncate if too long (Windchill often limits instructions to 4000 chars)
        String finalStr = result.toString();
        if (finalStr.length() > 4000) {
            finalStr = finalStr.substring(0, 4000);
        }

        // HTML encode
        return HTMLEncoder.encodeForHTMLContent(finalStr);
    }
}
