package ext.cwg.load;

import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import java.util.TimeZone;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.iba.definition.litedefinition.*;
import wt.iba.definition.service.StandardIBADefinitionService;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.litevalue.*;
import wt.iba.value.service.IBAValueHelper;
import wt.iba.value.service.StandardIBAValueService;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.WorkInProgressServerHelper;

public class IBAutility implements RemoteAccess {

    public static void main(String[] args) {
        RemoteMethodServer rms = RemoteMethodServer.getDefault();
        rms.setUserName("wcadmin");
        rms.setPassword("wcadmin");
        try {
            rms.invoke("remoteMethod", "ext.cwg.load.IBAutility", null, new Class[] { String.class },
                    new Object[] { args[0] });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void remoteMethod(String loaderPropPath) throws WTException, IOException {

        System.out.println("Remote method started...");

        Properties prop = new Properties();

        // Load from file
        FileInputStream fis = new FileInputStream(loaderPropPath);
        prop.load(fis);

        String excelPath = prop.getProperty("excelPath");

        LogWriter_IBA.getFilesLoc(loaderPropPath);

        List<Map<String, String>> records = ExcelFileReader.readExcel(excelPath);

        int count = 0;
        for (Map<String, String> row : records) {
            if (count >= 5) {
                break;
            }
            count++;
            String number = row.get("number");
            WTDocument doc = getDocumentByNumber(number);

            if (doc != null) {
                boolean atLeastOneSuccess = false;

                for (Map.Entry<String, String> entry : row.entrySet()) {
                    String key = entry.getKey().toLowerCase();

                    if (key.startsWith("ibaname")) {
                        String suffix = key.substring("ibaname".length());
                        String ibaName = row.get("IBAname" + suffix);
                        String ibaValue = row.get("IBAvalue" + suffix);

                        if (ibaName != null && !ibaName.trim().isEmpty()
                                && ibaValue != null && !ibaValue.trim().isEmpty()) {
                            Transaction tx = new Transaction();
                            try {
                                tx.start();
                                updateIBAValue(doc, ibaName.trim(), ibaValue.trim());
                                LogWriter_IBA.logTaskStatus(row, "IBA:" + ibaName, true);
                                atLeastOneSuccess = true;
                                tx.commit();
                            } catch (Exception e) {
                                tx.rollback();
                                LogWriter_IBA.logTaskStatus(row, "IBA:" + ibaName, false);
                                LogWriter_IBA.logError(row, "IBA:" + ibaName, e);
                            }
                        }
                    }
                }

                // Optional: mark overall IBA Update for the doc if needed
                LogWriter_IBA.logTaskStatus(row, "IBA Update", atLeastOneSuccess);

            } else {
                LogWriter_IBA.logTaskStatus(row, "IBA Update", false);
                LogWriter_IBA.logError(row, "Document Retrieval",
                        new Exception("Document not found for number: " + number));
            }
        }

        LogWriter_IBA.finalizeAndWrite();
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

    public static void updateIBAValue(IBAHolder ibaHolder, String ibaName, String ibaValueStr)
            throws RemoteException, WTException, WTPropertyVetoException {

        ibaHolder = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, null, null, null);
        StandardIBADefinitionService defService = new StandardIBADefinitionService();
        DefaultAttributeContainer attributeContainer = (DefaultAttributeContainer) ibaHolder.getAttributeContainer();
        AttributeDefDefaultView attributeDefinition = defService.getAttributeDefDefaultViewByPath(ibaName);

        AbstractContextualValueDefaultView attrValue = null;
        AbstractValueView[] abstractValueView = attributeContainer.getAttributeValues(attributeDefinition);

        boolean isReset = (ibaValueStr == null || ibaValueStr.trim().isEmpty());
        Object parsedValue;

        try {
            if (!isReset) {
                if (attributeDefinition instanceof TimestampDefView) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                    Date parsedDate = sdf.parse(ibaValueStr.trim());
                    parsedValue = new Timestamp(parsedDate.getTime());
                } else if (attributeDefinition instanceof StringDefView) {
                    parsedValue = ibaValueStr;
                } else if (attributeDefinition instanceof FloatDefView) {
                    parsedValue = Double.parseDouble(ibaValueStr.trim());
                } else if (attributeDefinition instanceof IntegerDefView) {
                    parsedValue = Long.parseLong(ibaValueStr.trim());
                } else if (attributeDefinition instanceof BooleanDefView) {
                    parsedValue = Boolean.parseBoolean(ibaValueStr.trim().toLowerCase());
                } else {
                    throw new WTException(
                            "Unsupported IBA type for: " + ibaName + " ,type: " + attributeDefinition.toString());
                }
            } else {
                return;
            }
        } catch (Exception e) {
            throw new WTException(
                    "Failed to parse value '" + ibaValueStr + "' for IBA: " + ibaName + ". Cause: " + e.toString());
        }

        if (abstractValueView.length == 0) {
            if (!isReset) {
                if (attributeDefinition instanceof TimestampDefView) {
                    attrValue = new TimestampValueDefaultView((TimestampDefView) attributeDefinition,
                            (Timestamp) parsedValue);
                } else if (attributeDefinition instanceof StringDefView) {
                    attrValue = new StringValueDefaultView((StringDefView) attributeDefinition, parsedValue.toString());
                } else if (attributeDefinition instanceof FloatDefView) {
                    attrValue = new FloatValueDefaultView((FloatDefView) attributeDefinition, (Double) parsedValue, 5);
                } else if (attributeDefinition instanceof IntegerDefView) {
                    attrValue = new IntegerValueDefaultView((IntegerDefView) attributeDefinition, (Long) parsedValue);
                } else if (attributeDefinition instanceof BooleanDefView) {
                    attrValue = new BooleanValueDefaultView((BooleanDefView) attributeDefinition,
                            (Boolean) parsedValue);
                }
                attributeContainer.addAttributeValue(attrValue);
            } else {
                return;
            }
        } else {
            AbstractValueView avv = abstractValueView[0];
            if (isReset) {
                attributeContainer.deleteAttributeValue(avv);
            } else {
                if (avv instanceof TimestampValueDefaultView) {
                    ((TimestampValueDefaultView) avv).setValue((Timestamp) parsedValue);
                } else if (avv instanceof StringValueDefaultView) {
                    ((StringValueDefaultView) avv).setValue(parsedValue.toString());
                } else if (avv instanceof FloatValueDefaultView) {
                    ((FloatValueDefaultView) avv).setValue((Double) parsedValue);
                } else if (avv instanceof IntegerValueDefaultView) {
                    ((IntegerValueDefaultView) avv).setValue((Long) parsedValue);
                } else if (avv instanceof BooleanValueDefaultView) {
                    ((BooleanValueDefaultView) avv).setValue((Boolean) parsedValue);
                }
                attributeContainer.updateAttributeValue(avv);
            }
        }

        ibaHolder.setAttributeContainer(attributeContainer);
        StandardIBAValueService.theIBAValueDBService.updateAttributeContainer(ibaHolder, null, null, null);

        WTCollection byPassIterationModifierSet = new WTHashSet();
        byPassIterationModifierSet.add(ibaHolder);
        WorkInProgressServerHelper.putInTxMapForValidateModifiable(byPassIterationModifierSet);

        PersistenceHelper.manager.save((Persistable) ibaHolder);
    }
}
