import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

class DataFile {

    // singleton data file object
    private static DataFile instance = null;
    // maximum number of record that can be created
    private static int maxNumberOfRecords = 20;
    // length of record
    private static int lengthOfRecord = 65;
    // length of page
    private static int lengthOfPage = 325;
    // number of record per page
    private static int numberOfRecordPerPage = 5;
    // number of maximum page
    private static int numberOfPage = 4;

    /**
     * Data File constructer, it is singleton
     */
    private DataFile() {}

    /**
     * methot to get the singleton object
     * @return singleton object
     */
    static DataFile getInstance() {
        if(instance == null) instance = new DataFile();
        return instance;
    }

    /**
     * Makes a string 8 character long. It fills it with # symbol
     * @param s string to be extended
     * @return extended string
     */
    private String makeEightCharLong(String s) {
        while (s.length() != 8) s = s.concat("#");
        return s;
    }

    /**
     * Prints the records
     * @param typeName typeName of the record
     * @param subChunk one record that is read from file
     * @throws IOException
     */
    private void printRecords(String typeName, String subChunk, PrintWriter pw) throws IOException {
        // get the number of fileds of the given type name
        int numberOfFileds = SystemCatalog.getInstance().getNumberOfField(typeName);
        // print all fields of that record
        for (int j = 0; j < numberOfFileds; j++)
            pw.print(deleteSharps(subChunk.substring(17 + j * 8, 25 + j * 8)) + ' ');
        pw.println();
    }

    /**
     * Deletes # symbol from a field
     * @param substring field that will be cleared from #'s
     * @return new string without #'s
     */
    private String deleteSharps(String substring) {
        for (int i = 0; i < substring.length(); i++)
            if (substring.charAt(i) == '#')
                return substring.substring(0,i);
        return null;
    }

    /**
     * Creates a string builder that is 65 char long.
     * First bit is 0 or 1
     * Next 8 bits is the name of the type
     * Next 56 bits are the fields of that type
     * @param typeName name of the type taht will be created
     * @param fields fields of the type
     * @return String Builder that is 65 char long
     */
    private StringBuilder createSBToWrite(String typeName, ArrayList<String> fields) {
        StringBuilder s = new StringBuilder();
        s.append("1").append(makeEightCharLong(typeName)).append(makeEightCharLong("" + fields.size()));
        for (String field : fields) s.append(makeEightCharLong(field));
        return s;
    }

    /**
     * Reads data from given file, starting from the offset
     * Reads 325 byte in each call, it makes it 5 records which is 1 page in my design
     * @param raf file that will be read
     * @return next 325 bits
     * @throws IOException
     */
    private StringBuilder getChunk(RandomAccessFile raf, int offset) throws IOException {
        StringBuilder chunk = new StringBuilder();
        int lengthOfChunk = maxNumberOfRecords/numberOfPage*lengthOfRecord; // 325

        // reads next 325 data, which is 1 page
        raf.seek(offset);
        String str = "";
        for (int i = 0; i < lengthOfChunk; i++)
            str += (char) raf.readByte();
        chunk.append(str);
        return chunk;
    }

    /**
     * Deletes all data that is related with a type
     * @param typeName type whose data will be deleted
     */
    void deleteDataOfType(String typeName){
        String command = "ls | grep -e " + typeName + "\\$[0-9] | xargs rm";
        String[] args = new String[] {"/bin/bash", "-c", command, "with", "args"};
        try {
            Process proc = new ProcessBuilder(args).start();
        } catch (IOException e) {
            System.out.println("asd");
            e.printStackTrace();
        }
    }

    /**
     * Searches all files of the given type and finds a proper position to use in write operation
     * @param raf file that will be written
     * @param s string that will be written to the file
     * @throws IOException
     */
    private void findAnEmptyRecordAndWrite(RandomAccessFile raf, StringBuilder s) throws IOException {
        for (int k = 0; k < numberOfPage; k++) {
            // gets all pages one by one
            StringBuilder chunk = getChunk(raf,2+k*lengthOfPage);
            // there are 5 records in each pages, traverse each of them
            for (int i = 0; i < numberOfRecordPerPage; i++) {
                String subChunk = chunk.substring(i*65,(i+1)*65);
                // if the record is full skip this record
                if (subChunk.charAt(0) == '1')
                    continue;
                // move curser to the proper position
                raf.seek(2+k*(numberOfRecordPerPage*lengthOfRecord) + i*lengthOfRecord);
                // write data to the file
                raf.writeBytes(s.toString());
                return;
            }
        }
    }

