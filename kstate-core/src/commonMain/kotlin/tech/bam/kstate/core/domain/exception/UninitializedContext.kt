package tech.bam.kstate.core.domain.exception

import tech.bam.kstate.core.domain.types.StateId

class UninitializedContext(id: StateId<*>) :
    Error("Context for state ${id::class.simpleName} is required and has not been initialized before the machine starts.")
