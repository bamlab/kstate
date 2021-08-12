package tech.bam.kstate.core.domain.exception

import tech.bam.kstate.core.domain.types.StateId

class AlreadyRegisteredStateId(id: StateId<*>) :
    Error("State with id ${id::class.simpleName} is already registered.")