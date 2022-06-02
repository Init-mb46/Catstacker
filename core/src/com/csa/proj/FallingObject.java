package com.csa.proj;

public interface FallingObject {
    float ACCELERATION = GameScreen.ACCELERATION;
    void render(float delta);
    void move(float delta);
}
