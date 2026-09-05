Lib1="../../java-advanced-2025/lib/junit-jupiter-api-5.10.2.jar"
Lib2="../../java-advanced-2025/lib/junit-platform-launcher-1.10.2.jar"
Lib3="../../java-advanced-2025/lib/junit-platform-engine-1.10.2.jar"
Lib4="../../java-advanced-2025/lib/junit-platform-commons-1.10.2.jar"
Lib5="../../java-advanced-2025/lib/apiguardian-api-1.1.2.jar"
Lib6="../../java-advanced-2025/lib/junit-jupiter-engine-5.10.2.jar"
cd .. &&
./build.sh &&
cd ../../../../../ &&
javac -cp .:"$Lib1":"$Lib2":"$Lib3":"$Lib4":"$Lib5":"$Lib6" $(find info/kgeorgiy/ja/baronov/bank/tests/ -name "*.java") &&
java -cp .:"$Lib1":"$Lib2":"$Lib3":"$Lib4":"$Lib5":"$Lib6" info.kgeorgiy.ja.baronov.bank.tests.Tester