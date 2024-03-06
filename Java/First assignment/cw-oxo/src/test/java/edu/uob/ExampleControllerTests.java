package edu.uob;

import edu.uob.OXOMoveException.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ExampleControllerTests {
  private OXOModel model;
  private OXOController controller;

  // Make a new "standard" (3x3) board before running each test case (i.e. this method runs before every `@Test` method)
  // In order to test boards of different sizes, winning thresholds or number of players, create a separate test file (without this method in it !)
  @BeforeEach
  void setup() {
    model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
  }

  // This next method is a utility function that can be used by any of the test methods to _safely_ send a command to the controller
  void sendCommandToController(String command) {
      // Try to send a command to the server - call will time out if it takes too long (in case the server enters an infinite loop)
      // Note: this is ugly code and includes syntax that you haven't encountered yet
      String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
      assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> controller.handleIncomingCommand(command), timeoutComment);
  }

  // Test simple move taking and cell claiming functionality
  @Test
  void testBasicMoveTaking() throws OXOMoveException {
    // Find out which player is going to make the first move
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a move
    sendCommandToController("a1");
    // Check that A1 (cell [0,0] on the board) is now "owned" by the first player
    String failedTestComment = "Cell a1 wasn't claimed by the first player";
    assertEquals(firstMovingPlayer, controller.gameModel.getCellOwner(0, 0), failedTestComment);
  }

  // Test out basic win detection
  @Test
  //TODO Test the row win
  void testBasicWin() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player

    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }

  @Test
  //TODO Test the col win
  void testBasicWinCol() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a bunch of moves for the two players
    sendCommandToController("a2"); // First player
    sendCommandToController("a3"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("b3"); // Second player
    sendCommandToController("c2"); // First player

    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }
  @Test
    //TODO Test the TopLeft win
  void testBasicWinTopLeft() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("a3"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("b3"); // Second player
    sendCommandToController("c1"); // First player

    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }

  @Test
    //TODO Test the TopRight win
  void testBasicWinTopRight() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("a3"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("b2"); // First player
    sendCommandToController("b3"); // Second player
    sendCommandToController("c3"); // First player

    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }
  // Example of how to test for the throwing of exceptions
  @Test
  //TODO Test Invalid Command Number
  void testInvalidIdentifierException() throws OXOMoveException {
    // Check that the controller throws a suitable exception when it gets an invalid command
    String failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for command `abc123`";
    // The next lines is a bit ugly, but it is the easiest way to test exceptions (soz)
    assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController("abc123"), failedTestComment);
  }
  @Test
    //TODO Test Invalid Row Character
  void testInvalidRowCharacter() throws OXOMoveException {
    // Check that the controller throws a suitable exception when it gets an invalid command
    String failedTestComment = "Controller failed to throw an InvalidIdentifierCharacterException for command `11`";
    // The next lines is a bit ugly, but it is the easiest way to test exceptions (soz)
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("11"), failedTestComment);
  }
  @Test
    //TODO Test Invalid Col Character
  void testInvalidColCharacter() throws OXOMoveException {
    // Check that the controller throws a suitable exception when it gets an invalid command
    String failedTestComment = "Controller failed to throw an InvalidIdentifierCharacterException for command `aa`";
    // The next lines is a bit ugly, but it is the easiest way to test exceptions (soz)
    assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("11"), failedTestComment);
  }
  @Test
    //TODO Test Outside Row Range
  void testOutsideRowRange() throws OXOMoveException {
    // Check that the controller throws a suitable exception when it gets an invalid command
    String failedTestComment = "Controller failed to throw an OutsideCellRangeException for command `z1`";
    // The next lines is a bit ugly, but it is the easiest way to test exceptions (soz)
    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("z1"), failedTestComment);
  }
  @Test
    //TODO Test Outside Col Range
  void testOutsideColRange() throws OXOMoveException {
    // Check that the controller throws a suitable exception when it gets an invalid command
    String failedTestComment = "Controller failed to throw an OutsideCellRangeException for command `a9`";
    // The next lines is a bit ugly, but it is the easiest way to test exceptions (soz)
    assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("a9"), failedTestComment);
  }
  @Test
    //TODO Test Occupied
  void testOccupied() throws OXOMoveException {
    // Check that the controller throws a suitable exception when it gets an invalid command
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    String failedTestComment = "Controller failed to throw an OutsideCellRangeException for command `a1`";
    // The next lines is a bit ugly, but it is the easiest way to test exceptions (soz)
    assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("a1"), failedTestComment);
  }
  @Test
    //TODO Test add Row
  void AddRow() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    // Make a bunch of moves for the two players
    controller.addRow();
    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Number of row was expected to be " + "4" + " but wasn't"+model.getWinThreshold();
    assertEquals(4, model.getNumberOfRows(), failedTestComment);
  }
  @Test
    //TODO Test delete Row
  void DeleteRow() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    // Make a bunch of moves for the two players
    controller.addRow();
    controller.addRow();
    controller.removeRow();
    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Number of row was expected to be " + "4" + " but wasn't"+model.getWinThreshold();
    assertEquals(4, model.getNumberOfRows(), failedTestComment);
  }
  @Test
  //TODO the RemoveRow() can't work, when remove a row, there will be a winner or a drawn
  void NotDeleteRow() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    // Make a bunch of moves for the two players
    controller.addRow();
    sendCommandToController("a1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("a3"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("b1"); // First player
    sendCommandToController("b3"); // Second player
    sendCommandToController("c2"); // First player
    sendCommandToController("c1"); // Second player
    sendCommandToController("c3"); // First player
    controller.removeRow();
    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Number of row was expected to be " + "4" + " but wasn't"+model.getWinThreshold();
    assertEquals(4, model.getNumberOfRows(), failedTestComment);
  }
  @Test
    //TODO Test add Threshold
  void AddThreshold() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    // Make a bunch of moves for the two players
    controller.increaseWinThreshold();
    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Threshold was expected to be " + "4" + " but wasn't"+model.getWinThreshold();
    assertEquals(4, model.getWinThreshold(), failedTestComment);
  }
  @Test
  //TODO Test Decrease Threshold
  void DecreaseThreshold() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    // Make a bunch of moves for the two players
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    controller.decreaseWinThreshold();
    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Threshold was expected to be " + "5" + " but wasn't"+model.getWinThreshold();
    assertEquals(5, model.getWinThreshold(), failedTestComment);
  }
  @Test
    //TODO Test When the game begin the DecreaseThreshold is not working
    //     but the IncreaseThreshold is working
  void NotDecreaseThreshold() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    controller.increaseWinThreshold();
    controller.decreaseWinThreshold();
    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Threshold was expected to be " + "6" + " but wasn't"+model.getWinThreshold();
    assertEquals(6, model.getWinThreshold(), failedTestComment);
  }
  @Test
  //TODO Test when win, you can't input anything
  void WhenWinNotChange() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player
    sendCommandToController("b3"); // Second player

    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "b3 is expected null " + " but it is" + model.getCellOwner(1,2);
    assertNull(model.getCellOwner(1, 2), failedTestComment);
  }

  @Test
    //TODO Test reset
  void Reset() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("a2"); // Second player
    controller.reset();
    boolean jud = true;
    for(int i = 0; i < model.getNumberOfRows(); i++ ){
      for (int j = 0; j < model.getNumberOfColumns(); j++ ){
        if(model.getCellOwner(i,j) != null){
          jud = false;
        }
      }
    }

    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "board was expected to be empty " + " but it wasn't";
    assertTrue(jud, failedTestComment);
  }
  @Test
    //TODO Test AddPlayer
  void AddPlayer() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    // Make a bunch of moves for the two players
    boolean a;
    model.addPlayer(new OXOPlayer('A'));
    sendCommandToController("a1"); // First player
    sendCommandToController("a2"); // Second player
    sendCommandToController("a3"); // Third player
    int numPlayer = model.getNumberOfPlayers();
    a = (model.getPlayerByNumber(2) == model.getCellOwner(0,2));
    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment1 = "Player was expected to be 3 " + " but it wasn't" + model.getNumberOfPlayers();
    String failedTestComment2 = "Cell was expected to be 'A' " + " but it wasn't" + model.getCellOwner(0,2);
    assertEquals(numPlayer, 3, failedTestComment1);
    assertTrue(a, failedTestComment2);
  }
}
