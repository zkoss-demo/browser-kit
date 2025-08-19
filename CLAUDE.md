# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a ZK addon (browser-kit) that provides Java wrappers for browser JavaScript APIs. The project wraps native browser APIs like Geolocation, Clipboard, and others into ZK components for use in ZK applications.

## Build and Development Commands

### Building the Project
- **Fresh build**: `./release/release` or `mvn clean install`
- **Official release build**: `./release/release official` (sets version to official release)
- **Run tests**: `mvn test`
- **Start development server**: `mvn jetty:run` (runs on http://localhost:8080/browser-kit)

### Maven Profiles
- `fresh` profile: Creates timestamped development versions (1.0.0.FL.YYYYMMDD)
- `official` profile: Uses clean version numbers for releases

## Architecture

### Core Components
- **Helper Classes**: Main entry points that bridge Java and JavaScript
  - `ClipboardHelper` (clipboard operations)
  - `GeolocationHelper` (browser geolocation API)
- **Result/Data Classes**: POJOs for API responses
  - `ClipboardResult`, `GeoLocationPosition`, `GeolocationPositionError`, etc.
- **JavaScript Integration**: Each helper has corresponding .js file in `src/main/resources/web/js/`

### Key Architecture Patterns
- **Event-Driven**: Uses ZK event system with anchor components to handle async JavaScript callbacks
- **Consumer Pattern**: Helpers accept `Consumer<ResultType>` callbacks for handling results
- **Resource Management**: JavaScript helpers are loaded dynamically via ZK's Script component

### Dependencies
- **ZK Framework**: 9.6.0 (scope: provided - assumes host application provides ZK)
- **Gson**: 2.11.0 for JSON serialization
- **Java**: Requires Java 11+

## Testing
- Test examples are in `src/test/` with ZUL pages demonstrating usage
- Access test pages via Jetty server at `http://localhost:8080/browser-kit/`

## Release Process
1. Update version in `pom.xml`
2. Run `./release/release`
3. Tag release in GitHub (e.g. `v1.0.0`)
4. Publish to ZK Maven repository via Jenkins