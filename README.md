aconex
======

1-800 Coding challenge exercise for Aconex Java position

##Building

The coding-challenge directory contains the source code for my submission.

Check out the code using:

`git clone https://github.com/seangay/aconex.git directory-to-clone-into`

Navigate to the `coding-challenge` directory under the cloned repository from above and run:

`mvn clean install`

This will run all tests and build the tool into:

`directory-to-clone-into/coding-challenge/target/coding-challenge-1.0-SNAPSHOT.jar`


## Running
1. Navigate to the "coding-challenge" directory once built (above).
2. To run the program with a sample file run:
  * `java -cp target/coding-challenge-1.0-SNAPSHOT.jar com.aconex.CodingChallengeLauncher src/test/resources/com/aconex/testNumberFile`
3. To run the program with command line entry simply leave off the file argument and follow instructions. 
  * `java -cp target/coding-challenge-1.0-SNAPSHOT.jar com.aconex.CodingChallengeLauncher`
4. To override the default dictionary packaged with the program run it using "-Ddictionary.file=PATH/TO/YOUR/DICTIONARY/FILE". There is a test directory that could be launched using the command below assuming running the program after performing step 1
  * `java -Ddictionary.file=src/test/resources/com/aconex/index/sampleDict -cp target/coding-challenge-1.0-SNAPSHOT.jar com.aconex.CodingChallengeLauncher`