package ch.uzh.ifi.seal.bencher.analysis.weight

import ch.uzh.ifi.seal.bencher.Method
import ch.uzh.ifi.seal.bencher.analysis.callgraph.*
import org.funktionale.either.Either

typealias MethodWeights = Map<out Method, Double>

interface MethodWeighter {
    fun weights(): Either<String, MethodWeights>
}

fun methodCallWeight(
        method: Method,
        reachability: Reachability,
        methodWeights: MethodWeights,
        exclusions: Set<Method>,
        accumulator: (Double, Double) -> Double = Double::plus
): Pair<Double, Set<Method>> =
        reachability.reachable(method, methodWeights.keys).fold(Pair(0.0, exclusions)) { acc, rr ->
            if (acc.second.contains(rr.to)) {
                return@fold acc
            }

            val w = methodWeights[rr.to] ?: 0.0
            val ne = setOf(rr.to.toPlainMethod())

            val v: Pair<Double, Set<Method>> = when (rr) {
                is NotReachable -> Pair(0.0, setOf())
                is PossiblyReachable -> Pair(w * rr.probability, ne)
                is Reachable -> Pair(w, ne)
            }
            Pair(
                    accumulator(acc.first, v.first),
                    acc.second + v.second
            )
        }