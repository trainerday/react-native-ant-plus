package com.reactnativeantplus

enum class AntPlusBikePowerRequest(val event: String) {
    CommandBurst("CommandBurst"),
    CrankParameters("CrankParameters"),
    CustomCalibrationParameters("CustomCalibrationParameters"),
    ManualCalibration("ManualCalibration"),
    SetAutoZero("SetAutoZero"),
    SetCrankParameters("SetCrankParameters"),
    SetCtfSlope("SetCtfSlope"),
    SetCustomCalibrationParameters("SetCustomCalibrationParameters"),
}
