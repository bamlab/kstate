package tech.bam.kstate.core.domain.exception

import tech.bam.kstate.core.Context
import tech.bam.kstate.core.StateIdWithContext

class UninitializedContext(stateId: StateIdWithContext<out Context>) :
    Error("A context is required for state with id $stateId and has not been initialized.")
