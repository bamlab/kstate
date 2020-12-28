package com.github.bamlab

sealed class MachineError {
    object UnvalidTransition : Exception("This transition has not been declared, thus it is not allowed")

}