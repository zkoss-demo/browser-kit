# Overview
This project is a simple ZK addon based on ZK CE (community edition) that wraps various browser built-in JavaScript API in Java.

# The JavaScript API to wrap
* [Geolocation API](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation_API/Using_the_Geolocation_API)
* [Clipboard API](https://developer.mozilla.org/en-US/docs/Web/API/Clipboard_API)
* [Notification API](https://developer.mozilla.org/en-US/docs/Web/API/Notification) (not yet supported)
* [Web Speech API](https://developer.mozilla.org/en-US/docs/Web/API/Web_Speech_API) (not yet supported)
* [MediaDevices](https://developer.mozilla.org/en-US/docs/Web/API/MediaDevices) (not yet supported)
* [Drag and Drop API](https://developer.mozilla.org/en-US/docs/Web/API/HTML_Drag_and_Drop_API) (not yet supported)

# Run Demo Application
Require Maven installed.
1. Run `mvn jetty:run`
2. Navigate to `http://localhost:8080/browser-kit/`

# Usage Examples
3 main steps:
1. Initialize the helper in a controller's lifecycle
2. Listen to the corresponding event
3. Call its methods in an event listener

see *.java and *.zul under src/test/


# Install via Maven

```xml
<dependency>
    <groupId>org.zkoss.zkforge</groupId>
    <artifactId>browser-kit</artifactId>
    <version>2.0.0</version>
</dependency>
```

Include ZK CE repository
```xml
        <repository>
            <id>ZK CE</id>
            <name>ZK CE Repository</name>
            <url>https://mavensync.zkoss.org/maven2</url>
        </repository>
```

# Browser API Helpers Architecture

Both ClipboardHelper and GeolocationHelper implement a unified, stateless design pattern that provides seamless integration with ZK applications.

## Unified Architecture Overview

Both helpers implement a consistent stateless architecture with auto-initialization and singleton AuService pattern for optimal resource management and developer experience.

### Key Architectural Patterns

#### Stateless Static Methods
- All public methods are static for simplified usage
- No instance management required - just call the methods directly

#### Singleton AuService Pattern
- Single AuService instance shared across all desktops 
- Desktop-level initialization tracking prevents duplicate setup
- Efficient resource utilization with proper cleanup support

#### Event-Driven Communication
```
Browser API → JavaScript Helper → zAu.send(desktop event) 
    → AuService → Desktop Event → Controller(Composer/ViewModel) Listeners
```

## ClipboardHelper

The ClipboardHelper provides static access to the browser's Clipboard API for reading and writing clipboard content.
* Secure context: This feature is available only in [secure contexts (HTTPS)](https://developer.mozilla.org/en-US/docs/Web/Security/Secure_Contexts)

### Usage Example

1. Init
```java
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        ClipboardHelper.init();
    }
```

2. Listening for results
```java
@Listen(ClipboardEvent.EVENT_NAME + " = #root")
public void handleClipboard(ClipboardEvent event) {
    ClipboardResult result = event.getClipboardResult();
    if (result.isSuccess()) {
        processClipboardContent(result.getText());
    }
}
```

3. Call operation methods

```java
// Writing to clipboard
ClipboardHelper.writeText("Hello World");

// Reading from clipboard (results via events)
ClipboardHelper.readText();

```

### Important Notes
⚠️ **User interaction required**: Clipboard operations must be triggered from user interactions (click, keypress, etc.) due to browser security restrictions.

⚠️ **Security concern**: When handling clipboard content, sanitize user input to prevent potential security vulnerabilities.

### Targeted Clipboard Operations (Since 2.1.0)

For scenarios with multiple forms or components requesting clipboard access simultaneously, use the **targeted clipboard API** to deliver events directly to the requesting component instead of broadcasting to all components.

#### Why Targeted Delivery?

When multiple forms on a page need clipboard access:
- **Broadcast approach** (original): All components receive all clipboard events → requires manual filtering and flag management
- **Targeted approach** (new): Each component receives only ITS own clipboard events → no filtering needed, no cross-form interference

#### API Architecture

The clipboard operations follow a **unified delegation pattern**:
- **Primary methods** (`readTextTo()`, `readImageTo()`, `writeTextTo()`): Accept a target component (or `null` for broadcast)
- **Broadcast methods** (`readText()`, `readImage()`, `writeText()`): Delegate to primary methods with `null` target
- **Single source of truth**: All operations go through the targeted methods

This design eliminates code duplication and provides a clean API:

```java
// Broadcast - delegates to readTextTo(null)
ClipboardHelper.readText();

// Targeted - primary implementation
ClipboardHelper.readTextTo(Component targetComponent);

// Same pattern for images and writes
ClipboardHelper.readImage();        // → readImageTo(null)
ClipboardHelper.readImageTo(Component);

ClipboardHelper.writeText(String);  // → writeTextTo(null, String)
ClipboardHelper.writeTextTo(Component, String);
```

#### Targeted Usage Example

```java
public class FormComposer extends SelectorComposer<Component> {
    @Wire
    private Textbox pastingTarget;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        ClipboardHelper.init();

        // Listen for clipboard events - events are automatically targeted to this component
        pastingTarget.addEventListener(ClipboardEvent.EVENT_NAME, (ClipboardEvent event) -> {
            if (event.isSuccess() && event.isTextResult()) {
                // Safe to process - event is guaranteed for this component only
                pastingTarget.setValue(event.getClipboardText().getText());
            }
        });
    }

    @Listen("onClick = #read")
    public void read() {
        // Use targeted API - delivers result only to THIS component
        ClipboardHelper.readTextTo(pastingTarget);
    }
}
```

#### Benefits of Targeted Delivery

| Feature | Broadcast API | Targeted API |
|---------|--|--|
| **Event Delivery** | All root components | Target component only |
| **Multiple Forms** | Events broadcast to all | No interference |
| **Code Complexity** | Flags/IDs needed for filtering | No filtering required |
| **Concurrent Requests** | Potential race conditions | Naturally concurrent-safe |
| **Use Case** | Global clipboard monitoring | Form-specific operations |
| **How to Use** | `readText()` | `readTextTo(self)` or `readTextTo(null)` for broadcast |

#### Full Example

See [multiple-requester.zul](src/test/webapp/multiple-requester.zul) and [FormComposer.java](src/test/java/test/clipboard/FormComposer.java) for a complete working example with 3 forms requesting clipboard simultaneously.

## GeolocationHelper

The GeolocationHelper provides static access to the browser's Geolocation API for requesting user location.
* Secure context: This feature is available only in secure contexts (HTTPS)

### Usage Example
```java
// Request current position
GeolocationHelper.getCurrentPosition();

// Listen for results
@Listen(GeolocationEvent.EVENT_NAME + " = #root")
public void handleGeolocation(GeolocationEvent event) {
    if (event.isSuccess()) {
        GeoLocationPosition position = event.getGeoLocationPosition();
        processLocation(position);
    } else {
        handleLocationError(event.getGeoLocationPositionError());
    }
}
```

### Implementation Benefits
- **Simplified API**: No instance management - direct static method calls
- **Desktop-level Events**: Multiple composers can listen independently
- **Resource Efficiency**: Singleton AuService pattern minimizes overhead
- **Clean Lifecycle**: Optional `dispose()` method for explicit cleanup

### Architecture Diagram
**Init** 
```mermaid
flowchart TB
    subgraph Client[Browser]
        JS[JavaScript Helper]
    end

    subgraph Server[Server]
        Java[Static Helper Method]
        Init[Auto-initialization]
        AU[Singleton AuService]
    end

    Java -->|1. Calls| Init
    Init -->|2. Checks| Init
    Init -->|3. Registers| AU
    Init -->|4. Loads| JS

    style Client fill:#f0f8ff,color:black
    style Server fill:#e8f5e9,color:black
    style Java fill:#ffd1dc,stroke:#c2185b,color:black
    style Init fill:#e0f7fa,stroke:#006064,color:black
    style AU fill:#e8f5e9,stroke:#2e7d32,color:black
    style JS fill:#fff3e0,stroke:#ef6c00,color:black
```

**Calling**
```mermaid
flowchart TB
    subgraph Client[Browser]
        API[Browser API]
        JS[JavaScript Helper]
    end

    subgraph Server[Server]
        Java[Static Helper Method]
        AU[Singleton AuService]
        Controller[Java Controller]
    end

    Java -->|1. Executes| API
    API -->|2. Returns| JS
    JS -->|3. Sends Event| AU
    AU -->|4. Posts Event| Controller

    style Client fill:#f0f8ff,color:black
    style Server fill:#e8f5e9,color:black
    style Java fill:#ffd1dc,stroke:#c2185b,color:black
    style AU fill:#e8f5e9,stroke:#2e7d32,color:black
    style JS fill:#fff3e0,stroke:#ef6c00,color:black
    style API fill:#e3f2fd,stroke:#1565c0,color:black
    style Controller fill:#ffecb3,stroke:#ff6f00,color:black
```

### Usage Examples
- ClipboardHelper: see [ClipboardComposer.java](src/test/java/test/clipboard/ClipboardComposer.java)
- GeolocationHelper: see [LocationComposer.java](src/test/java/test/geolocation/LocationComposer.java)

**Note**: Both helpers require user permissions and are subject to browser support and security restrictions.

# Release Process
1. change version in `pom.xml` to official version
2. run [release](release/release)
3. set tag in GitHub as the version (e.g. `v1.0.0`)
4. Publish to ZK Maven CE repository with jekins3/PBFUM

* a simple project, no need freshly version

# License
* Source Code - [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
