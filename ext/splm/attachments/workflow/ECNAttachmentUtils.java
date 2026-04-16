package ext.splm.attachments.workflow;

import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.fc.QueryResult;
import wt.util.WTException;

public class ECNAttachmentUtils {

    public static boolean hasDistributableAttachment(WTChangeOrder2 ecn) throws WTException{
        if (ecn == null) return false;

        QueryResult qr = ContentHelper.service.getContentsByRole(ecn, ContentRoleType.SECONDARY, false);

        while (qr.hasMoreElements()) {
            ApplicationData appData = (ApplicationData) qr.nextElement();

            // Direct getter method
            if (appData.isDistributable()) {
                return true;
            }
        }

        throw new WTException("Please make at least one attachment distributable before completing the task.");
    }
}
