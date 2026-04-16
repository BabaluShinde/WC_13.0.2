package ext.splm.extraction;

import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentType;
import wt.epm.EPMFamily;
import wt.epm.familytable.EPMSepFamilyTableMaster;
import wt.epm.structure.EPMContainedIn;
import wt.epm.structure.EPMMemberLink;
import wt.epm.structure.EPMStructureHelper;
import wt.epm.util.EPMContentHelper;
import wt.epm.workspaces.EPMAsStoredConfigSpec;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.IBAValueUtility;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.service.IBAValueHelper;
import wt.inf.library.WTLibrary;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleManaged;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.ConstantExpression;
import wt.query.QuerySpec;
import wt.query.RelationalExpression;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;

import com.ptc.wvs.server.util.PublishUtils;

@SuppressWarnings("unchecked")
public class dataUtil {
	private static DateFormat dateFormat = new SimpleDateFormat(
			"dd-MMM-yyyy hh:mm");// yyyy_MM_dd_HHmmss
	private static Calendar cal = Calendar.getInstance();
	public static final String USER_NAME = "wcadmin";
	public static final String USER_PWD = "wcadmin";
	static StringBuilder stringbuilder;
	static String[] tmpStg, tmpStgCur, tmpStgCur2, strAsmDetails;
	static String OT, QueStg, StStg, epmNo, epmNmIn, epmNoIn, epmNoLg, epmNoSh,
			parStg, fullStg, fullStgSuppressed, childStg;
	static String oid, oidEpm, linkEpmStg, oidCh, oidPar, verStg, iterStg,
			typStg;
	static long OBID;
	static boolean isGen, isInst, isQty;
	static int ftStat, z, iDebug = 0;
	static EPMDocument epm, epm1, epmChild, epmChildp1, epmTop;
	static LifeCycleManaged LMObject;
	static QueryResult qrUses, qrChildChecker, qr;
	static ArrayList arraylistTopEPM;

	static ArrayList arraylistTopEPMLink;
	static ConfigSpec configSpec;

	// Enhanced Configuration Types for Relationship Analysis
	public enum ConfigSpecType {
		AS_STORED,    // Use as-stored relationships (preserves original design intent)
		LATEST,       // Always use latest child versions
		SPECIFIC,     // Use specific configuration spec
		ALL_VERSIONS  // Show relationships for all child versions
	}

	// Data structure to hold relationship metadata
	public static class AsStoredRelationshipMetadata {
		public String parentNumber;
		public String parentVersion;
		public String parentIteration;
		public String parentState;
		public String childNumber;
		public String childAsStoredVersion;
		public String childAsStoredIteration;
		public String childAsStoredState;
		public String childLatestVersion;
		public String childLatestIteration;
		public String childLatestState;
		public boolean isChildOutdated;
		public String relationshipType;
		public int quantityUsed;
		public String dependencyInfo;
		
		@Override
		public String toString() {
			return parentNumber + "|" + parentVersion + "." + parentIteration + 
				   "|" + childNumber + "|AS_STORED:" + childAsStoredVersion + "." + childAsStoredIteration +
				   "|LATEST:" + childLatestVersion + "." + childLatestIteration + 
				   "|OUTDATED:" + isChildOutdated + "|QTY:" + quantityUsed + 
				   "|TYPE:" + relationshipType;
		}
	}

