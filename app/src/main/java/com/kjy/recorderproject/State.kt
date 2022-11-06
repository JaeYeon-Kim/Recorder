package com.kjy.recorderproject

// 녹음 상태등을 정의해줌
/* enum class = 열거형 class
 사용하는 이유 ?
 1. 코드가 단순해지며 가독성이 UP
 2. 인스턴스 생성과 상속을 방지, 상수값의 타입 안정성을 보장.
 */
enum class State {
    BEFORE_RECORDING,
    ON_RECORDING,
    AFTER_RECORDING,
    ON_PLAYING
}