package ext.splm.extraction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ext.splm.extraction.nongeneric.docDataTCExtractionHelper;
import wt.content.ApplicationData;
import wt.epm.EPMDocConfigSpec;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMReferenceType;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleManaged;
import wt.method.RemoteMethodServer;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.WhereExpression;
import wt.vc.config.ConfigSpec;

public class CADDataExtractionUtility {

	/**
	 * @param args
	 */
	@SuppressWarnings("unused")
	private static final String txtSep = "|";
	private static DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm");// yyyy_MM_dd_HHmmss
	private static Calendar cal = Calendar.getInstance();
	private static final String USER_NAME = "wcadmin";
	private static final String USER_PWD = "wcadmin";
	private static File folderDir = null;
	private static String baseDir = "C:" + File.separator + "cad_contents";

	static StringBuilder stringbuilder;
	static String[] tmpStg, tmpStgCur, tmpStgCur2, strAsmDetails;
	static String OT, QueStg, StStg, epmNo, epmNmIn, epmNoIn, epmNoLg, epmNoSh, parStg, fullStg, fullStgSuppressed,
			childStg;
	static String oid, oidEpm, linkEpmStg, oidCh, oidPar, verStg, iterStg, typStg;
	static long OBID;
	static boolean isGen, isInst, isQty;
	static int ftStat, z, iDebug = 0;
	static EPMDocument epm, epm1, epmChild, epmChildp1, epmTop;
	static LifeCycleManaged LMObject;
	static QueryResult qrUses, qrChildChecker, qr;
	static ArrayList arraylistTopEPM;
	static ArrayList skipEPM = new ArrayList();

	static ArrayList arraylistTopEPMLink;
	static ConfigSpec configSpec;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Date lv_start = new Date();

		// java ext.cms.mr.epm.dataTCDrawingDetails wcadmin wcadmin drw false

		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		String strUserName = new String();
		String strUserPass = new String();
		boolean needContent = false;

		String whatCADData = new String();

		skipEPM.add("SK-08-108.ASM");
		skipEPM.add("SK-08-108.DRW"); // it doestn't have primary content.
		skipEPM.add("BATT_PACK_HRNS.PRT");
		skipEPM.add("JAWA44.ASM");
		if (args.length == 4) {

			strUserName = args[0];
			strUserPass = args[1];
			whatCADData = args[2];
			needContent = Boolean.parseBoolean(args[3].toString().trim());

		} else
			throw new Exception("\n\n_______________________________________________________________________\n\n "
					+ "Usage : java ext.custom.extraction.CADDataExtractionUtility \n\n "
					+ "	UserName  : Admin User ID \n " + "	Password  : Admin Password \n "
					+ "	whatCADData  :\n\t asm or prt or drw" + "	canDownload  :\n\t true or false"
					+ "\n\n_______________________________________________________________________\n");

		whatCADData = whatCADData.trim().toUpperCase();

		rms.setUserName(strUserName);
		rms.setPassword(strUserPass);

		/*
		 * rms.setUserName("wcadmin"); rms.setPassword("wcadmin"); whatCADData = "asm";
		 * getEPMMasterByCADType(whatCADData.trim().toUpperCase(), true); whatCADData =
		 * "prt"; getEPMMasterByCADType(whatCADData.trim().toUpperCase(), true);
		 */
		getEPMMasterByCADType(whatCADData.trim().toUpperCase(), needContent);

