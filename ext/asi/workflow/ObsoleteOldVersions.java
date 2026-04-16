package ext.asi.workflow;

import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
	public class ObsoleteOldVersions {

	public static void markOlderVersionsObsolete(WTDocument latestDoc) throws WTException {

	    QueryResult qr = VersionControlHelper.service.allIterationsOf(latestDoc.getMaster());
	    while (qr.hasMoreElements()) {
	        Object obj = qr.nextElement();

	        if (obj instanceof WTDocument) {
	            WTDocument oldDoc = (WTDocument) obj;

	            // Skip latest
	            if (oldDoc.getPersistInfo().getObjectIdentifier().equals(latestDoc.getPersistInfo().getObjectIdentifier())) {
	                continue;
	            }

	            // Skip already approved
	            if (oldDoc.getLifeCycleState().toString().equalsIgnoreCase("APPROVED")) {
	                continue;
	            }

	            // Set Obsolete state WITHOUT check-out
	            LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) oldDoc, State.toState("OBSOLETE"));
	            System.out.println("Marked version " + oldDoc.getVersionIdentifier().getValue() + " as OBSOLETE");
	        }
	    }
	}
}