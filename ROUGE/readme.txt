This is a perl script that takes in all your system generated files, then all your gold standard/reference summary files, and prepares it in the format used by the ROUGE evaluation toolkit. In other words, it prepares the following from the input that you provide:

    * models/
    * systems/
    * settings.xml

There are 2 versions of the script, one is output using jackknifing and the other is the usual evaluation without the need for jackknifing .
Step 1: Your input

 To start off you should have a directory/file structure as follows:

   1. SYSTEM/ - A directory where all the system or baseline summaries are stored
   2. GOLD/ - A directory where all the reference summaries are stored

The matching of system summaries with reference summaries is through the file name prefix. So, if you have a system summary with the file name abc.system1.system, then accordingly you should have a reference summary/summaries with the prefix abc.xyz.gold. With this, all the system summaries can be easily matched up with all the reference summaries. Each sentence in all your system/gold summaries, should be on its own  separate line. Here are specifics for each directory.

SYSTEM/
Within this directory, the results of each system should be stored in a separate folder. So if you have summaries from 3 different systems, there should be 3 folders each identified by the system name. Inside these folders, you would have the summary files. For example, if you have a system summary for a document 'abc' from 'system1'. Then your system1's summary would be stored in SYSTEM/system1/abc.system1.system. 'system1' is the name of the system and you can use any identifying name so long as it is consistent for each system. All system files should end with a '.system' extension.

GOLD/
Each set of reference summary should be within a separate folder. So if you have 4 reference summaries for topic abc, you would have:
GOLD/abc/
              abc.1.gold
              abc.2.gold
              abc.3.gold
              abc.4.gold

To get a better idea, you can see sample input and output in prepare4rouge/examples.

Step 2: Edit Perl Script

Next, you need to edit the perl script to reflect the directories that you have created in STEP 1. Open the perl script, and edit the following:

my $OUTPUT_HOME = "<root directory for your output>";
my $DIR_SYSTEM="<location of system generated files>";
my $DIR_GOLD="<location of gold standard files>";


Step 3: Run Script

To generate output with jackknifing in place run the following:

$perl prepare4rouge-jk.pl

To generate regular output run the following:

$perl prepare4rouge-simple.pl