package org.victor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.victor.exception.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Elevator system covering Public and Freight elevators
 */
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
        assertThrows(AccessDeniedException.class, 
                () -> publicElevator.goToFloor(50, false));
        assertEquals(1, publicElevator.getCurrentFloor(), "Should remain on floor 1 when access is denied");
    }

    @Test
    @DisplayName("Public elevator: Access granted with keycard")
    void testPublicElevatorWithKeycard() {
        assertDoesNotThrow(() -> publicElevator.goToFloor(50, true));
        assertEquals(50, publicElevator.getCurrentFloor(), "Should reach 50th floor with keycard");
    }

    @Test
    @DisplayName("Public elevator: Should shutdown when weight limit exceeded")
    void testPublicElevatorWeightLimitShutdown() {
        assertThrows(ElevatorOverloadException.class, 
                () -> publicElevator.addWeight(1200));
        assertFalse(publicElevator.isOperational(), "Elevator should shut down after exceeding limit");
    }

    @Test
    @DisplayName("Public elevator: Should not move after shutdown")
    void testPublicElevatorCannotMoveAfterShutdown() {
        assertThrows(ElevatorOverloadException.class, 
                () -> publicElevator.addWeight(1500));
        assertThrows(ElevatorNotOperationalException.class, 
                () -> publicElevator.goToFloor(10, true));
        assertEquals(1, publicElevator.getCurrentFloor(), "Should stay on same floor after shutdown");
    }

    @Test
    @DisplayName("Public elevator: Should reduce weight correctly when passengers exit")
    void testPublicElevatorRemoveWeight() {
        assertDoesNotThrow(() -> publicElevator.addWeight(500));
        publicElevator.removeWeight(200);
        assertTrue(publicElevator.isOperational());
        assertEquals(300, publicElevator.getCurrentWeight(), "Weight should decrease correctly");
    }

    @Test
    @DisplayName("Public elevator: Should not accept negative floor requests")
    void testPublicElevatorInvalidFloorBelowZero() {
        assertThrows(InvalidFloorException.class, 
                () -> publicElevator.goToFloor(-5));
        assertEquals(1, publicElevator.getCurrentFloor(), "Invalid floors should not change position");
    }

    @Test
    @DisplayName("Public elevator: Should not move above total floors")
    void testPublicElevatorInvalidFloorAboveLimit() {
        assertThrows(InvalidFloorException.class, 
                () -> publicElevator.goToFloor(100));
        assertEquals(1, publicElevator.getCurrentFloor(), "Invalid floor > total should be ignored");
    }

    // ───────────────────────────────
    // FREIGHT ELEVATOR TESTS
    // ───────────────────────────────

    @Test
    @DisplayName("Freight elevator: Can move to basement without keycard")
    void testFreightElevatorAccessBasement() {
        assertDoesNotThrow(() -> freightElevator.goToFloor(0));
        assertEquals(0, freightElevator.getCurrentFloor(), "Freight elevator should access basement");
    }

    @Test
    @DisplayName("Freight elevator: Should shut down when overweight")
    void testFreightElevatorOverload() {
        assertDoesNotThrow(() -> freightElevator.addWeight(2500));
        assertTrue(freightElevator.isOperational());
        assertThrows(ElevatorOverloadException.class, 
                () -> freightElevator.addWeight(600)); // exceeds 3000 kg limit
        assertFalse(freightElevator.isOperational(), "Freight elevator should shut down after overload");
    }

    @Test
    @DisplayName("Freight elevator: Should not move when shut down")
    void testFreightElevatorCannotMoveAfterShutdown() {
        assertThrows(ElevatorOverloadException.class, 
                () -> freightElevator.addWeight(4000));
        assertThrows(ElevatorNotOperationalException.class, 
                () -> freightElevator.goToFloor(10));
        assertEquals(1, freightElevator.getCurrentFloor(), "Should not move after shutdown");
    }

    @Test
    @DisplayName("Freight elevator: Should decrease weight correctly")
    void testFreightElevatorWeightDecrease() {
        assertDoesNotThrow(() -> freightElevator.addWeight(1000));
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
        assertDoesNotThrow(() -> publicElevator.addWeight(200));
        publicElevator.removeWeight(500);
        assertEquals(0, publicElevator.getCurrentWeight(), "Weight cannot go below zero");
    }

    @Test
    @DisplayName("Both elevators: Should validate floor range")
    void testFloorValidation() {
        // Test valid floors
        for (int floor = 0; floor <= 50; floor += 10) {
            int f = floor;
            assertDoesNotThrow(() -> publicElevator.goToFloor(f));
        }

        // Test invalid floors
        assertThrows(InvalidFloorException.class, () -> publicElevator.goToFloor(-1));
        assertThrows(InvalidFloorException.class, () -> publicElevator.goToFloor(51));
    }

    @Test
    @DisplayName("Both elevators: Should have unique IDs")
    void testUniqueElevatorIDs() {
        PublicElevator elevator1 = new PublicElevator(50);
        PublicElevator elevator2 = new PublicElevator(50);
        
        assertNotEquals(elevator1.getElevatorId(), elevator2.getElevatorId(), 
                "Each elevator should have a unique ID");
    }
}
