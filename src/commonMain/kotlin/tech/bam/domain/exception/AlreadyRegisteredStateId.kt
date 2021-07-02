package tech.bam.domain.exception

import tech.bam.KSStateId

class AlreadyRegisteredStateId(id: KSStateId) : Error("State with id $id is already registered.")