    /**
     * Gets all data files of given type
     * Uses terminal command(grep) to list all file names
     * @param typeName name of the type that is searched
     * @return list of data files
     * @throws IOException
     */
    private ArrayList<String> getAllDataFiles(String typeName) throws IOException {
        String command = "ls | grep -e " + typeName + "\\$[0-9]";
        String[] args = new String[] {"/bin/bash", "-c", command, "with", "args"};
        Process process = new ProcessBuilder(args).start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        ArrayList<String> output = new ArrayList<>();
        String line = null;
        while ( (line = reader.readLine()) != null) output.add(line);
        return output;
    }

    /**
     * Searches all files of given type and finds an empty one to be used in write operation
     * @param recordFiles all files of the given type
     * @param typeName name of the type
     * @return data file that has an empty record slot
     * @throws IOException
     */
    private String findProperRecordFile(ArrayList<String> recordFiles,String typeName) throws IOException {
        for (int i = 0; i < recordFiles.size(); i++) {
            RandomAccessFile raf = new RandomAccessFile(recordFiles.get(i), "rw");
            char a = (char) raf.readByte();
            char b = (char) raf.readByte();
            if (a != '2' || b != '0') return recordFiles.get(i);
        }

        StringBuilder temp = new StringBuilder();
        String newFileName = typeName + "$" + (recordFiles.size()+1) + ".txt";
        temp.append("00");
        for (int i = 0; i < 1300; i++) temp.append('#'); // maxNumberOfRecord*lengthOfARecord = 20*65
        new File(newFileName).createNewFile();
        Files.write(Paths.get(newFileName), temp.toString().getBytes(), StandardOpenOption.APPEND);

        return newFileName;
    }

    /**
     * Increases the counter of the file that the record is added
     * @param raf file to be processed
     * @throws IOException
     */
    private void increaseRecordCounter(RandomAccessFile raf) throws IOException {
        String str = "";
        raf.seek(0);
        str += (char) raf.readByte();
        str += (char) raf.readByte();
        int val = Integer.parseInt(str);
        val++;
        raf.seek(0);
        if(val < 10) str = "" + 0 + val;
        else str = "" + val;
        raf.writeBytes(str);
    }

    /**
     * Decreases the counter of the file that the record is added
     * @param raf file to be processed
     * @throws IOException
     */
    private void decreaseRecordCounter(RandomAccessFile raf) throws IOException {
        String str = "";
        raf.seek(0);
        str += (char) raf.readByte();
        str += (char) raf.readByte();
        int val = Integer.parseInt(str);
        val--;
        raf.seek(0);
        if(val < 10) str = "" + 0 + val;
        else str = "" + val;
        raf.writeBytes(str);
    }

    /**
     * Creates a record of the given type
     * @param typeName type of the record will be created
     * @param fields fields of the record
     * @param pw print writer to be used in output
     * @throws IOException
     */
    void createRecord(String typeName, ArrayList<String> fields, PrintWriter pw) throws IOException {

        ArrayList<String> dataFiles = getAllDataFiles(typeName);

        if(searchForARecord(typeName, fields.get(0), pw, false)) return;

        String dataFile = findProperRecordFile(dataFiles,typeName);

        RandomAccessFile raf = new RandomAccessFile(dataFile, "rw");
        StringBuilder s = createSBToWrite(typeName,fields);
        findAnEmptyRecordAndWrite(raf,s);
        increaseRecordCounter(raf);
    }

    /**
     * Searches for a record in all files
     * @param typeName type name of the record that will be searched
     * @param primaryKey first field of the record
     * @param pw print writer to be used in output
     * @param willPrint boolean value to show whether the output will be printed or not
     * @return  true if searched record exists, false otherwise
     * @throws IOException
     */
    boolean searchForARecord(String typeName, String primaryKey, PrintWriter pw, boolean willPrint) throws IOException {
        ArrayList<String> dataFiles = getAllDataFiles(typeName);
        for (int i = 0; i < dataFiles.size(); i++)
            if(searchForARecordInAFile(dataFiles.get(i), typeName, primaryKey, pw, willPrint))
                return true;
        return false;
    }

