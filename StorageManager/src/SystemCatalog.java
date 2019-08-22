import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;

class SystemCatalog {

    // singleton system catalog object
    private static SystemCatalog instance = null;
    // maximum number of type that can be created
    private static int maxNumberOfType = 20;
    // maximum number of page in the system catalog file
    private static int numberOfPage = 4;
    // number of record per page
    private static int numberOfRecordPerPage = 5;
    // length of a single record
    private static int lengthOfRecord = 65;

    /**
     * System catalog constructer, it is singleton
     */
    private SystemCatalog() {}

    /**
     * methot to get the singleton object
     * @return singleton object
     */
    static SystemCatalog getInstance() {
        if(instance == null) instance = new SystemCatalog();
        return instance;
    }

    /**
     * Makes a string 8 character long. It fills it with # symbol
     * @param s string to be extended
     * @return extended string
     */
    private String makeEightCharLong(String s){
        while (s.length() != 8 ) s = s.concat("#");
        return s;
    }

    /**
     * Makes a eight character long string, an integer
     * @param s extended string
     * @return integer that represents the string
     */
    private int eightCharToInt(String s) {
        String temp = "";
        for (int i = 0; i < s.length(); i++)
            if(s.charAt(i) != '#')
                temp = temp + s.charAt(i);
        return Integer.parseInt(temp);
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
     * Reads data from given file.
     * reads 325 byte in each call, it makes it 5 records which is 1 page in my design
     * @param raf file that will be read
     * @return next 325 bits
     * @throws IOException
     */
    private StringBuilder getChunk(RandomAccessFile raf) throws IOException {
        StringBuilder chunk = new StringBuilder();
        int lengthOfChunk = maxNumberOfType/numberOfPage*lengthOfRecord; // 325
        byte[] readData = new byte[lengthOfChunk];
        // reads next 325 data, which is 1 page
        raf.readFully(readData);
        for (int i = 0; i < lengthOfChunk; i++)
            chunk.append("" + (char) readData[i]);
        return chunk;
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
     * Searches the given file and finds a proper position to use in write operation
     * @param raf file that will be written
     * @param s string that will be written to the file
     * @throws IOException
     */
    private void findAnEmptyRecordAndWrite(RandomAccessFile raf, StringBuilder s) throws IOException {
        for (int k = 0; k < numberOfPage; k++) {
            // gets all pages one by one
            StringBuilder chunk = getChunk(raf);
            // there are 5 records in each pages, traverse each of them
            for (int i = 0; i < numberOfRecordPerPage; i++) {
                String subChunk = chunk.substring(i*65,(i+1)*65);
                // if the record is full skip this record
                if (subChunk.charAt(0) == '1')
                    continue;
                // move curser to the proper position
                raf.seek(k*(numberOfRecordPerPage*lengthOfRecord) + i*lengthOfRecord);
                // write data to the file
                raf.writeBytes(s.toString());
                return;
            }
        }
    }

    /**
     * Checks whether the type exists or not
     * @param typeName name of the type that will be checked
     * @return whether the type exists or not
     * @throws IOException
     */
    private boolean typeAlreadyExists(String typeName) throws IOException {
        RandomAccessFile raf = new RandomAccessFile("SystemCatalog.txt", "rw");
        // make string eight character long
        typeName = makeEightCharLong(typeName);

        for (int k = 0; k < numberOfPage; k++) {
            // gets all pages one by one
            StringBuilder chunk = getChunk(raf);
            // there are 5 records in each pages, traverse each of them
            for (int i = 0; i < numberOfRecordPerPage; i++) {
                String subChunk = chunk.substring(i*65,(i+1)*65);
                // if the record is not full skip this record
                if (subChunk.charAt(0) != '1')
                    continue;
                // if the record correspond to the type that is searched, return true
                if (subChunk.substring(1,9).equals(typeName))
                    return true;
            }
        }
        return false;

    }

    /**
     * Deletes the type from System Catalog
     * @param typeName name of the type that will be deleted
     * @throws IOException
     */
    void deleteType(String typeName) throws IOException {

        RandomAccessFile raf = new RandomAccessFile("SystemCatalog.txt", "rw");
        // make string eight character long
        typeName = makeEightCharLong(typeName);

        for (int k = 0; k < numberOfPage; k++) {
            // gets all pages one by one
            StringBuilder chunk = getChunk(raf);
            // there are 5 records in each pages, traverse each of them
            for (int i = 0; i < numberOfRecordPerPage; i++) {
                String subChunk = chunk.substring(i*65,(i+1)*65);
                String tempTypeName = subChunk.substring(1,9);
                // if the type name is equal to the name of the type that will be deleted
                if(!typeName.equals(tempTypeName))
                    continue;
                // move curser to the proper position
                raf.seek(k*(numberOfRecordPerPage*lengthOfRecord) + i*lengthOfRecord);
                // write data to the file
                raf.writeByte('0');
                break;
            }
        }
    }

    /**
     * Lists all types that is currently in database
     * @throws IOException
     */
    void listAllTypes(PrintWriter pw) throws IOException {
        // list to keep all types
        ArrayList<String> allTypes = new ArrayList<>();
        RandomAccessFile raf = new RandomAccessFile("SystemCatalog.txt", "rw");

        for (int k = 0; k < numberOfPage; k++) {
            // gets all pages one by one
            StringBuilder chunk = getChunk(raf);
            // there are 5 records in each pages, traverse each of them
            for (int i = 0; i < numberOfRecordPerPage; i++) {
                String subChunk = chunk.substring(i * 65, (i + 1) * 65);
                // if the record is full, add its typename to the list
                if (subChunk.charAt(0) == '1')
                    allTypes.add(subChunk.substring(1, 9));
            }
        }
        ArrayList<String> temp = new ArrayList<>();
        // deletes the # symbols of each string in the list and sorts them
        for (int i = 0; i < allTypes.size(); i++)
            temp.add(deleteSharps(allTypes.get(i)));
        Collections.sort(temp);
        for (int i = 0; i < temp.size(); i++)
            pw.println(temp.get(i));
    }

    /**
     * Creates a type with given type name.
     * Creates a txt file with the name of created type
     * @param typeName name of the type
     * @param fields fields of the type
     * @throws IOException
     */
    void createType(String typeName, ArrayList<String> fields) throws IOException {

        // if type already exists do nothing
        if (typeAlreadyExists(typeName)) return;

        // create a file with the type name and fill it with #'s
        StringBuilder temp = new StringBuilder();
        temp.append("00");
        for (int i = 0; i < 1300; i++) temp.append('#'); // maxNumberOfRecord*lengthOfARecord = 20*65
        new File(typeName + "$1" + ".txt").createNewFile();
        Files.write(Paths.get(typeName + "$1" + ".txt"), temp.toString().getBytes(), StandardOpenOption.APPEND);

        // formats the typename and fields so that they can be written into database
        StringBuilder s = createSBToWrite(typeName,fields);
        RandomAccessFile raf = new RandomAccessFile("SystemCatalog.txt", "rw");
        // write the created string in to the file
        findAnEmptyRecordAndWrite(raf, s);
    }

    /**
     * Finds the number of fields of the given type name
     * @param typeName
     * @return number of fields of the given type name, return -1 if the type doesn't exists
     * @throws IOException
     */
    int getNumberOfField(String typeName) throws IOException {
        RandomAccessFile raf = new RandomAccessFile("SystemCatalog.txt", "rw");
        int temp = -1;
        // make string eight character long
        typeName = makeEightCharLong(typeName);

        for (int k = 0; k < numberOfPage; k++) {
            // gets all pages one by one
            StringBuilder chunk = getChunk(raf);
            // there are 5 records in each pages, traverse each of them
            for (int i = 0; i < numberOfRecordPerPage; i++) {
                String subChunk = chunk.substring(i*65,(i+1)*65);
                // if the type name is not equal to the name of the type that will be deleted, skip it
                if (!typeName.equals(subChunk.substring(1, 9)))
                    continue;
                // returns the integer value of numberOfFileds
                return eightCharToInt(subChunk.substring(9,17));
            }
        }
        return temp;
    }

}
