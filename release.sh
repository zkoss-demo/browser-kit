#!/bin/bash

if [ "$1" == "official" ] ; then
	profile=$1
else
	profile="fresh"
fi

echo "Building with profile: $profile"

mvn clean source:jar javadoc:jar repository:bundle-create -DskipTests -Dformat=yyyyMMdd --batch-mode -P$profile



