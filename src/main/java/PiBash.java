import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by dev on 02.04.2017.
 */
public class PiBash {

    public static String execute(String command) {
        StringBuilder returnValue = new StringBuilder();
        BufferedReader in = null;
        boolean failed = false;

        if (null == command || command.length() == 0)
            return null;

        try {
            Process process = Runtime.getRuntime().exec(command);

            //Get input stream and read from it
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = in.readLine()) != null) {
                returnValue.append(line);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            failed = true;
        } finally {
            closeReader(in);
            in = null;
            return failed ? null : returnValue.toString();
        }
    }

    private static void closeReader(BufferedReader in) {
        if (null != in)
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
