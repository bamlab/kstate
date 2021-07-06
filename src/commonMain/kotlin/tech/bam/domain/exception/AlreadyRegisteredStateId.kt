package tech.bam.domain.exception

import tech.bam.StateId

class AlreadyRegisteredStateId(id: StateId) : Error("State with id $id is already registered.")