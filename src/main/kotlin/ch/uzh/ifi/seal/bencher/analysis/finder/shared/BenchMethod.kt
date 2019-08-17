package ch.uzh.ifi.seal.bencher.analysis.finder.shared

import ch.uzh.ifi.seal.bencher.execution.ExecutionConfiguration
import ch.uzh.ifi.seal.bencher.execution.unsetExecConfig
import org.funktionale.option.Option

class BenchMethod() {
    lateinit var name: String
    lateinit var params: List<String>

    var isBench: Boolean = false
    var isSetup: Boolean = false
    var isTearDown: Boolean = false
    lateinit var execConfig: Option<ExecutionConfiguration>
        private set

    fun group() = groupVisitor?.name

    // sub-visitor
    var groupVisitor: BenchGroupAnnotation? = null
    var forkVisitor: BenchForkAnnotation? = null
    var measurementVisitor: BenchIterationAnnotation? = null
    var warmupVisitor: BenchIterationAnnotation? = null
    var benchModeVisitor: BenchModeAnnotation? = null
    var outputTimeUnitAnnotationVisitor: BenchOutputTimeUnitAnnotation? = null

    constructor(name: String) : this() {
        this.name = name
    }

    fun setExecInfo() {
        execConfig = if (isBench) {
            if (!group().isNullOrEmpty()) {
                // TODO mode is union of all benchmark modes in the same group
                val bm = benchModeVisitor?.mode() ?: listOf()

                val config = if (bm.isEmpty()) {
                    unsetExecConfig
                } else {
                    unsetExecConfig.copy(mode = bm)
                }

                Option.Some(config)
            } else {
                ExecutionConfigurationHelper.toExecutionConfiguration(forkVisitor, measurementVisitor, warmupVisitor, benchModeVisitor, outputTimeUnitAnnotationVisitor)
            }
        } else {
            Option.empty()
        }
    }
}