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
1. Ensure you are in the "coding-challenge" directory after building the software.
2. Run the program with a sample file:
  * `java -cp target/coding-challenge-1.0-SNAPSHOT.jar com.aconex.CodingChallengeLauncher src/test/resources/com/aconex/testNumberFile`
3. Run the program with command line entry do not supply any arguments and follow on screen instructions: 
  * `java -cp target/coding-challenge-1.0-SNAPSHOT.jar com.aconex.CodingChallengeLauncher`
4. Override the default dictionary packaged with the software by setting a system property of `dictionary.file` which points to a different dictionary. The example below shows an override that is part of the test source packaged with the application which has a smaller set of words in it.
  * `java -Ddictionary.file=src/test/resources/com/aconex/index/sampleDict -cp target/coding-challenge-1.0-SNAPSHOT.jar com.aconex.CodingChallengeLauncher`

## Sample data
A number of things should return results. The packaged dictionary has around 100K words in it. The example from the challenge actually works as well: 225563.

Adding 1800 to the start of a number will be ignored so keep that in mind when you are playing around. All output is prefixed with 1-800 as well.