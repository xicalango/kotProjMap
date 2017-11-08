package xx.projmap.app

import xx.projmap.app.State.CAMERA_CALIBRATION
import xx.projmap.app.State.KEY_CALIBRATION
import xx.projmap.scene2.Behavior
import xx.projmap.scene2.Entity

enum class State {
    CAMERA_CALIBRATION,
    KEY_CALIBRATION
}

class StateManager : Entity("stateManager") {

    init {
        addComponent(StateManagerBehavior())
        addChild(CameraCalibrationState())
        addChild(KeyCalibration())
    }
}

class StateManagerBehavior : Behavior() {

    private lateinit var cameraCalibrationState: CameraCalibrationBehavior
    private lateinit var keyCalibrationState: KeyCalibrationBehavior

    var nextState: State? = null
    var currentState: State = CAMERA_CALIBRATION
        private set

    override fun setup() {
        cameraCalibrationState = entity.findChild<CameraCalibrationState>()?.findComponent()!!
        keyCalibrationState = entity.findChild<KeyCalibration>()?.findComponent()!!
    }

    override fun update(dt: Double) {
        val nextState = this.nextState
        if (nextState != null) {
            currentState = nextState
            this.nextState = null
            activateState(currentState)
        }
    }

    private fun activateState(state: State) = when (state) {
        CAMERA_CALIBRATION -> {
            cameraCalibrationState.enabled = true
            keyCalibrationState.enabled = false
        }
        KEY_CALIBRATION -> {
            cameraCalibrationState.enabled = false
            keyCalibrationState.enabled = true
        }
    }

}