# Overview
This project is a ZK addon based on ZK CE that wraps various browser built-in JavaScript API in Java.

# The JavaScript API to wrap
* [Geolocation API](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation_API/Using_the_Geolocation_API)
* Clipboard API
* Notification API
* Web Speech API
* MediaDevices.getUserMedia
* Drag and Drop API

# Usage Examples
see src/test/

# Release Process
1. build jar
2. change version in `pom.xml` to official version (remove `-SNAPSHOT`)
3. set tag in GitHub as version (e.g. `v1.0.0`)
4. Publish to ZK Maven CE repository

## Build jar
* freshly
`./release.sh`

* Official
`./release.sh official`

## Publish to Maven
[jenkins job]()