package tech.bam.kstate.core.domain.exception

import tech.bam.kstate.core.StateId

class AlreadyRegisteredStateId(id: StateId) :
    Error("State with id $id is already registered.")