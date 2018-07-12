package ch.uzh.ifi.seal.bencher.jmh_results

data class JMHResult(
        val project: String,
        val commit: String,
        val trial: Int,
        val benchmarks: List<BenchmarkResult>
)

data class BenchmarkResult(
        val name: String,
        val jmhVersion: String,
        val mode: String,
        val forks: Int,
        val threads: Int,
        val warmupIterations: Int,
        val warmuptTime: String,
        val measurementIterations: Int,
        val measurementTime: String,
        val unit: String,
        val values: List<ForkResult>
)

data class ForkResult(
        val fork: Int,
        val iterations: List<IterationResult>
)

data class IterationResult(
        val iteration: Int,
        val value: Float
)
