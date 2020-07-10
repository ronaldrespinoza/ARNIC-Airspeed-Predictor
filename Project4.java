import java.math.*;
/**
 * CS152 Project 4 -- class with 10 custom methods
 * Requirements:
 *    Input parameters:
 *        - At least one needs to be a 1d array
 *        - At least one needs to be a 2d array
 *    Control Flow
 *        - One method must have while loop
 *        - One method must have a for loop
 *        - One method must have a nested for loop for 2d array
 *        - Three methods that are called in another method and not just from main
 *        - 1 recursive method
 *        - 1 method to change elements in array
 * Purpose:
 *    This class generates an airspeed prediction for altitude at 10250.
 *    Using the least squares method and the following preset dummy data
 *    airspeed,altimeter =
 *        {(110,10180),(100,10210),(120,10140),(105,10190),(115,10200)}
 *    This predictions is then encapsulated into an ARNIC 429 word format
 *    and displayed to the console.
 * ARINC 429 Word Format
 *    Each ARINC 429 word is a 32-bit sequence that contains five fields:
 *    1: P: 32  //Parity Bit values(0,1)
 *    2: SSM: 31,30 //Sign/Status Matrix values(00, 01, 10, 11)(+ or -)
 *    3: Data: 29 - 11 //Digits 1 - 5 in BCD format
 *    4: SDI: 10,9 //Source Destination Identifiers(00, 01, 10, 11)
 *    5: Label: 8 - 1 //Label words(octal representation)
 * Sources:
 * 1.  https://www.mathsisfun.com/data/least-squares-regression.html
 * 2.  https://www.mcico.com/resources/flight-instruments/six-pack-aircraft-instruments-explained
 * 3.  https://en.wikipedia.org/wiki/ARINC_429
 * 4.  https://www.baeldung.com/java-round-decimal-number
 * 5.  https://www.tutorialspoint.com/tail-recursion-in-data-structures
 * @author Ronald R Espinoza (UNMID: 101508826) CS152L Group 4
 * @version 1.0.1, 9 July 2020
 * @param String[] args
 * @return Void
 * @throws N/A
 * @see
 * @since 7 July 2020
 * @serial
 * @Deprecated
 */
public class Project4{

    public static final int INDEX_SIZE = 5;
    public static final int FIELD_SIZE = 19;
    public static final int WORD_SIZE = 32;
    public static final int TEST_ALTITUDE = 10195;
    public static int[] arnic429Word = new int[WORD_SIZE];
    public static int[][] arnic429Fields = new int[INDEX_SIZE][FIELD_SIZE];
    public static int[][] testData = {{110,10180},{100,10210},{120,10140},
                                     {105,10190},{115,10200}};
    /* The below test data was used for validation of least common squares */
    /* public static int[][] testData = {{2,4},{3,5},{5,7},{7,10},{9,15}}; */
    /* The result should be 12.45 see source(1) */

