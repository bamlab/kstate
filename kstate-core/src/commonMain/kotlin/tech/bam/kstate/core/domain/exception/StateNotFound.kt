package tech.bam.kstate.core.domain.exception

import tech.bam.kstate.core.domain.types.StateId

class StateNotFound(id: StateId<*>, parentId: StateId<*>) :
    Error("${parentId::class.simpleName}.${id::class.simpleName} not found ! State with id ${id::class.simpleName} is not registered in compound state with id ${parentId::class.simpleName}.")