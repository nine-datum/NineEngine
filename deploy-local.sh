name=$1
ver=$2
sed -i "s/<version>[^<]*<\/version>/<version>$ver<\/version>/" pom.xml
mvn install:install-file -Dfile=build/nine.jar -DgroupId=io.github.taqmuraz -DartifactId=$name -Dversion=$ver -Dpackaging=jar