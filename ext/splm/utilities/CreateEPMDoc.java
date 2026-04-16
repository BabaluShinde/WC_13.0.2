package ext.splm.utilities;

import java.io.File;

import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.epm.EPMApplicationType;
import wt.epm.EPMAuthoringAppType;
import wt.epm.EPMContextHelper;
import wt.epm.EPMDocSubType;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentType;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pdmlink.PDMLinkProduct;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

public class CreateEPMDoc implements RemoteAccess {

    /* =====================================================
     * Shell Entry Point
     * ===================================================== */
    public static void main(String[] args) throws Exception {

        if (args.length < 4) {
            System.out.println(
                "Usage:\n" +
                "windchill ext.splm.utilities.CreateEPM " +
                "<number> <name> <cadFilePath> <productName>"
            );
            return;
        }

        RemoteMethodServer rms = RemoteMethodServer.getDefault();

        rms.invoke(
                "createEPM",
                CreateEPMDoc.class.getName(),
                null,
                new Class[] {
                        String.class,
                        String.class,
                        String.class,
                        String.class
                },
                args
        );
    }

    /* =====================================================
     * MethodServer logic
     * ===================================================== */
    public static void createEPM(
            String number,
            String name,
            String cadFilePath,
            String productName
    ) throws Exception {

        /* 1️⃣ Set EPM Context */
        EPMContextHelper.setApplication(
                EPMApplicationType.toEPMApplicationType("EPM")
        );

        /* 2️⃣ Find Product */
        PDMLinkProduct product = findProduct(productName);
        WTContainerRef containerRef = WTContainerRef.newWTContainerRef(product);

        /* 3️⃣ Resolve Folder INSIDE Product */
        Folder folder = FolderHelper.service.getFolder(
                "/Default",
                containerRef
        );

        /* 4️⃣ Authoring + Doc Types (from your example) */
        EPMAuthoringAppType authoringApp =
                EPMAuthoringAppType.toEPMAuthoringAppType("PROE");

        EPMDocumentType docType =
                EPMDocumentType.toEPMDocumentType("CADCOMPONENT");

        String cadName = new File(cadFilePath).getName();

        /* 5️⃣ Create EPMDocument */
        EPMDocument epm = EPMDocument.newEPMDocument(
                number,
                name,
                authoringApp,
                docType,
                cadName
        );

        epm.setDocSubType(EPMDocSubType.getEPMDocSubTypeDefault());
        epm.setContainer(product);
        epm.setPlaceHolder(false);

        FolderHelper.assignLocation(epm, folder);

        epm = (EPMDocument) PersistenceHelper.manager.store(epm);

        /* 6️⃣ Upload PRIMARY Content */
        ApplicationData data =
                ApplicationData.newApplicationData(epm);

        data.setRole(ContentRoleType.PRIMARY);
        data.setFileName(cadName);

        ContentServerHelper.service.updateContent(
                epm,
                data,
                cadFilePath
        );

        System.out.println("SUCCESS: EPM created -> " + epm.getNumber());
    }

    /* =====================================================
     * Helper: Find Product
     * ===================================================== */
    private static PDMLinkProduct findProduct(String name)
            throws WTException {

        QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
        qs.appendWhere(
                new SearchCondition(
                        PDMLinkProduct.class,
                        PDMLinkProduct.NAME,
                        SearchCondition.EQUAL,
                        name
                ),
                new int[] { 0 }
        );

        QueryResult qr = PersistenceHelper.manager.find(qs);

        if (!qr.hasMoreElements()) {
            throw new WTException("Product not found: " + name);
        }

        return (PDMLinkProduct) qr.nextElement();
    }
}
