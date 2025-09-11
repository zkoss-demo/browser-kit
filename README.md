# Overview
This project is a simple ZK addon based on ZK CE (compact edition) that wraps various browser built-in JavaScript API in Java.

# The JavaScript API to wrap
* [Geolocation API](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation_API/Using_the_Geolocation_API)
* [Clipboard API](https://developer.mozilla.org/en-US/docs/Web/API/Clipboard_API)
* [Notification API](https://developer.mozilla.org/en-US/docs/Web/API/Notification)
* [Web Speech API](https://developer.mozilla.org/en-US/docs/Web/API/Web_Speech_API)
* [MediaDevices](https://developer.mozilla.org/en-US/docs/Web/API/MediaDevices)
* [Drag and Drop API](https://developer.mozilla.org/en-US/docs/Web/API/HTML_Drag_and_Drop_API)

# Usage Examples
see *.java and *.zul under src/test/

# ClipboardHelper

The ClipboardHelper provides access to the browser's Clipboard API for reading and writing clipboard content.
## Important Notes
⚠️ **Its methods must be called within an event listener**: ClipboardHelper operations must be triggered from user interactions (click, keypress, etc.) due to browser security restrictions. Calling outside of event handlers will fail.

⚠️ **Security concern**: When handling clipboard content, you should sanitize user input yourself before using it in your application to prevent potential security vulnerabilities.

# GeolocationHelper Architecture

## Overview
The GeolocationHelper provides a robust, event-driven wrapper for the browser's Geolocation API, implementing an advanced AuService-based architecture for seamless integration with ZK applications.

## Key Architectural Patterns

### AuService-Based Event Handling
- Utilizes ZK's native AuService mechanism for desktop-level event management
- Replaces traditional page-level listeners with a more scalable desktop event system
- Enables decoupled, efficient event communication between browser and server

### Singleton Desktop-Level Management
- Ensures only one GeolocationHelper instance per desktop
- Provides lazy initialization with `getInstance()` method
- Prevents multiple concurrent geolocation requests

### Event Communication Flow
```
Browser API → JavaScript Helper → zAu.send(desktop event) → AuService → Desktop Event → Component Listeners
```

## Architecture Diagram

```mermaid
sequenceDiagram
    participant Browser as Browser Geolocation API
    participant JS as GeolocationHelper.js
    participant AU as AuService
    participant Java as GeolocationHelper

    Browser->>JS: Return position/error
    JS->>AU: zAu.send(desktop-level event)
    AU->>Java: Trigger desktop event
    Java->>Java: Process geolocation result
```

### Key Implementation Details
- Uses ZK's `desktop.addListener()` for AuService registration
- Sends events directly to desktop using `zk.Desktop._dt`
- Supports clean resource management through desktop-level event handling
- Provides a more robust alternative to page-level event listeners

### Usage Example
see [LocationComposer.java](src/test/java/test/geolocation/LocationComposer.java)

**Benefits of AuService Pattern**:
- Decoupled event handling
- Improved resource management
- Simplified event registration
- More scalable desktop-level communication

**Note**: Geolocation access is subject to user permissions and browser support.

# Release Process
1. change version in `pom.xml` to official version
2. run [release](release/release)
3. set tag in GitHub as version (e.g. `v1.0.0`)
4. Publish to ZK Maven CE repository with jekins3/PBFUM

## Build jar
* freshly
`./release.sh`

* Official
`./release.sh official`

## Publish to Maven
[jenkins job]()

## License
* Source Code - [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
