package serverSide.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * This data type represents the Configuration of the Servers of the Heist to
 * the Museum Problem.
 *
 * @author António Mota
 * @author Marcos Pires
 */
public class Config {

    private final HashMap<String, String>[] map;

    /**
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public Config() throws FileNotFoundException, IOException {
        map = readFile();
    }

    /**
     *
     * escreve aqui
     * @return  e aqui
     */
    private static HashMap[] readFile() throws FileNotFoundException, IOException {
        File currentDir = new File("src/Files/Configurations.txt");
        System.out.println(currentDir.getAbsolutePath());
        // File parentDir = currentDir.getParentFile();

        //File newFile = new File(parentDir, "/Files/Configurations.txt");
        FileReader fileReader = new FileReader(currentDir);

        BufferedReader bufferedReader = new BufferedReader(fileReader);
        HashMap<String, String> hostName = new HashMap<String, String>();
        HashMap<String, String> portName = new HashMap<String, String>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] tmp = line.split(" ");
            hostName.put(tmp[0], tmp[1]);
            portName.put(tmp[0], tmp[2]);
        }

        bufferedReader.close();
        HashMap<String, String>[] responseArray = new HashMap[]{hostName, portName};
        return responseArray;
    }

    /**
     *
     * @return e aqui
     */
    public HashMap<String, String>[] getMap() {
        return map;
    }

}
