package ext.cwg.load;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.container.WTContainerHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pom.Transaction;
import wt.series.MultilevelSeries;
import wt.series.Series;
import wt.series.SeriesException;
import wt.type.TypedUtilityServiceHelper;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.IterationIdentifier;
import wt.vc.VersionControlHelper;
import wt.vc.VersionIdentifier;
import wt.content.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Properties;

import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.impl.WCTypeIdentifier;

public class LoaderUtility implements RemoteAccess {

	public static void main(String[] args) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");

		try {

			System.out.println("reading from file :" + args[0]);
			rms.invoke("remoteMethod", "ext.cwg.load.LoaderUtility", null, new Class[] { String.class },
					new Object[] { args[0] });
			System.out.println("check the log file");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void remoteMethod(String loaderPropPath) throws WTException {
		System.out.println("Remote method started...");
		String excelPath = null;
		String contentBaseDir = null;
		// Load properties file
		Properties prop = new Properties();
		FileInputStream fis = null;
		try {
			System.out.println("prop files loc :" + loaderPropPath);
			fis = new FileInputStream(loaderPropPath);
			prop.load(fis);

			excelPath = prop.getProperty("excelPath");
			contentBaseDir = prop.getProperty("content.baseDir");

			System.out.println("excelPath: " + excelPath);

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ignore) {
				}
			}
		}

		LogWriter_Loader.getFilesLoc(loaderPropPath);

		// Counter to track processed rows (for limiting to 5 as per original logic)
		// Using an array or AtomicInteger to be effectively final for use in lambda
		final String baseDirForEnrich = contentBaseDir;
		final int[] count = { 0 };

		ExcelFileReader.processExcelRows(excelPath, row -> {
			if (count[0] >= 5) {
				// Return here acts like 'continue' in a loop, but we can't easily break the
				// outer loop
				// in the current simple callback structure without throwing an exception or
				// checking a flag.
				// For now, we'll just stop doing work in the callback if limit is reached.
				return;
			}
			count[0]++; // Increment count

			enrichRow(row, baseDirForEnrich);
			try {
				String number = row.get("number");
				WTDocument doc = getDocumentByNumber(number);

				if (doc != null) {
					// Document already exists — skip creation
					System.out.println("Document " + number + " already exists. Skipping creation.");
					LogWriter_Loader.logTaskStatus(row, "Document Created", "Skipped");
				} else {
					// New document — create it
					doc = createDocument(row);
				}

				// Upload content and set lifecycle for both new and existing docs
				doc = uploadPrimaryContent(doc, row);
				doc = uploadAttachments(doc, row);
				doc = setLifecycleState(doc, row);

				LogWriter_Loader.logTaskStatus(row, "Overall Status", true);
			} catch (Exception e) {
				LogWriter_Loader.logTaskStatus(row, "Overall Status", false);
				LogWriter_Loader.logError(row, "Overall Document Processing", e);
			}
		});

