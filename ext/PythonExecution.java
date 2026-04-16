package ext;
 
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public class PythonExecution {
 
    private static final Logger LOGGER = LoggerFactory.getLogger(PythonExecution.class);
 
    public static void main(String[] args) {
        // Path to Python executable
        final String pythonExe = "C:\\Users\\babal\\AppData\\Local\\Programs\\Python\\Python314\\python.exe";
 
        // Path to your Python script
        final String pythonScriptPath = "C:\\SPLM\\CWG\\Engineering_Drawing_Consolidator\\Engineering_Drawing_Consolidator_Copy_SwiftPLM\\scripts\\main.py";
 
        // Argument to pass (Revision Initials) – example
        final String revBy = "C";
 
        // Working directory (project root)
        final File workDir = new File("C:\\SPLM\\CWG\\Engineering_Drawing_Consolidator\\Engineering_Drawing_Consolidator_Copy_SwiftPLM");
 
        // Optional: overall timeout to avoid stuck processes
        final long timeoutMinutes = 10;
 
        try {
            // Build command: add "-u" for unbuffered stdout/stderr so print() is immediate
            ProcessBuilder pb = new ProcessBuilder(
                pythonExe,
                "-u",                 // <-- unbuffered mode (critical for logs)
                pythonScriptPath,
                revBy
            );
 
            // Set working directory
            pb.directory(workDir);
 
            // Environment: ensure Python can import packages and logs are unbuffered & UTF-8
            pb.environment().put("PYTHONPATH", workDir.getAbsolutePath());
            pb.environment().put("PYTHONUNBUFFERED", "1");      // redundant with -u but safe
            pb.environment().put("PYTHONIOENCODING", "utf-8");
 
            // Merge stderr into stdout so we capture everything in one stream
            pb.redirectErrorStream(true);
 
            // Log the command we’re about to run
            LOGGER.info("Starting Python: exe='{}', script='{}', arg='{}', cwd='{}'",
                    pythonExe, pythonScriptPath, revBy, workDir.getAbsolutePath());
 
            // Start process
            Process process = pb.start();
 
            // Read merged output and log to MethodServer.log via SLF4J
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    LOGGER.info("[python] {}", line);
                }
            }
 
            // Wait for completion with timeout
            boolean finished = process.waitFor(timeoutMinutes, TimeUnit.MINUTES);
            if (!finished) {
                // If it didn't finish, destroy the process and report
                process.destroyForcibly();
                LOGGER.error("Python script timed out after {} minutes and was terminated.", timeoutMinutes);
                System.out.println("Python script timed out."); // optional
                return;
            }
 
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                LOGGER.info("Python script exited with code {}", exitCode);
                System.out.println("Python script exited with code " + exitCode);
            } else {
                LOGGER.error("Python script exited with non-zero code {}", exitCode);
                System.err.println("Python script exited with non-zero code " + exitCode);
            }
 
        } catch (Exception e) {
            // Log exception stack to MethodServer.log
            LOGGER.error("Error running Python script", e);
            e.printStackTrace(); // optional: also write to System.err
        }
    }
}