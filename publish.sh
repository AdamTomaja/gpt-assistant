#!/bin/sh
version=$1
echo "Running spotless"
./gradlew spotlessApply

echo "Publishing with version $version"
echo "Building docker image"
docker build -t vault:5556/gpt-assistant:$version .

echo "Pushing image to vault"
docker push vault:5556/gpt-assistant:$version

echo "Commiting changes"
git add .
git commit -m "New version $version"
git push