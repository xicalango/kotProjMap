package xx.projmap.app

import xx.projmap.app.State.*
import xx.projmap.events.KeyEvent
import xx.projmap.scene2.Behavior
import xx.projmap.scene2.Entity

enum class State {
    CAMERA_CALIBRATION,
    KEY_CALIBRATION,
    COLOR_CYCLER,
    KEY_SHOOT
}

class StateManager : Entity("stateManager") {

    init {
        addComponent(StateManagerBehavior())
        addChild(CameraCalibrationState())
        addChild(KeyCalibration())
        addChild(ColorCyclerEntity())
        addChild(KeyShootEntity())
    }
}

class StateManagerBehavior : Behavior() {

    private lateinit var cameraCalibrationState: CameraCalibrationBehavior
    private lateinit var keyCalibrationState: KeyCalibrationBehavior
    private lateinit var colorCyclerState: ColorCyclerBehavior
    private lateinit var keyShootBehavior: KeyShootBehavior

    var nextState: State? = null
    var currentState: State = CAMERA_CALIBRATION
        private set

    override fun initialize() {
        cameraCalibrationState = entity.findChild<CameraCalibrationState>()?.findComponent()!!
        keyCalibrationState = entity.findChild<KeyCalibration>()?.findComponent()!!
        colorCyclerState = entity.findChild<ColorCyclerEntity>()?.findComponent()!!
        keyShootBehavior = entity.findChild<KeyShootEntity>()?.findComponent()!!
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
            keyShootBehavior.enabled = false
        }
        KEY_CALIBRATION -> {
            cameraCalibrationState.enabled = false
            keyCalibrationState.enabled = true
            colorCyclerState.enabled = false
            keyShootBehavior.enabled = false
        }
        COLOR_CYCLER -> {
            cameraCalibrationState.enabled = false
            keyCalibrationState.enabled = false
            colorCyclerState.enabled = true
            keyShootBehavior.enabled = false
        }
        KEY_SHOOT -> {
            cameraCalibrationState.enabled = false
            keyCalibrationState.enabled = false
            colorCyclerState.enabled = false
            keyShootBehavior.enabled = true
        }
    }

    override fun onKeyReleased(event: KeyEvent) {
        when (event.keyCode) {
            112 -> nextState = CAMERA_CALIBRATION
            113 -> nextState = KEY_CALIBRATION
            114 -> nextState = COLOR_CYCLER
            115 -> nextState = KEY_SHOOT
        }
    }

}