package xx.projmap.app

import xx.projmap.app.State.*
import xx.projmap.events.KeyEvent
import xx.projmap.scene2.Behavior
import xx.projmap.scene2.Entity

enum class State {
    CAMERA_CALIBRATION,
    KEY_CALIBRATION,
    COLOR_CYCLER
}

class StateManager : Entity("stateManager") {

    init {
        addComponent(StateManagerBehavior())
        addChild(CameraCalibrationState())
        addChild(KeyCalibration())
        addChild(ColorCyclerEntity())
    }
}

class StateManagerBehavior : Behavior() {

    private lateinit var cameraCalibrationState: CameraCalibrationBehavior
    private lateinit var keyCalibrationState: KeyCalibrationBehavior
    private lateinit var colorCyclerState: ColorCyclerBehavior

    var nextState: State? = null
    var currentState: State = CAMERA_CALIBRATION
        private set

    override fun initialize() {
        cameraCalibrationState = entity.findChild<CameraCalibrationState>()?.findComponent()!!
        keyCalibrationState = entity.findChild<KeyCalibration>()?.findComponent()!!
        colorCyclerState = entity.findChild<ColorCyclerEntity>()?.findComponent()!!
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
            colorCyclerState.enabled = false
        }
        KEY_CALIBRATION -> {
            cameraCalibrationState.enabled = false
            keyCalibrationState.enabled = true
            colorCyclerState.enabled = false
        }
        COLOR_CYCLER -> {
            cameraCalibrationState.enabled = false
            keyCalibrationState.enabled = false
            colorCyclerState.enabled = true
        }
    }

    override fun onKeyReleased(event: KeyEvent) {
        when (event.keyChar) {
            '1' -> nextState = CAMERA_CALIBRATION
            '2' -> nextState = KEY_CALIBRATION
            '3' -> nextState = COLOR_CYCLER
        }
    }

}