package tech.bam.domain.exception

class UnexpectedError() :
    Error("No current state found for this machine. This is a bug with `kstate`, please open an issue with a reproducible example at https://github.com/bamlab/kstate/issues")