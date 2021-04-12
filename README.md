![kstate](./kstate.svg)

![JVM Continuous Integration](https://github.com/bamlab/kstate/workflows/JVM%20Continuous%20Integration/badge.svg)

![JVM Continuous Deployment](https://github.com/bamlab/kstate/workflows/JVM%20Continuous%20Deployment/badge.svg)

## Install

```groovy
implementation("tech.bam:kstate-jvm:VERSION")
```

### Snapshot releases

```groovy
repositories {
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" } // Add this line
}
// ...

implementation("tech.bam:kstate-jvm:VERSION-SNAPSHOT")
```

## Usage

### Machine

```kotlin
sealed class TrafficLightState {
    object GREEN : TrafficLightState()
    object YELLOW : TrafficLightState()
    object RED : TrafficLightState()
}

val myMachine = machine<TrafficLightState> {
    state(TrafficLightState.GREEN) {}
    state(TrafficLightState.YELLOW) {}
    state(TrafficLightState.RED) {}
}
```

### Developed with IntelliJ IDEA

[![JetBrains](./jetbrains.svg)](https://www.jetbrains.com/?from=kstate)
