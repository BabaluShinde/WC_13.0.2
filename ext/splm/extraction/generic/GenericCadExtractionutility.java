package ext.splm.extraction.generic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ext.splm.extraction.nongeneric.dataUtil;
import ext.splm.extraction.nongeneric.docDataTCExtractionHelper;
import wt.content.ApplicationData;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMFamily;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteMethodServer;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class GenericCadExtractionutility {

	/**
	 * @param args
	 */
	@SuppressWarnings("unused")
	private static final String txtSep = "|";
	private static DateFormat dateFormat = new SimpleDateFormat(
	"dd-MMM-yyyy hh:mm");// yyyy_MM_dd_HHmmss
	private static Calendar cal = Calendar.getInstance();
	private static final String USER_NAME = "wcadmin";
	private static final String USER_PWD = "wcadmin";
	private static File folderDir = null;
	private static String baseDir = "C:" + File.separator + "cad_contents";
	static ArrayList skipEPM = new ArrayList(); // Skip list for problematic documents

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Date lv_start = new Date();

		// java ext.cms.mr.epm.dataTCGenericDetails wcadmin wcadmin asm false
		// java ext.cms.mr.epm.dataTCGenericDetails wcadmin wcadmin prt false
		// java ext.cms.mr.epm.dataTCGenericDetails wcadmin wcadmin drw false
		// #Generic|GRev|pe_subtypes|Instance|IRev|pe_subtypes|
		// 000128|001|Generic|000166|001|Instance|
		// 000128|001|Generic|000167|001|Instance|

		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		String strUserName = new String();
		String strUserPass = new String();

		// // if (args.length == 3) {
		// //
		// // strUserName = args[0];
		// // strUserPass = args[1];
		// // whatCADData = args[2];
		// //
		// // } else
		// // throw new Exception(
		// //
		// "\n\n_______________________________________________________________________\n\n "
		// // + "Usage : java ext.cms.mr.doc.docDataTCExtraction \n\n "
		// // + "	UserName  : Admin User ID \n "
		// // + "	Password  : Admin Password \n "
		// // + "	whatCADData  :\n\t asm or prt or drw"
		// // +
		// //
		// "\n\n_______________________________________________________________________\n");
		// //
		// // whatCADData = whatCADData.trim().toUpperCase();
		//
		// // rms.setUserName(strUserName);
		// // rms.setPassword(strUserPass);

		// rms.setUserName("wcadmin");
		// rms.setPassword("wcadmin");

		// 712-00-03.ASM
		/*
		 * QuerySpec qs = new QuerySpec(EPMSepFamilyTableMaster.class);
		 * qs.appendWhere(new SearchCondition(EPMSepFamilyTableMaster.class,
		 * EPMSepFamilyTableMaster.NAME, SearchCondition.EQUAL,
		 * "712-00-03.asm"), new int[] { 0, 1 }); QueryResult localQueryResult =
		 * PersistenceHelper.manager .find((StatementSpec) qs);
		 * System.out.println(localQueryResult.size()); while
		 * (localQueryResult.hasMoreElements()) { EPMSepFamilyTableMaster
		 * masterepm = (EPMSepFamilyTableMaster) localQueryResult
		 * .nextElement(); EPMSepFamilyTable latestepm = (EPMSepFamilyTable)
		 * wt.vc.VersionControlHelper.service
		 * .allVersionsOf(masterepm).nextElement();
		 * System.out.println(masterepm.getName());
		 * 
		 * }
		 */

		strUserName = args[0];
		strUserPass = args[1];

		rms.setUserName(strUserName);
		rms.setPassword(strUserPass);

		String whatType = args[2].toString().trim();

		ArrayList result = dataUtil.getAllFamilyGeneric(whatType.toLowerCase());
		// for (int k = 0; k < result.size(); k++) {
		// System.out.println((k + 1) + " of " + result.size() + " == "
		// + result.get(k));
		// }
		// System.out.println("\n\n");
		boolean needContent = false;
		needContent = Boolean.parseBoolean(args[3].toString().trim());
		// result.clear();
		// result.add("531-00-03.ASM"); // Initiall G then Normal
		// result.add("913-10-08.ASM");
		// result.add("724-01-47.ASM"); // intially G then I and Normal
		// result.add("873-00-05.ASM");
		// result.add("510-00-11.ASM");
		// result.add("500-00-46.ASM");
		// result.add("570-10-03.ASM");

		getEPMMasterByCADType(whatType, result, needContent);
		System.out.println("Start Time :: " + lv_start);
		System.out.println("End   Time :: " + new Date());

	}

	@SuppressWarnings("deprecation")
	public static QueryResult getEPMMasterByCADType(String whatType,
			ArrayList genericList, boolean canDownload) throws Exception {

		QueryResult localQueryResult = new QueryResult();
		EPMDocumentMaster masterepm = null;
		EPMDocument epmObj = null;
		String tcVersion = new String();

	// Use extraction directory for CSV files, cad_contents for actual CAD files
	String outputDir = getExtractionDirectory();
	System.out.println("Using CSV output directory: " + outputDir);
	
	String createItemFileName = outputDir + File.separator
		+ "Generic_CAD_Item_Metadata_" + whatType.toUpperCase()
		+ ".csv";
	String cadDataSetFileName = outputDir + File.separator
		+ "Generic_CAD_Dataset_Metadata_" + whatType.toUpperCase() + ".csv";
	String cadModelDataFileName = outputDir + File.separator
		+ "Generic_CAD_Model_Structure_" + whatType.toUpperCase() + ".csv";

	String cadGenInsDataFileName = outputDir + File.separator
		+ "Generic_CAD_Instance_Relationships_" + whatType.toUpperCase()
		+ ".csv";
	String createInstanceItemFileName = outputDir + File.separator
		+ "Instance_CAD_Item_Metadata_" + whatType.toUpperCase()
		+ ".csv";
	String createInstanceDataSetFileName = outputDir + File.separator
		+ "Instance_CAD_Dataset_Metadata_" + whatType.toUpperCase()
		+ ".csv";

	// Ensure output files and directories exist
	ensureFileExists(createItemFileName);
	ensureFileExists(cadDataSetFileName);
	ensureFileExists(cadModelDataFileName);
	ensureFileExists(cadGenInsDataFileName);
	ensureFileExists(createInstanceItemFileName);
	ensureFileExists(createInstanceDataSetFileName);

	BufferedWriter fwriterCadDetails = new BufferedWriter(new FileWriter(
			new File(createItemFileName)));
	BufferedWriter fwriterCadModelDetails = new BufferedWriter(
			new FileWriter(new File(cadModelDataFileName)));
	BufferedWriter fwriterCadDataSetDetails = new BufferedWriter(
			new FileWriter(new File(cadDataSetFileName)));
	BufferedWriter fwriterCadGenInsDetails = new BufferedWriter(
			new FileWriter(new File(cadGenInsDataFileName)));
	BufferedWriter fwriterCadInstanceDataSetDetails = new BufferedWriter(
			new FileWriter(new File(createInstanceDataSetFileName)));
	BufferedWriter fwriterCadInstanceItemDetails = new BufferedWriter(
			new FileWriter(new File(createInstanceItemFileName)));

		fwriterCadDetails
		.write("#ItemType|ItemID(Number)|RevID(REv andversion)|Description|ModifiedDate|Rel_status(State)|Date_created|Last_mod_user|owner|Item_Name|Group|APPROVED_BY|CHECKED_BY|DRW_STATUS|MATERIAL|ASSY_NO|WEIGHT|REMARKS|Supplier|surfacearea|description|mfgproces|drawingref|surfacefinish|WCVersion|TC State|EOL\n");
		fwriterCadModelDetails
		.write("#Level|ItemRev ID|Rev|QTY|Seq|Occ|UOM|Item Type\n");
		fwriterCadDataSetDetails
		.write("#DatasetType|DatasetName|AttachedTo|Filepath|Filename|Rev|Rel_status|Date_created|Modified_Date|owner|Last_mod_user|Group|TYPE_OF_DOC|EOL\n");
		fwriterCadGenInsDetails
		.write("#Generic|GRev|pe_subtypes|Instance|IRev|pe_subtypes|\n");
		fwriterCadInstanceDataSetDetails
		.write("#DatasetType|DatasetName|AttachedTo|Filepath|Filename|Rev|Rel_status|Date_created|Modified_Date|owner|Last_mod_user|Group|TYPE_OF_DOC|EOL\n");
		fwriterCadInstanceItemDetails
		.write("#ItemType|ItemID(Number)|RevID(REv andversion)|Description|ModifiedDate|Rel_status(State)|Date_created|Last_mod_user|owner|Item_Name|Group|APPROVED_BY|CHECKED_BY|DRW_STATUS|MATERIAL|ASSY_NO|WEIGHT|REMARKS|Supplier|surfacearea|description|mfgproces|drawingref|surfacefinish|WCVersion|TC State|EOL\n");
		
		// Initialize skip list for problematic documents
		skipEPM.add("SK-08-108.ASM");
		skipEPM.add("SK-08-108.DRW");
		skipEPM.add("BATT_PACK_HRNS.PRT");
		skipEPM.add("JAWA44.ASM");
		
		int totalGeneric = genericList.size();
		try {
			for (int k = 0; k < totalGeneric; k++) {
				QuerySpec qs = new QuerySpec(EPMDocumentMaster.class);

				qs.appendWhere(new SearchCondition(EPMDocumentMaster.class,
						EPMDocumentMaster.NUMBER, "=", genericList.get(k)
						.toString()), new int[] { 0, 1 });

				localQueryResult = PersistenceHelper.manager
				.find((StatementSpec) qs);

				while (localQueryResult.hasMoreElements()) {
					masterepm = (EPMDocumentMaster) localQueryResult.nextElement();
					
					// Get ALL versions of the EPMDocument instead of just latest
					wt.fc.QueryResult allVersionsQr = wt.vc.VersionControlHelper.service
							.allVersionsOf(masterepm);
					
					System.out.println((k + 1) + " of " + totalGeneric + " | "
							+ masterepm.getNumber() + " | Total Versions: " + allVersionsQr.size());
					
					// Process ALL versions of this EPMDocument
					while (allVersionsQr.hasMoreElements()) {
						EPMDocument currentVersion = (EPMDocument) allVersionsQr.nextElement();
						
						// Include family table parts - only check skip list
						boolean shouldProcess = !(skipEPM.contains(currentVersion.getNumber().toUpperCase()));
						
						// Debug info - print family table status for analysis
						System.out.println("  Version: " + currentVersion.getVersionDisplayIdentifier()
							+ " | FamilyTableStatus: " + currentVersion.getFamilyTableStatus()
							+ " | Will Process: " + shouldProcess);

						if (shouldProcess) {
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
								
								System.out.println("    Processing LATEST Iteration: " + currentIteration.getVersionDisplayIdentifier()
										+ "." + currentIteration.getIterationIdentifier().getValue()
										+ " | State: " + currentIteration.getState().getState().getDisplay()
										+ " | Family Status: " + currentIteration.getFamilyTableStatus());
								
								epmObj = currentIteration;

								// Dynamic state-based folder path (not hardcoded to "released")
								String versionState = currentIteration.getState().getState().getDisplay().toLowerCase();
								String folderPath = dataUtil.buildFolderPath(currentIteration,
										baseDir, versionState);

								// Write item details for current iteration
								String outData = dataUtil.getCADNonGenericDetails(
										currentIteration, currentIteration.getVersionDisplayIdentifier().toString());
								fwriterCadDetails.write(outData + "|" + versionState + "|\n");

								// ADD AS STORED RELATIONSHIP METADATA ONLY FOR ASSEMBLIES
								if (whatType.equalsIgnoreCase("ASM")) {
									try {
										String asStoredRelationships = dataUtil.getCADModelDetailsWithAsStoredMetadata(currentIteration, false);
										if (asStoredRelationships != null && !asStoredRelationships.trim().isEmpty()) {
											// Extract only the relationship metadata part
											String[] relationshipLines = asStoredRelationships.split("\n");
											for (String line : relationshipLines) {
												if (line.startsWith("RELATIONSHIP|")) {
													fwriterCadDetails.write("# AS_STORED_" + line + "\n");
												}
											}
										}
									} catch (Exception e) {
										System.err.println("Error getting As Stored relationships for assembly " + currentIteration.getNumber() + ": " + e.getMessage());
									}
								}

								// GET CAD MODEL Details Start (Only for assemblies)
								String docType = "" + currentIteration.getDocType();
								if (docType.equalsIgnoreCase("CADASSEMBLY")) {
									String bomData = dataUtil.getCADModelDetails(currentIteration, false);
									fwriterCadModelDetails.write(bomData);
								}

								// GET DATA SET DETAILS START
								String fileName = new String();
								ApplicationData appData = dataUtil.getEPMAppData(currentIteration);
								fileName = dataUtil.getEPMFileName(currentIteration, appData);
								String DatasetType = fileName.substring(
										fileName.lastIndexOf(".") + 1,
										fileName.length()).toLowerCase();

							if (canDownload) {
								folderDir = new File(folderPath);
								folderDir.mkdirs();
							}

							String currfileWithPathName = folderPath
									+ File.separator + fileName;

							if (canDownload) {
								Class[] classprms = { ApplicationData.class,
										String.class };
								Object[] objectprms = { appData,
										currfileWithPathName };
								RemoteMethodServer.getDefault().invoke(
										"writeInFolderLocation",
										docDataTCExtractionHelper.class
												.getName(), null, classprms,
										objectprms);
							}

								outData = dataUtil.getEPMDataSetDetails(currentIteration,
										currentIteration.getVersionDisplayIdentifier().toString(), 
										dataUtil.getDataSetCADType(DatasetType),
										fileName, currfileWithPathName);

								fwriterCadDataSetDetails.write(outData + "\n");

								// Process family table instances if they exist
								if (currentIteration.getFamilyTableStatus() == 2) {
									// This is a Generic (Family Table Master)
									outData = dataUtil.getGenInsDetails(currentIteration, false);
									fwriterCadGenInsDetails.write(outData);

									EPMFamily epmfamily = EPMFamily.getEPMFamily(currentIteration);
									if (epmfamily != null) {
										List list = epmfamily.getInstances();
										// Process all instances
										for (int i = 0; i < list.size(); i++) {
											EPMDocument variant = (EPMDocument) list.get(i);
											
											appData = dataUtil.getEPMAppData(variant);
											fileName = dataUtil.getEPMFileName(variant, appData);
											DatasetType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
											
											// Dynamic state-based folder for instances
											String instanceState = variant.getState().getState().getDisplay().toLowerCase();
											folderPath = dataUtil.buildFolderPath(variant, baseDir, instanceState);
											
											if (canDownload) {
												File folderDir = new File(folderPath);
												folderDir.mkdirs();
											}
											currfileWithPathName = folderPath + File.separator + fileName;
											
											// Download instance file if needed
											if (canDownload) {
												Class[] classprms = { ApplicationData.class, String.class };
												Object[] objectprms = { appData, currfileWithPathName };
												RemoteMethodServer.getDefault().invoke("writeInFolderLocation",
														docDataTCExtractionHelper.class.getName(), null, classprms, objectprms);
											}

											// Write instance dataset details
											outData = dataUtil.getEPMDataSetDetails(variant,
													variant.getVersionDisplayIdentifier().toString(),
													dataUtil.getDataSetCADType(DatasetType),
													fileName, currfileWithPathName);
											fwriterCadInstanceDataSetDetails.write(outData + "\n");

											// Write instance item details
											outData = dataUtil.getCADNonGenericDetails(variant,
													variant.getVersionDisplayIdentifier().toString());
											fwriterCadInstanceItemDetails.write(outData + "\n");
										}
									}
								}
							}
						} else {
							System.out.println("    No latest iteration found for revision: " 
									+ currentVersion.getVersionDisplayIdentifier());
						}
				}
				
			}

			}
			fwriterCadDetails.flush();
			fwriterCadModelDetails.flush();
			fwriterCadDataSetDetails.flush();
			fwriterCadGenInsDetails.flush();
			fwriterCadInstanceItemDetails.flush();
			fwriterCadInstanceDataSetDetails.flush();
			fwriterCadInstanceDataSetDetails.close();
			fwriterCadInstanceItemDetails.close();
			fwriterCadDetails.close();
			fwriterCadModelDetails.close();
			fwriterCadDataSetDetails.close();
			fwriterCadGenInsDetails.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	return localQueryResult;
	}

	/**
	 * Helper method to ensure a file and its parent directories exist
	 * @param filePath The full path to the file
	 * @throws Exception if file creation fails
	 */
	private static void ensureFileExists(String filePath) throws Exception {
		File file = new File(filePath);
		File parentDir = file.getParentFile();
		
		// First, check if we can write to the target directory
		if (parentDir != null) {
			if (!parentDir.exists()) {
				try {
					boolean dirsCreated = parentDir.mkdirs();
					if (!dirsCreated && !parentDir.exists()) {
						throw new Exception("Failed to create directories: " + parentDir.getAbsolutePath());
					}
					System.out.println("Created directories: " + parentDir.getAbsolutePath());
				} catch (SecurityException e) {
					throw new Exception("Permission denied creating directories: " + parentDir.getAbsolutePath() + 
							". Please run as Administrator or choose a different output location.", e);
				}
			}
			
			// Check if directory is writable
			if (!parentDir.canWrite()) {
				throw new Exception("No write permission for directory: " + parentDir.getAbsolutePath() + 
						". Please run as Administrator or choose a different output location.");
			}
		}
		
		// Create the file if it doesn't exist
		if (!file.exists()) {
			try {
				boolean fileCreated = file.createNewFile();
				if (!fileCreated) {
					throw new Exception("Failed to create file: " + file.getAbsolutePath());
				}
				System.out.println("Created output file: " + file.getAbsolutePath());
			} catch (Exception e) {
				// Check for permission-related errors
				String errorMessage = e.getMessage();
				if ((errorMessage != null && errorMessage.contains("Access is denied")) || 
					e instanceof java.security.AccessControlException) {
					throw new Exception("Permission denied creating file: " + file.getAbsolutePath() + 
							". Please run as Administrator or choose a different output location (e.g., your Documents folder).", e);
				} else {
					throw new Exception("Failed to create file: " + file.getAbsolutePath() + ". " + 
							(errorMessage != null ? errorMessage : e.getClass().getSimpleName()), e);
				}
			}
		} else {
			System.out.println("Output file already exists: " + file.getAbsolutePath());
		}
	}
	
	/**
	 * Gets the extraction directory for CSV output files
	 * @return String path to the extraction directory (C:\extraction)
	 */
	private static String getExtractionDirectory() {
		// Use C:\extraction as the main output directory for CSV files
		String extractionDir = "C:" + File.separator + "extraction";
		
		try {
			File extractionFolder = new File(extractionDir);
			if (!extractionFolder.exists()) {
				boolean created = extractionFolder.mkdirs();
				if (created) {
					System.out.println("Created extraction directory: " + extractionDir);
				} else {
					System.out.println("Failed to create extraction directory: " + extractionDir);
				}
			} else {
				System.out.println("Using existing extraction directory: " + extractionDir);
			}
			
			// Test write permissions
			if (extractionFolder.canWrite()) {
				return extractionDir;
			} else {
				System.err.println("No write permission for extraction directory: " + extractionDir);
				throw new Exception("No write permission for extraction directory");
			}
			
		} catch (Exception e) {
			System.err.println("Cannot use extraction directory " + extractionDir + ": " + e.getMessage());
			// Fallback to user's current directory
			String fallbackDir = System.getProperty("user.dir");
			System.out.println("Using fallback directory for CSV files: " + fallbackDir);
			return fallbackDir;
		}
	}
}
