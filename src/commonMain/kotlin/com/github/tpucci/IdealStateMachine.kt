package com.github.tpucci

/**
 * Checkpoints:
 * parallel
 * context
 * hierarchy simple
 * split
 */

val idealStateMachine = machine {
    type(PARALLEL)
    states {
        state(BIOMETRIC_WALL, biometricWallMachine)
        state(NEW_WOUND)
        state(NEW_PICTURE)
        state(NEW_ASSESSMENT)
        state(NEW_TREATMENT)
        state(MAIN, mainStateMachine)
    }
}

val shortcutModalMachine {
    ...
}

val biometricWallMachine = machine {
    initial(NONE)
    states {
        state(NONE) { on(LOCK to LOCKED) }
        state(LOCKED) { on(UNLOCK to NONE) }
    }
}

val mainStateMachine = machine {
    initial(HOME)
    states {
        state(HOME) {
            on(OPEN_SETTINGS to SETTINGS, OPEN_PATIENT_CHANNEL to PATIENT_CHANNEL)
            factory(homeStateMachineFactory)
        }
        state(SETTINGS) { on(BACK to HOME, CLOSE to HOME) }
        state(PATIENT_CHANNEL) {
            on(BACK to HOME, CLOSE to HOME)
            factory(patientChannelStateMachineFactory)
            onEntry {
                assign { (event: OPEN_PATIENT_CHANNEL) -> patientId = event.patientId}
            }
        }
    }
}

val homeStateMachineFactory = machine {
    type(PARALLEL)
    state(HOME)
    state(SHORTCUT_MODAL) {
        factory(shortcutModalStateMachineFactory)
    }
}

val patientChannelStateMachineFactory = machineFactory { context ->
    initial(context.initial)
    context {
        val patientId: context.patientId
    }
    states {
        state(WOUNDS) {
            on(OPEN_WOUND to WOUND_DETAILS)
        }
        state(CHAT) {}
        state(WOUND_DETAILS) {
            on(BACK to HISTORY)
            factory(woundDetailsFactory)
            onEntry {
                assign { (event: OPEN_WOUND) -> woundId = event.woundId et patientId ? }
            }
        }
    }
}

val woundDetailsFactory = machineFactory { context ->
    ...
}

val shortcutModalStateMachineFactory = machineFactory { context ->
    initial(context.inital)
    state(ROOT) {
        on(NEW_WOUND to {
            target: OPEN_WOUND, cond: patientId != null,
            target: CHOOSE_PATIENT, cond: patient == null
        })
    }
}



currentStates = {
    root: {
        biometric: none
        new..: none,
        main: {
            home: HOME
            shortcut: none
        }
    }
}


currentStates = {
    root: {
        biometric: none
        new..: none,
        main: {
            patientChannel: {
                woundDetails: {

                }
            }
        }
    }
}