		System.out.println("Start Time :: " + lv_start);
		System.out.println("End   Time :: " + new Date());

	}

	@SuppressWarnings({})
	public static QueryResult getEPMMasterByCADType(String whatItemType, boolean canDownload) throws Exception {
		QueryResult localQueryResult = new QueryResult();
		EPMDocumentMaster masterepm = null;
		EPMDocument epmObj = null;
		String tcVersion = new String();
		EPMDocument formatfile = null;
		// Ensure extraction directory exists
		String extractionDir = "C:" + File.separator + "extraction";
		File extractionFolder = new File(extractionDir);
		if (!extractionFolder.exists()) {
			extractionFolder.mkdirs();
		}
		// More meaningful and self-explanatory CSV filenames based on file type
		String createItemFileName;
		if (whatItemType.equalsIgnoreCase("ASM")) {
			createItemFileName = extractionDir + File.separator + whatItemType
					+ "_CAD_Assemblies_With_AsStored_Relationships.csv";
		} else {
			createItemFileName = extractionDir + File.separator + whatItemType + "_CAD_Items_Metadata.csv";
		}
		String cadDataSetFileName = extractionDir + File.separator + whatItemType
				+ "_CAD_Files_With_Drawing_References.csv";
		String cadIndividualDrawingDataSetFileName = extractionDir + File.separator + whatItemType
				+ "_CAD_Files_Standalone_No_Drawings.csv";

		BufferedWriter fwriterCadIndividualDrawingDetails = new BufferedWriter(
				new FileWriter(new File(createItemFileName), true));
		BufferedWriter fwriterCadDrawingDataSetDetails = new BufferedWriter(
				new FileWriter(new File(cadDataSetFileName), true));
		BufferedWriter fwriterCadIndividualDrawingDataSetDetails = new BufferedWriter(
				new FileWriter(new File(cadIndividualDrawingDataSetFileName), true));

		// If files are newly created, write header
		File file1 = new File(createItemFileName);
		if (file1.length() == 0) {
			fwriterCadIndividualDrawingDetails.write(
					"#ItemType|ItemID(Number)|RevID(REv andversion)|Description|ModifiedDate|Rel_status(State)|Date_created|Last_mod_user|owner|Item_Name|Group|APPROVED_BY|CHECKED_BY|DRW_STATUS|MATERIAL|ASSY_NO|WEIGHT|REMARKS|Supplier|surfacearea|description|mfgproces|drawingref|surfacefinish|WCVersion|TC State|EOL\n");
		}
		File file2 = new File(cadDataSetFileName);
		if (file2.length() == 0) {
			fwriterCadDrawingDataSetDetails.write(
					"#DatasetType|DatasetName|AttachedTo|Filepath|Filename|Rev|Rel_status|Date_created|Modified_Date|owner|Last_mod_user|Group|TYPE_OF_DOC|FormatFile1|FormatFile2|FormatFile3|FormatFile4|EOL\n");
		}
		File file3 = new File(cadIndividualDrawingDataSetFileName);
		if (file3.length() == 0) {
			fwriterCadIndividualDrawingDataSetDetails.write(
					"#DatasetType|DatasetName|AttachedTo|Filepath|Filename|Rev|Rel_status|Date_created|Modified_Date|owner|Last_mod_user|Group|TYPE_OF_DOC|FormatFile1|FormatFile2|FormatFile3|FormatFile4|EOL\n");
		}

		try {
			QuerySpec qs = new QuerySpec(EPMDocumentMaster.class);

			qs.appendWhere(new SearchCondition(EPMDocumentMaster.class, EPMDocumentMaster.NUMBER, "LIKE",
					"%." + whatItemType.toUpperCase().trim()), new int[] { 0, 1 });

			// qs.appendWhere(new SearchCondition(EPMDocumentMaster.class,
			// EPMDocumentMaster.NUMBER, SearchCondition.EQUAL,
			// "953-01-11.PRT"), new int[] { 0, 1 }); // 531-00-50.ASM

			localQueryResult = PersistenceHelper.manager.find((StatementSpec) qs);
			System.out.println(
					"For ITEM " + whatItemType.toUpperCase().trim() + " Result Size == " + localQueryResult.size());
			int sno = 1;
			int totalNonGeneric = localQueryResult.size();
			for (int i = 0; i < totalNonGeneric; i++) {
				masterepm = (EPMDocumentMaster) localQueryResult.nextElement();

				// Get ALL versions of the EPMDocument instead of just latest
				wt.fc.QueryResult allVersionsQr = wt.vc.VersionControlHelper.service.allVersionsOf(masterepm);

				System.out.println((i + 1) + " of " + localQueryResult.size() + " | " + masterepm.getNumber()
						+ " | Total Versions: " + allVersionsQr.size());

				// Process ALL versions of this EPMDocument
				while (allVersionsQr.hasMoreElements()) {
					EPMDocument currentVersion = (EPMDocument) allVersionsQr.nextElement();

					// MODIFIED: Include family table parts - only check skip list
					boolean tempResult = !(skipEPM.contains(currentVersion.getNumber().toUpperCase()));

					// Debug info - print family table status for analysis
					System.out.println(
							"    Version: " + currentVersion.getVersionDisplayIdentifier() + " | FamilyTableStatus: "
									+ currentVersion.getFamilyTableStatus() + " | Will Process: " + tempResult);

					if (tempResult) {
						// Get all iterations of this specific version to find the latest
						wt.fc.QueryResult allIterationsQr = wt.vc.VersionControlHelper.service
								.iterationsOf((wt.vc.Iterated) currentVersion);

						// Find the latest iteration for this revision
						EPMDocument latestIteration = null;
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

						// Process only the latest iteration of this revision
						if (latestIteration != null) {
							EPMDocument currentIteration = latestIteration;

							System.out.println("  Processing LATEST Iteration of Revision: "
									+ currentIteration.getVersionDisplayIdentifier() + "."
									+ currentIteration.getIterationIdentifier().getValue() + " | State: "
									+ currentIteration.getState().getState().getDisplay() + " | Family Status: "
									+ currentIteration.getFamilyTableStatus());

							epmObj = currentIteration;

							// Current version folder (not hardcoded to "released")
							String versionState = currentIteration.getState().getState().getDisplay().toLowerCase();
							String folderPath = dataUtil.buildFolderPath(currentIteration, baseDir, versionState);

							// GET DATA SET DETAILS START for current iteration
							String fileName = new String();
							ApplicationData appData = dataUtil.getEPMAppData(currentIteration);
							fileName = dataUtil.getEPMFileName(currentIteration, appData);
							String DatasetType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length())
									.toLowerCase();
							EPMDocConfigSpec config = new EPMDocConfigSpec();

							if (canDownload) {
								folderDir = new File(folderPath);
								folderDir.mkdirs();
							}
							String currfileWithPathName = folderPath + File.separator + fileName;

							if (canDownload) {
								Class[] classprms = { ApplicationData.class, String.class };
								Object[] objectprms = { appData, currfileWithPathName };
								RemoteMethodServer.getDefault().invoke("writeInFolderLocation",
										docDataTCExtractionHelper.class.getName(), null, classprms, objectprms);
							}

							// Process drawing references for current iteration
							QuerySpec querySpec = new QuerySpec();
							int linkIndex = querySpec.appendClassList(EPMReferenceLink.class, false);
							querySpec.appendWhere((WhereExpression) new SearchCondition(EPMReferenceLink.class,
									"referenceType", "=", EPMReferenceType.toEPMReferenceType("DRAWING")),
									new int[] { linkIndex });
							QueryResult qr3 = EPMStructureHelper.service.navigateReferencesToIteration(currentIteration,
									querySpec, true, config);

							// Process format files for current iteration
							QuerySpec formatspec = new QuerySpec(EPMDocumentMaster.class);
							linkIndex = formatspec.appendClassList(EPMReferenceLink.class, false);
							formatspec.appendWhere(
									(WhereExpression) new SearchCondition(EPMReferenceLink.class, "referenceType", "=",
											EPMReferenceType.toEPMReferenceType("DRAWING_FORMAT")),
									new int[] { linkIndex });
							QueryResult formatfiles = EPMStructureHelper.service
									.navigateReferencesToIteration(currentIteration, formatspec, true, config);

							if (qr3.size() > 0) {
								// ADD AS STORED RELATIONSHIP METADATA ONLY FOR ASSEMBLIES
								if (whatItemType.equalsIgnoreCase("ASM")) {
									try {
										String asStoredRelationships = dataUtil
												.getCADModelDetailsWithAsStoredMetadata(currentIteration, false);
										if (asStoredRelationships != null && !asStoredRelationships.trim().isEmpty()) {
											// Extract only the relationship metadata part and write to PartMetaData
											// file
											String[] relationshipLines = asStoredRelationships.split("\n");
											for (String line : relationshipLines) {
												if (line.startsWith("RELATIONSHIP|")) {
													fwriterCadIndividualDrawingDetails
															.write("# AS_STORED_" + line + "\n");
												}
											}
										}
									} catch (Exception e) {
										System.err.println("Error getting As Stored relationships for assembly "
												+ currentIteration.getNumber() + ": " + e.getMessage());
									}
								}

								while (qr3.hasMoreElements()) {
									EPMDocument doc = (EPMDocument) qr3.nextElement();
									String docType = "" + doc.getDocType();
									if (docType.equalsIgnoreCase("CADCOMPONENT")
											|| docType.equalsIgnoreCase("CADASSEMBLY")) {
										String outData = CADDataExtractionUtility.getEPMDrawingDataSetDetails(
												currentIteration,
												currentIteration.getVersionDisplayIdentifier().toString(),
												dataUtil.getDataSetCADType(DatasetType), fileName, currfileWithPathName,
												doc, formatfiles);
										fwriterCadDrawingDataSetDetails.write(outData + "\n");
									}
								}
							} else {
								// No drawing references - process as standalone
								String outData = dataUtil.getCADNonGenericDetails(currentIteration,
										currentIteration.getVersionDisplayIdentifier().toString());
								fwriterCadIndividualDrawingDetails.write(outData + "\n");

								// ADD AS STORED RELATIONSHIP METADATA ONLY FOR ASSEMBLIES
								if (whatItemType.equalsIgnoreCase("ASM")) {
									try {
										String asStoredRelationships = dataUtil
												.getCADModelDetailsWithAsStoredMetadata(currentIteration, false);
										if (asStoredRelationships != null && !asStoredRelationships.trim().isEmpty()) {
											// Extract only the relationship metadata part
											String[] relationshipLines = asStoredRelationships.split("\n");
											for (String line : relationshipLines) {
												if (line.startsWith("RELATIONSHIP|")) {
													fwriterCadIndividualDrawingDetails
															.write("# AS_STORED_" + line + "\n");
												}
											}
										}
									} catch (Exception e) {
										System.err.println("Error getting As Stored relationships for assembly "
												+ currentIteration.getNumber() + ": " + e.getMessage());
									}
								}

								String DSData = CADDataExtractionUtility.getEPMDrawingDataSetDetails(currentIteration,
										currentIteration.getVersionDisplayIdentifier().toString(),
										dataUtil.getDataSetCADType(DatasetType), fileName, currfileWithPathName, null,
										formatfiles);
								fwriterCadIndividualDrawingDataSetDetails.write(DSData + "\n");
							}

							// Clean up
							appData = null;
						}
					}
					// END processing current version
				}
			}

			fwriterCadDrawingDataSetDetails.flush();
			fwriterCadDrawingDataSetDetails.close();
			fwriterCadIndividualDrawingDetails.flush();
			fwriterCadIndividualDrawingDetails.close();
			fwriterCadIndividualDrawingDataSetDetails.flush();
			fwriterCadIndividualDrawingDataSetDetails.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return localQueryResult;
	}

	public static String getEPMDrawingDataSetDetails(EPMDocument dataObj, String tcVersion, String itemType,
			String fileName, String filePath, EPMDocument AttachedTo, QueryResult Formatfiles) throws Exception {

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
		String AttachedToPart = new String();
		String eol = "EOL";
		String tcState = "X";
		String strSep = "|";
		String FormatFilelist = "";
		if (Formatfiles != null) {
			while (Formatfiles.hasMoreElements()) {
				EPMDocument formatfile = (EPMDocument) Formatfiles.nextElement();
				FormatFilelist = FormatFilelist + formatfile.getNumber() + strSep;
			}
			if (Formatfiles.size() == 0) {
				FormatFilelist = FormatFilelist + "-" + strSep + "-" + strSep + "-" + strSep + "-" + strSep;
			} else if (Formatfiles.size() == 1) {
				FormatFilelist = FormatFilelist + "-" + strSep + "-" + strSep + "-" + strSep;
			} else if (Formatfiles.size() == 2) {
				FormatFilelist = FormatFilelist + "-" + strSep + "-" + strSep;
			} else if (Formatfiles.size() == 3) {
				FormatFilelist = FormatFilelist + "-" + strSep;
			}
		}
		// System.out.println("FormatFiles"+FormatFilelist);
		objNumber = dataObj.getNumber();
		if (AttachedTo != null) {
			AttachedToPart = AttachedTo.getNumber();
		}
		objVersion = dataObj.getVersionIdentifier().getValue() + "." + dataObj.getIterationIdentifier().getValue();
		objModifyDate = dateFormat.format(PersistenceHelper.getModifyStamp(dataObj));
		objState = dataObj.getState().getState().getDisplay();
		objCreatedDate = dateFormat.format(PersistenceHelper.getCreateStamp(dataObj));
		objLastModified = dataObj.getModifierFullName();
		objOwner = dataObj.getCreatorFullName();

		objGroup = "dba";
		objLastModified = "migration_user";
		objOwner = "migration_user";
		Typeofdoc = itemType;

		boolean isFirstVesrion = wt.vc.VersionControlHelper.service.isFirstIteration((wt.vc.Iterated) dataObj);
		if (!isFirstVesrion) {
			objCreatedDate = objModifyDate;
		}
		// #DatasetType|DatasetName|AttachedTo|Filepath|Filename|Rev|Rel_status|Date_created|
		// Modified_Date|owner|Last_mod_user|Group|TYPE_OF_DOC|FormatFile1|FormatFile2|FormatFile3|FormatFile4|EOL

		outData = itemType + strSep + fileName + strSep + AttachedToPart + strSep + filePath + strSep + fileName
				+ strSep + tcVersion + strSep + tcState + strSep + objCreatedDate + strSep + objModifyDate + strSep
				+ objOwner + strSep + objLastModified + strSep + objGroup + strSep + Typeofdoc + strSep + FormatFilelist
				+ eol;

		return outData;

	}
}