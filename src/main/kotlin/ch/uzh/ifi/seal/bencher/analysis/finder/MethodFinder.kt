package ch.uzh.ifi.seal.bencher.analysis.finder

import ch.uzh.ifi.seal.bencher.*
import ch.uzh.ifi.seal.bencher.execution.ExecutionConfiguration
import com.ibm.wala.classLoader.IMethod
import org.funktionale.either.Either

interface MethodFinder<out T : Method> {
    fun all(): Either<String, List<T>>
}

interface BenchmarkFinder : MethodFinder<Benchmark> {
    fun setups(b: Benchmark): Collection<SetupMethod>
    fun tearDowns(b: Benchmark): Collection<TearDownMethod>
    fun benchmarkExecutionInfos(): Either<String, Map<Benchmark, ExecutionConfiguration>>
    fun classExecutionInfos(): Either<String, Map<Class, ExecutionConfiguration>>
    fun jmhParamSource(b: Benchmark): Map<String, String>
}

interface BencherWalaMethodFinder<out T : Method> : MethodFinder<T> {
    fun bencherWalaMethods(): Either<String, List<Pair<T, IMethod?>>>
}

class IterableMethodFinder<out T : Method>(
        private val methods: Iterable<T>,
        private val includeNoMethods: Boolean = false
) : MethodFinder<T> {
    override fun all(): Either<String, List<T>> =
            Either.right(methods.filter { (it != NoMethod) || includeNoMethods })
}
