package ext.splm.utilities;

import wt.content.*;
import wt.epm.EPMDocument;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.method.RemoteAccess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class EPMTest implements RemoteAccess {

    // Hardcoded download directory
    private static final String DOWNLOAD_DIR = "C:\\temp\\EPM_Attachments";

    // Download secondary attachments for a given EPMDocument number
    public static void runExport(String drawingNumber, String fileTypeFilter) throws Exception {
        File dir = new File(DOWNLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();

        System.out.println("Processing drawing: " + drawingNumber);

        EPMDocument epmDoc = getEPMDocumentByNumber(drawingNumber);
        if (epmDoc == null) {
            System.out.println("Drawing not found: " + drawingNumber);
            return;
        }

        downloadSecondaryAttachments(epmDoc, DOWNLOAD_DIR, fileTypeFilter);
        System.out.println("Export completed.");
    }

    // Query EPMDocument by number (latest iteration only)
    private static EPMDocument getEPMDocumentByNumber(String number) throws Exception {
        QuerySpec qs = new QuerySpec(EPMDocument.class);
        qs.appendWhere(new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, SearchCondition.EQUAL, number));
        qs.appendAnd();
        qs.appendWhere(new SearchCondition(EPMDocument.class, "iterationInfo.latest", SearchCondition.IS_TRUE));

        QueryResult qr = PersistenceHelper.manager.find(qs);
        if (qr.hasMoreElements()) {
            return (EPMDocument) qr.nextElement();
        }
        return null;
    }

    // Download secondary attachments from the default representation
    private static void downloadSecondaryAttachments(EPMDocument epmDoc, String basePath, String fileTypeFilter) throws Exception {
        System.out.println("Fetching secondary attachments for: " + epmDoc.getNumber());

        // 1️⃣ Get default representation
        Representation defaultRep = RepresentationHelper.service.getDefaultRepresentation(epmDoc);
        if (defaultRep == null) {
            System.out.println("No default representation found for: " + epmDoc.getNumber());
            return;
        }

        // 2️⃣ Get ContentHolder for the representation
        ContentHolder holder = (ContentHolder) ContentHelper.service.getContents(defaultRep);

        // 3️⃣ Fetch secondary attachments only
        QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
        if (!qr.hasMoreElements()) {
            System.out.println("No secondary attachments found for: " + epmDoc.getNumber());
            return;
        }

        // 4️⃣ Iterate and export attachments
        List<ApplicationData> attachments = new ArrayList<>();
        while (qr.hasMoreElements()) {
            Object obj = qr.nextElement();
            if (obj instanceof ApplicationData) {
                ApplicationData data = (ApplicationData) obj;

                // Filter by file extension if provided
                if (fileTypeFilter == null || data.getFileName().endsWith(fileTypeFilter)) {
                    attachments.add(data);
                }
            }
        }

        // 5️⃣ Write files to disk
        for (ApplicationData data : attachments) {
            File outFile = new File(basePath, data.getFileName());
            try (InputStream in = ContentServerHelper.service.findContentStream(data);
                 FileOutputStream out = new FileOutputStream(outFile)) {

                byte[] buffer = new byte[4096];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                System.out.println("Exported: " + outFile.getAbsolutePath());
            }
        }
    }
}
