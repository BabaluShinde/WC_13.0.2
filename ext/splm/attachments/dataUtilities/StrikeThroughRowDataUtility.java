package ext.splm.attachments.dataUtilities;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.AbstractNonSelectableRowDataUtility;

import wt.content.ApplicationData;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StrikeThroughRowDataUtility extends AbstractNonSelectableRowDataUtility {

    @Override
    public boolean isNonSelectableRow(String component_id, Object datum, ModelContext mc) throws WTException {
        try {
            WTPrincipal principal = SessionHelper.manager.getPrincipal();

            if (principal instanceof WTUser && datum instanceof ApplicationData) {
                WTUser currentUser = (WTUser) principal;
                ApplicationData appData = (ApplicationData) datum;

                String ownerName = (appData.getCreatedBy() != null) ? appData.getCreatedBy().getName() : "";
                boolean isOwner = currentUser.getName().equals(ownerName);

                System.out.println("Checking file: " + appData.getFileName()
                        + " | Owner: " + ownerName
                        + " | CurrentUser: " + currentUser.getName()
                        + " | isOwner? " + isOwner);

                return !isOwner;
            }
        } catch (Exception e) {
            throw new WTException(e);
        }
        return false;
    }
}
