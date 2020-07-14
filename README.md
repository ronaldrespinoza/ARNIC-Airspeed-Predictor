# ARNIC-Airspeed-Predictor

<h2>Purpose: </h2>

*  This class generates an airspeed prediction for altitude at 10250.

*  Using the least squares method and the following preset dummy data

*  airspeed,altimeter =  {(110,10180),(100,10210),(120,10140),(105,10190),(115,10200)}

*  This prediction is then encapsulated into an ARNIC 429 word format and displayed to the console.

<h5>Note:</h5>

<p>The data is made up and has no real value and is merely used as a practice for least squares regression implementation</p>

<h3>ARINC 429 Word Format</h3>

*  Each ARINC 429 word is a 32-bit sequence that contains five fields:

<ol>
<li>P: 32  //Parity Bit values(0,1)</li>
<li>SSM: 31,30 //Sign/Status Matrix values(00, 01, 10, 11)(+ or -)</li>
<li>Data: 29 - 11 //Digits 1 - 5 in BCD format</li>
<li>SDI: 10,9 //Source Destination Identifiers(00, 01, 10, 11)</li>
<li>Label: 8 - 1 //Label words(octal representation)</li>
</ol>

<h3>Execution Steps</h3>

<ol>
<li>Clear the byte stream of all data</li>
<li>Generate the Airspeed Prediction
<ol>
<li>Get a count for the total steps in the summation sequence</li>
<li>Iterate through steps 1 and 2 for the entire dataset using tail recursion</li>
<li>Step 1: For each (x,y) calculate x2 and xy:</li>
<li>Step 2: Sum x, y, x2 and xy (gives us sumX, sumY, sumXX and sumXY):</li>
<li>Then once the data set has been fully processed Proceed with steps 3 and 4</li>
<li>Step 3: Calculate Slope m:</li>
<li>Step 4: Calculate Intercept b:</li>
<li>Then solve for the x position using x = (TEST - b) / m</li>
<li>Return the prediction</li>
</ol>
</li>
<li>Format the Prediction to two decimal places</li>
<li>Convert each number in the prediction to it's BCD representation</li>
<li>Pack an ARNIC429 object with the data</li>
<li>Using the object fill a binary word with the data</li>
<li>Display the prediction to the User</li>
</ol>