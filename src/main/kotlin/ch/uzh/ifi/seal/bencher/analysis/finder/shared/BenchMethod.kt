package ch.uzh.ifi.seal.bencher.analysis.finder.shared

import ch.uzh.ifi.seal.bencher.execution.ExecutionConfiguration
import org.funktionale.option.Option

class BenchMethod() {
    lateinit var name: String
    lateinit var params: List<String>

    var isBench: Boolean = false
    var isSetup: Boolean = false
    var isTearDown: Boolean = false
    lateinit var execConfig: Option<ExecutionConfiguration>
        private set

    // sub-visitor
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
            ExecutionConfigurationHelper.toExecutionConfiguration(forkVisitor, measurementVisitor, warmupVisitor, benchModeVisitor, outputTimeUnitAnnotationVisitor)
        } else {
            Option.empty()
        }
    }
}