package org.victor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ElevatorTest {

    private PublicElevator publicElevator;
    private FreightElevator freightElevator;

    @BeforeEach
    void setup() {
        publicElevator = new PublicElevator(50);
        freightElevator = new FreightElevator(50);
    }

    // ───────────────────────────────
    // PUBLIC ELEVATOR TESTS
    // ───────────────────────────────

    @Test
    @DisplayName("Public elevator: Access denied to restricted floor without keycard")
    void testPublicElevatorAccessDenied() {
        publicElevator.goToFloor(50, false);
        assertEquals(1, publicElevator.getCurrentFloor(), "Should remain on floor 1 when access is denied");
    }

    @Test
    @DisplayName("Public elevator: Access granted with keycard")
    void testPublicElevatorWithKeycard() {
        publicElevator.goToFloor(50, true);
        assertEquals(50, publicElevator.getCurrentFloor(), "Should reach 50th floor with keycard");
    }

    @Test
    @DisplayName("Public elevator: Should shutdown when weight limit exceeded")
    void testPublicElevatorWeightLimitShutdown() {
        publicElevator.addWeight(1200);
        assertFalse(publicElevator.isOperational(), "Elevator should shut down after exceeding limit");
    }

    @Test
    @DisplayName("Public elevator: Should not move after shutdown")
    void testPublicElevatorCannotMoveAfterShutdown() {
        publicElevator.addWeight(1500); // exceeds weight limit
        publicElevator.goToFloor(10, true);
        assertEquals(1, publicElevator.getCurrentFloor(), "Should stay on same floor after shutdown");
    }

    @Test
    @DisplayName("Public elevator: Should reduce weight correctly when passengers exit")
    void testPublicElevatorRemoveWeight() {
        publicElevator.addWeight(500);
        publicElevator.removeWeight(200);
        assertTrue(publicElevator.isOperational());
        assertEquals(300, publicElevator.getCurrentWeight(), "Weight should decrease correctly");
    }

    @Test
    @DisplayName("Public elevator: Should not accept negative floor requests")
    void testPublicElevatorInvalidFloorBelowZero() {
        publicElevator.goToFloor(-5, true);
        assertEquals(1, publicElevator.getCurrentFloor(), "Invalid floors should not change position");
    }

    @Test
    @DisplayName("Public elevator: Should not move above total floors")
    void testPublicElevatorInvalidFloorAboveLimit() {
        publicElevator.goToFloor(100, true);
        assertEquals(1, publicElevator.getCurrentFloor(), "Invalid floor > total should be ignored");
    }

    // ───────────────────────────────
    // FREIGHT ELEVATOR TESTS
    // ───────────────────────────────

    @Test
    @DisplayName("Freight elevator: Can move to basement without keycard")
    void testFreightElevatorAccessBasement() {
        freightElevator.goToFloor(0);
        assertEquals(0, freightElevator.getCurrentFloor(), "Freight elevator should access basement");
    }

    @Test
    @DisplayName("Freight elevator: Should shut down when overweight")
    void testFreightElevatorOverload() {
        freightElevator.addWeight(2500);
        assertTrue(freightElevator.isOperational());
        freightElevator.addWeight(600); // exceeds 3000 kg limit
        assertFalse(freightElevator.isOperational(), "Freight elevator should shut down after overload");
    }

    @Test
    @DisplayName("Freight elevator: Should not move when shut down")
    void testFreightElevatorCannotMoveAfterShutdown() {
        freightElevator.addWeight(4000);
        freightElevator.goToFloor(10);
        assertEquals(1, freightElevator.getCurrentFloor(), "Should not move after shutdown");
    }

    @Test
    @DisplayName("Freight elevator: Should decrease weight correctly")
    void testFreightElevatorWeightDecrease() {
        freightElevator.addWeight(1000);
        freightElevator.removeWeight(400);
        assertEquals(600, freightElevator.getCurrentWeight(), "Weight should be reduced correctly");
    }

    // ───────────────────────────────
    // GENERAL BEHAVIOR TESTS
    // ───────────────────────────────

    @Test
    @DisplayName("Both elevators: Should start operational and at floor 1")
    void testInitialState() {
        assertTrue(publicElevator.isOperational());
        assertTrue(freightElevator.isOperational());
        assertEquals(1, publicElevator.getCurrentFloor());
        assertEquals(1, freightElevator.getCurrentFloor());
    }

    @Test
    @DisplayName("Both elevators: Should not allow adding negative weight")
    void testInvalidNegativeWeight() {
        publicElevator.addWeight(-200);
        freightElevator.addWeight(-100);
        assertEquals(0, publicElevator.getCurrentWeight(), "Negative weight should be ignored");
        assertEquals(0, freightElevator.getCurrentWeight(), "Negative weight should be ignored");
    }

    @Test
    @DisplayName("Both elevators: Should ignore removing more weight than current")
    void testRemoveExcessiveWeight() {
        publicElevator.addWeight(200);
        publicElevator.removeWeight(500);
        assertEquals(0, publicElevator.getCurrentWeight(), "Weight cannot go below zero");
    }
}
