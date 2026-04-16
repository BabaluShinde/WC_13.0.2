package ext.splm.attachments.handler;
import java.util.ArrayList;

import wt.util.WTException;

import com.ptc.netmarkets.model.NmObject;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.misc.NmModel;
import com.ptc.netmarkets.util.table.NmDefaultHTMLTable;
import com.ptc.netmarkets.util.treetable.NmDefaultHTMLTableTree;

import wt.content.ApplicationData;
import wt.fc.WTObject;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.fc.ReferenceFactory;

public class StrikeThroughRowHandler {

    public static ArrayList<NmOid> getNonSelectableRows(NmModel nmModel) throws WTException {
        ArrayList<NmOid> nmOidList = new ArrayList<>();
        WTPrincipal principal = SessionHelper.manager.getPrincipal();
        if (!(principal instanceof WTUser)) return nmOidList;

        WTUser currentUser = (WTUser) principal;
        ReferenceFactory rf = new ReferenceFactory();

        NmDefaultHTMLTableTree tableTree = (NmDefaultHTMLTableTree) nmModel;
        NmDefaultHTMLTable table = (NmDefaultHTMLTable) tableTree.getTable();

        for (int i = 0; i < table.getRowCount(); i++) {
            NmObject nmObj = (NmObject) table.getObject(i);
            if (nmObj == null) continue;

            NmOid nmOid = nmObj.getOid();
            if (nmOid == null) continue;

            try {
                // Resolve the NmOid to a WTObject safely
                WTObject wtObj = (WTObject) rf.getReference(nmOid.toString()).getObject();
                if (wtObj instanceof ApplicationData) {
                    ApplicationData appData = (ApplicationData) wtObj;
                    boolean isOwner = currentUser.getName().equals(appData.getCreatedBy().getName());
                    if (!isOwner) {
                        nmOidList.add(nmOid);
                        System.out.println("Row disabled for: " + appData.getFileName());
                    }
                }
            } catch (Exception e) {
                System.out.println("Skipping invalid NmOid: " + nmOid);
            }
        }
        return nmOidList;
    }
   
}
