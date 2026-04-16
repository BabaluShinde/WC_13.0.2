package ext.amph.workflow;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfState;
import wt.vc.VersionControlHelper;
import wt.vc.Iterated;

public class RunningWFProcessesRMI implements RemoteAccess {

    // check running workflow
    public static boolean hasRunningWorkflow(WTDocument doc) throws WTException {
        if (doc == null) return false;

        QueryResult qr = WfEngineHelper.service.getAssociatedProcesses(doc, WfState.OPEN_RUNNING, null);

        return qr.hasMoreElements();
    }

    //  Get LATEST iteration using VersionControlHelper
    private static WTDocument getLatestDocument(String number) throws WTException {

        QuerySpec qs = new QuerySpec(WTDocument.class);

        qs.appendWhere(
                new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number),
                new int[]{0}
        );

        QueryResult qr = PersistenceHelper.manager.find(qs);

        if (qr.hasMoreElements()) {
            WTDocument doc = (WTDocument) qr.nextElement();
            Iterated latest = VersionControlHelper.service.getLatestIteration(doc, true);

            return (WTDocument) latest;
        }

        return null;
    }

    //  RMI method
    public static boolean checkRunningWorkflow(String number) throws WTException {
        WTDocument doc = getLatestDocument(number);
        return hasRunningWorkflow(doc);
    }

    //  Main method
    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Usage: windchill ext.amph.workflow.RunningWFProcessesRMI <DOC_NUMBER>");
            return;
        }

        RemoteMethodServer rms = RemoteMethodServer.getDefault();

        Boolean result = (Boolean) rms.invoke(
                "checkRunningWorkflow",
                "ext.amph.workflow.RunningWFProcessesRMI",
                null,
                new Class[]{String.class},
                new Object[]{args[0]}
        );

        System.out.println("Has Running Workflow: " + result);
    }
}