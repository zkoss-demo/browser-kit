#!/bin/bash
# Extract info from pom.xml
VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
ARTIFACT_ID=$(mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout)
set -e

UPLOAD_FOLDER=target/to_upload
MVN_FOLDER=$VERSION/maven

# follow file server /potix/rd folder structure, so we can directly upload
TARGET_FOLDER=$UPLOAD_FOLDER/$ARTIFACT_ID/releases/$MVN_FOLDER

rm -rf $UPLOAD_FOLDER

mkdir -p $TARGET_FOLDER/$EVAL_FOLDER
cp target/$ARTIFACT_ID-$VERSION-bundle.jar $TARGET_FOLDER
echo "Files prepared for upload in $TARGET_FOLDER"