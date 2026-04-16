package ext.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentServerHelper;
import wt.epm.EPMAuthoringAppType;
import wt.epm.EPMDocSubType;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentType;
import wt.epm.structure.EPMMemberLink;
import wt.epm.structure.EPMStructureHelper;
import wt.epm.workspaces.EPMAsStoredConfigSpec;
import wt.fc.PersistenceHelper;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.wip.CheckoutInfo;

public class EPMPrimaryContentFetcherRMI implements RemoteAccess {

	public static void downloadAssembly(String asmNumber, String outputPath) throws Exception {
		if (!RemoteMethodServer.ServerFlag) {
			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			rms.invoke("downloadAssembly", EPMPrimaryContentFetcherRMI.class.getName(), null,
					new Class[] { String.class, String.class }, new Object[] { asmNumber, outputPath });
			return;
		}

		System.out.println("Inside MethodServer - Starting structure download for: " + asmNumber);

		EPMDocument asmDoc = findLatestEPM(asmNumber);
		if (asmDoc == null) {
			System.out.println("Assembly not found: " + asmNumber);
			return;
		}

		File dir = new File(outputPath);
		if (!dir.exists())
			dir.mkdirs();

		Set<String> visited = new HashSet<>();
		traverseAndDownload(asmDoc, dir, visited);
	}

	private static void traverseAndDownload(EPMDocument parent, File outputDir, Set<String> visited) throws Exception {
		if (parent == null || visited.contains(parent.getNumber()))
			return;
		visited.add(parent.getNumber());

		downloadPrimaryContent(parent, outputDir);

		EPMAsStoredConfigSpec configSpec = EPMAsStoredConfigSpec.newEPMAsStoredConfigSpec(parent);
		QueryResult structure = EPMStructureHelper.service.navigateUsesToIteration(parent, null, false, configSpec);

		while (structure.hasMoreElements()) {
			Object[] linkObj = (Object[]) structure.nextElement();
			if (linkObj.length < 2)
				continue;

			EPMMemberLink link = (EPMMemberLink) linkObj[0];
			Persistable childObj = (Persistable) linkObj[1];

			if (childObj instanceof EPMDocument) {
				EPMDocument child = (EPMDocument) childObj;
				System.out.println("   ↳ Found child: " + child.getNumber() + " (" + child.getDocType() + ")");
				traverseAndDownload(child, outputDir, visited);
			}
		}
	}

	private static void downloadPrimaryContent(EPMDocument epm, File outputDir) throws Exception {
		epm = (EPMDocument) ContentHelper.service.getContents(epm);
		ContentItem item = ContentHelper.getPrimary(epm);

		if (!(item instanceof ApplicationData)) {
			System.out.println("No primary content found for: " + epm.getNumber());
			return;
		}

		ApplicationData appData = (ApplicationData) item;
		InputStream in = ContentServerHelper.service.findContentStream(appData);

		String fileName = appData.getFileName();

		if (fileName == null || fileName.contains("{$CAD_NAME}")) {
			fileName = epm.getCADName();
		
		}

		File outFile = new File(outputDir, fileName);
		try (FileOutputStream out = new FileOutputStream(outFile)) {
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		}
		in.close();

		System.out.println("Downloaded: " + outFile.getAbsolutePath());
	}

	private static EPMDocument findLatestEPM(String number) throws WTException {
		QuerySpec qs = new QuerySpec(EPMDocument.class);
		qs.appendSearchCondition(
				new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, SearchCondition.EQUAL, number, false));

		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (!qr.hasMoreElements())
			return null;
		EPMDocument doc = (EPMDocument) qr.nextElement();
		EPMDocumentMaster master = (EPMDocumentMaster) doc.getMaster();

		QueryResult versions = VersionControlHelper.service.allVersionsOf(master);
		EPMDocument latestDoc = null;
		while (versions.hasMoreElements()) {
			EPMDocument version = (EPMDocument) versions.nextElement();
			latestDoc = (EPMDocument) VersionControlHelper.service.getLatestIteration(version, true);
		}
		return latestDoc;
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("Usage: windchill ext.util.EPMPrimaryContentFetcher <ASM_NUMBER>");
			System.out.println("Example: windchill ext.util.EPMPrimaryContentFetcher 01-51283.ASM");
			return;
		}

		String epmNumber = args[0];
		String saveDir = "C:\\Temp\\EPMDownloads";

		System.out.println("Input Assembly Number: " + epmNumber);
		System.out.println("Output Directory: " + saveDir);

		downloadAssembly(epmNumber, saveDir);
	}
}
