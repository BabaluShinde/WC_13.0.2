package ext.cwg.load;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class LogWriter_Loader {

    private static String SUMMARY_LOG_PATH;
    private static String ERROR_LOG_PATH;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private static final Set<String> header;
    private static final Map<String, Map<String, String>> logMap = new LinkedHashMap<String, Map<String, String>>();

    static {
        header = new LinkedHashSet<String>();
        header.add("name");
        header.add("number");
        header.add("Document Created");
        header.add("Primary Content Status");
        header.add("Attachment Status");
        header.add("Lifecycle Status");
        header.add("Overall Status");

        // dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); // Set to IST

    }

    public static void getFilesLoc(String loaderPropPath) {
        // Load properties file
        Properties prop = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(loaderPropPath);
            prop.load(fis);

            SUMMARY_LOG_PATH = prop.getProperty("LOADER_SUMMARY_LOG_PATH");
            ERROR_LOG_PATH = prop.getProperty("LOADER_ERROR_LOG_PATH");

            System.out.println("SUMMARY_LOG_PATH: " + SUMMARY_LOG_PATH);
            System.out.println("ERROR_LOG_PATH: " + ERROR_LOG_PATH);

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

    }

    public static synchronized void logTaskStatus(Map<String, String> row, String task, boolean success) {
        logTaskStatus(row, task, success ? "Pass" : "Fail");
    }

    public static synchronized void logTaskStatus(Map<String, String> row, String task, String status) {
        String name = row.containsKey("name") && row.get("name") != null ? row.get("name") : "N/A";
        String number = row.containsKey("number") && row.get("number") != null ? row.get("number") : "N/A";
        String key = name + "|" + number;

        Map<String, String> taskMap = logMap.get(key);
        if (taskMap == null) {
            taskMap = new LinkedHashMap<String, String>();
            logMap.put(key, taskMap);
        }

        taskMap.put(task, status);
    }

    public static synchronized void logError(Map<String, String> row, String task, Exception e) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(ERROR_LOG_PATH, true));
            String name = row.containsKey("name") && row.get("name") != null ? row.get("name") : "N/A";
            String number = row.containsKey("number") && row.get("number") != null ? row.get("number") : "N/A";

            writer.write("name: " + name);
            writer.newLine();
            writer.write("number: " + number);
            writer.newLine();
            writer.write("task: " + task);
            writer.newLine();
            writer.write("error: " + e.toString());
            writer.newLine();

            String timestamp = dateFormat.format(Calendar.getInstance().getTime());
            writer.write("timestamp: " + timestamp);
            writer.newLine();
            writer.write("-------------------------------");
            writer.newLine();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    public static synchronized void finalizeAndWrite() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(SUMMARY_LOG_PATH));
            writer.write(join(header, "|"));
            writer.newLine();

            for (Map.Entry<String, Map<String, String>> entry : logMap.entrySet()) {
                String[] parts = entry.getKey().split("\\|", -1);
                Map<String, String> taskMap = entry.getValue();
                List<String> row = new ArrayList<String>();
                if (parts.length >= 2) {
                    row.add(parts[0]); // name
                    row.add(parts[1]); // number
                } else {
                    row.add("Unknown");
                    row.add("Unknown");
                }

                boolean overallPass = true;
                for (String task : header) {
                    if ("name".equals(task) || "number".equals(task) || "Overall Status".equals(task))
                        continue;

                    String result = taskMap.containsKey(task) && taskMap.get(task) != null ? taskMap.get(task) : "Fail";
                    row.add(result);

                    if (!"Pass".equalsIgnoreCase(result) && !"Skipped".equalsIgnoreCase(result)) {
                        overallPass = false;
                    }
                }
                row.add(overallPass ? "Pass" : "Fail");

                writer.write(join(row, "|"));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private static String join(Collection<String> list, String delimiter) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iter = list.iterator();
        while (iter.hasNext()) {
            sb.append(iter.next());
            if (iter.hasNext())
                sb.append(delimiter);
        }
        return sb.toString();
    }
}
