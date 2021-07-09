![kstate](./kstate.svg)

![JVM Continuous Integration](https://github.com/bamlab/kstate/workflows/JVM%20Continuous%20Integration/badge.svg)

![JVM Continuous Deployment](https://github.com/bamlab/kstate/workflows/JVM%20Continuous%20Deployment/badge.svg)

## Install

![Maven Central](https://img.shields.io/maven-central/v/tech.bam.kstate/kstate-jvm)

```groovy
implementation("tech.bam.kstate:kstate-core:VERSION")
```

### Snapshot releases

```groovy
repositories {
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" } // Add this line
}
// ...

implementation("tech.bam.kstate:kstate-core:VERSION-SNAPSHOT")
```

## Usage

### State ids and event declaration

Extend the `StateId` and the `Event` types.

```kotlin
sealed class TrafficLightStateId : StateId {
    object RED : TrafficLightStateId()
    object YELLOW : TrafficLightStateId()
    object GREEN : TrafficLightStateId()
}

sealed class TrafficLightEvent : Event {
    object TIMER : TrafficLightEvent()
    object SHORT_TIMER : TrafficLightEvent()
}

sealed class PedestrianLightStateId : StateId {
    object WALK : PedestrianLightStateId()
    object WAIT : PedestrianLightStateId()
}
```

### Transition between states

```kotlin
val machine = createMachine {
    initial(RED)
    state(RED) {
        transition(on = TIMER, target = GREEN)
    }
    state(GREEN) {
        transition(on = TIMER, target = YELLOW)
    }
    state(YELLOW) {
        transition(on = TIMER, target = RED)
    }
}

machine.send(TIMER)

assertEquals(GREEN, machine.currentStateId)
```

### Transition between nested states

```kotlin
val machine = createMachine {
    initial(RED)
    state(RED) {
        initial(WALK)
        state(WALK) {
            transition(on = SHORT_TIMER, target = WAIT)
        }
        state(WAIT)
    }
}

machine.send(SHORT_TIMER)

assertEquals(listOf(RED, WAIT), machine.activeStateIds())
```

### Transition between nested state with the internal strategy

With the internal strategy, all events are passed to children **before** they are passed to the
compound state.

```kotlin
val machine = createMachine(strategy = KSStrategyType.Internal) {
    initial(RED)
    state(RED) {
        transition(on = TIMER, target = YELLOW)

        initial(WALK)
        state(WALK) {
            transition(on = TIMER, target = WAIT)
        }
        state(WAIT)
    }
    state(YELLOW)
}

machine.send(TIMER)

assertEquals(listOf(RED, WAIT), machine.activeStateIds())
```

### Parallel state machine

```kotlin
val machine = createMachine(type = Type.Parallel) {
    state(TRAFFIC_LIGHT) {
        initial(RED)
        state(RED) {
            transition(on = TIMER, target = GREEN)
        }
        state(GREEN) {
            transition(on = TIMER, target = RED)
        }
    }
    state(PEDESTRIAN_LIGHT) {
        initial(WAIT)
        state(WAIT) {
            transition(on = TIMER, target = WALK)
        }
        state(WALK) {
            transition(on = TIMER, target = WAIT)
        }
    }
}

machine.send(TIMER)

assertEquals(
    listOf(TRAFFIC_LIGHT, GREEN, PEDESTRIAN_LIGHT, WALK),
    machine.activeStateIds()
)
```

### Listen for transitions

```kotlin
val machine = createMachine {
    initial(RED)
    state(RED)
    state(YELLOW)
    state(GREEN)
}

val listener = MachineTransitionListener { previousActiveStateIds, nextActiveStateIds ->
    print("I'm listening.")
}

machine.subscribe(listener)
machine.unsubscribe(listener)

// OR

machine.onTransition { previousActiveStateIds, nextActiveStateIds ->
    print("I'm listening.")
}
```

## Developed with IntelliJ IDEA

[![JetBrains](./jetbrains.svg)](https://www.jetbrains.com/?from=kstate)
