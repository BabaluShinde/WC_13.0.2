package ext.cwg.load;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LogWriter_IBA {

    private static String SUMMARY_LOG_PATH = "";
    private static String ERROR_LOG_PATH = "";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//    static {
//        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
//    }

    private static final Set<String> dynamicHeaders = new LinkedHashSet<>();
    private static final Map<String, Map<String, String>> logMap = new LinkedHashMap<>();

    //  Clears existing log state (use at start of utility to ensure fresh logging)
    public static synchronized void reset() {
        logMap.clear();
        dynamicHeaders.clear();
    }
    public static void getFilesLoc(String loaderPropPath) throws IOException {

         
         Properties prop = new Properties();
    	 
      	// Load from file
           FileInputStream fis = new FileInputStream(loaderPropPath);
           prop.load(fis);
      	
           
           SUMMARY_LOG_PATH = prop.getProperty("IBA_SUMMARY_LOG_PATH");
           ERROR_LOG_PATH = prop.getProperty("IBA_ERROR_LOG_PATH");
           System.out.println("SUMMARY_LOG_PATH_logger :"+SUMMARY_LOG_PATH);
           System.out.println("ERROR_LOG_PATH_logger :"+ERROR_LOG_PATH);
         
    }

    public static synchronized void logTaskStatus(Map<String, String> row, String task, boolean success) {
        //String name = row.getOrDefault("name", "N/A");
        String name = row.containsKey("name") && row.get("name") != null ? row.get("name") : "N/A";

        //String number = row.getOrDefault("number", "N/A");
        String number = row.containsKey("number") && row.get("number") != null ? row.get("number") : "N/A";

        String key = name + "|" + number;

        //logMap.putIfAbsent(key, new LinkedHashMap<>());
        if (!logMap.containsKey(key)) {
            logMap.put(key, new LinkedHashMap<String, String>());
        }


        if (!task.equalsIgnoreCase("Overall Status") && !task.equalsIgnoreCase("IBA Update")) {
            dynamicHeaders.add(task); 
        }

        logMap.get(key).put(task, success ? "Pass" : "Fail");
    }

    public static synchronized void logError(Map<String, String> row, String task, Exception e) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(ERROR_LOG_PATH, true));
            //String name = row.getOrDefault("name", "N/A");
            String name = row.containsKey("name") && row.get("name") != null ? row.get("name") : "N/A";
            
            //String number = row.getOrDefault("number", "N/A");
            String number = row.containsKey("number") && row.get("number") != null ? row.get("number") : "N/A";

            writer.write("name: " + name);
            writer.newLine();
            writer.write("number: " + number);
            writer.newLine();
            writer.write("task: " + task);
            writer.newLine();
            writer.write("error: " + e.toString());
            writer.newLine();

            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
            String timestamp = dateFormat.format(cal.getTime());
            writer.write("timestamp: " + timestamp);
            writer.newLine();
            writer.write("-------------------------------");
            writer.newLine();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    public static synchronized void finalizeAndWrite() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(SUMMARY_LOG_PATH));

            // Header: name|number|<IBA:...>|Overall Status
            List<String> headers = new ArrayList<>();
            headers.add("name");
            headers.add("number");
            headers.addAll(dynamicHeaders);
            headers.add("Overall Status");

            writer.write(join(headers, "|"));
            writer.newLine();

            for (String docKey : logMap.keySet()) {
                Map<String, String> taskMap = logMap.get(docKey);
                String[] parts = docKey.split("\\|", 2);

                List<String> row = new ArrayList<>();
                row.add(parts[0]); // name
                row.add(parts[1]); // number

                boolean overallPass = true;

                for (String task : dynamicHeaders) {
                    //String status = taskMap.getOrDefault(task, "Fail");
                    String status = taskMap.containsKey(task) ? taskMap.get(task) : "Fail";


                    row.add(status);
                    if (!"Pass".equalsIgnoreCase(status)) {
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
            try {
                if (writer != null) writer.close();
            } catch (IOException closeEx) {
                closeEx.printStackTrace();
            }

            //  Clear log state after writing
            logMap.clear();
            dynamicHeaders.clear();
        }
    }

    private static String join(Collection<String> list, String delimiter) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iter = list.iterator();
        while (iter.hasNext()) {
            sb.append(iter.next());
            if (iter.hasNext()) sb.append(delimiter);
        }
        return sb.toString();
    }
}
