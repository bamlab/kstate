package tech.bam.kstate.core.domain.exception

import tech.bam.kstate.core.Context
import tech.bam.kstate.core.StateIdWithContext

class AlreadyRegisteredStateId(id: StateIdWithContext<out Context>) :
    Error("State with id $id is already registered.")