    /**
     * Searches for a record in a files
     * @param fileName name of the file that will be scanned
     * @param typeName type name of the record that will be searched
     * @param primaryKey first field of the record
     * @param pw print writer to be used in output
     * @param willPrint boolean value to show whether the output will be printed or not
     * @return true if searched record exists, false otherwise
     * @throws IOException
     */
    private boolean searchForARecordInAFile(String fileName, String typeName, String primaryKey, PrintWriter pw, boolean willPrint) throws IOException {
        // checks whether a file with the given type name exists or not
        if(new File(fileName).exists()){
            RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
            for (int k = 0; k < numberOfPage; k++) {
                // gets all pages one by one
                StringBuilder chunk = getChunk(raf,2+k*lengthOfPage);
                // there are 5 records in each pages, traverse each of them
                for (int i = 0; i < numberOfRecordPerPage; i++) {
                    String subChunk = chunk.substring(i*65,(i+1)*65);
                    // if the record is not full skip this record
                    if (subChunk.charAt(0) != '1')
                        continue;
                    // if the record correspond to the type that is searched
                    if (subChunk.substring(17,25).equals(makeEightCharLong(primaryKey))) {
                        // print its fields
                        if(willPrint) printRecords(typeName,subChunk,pw);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Lists all records of the given type
     * @param typeName type name of the record that will be searched
     * @param pw print writer to be used in output
     * @throws IOException
     */
    void listAllRecords(String typeName, PrintWriter pw) throws IOException {
        ArrayList<String> dataFiles = getAllDataFiles(typeName);
        TreeMap<Integer, String> map = new TreeMap<>();
        for (int i = 0; i < dataFiles.size(); i++)
            map.putAll(listAllRecordsInTheFile(dataFiles.get(i), typeName, pw));
        for(Map.Entry<Integer,String> entry : map.entrySet())
            printRecords(entry.getValue().substring(1,9), entry.getValue(), pw);

    }

    /**
     * Finds all records of the given type in a specific file
     * @param fileName name of the file that will be searched
     * @param typeName type name of the record that will be searched
     * @param pw print writer to be used in output
     * @return list of all records in the file
     * @throws IOException
     */
    private TreeMap<Integer, String> listAllRecordsInTheFile(String fileName, String typeName, PrintWriter pw) throws IOException {
        // map is used to sort output
        TreeMap<Integer, String> map = new TreeMap<>();

        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        for (int k = 0; k < numberOfPage; k++) {
            // gets all pages one by one
            StringBuilder chunk = getChunk(raf,2+k*lengthOfPage);
            // there are 5 records in each pages, traverse each of them
            for (int i = 0; i < numberOfRecordPerPage; i++) {
                String subChunk = chunk.substring(i*65,(i+1)*65);
                // if the record is not full skip this record
                if (subChunk.charAt(0) != '1')
                    continue;
                // print its fields

                String temp = deleteSharps(subChunk.substring(17,25));
                if (temp != null && !temp.isEmpty())
                    map.put(Integer.parseInt(temp),subChunk);
            }
        }
        return map;
    }

    /**
     * Updates the given record with the given name
     * @param typeName typeName of the record that will be updated
     * @param fields fields of the given record
     * @throws IOException
     */
    void updateRecord(String typeName, ArrayList<String> fields) throws IOException {
        ArrayList<String> dataFiles = getAllDataFiles(typeName);
        for (int j = 0; j < dataFiles.size(); j++) {
            StringBuilder s = createSBToWrite(typeName,fields);
            RandomAccessFile raf = new RandomAccessFile(dataFiles.get(j), "rw");
            for (int k = 0; k < numberOfPage; k++) {
                // gets all pages one by one
                StringBuilder chunk = getChunk(raf,2+k*lengthOfPage);
                // there are 5 records in each pages, traverse each of them
                for (int i = 0; i < numberOfRecordPerPage; i++) {
                    String subChunk = chunk.substring(i*65,(i+1)*65);
                    // if the record is not full skip this record
                    if (subChunk.charAt(0) != '1')
                        continue;
                    // if the record correspond to the type that is searched
                    if (subChunk.substring(17,25).equals(makeEightCharLong(fields.get(0)))){
                        // move curser to the proper position
                        raf.seek(2 + (k * numberOfRecordPerPage + i) * lengthOfRecord);
                        // write data to the file
                        raf.writeBytes(s.toString());
                        return;
                    }
                }
            }
        }

    }

    /**
     * Deletes a record
     * @param typeName name of the type of the record
     * @param primaryKey first field of the record
     * @throws IOException
     */
    void deleteRecord(String typeName, String primaryKey) throws IOException {
        ArrayList<String> dataFiles = getAllDataFiles(typeName);
        for (int j = 0; j < dataFiles.size(); j++) {
            RandomAccessFile raf = new RandomAccessFile(dataFiles.get(j), "rw");
            for (int k = 0; k < numberOfPage; k++) {
                // gets all pages one by one
                StringBuilder chunk = getChunk(raf, 2 + k * lengthOfPage);
                // there are 5 records in each pages, traverse each of them
                for (int i = 0; i < numberOfRecordPerPage; i++) {
                    String subChunk = chunk.substring(i * 65, (i + 1) * 65);
                    // if the record is not full skip this record
                    if (subChunk.charAt(0) != '1')
                        continue;
                    // if the record correspond to the type that is searched
                    if (subChunk.substring(17, 25).equals(makeEightCharLong(primaryKey))) {
                        // move curser to the proper position
                        raf.seek(2 + (k * numberOfRecordPerPage + i) * lengthOfRecord);
                        // write data to the file
                        raf.writeByte('0');
                        decreaseRecordCounter(raf);
                        return;
                    }
                }
            }
        }

    }

}
