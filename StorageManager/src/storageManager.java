import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class storageManager {

    // System Catalog that we keep our meta data
    private static final SystemCatalog systemCatalog = SystemCatalog.getInstance();
    // Data Files that we keep our actual data
    private static final DataFile dataFile = DataFile.getInstance();

    /**
     * Main file of the storage manager
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        createSystemCatalogFile();
        BufferedReader br = createBufferReader(args[0]);
        PrintWriter pw = createPrintWriter(args[1]);

        // input statement is read line by line, each line is written in this variable
        String statement;
        while ((statement = br.readLine()) != null){

            // if the statement is empty skip it
            if(statement.length() == 0) continue;

            statement = trimAll(statement);

            String[] splittedStatement = statement.split(" ");

            String operation = splittedStatement[0];
            String typeOrRecord = splittedStatement[1];

            if (typeOrRecord.equals("type")) { // System Catalog operations
                if (operation.equals("create")) {
                    String typeName = splittedStatement[2];
                    int numberOfFields = Integer.parseInt(splittedStatement[3]);
                    ArrayList<String> fields = new ArrayList<>();
                    for (int i = 1; i <= numberOfFields; i++)
                        fields.add(splittedStatement[i+3]);
                    systemCatalog.createType(typeName,fields);
                } else if (operation.equals("delete")){
                    String typeName = splittedStatement[2];
                    systemCatalog.deleteType(typeName);
                    dataFile.deleteDataOfType(typeName);
                } else if (operation.equals("list")){
                    systemCatalog.listAllTypes(pw);
                }
            } else if (typeOrRecord.equals("record")){ // System Catalog operations
                String typeName = splittedStatement[2];
                switch (operation) {
                    case "delete": {
                        String primaryKey = splittedStatement[3];
                        dataFile.deleteRecord(typeName, primaryKey);
                        break;
                    }
                    case "search": {
                        String primaryKey = splittedStatement[3];
                        dataFile.searchForARecord(typeName, primaryKey, pw, true);
                        break;
                    }
                    case "list":
                        dataFile.listAllRecords(typeName, pw);
                        break;
                    default:
                        int numberOfFields = systemCatalog.getNumberOfField(typeName);
                        if (numberOfFields != -1) {
                            ArrayList<String> fields = new ArrayList<>();
                            for (int i = 1; i <= numberOfFields; i++)
                                fields.add(splittedStatement[i + 2]);
                            if (operation.equals("create"))
                                dataFile.createRecord(typeName, fields, pw);
                            else
                                dataFile.updateRecord(typeName, fields);
                            break;
                        }
                }
            }

        }
        br.close();
        pw.close();
    }
    
    private static String trimAll(String str){
        str = str.trim();
        return Arrays.stream(str.split("\\s+")).collect(Collectors.joining(" "));
    }

    /**
     * Creates Buffer Writer for output file
     * @return buffer writer
     */
    private static PrintWriter createPrintWriter(String outputFileName) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(outputFileName, "UTF-8");
        } catch (IOException e){
            System.out.println("A problem occured in creation of input.txt file");
        }
        return pw;
    }

    /**
     * Creates Buffer Reader for input file
     * @return buffer reader
     */
    private static BufferedReader createBufferReader(String inputFileName) {
        BufferedReader br = null;
        try {
            File file = new File(inputFileName);
            br = new BufferedReader(new FileReader(file));
        } catch (IOException e){
            System.out.println("A problem occured in creation of input.txt file");
        }
        return br;
    }

    /**
     * Creates SystemCatalog.txt file. If txt file already exists, do nothing
     */
    private static void createSystemCatalogFile() {
        try {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < 20*65; i++) s.append('#');
            if(new File("SystemCatalog.txt").createNewFile())
                Files.write(Paths.get("SystemCatalog.txt"), s.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("A problem occured in creation of SystemCatalog.txt file");
        }
    }

}