    /**
     * MethodName: round()
     * DataType: double
     * <p>
     * Helper method for precise decimal format control
     * </p>
     * @param double value - the value to be formatted
     * @param int places - the precision of the value
     * @see generateData()
     * @see source 4
     * @return bd - in it's requested precision
     * @author Ronald R Espinoza
     */
    public static double round(double value, int places) {
        if(places < 0){//a valid precison must be specified
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(Double.toString(value));
        //round the value up using the if greater than 5 method
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        //convert and return the big decimal to a double value
        return bd.doubleValue();
    }

    /**
     * MethodName: printPrediction()
     * DataType: void
     * <p>
     * Displays the Encoded ARNIC word to the console
     * </p>
     * @param uses the global arnic429Word variable
     * @author Ronald R Espinoza
     */
    public static void printPrediction(){
        System.out.println("The predicted ARNIC word is as follows:");
        for(int j = 0; j < WORD_SIZE; j++){
            System.out.print(arnic429Word[j]);
        }
    }

    /**
     * MethodName: getLeastSquaresRegression()
     * DataType: double
     * <p>
     * Recursively calculate step 1 and 2
     *    Step 1: For each (x,y) calculate x2 and xy:
     *    Step 2: Sum x, y, x2 and xy (gives us Σx, Σy, Σx2 and Σxy):
     * Then after the data set has been processed
     *    Step 3: Calculate Slope m:
     *    Step 4: Calculate Intercept b:
     *    Step 5: predict airspeed for TEST_ALTITUDE:
     * </p>
     * @param int[] calculations
     * @see generateData() 
     * @see source 1
     * @return double prediction - the predicted airspeed
     * @author Ronald R Espinoza
     */
    public static double getLeastSquaresRegression(int[] calculations){
        int stepsTaken = calculations[7];
        int sumX = calculations[3];
        int sumY = calculations[4];
        int sumXX = calculations[5];
        int sumXY = calculations[6];
        int xx, xy;
        double m, b, prediction, numerator, denominator;
        int n = calculations[2];
        n--;
        calculations[2] = n;
        if(n > -1){
            calculations[0] = testData[n][0];//getting previous x
            calculations[1] = testData[n][1];//getting previous y
        }
        int x = calculations[0];
        int y = calculations[1];
        if(n == -1){//data set has been fully processed
            //Step 3: Calculate Slope m:
            numerator = ((stepsTaken * sumXY) - sumX * sumY);
            denominator = ((stepsTaken * sumXX) - (sumX * sumX));
            m = numerator / denominator;
            //Step 4: Calculate Intercept b:
            numerator = ((sumY) - (m * sumX));
            denominator = stepsTaken;
            b = numerator / denominator;
            //Step 5: predict airspeed for TEST_ALTITUDE:
            /*prediction = m*x + b *///this was used for formula validation
            prediction = (TEST_ALTITUDE - b) / m;
            return prediction;
        }
        else if(n >= 0){//There is still data to process
            //Step 1: For each (x,y) calculate x2 and xy:
            xx = x*x;
            xy = x * y;
            //Step 2: Sum x, y, x2 and xy (gives us sumX, sumY, sumXX and sumXY):
            sumX += x; calculations[3] = sumX;
            sumY += y; calculations[4] = sumY;
            sumXX += xx; calculations[5] = sumXX;
            sumXY += xy; calculations[6] = sumXY;
            //recursive callback using tail recursion @see source 5
            return getLeastSquaresRegression(calculations);
        }
        else{// in an error state where default return is 0.0000
            return 0.0000;
        }
    }

    /**
     * MethodName: generateData()
     * DataType: double
     * <p>
     * Calls leastSquares to find the airspeed prediction using the testData
     * Then rounds and returns this value
     * </p>
     * @param 
     * @see getLeastSquaresRegression()
     * @return unencryptedData - The airspeed prediction
     * @author Ronald R Espinoza
     */
    public static double generateData(){
        double unencryptedData;
        int n = testData.length;
        int[] calculations = new int[] {0,0,0,0,0,0,0,0};
        calculations[7] = n;//Steps to be taken
        calculations[2] = n;//n count to be manipulated
        calculations[1] = testData[n - 1][1];//y
        calculations[0] = testData[n - 1][0];//x
        unencryptedData = getLeastSquaresRegression(calculations);
        unencryptedData = round(unencryptedData, 2);//only 5 digits are allowed
        return unencryptedData;
    }

    /**
     * MethodName: getBCDRepresentation()
     * DataType: String
     * <p>
     * Converts a single integer into a Binary Coded Display
     * </p>
     * @param int piece
     * @see encryptData()
     * @return the BCD as a string
     * @author Ronald R Espinoza
     */
    public static String getBCDRepresentation(int piece){
        switch(piece){
            case 0:  return "0000";
            case 1:  return "0001";
            case 2:  return "0010";
            case 3:  return "0011";
            case 4:  return "0100";
            case 5:  return "0101";
            case 6:  return "0110";
            case 7:  return "0111";
            case 8:  return "1000";
            case 9:  return "1001";
            default:  return "1111";//error return
        }
    }

    /**
     * MethodName: encryptData()
     * DataType: String
     * <p>
     * Converts the predicted airspeed to BCD format
     * </p>
     * @param double prediction
     * @see the string builder class
     * @return data
     * @author Ronald R Espinoza
     */
    public static String encryptData(double prediction){
        StringBuilder data = new StringBuilder();
        int[] dataPieces;
        int dataLength;
        String predictionString = Double.toString(prediction);//convert double to string
        //Then piece out the string into a character array
        dataPieces = predictionString.chars().map(Character::getNumericValue).toArray();
        dataLength = dataPieces.length;
        //Now for all the pieces get the BCD representation for digits 1 - 5
        for(int i = 0; i < dataLength; i++){
            if(dataPieces[i] != -1){//ignore the decimal point
                data.append(getBCDRepresentation(dataPieces[i]));
            }
        }
        return data.toString();
    }

    /**
     * MethodName: isOddParity()
     * DataType: boolean
     * <p>
     * Validates parity bit for proper signal
     * </p>
     * @param int parity
     * @see fillWord()
     * @return True/False
     * @author Ronald R Espinoza
     */
    public static boolean isOddParity(int parity){
        if(parity == 1){return true;}
        return false;
    }

    /**
     * MethodName: reverseWord()
     * DataType: int[]
     * <p>
     * Using recursion reverse the ARNIC 429 data
     * </p>
     * @param int[] arnic429Word
     * @param int upCount - starts at 0 and counts up till exit condition
     * @param int downCount - starts at (WORD_SIZE - 1) and counts down
     * @see fillWord()
     * @return arnic429Word
     * @author Ronald R Espinoza
     */
    public static int[] reverseWord(int[] arnic429Word, int upCount, int downCount){
        //reverse the LSB and MSB i.e. 32 is now 1 and visa versa
        if(upCount < downCount){
            int temp = arnic429Word[upCount];//storing LSB
            arnic429Word[upCount] = arnic429Word[downCount];//switching MSB and LSB
            arnic429Word[downCount] = temp;//resetting MSB
            return reverseWord(arnic429Word, ++upCount, --downCount);
        }
        return arnic429Word;
    }

    /**
     * MethodName: setFields()
     * DataType: int[][]
     * <p>
     * Sets all arnic429Fields according to their index
     * </p>
     * @param String the Data
     * @see encryptData()
     * @return arnic429Fields
     * @author Ronald R Espinoza
     */
    public static int[][] setFields(String theData){
        int dataCount, reverseCount = 0;
        //setting parity bit
        arnic429Fields[0][0] = 1;//usually odd
        //setting sign status
        arnic429Fields[1][0] = 0;//having 00 means value is positive
        arnic429Fields[1][1] = 0;
        //filling the data field with the encrypted BCD values
        for(dataCount = FIELD_SIZE - 1; dataCount > -1; dataCount--){
            arnic429Fields[2][reverseCount] = Character.getNumericValue(
                                              theData.charAt(FIELD_SIZE - reverseCount)
                                              );//walks backwards in theData
            reverseCount++;//this truncates the MSB(DIGIT 1) to be less than 7
        }
        //setting SDI 
        arnic429Fields[3][0] = 0;//if 11 then format is BNR
        arnic429Fields[3][1] = 0;
        //Setting the label to permanently be velocity
        for(dataCount = 0; dataCount < 8; dataCount++){
            if(dataCount < 4){//controls the label to be 366 in octal
                arnic429Fields[4][dataCount] = 1;
            }
            else if(dataCount == 4){
                arnic429Fields[4][dataCount] = 0;
            }
            else if(dataCount >= 5 && dataCount <= 6){
                arnic429Fields[4][dataCount] = 1;
            }
            else if(dataCount == 7){
                arnic429Fields[4][dataCount] = 0;
            }
        }
        return arnic429Fields;
    }

    /**
     * MethodName: fillWord()
     * DataType: int[]
     * <p>
     * fills the arnic429Word with the binary data
     * </p>
     * @author Ronald R Espinoza
     */
    public static int[] fillWord(){
        int bitCount = 0;
        int dataCount;
        int indexCount = 10;
        //case when
        //at parity bits
        arnic429Word[31] = arnic429Fields[0][0];
        //at SSM bits
        arnic429Word[30] = arnic429Fields[1][0];
        arnic429Word[29] = arnic429Fields[1][1];
        //at Data bits
        for(dataCount = 28; dataCount > 9; dataCount--){
            arnic429Word[indexCount] = arnic429Fields[2][bitCount];
            bitCount++; indexCount++;
        }
        //at SDI bits
        arnic429Word[9] =  arnic429Fields[3][0];
        arnic429Word[8] =  arnic429Fields[3][1];
        //at Label bits
        for(dataCount = 0; dataCount < 8; dataCount++){
            arnic429Word[dataCount] =  arnic429Fields[4][dataCount];
        }
        if(isOddParity(arnic429Word[31])){//the Data stream is valid with odd parity
            arnic429Word = reverseWord(arnic429Word, 0, WORD_SIZE - 1);
        }
        else{//The ARNIC format was corrupt
            System.out.println("data stream was invalid");
        }
        return arnic429Word;
    }

    /**
     * MethodName: clearWord()
     * DataType: int[]
     * <p>
     * resets arnic429Word to all 0 values
     * </p>
     * @author Ronald R Espinoza
     */
    public static int[] clearWord(){
        int wordCount = 0;
        //Zero out the ARNIC 429 word
        while(wordCount < WORD_SIZE){
            arnic429Word[wordCount] = 0;
            wordCount++;
        }
        return arnic429Word;
    }

    /**
     * MethodName: clearFields()
     * DataType: int[][]
     * <p>
     * Resets arnic429Fields to all 0 values
     * </p>
     * @param int[][] fields
     * @see main()
     * @return fields
     * @author Ronald R Espinoza
     */
    public static int[][] clearFields(int[][] fields){
        int fieldIndex;
        int fieldValue;
        //for all indicies reset the index to 0
        for(fieldIndex = 0; fieldIndex < INDEX_SIZE; fieldIndex++){
            //Then clear all field values by resetting the value to 0
            for(fieldValue = 0; fieldValue < FIELD_SIZE; fieldValue++){
                fields[fieldIndex][fieldValue] = 0;
            }
        }
        return fields;
    }

    /**
     * MethodName: main()
     * DataType: void
     * <p>
     * This method is the main entry point for the program
     * </p>
     * @param String[] args - The command line arguments
     * @author Ronald R Espinoza
     */
    public static void main(String[] args){
        double prediction;
        String theEncryptedData;
        //reset globals for use
        arnic429Fields = clearFields(arnic429Fields);
        arnic429Word = clearWord();
        //generate the airspeed prediction
        prediction = generateData();
        System.out.println("prediction: " + prediction + " Knots");
        //convert the prediction to a binary representation
        theEncryptedData = encryptData(prediction);
        System.out.println("data as BCD: " +theEncryptedData);
        arnic429Fields = setFields(theEncryptedData);
        //encapsulate and fill the ARNIC429 word with the prediction
        arnic429Word = fillWord();
        //display this prediction to the user
        printPrediction();
    }
}