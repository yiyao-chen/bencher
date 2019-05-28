package ch.uzh.ifi.seal.bencher.analysis.callgraph.dyn

import ch.uzh.ifi.seal.bencher.Benchmark
import ch.uzh.ifi.seal.bencher.analysis.JarTestHelper
import ch.uzh.ifi.seal.bencher.analysis.callgraph.CGResult
import ch.uzh.ifi.seal.bencher.analysis.callgraph.Reachabilities
import ch.uzh.ifi.seal.bencher.analysis.callgraph.Reachable

object DCGTestHelper {

    private fun emptyReachabilities(b: Benchmark): Pair<Benchmark, Reachabilities> = Pair(
            b,
            Reachabilities(start = b, reachabilities = setOf())
    )

    object BenchParameterized {
        private fun bench1Reachabilities(b: Benchmark): Pair<Benchmark, Reachabilities> {
            val pb = b.toPlainMethod()
            return Pair(
                    b,
                    Reachabilities(
                            start = b,
                            reachabilities = setOf(
                                    Reachable(from = pb, to = JarTestHelper.CoreA.m, level = 1),
                                    Reachable(from = pb, to = JarTestHelper.CoreB.m, level = 3),
                                    Reachable(from = pb, to = JarTestHelper.CoreC.m, level = 4)
                            )
                    )
            )
        }

        private fun bench1Cg(b: Benchmark): Pair<Benchmark, Reachabilities> = bench1Reachabilities(b)


        val bench1Cgs: Array<Pair<Benchmark, Reachabilities>> = JarTestHelper.BenchParameterized.bench1.let { b ->
            val pbs = b.parameterizedBenchmarks()
            pbs.map { bench1Cg(it) }.toTypedArray()
        }

        val bench1NonParamCgs: Array<Pair<Benchmark, Reachabilities>> =
                arrayOf(bench1Cg(JarTestHelper.BenchParameterized.bench1))
    }

    object BenchNonParameterized {
        private val bench2Reachabilities: Pair<Benchmark, Reachabilities> = JarTestHelper.BenchNonParameterized.bench2.let { b ->
            val pb = b.toPlainMethod()
            Pair(
                    b,
                    Reachabilities(
                            start = b,
                            reachabilities = setOf(
                                    Reachable(from = pb, to = JarTestHelper.CoreC.m, level = 1)
                            )
                    )
            )
        }

        val bench2Cg: Pair<Benchmark, Reachabilities> = bench2Reachabilities
    }

    object OtherBench {
        private val bench3Reachabilities: Pair<Benchmark, Reachabilities> = JarTestHelper.OtherBench.bench3.let { b ->
            val pb = b.toPlainMethod()
            Pair(
                    b,
                    Reachabilities(
                            start = b,
                            reachabilities = setOf(
                                    Reachable(from = pb, to = JarTestHelper.CoreB.m, level = 1),
                                    Reachable(from = pb, to = JarTestHelper.CoreC.m, level = 2)
                            )
                    )
            )
        }

        val bench3Cg: Pair<Benchmark, Reachabilities> = bench3Reachabilities
    }

    object BenchParameterized2 {
        private fun bench4Reachabilities(b: Benchmark): Pair<Benchmark, Reachabilities> {
            val pb = b.toPlainMethod()
            return Pair(
                    b,
                    Reachabilities(
                            start = b,
                            reachabilities = setOf(
                                    Reachable(from = pb, to = JarTestHelper.CoreA.m, level = 1),
                                    Reachable(from = pb, to = JarTestHelper.CoreD.m, level = 3)
                            )
                    )
            )
        }

        internal fun bench4Cg(b: Benchmark): Pair<Benchmark, Reachabilities> = bench4Reachabilities(b)

        val bench4Cgs: Array<Pair<Benchmark, Reachabilities>> = JarTestHelper.BenchParameterized2.bench4.let { b ->
            val pbs = b.parameterizedBenchmarks()
            pbs.map { bench4Cg(it) }.toTypedArray()
        }

        val bench4NonParamCgs: Array<Pair<Benchmark, Reachabilities>> =
                arrayOf(bench4Cg(JarTestHelper.BenchParameterized2.bench4))
    }

    object BenchParameterized2v2 {
        val bench4Cgs: Array<Pair<Benchmark, Reachabilities>> = JarTestHelper.BenchParameterized2v2.bench4.let { b ->
            val pbs = b.parameterizedBenchmarks()
            pbs.map { BenchParameterized2.bench4Cg(it) }.toTypedArray()
        }

        val bench4NonParamCgs: Array<Pair<Benchmark, Reachabilities>> =
                arrayOf(BenchParameterized2.bench4Cg(JarTestHelper.BenchParameterized2v2.bench4))
    }

    object NestedBenchmark {

        object Bench1 {
            val bench11Cg: Pair<Benchmark, Reachabilities> =
                    emptyReachabilities(JarTestHelper.NestedBenchmark.Bench1.bench11)

            val bench12Cg: Pair<Benchmark, Reachabilities> =
                    emptyReachabilities(JarTestHelper.NestedBenchmark.Bench1.bench12)
        }

        val bench2Cg: Pair<Benchmark, Reachabilities> = emptyReachabilities(JarTestHelper.NestedBenchmark.bench2)

        object Bench3 {
            val bench31Cg: Pair<Benchmark, Reachabilities> =
                    emptyReachabilities(JarTestHelper.NestedBenchmark.Bench3.bench31)

            object Bench32 {
                val bench321Cg: Pair<Benchmark, Reachabilities> =
                        emptyReachabilities(JarTestHelper.NestedBenchmark.Bench3.Bench32.bench321)
            }
        }
    }

    private fun cgResult(bp1: Array<Pair<Benchmark, Reachabilities>>, bp2: Array<Pair<Benchmark, Reachabilities>>): CGResult =
            CGResult(mapOf(
                    *bp1,
                    BenchNonParameterized.bench2Cg,
                    OtherBench.bench3Cg,
                    *bp2,
                    NestedBenchmark.Bench1.bench11Cg,
                    NestedBenchmark.Bench1.bench12Cg,
                    NestedBenchmark.bench2Cg,
                    NestedBenchmark.Bench3.bench31Cg,
                    NestedBenchmark.Bench3.Bench32.bench321Cg
            ))

    val cgResult = cgResult(BenchParameterized.bench1Cgs, BenchParameterized2.bench4Cgs)
    val cgResultNonParam = cgResult(BenchParameterized.bench1NonParamCgs, BenchParameterized2.bench4NonParamCgs)

    val cgResultv2 = cgResult(BenchParameterized.bench1Cgs, BenchParameterized2v2.bench4Cgs)
    val cgResultv2NonParam = cgResult(BenchParameterized.bench1NonParamCgs, BenchParameterized2v2.bench4NonParamCgs)
}
