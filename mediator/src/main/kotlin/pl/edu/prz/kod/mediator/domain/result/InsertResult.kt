package pl.edu.prz.kod.mediator.domain.result

sealed class InsertResult {
    object Success : InsertResult()
    object Failure : InsertResult()
}