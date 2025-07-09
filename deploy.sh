name=$1
ver=$2
vn=$name-$ver
repo=io/github/taqmuraz
path=deploy/$repo/$name/$ver

root=$(pwd)

rm -r deploy
mkdir -p $path
cp build/nine.jar $path/$vn.jar
cp pom.xml $path/$vn.pom
cd $path
sed -i "s/<version>[^<]*<\/version>/<version>$ver<\/version>/" $vn.pom

mkdir empty
jar -cf $vn-javadoc.jar -C empty .
jar -cf $vn-sources.jar -C empty .
rm -r empty

sum(){
  local n=$1
  md5sum $n | cut -d ' ' -f 1 > $n.md5
  sha1sum $n | cut -d ' ' -f 1 > $n.sha1
}
sig() {
  local n=$1
  gpg --output $n.asc --detach-sig $n
}
proc(){
  local n=$1
  sum $n
  sig $n
}

proc $vn.pom
proc $vn.jar
proc $vn-javadoc.jar
proc $vn-sources.jar

cd $root/deploy

zip -r $vn.zip io