		LogWriter_Loader.finalizeAndWrite();
	}

	public static WTDocument createDocument(Map<String, String> row)
			throws WTException, WTPropertyVetoException, RemoteException {

		Transaction tx = new Transaction();
		WTDocument doc = null;

		try {
			tx.start();

			String name = row.get("name");
			String number = row.get("number");
			// String description = row.get("Description");
			String folderPath = row.get("folder_path");
			String containerPath = row.get("container_path");
			String lifeCycleName = row.get("lifeCycleName");
			String docType = row.get("Document_Type");

			TypeIdentifier typeId = TypeHelper.getTypeIdentifier(docType);
			WCTypeIdentifier wcTypeId = (WCTypeIdentifier) typeId;
			TypeDefinitionReference typeRef = TypedUtilityServiceHelper.service
					.getTypeDefinitionReference(wcTypeId.getTypename());

			WTContainerRef containerRef = WTContainerHelper.service.getByPath(containerPath);
			Folder folder = FolderHelper.service.getFolder(folderPath, containerRef);

			doc = WTDocument.newWTDocument();
			doc.setName(name);
			doc.setNumber(number);
			// doc.setDescription(description);
			doc.setContainerReference(containerRef);
			doc.setTypeDefinitionReference(typeRef);
			FolderHelper.assignLocation((FolderEntry) doc, folder);

			LifeCycleTemplate lct = LifeCycleHelper.service.getLifeCycleTemplate(lifeCycleName, containerRef);
			if (lct != null) {
				doc = (WTDocument) LifeCycleHelper.setLifeCycle(doc, lct);
			}

			doc = setVersion(doc, row);
			doc = (WTDocument) PersistenceHelper.manager.store(doc);

			tx.commit();
			LogWriter_Loader.logTaskStatus(row, "Document Created", true);
		} catch (Exception e) {
			try {
				tx.rollback();
			} catch (Exception ignore) {
			}
			LogWriter_Loader.logTaskStatus(row, "Document Created", false);
			LogWriter_Loader.logError(row, "Document Created", e);
			throw new WTException(e);
		}

		return doc;
	}

	public static WTDocument uploadPrimaryContent(WTDocument doc, Map<String, String> row) {
		Transaction tx = new Transaction();
		try {
			tx.start();

			String filePath = row.get("primaryloc") + File.separator + row.get("primaryname");
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);

			// Check if primary content already exists
			QueryResult existingContent = ContentHelper.service.getContentsByRole(doc, ContentRoleType.PRIMARY);
			ApplicationData content;

			if (existingContent.hasMoreElements()) {
				// Replace existing primary content
				content = (ApplicationData) existingContent.nextElement();
				content.setFileName(file.getName());
				content.setFileSize(file.length());
			} else {
				// Create new primary content
				content = ApplicationData.newApplicationData(doc);
				content.setRole(ContentRoleType.PRIMARY);
				content.setFileName(file.getName());
				content.setFileSize(file.length());
			}

			ContentServerHelper.service.updateContent(doc, content, fis);
			ContentServerHelper.service.updateHolderFormat(doc);

			tx.commit();
			LogWriter_Loader.logTaskStatus(row, "Primary Content Status", true);
		} catch (Exception e) {
			try {
				tx.rollback();
			} catch (Exception ignore) {
			}
			LogWriter_Loader.logTaskStatus(row, "Primary Content Status", false);
			LogWriter_Loader.logError(row, "Primary Content Upload", e);
		}
		return doc;
	}

	public static WTDocument uploadAttachments(WTDocument doc, Map<String, String> row) {

		// Clear existing secondary content first
		try {
			QueryResult existingAttachments = ContentHelper.service.getContentsByRole(doc, ContentRoleType.SECONDARY);
			while (existingAttachments.hasMoreElements()) {
				ApplicationData existingAtt = (ApplicationData) existingAttachments.nextElement();
				ContentServerHelper.service.deleteContent(doc, existingAtt);
			}
		} catch (Exception e) {
			LogWriter_Loader.logTaskStatus(row, "Attachment Status", false);
			LogWriter_Loader.logError(row, "Clearing Existing Attachments", e);
			return doc;
		}

		// Upload new attachments from Excel
		for (Map.Entry<String, String> entry : row.entrySet()) {
			if (entry.getKey().toLowerCase().startsWith("attachmentloc") && entry.getValue() != null
					&& !entry.getValue().isEmpty()) {
				String attLoc = entry.getValue();
				String suffix = entry.getKey().substring("attachmentloc".length());
				String attName = row.get("attachmentname" + suffix);
				String fileLoc = attLoc + File.separator + attName;

				Transaction tx = new Transaction();
				try {
					tx.start();

					File file = new File(fileLoc);
					FileInputStream fis = new FileInputStream(file);
					ApplicationData content = ApplicationData.newApplicationData(doc);
					content.setRole(ContentRoleType.SECONDARY);
					content.setFileName(file.getName());
					content.setFileSize(file.length());

					ContentServerHelper.service.updateContent(doc, content, fis);

					tx.commit();
				} catch (Exception e) {
					try {
						tx.rollback();
					} catch (Exception ignore) {
					}
					LogWriter_Loader.logTaskStatus(row, "Attachment Status", false);
					LogWriter_Loader.logError(row, "Attachment Upload: " + attName, e);
					return doc;
				}
			}
		}

		LogWriter_Loader.logTaskStatus(row, "Attachment Status", true);
		return doc;
	}

	public static WTDocument setLifecycleState(WTDocument doc, Map<String, String> row) {
		Transaction tx = new Transaction();
		try {
			tx.start();

			String state = row.get("lifecycleState");
			State lifecycleState = State.toState(state.toUpperCase());
			doc = (WTDocument) LifeCycleHelper.service.setLifeCycleState(doc, lifecycleState);

			tx.commit();
			LogWriter_Loader.logTaskStatus(row, "Lifecycle Status", true);
		} catch (Exception e) {
			try {
				tx.rollback();
			} catch (Exception ignore) {
			}
			LogWriter_Loader.logTaskStatus(row, "Lifecycle Status", false);
			LogWriter_Loader.logError(row, "Lifecycle State", e);
		}
		return doc;
	}

	public static WTDocument setVersion(WTDocument doc, Map<String, String> row)
			throws WTPropertyVetoException, SeriesException, WTException {
		try {
			String revision = row.get("revision");
			VersionIdentifier vc = VersionIdentifier
					.newVersionIdentifier(MultilevelSeries.newMultilevelSeries("wt.series.HarvardSeries", revision));
			doc.getMaster().setSeries("wt.series.HarvardSeries");
			VersionControlHelper.setVersionIdentifier(doc, vc);
		} catch (Exception e) {
			LogWriter_Loader.logTaskStatus(row, "Version (Revision)", false);
			LogWriter_Loader.logError(row, "Version (Revision)", e);
		}

		try {
			String iterationStr = row.get("iteration");
			int iterationInt = Integer.parseInt(iterationStr.trim());
			Series ser = Series.newSeries("wt.vc.IterationIdentifier", String.valueOf(iterationInt));
			IterationIdentifier iid = IterationIdentifier.newIterationIdentifier(ser);
			VersionControlHelper.setIterationIdentifier(doc, iid);
		} catch (Exception e) {
			LogWriter_Loader.logTaskStatus(row, "Iteration", false);
			LogWriter_Loader.logError(row, "Iteration", e);
		}

		return doc;
	}

	public static WTDocument getDocumentByNumber(String number) throws WTException {
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number),
				new int[] { 0 });

		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			return (WTDocument) qr.nextElement();
		}
		return null;
	}

	public static void enrichRow(Map<String, String> row, String baseDirArg) {
		// Constants (derived from sharedStrings.xml)
		String CONST_CONTAINER_PATH = "/wt.inf.container.OrgContainer=Masco Cabinetry/wt.pdmlink.PDMLinkProduct=Non-Configured";
		String CONST_FOLDER_PATH = "/Default/PURCHASED/V23 CORRUGATED PACKAGING";
		String CONST_LIFECYCLE_NAME = "Masco CAD Life Cycle";
		String CONST_DOC_TYPE = "wt.doc.WTDocument|COM.MASCOCABINETRY.ArtiosCAD_Document";
		String CONST_LIFECYCLE_STATE = "Released";

		// Default base directories (from properties or hardcoded fallback)
		String baseDir = baseDirArg;
		if (baseDir == null || baseDir.isEmpty()) {
			throw new RuntimeException("Property 'content.baseDir' is missing in loader.properties!");
		}

		String defaultArdDir = baseDir + File.separator + "ARD";
		String defaultPdfDir = baseDir + File.separator + "PDF";
		String defaultZipDir = baseDir + File.separator + "ZIP";

		// Map "Artios CAD Documents" to "name" and "number" if missing
		if ((!row.containsKey("name") || row.get("name").trim().isEmpty()) && row.containsKey("Artios CAD Documents")) {
			String val = row.get("Artios CAD Documents");
			if (val != null && !val.trim().isEmpty()) {
				row.put("name", val);
				row.put("number", val);
			}
		}

		if (!row.containsKey("name") || row.get("name") == null || row.get("name").trim().isEmpty()) {
			row.put("name", "MISSING_NAME_" + System.currentTimeMillis());
		}
		if (!row.containsKey("number") || row.get("number") == null || row.get("number").trim().isEmpty()) {
			row.put("number", "MISSING_NUMBER_" + System.currentTimeMillis());
		}

		// Set constants (Only if missing in Excel)
		if (!row.containsKey("container_path") || row.get("container_path").isEmpty())
			row.put("container_path", CONST_CONTAINER_PATH);
		if (!row.containsKey("folder_path") || row.get("folder_path").isEmpty())
			row.put("folder_path", CONST_FOLDER_PATH);
		if (!row.containsKey("lifeCycleName") || row.get("lifeCycleName").isEmpty())
			row.put("lifeCycleName", CONST_LIFECYCLE_NAME);
		if (!row.containsKey("Document_Type") || row.get("Document_Type").isEmpty())
			row.put("Document_Type", CONST_DOC_TYPE);
		if (!row.containsKey("lifecycleState") || row.get("lifecycleState").isEmpty())
			row.put("lifecycleState", CONST_LIFECYCLE_STATE);

		if (!row.containsKey("revision") || row.get("revision").isEmpty()) {
			row.put("revision", "A");
		}
		if (!row.containsKey("iteration") || row.get("iteration").isEmpty()) {
			row.put("iteration", "1");
		}

		// Primary Content Logic (.ARD file)
		// Expect 'primaryloc' and 'primaryname' from Excel.
		// Fallback to default only if missing.
		if (!row.containsKey("primaryloc") || row.get("primaryloc").isEmpty()) {
			row.put("primaryloc", defaultArdDir);
		}
		if (!row.containsKey("primaryname") || row.get("primaryname").isEmpty()) {
			String name = row.get("name");
			if (name != null) {
				row.put("primaryname", name + ".ARD");
			}
		}

		// Attachments Logic
		// The code currently maps from "source" column names ("Document Attachment1 -
		// PDF")
		// to "target" keys ("attachmentname1").
		// We should respect if target keys already exist.

		// Attachment 1 (PDF)
		if (!row.containsKey("attachmentloc1") || row.get("attachmentloc1").isEmpty()) {
			// Try to map from source column if not already mapped
			if (row.containsKey("Document Attachment1 - PDF")) {
				String val = row.get("Document Attachment1 - PDF");
				if (val != null && !val.isEmpty()) {
					row.put("attachmentname1", val);
					row.put("attachmentloc1", defaultPdfDir); // Uses default if not explicit
				}
			}
		}

		// Attachment 2 (ZIP1)
		if (!row.containsKey("attachmentloc2") || row.get("attachmentloc2").isEmpty()) {
			if (row.containsKey("Document Attachment2 - ZIP1")) {
				String val = row.get("Document Attachment2 - ZIP1");
				if (val != null && !val.isEmpty()) {
					row.put("attachmentname2", val);
					row.put("attachmentloc2", defaultZipDir);
				}
			}
		}

		// Attachment 3 (ZIP2)
		if (!row.containsKey("attachmentloc3") || row.get("attachmentloc3").isEmpty()) {
			if (row.containsKey("Document Attachment3 - ZIP2")) {
				String val = row.get("Document Attachment3 - ZIP2");
				if (val != null && !val.isEmpty()) {
					row.put("attachmentname3", val);
					row.put("attachmentloc3", defaultZipDir);
				}
			}
		}
	}
}