	/**
	 * Enhanced method to get CAD Model Details with As Stored relationship metadata
	 * Shows both as-stored relationships and latest available versions for comparison
	 */
	public static String getCADModelDetailsWithAsStoredMetadata(EPMDocument epmDoc,
			boolean isVersionPlus) throws WTException {
		String lvResult = new String();
		ArrayList<AsStoredRelationshipMetadata> relationshipMetadata = new ArrayList<>();
		
		try {
			String level0 = new String();
			arraylistTopEPM = new ArrayList();
			arraylistTopEPMLink = new ArrayList();

			LMObject = (LifeCycleManaged) epmDoc;
			StStg = (String) LMObject.getLifeCycleState().toString();
			OBID = epmDoc.getPersistInfo().getObjectIdentifier().getId();
			verStg = epmDoc.getVersionIdentifier().getValue();
			iterStg = epmDoc.getIterationIdentifier().getValue();

			isGen = epmDoc.isGeneric();
			isInst = epmDoc.isInstance();
			ftStat = epmDoc.getFamilyTableStatus();

			if (isVersionPlus)
				verStg = getTCVersion(epmDoc.getVersionDisplayIdentifier().toString());

			level0 = "0|" + epmDoc.getNumber() + "|" + verStg + "|1|0|0|_|M2_MM_ITEM_MREVA";
			parStg = "parent\t" + OBID + "\t" + epmDoc.getNumber() + "\t"
					+ epmDoc.getCADName() + "\t" + StStg + "\t" + verStg + "."
					+ iterStg + "\t" + isGen + "\t" + isInst + "\t" + ftStat;

			// Use AS STORED configuration to get relationships as they were stored
			EPMAsStoredConfigSpec asStoredConfigSpec = EPMAsStoredConfigSpec
					.newEPMAsStoredConfigSpec(epmDoc);
			
			// Get children using as-stored configuration
			qrChildChecker = EPMStructureHelper.service
					.navigateUsesToIteration(epmDoc, null, false, asStoredConfigSpec);

			if (qrChildChecker.size() > 0) {
				// Process each child relationship
				for (int x = 0; x < qrChildChecker.size(); x++) {
					Persistable apersistable1[] = (Persistable[]) qrChildChecker.nextElement();
					if (apersistable1[1] instanceof EPMDocument) {
						EPMMemberLink bomUsage = (EPMMemberLink) apersistable1[0];
						EPMDocument asStoredChild = (EPMDocument) apersistable1[1];

						arraylistTopEPM.add(asStoredChild);
						arraylistTopEPMLink.add(bomUsage);
						
						// Create relationship metadata comparing as-stored vs latest
						AsStoredRelationshipMetadata metadata = createRelationshipMetadata(
								epmDoc, asStoredChild, bomUsage);
						relationshipMetadata.add(metadata);
					}
				}

				// Generate traditional BOM output
				lvResult = generateTraditionalBOMOutput(epmDoc, level0, arraylistTopEPM, arraylistTopEPMLink);
				
				// Add relationship metadata section
				lvResult += generateAsStoredRelationshipReport(relationshipMetadata);

			} else {
				lvResult = level0 + "\n# No children found for " + epmDoc.getNumber();
			}
			
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return lvResult;
	}

	/**
	 * Create metadata comparing as-stored child vs latest available version
	 */
	private static AsStoredRelationshipMetadata createRelationshipMetadata(
			EPMDocument parent, EPMDocument asStoredChild, EPMMemberLink bomUsage) {
		
		AsStoredRelationshipMetadata metadata = new AsStoredRelationshipMetadata();
		
		try {
			// Parent information
			metadata.parentNumber = parent.getNumber();
			metadata.parentVersion = parent.getVersionIdentifier().getValue();
			metadata.parentIteration = parent.getIterationIdentifier().getValue();
			metadata.parentState = parent.getState().getState().getDisplay();
			
			// As-stored child information
			metadata.childNumber = asStoredChild.getNumber();
			metadata.childAsStoredVersion = asStoredChild.getVersionIdentifier().getValue();
			metadata.childAsStoredIteration = asStoredChild.getIterationIdentifier().getValue();
			metadata.childAsStoredState = asStoredChild.getState().getState().getDisplay();
			
			// Find latest version of the child
			EPMDocument latestChild = getLatestEPMDoc(asStoredChild.getNumber());
			if (latestChild != null) {
				metadata.childLatestVersion = latestChild.getVersionIdentifier().getValue();
				metadata.childLatestIteration = latestChild.getIterationIdentifier().getValue();
				metadata.childLatestState = latestChild.getState().getState().getDisplay();
				
				// Check if as-stored child is outdated
				metadata.isChildOutdated = !isLatestVersion(asStoredChild, latestChild);
			} else {
				metadata.childLatestVersion = metadata.childAsStoredVersion;
				metadata.childLatestIteration = metadata.childAsStoredIteration;
				metadata.childLatestState = metadata.childAsStoredState;
				metadata.isChildOutdated = false;
			}
			
			// BOM Usage information
			if (bomUsage != null) {
				metadata.quantityUsed = 1; // Default quantity
				metadata.relationshipType = "ASSEMBLY";
				
				// Build dependency information
				StringBuilder depInfo = new StringBuilder();
				depInfo.append(bomUsage.isRequired() ? "Required" : "Optional");
				if (bomUsage.isSuppressed()) depInfo.append(",Suppressed");
				if (bomUsage.isSubstitute()) depInfo.append(",Substitute");
				if (bomUsage.isPlaced()) depInfo.append(",Placed");
				if (bomUsage.isAnnotated()) depInfo.append(",Annotated");
				
				metadata.dependencyInfo = depInfo.toString();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return metadata;
	}

	/**
	 * Check if the as-stored child is the latest version
	 */
	private static boolean isLatestVersion(EPMDocument asStoredChild, EPMDocument latestChild) {
		try {
			return asStoredChild.getVersionIdentifier().getValue().equals(latestChild.getVersionIdentifier().getValue()) &&
				   asStoredChild.getIterationIdentifier().getValue() == latestChild.getIterationIdentifier().getValue();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Generate traditional BOM output (your existing logic)
	 */
	private static String generateTraditionalBOMOutput(EPMDocument epmDoc, String level0, 
			ArrayList arraylistTopEPM, ArrayList arraylistTopEPMLink) {
		
		String lvResult = level0 + "\n";
		
		try {
			tmpStg = new String[arraylistTopEPM.size()];

			for (int y = 0; y < arraylistTopEPM.size(); y++) {
				epmChild = (EPMDocument) arraylistTopEPM.get(y);
				EPMMemberLink epmmemberlink = (EPMMemberLink) arraylistTopEPMLink.get(y);

				String depStg = "";
				if (epmmemberlink != null) {
					stringbuilder = new StringBuilder();
					stringbuilder.append('[')
							.append(epmmemberlink.isRequired() ? "(Required)" : "(O)")
							.append(epmmemberlink.isSuppressed() ? "(Suppressed)" : "")
							.append(epmmemberlink.isSubstitute() ? "(Substitute)" : "")
							.append(epmmemberlink.isPlaced() ? "(Placed)" : "")
							.append(epmmemberlink.isAnnotated() ? "(Annotated)" : "")
							.append(']');

					depStg = com.ptc.wpcfg.pdmabstr.PROEDependency
							.toString(epmmemberlink.getDepType())
							+ "[" + epmmemberlink.getDepType() + "]";
					typStg = depStg + ":" + stringbuilder.toString();
				}

				LMObject = (LifeCycleManaged) epmChild;
				StStg = (String) LMObject.getLifeCycleState().toString();
				OBID = epmChild.getPersistInfo().getObjectIdentifier().getId();
				verStg = epmChild.getVersionIdentifier().getValue();
				iterStg = epmChild.getIterationIdentifier().getValue();

				tmpStg[y] = epmChild.getNumber() + "|" + parStg
						+ "\tChild\t" + typStg + "\t" + OBID + "\t"
						+ epmChild.getNumber() + "\t"
						+ epmChild.getCADName() + "\t" + StStg + "\t"
						+ verStg + "\t" + isGen + "\t" + isInst + "\t" + ftStat;
			}

			Arrays.sort(tmpStg);
			
			// Process sorted children for output
			for (int i = 0; i < tmpStg.length; i++) {
				String[] parts = tmpStg[i].split("\\|");
				if (parts.length > 12) {
					lvResult += "1|" + parts[12] + "|" + parts[15] + "|1|0|0|M2_MM_ITEM_MREVA|\n";
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return lvResult;
	}

	/**
	 * Generate detailed As Stored relationship report
	 */
	private static String generateAsStoredRelationshipReport(ArrayList<AsStoredRelationshipMetadata> relationships) {
		StringBuilder report = new StringBuilder();
		
		report.append("\n\n# AS STORED RELATIONSHIP METADATA REPORT\n");
		report.append("# Format: Parent|ParentVer|Child|AsStoredVer|LatestVer|IsOutdated|Qty|Type|Dependencies\n");
		report.append("# ================================================================================\n");
		
		for (AsStoredRelationshipMetadata rel : relationships) {
			report.append("RELATIONSHIP|");
			report.append(rel.parentNumber).append("|");
			report.append(rel.parentVersion).append(".").append(rel.parentIteration).append("|");
			report.append(rel.childNumber).append("|");
			report.append("AS_STORED:").append(rel.childAsStoredVersion).append(".").append(rel.childAsStoredIteration).append("|");
			report.append("LATEST:").append(rel.childLatestVersion).append(".").append(rel.childLatestIteration).append("|");
			report.append("OUTDATED:").append(rel.isChildOutdated).append("|");
			report.append("QTY:").append(rel.quantityUsed).append("|");
			report.append("TYPE:").append(rel.relationshipType).append("|");
			report.append("DEPS:").append(rel.dependencyInfo).append("|");
			report.append("AS_STORED_STATE:").append(rel.childAsStoredState).append("|");
			report.append("LATEST_STATE:").append(rel.childLatestState);
			report.append("\n");
		}
		
		// Summary statistics
		long outdatedCount = 0;
		for (AsStoredRelationshipMetadata r : relationships) {
			if (r.isChildOutdated) outdatedCount++;
		}
		report.append("\n# SUMMARY: Total Relationships: ").append(relationships.size());
		report.append(", Outdated Children: ").append(outdatedCount);
		report.append(", Up-to-date: ").append(relationships.size() - outdatedCount).append("\n");
		
		return report.toString();
	}

	/**
	 * Enhanced method to generate comprehensive version relationship report
	 * Shows as-stored relationships for ALL versions of a document
	 */
	public static String generateCompleteVersionRelationshipReport(String docNumber) {
		StringBuilder report = new StringBuilder();
		
		try {
			report.append("# COMPLETE VERSION RELATIONSHIP REPORT FOR: ").append(docNumber).append("\n");
			report.append("# ================================================================================\n");
			
			// Get all versions of the document
			QueryResult allVersions = getAllVersionsEPMDoc(docNumber);
			
			if (allVersions != null && allVersions.size() > 0) {
				while (allVersions.hasMoreElements()) {
					EPMDocument version = (EPMDocument) allVersions.nextElement();
					
					// Get latest iteration of this version
					EPMDocument latestIteration = getLatestIterationOfVersion(version);
					if (latestIteration != null) {
						report.append("\n## VERSION: ").append(latestIteration.getVersionDisplayIdentifier());
						report.append(".").append(latestIteration.getIterationIdentifier().getValue());
						report.append(" [").append(latestIteration.getState().getState().getDisplay()).append("]\n");
						
						// Get as-stored relationships for this version
						String versionRelationships = getCADModelDetailsWithAsStoredMetadata(latestIteration, false);
						report.append(versionRelationships).append("\n");
					}
				}
			} else {
				report.append("No versions found for document: ").append(docNumber).append("\n");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			report.append("ERROR generating report: ").append(e.getMessage()).append("\n");
		}
		
		return report.toString();
	}

	// [Include all your existing methods here - getLatestEPMDoc, getAllVersionsEPMDoc, etc.]
	
	/**
	 * Get the latest EPM Document (Original method - kept for backward compatibility)
	 */
	@SuppressWarnings("deprecation")
	public static EPMDocument getLatestEPMDoc(String docnum)
			throws WTException, LifeCycleException {

		EPMDocument obj = null;
		EPMDocument lateobj = null;
		LatestConfigSpec config_spec = null;

		if (docnum != null) {
			QuerySpec qs = new QuerySpec(wt.epm.EPMDocument.class);
			qs.appendWhere(new SearchCondition(wt.epm.EPMDocument.class,
					"master>number", "=", docnum.toUpperCase(), false));
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(wt.epm.EPMDocument.class,
					"iterationInfo.latest", "TRUE"));
			config_spec = new LatestConfigSpec();
			if (config_spec != null) {
				qs = config_spec.appendSearchCriteria(qs);
			}
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (config_spec != null) {
				qr = config_spec.process(qr);
			}
			while (qr.hasMoreElements()) {
				obj = (EPMDocument) qr.nextElement();
				try {
					if (obj.isLatestIteration()) {
						lateobj = obj;
						return lateobj;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		return lateobj;
	}

	/**
	 * Get ALL versions and iterations of an EPM Document (not just latest)
	 * Returns QueryResult containing all versions regardless of state
	 */
	@SuppressWarnings("deprecation")
	public static QueryResult getAllVersionsEPMDoc(String docnum)
			throws WTException, LifeCycleException {

		QueryResult allVersionsQr = null;

		if (docnum != null) {
			// Query for ALL versions, not just latest
			QuerySpec qs = new QuerySpec(wt.epm.EPMDocument.class);
			qs.appendWhere(new SearchCondition(wt.epm.EPMDocument.class,
					"master>number", "=", docnum.toUpperCase(), false));
			// Remove the "latest" filter to get ALL versions
			// NO config_spec filtering to get all states (not just released)
			allVersionsQr = PersistenceHelper.manager.find(qs);
		}
		return allVersionsQr;
	}

	/**
	 * Get the latest iteration of a specific EPM Document version
	 * This finds the highest iteration number for a given version (e.g., A.4, B.2, C.1)
	 */
	@SuppressWarnings("deprecation")
	public static EPMDocument getLatestIterationOfVersion(EPMDocument version)
			throws WTException {
		
		EPMDocument latestIteration = null;
		
		try {
			// Get all iterations of this specific version
			wt.fc.QueryResult allIterationsQr = wt.vc.VersionControlHelper.service
					.iterationsOf((wt.vc.Iterated) version);
			
			int maxIterationValue = -1;
			
			while (allIterationsQr.hasMoreElements()) {
				EPMDocument iteration = (EPMDocument) allIterationsQr.nextElement();
			String iterVal = iteration.getIterationIdentifier().getValue();
			int iterValue = Integer.parseInt(iterVal);
			if (iterValue > maxIterationValue) {
				maxIterationValue = iterValue;
				latestIteration = iteration;
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return latestIteration;
	}

	// [Continue with all your other existing methods...]
	
	public static String getTCVersion(String version) {
		ArrayList<String> list = new ArrayList<String>();
		// Series Name : Reva
		// Seed Name :CONSERIES
		list.add("OR");
		list.add("A");
		list.add("B");
		list.add("C");
		list.add("D");
		list.add("E");
		list.add("F");
		list.add("G");
		list.add("H");
		list.add("I");
		list.add("J");
		list.add("K");
		list.add("L");
		list.add("M");
		list.add("N");
		list.add("O");
		list.add("P");
		list.add("Q");
		list.add("R");
		list.add("S");
		list.add("T");
		list.add("U");
		list.add("V");
		list.add("W");
		list.add("X");
		list.add("Y");
		list.add("Z");
		list.add("OB");
		if (version.equalsIgnoreCase("OB"))
			return "OB";
		return list.get(list.indexOf(version) + 1);
	}

	/**
	 * Main method to demonstrate the enhanced functionality
	 */
	public static void main(String[] args) {
		// Example usage:
		try {
			// Generate complete relationship report for a document
			if (args.length > 0) {
				String docNumber = args[0];
				String report = generateCompleteVersionRelationshipReport(docNumber);
				System.out.println(report);
				
				// Optionally save to file
				createLogOnLocation("C:\\temp", "relationship_report_" + docNumber, "txt", report);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Enhanced buildFolderPath method with revision folder support
	 */
	@SuppressWarnings("unused")
	public static String buildFolderPath(Persistable obj, String baseDir,
			String objState) throws Exception {
		String strFolderName = new String();
		String contextPrefix = new String();
		if (obj instanceof EPMDocument) {
			EPMDocument objEpm = (EPMDocument) obj;
			if (objEpm.getContainerReference().getContainer() instanceof WTLibrary) {
				contextPrefix = "L";
			}
			if (objEpm.getContainerReference().getContainer() instanceof PDMLinkProduct) {
				contextPrefix = "P";
			}
			String contName = objEpm.getContainerName();
			String foldLocation = objEpm.getLocation().toString().replaceAll(
					"/Default", "");
			contName = contName.replaceAll(" ", "_");
			foldLocation = foldLocation.replaceAll(" ", "_");
			String fileSep = File.separator;
			foldLocation = foldLocation.replace("/", fileSep);
			
			// Get revision letter (A, B, C, etc.) for folder structure
			String revisionFolder = objEpm.getVersionIdentifier().getValue();
			
			// Build folder path with revision folder: baseDir/state/containerName_PEPM/location/Rev_X/
			strFolderName = baseDir + fileSep + objState + fileSep + contName
					+ "_" + contextPrefix + "EPM" + foldLocation + fileSep 
					+ "Rev_" + revisionFolder + fileSep;

		}
		if (obj instanceof WTDocument) {
			WTDocument objDoc = (WTDocument) obj;
			if (objDoc.getContainerReference().getContainer() instanceof WTLibrary) {
				contextPrefix = "L";
			}
			if (objDoc.getContainerReference().getContainer() instanceof PDMLinkProduct) {
				contextPrefix = "P";
			}
			String contName = objDoc.getContainerName();
			String foldLocation = objDoc.getLocation().toString().replaceAll(
					"/Default", "");
			contName = contName.replaceAll(" ", "_");
			foldLocation = foldLocation.replaceAll(" ", "_");
			String fileSep = File.separator;
			foldLocation = foldLocation.replace("/", fileSep);
			
			// Get revision letter (A, B, C, etc.) for folder structure
			String revisionFolder = objDoc.getVersionIdentifier().getValue();
			
			// Build folder path with revision folder: baseDir/state/containerName_PDOC/location/Rev_X/
			strFolderName = baseDir + fileSep + objState + fileSep + contName
					+ "_" + contextPrefix + "DOC" + foldLocation + fileSep 
					+ "Rev_" + revisionFolder + fileSep;
			System.out.println("strFolderName" + strFolderName);
		}

		return strFolderName;
	}

	public static String getIBAValuebyLogicalID(IBAHolder ibaHolder,
			String logicalID) {
		String s = null;
		try {
			ibaHolder = IBAValueHelper.service.refreshAttributeContainer(
					ibaHolder, null, SessionHelper.manager.getLocale(), null);
			DefaultAttributeContainer theContainer = (DefaultAttributeContainer) ibaHolder
					.getAttributeContainer();
			if (theContainer != null) {
				AttributeDefDefaultView[] theAtts = theContainer
						.getAttributeDefinitions();
				for (int i = 0; i < theAtts.length; i++) {
					if (theAtts[i].getLogicalIdentifier().equals(logicalID)) {
						AbstractValueView[] theValues = theContainer
								.getAttributeValues(theAtts[i]);

						if (theValues != null) {
							for (int j = 0; j < theValues.length; j++) {
								if (s == null)
									s = IBAValueUtility
											.getLocalizedIBAValueDisplayString(
													theValues[j],
													SessionHelper.manager
															.getLocale());
								else
									s += IBAValueUtility
											.getLocalizedIBAValueDisplayString(
													theValues[j],
													SessionHelper.manager
															.getLocale());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	@SuppressWarnings("deprecation")
	public static ApplicationData getEPMAppData(EPMDocument epmdoc)
			throws WTException, PropertyVetoException {

		ApplicationData appData = new ApplicationData();

		if (epmdoc.isGeneric() || epmdoc.isInstance()) {
			FormatContentHolder fch = (FormatContentHolder) PublishUtils
					.findFamilyTableFromEPMDocument(epmdoc, null);
			ContentHolder content = ContentHelper.service
					.getContents((ContentHolder) fch);
			ContentItem contentitem = ContentHelper.service
					.getPrimary((FormatContentHolder) content);
			if (contentitem instanceof ApplicationData) {
				appData = (ApplicationData) contentitem;
				fch = null;
				content = null;
				contentitem = null;
			}
		} else {
			ContentHolder content = ContentHelper.service
					.getContents((ContentHolder) epmdoc);
			ContentItem contentitem = ContentHelper.service
					.getPrimary((FormatContentHolder) content);
			if (contentitem instanceof ApplicationData) {
				appData = (ApplicationData) contentitem;
				content = null;
				contentitem = null;
			}
		}

		return appData;
	}

	@SuppressWarnings("deprecation")
	public static String getEPMFileName(EPMDocument epmdoc,
			ApplicationData appData) throws WTException, PropertyVetoException {
		return EPMContentHelper.getContentName(epmdoc, appData);
	}

	@SuppressWarnings("deprecation")
	public static String getEPMFileName(EPMDocument epmdoc) throws WTException,
			PropertyVetoException {
		String fileName = new String();

		if (epmdoc.isGeneric() || epmdoc.isInstance()) {
			FormatContentHolder fch = (FormatContentHolder) PublishUtils
					.findFamilyTableFromEPMDocument(epmdoc, null);
			ContentHolder content = ContentHelper.service
					.getContents((ContentHolder) fch);
			ContentItem contentitem = ContentHelper.service
					.getPrimary((FormatContentHolder) content);
			if (contentitem instanceof ApplicationData) {
				ApplicationData appData = (ApplicationData) contentitem;

				fileName = EPMContentHelper.getContentName(epmdoc, appData);
				appData = null;
				fch = null;
				content = null;
				contentitem = null;
			}
		} else {
			ContentHolder content = ContentHelper.service
					.getContents((ContentHolder) epmdoc);
			ContentItem contentitem = ContentHelper.service
					.getPrimary((FormatContentHolder) content);
			if (contentitem instanceof ApplicationData) {
				ApplicationData appData = (ApplicationData) contentitem;
				fileName = EPMContentHelper.getContentName(epmdoc, appData);
				appData = null;
				content = null;
				contentitem = null;
			}
		}

		return fileName;
	}

	public static String getCADNonGenericDetails(EPMDocument dataObj,
			String tcVersion) {
		String outData = new String();
		String strWeight = new String();

		try {
			String itemType = "NAVICO_CAD";
			String objNumber = new String();
			String objName = new String();
			String objVersion = new String();
			String objDescription = new String();
			String objState = new String();
			String objCreatedDate = new String();
			String objModifyDate = new String();
			String objOwner = new String();
			String objLastModified = new String();
			String objGroup = new String();
			String ApprovedBy = new String();
			String CheckedBy = new String();
			String DrawStatus = new String();
			String Material = new String();
			String Assyno = new String();
			String Weight = new String();
			String Remarks = new String();
			String Supplier = new String();
			String Surfacearea = new String();
			String Description = new String();
			String Mfgprocess = new String();
			String Drawingrefno = new String();
			String Surfacefinish = new String();
			String Typeofdoc = new String();
			String TAssyno = new String();
			String tcState = "X";
			String eol = "EOL";
			String strSep = "|";

			objNumber = dataObj.getNumber();
			objName = dataObj.getName().toUpperCase();
			objVersion = dataObj.getVersionIdentifier().getValue() + "."
					+ dataObj.getIterationIdentifier().getValue();
//			objDescription = dataUtil.removeFormattingCharacters(
//					dataObj.getDescription()).trim();
			objDescription ="";
			objState = dataObj.getState().getState().getDisplay();
			objModifyDate = dateFormat.format(PersistenceHelper
					.getModifyStamp(dataObj));
			objState = dataObj.getState().getState().getDisplay();
			objCreatedDate = dateFormat.format(PersistenceHelper
					.getCreateStamp(dataObj));
			objGroup = "dba";
			objLastModified = "migration_user";
			objOwner = "migration_user";

			ApprovedBy = dataUtil
					.removeFormattingCharacters(getIBAValuebyLogicalID(dataObj,
							"APPROVED_BY"));
			CheckedBy = dataUtil
					.removeFormattingCharacters(getIBAValuebyLogicalID(dataObj,
							"CHECKED_BY"));
			DrawStatus = dataUtil
					.removeFormattingCharacters(getIBAValuebyLogicalID(dataObj,
							"DRW_STATUS"));
			Material = dataUtil
					.removeFormattingCharacters(getIBAValuebyLogicalID(dataObj,
							"MATERIAL"));
			Assyno = dataUtil
					.removeFormattingCharacters(getIBAValuebyLogicalID(dataObj,
							"ASSY_NO"));
			Weight = dataUtil
					.removeFormattingCharacters(getIBAValuebyLogicalID(dataObj,
							"WEIGHT"));
			Remarks = dataUtil
					.removeFormattingCharacters(getIBAValuebyLogicalID(dataObj,
							"REMARKS"));
			Supplier = dataUtil
					.removeFormattingCharacters(getIBAValuebyLogicalID(dataObj,
							"SUPPLIER"));
			Surfacearea = dataUtil
					.removeFormattingCharacters(getIBAValuebyLogicalID(dataObj,
							"SURFACE_AREA"));
			Description = dataUtil
					.removeFormattingCharacters(getIBAValuebyLogicalID(dataObj,
							"DESCRIPTION"));
			Mfgprocess = dataUtil
					.removeFormattingCharacters(getIBAValuebyLogicalID(dataObj,
							"MFG_PROCESS"));
			Drawingrefno = dataUtil
					.removeFormattingCharacters(getIBAValuebyLogicalID(dataObj,
							"DRW_REF_NO"));
			Surfacefinish = dataUtil
					.removeFormattingCharacters(getIBAValuebyLogicalID(dataObj,
							"SURFACE_FINISH"));

			if (Weight != null && !Weight.equalsIgnoreCase("0")
					&& !Weight.equalsIgnoreCase("-")) {
				float f = Float.parseFloat(Weight);
				strWeight = String.format("%.3f", f);
			}

			Typeofdoc = itemType;
			boolean isFirstVesrion = wt.vc.VersionControlHelper.service
					.isFirstIteration((wt.vc.Iterated) dataObj);
			if (!isFirstVesrion) {
				objCreatedDate = objModifyDate;
			}
			
			if(DrawStatus != null && DrawStatus.length()>2)
			{
				if(DrawStatus.trim().equalsIgnoreCase("WIP"))
				{
					DrawStatus="-";
				}else
				{
					DrawStatus = DrawStatus.substring(0, 2);
				}
			}
			if(Assyno != null && Assyno.length()>50)
			{
				Assyno = Assyno.substring(0, 50);
			}

			// TC validation for length < 7 or > 13 characters
			String numCount = "NA";
			int objLen = objNumber.length();
			if (objLen < 7)
				numCount = "Less than 7";
			if (objLen > 13)
				numCount = "More than 13";

			outData = itemType + strSep + objNumber + strSep + tcVersion
					+ strSep + objDescription + strSep + objModifyDate + strSep
					+ tcState + strSep + objCreatedDate + strSep
					+ objLastModified + strSep + objOwner + strSep + objName
					+ strSep + objGroup + strSep + ApprovedBy + strSep
					+ CheckedBy + strSep + DrawStatus + strSep + Material
					+ strSep + Assyno + strSep + strWeight + strSep + Remarks
					+ strSep + Supplier + strSep + Surfacearea + strSep
					+ Description + strSep + Mfgprocess + strSep + Drawingrefno
					+ strSep + Surfacefinish + strSep + objVersion + strSep
					+ objState + strSep + eol + strSep + numCount;

			return outData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outData;
	}

	public static String getDataSetCADType(String ext) {
		ext = ext.toLowerCase();

		HashMap<String, String> typeMap = new HashMap<String, String>();
		String type = null;
		typeMap.put("prt", "ProPrt");
		typeMap.put("asm", "ProAsm");
		typeMap.put("drw", "ProDrw");
		type = typeMap.get(ext);
		if (type == null) {
			type = "#Error";
		}
		typeMap = null;
		return type;
	}
	
	// ============ MISSING METHODS FROM COMPLETE VERSION ============
	
	public static String getEPMDataSetDetails(EPMDocument dataObj,
			String tcVersion, String itemType, String fileName, String filePath)
			throws Exception {

		String outData = new String();
		String objNumber = new String();
		String objVersion = new String();
		String objState = new String();
		String objCreatedDate = new String();
		String objModifyDate = new String();
		String objOwner = new String();
		String objLastModified = new String();
		String objGroup = new String();
		String Typeofdoc = new String();
		String eol = "EOL";
		String tcState = "X";
		String strSep = "|";

		objNumber = dataObj.getNumber();
		objVersion = dataObj.getVersionIdentifier().getValue() + "."
				+ dataObj.getIterationIdentifier().getValue();
		objModifyDate = dateFormat.format(PersistenceHelper
				.getModifyStamp(dataObj));
		objState = dataObj.getState().getState().getDisplay();
		objCreatedDate = dateFormat.format(PersistenceHelper
				.getCreateStamp(dataObj));
		objLastModified = dataObj.getModifierFullName();
		objOwner = dataObj.getCreatorFullName();

		objGroup = "dba";
		objLastModified = "migration_user";
		objOwner = "migration_user";
		objVersion = tcVersion;
		Typeofdoc = itemType;

		boolean isFirstVesrion = wt.vc.VersionControlHelper.service
				.isFirstIteration((wt.vc.Iterated) dataObj);
		if (!isFirstVesrion) {
			objCreatedDate = objModifyDate;
		}

		outData = itemType + strSep + fileName + strSep + objNumber + strSep
				+ filePath + strSep + fileName + strSep + objVersion + strSep
				+ tcState + strSep + objCreatedDate + strSep + objModifyDate
				+ strSep + objOwner + strSep + objLastModified + strSep
				+ objGroup + strSep + Typeofdoc + strSep + eol;

		return outData;
	}
	
	public static ArrayList getAllFamilyGeneric(String whatType)
			throws Exception {
		ArrayList result = new ArrayList();

		QuerySpec qs = new QuerySpec(EPMSepFamilyTableMaster.class);

		qs.appendWhere(new SearchCondition(EPMSepFamilyTableMaster.class,
				EPMSepFamilyTableMaster.NAME, SearchCondition.LIKE, "%."
						+ whatType.toLowerCase()), new int[] { 0, 1 });

		QueryResult localQueryResult = PersistenceHelper.manager
				.find((StatementSpec) qs);
		while (localQueryResult.hasMoreElements()) {
			EPMSepFamilyTableMaster masterepm = (EPMSepFamilyTableMaster) localQueryResult
					.nextElement();
			result.add(masterepm.getName().toUpperCase().trim());
		}
		return result;
	}
	
	public static String getGenInsDetails(EPMDocument epmObj,
			boolean isVersionPlus) throws WTException {
		String lvResult = new String();
		String verStg = new String();
		try {
			// Create Generic and Instance data for file START
			EPMFamily epmfamily = EPMFamily.getEPMFamily(epmObj);
			if (epmfamily != null) {
				EPMDocument generic = (EPMDocument) epmfamily.getGeneric();
				verStg = generic.getVersionDisplayIdentifier().toString();
				if (isVersionPlus)
					verStg = getTCVersion(generic.getVersionDisplayIdentifier()
							.toString());

				List list = epmfamily.getInstances();
				// Retrieve all the instances in the FT
				for (int i = 0; i < list.size(); i++) {
					EPMDocument variant = (EPMDocument) list.get(i);
					lvResult += generic.getNumber() + "|" + verStg
							+ "|Generic|" + variant.getNumber() + "|"
							+ variant.getVersionDisplayIdentifier()
							+ "|Instance|" + "\n";
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return lvResult;
	}
	
	public static String getInstanceDetails(EPMDocument epmObj, String eolStr)
			throws WTException {
		String lvResult = new String();
		String verStg = new String();
		try {
			EPMFamily epmfamily = EPMFamily.getEPMFamily(epmObj);
			if (epmfamily != null) {
				EPMDocument generic = (EPMDocument) epmfamily.getGeneric();

				String info = generic.getNumber() + " | "
						+ generic.getVersionDisplayIdentifier() + "."
						+ generic.getIterationIdentifier().getValue();
				List list = epmfamily.getInstances();
				for (int i = 0; i < list.size(); i++) {
					EPMDocument variant = (EPMDocument) list.get(i);
					lvResult += getCADNonGenericDetails(variant, variant
							.getVersionDisplayIdentifier().toString())
							+ "\n";
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return lvResult;
	}
	
	public static String getCADModelDetails(EPMDocument epmDoc,
			boolean isVersionPlus) throws WTException {
		String lvResult = new String();
		try {
			String level0 = new String();
			arraylistTopEPM = new ArrayList();
			arraylistTopEPMLink = new ArrayList();

			LMObject = (LifeCycleManaged) epmDoc;
			StStg = (String) LMObject.getLifeCycleState().toString();
			OBID = epmDoc.getPersistInfo().getObjectIdentifier().getId();
			verStg = epmDoc.getVersionIdentifier().getValue();
			iterStg = epmDoc.getIterationIdentifier().getValue();

			isGen = false;
			isInst = false;
			ftStat = 0;

			isGen = epmDoc.isGeneric();
			isInst = epmDoc.isInstance();
			ftStat = epmDoc.getFamilyTableStatus();

			if (isVersionPlus)
				verStg = getTCVersion(epmDoc.getVersionDisplayIdentifier()
						.toString());

			level0 = "0|" + epmDoc.getNumber() + "|" + verStg
					+ "|1|0|0|_|M2_MM_ITEM_MREVA";
			parStg = "parent\t" + OBID + "\t" + epmDoc.getNumber() + "\t"
					+ epmDoc.getCADName() + "\t" + StStg + "\t" + verStg + "."
					+ iterStg + "\t" + isGen + "\t" + isInst + "\t" + ftStat;

			EPMAsStoredConfigSpec configSpec = EPMAsStoredConfigSpec
					.newEPMAsStoredConfigSpec(epmDoc);
			LatestConfigSpec latest_config = new LatestConfigSpec();
			qrChildChecker = EPMStructureHelper.service
					.navigateUsesToIteration(epmDoc, null, false, configSpec);

			if (qrChildChecker.size() > 0) {
				for (int x = 0; x < qrChildChecker.size(); x++) {

					Persistable apersistable1[] = (Persistable[]) qrChildChecker
							.nextElement();
					if (apersistable1[1] instanceof EPMDocument) {
						EPMMemberLink bomUsage = (EPMMemberLink) apersistable1[0];
						EPMDocument child = (EPMDocument) apersistable1[1];
//System.out.println("Chil Part Name and Version" + child.getNumber() +"  "+ child.getVersionDisplayIdentifier().toString());
						arraylistTopEPM.add(child);
						arraylistTopEPMLink.add(bomUsage);
					}
				}

				tmpStg = new String[0];
				tmpStg = new String[arraylistTopEPM.size()];

				for (int y = 0; y < arraylistTopEPM.size(); y++) {
					isQty = true;
					epmChild = null;
					epmChildp1 = null;

					epmChild = (EPMDocument) arraylistTopEPM.get(y);
					EPMMemberLink epmmemberlink = (EPMMemberLink) arraylistTopEPMLink
							.get(y);

					if (epmmemberlink != null) {
						stringbuilder = new StringBuilder();

						stringbuilder
								.append('[')
								.append(
										epmmemberlink.isRequired() ? "(Required)"
												: "(O)")
								.append(
										epmmemberlink.isSuppressed() ? "(Suppressed)"
												: "")
								.append(
										epmmemberlink.isSubstitute() ? "(Substitute)"
												: "")
								.append(
										epmmemberlink.isPlaced() ? "(Placed)"
												: "")
								.append(
										epmmemberlink.isAnnotated() ? "(Annotated)"
												: "").append(']');

						String depStg = com.ptc.wpcfg.pdmabstr.PROEDependency
								.toString(epmmemberlink.getDepType())
								+ "[" + epmmemberlink.getDepType() + "]";
						typStg = depStg + ":" + stringbuilder.toString();
					}

					LMObject = (LifeCycleManaged) epmChild;
					StStg = (String) LMObject.getLifeCycleState().toString();
					OBID = epmChild.getPersistInfo().getObjectIdentifier()
							.getId();
					verStg = epmChild.getVersionIdentifier().getValue();
					iterStg = epmChild.getIterationIdentifier().getValue();

					isGen = false;
					isInst = false;
					ftStat = 0;

					isGen = epmChild.isGeneric();
					isInst = epmChild.isInstance();
					ftStat = epmChild.getFamilyTableStatus();

					tmpStg[y] = epmChild.getNumber() + "|" + parStg
							+ "\tChild\t" + typStg + "\t" + OBID + "\t"
							+ epmChild.getNumber() + "\t"
							+ epmChild.getCADName() + "\t" + StStg + "\t"
							+ verStg + "\t" + isGen + "\t"
							+ isInst + "\t" + ftStat;
				}

				Arrays.sort(tmpStg);

				int qtyNo = 0;
				int qtyNoSuppressed = 0;
				ArrayList fullData = new ArrayList();

				for (int z = 0; z <= (tmpStg.length - 1); z++) {
					tmpStgCur = tmpStg[z].split("\\|");
					if (z < (tmpStg.length - 1)) {
						tmpStgCur2 = tmpStg[z + 1].split("\\|");
					} else {
						tmpStgCur2 = new String[1];
						tmpStgCur2[0] = "";
					}
					if (iDebug > 1)
						System.out.println("\n===> tmpStg[" + z + "] " + qtyNo
								+ "/" + qtyNoSuppressed + " : " + tmpStg[z]
								+ "\n");

					if (tmpStgCur[0].equals(tmpStgCur2[0])) {
						isQty = false;

						if (tmpStgCur[1].contains("Suppressed")) {
							qtyNoSuppressed++;
							if (iDebug > 1)
								System.out.println("\nBANG " + qtyNo + "/"
										+ qtyNoSuppressed + " : "
										+ tmpStgCur[1] + "\n");
							fullStgSuppressed = tmpStgCur[1] + "\t"
									+ qtyNoSuppressed + "\t" + "Suppressed";
						} else {
							qtyNo++;
							fullStg = tmpStgCur[1] + "\t" + qtyNo;
						}

					} else {
						if (tmpStgCur[1].contains("Suppressed")) {
							qtyNoSuppressed++;
							if (iDebug > 1)
								System.out.println("\nBANG C " + qtyNo + "/"
										+ qtyNoSuppressed + " : "
										+ tmpStgCur[1] + "\n");
							fullStgSuppressed = tmpStgCur[1] + "\t"
									+ qtyNoSuppressed + "\t" + "Suppressed";
						} else {
							qtyNo++;
							fullStg = tmpStgCur[1] + "\t" + qtyNo;
						}
						isQty = true;
					}

					if (isQty) {
						if (qtyNo > 0) {
							fullData.add(fullStg + "\t" + "\t");
							qtyNo = 0;
						}
						if (qtyNoSuppressed > 0) {
							fullData.add(fullStgSuppressed + "\t");
							qtyNoSuppressed = 0;
						}
					}
				}
				int sno = 1;
				lvResult = level0 + "\n";
				for (int k = 0; k < fullData.size(); k++) {
					strAsmDetails = fullData.get(k).toString().split("\\t");
					String isSuppressed = fullData.get(k).toString();
					if (!isSuppressed.contains("Suppressed"))
						lvResult += "1|" + strAsmDetails[12] + "|"
								+ strAsmDetails[15] + "|" + strAsmDetails[19]
								+ "|0|0|M2_MM_ITEM_MREVA|\n";
				}
			} else {
				// No children
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return lvResult;
	}

	@SuppressWarnings("null")
	public static String removeFormattingCharacters(String toBeEscaped) {
		if (toBeEscaped == null) {
			return "-";
		}
		
		String removeEscape = toBeEscaped;
		StringBuffer escapedBuffer = new StringBuffer();
		
		if(removeEscape!=null && removeEscape.length()>1)
		{
			if("-".equalsIgnoreCase(removeEscape.substring(0,1)))
			{
				removeEscape =removeEscape.substring(1, removeEscape.length());
			}
		}

		if (removeEscape == null || removeEscape.isEmpty()
				|| removeEscape.equals(" ") || removeEscape.equals("")
				|| removeEscape.equalsIgnoreCase("null")) {
			escapedBuffer.append("-");
		} else {
			removeEscape = removeEscape.replaceAll("\n|\r|\t", "");
			escapedBuffer.append(removeEscape);
		}
		String s = escapedBuffer.toString();
		escapedBuffer = null;
		return s.trim();
	}

	public static void createLogOnLocation(String location, String fileName,
			String fileExtn, String string) throws Exception {

		fileName = location + File.separator + fileName + "."
				+ fileExtn;
		FileWriter writer = new FileWriter(new File(fileName));
		BufferedWriter wrtiter = new BufferedWriter(writer);
		writer.append(string);
		writer.flush();
		writer.close();
	}
}
