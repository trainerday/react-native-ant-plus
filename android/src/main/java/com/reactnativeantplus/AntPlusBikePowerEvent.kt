package com.reactnativeantplus

enum class AntPlusBikePowerEvent(val event: String) {
    AutoZeroStatus("AutoZeroStatus"),
    CalculatedCrankCadence("CalculatedCrankCadence"),
    CalculatedPower("CalculatedPower"),
    CalculatedTorque("CalculatedTorque"),
    CalculatedWheelDistance("CalculatedWheelDistance"),
    CalculatedWheelSpeed("CalculatedWheelSpeed"),
    CalibrationMessage("CalibrationMessage"),
    CrankParameters("CrankParameters"),
    InstantaneousCadence("InstantaneousCadence"),
    MeasurementOutputData("MeasurementOutputData"),
    PedalPowerBalance("PedalPowerBalance"),
    PedalSmoothness("PedalSmoothness"),
    RawCrankTorqueData("RawCrankTorqueData"),
    RawCtfData("RawCtfData"),
    RawPowerOnlyData("RawPowerOnlyData"),
    RawWheelTorqueData("RawWheelTorqueData"),
    TorqueEffectiveness("TorqueEffectiveness")
}
