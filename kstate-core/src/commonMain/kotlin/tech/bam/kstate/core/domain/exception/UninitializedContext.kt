package tech.bam.kstate.core.domain.exception

import tech.bam.kstate.core.StateId

class UninitializedContext(id: StateId<*, *>) :
    Error("Context for state $id is required and has not been initialized before the machine starts.")
