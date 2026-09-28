package com.example.gridboxes

import com.example.gridboxes.logic.GameLogic
import com.example.gridboxes.model.BorderKey
import com.example.gridboxes.model.BorderOrientation
import com.example.gridboxes.model.CellPosition
import com.example.gridboxes.model.GridBoxesState
import com.example.gridboxes.model.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameLogicTest {

    @Test
    fun testValidHorizontalBorder() {
        // Valid horizontal borders: row 0..5, col 0..4
        assertTrue(GameLogic.isValidBorder(BorderKey(0, 0, BorderOrientation.HORIZONTAL)))
        assertTrue(GameLogic.isValidBorder(BorderKey(5, 4, BorderOrientation.HORIZONTAL)))
        assertTrue(GameLogic.isValidBorder(BorderKey(2, 3, BorderOrientation.HORIZONTAL)))

        // Invalid horizontal borders
        assertFalse(GameLogic.isValidBorder(BorderKey(-1, 0, BorderOrientation.HORIZONTAL)))
        assertFalse(GameLogic.isValidBorder(BorderKey(6, 0, BorderOrientation.HORIZONTAL)))
        assertFalse(GameLogic.isValidBorder(BorderKey(0, 5, BorderOrientation.HORIZONTAL)))
        assertFalse(GameLogic.isValidBorder(BorderKey(0, -1, BorderOrientation.HORIZONTAL)))
    }

    @Test
    fun testValidVerticalBorder() {
        // Valid vertical borders: row 0..4, col 0..5
        assertTrue(GameLogic.isValidBorder(BorderKey(0, 0, BorderOrientation.VERTICAL)))
        assertTrue(GameLogic.isValidBorder(BorderKey(4, 5, BorderOrientation.VERTICAL)))
        assertTrue(GameLogic.isValidBorder(BorderKey(3, 2, BorderOrientation.VERTICAL)))

        // Invalid vertical borders
        assertFalse(GameLogic.isValidBorder(BorderKey(5, 0, BorderOrientation.VERTICAL)))
        assertFalse(GameLogic.isValidBorder(BorderKey(-1, 0, BorderOrientation.VERTICAL)))
        assertFalse(GameLogic.isValidBorder(BorderKey(0, 6, BorderOrientation.VERTICAL)))
        assertFalse(GameLogic.isValidBorder(BorderKey(0, -1, BorderOrientation.VERTICAL)))
    }

    @Test
    fun testDuplicateBorderRejection() {
        val state = GridBoxesState()
        val border = BorderKey(1, 1, BorderOrientation.HORIZONTAL)

        val firstMove = GameLogic.applyMove(state, border)
        assertTrue(firstMove is GameLogic.MoveExecutionResult.Success)

        val newState = (firstMove as GameLogic.MoveExecutionResult.Success).newState
        val duplicateMove = GameLogic.applyMove(newState, border)
        assertTrue(duplicateMove is GameLogic.MoveExecutionResult.Rejected)
    }

    @Test
    fun testTurnSwitchWhenNoCellCompleted() {
        val state = GridBoxesState(currentPlayer = Player.PLAYER_ONE)
        val border = BorderKey(0, 0, BorderOrientation.HORIZONTAL)

        val result = GameLogic.applyMove(state, border)
        assertTrue(result is GameLogic.MoveExecutionResult.Success)
        val success = result as GameLogic.MoveExecutionResult.Success

        assertEquals(Player.PLAYER_TWO, success.newState.currentPlayer)
        assertFalse(success.grantedExtraTurn)
        assertEquals(0, success.newState.player1Score)
        assertEquals(0, success.newState.player2Score)
        assertEquals(1, success.newState.moveCount)
    }

    @Test
    fun testSingleCellCompletionAndExtraTurnAndScoreUpdate() {
        // Target cell: (0, 0)
        // Borders:
        // Top: HORIZONTAL(0, 0)
        // Bottom: HORIZONTAL(1, 0)
        // Left: VERTICAL(0, 0)
        // Right: VERTICAL(0, 1)

        var state = GridBoxesState(currentPlayer = Player.PLAYER_ONE)

        // P1 plays top
        val m1 = GameLogic.applyMove(state, BorderKey(0, 0, BorderOrientation.HORIZONTAL)) as GameLogic.MoveExecutionResult.Success
        state = m1.newState
        assertEquals(Player.PLAYER_TWO, state.currentPlayer)

        // P2 plays left
        val m2 = GameLogic.applyMove(state, BorderKey(0, 0, BorderOrientation.VERTICAL)) as GameLogic.MoveExecutionResult.Success
        state = m2.newState
        assertEquals(Player.PLAYER_ONE, state.currentPlayer)

        // P1 plays bottom
        val m3 = GameLogic.applyMove(state, BorderKey(1, 0, BorderOrientation.HORIZONTAL)) as GameLogic.MoveExecutionResult.Success
        state = m3.newState
        assertEquals(Player.PLAYER_TWO, state.currentPlayer)

        // P2 plays right (4th border for cell (0, 0))
        val m4 = GameLogic.applyMove(state, BorderKey(0, 1, BorderOrientation.VERTICAL)) as GameLogic.MoveExecutionResult.Success
        state = m4.newState

        // P2 should have scored +1, claimed (0,0), and maintained turn (extra turn)
        assertEquals(1, state.player2Score)
        assertEquals(0, state.player1Score)
        assertEquals(Player.PLAYER_TWO, state.currentPlayer) // Extra turn!
        assertTrue(m4.grantedExtraTurn)
        assertEquals(Player.PLAYER_TWO, state.claimedCells[CellPosition(0, 0)])
        assertEquals(1, state.claimedCells.size)
        assertEquals(0, state.doubleBoxMoves)
    }

    @Test
    fun testTwoCellCompletionFromOneBorder() {
        // Cells (1, 1) and (1, 2) share the vertical border at (1, 2).
        // Borders of cell (1, 1):
        // Top: HORIZONTAL(1, 1)
        // Bottom: HORIZONTAL(2, 1)
        // Left: VERTICAL(1, 1)
        // Right: VERTICAL(1, 2) [shared]

        // Borders of cell (1, 2):
        // Top: HORIZONTAL(1, 2)
        // Bottom: HORIZONTAL(2, 2)
        // Left: VERTICAL(1, 2) [shared]
        // Right: VERTICAL(1, 3)

        val bordersExceptShared = setOf(
            BorderKey(1, 1, BorderOrientation.HORIZONTAL),
            BorderKey(2, 1, BorderOrientation.HORIZONTAL),
            BorderKey(1, 1, BorderOrientation.VERTICAL),
            BorderKey(1, 2, BorderOrientation.HORIZONTAL),
            BorderKey(2, 2, BorderOrientation.HORIZONTAL),
            BorderKey(1, 3, BorderOrientation.VERTICAL)
        )

        var state = GridBoxesState(
            drawnBorders = bordersExceptShared,
            currentPlayer = Player.PLAYER_ONE,
            player1Score = 0,
            player2Score = 0
        )

        // Now Player 1 places the shared border VERTICAL(1, 2)
        val sharedBorder = BorderKey(1, 2, BorderOrientation.VERTICAL)
        val result = GameLogic.applyMove(state, sharedBorder)
        assertTrue(result is GameLogic.MoveExecutionResult.Success)
        val success = result as GameLogic.MoveExecutionResult.Success

        assertEquals(2, success.completedCells.size)
        assertTrue(success.completedCells.contains(CellPosition(1, 1)))
        assertTrue(success.completedCells.contains(CellPosition(1, 2)))

        // Score should increase by +2
        assertEquals(2, success.newState.player1Score)
        assertEquals(0, success.newState.player2Score)
        // Double box moves counter should increment
        assertEquals(1, success.newState.doubleBoxMoves)
        // Still Player 1's turn
        assertEquals(Player.PLAYER_ONE, success.newState.currentPlayer)
        assertTrue(success.grantedExtraTurn)
        assertEquals(Player.PLAYER_ONE, success.newState.claimedCells[CellPosition(1, 1)])
        assertEquals(Player.PLAYER_ONE, success.newState.claimedCells[CellPosition(1, 2)])
    }

    @Test
    fun testCellClaimedOnlyOnce() {
        // Cell already claimed cannot be claimed again
        val initialBorders = GameLogic.getCellBorders(CellPosition(0, 0))
        val state = GridBoxesState(
            drawnBorders = initialBorders,
            claimedCells = mapOf(CellPosition(0, 0) to Player.PLAYER_ONE),
            player1Score = 1
        )

        // Adjacent border to (0, 0) is VERTICAL(0, 1), which touches (0, 0) and (0, 1).
        // Since (0, 0) is already claimed, placing another border touching it must not re-claim (0, 0).
        val move = GameLogic.applyMove(state, BorderKey(0, 2, BorderOrientation.VERTICAL))
        assertTrue(move is GameLogic.MoveExecutionResult.Success)
        val success = move as GameLogic.MoveExecutionResult.Success
        assertFalse(success.completedCells.contains(CellPosition(0, 0)))
    }

    @Test
    fun testTotalBoardBordersCount() {
        val allBorders = GameLogic.getAllBorders()
        assertEquals(60, allBorders.size)

        val allCells = GameLogic.getAllCells()
        assertEquals(25, allCells.size)
    }

    @Test
    fun testBoardCompletionAndWinnerDetermination() {
        // Simulate a game where all 25 cells are claimed
        val allCells = GameLogic.getAllCells()
        val allBorders = GameLogic.getAllBorders()

        // P1 claims 15 cells, P2 claims 10 cells
        val claimed = mutableMapOf<CellPosition, Player>()
        allCells.take(15).forEach { claimed[it] = Player.PLAYER_ONE }
        allCells.drop(15).forEach { claimed[it] = Player.PLAYER_TWO }

        val almostCompleteState = GridBoxesState(
            drawnBorders = allBorders.dropLast(1).toSet(),
            claimedCells = claimed,
            player1Score = 15,
            player2Score = 10,
            currentPlayer = Player.PLAYER_ONE
        )

        val lastBorder = allBorders.last()
        val result = GameLogic.applyMove(almostCompleteState, lastBorder)
        assertTrue(result is GameLogic.MoveExecutionResult.Success)
        val success = result as GameLogic.MoveExecutionResult.Success

        assertTrue(success.isGameOver)
        assertTrue(success.newState.isGameOver)
        assertEquals(Player.PLAYER_ONE, success.newState.winner)
        assertFalse(success.newState.isDraw)
    }

    @Test
    fun testDrawResult() {
        val allBorders = GameLogic.getAllBorders()
        val allCells = GameLogic.getAllCells()
        // 24 cells already claimed: 12 for P1, 12 for P2. Cell (4, 4) is uncompleted until lastBorder
        val claimed = mutableMapOf<CellPosition, Player>()
        allCells.filter { it != CellPosition(4, 4) }.forEachIndexed { index, cell ->
            if (index < 12) claimed[cell] = Player.PLAYER_ONE else claimed[cell] = Player.PLAYER_TWO
        }

        val almostCompleteState = GridBoxesState(
            drawnBorders = allBorders.dropLast(1).toSet(),
            claimedCells = claimed,
            player1Score = 12,
            player2Score = 13,
            currentPlayer = Player.PLAYER_ONE
        )

        val lastBorder = allBorders.last()
        val result = GameLogic.applyMove(almostCompleteState, lastBorder)
        assertTrue(result is GameLogic.MoveExecutionResult.Success)
        val success = result as GameLogic.MoveExecutionResult.Success

        assertTrue(success.isGameOver)
        assertTrue(success.newState.isDraw)
        assertNull(success.newState.winner)
    }

    @Test
    fun testUndoStatePreservation() {
        val undoStack = ArrayDeque<GridBoxesState>()
        var state = GridBoxesState(currentPlayer = Player.PLAYER_ONE)

        // Save snapshot before move 1
        undoStack.addLast(state)
        val m1 = GameLogic.applyMove(state, BorderKey(0, 0, BorderOrientation.HORIZONTAL)) as GameLogic.MoveExecutionResult.Success
        state = m1.newState
        assertEquals(Player.PLAYER_TWO, state.currentPlayer)
        assertEquals(1, state.moveCount)

        // Perform undo
        val previousState = undoStack.removeLast()
        state = previousState
        assertEquals(Player.PLAYER_ONE, state.currentPlayer)
        assertEquals(0, state.moveCount)
        assertTrue(state.drawnBorders.isEmpty())
    }
}
